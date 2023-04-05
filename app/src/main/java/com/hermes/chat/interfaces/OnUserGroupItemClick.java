package com.hermes.chat.interfaces;

import android.view.View;

import com.hermes.chat.models.Group;
import com.hermes.chat.models.User;


public interface OnUserGroupItemClick {
    void OnUserClick(User user, int position, View userImage);
    void OnGroupClick(Group group, int position, View userImage);
}
