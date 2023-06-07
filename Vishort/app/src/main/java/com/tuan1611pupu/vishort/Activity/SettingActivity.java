package com.tuan1611pupu.vishort.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.Utilities.PreferenceManager;
import com.tuan1611pupu.vishort.databinding.ActivitySettingBinding;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        logout();

        binding.back.setOnClickListener(v->{
            onBackPressed();
            finish();
        });
    }


    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        binding.logout.setOnClickListener(v -> {
            showMessage("Signing out ....");

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference documentReference =
                    database.collection(Constants.KEY_COLLECTION_USERS).document(
                            preferenceManager.getString(Constants.KEY_USER_ID)
                    );
            HashMap<String,Object> update = new HashMap<>();
            update.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
            documentReference.update(update)
                    .addOnSuccessListener(unused -> {
                        FirebaseAuth.getInstance().signOut();
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();
                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Xử lý sau khi đăng xuất khỏi Google thành công
                            }
                        });

                        LoginManager.getInstance().logOut();
                        preferenceManager.clear();
                        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "loi dang xuat", Toast.LENGTH_SHORT).show());


        });
    }



}