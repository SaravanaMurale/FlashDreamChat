package com.hermes.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.R;
import com.hermes.chat.fragments.MyStatusFragment;
import com.hermes.chat.models.Group;
import com.hermes.chat.models.StatusImageNew;
import com.hermes.chat.models.StatusNew;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.CircularStatusView;
import com.hermes.chat.utils.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.RealmList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<StatusNew> dataList;
    MyStatusFragment myStatusFragment;


    public StatusAdapter(Context context, ArrayList<StatusNew> dataList, MyStatusFragment aStatusFragment) {
        this.context = context;
        this.dataList = dataList;
        this.myStatusFragment = aStatusFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_item_status, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.setData(dataList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStatusFragment.navigateStatusStories(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView status, name, lastMessage, time;
        private ImageView myUserImageOnline, statusImage;
        private CircularStatusView image;
        private RelativeLayout user_details_container;
        //  private RecyclerView statusImageRecycler;
        // private StatusItemAdapter adapter;

        MyViewHolder(View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.emotion);
            name = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            lastMessage = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.user_image);
            statusImage = itemView.findViewById(R.id.statusImage);
            //   statusImageRecycler = itemView.findViewById(R.id.statusImageRecycler);


            image.setPortionsColor(context.getResources().getColor(R.color.colorPrimary));
            user_details_container = itemView.findViewById(R.id.user_details_container);
            myUserImageOnline = itemView.findViewById(R.id.user_image_online);
        }

        private void setData(StatusNew chat) {
            User chatUser = chat.getUser();
            Group chatGroup = chat.getGroup();
            RealmList<StatusImageNew> statusImagesList = chat.getStatusImages();

            for (int i = 0; i < statusImagesList.size(); i++) {
                image.setPortionsCount(statusImagesList.get(i).getAttachment().getUrlList().size());
                if (statusImagesList.get(i).getAttachment().getUrlList().size() > 0) {
                    Picasso.get()
                            .load(statusImagesList.get(i).getAttachment().getUrlList()
                                    .get(statusImagesList.get(i).getAttachment().getUrlList().size() - 1).getUrl())
                            .tag(this)
                            .placeholder(R.drawable.ic_avatar)
                            .into(statusImage);

                   /* LinearLayoutManager mLayoutManager = new LinearLayoutManager(context,
                            LinearLayoutManager.HORIZONTAL, false);
                    statusImageRecycler.setLayoutManager(mLayoutManager);

                    adapter = new StatusItemAdapter(context, statusImagesList.get(i).getAttachment().getUrlList(), chat);
                    statusImageRecycler.setAdapter(adapter);*/
                }
            }


           /* if (chatUser != null && chatUser.getImage() != null && !chatUser.getImage().equalsIgnoreCase("")) {
                Picasso.get()
                        .load(chatUser.getImage())
                        .tag(this)
                        .placeholder(R.drawable.ic_logo_)
                        .into(image);

            } else if (chatGroup != null && chatGroup.getImage() != null && !chatGroup.getImage().equalsIgnoreCase("")) {
                Picasso.get()
                        .load(chatGroup.getImage())
                        .tag(this)
                        .placeholder(R.drawable.ic_logo_)
                        .into(image);
            }*/

            name.setText(chatUser != null ? chatUser.getNameToDisplay() : chatGroup.getName());
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            //  name.setCompoundDrawablesWithIntrinsicBounds(0, 0, !chat.isRead() ? R.drawable.ring_blue : 0, 0);
            status.setText(chatUser != null ? chatUser.getStatus() : chatGroup.getStatus());
            time.setText(Helper.getFormattedDate(chat.getTimeUpdated()));
            lastMessage.setText(chat.getLastMessage());
            lastMessage.setTextColor(ContextCompat.getColor(context, !chat.isRead() ? R.color.textColorPrimary : R.color.textColorSecondary));

            user_details_container.setBackgroundColor(ContextCompat.getColor(context, (chat.isSelected() ? R.color.bg_gray : R.color.colorIcon)));

            try {
                if (chatUser != null && chatUser.isOnline()) {
                    myUserImageOnline.setVisibility(View.VISIBLE);
                    lastMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, chatUser.isOnline() ? R.drawable.ring_green : 0, 0);
                } else {
                    myUserImageOnline.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
