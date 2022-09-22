package com.example.community2.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.community2.R;
import com.example.community2.chat.MessageActivity;
import com.example.community2.model.ChatModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {
    EditText title;
    EditText explain;
    LinearLayout dialogView;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    List<String> keys = new ArrayList<>();

    EditText editText_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.communityFragment_recyclerview);
        editText_search = (EditText) view.findViewById(R.id.communityFragment_edittext);

        CommunityFragmentRecyclerViewAdapter adapter = new CommunityFragmentRecyclerViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(adapter);

        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.communityFragment_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                LayoutInflater inflater1 = getLayoutInflater();
                dialogView = (LinearLayout) inflater1.inflate(R.layout.dialog, null);
                dialogView = (LinearLayout) View.inflate(view.getContext(), R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("커뮤니티 생성");
                builder.setView(dialogView);
                builder.setPositiveButton("만들기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        title = (EditText) dialogView.findViewById(R.id.dialog_edittext_title);
                        explain = (EditText) dialogView.findViewById(R.id.dialog_edittext_explain);
                        chatModel.roomName = title.getText().toString();
                        chatModel.roomExplain = explain.getText().toString();
                        chatModel.users.put(uid, true);
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    class CommunityFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel> unfilterList;
        List<ChatModel> filterList;

        public CommunityFragmentRecyclerViewAdapter() {
            unfilterList = new ArrayList<>();
            filterList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("chatrooms").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    unfilterList.clear();
                    filterList.clear();
                    for(DataSnapshot item : snapshot.getChildren()) {
                        ChatModel chatModel = item.getValue(ChatModel.class);
                        unfilterList.add(chatModel);
                        filterList.add(chatModel);
                        keys.add(item.getKey());
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ((CustomViewHolder)holder).textView_name.setText(filterList.get(position).roomName);
            ((CustomViewHolder)holder).textView_explain.setText(filterList.get(position).roomExplain);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    intent.putExtra("roomId", keys.get(position));
                    intent.putExtra("roomName", filterList.get(position).roomName);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.from_right,R.anim.to_left);
                    startActivity(intent, activityOptions.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return filterList.size();
        }

        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String str = charSequence.toString();
                    if(str.isEmpty()) {
                        filterList = unfilterList;
                    } else {
                        List<ChatModel> filteringList = new ArrayList<>();
                        for( ChatModel item : unfilterList) {
                            if(item.roomName.toLowerCase().contains(str)) {
                                filteringList.add(item);
                            }
                        }
                        filterList = filteringList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filterList = (List<ChatModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_name;
            public TextView textView_explain;

            public CustomViewHolder(View view) {
                super(view);
                textView_name = (TextView) view.findViewById(R.id.communityItem_textview_name);
                textView_explain = (TextView) view.findViewById(R.id.communityItem_textview_explain);
            }
        }
    }
}
