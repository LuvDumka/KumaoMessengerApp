
package com.luvdumka.kumaonmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatwindo extends AppCompatActivity {
    String reciverimg, reciverUid,reciverName,SenderUID;
    CircleImageView profile;
    TextView reciverNName, userOnlineStatus;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public  static String senderImg;
    public  static String reciverIImg;
    CardView sendbtn;
    EditText textmsg;

    String senderRoom,reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdpter mmessagesAdpter;
    
    // Typing indicator components
    LinearLayout typingIndicatorLayout;
    TextView typingIndicatorText;
    ProgressBar typingProgress;
    Handler typingHandler = new Handler();
    Runnable stopTypingRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindo);
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        reciverNName = findViewById(R.id.recivername);
        userOnlineStatus = findViewById(R.id.user_online_status);
        profile = findViewById(R.id.profileimgg);
        messageAdpter = findViewById(R.id.msgadpter);
        
        // Initialize typing indicator components with safety checks
        try {
            typingIndicatorLayout = findViewById(R.id.typing_indicator_layout);
            typingIndicatorText = findViewById(R.id.typing_indicator_text);
            typingProgress = findViewById(R.id.typing_progress);
            
            // Only enable typing indicator if all components are found
            if (typingIndicatorLayout != null && typingIndicatorText != null && typingProgress != null) {
                // Typing indicator is ready
            }
        } catch (Exception e) {
            // If any error, disable typing indicator
            typingIndicatorLayout = null;
            typingIndicatorText = null;
            typingProgress = null;
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);
        mmessagesAdpter = new messagesAdpter(chatwindo.this,messagesArrayList);
        messageAdpter.setAdapter(mmessagesAdpter);


        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(""+reciverName);

        // Monitor receiver's online status
        monitorUserOnlineStatus();
        
        // Setup typing indicator monitoring with safety checks
        if (typingIndicatorLayout != null && typingIndicatorText != null) {
            setupTypingIndicator();
        }
        
        SenderUID =  firebaseAuth.getUid();

        senderRoom = SenderUID+reciverUid;
        reciverRoom = reciverUid+SenderUID;
        
        // Set room info for reactions (with safety check)
        if (mmessagesAdpter != null) {
            mmessagesAdpter.setSenderRoom(senderRoom);
            mmessagesAdpter.setReceiverRoom(reciverRoom);
        }



        DatabaseReference  reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference  chatreference = database.getReference().child("chats").child(senderRoom).child("messages");


        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    
                    // Mark received messages as delivered if they're not from current user
                    if (messages != null && !messages.getSenderid().equals(SenderUID)) {
                        // Auto-mark as delivered when message is received
                        if ("sent".equals(messages.getMessageStatus())) {
                            messages.setMessageStatus("delivered");
                            // Update in database
                            dataSnapshot.getRef().child("messageStatus").setValue("delivered");
                        }
                        
                        // Auto-mark as read after a short delay (simulating user reading)
                        if ("delivered".equals(messages.getMessageStatus())) {
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    messages.setMessageStatus("read");
                                    dataSnapshot.getRef().child("messageStatus").setValue("read");
                                    mmessagesAdpter.notifyDataSetChanged();
                                }
                            }, 2000); // 2 second delay to simulate reading
                        }
                    }
                    
                    messagesArrayList.add(messages);
                }
                mmessagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg= snapshot.child("profilepic").getValue().toString();
                reciverIImg=reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(chatwindo.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                
                // Stop typing indicator when message is sent (with safety check)
                if (typingIndicatorLayout != null) {
                    sendTypingStatus(false);
                }
                
                Date date = new Date();
                // Create message with default "sent" status
                msgModelclass messagess = new msgModelclass(message,SenderUID,date.getTime());
                // Explicitly set status to sent
                messagess.setMessageStatus("sent");

                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Message sent successfully, update status to delivered after short delay
                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            messagess.setMessageStatus("delivered");
                                        }
                                    }, 1000); // 1 second delay to simulate delivery
                                }
                                
                                database.getReference().child("chats")
                                        .child(reciverRoom)
                                        .child("messages")
                                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });

    }

    private void monitorUserOnlineStatus() {
        DatabaseReference userStatusRef = database.getReference().child("user").child(reciverUid);
        userStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user != null) {
                        updateUserStatus(user);
                    } else {
                        // Handle case where user data is null
                        setUserOffline("User data is null");
                    }
                } else {
                    // Handle case where user doesn't exist
                    setUserOffline("User not found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setUserOffline("Database error: " + error.getMessage());
            }
        });
    }

    private void updateUserStatus(Users user) {
        if (user.isOnline()) {
            // User is online
            userOnlineStatus.setText(getString(R.string.online));
            userOnlineStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            // User is offline
            long lastSeenTime = user.getLastSeen();
            String lastSeenText = getLastSeenText(lastSeenTime);
            userOnlineStatus.setText(lastSeenText);
            userOnlineStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void setUserOffline(String reason) {
        userOnlineStatus.setText(getString(R.string.offline));
        userOnlineStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private String getLastSeenText(long lastSeenTime) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastSeenTime;
        
        if (timeDiff < 60000) { // Less than 1 minute
            return getString(R.string.last_seen) + " " + getString(R.string.just_now);
        } else if (timeDiff < 3600000) { // Less than 1 hour
            int minutes = (int) (timeDiff / 60000);
            return getString(R.string.last_seen) + " " + minutes + " " + getString(R.string.min_ago);
        } else if (timeDiff < 86400000) { // Less than 1 day
            int hours = (int) (timeDiff / 3600000);
            String hourText = hours > 1 ? getString(R.string.hours_ago) : getString(R.string.hour_ago);
            return getString(R.string.last_seen) + " " + hours + " " + hourText;
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault());
            return getString(R.string.last_seen) + " " + sdf.format(new java.util.Date(lastSeenTime));
        }
    }

    // ========== TYPING INDICATOR METHODS ==========
    
    private void setupTypingIndicator() {
        // Listen for typing events from the other user
        monitorOtherUserTyping();
        
        // Setup text watcher for current user typing
        textmsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    sendTypingStatus(true);
                    resetStopTypingTimer();
                } else {
                    sendTypingStatus(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void monitorOtherUserTyping() {
        DatabaseReference typingRef = database.getReference().child("typing").child(reciverRoom);
        typingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String typingStatus = snapshot.getValue(String.class);
                    if (typingStatus != null && !typingStatus.equals(SenderUID)) {
                        // Other user is typing
                        showTypingIndicator(reciverName + " is typing...");
                    } else {
                        hideTypingIndicator();
                    }
                } else {
                    hideTypingIndicator();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideTypingIndicator();
            }
        });
    }

    private void sendTypingStatus(boolean isTyping) {
        try {
            DatabaseReference typingRef = database.getReference().child("typing").child(senderRoom);
            if (isTyping) {
                typingRef.setValue(SenderUID);
            } else {
                typingRef.removeValue();
            }
        } catch (Exception e) {
            // Handle database error silently
        }
    }

    private void resetStopTypingTimer() {
        if (stopTypingRunnable != null) {
            typingHandler.removeCallbacks(stopTypingRunnable);
        }
        
        stopTypingRunnable = new Runnable() {
            @Override
            public void run() {
                sendTypingStatus(false);
            }
        };
        
        // Stop typing indicator after 3 seconds of inactivity
        typingHandler.postDelayed(stopTypingRunnable, 3000);
    }

    private void showTypingIndicator(String message) {
        if (typingIndicatorText != null && typingIndicatorLayout != null) {
            typingIndicatorText.setText(message);
            typingIndicatorLayout.setVisibility(View.VISIBLE);
            
            // Auto-scroll to bottom to show typing indicator
            if (messageAdpter != null && mmessagesAdpter != null) {
                messageAdpter.smoothScrollToPosition(mmessagesAdpter.getItemCount());
            }
        }
    }

    private void hideTypingIndicator() {
        if (typingIndicatorLayout != null) {
            typingIndicatorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop typing when user leaves the chat (with safety check)
        if (typingIndicatorLayout != null) {
            sendTypingStatus(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up typing status (with safety checks)
        if (typingIndicatorLayout != null) {
            sendTypingStatus(false);
        }
        if (typingHandler != null && stopTypingRunnable != null) {
            typingHandler.removeCallbacks(stopTypingRunnable);
        }
    }
}
