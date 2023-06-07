package com.tuan1611pupu.vishort.Activity;

import static android.provider.MediaStore.MediaColumns.DATA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tuan1611pupu.vishort.Model.User;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.databinding.ActivityEditBinding;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;

    static final int GALLERY_COVER_CODE = 1002;
    static final int PERMISSION_REQUEST_CODE = 101;
    static final int GALLERY_CODE = 1001;
    Uri selectedImage;
    String picturePath;
    Uri selectedCoverImage;
    User user;
    private FirebaseFirestore database;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        iniView();
    }
    private void iniView() {
        openEditSheet();

        binding.coverImag.setVisibility(View.VISIBLE);
        binding.coverLay.setOnClickListener(v -> chooseCoverPhoto());

        Glide.with(this).load(user.getImage_cover()).into(binding.coverImag);
        Glide.with(this).load(user.getImage()).into(binding.userImg);
        binding.etEmail.setText(user.getUsername());
        binding.etBio.setText(user.getBio());

        binding.close.setOnClickListener(v -> finish());
        binding.done.setOnClickListener(v -> {
            if (selectedImage != null) {
                // luu
                final StorageReference reference = storage.getReference().child("avatar").child(user.getId());
                reference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("image", uri.toString());
                                updates.put("bio",binding.etBio.getText().toString().trim());
                                updates.put("username",binding.etEmail.getText().toString().trim());
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(user.getId())
                                        .update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Xử lý khi cập nhật thành công
                                                if(selectedCoverImage == null){
                                                    finish();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Xử lý khi cập nhật thất bại
                                            }
                                        });

                            }
                        });
                    }
                });
            }
            if(selectedCoverImage != null){
                final StorageReference reference = storage.getReference().child("cover_photo").child(user.getId());
                reference.putFile(selectedCoverImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("image_cover", uri.toString());
                                updates.put("bio",binding.etBio.getText().toString().trim());
                                updates.put("username",binding.etEmail.getText().toString().trim());
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(user.getId())
                                        .update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Xử lý khi cập nhật thành công
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Xử lý khi cập nhật thất bại
                                            }
                                        });
                            }
                        });
                    }
                });
            }
            // kkk
            Map<String, Object> updates = new HashMap<>();
            updates.put("bio",binding.etBio.getText().toString().trim());
            updates.put("username",binding.etEmail.getText().toString().trim());
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(user.getId())
                    .update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Xử lý khi cập nhật thành công
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xử lý khi cập nhật thất bại
                        }
                    });

        });
    }


    private void openEditSheet() {
        binding.lytimg.setOnClickListener(v -> choosePhoto());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            Log.d("TAG", "onActivityResult: " + selectedImage);

            Glide.with(this).load(selectedImage).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(false).into(binding.userImg);

            String[] filePathColumn = {DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("TAG", "picpath:2 " + picturePath);
            Log.d("TAG", "onActivityResultpicpath: " + picturePath);
        }

        if (requestCode == GALLERY_COVER_CODE && resultCode == RESULT_OK && null != data) {
            binding.coverLay.setVisibility(View.GONE);

            selectedCoverImage = data.getData();
            Log.d("TAG", "onActivityResult: " + selectedCoverImage);
            Glide.with(this).load(selectedCoverImage).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(false).into(binding.coverImag);

            String[] filePathColumn = {DATA};

            Cursor cursor = getContentResolver().query(selectedCoverImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String pictureCoverPath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("TAG", "picpath:2 " + pictureCoverPath);
            Log.d("TAG", "onActivityResultpicpath: " + pictureCoverPath);
        }

    }


    private void chooseCoverPhoto() {
        if (checkPermission()) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_COVER_CODE);
        } else {
            requestPermission();
        }
    }

    private void choosePhoto() {

        if (checkPermission()) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_CODE);
        } else {
            requestPermission();
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


}