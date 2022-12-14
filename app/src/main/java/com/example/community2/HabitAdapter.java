package com.example.community2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class HabitAdapter extends ListAdapter<String, HabitAdapter.MyViewHolder> {
        //string -> model로 고쳐...

        // ListAdapter의 생성자
        protected HabitAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback) {
            super(diffCallback);
        }

        // 뷰홀더 클래스 - 아이템 하나씩 그릴 때 들어가게 함
        public static class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView habitName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                habitName = itemView.findViewById(R.id.txt_Name);
            }

            private void bind(String strNum) {
                habitName.setText(strNum);
            }


        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_retrive, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bind(getItem(position));
        }
    }

