package com.hermes.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.R;
import com.hermes.chat.activities.MyStatusActivity;
import com.hermes.chat.models.StatusImageList;
import com.hermes.chat.utils.Helper;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmList;

public class MyStatusAdapter extends RecyclerView.Adapter<MyStatusAdapter.ViewHolder> {
    private Context context;
    RealmList<StatusImageList> urlList;

    public MyStatusAdapter(Context context, RealmList<StatusImageList> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_my_status,
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get()
                .load(urlList.get(i).getUrl())
                .tag(this)
                .placeholder(R.drawable.ic_avatar)
                .into(viewHolder.statusImage);
        viewHolder.time.setText(Helper.getFormattedDate(urlList.get(i).getUploadTime()));

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyStatusActivity) context).showDeleteDialog(urlList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView statusImage;
        TextView time;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusImage = itemView.findViewById(R.id.statusImage);
            time = itemView.findViewById(R.id.time);
            delete = itemView.findViewById(R.id.delete);
        }
    }


}
