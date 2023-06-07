package com.tuan1611pupu.vishort.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.tuan1611pupu.vishort.Activity.EditActivity;
import com.tuan1611pupu.vishort.Adapter.UserPostVideoAdapter;
import com.tuan1611pupu.vishort.Model.Democontents;
import com.tuan1611pupu.vishort.Model.Reels;
import com.tuan1611pupu.vishort.Model.User;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.Utilities.PreferenceManager;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ImageView imageProfile,edit;
    private TextView userName,bioUser;
    private RecyclerView rcListVideo;
    private FirebaseFirestore database;
    private ArrayList<Reels> list;
    private PreferenceManager preferenceManager;
    private UserPostVideoAdapter userPostVideoAdapter;
    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getContext());
        list = new ArrayList<>();
        userPostVideoAdapter = new UserPostVideoAdapter(list,getContext());
        imageProfile = view.findViewById(R.id.profile_image);
        userName =view.findViewById(R.id.username);
        bioUser = view.findViewById(R.id.bioUser1);
        rcListVideo = view.findViewById(R.id.rvPostVideo);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rcListVideo.setLayoutManager(staggeredGridLayoutManager);
        rcListVideo.setAdapter(userPostVideoAdapter);

        edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), EditActivity.class);
            intent.putExtra("user",user);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        getData();
        getListReels();
        return  view;
    }

    private void getData() {
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
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
                        //Glide.with(this).load(user.getImage()).into(imageProfile);
                        Picasso.get().load(user.getImage()).into(imageProfile);
                        userName.setText(user.getUsername());
                        bioUser.setText(user.getBio());
                    } else {
                        // loi
                    }
                });

    }

    private void getListReels(){
        list.clear();
        database.collection(Constants.KEY_COLLECTION_REELS)
                .whereEqualTo("reelsBy",preferenceManager.getString(Constants.KEY_USER_ID))
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
