package com.example.community2.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public String roomName;
    public Map<String, Boolean> users = new HashMap<>();
    public Map<String, Comment> comments = new HashMap<>();

    public static class Comment {
        public String uid;
        public String message;
    }
}
