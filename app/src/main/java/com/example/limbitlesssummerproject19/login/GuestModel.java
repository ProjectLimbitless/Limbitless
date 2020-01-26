package com.example.limbitlesssummerproject19.login;

import android.content.Context;
import android.content.Intent;
import com.example.limbitlesssummerproject19.camera.DrawerActivity;

public class GuestModel implements LoginActivityMVPManager.guestModel {

    private String name = "username";
    private String password = "Guest";

    // empty constructor
    public GuestModel() { }

    // starting activity as a guest user
    public void starGuestActivity(LoginActivityMVPManager.View view) {

        Intent intent = new Intent((Context) view, DrawerActivity.class);
        intent.putExtra(name, password);
        ((Context) view).startActivity(intent);

    }
}
