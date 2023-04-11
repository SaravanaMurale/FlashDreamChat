package com.hermes.chat.interfaces;

import com.hermes.chat.models.Message;

import java.util.ArrayList;

/**
 * Created by a_man on 6/28/2017.
 */

public interface OnUserDetailFragmentInteraction {
    void getAttachments();

    ArrayList<Message> getAttachments(int tabPos);

    void switchToMediaFragment();
}