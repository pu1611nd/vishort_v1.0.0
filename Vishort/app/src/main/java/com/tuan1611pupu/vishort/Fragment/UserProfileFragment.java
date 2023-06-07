package com.tuan1611pupu.vishort.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuan1611pupu.vishort.Activity.ChatActivity;
import com.tuan1611pupu.vishort.Adapter.UserPostVideoAdapter;
import com.tuan1611pupu.vishort.Model.Reels;
import com.tuan1611pupu.vishort.Model.User;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.Utilities.PreferenceManager;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment {

    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    private String userId;
    private User user;
    private ArrayList<Reels> list;
    private UserPostVideoAdapter userPostVideoAdapter;

    private RecyclerView rvPostVideo;
    private ImageView back,profilePic,coverImag;
    private RelativeLayout message;

    private TextView username,bioUser;

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile,container,false);
        preferenceManager = new PreferenceManager(getContext());
        database = FirebaseFirestore.getInstance();
        list = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            // Lấy đối tượng Reel từ Bundle
            userId = (String) bundle.getSerializable("userId");
            // Sử dụng đối tượng Reel ở đây
        }

        rvPostVideo = view.findViewById(R.id.rvPostVideo);
        back = view.findViewById(R.id.back);
        message = view.findViewById(R.id.message);
        profilePic = view.findViewById(R.id.profilePic);
        coverImag = view.findViewById(R.id.coverImag);
        username = view.findViewById(R.id.username);
        bioUser = view.findViewById(R.id.bioUser);
        userPostVideoAdapter = new UserPostVideoAdapter(list,getContext());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rvPostVideo.setLayoutManager(staggeredGridLayoutManager);
        rvPostVideo.setAdapter(userPostVideoAdapter);

        message.setOnClickListener(v->{
            Intent intent1 = new Intent(getContext(), ChatActivity.class);
            intent1.putExtra(Constants.KEY_USER, user);
            startActivity(intent1);
        });
        back.setOnClickListener(v->{
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.popBackStack();

        });


        getUser();
        getListReels();
        return view;
    }


    private void getUser(){
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        // loi
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Thực hiện cập nhật dữ liệu ở đây
                        user = new User();
                        user.setUsername(documentSnapshot.getString(Constants.KEY_USER_NAME));
                        user.setImage_cover(documentSnapshot.getString("image_cover"));
                        user.setBio(documentSnapshot.getString("bio"));
                        user.setId(documentSnapshot.getString("id"));
                        user.setProvider(documentSnapshot.getString("provider"));
                        user.setEmail(documentSnapshot.getString(Constants.KEY_EMAIL));
                        user.setImage(documentSnapshot.getString(Constants.KEY_IMAGE));
                        Glide.with(this).load(user.getImage()).into(profilePic);
                        Glide.with(this).load(user.getImage_cover()).into(coverImag);
                        username.setText(user.getUsername());
                        bioUser.setText(user.getBio());

                    } else {
                        // loi
                    }
                });
    }


    private void getListReels(){
        list.clear();
        database.collection(Constants.KEY_COLLECTION_REELS)
                .whereEqualTo("reelsBy",userId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        for (DocumentSnapshot documentSnapshot :task.getResult().getDocuments()) {
                            Reels reels = documentSnapshot.toObject(Reels.class);
                            reels.setReelsId(documentSnapshot.getString("reelsId"));
                            list.add(reels);
                        }
                        userPostVideoAdapter.notifyDataSetChanged();
                    }else{

                    }
                });
    }

}