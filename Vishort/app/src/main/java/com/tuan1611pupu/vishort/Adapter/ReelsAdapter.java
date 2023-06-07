package com.tuan1611pupu.vishort.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.tuan1611pupu.vishort.Model.Reels;
import com.tuan1611pupu.vishort.Model.User;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.databinding.ItemReelsBinding;

import java.util.ArrayList;
import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelsViewHolder> {
    OnReelsVideoAdapterListner onReelsVideoAdapterListner;
    private List<Reels> reels = new ArrayList<>();
    private List<ReelsViewHolder> holders = new ArrayList<>();
    Context context;
    private FirebaseFirestore database;

    @Override
    public ReelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ReelsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reels, parent, false));
    }


    public void setOnReelsVideoAdapterListner(OnReelsVideoAdapterListner onReelsVideoAdapterListner) {
        this.onReelsVideoAdapterListner = onReelsVideoAdapterListner;
    }

    @Override
    public void onBindViewHolder(ReelsViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holders.add(holder);
        // Ngăn không cho ViewHolder bị recycle
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    public List<Reels> getList() {
        return reels;
    }
    public ItemReelsBinding getBindingAtPosition (int pos){
        if (pos < 0 || pos >= holders.size()) {
            return null;
        }
        ReelsViewHolder holder = holders.get(pos);
        if (holder != null) {
            return holder.binding;
        }
        return null;
    }

    public void addData(List<Reels> reels) {
        this.reels.addAll(reels);
        notifyItemRangeInserted(this.reels.size(), reels.size());
    }

    public interface OnReelsVideoAdapterListner {
        void onItemClick(ItemReelsBinding reelsBinding, int pos, int type);

        void onDoubleClick(Reels model, MotionEvent event, ItemReelsBinding binding);

        void onClickLike(ItemReelsBinding reelsBinding, int pos);

        void onClickUser(Reels reel);

        void onClickComments(Reels reels);

        void onClickShare(Reels reel);

        void test();

    }

    public class ReelsViewHolder extends RecyclerView.ViewHolder {
        public ItemReelsBinding binding;

        public ReelsViewHolder(View itemView) {
            super(itemView);
            binding = ItemReelsBinding.bind(itemView);
        }

        public void getUser(String userId){
            database = FirebaseFirestore.getInstance();
            DocumentReference userRef = database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId);

            userRef.addSnapshotListener((value, error) -> {
                if (error != null) {
                    // Xử lý lỗi
                    return;
                }

                // Lấy dữ liệu từ tài liệu Firestore
                User user = new User();
                user.setUsername(value.getString("username"));
                user.setEmail(value.getString(Constants.KEY_EMAIL));
                user.setImage(value.getString(Constants.KEY_IMAGE));

                // Cập nhật giao diện người dùng với dữ liệu mới
                //Glide.with(binding.getRoot()).load(user.getImage()).into(binding.thumbnail);
                Picasso.get().load(user.getImage()).into(binding.thumbnail);
                binding.username.setText(user.getUsername());
                binding.email.setText(user.getEmail());
            });
        }

        public void setData(int position) {
            Reels reel = reels.get(position);
            getUser(reel.getReelsBy());
            binding.bio.setText(reel.getCaption());
            binding.songName.setText("Sound Name..");
            binding.likeCount.setText(String.valueOf(reel.getLikes()));
            binding.commentCount.setText(String.valueOf(reel.getComments()));

            onReelsVideoAdapterListner.test();



            binding.thumbnail.setOnClickListener(v -> onReelsVideoAdapterListner.onClickUser(reel));
            binding.comment.setOnClickListener(v -> onReelsVideoAdapterListner.onClickComments(reel));


            binding.comment.setOnClickListener(v ->
                    onReelsVideoAdapterListner.onClickComments(reel));


            binding.share.setOnClickListener(v ->
                    onReelsVideoAdapterListner.onClickShare(reel));


            binding.like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    onReelsVideoAdapterListner.onClickLike(binding, position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    onReelsVideoAdapterListner.onClickLike(binding, position);
                }
            });

            binding.playerView.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(binding.getRoot().getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        Log.d("TAGA", "onSingleTapUp: ");

                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                        Log.d("TAGA", "onShowPress: ");
                        super.onShowPress(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        Log.d("TAGA", "onSingleTapConfirmed: ");
                        onReelsVideoAdapterListner.onItemClick(binding, position, 2);
                        return super.onSingleTapConfirmed(e);
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.d("TAGA", "onDoubleTap: ");
                        onReelsVideoAdapterListner.onDoubleClick(reel, e, binding);
                        return true;
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        }


    }


}
