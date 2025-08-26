package com.luvdumka.kumaonmessenger;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdpter extends RecyclerView.Adapter<UserAdpter.viewholder> {
    Context mainActivity;
    ArrayList<Users> usersArrayList;
    public UserAdpter(MainActivity mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity=mainActivity;
        this.usersArrayList=usersArrayList;
    }

    @NonNull
    @Override
    public UserAdpter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdpter.viewholder holder, int position) {

        Users users = usersArrayList.get(position);
        holder.username.setText(users.userName);
        
        // Update status based on online/offline state
        if (users.isOnline()) {
            holder.userstatus.setText(mainActivity.getString(R.string.online));
            holder.onlineStatusIndicator.setImageResource(R.drawable.online_indicator);
        } else {
            // Show last seen time
            long lastSeenTime = users.getLastSeen();
            String lastSeenText = getLastSeenText(lastSeenTime);
            holder.userstatus.setText(lastSeenText);
            holder.onlineStatusIndicator.setImageResource(R.drawable.offline_indicator);
        }
        
        Picasso.get().load(users.profilepic).into(holder.userimg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, chatwindo.class);
                intent.putExtra("nameeee",users.getUserName());
                intent.putExtra("reciverImg",users.getProfilepic());
                intent.putExtra("uid",users.getUserId());
                mainActivity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    private String getLastSeenText(long lastSeenTime) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastSeenTime;
        
        if (timeDiff < 60000) { // Less than 1 minute
            return mainActivity.getString(R.string.last_seen) + " " + mainActivity.getString(R.string.just_now);
        } else if (timeDiff < 3600000) { // Less than 1 hour
            int minutes = (int) (timeDiff / 60000);
            return mainActivity.getString(R.string.last_seen) + " " + minutes + " " + mainActivity.getString(R.string.min_ago);
        } else if (timeDiff < 86400000) { // Less than 1 day
            int hours = (int) (timeDiff / 3600000);
            String hourText = hours > 1 ? mainActivity.getString(R.string.hours_ago) : mainActivity.getString(R.string.hour_ago);
            return mainActivity.getString(R.string.last_seen) + " " + hours + " " + hourText;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return mainActivity.getString(R.string.last_seen) + " " + sdf.format(new Date(lastSeenTime));
        }
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username;
        TextView userstatus;
        ImageView onlineStatusIndicator;
        
        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
            onlineStatusIndicator = itemView.findViewById(R.id.online_status_indicator);
        }
    }
}
