

package com.luvdumka.kumaonmessenger;

import static com.luvdumka.kumaonmessenger.chatwindo.reciverIImg;
import static com.luvdumka.kumaonmessenger.chatwindo.senderImg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdpter extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModelclass> messagesAdpterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;
    String senderRoom, receiverRoom;

    public messagesAdpter(Context context, ArrayList<msgModelclass> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }
    
    public void setSenderRoom(String senderRoom) {
        this.senderRoom = senderRoom;
    }
    
    public void setReceiverRoom(String receiverRoom) {
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderVierwHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelclass messages = messagesAdpterArrayList.get(position);
        
        // Enable reactions with safety checks
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    showReactionDialog(messages, position);
                } catch (Exception e) {
                    // Handle any reaction dialog errors silently
                }
                return true;
            }
        });
        
        if (holder.getClass()==senderVierwHolder.class){
            senderVierwHolder viewHolder = (senderVierwHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);
            
            // Set message status icon
            setMessageStatusIcon(viewHolder.statusIcon, messages);
            
            // Set reaction if exists (with safety check)
            if (viewHolder.reactionText != null) {
                setReactionDisplay(viewHolder.reactionText, messages);
            }
            
        }else { 
            reciverViewHolder viewHolder = (reciverViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            Picasso.get().load(reciverIImg).into(viewHolder.circleImageView);
            
            // Set reaction if exists (with safety check)
            if (viewHolder.reactionText != null) {
                setReactionDisplay(viewHolder.reactionText, messages);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelclass messages = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    class  senderVierwHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        ImageView statusIcon;
        TextView reactionText;
        public senderVierwHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);
            statusIcon = itemView.findViewById(R.id.message_status_icon);
            // Re-enable reaction with safety check
            try {
                reactionText = itemView.findViewById(R.id.message_reaction);
            } catch (Exception e) {
                // If reaction view not found, set to null
                reactionText = null;
            }
        }
    }
    class reciverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        TextView reactionText;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            msgtxt = itemView.findViewById(R.id.recivertextset);
            // Re-enable reaction with safety check
            try {
                reactionText = itemView.findViewById(R.id.message_reaction);
            } catch (Exception e) {
                // If reaction view not found, set to null
                reactionText = null;
            }
        }
    }

    private void setMessageStatusIcon(ImageView statusIcon, msgModelclass message) {
        String status = message.getMessageStatus();
        
        if (status == null) {
            status = "sent"; // Default fallback
        }
        
        switch (status) {
            case "sent":
                statusIcon.setImageResource(R.drawable.ic_message_sent);
                break;
            case "delivered":
                statusIcon.setImageResource(R.drawable.ic_message_delivered);
                break;
            case "read":
                statusIcon.setImageResource(R.drawable.ic_message_read);
                break;
            default:
                statusIcon.setImageResource(R.drawable.ic_message_sent);
                break;
        }
        statusIcon.setVisibility(View.VISIBLE);
    }

    private void setReactionDisplay(TextView reactionText, msgModelclass message) {
        try {
            if (reactionText != null && message != null) {
                if (message.hasReaction()) {
                    reactionText.setText(message.getReaction());
                    reactionText.setVisibility(View.VISIBLE);
                } else {
                    reactionText.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            // Fail silently if reaction display fails
            if (reactionText != null) {
                reactionText.setVisibility(View.GONE);
            }
        }
    }

    private void showReactionDialog(msgModelclass message, int position) {
        try {
            if (context == null || message == null) {
                return; // Exit if essential components are null
            }
            
            String[] reactions = {"❤️", "👍", "😂", "😮", "😢", "😡", "Remove"};
            
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("React to message");
            builder.setItems(reactions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        
                        if (which == reactions.length - 1) {
                            // Remove reaction
                            addReactionToMessage(message, null, currentUserId, position);
                        } else {
                            // Add reaction
                            addReactionToMessage(message, reactions[which], currentUserId, position);
                        }
                    } catch (Exception e) {
                        // Handle reaction selection error silently
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            // Handle dialog creation error silently
        }
    }

    private void addReactionToMessage(msgModelclass message, String reaction, String userId, int position) {
        try {
            if (message == null || userId == null) {
                return; // Exit if essential components are null
            }
            
            // Update local message
            message.setReaction(reaction);
            message.setReactionBy(userId);
            
            // Update Firebase database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String messageKey = String.valueOf(message.getTimeStamp()); // Use timestamp as key
            
            if (senderRoom != null) {
                DatabaseReference senderRef = database.getReference()
                    .child("chats").child(senderRoom).child("messages");
                updateReactionInFirebase(senderRef, messageKey, reaction, userId);
            }
            
            if (receiverRoom != null) {
                DatabaseReference receiverRef = database.getReference()
                    .child("chats").child(receiverRoom).child("messages");
                updateReactionInFirebase(receiverRef, messageKey, reaction, userId);
            }
            
            // Update UI
            notifyItemChanged(position);
        } catch (Exception e) {
            // Handle Firebase or adapter update error silently
        }
    }

    private void updateReactionInFirebase(DatabaseReference ref, String messageKey, String reaction, String userId) {
        try {
            if (ref == null || messageKey == null || userId == null) {
                return; // Exit if essential components are null
            }
            
            ref.orderByChild("timeStamp").equalTo(Long.parseLong(messageKey))
                .get().addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            for (com.google.firebase.database.DataSnapshot snapshot : task.getResult().getChildren()) {
                                if (reaction != null) {
                                    snapshot.getRef().child("reaction").setValue(reaction);
                                    snapshot.getRef().child("reactionBy").setValue(userId);
                                } else {
                                    snapshot.getRef().child("reaction").removeValue();
                                    snapshot.getRef().child("reactionBy").removeValue();
                                }
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // Handle Firebase update error silently
                    }
                });
        } catch (Exception e) {
            // Handle Firebase query error silently
        }
    }
}
