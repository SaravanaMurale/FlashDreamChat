package com.hermes.chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.R;
import com.hermes.chat.activities.UserNameSignInActivity;
import com.hermes.chat.fragments.ChatDetailFragment;
import com.hermes.chat.fragments.OptionsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class DisappearingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "DisappearingListAdapter";
    Context mContext;
    public List<String> itemsData = new ArrayList<>();
    UserNameSignInActivity splashScreenActivity;
    ChatDetailFragment chatDetailFragment;
    OptionsFragment optionsFragment;
    SharedPreferences pref;
    String msgTime= "Turn off";


    public DisappearingListAdapter(Context mContext, List<String> data,
                                   ChatDetailFragment optionsFragment, String msgTime) {
        this.mContext = mContext;
        this.itemsData = data;
        this.chatDetailFragment = optionsFragment;
        pref = mContext.getSharedPreferences(
                String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        this.msgTime = msgTime;
        Log.d(TAG, "DisappearingListAdapter: "+msgTime);
    }

    public DisappearingListAdapter(Context mContext, List<String> data,
                                   OptionsFragment optionsFragment) {
        this.mContext = mContext;
        this.itemsData = data;
        this.optionsFragment = optionsFragment;
        pref = mContext.getSharedPreferences(
                String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        msgTime = pref.getString("disappear", "");
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_language, parent, false);
        return new LanguageViewHolder(itemView);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        LanguageViewHolder languageViewHolder = (LanguageViewHolder) viewHolder;
        String time = itemsData.get(position)+" Minutes";

        Log.d(TAG, "onBindViewHolder: "+msgTime+" ==== "+time);

        if(msgTime.trim().equalsIgnoreCase(time)){
            languageViewHolder.tvLangTxt.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }
        if(!itemsData.get(viewHolder.getAdapterPosition()).equalsIgnoreCase("Turn off"))
        {
            languageViewHolder.tvLangTxt.setText(itemsData.get(position) + " Minutes");
        }else {
            languageViewHolder.tvLangTxt.setText(itemsData.get(position));
        }

        languageViewHolder.tvLangTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionsFragment != null){
                    if(!itemsData.get(viewHolder.getAdapterPosition()).equalsIgnoreCase("Turn off")){
                        optionsFragment.addToSharedPref(itemsData.get(viewHolder.getAdapterPosition())+" Minutes");
                    } else {
                        optionsFragment.addToSharedPref(itemsData.get(viewHolder.getAdapterPosition()));
                    }

                } else if (chatDetailFragment != null){
                    if(!itemsData.get(viewHolder.getAdapterPosition()).equalsIgnoreCase("Turn off")){
                    chatDetailFragment.addToSharedPref(itemsData.get(viewHolder.getAdapterPosition())+" Minutes");
                    } else {
                        chatDetailFragment.addToSharedPref(itemsData.get(viewHolder.getAdapterPosition()));
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }


    public class LanguageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_lang_txt)
        TextView tvLangTxt;

        public LanguageViewHolder(View view) {
            super(view);
            tvLangTxt = view.findViewById(R.id.tv_lang_txt);
        }
    }

}
