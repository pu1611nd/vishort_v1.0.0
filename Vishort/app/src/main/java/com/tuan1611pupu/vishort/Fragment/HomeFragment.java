package com.tuan1611pupu.vishort.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tuan1611pupu.vishort.Adapter.ReelsAdapter;
import com.tuan1611pupu.vishort.Model.Reels;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.Utilities.PreferenceManager;
import com.tuan1611pupu.vishort.databinding.FragmentHomeBinding;
import com.tuan1611pupu.vishort.databinding.ItemReelsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements ReelsAdapter.OnReelsVideoAdapterListner {


    private FragmentHomeBinding binding;
    Animation animation;
    private SimpleExoPlayer player;
    private ItemReelsBinding playerBinding;
    Animation rotateAnimation;
    Boolean isLike = false;
    private int like = 0;
    private ReelsAdapter adapter;
    private ArrayList<Reels> reelsList;
    private int visibale = 0;
    Fragment_comment bottomSheetDialogFragment;
    private Reels reelUser;

    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Lấy đối tượng Reel từ Bundle
            reelUser = (Reels) bundle.getSerializable("reel");
            // Sử dụng đối tượng Reel ở đây
        }
        initView();
        return binding.getRoot();
    }

    private void initView() {
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        rotateAnimation = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.slow_rotate);

        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getContext());

        bottomSheetDialogFragment = new Fragment_comment();

        // Khởi tạo RecyclerView và adapter
        binding.rvReels.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // Khởi tạo ExoPlayer
        new PagerSnapHelper().attachToRecyclerView(binding.rvReels);
        player = new SimpleExoPlayer.Builder(getContext()).build();
        reelsList = new ArrayList<>();
        adapter = new ReelsAdapter();
        adapter.setOnReelsVideoAdapterListner(this);
        if(reelUser != null){
            reelsList.add(reelUser);
            adapter.addData(reelsList);
            binding.rvReels.setAdapter(adapter);
        }else {
            loadMoreDataToAdapter();
        }
        binding.rvReels.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        // Resume phát video khi RecyclerView đang ở trạng thái tĩnh (không cuộn)
                        player.setPlayWhenReady(true);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        // Pause phát video khi RecyclerView đang ở trạng thái cuộn
                        player.setPlayWhenReady(false);
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Tìm ViewHolder đang hiển thị ở giữa màn hình
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                visibale = firstVisiblePosition;
                checkLikeAndCommentCount(visibale);

                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    ReelsAdapter.ReelsViewHolder holder = ( ReelsAdapter.ReelsViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    playerBinding = holder.binding;
                    playerBinding.playerView.setPlayer(player);
                    Animation animation = AnimationUtils.loadAnimation(playerBinding.getRoot().getContext(), R.anim.slow_rotate);
                    playerBinding.lytSound.startAnimation(animation);
                    playerBinding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    Reels currentReel = adapter.getList().get(i);
                    Uri videoUri = Uri.parse(currentReel.getVideo());
                    player.clearMediaItems();
                    player.setMediaItem(MediaItem.fromUri(videoUri));
                    player.prepare();

                }

            }


        });

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if(playbackState == Player.STATE_ENDED) {
                    player.seekToDefaultPosition(); // quay về vị trí đầu tiên của video
                    player.setPlayWhenReady(true); // phát lại video
                }
            }
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                if (isLoading) {
                    // Player đang tải nội dung
                    binding.buffering.setVisibility(View.VISIBLE);
                    player.setPlayWhenReady(false);
                } else {
                    // Player đã tải xong nội dung
                    binding.buffering.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                }
            }
        });

    }



    private void loadMoreDataToAdapter() {
        database.collection(Constants.KEY_COLLECTION_REELS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        reelsList.clear();
                        // Lấy các dòng dữ liệu mới nhất và thêm vào listPos
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reels reels = document.toObject(Reels.class);
                            reels.setReelsId(document.getString("reelsId"));
                            reelsList.add(reels);
                        }
                        adapter.addData(reelsList);
                        binding.rvReels.setAdapter(adapter);
                    } else {
                        Log.d("hhh","Error getting documents: ", task.getException());
                    }
                });
    }


    private void loadVideo(int pos){
        playerBinding = adapter.getBindingAtPosition(pos);
        playerBinding.playerView.setPlayer(player);
        Animation animation = AnimationUtils.loadAnimation(playerBinding.getRoot().getContext(), R.anim.slow_rotate);
        playerBinding.lytSound.startAnimation(animation);
        playerBinding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        Reels currentReel = adapter.getList().get(pos);
        Uri videoUri = Uri.parse(currentReel.getVideo());
        player.clearMediaItems();
        player.setMediaItem(MediaItem.fromUri(videoUri));
        player.prepare();
        player.setPlayWhenReady(true);

    }

    @Override
    public void onResume() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
    }


    // Hàm callback khi người dùng bấm vào item video
    @Override
    public void onItemClick(ItemReelsBinding reelsBinding, int pos, int type) {

        if (player.isPlaying()) {
            // Nếu ExoPlayer đang phát, tạm dừng và lưu lại vị trí hiện tại
            player.setPlayWhenReady(false);
            binding.playing.setVisibility(View.VISIBLE);
        } else {
            // Nếu ExoPlayer đang tạm dừng, tiếp tục phát từ vị trí hiện tại
            binding.playing.setVisibility(View.GONE);
            player.setPlayWhenReady(true);
        }

    }


    // Hàm callback khi video được chạm đôi
    @Override
    public void onDoubleClick(Reels model, MotionEvent event, ItemReelsBinding binding) {
        // Xử lý sự kiện khi người dùng chạm đôi vào video
    }

    // Hàm callback khi người dùng bấm nút like
    @Override
    public void onClickLike(ItemReelsBinding reelsBinding, int pos) {
        // Xử lý sự kiện khi người dùng bấm vào nút like video
        playerBinding = reelsBinding;
        Reels currentReel = adapter.getList().get(pos);
        Map<String, Object> updates = new HashMap<>();
       // like = currentReel.getLikes();
        if(isLike && like >0) {
            updates.clear();
            updates.put(preferenceManager.getString(Constants.KEY_USER_ID),false);
            database.collection(Constants.KEY_COLLECTION_REELS)
                    .document(currentReel.getReelsId())
                    .update("like",updates)
                    .addOnSuccessListener(aVoid -> {
                        // Xử lý khi cập nhật thành công
                        like --;
                        updates.clear();
                        updates.put("likes", like);
                        Log.d("like",like+"");
                        database.collection(Constants.KEY_COLLECTION_REELS)
                                .document(currentReel.getReelsId())
                                .update(updates)
                                .addOnSuccessListener(aVoid1 -> {
                                    // Xử lý khi cập nhật thành công
                                    playerBinding.likeCount.setText(like+"");
                                })
                                .addOnFailureListener(e -> {
                                    // Xử lý khi cập nhật thất bại
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi cập nhật thất bại
                    });
        }else {
            updates.clear();
            updates.put(preferenceManager.getString(Constants.KEY_USER_ID),true);
            database.collection(Constants.KEY_COLLECTION_REELS)
                    .document(currentReel.getReelsId())
                    .update("like",updates)
                    .addOnSuccessListener(aVoid -> {
                        updates.clear();
                        like ++;
                        // Xử lý khi cập nhật thành công
                        updates.put("likes",like);
                        database.collection(Constants.KEY_COLLECTION_REELS)
                                .document(currentReel.getReelsId())
                                .update(updates)
                                .addOnSuccessListener(aVoid1 -> {
                                    // Xử lý khi cập nhật thành công
                                    playerBinding.likeCount.setText(like+"");
                                })
                                .addOnFailureListener(e -> {
                                    // Xử lý khi cập nhật thất bại
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi cập nhật thất bại
                    });
        }


    }

    // Hàm callback khi người dùng bấm vào tên người đăng video
    @Override
    public void onClickUser(Reels reel) {
        // Xử lý sự kiện khi người dùng bấm vào tên người đăng
        String userId = reel.getReelsBy();
        if (!userId.equals(preferenceManager.getString(Constants.KEY_USER_ID))) {
            player.setPlayWhenReady(false);
            UserProfileFragment fragment = new UserProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("userId",userId);
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

    // Hàm callback khi người dùng bấm vào nút comment
    @Override
    public void onClickComments(Reels reels) {
        // Xử lý sự kiện khi người dùng bấm vào nút comment
        Bundle bundle = new Bundle();
        bundle.putString("reelId", reels.getReelsId());
        bundle.putString("reelBy", reels.getReelsBy());// Chèn giá trị vào Bundle
        bottomSheetDialogFragment.setArguments(bundle);
        bottomSheetDialogFragment.show(getChildFragmentManager(),bottomSheetDialogFragment.getTag());
    }

    // Hàm callback khi người dùng bấm vào nút share
    @Override
    public void onClickShare(Reels reel) {

    }

    @Override
    public void test() {
        if(visibale == 0){
            loadVideo(visibale);
            checkLikeAndCommentCount(visibale);
        }
    }
    private void checkLikeAndCommentCount(int pos) {
        playerBinding = adapter.getBindingAtPosition(pos);
        Reels currentReel = adapter.getList().get(pos);
        DocumentReference reelsRef = database.collection(Constants.KEY_COLLECTION_REELS)
                .document(currentReel.getReelsId());
        reelsRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                // Xử lý lỗi
                return;
            }
            if (snapshot != null && snapshot.exists() && snapshot.getData().get("like") != null) {
                Reels reels = snapshot.toObject(Reels.class);
                 like = reels.getLikes();
                int commentCount = reels.getComments();
                playerBinding.likeCount.setText(String.valueOf(like));
                playerBinding.commentCount.setText(String.valueOf(commentCount));

                Map<String, Object> likes = (Map<String, Object>) snapshot.getData().get("like");
                if (likes != null && likes.get(preferenceManager.getString(Constants.KEY_USER_ID)) != null &&
                        (Boolean) likes.get(preferenceManager.getString(Constants.KEY_USER_ID))) {
                    // Người dùng đã thích bài viết này
                    isLike = true;
                    playerBinding.like.setLiked(true);

                } else {
                    // Người dùng chưa thích bài viết này
                    isLike = false;
                    playerBinding.like.setLiked(false);
                }
            } else {
                // Xử lý trường hợp không tìm thấy tài liệu được yêu cầu
                isLike = false;
                playerBinding.like.setLiked(false);
            }
        });
    }





}
