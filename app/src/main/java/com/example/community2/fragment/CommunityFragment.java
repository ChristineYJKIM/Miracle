package com.example.community2.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.community2.R;
import com.example.community2.chat.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;

public class CommunityFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.communityFragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new CommunityFragmentRecyclerViewAdapter());
        return view;
    }

    class CommunityFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public CommunityFragmentRecyclerViewAdapter() {
            //Community 정보를 database에서 가져오기. chatmodel(chatrooms)을 communitymodel로 생각해서
            //동영상 #7 6:45
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //chatrooms에 imageurl 넣어야 함. -> 여기가 화면에 나타내는 부분.
            //동영상 #7 14:23
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.from_right,R.anim.to_left);
                    startActivity(intent, activityOptions.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            //여기도 나중에 채팅방 만들고 나면 chatrooms(chatmodel) 사이즈 return하기.
            return 0;
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_name;
            public TextView textView_explain;
            public ImageView imageView;
            public LinearLayout linearLayout;

            public CustomViewHolder(View view) {
                super(view);
                textView_name = (TextView) view.findViewById(R.id.communityItem_textview_name);
                textView_explain = (TextView) view.findViewById(R.id.communityItem_textview_explain);
                imageView = (ImageView) view.findViewById(R.id.communityItem_imageview);
                linearLayout = (LinearLayout) view.findViewById(R.id.communityItem_linearlayout);
            }
        }
    }
}
