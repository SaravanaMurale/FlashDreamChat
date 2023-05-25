package com.hermes.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.R;
import com.hermes.chat.activities.UserNameSignInActivity;
import com.hermes.chat.fragments.OptionsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class LanguageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    public List<String> itemsData = new ArrayList<>();
    UserNameSignInActivity splashScreenActivity;
    OptionsFragment optionsFragment;


    public LanguageListAdapter(Context mContext, List<String> data,
                               UserNameSignInActivity splashScreenActivity) {
        this.mContext = mContext;
        this.itemsData = data;
        this.splashScreenActivity = splashScreenActivity;
    }

    public LanguageListAdapter(Context mContext, List<String> data,
                               OptionsFragment optionsFragment) {
        this.mContext = mContext;
        this.itemsData = data;
        this.optionsFragment = optionsFragment;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View itemView;
        itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_language, parent, false);
        return new LanguageViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        LanguageViewHolder languageViewHolder = (LanguageViewHolder) viewHolder;
        languageViewHolder.tvLangTxt.setText(itemsData.get(position).split("-")[0]);

        languageViewHolder.tvLangTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (splashScreenActivity != null)
                    splashScreenActivity.setLocale(itemsData.get(viewHolder.getAdapterPosition()),
                            itemsData.get(viewHolder.getAdapterPosition()).split("-")[1],
                            viewHolder.getAdapterPosition(),0);
                else if (optionsFragment != null)
                    optionsFragment.setLocale(itemsData.get(viewHolder.getAdapterPosition()),
                            itemsData.get(viewHolder.getAdapterPosition()).split("-")[1]);
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
