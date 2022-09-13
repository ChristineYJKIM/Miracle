package com.example.community2.chat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.community2.R;
import com.example.community2.model.ChatModel;
import com.example.community2.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {
    private EditText text;  //메세지 입력부분
    private RecyclerView recyclerView;  //이전 메세지들이 뜨도록 하는 recyclerview
    private String myuid;   //현재 로그인한 아이디
    private String uid = null; //채팅방으로 들어온 아이디
    private String roomId;  //현재 속한 채팅방
    private String roomName;
    private ProgressBar progressBar;

    private ImageView imageView;
    private Uri imageUri;

    Map<String, UserModel> users = new HashMap<>();
    List<ChatModel.Comment> comments = new ArrayList<>();

    private DatabaseReference databaseReference;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private ValueEventListener valueEventListener;
    MessageRecyclerViewAdapter messageRecyclerViewAdapter;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    int peopleCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.community2.R.layout.activity_message);

        text = (EditText) findViewById(R.id.messageActivity_editText);
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        uid = getIntent().getStringExtra("uid");
        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");
        progressBar = findViewById(R.id.messageActivity_progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()) {
                    users.put(item.getKey(), item.getValue(UserModel.class));
                }

                init();
                recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recylerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                messageRecyclerViewAdapter = new MessageRecyclerViewAdapter();
                recyclerView.setAdapter(messageRecyclerViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void init() {
        imageView = (ImageView)findViewById(R.id.messageActivity_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myuid != uid && uid != null) {
                    Map<String, Object> user = new HashMap<>();
                    user.put(uid, true);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("users").updateChildren(user);
                }

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                activityResult.launch(galleryIntent);
            }
        });

        ImageButton send = (ImageButton) findViewById(R.id.messageActivity_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //채팅방으로 들어와서 메세지를 보낸 아이디가 내가 로그인한 아이디와 같지 않으면 채팅방 유저에 추가해주기.
                if(myuid != uid && uid != null) {
                    Map<String, Object> user = new HashMap<>();
                    user.put(uid, true);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("users").updateChildren(user);
                }
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = myuid;
                comment.message = text.getText().toString();
                comment.imageUrl = null;
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Map<String, Boolean> map = (Map<String, Boolean>) snapshot.getValue();
                                for(String item : map.keySet()) {
                                    if(item.equals(myuid)) {
                                        continue;
                                    }
                                    sendFcm(users.get(item).pushToken);
                                }
                                text.setText("");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });
    }

    void sendFcm(String pushToken) {
        String BASE_URL = "https://fcm.googleapis.com/fcm/send";
        String SERVER_KEY = "key=AAAAlGh892g:APA91bHRktBXD6b2aQCWZhPnQanyd8MPxdIJ_OWvXOFzqPMKJ7fr_rfGCuLV5b8I4g6FlrAruPW-9bOstg2--eFavyAVDXP57OWwMgmJNvnNjcHeMNKAjGDC9WPL4xLYmMKzme6uFoJc";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to", pushToken);
            JSONObject notification = new JSONObject();
            notification.put("title", roomName);
            notification.put("body", text.getText().toString().trim());
            jsonObject.put("notification", notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL,jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public MessageRecyclerViewAdapter() {
            getMessageList();
        }

        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();
                    Map<String, Object> readUserMap = new HashMap<>();
                    for(DataSnapshot item : snapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify = item.getValue(ChatModel.Comment.class);
                        comment_modify.readUsers.put(myuid, true);
                        readUserMap.put(key, comment_modify);
                        comments.add(comment_origin);
                    }
                    if(comments.size() > 0) {
                        if(!comments.get(comments.size()-1).readUsers.containsKey(myuid)) {
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("comments").updateChildren(readUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    notifyItemInserted(comments.size() + 1);
                                    recyclerView.scrollToPosition(comments.size()-1);
                                }
                            });
                        } else {
                            notifyItemInserted(comments.size() + 1);
                            recyclerView.scrollToPosition(comments.size()-1);
                        }
                    }
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
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            if(comments.get(position).uid.equals(myuid)) {
                if(comments.get(position).imageUrl == null) {
                    messageViewHolder.imageView_selectPhoto.setVisibility(View.GONE);
                    messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                    messageViewHolder.textView_message.setText(comments.get(position).message);
                    messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                } else {
                    messageViewHolder.textView_message.setVisibility(View.GONE);
                    messageViewHolder.imageView_selectPhoto.setVisibility(View.VISIBLE);
                    Glide.with(MessageActivity.this).load(comments.get(position).imageUrl).into(messageViewHolder.imageView_selectPhoto);
                }
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
            } else {
                if(comments.get(position).imageUrl == null) {
                    messageViewHolder.imageView_selectPhoto.setVisibility(View.GONE);
                    messageViewHolder.textView_message.setVisibility(View.VISIBLE);
                    messageViewHolder.textView_message.setText(comments.get(position).message);
                    messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                } else {
                    messageViewHolder.textView_message.setVisibility(View.GONE);
                    messageViewHolder.imageView_selectPhoto.setVisibility(View.VISIBLE);
                    Glide.with(MessageActivity.this).load(comments.get(position).imageUrl).into(messageViewHolder.imageView_selectPhoto);
                }
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }

        void setReadCounter(int position, TextView textView) {
            if(peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) snapshot.getValue();
                        peopleCount = users.size();
                        int count = peopleCount - comments.get(position).readUsers.size();
                        if(count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                int count = peopleCount - comments.get(position).readUsers.size();
                if(count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            public ImageView imageView_selectPhoto;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textView_name = (TextView) view.findViewById(R.id.messageItem_textview_name);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearLayout_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearLayout_main);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textview_timestamp);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_right);
                imageView_selectPhoto = (ImageView) view.findViewById(R.id.messageItem_imageview);
            }
        }
    }

    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                uploadToFirebase(imageUri);
                messageRecyclerViewAdapter.notifyItemInserted(comments.size() + 1);
            }
        }
    });

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ChatModel.Comment comment = new ChatModel.Comment(uri.toString());
                        comment.uid = myuid;
                        comment.message = null;
                        comment.timestamp = ServerValue.TIMESTAMP;
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Map<String, Boolean> map = (Map<String, Boolean>) snapshot.getValue();
                                        for(String item : map.keySet()) {
                                            if(item.equals(myuid)) {
                                                continue;
                                            }
                                            sendFcm(users.get(item).pushToken);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MessageActivity.this, "전송실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}