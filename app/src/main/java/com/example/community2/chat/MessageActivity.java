package com.example.community2.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.community2.R;
import com.example.community2.model.ChatModel;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private EditText text;
    private ImageButton send;
    private RecyclerView recyclerView;
    private String uid;
    private ChatModel chatModel = new ChatModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.community2.R.layout.activity_message);

        text = (EditText) findViewById(R.id.messageActivity_editText);
        send = (ImageButton) findViewById(R.id.messageActivity_button);
        recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recylerview);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatModel.users.put(uid, true);
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatModel.roomName).setValue(chatModel);

                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = text.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatModel.roomName).child("comments").push().setValue(comment);
            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

        }

        void getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatModel.roomName).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();
                    for(DataSnapshot item : snapshot.getChildren()) {
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((MessageViewHolder)holder).textView_message.setText(comments.get(position).message);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public LinearLayout linearLayout_destination;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textView_name = (TextView) view.findViewById(R.id.messageItem_textview_name);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearLayout_destination);
            }
        }
    }
}