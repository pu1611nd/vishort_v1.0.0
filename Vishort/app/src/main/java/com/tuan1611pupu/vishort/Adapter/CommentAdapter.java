package com.tuan1611pupu.vishort.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuan1611pupu.vishort.Model.Comment;
import com.tuan1611pupu.vishort.Model.User;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.databinding.LayoutCommentBinding;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {
    private ArrayList<Comment> list;
    private Context context;
    private FirebaseFirestore database;

    public CommentAdapter(ArrayList<Comment> list, Context context) {
        this.list = list;
        this.context = context;
        this.database = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_comment,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Comment comment = list.get(position);
        holder.binding.tvCommentBody.setText(comment.getCommentBody());
        String timeAgo = TimeAgo.using(comment.getCommentedAt());
        holder.binding.tvCommentTime.setText(timeAgo);
        DocumentReference userRef = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(comment.getCommentedBy());

        userRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Xử lý lỗi
                return;
            }

            // Lấy dữ liệu từ tài liệu Firestore
            User user = new User();
            user.setUsername(value.getString(Constants.KEY_USER_NAME));
            user.setEmail(value.getString(Constants.KEY_EMAIL));
            user.setImage(value.getString(Constants.KEY_IMAGE));

            // Cập nhật giao diện người dùng với dữ liệu mới
            Glide.with(holder.binding.getRoot()).load(user.getImage()).into(holder.binding.imageProfile);
            holder.binding.tvName.setText(user.getUsername());
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        LayoutCommentBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutCommentBinding.bind(itemView);
        }
    }
}
