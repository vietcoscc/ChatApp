package com.example.viet.chatapp;

/**
 * Created by viet on 23/08/2017.
 */

public class UserManager {
    private String mDisplayName = "viet";
    private static UserManager sInstance;

    public static UserManager getsInstance() {
        if (sInstance == null) {
            sInstance = new UserManager();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }
}
