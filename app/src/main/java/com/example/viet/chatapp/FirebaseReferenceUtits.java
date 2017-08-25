package com.example.viet.chatapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by viet on 23/08/2017.
         */

public class FirebaseReferenceUtits {
    public static final String CHAT = "chat";
    private static DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public static DatabaseReference getChatRef() {
        return mDatabaseRef.child(CHAT);
    }

    public static StorageReference getImageRef(String name) {
        return mStorageRef.child("image/"+name+".jpg");
    }
}
