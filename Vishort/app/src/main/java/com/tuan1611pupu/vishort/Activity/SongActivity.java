package com.tuan1611pupu.vishort.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tuan1611pupu.vishort.Adapter.SongsAdapter;
import com.tuan1611pupu.vishort.Model.Democontents;
import com.tuan1611pupu.vishort.Model.Song;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.databinding.ActivitySongBinding;
import com.tuan1611pupu.vishort.workers.FileDownloadWorker;

import java.io.File;

import nl.changer.audiowife.AudioWife;

public class SongActivity extends AppCompatActivity {
    private static final String TAG = "SongActivity";
    private ActivitySongBinding binding;
    private SongsAdapter songsAdapter ;
    private ProgressBar progressBar;
    private BottomSheetBehavior<View> bsb;

    private ImageView play,pause,use;
    private TextView start, end,songName;
    private SeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songsAdapter = new SongsAdapter();

        songsAdapter.setOnSongClickListner(song -> {
            binding.progressbar.setVisibility(View.VISIBLE);
            downloadSelectedSong(song);
            binding.browse.setVisibility(View.GONE);
        });

        binding.rvSongs.setAdapter(songsAdapter);
        initView();

        songsAdapter.addData(Democontents.getSongFiles());


    }

    private void initView() {
        binding.browse.setVisibility(View.VISIBLE);
        View sheet = findViewById(R.id.song_preview_sheet);
        bsb = BottomSheetBehavior.from(sheet);
        bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View sheet, int state) {
                Log.v(TAG, "Song preview sheet state is: " + state);
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    AudioWife.getInstance().release();
                }
            }

            @Override
            public void onSlide(@NonNull View sheet, float offset) {

            }
        });
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        seekbar = findViewById(R.id.seekbar);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        songName = findViewById(R.id.song);
        use = findViewById(R.id.use);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioWife.getInstance().release();

    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioWife.getInstance().pause();
    }


    public void downloadSelectedSong(Song song) {
        File songs = new File(getFilesDir(), "songs");
        if (!songs.exists() && !songs.mkdirs()) {
            Log.w(TAG, "Could not create directory at " + songs);
        }
        binding.progressbar.setVisibility(View.GONE);

        KProgressHUD progress = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait.....")
                .setCancellable(false)
                .show();

        String inputUrl = song.getAudio(); // lấy đường dẫn để tải xuống bài hát
        String outputDir = songs.getAbsolutePath() + File.separator + song.getTitle()+ ".mp3"; // tạo thư mục lưu trữ cho bài hát được chọn
        Data inputData = new Data.Builder()
                .putString(FileDownloadWorker.KEY_INPUT, inputUrl)
                .putString(FileDownloadWorker.KEY_OUTPUT, outputDir)
                .build(); // truyền đường dẫn để tải xuống và lưu trữ vào Data object

        WorkRequest request = new OneTimeWorkRequest.Builder(FileDownloadWorker.class)
                .setInputData(inputData)
                .build();
        WorkManager wm = WorkManager.getInstance(this);
        wm.enqueue(request);
        wm.getWorkInfoByIdLiveData(request.getId())
                .observe(this, info -> {
                    Log.d(TAG, "downloadSelectedSong: " + info);
                    boolean ended = info.getState() == WorkInfo.State.CANCELLED
                            || info.getState() == WorkInfo.State.FAILED
                            || info.getState() == WorkInfo.State.SUCCEEDED;
                    if (ended) {
                        progress.dismiss();
                    }

                    if (info.getState() == WorkInfo.State.SUCCEEDED) {
                        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
                        songName.setText(song.getTitle());
                        Log.d(TAG, "downloadSelectedSong376772: " + outputDir);
                        Uri songUri = Uri.parse(outputDir);
                        AudioWife.getInstance()
                                .init(this, songUri)
                                .setPlayView(play)
                                .setPauseView(pause)
                                .setSeekBar(seekbar)
                                .setRuntimeView(start)
                                .setTotalTimeView(end);
                        use.setOnClickListener(v->{
                            Intent intent = new Intent(getApplicationContext(),RecorderActivity.class);
                            intent.putExtra("linkAudio",outputDir);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
                    }
                });
    }



}