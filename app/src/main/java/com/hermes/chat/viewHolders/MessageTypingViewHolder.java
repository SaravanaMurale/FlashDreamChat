package com.hermes.chat.viewHolders;

import android.view.View;

import com.hermes.chat.models.AttachmentTypes;

public class MessageTypingViewHolder extends BaseMessageViewHolder {
    public MessageTypingViewHolder(View itemView) {
        super(itemView, AttachmentTypes.NONE_TYPING,null);
    }
}
