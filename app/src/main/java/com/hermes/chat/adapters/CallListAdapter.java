package com.hermes.chat.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hermes.chat.R;
import com.hermes.chat.activities.CallListActivity;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.models.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "CallListAdapter";
    private Context context;
    private ArrayList<User> myUsers;
    public ArrayList<User> itemsFiltered;
    private User userMe;

    public CallListAdapter(Context context, ArrayList<User> myUsers, User loggedInUser) {
        this.context = context;
        this.myUsers = myUsers;
        this.itemsFiltered = myUsers;
        this.userMe = loggedInUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_list_item_log_call,
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final User user = itemsFiltered.get(i);

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            viewHolder.myProgressBar.setVisibility(View.VISIBLE);
            if (user.getBlockedUsersIds() != null && !user.getBlockedUsersIds().contains(MainActivity.userId))
                Picasso.get()
                        .load(user.getImage())
                        .tag(this)
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(viewHolder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                viewHolder.myProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                viewHolder.myProgressBar.setVisibility(View.GONE);
                            }
                        });
            else {
                Picasso.get()
                        .load(R.drawable.ic_avatar)
                        .tag(this)
                        .error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar)
                        .into(viewHolder.userImage);
                viewHolder.myProgressBar.setVisibility(View.GONE);
            }
        } else {
            Picasso.get()
                    .load(R.drawable.ic_avatar)
                    .tag(this)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .into(viewHolder.userImage);
            viewHolder.myProgressBar.setVisibility(View.GONE);
        }
        viewHolder.userName.setText(user.getName());
        viewHolder.status.setText(user.getStatus());

        viewHolder.audioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+user.getDeviceToken());
                Log.d(TAG, "onClick: "+user.getOsType());
                ((CallListActivity) context).forwardToRoom("Audio call", false, user);
            }
        });

        viewHolder.videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CallListActivity) context).forwardToRoom("Video call", true, user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                ArrayList<User> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = myUsers;
                } else {
                    for (User user : myUsers) {
                        if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(user);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                itemsFiltered = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateReceiptsList(ArrayList<User> newlist) {
        this.itemsFiltered = newlist;
       /* itemsFiltered.clear();
        itemsFiltered.addAll(newlist);*/
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private ImageView audioCall;
        private ImageView videoCall;
        private TextView userName;
        private TextView status;
        private ProgressBar myProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            status = itemView.findViewById(R.id.status);
            audioCall = itemView.findViewById(R.id.audioCall);
            videoCall = itemView.findViewById(R.id.videoCall);
            myProgressBar = itemView.findViewById(R.id.progressBar);

        }
    }
}
