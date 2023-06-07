package com.tuan1611pupu.vishort.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.tuan1611pupu.vishort.Adapter.CommentAdapter;
import com.tuan1611pupu.vishort.Model.Comment;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.Utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Fragment_comment extends BottomSheetDialogFragment {


    private ImageView imgExit;
    private RecyclerView rcListComment;
    private EditText inputComment;
    private FrameLayout layoutSend;

    private ArrayList<Comment> list;
    private CommentAdapter adapter;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    private String reelsId;
    private String reelsBy;

    private int commentCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            reelsId= bundle.getString("reelId"); // Lấy giá trị từ Bundle
            reelsBy= bundle.getString("reelBy");
        }

        imgExit = view.findViewById(R.id.imgExit);
        rcListComment = view.findViewById(R.id.rc_comment);
        inputComment = view.findViewById(R.id.inputMessage);
        layoutSend = view.findViewById(R.id.layoutSend);
        preferenceManager = new PreferenceManager(getContext());
        list = new ArrayList<>();
        adapter = new CommentAdapter(list,getContext());
        database = FirebaseFirestore.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rcListComment.setLayoutManager(linearLayoutManager);
        rcListComment.setNestedScrollingEnabled(false);
        rcListComment.setAdapter(adapter);

        imgExit.setOnClickListener(v->{
            dismiss();
        });
        layoutSend.setOnClickListener(v->{
            Comment comment = new Comment();
            comment.setCommentBody(inputComment.getText().toString().trim());
            comment.setCommentedAt(new Date().getTime());
            comment.setCommentedBy(preferenceManager.getString(Constants.KEY_USER_ID));
            Map<String, Object> updates = new HashMap<>();
            commentCount ++;
            updates.put("comments",commentCount);
            updates.put("comment", FieldValue.arrayUnion(comment));
            database.collection(Constants.KEY_COLLECTION_REELS)
                    .document(reelsId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // Xử lý khi cập nhật thành công
                        inputComment.setText("");
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi cập nhật thất bại
                    });
        });

        getComment();

        return view;
    }

    private void getComment() {
        DocumentReference postRef = database.collection(Constants.KEY_COLLECTION_REELS)
                .document(reelsId);
        postRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Xử lý lỗi
                return;
            }

            List<HashMap<String, Object>> commentData = (List<HashMap<String, Object>>) value.get("comment");
            List<Comment> comments = new ArrayList<>();
            if(commentData != null){
                for (HashMap<String, Object> commentHashMap : commentData) {
                    Comment comment = new Comment();
                    comment.setCommentBody(((String) commentHashMap.get("commentBody")));
                    comment.setCommentedBy(((String) commentHashMap.get("commentedBy")));
                    comment.setCommentedAt(((long) commentHashMap.get("commentedAt")));
                    comments.add(comment);
                }


                // Xóa list hiện tại và thêm danh sách comment vào list
                list.clear();
                list.addAll(comments);
                commentCount = list.size();

                // Báo cho adapter biết rằng dữ liệu đã thay đổi
                adapter.notifyDataSetChanged();
            }

        });



    }
}
