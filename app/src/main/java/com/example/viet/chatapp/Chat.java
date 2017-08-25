package com.example.viet.chatapp;

/**
 * Created by viet on 23/08/2017.
 */

public class Chat {
    String content;
    String displayName;
    String type;

    public Chat() {
    }

    public Chat(String content, String displayName, String type) {
        this.content = content;
        this.displayName = displayName;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
