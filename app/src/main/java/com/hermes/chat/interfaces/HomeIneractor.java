package com.hermes.chat.interfaces;

import com.hermes.chat.models.Contact;
import com.hermes.chat.models.User;

import java.util.ArrayList;

/**
 * Created by a_man on 01-01-2018.
 */

public interface HomeIneractor {
    User getUserMe();

    ArrayList<Contact> getLocalContacts();

}
