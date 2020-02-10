package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;

/**
 * File: GuestModel.java
 *
 *
 * Model class for a guest user.
 *
 */
public class GuestModel implements LoginActivityMVPManager.guestModel {

    /** empty constructor */
    GuestModel() { }

    /** starting activity as a guest user */
    public void starGuestActivity(LoginActivityMVPManager.View view) {

        String password = "Guest";
        String name = "username";
        Intent intent = new Intent((Context) view, DrawerActivity.class);
        intent.putExtra(name, password);
        ((Context) view).startActivity(intent);

    }
}
