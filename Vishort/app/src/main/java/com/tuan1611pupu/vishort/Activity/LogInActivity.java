package com.tuan1611pupu.vishort.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuan1611pupu.vishort.Model.User;
import com.tuan1611pupu.vishort.R;
import com.tuan1611pupu.vishort.Utilities.Constants;
import com.tuan1611pupu.vishort.Utilities.PreferenceManager;
import com.tuan1611pupu.vishort.databinding.ActivityLogInBinding;

public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding binding;

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private GoogleSignInClient googleSignInClient;

    private PreferenceManager preferenceManager;

    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        
        binding.buttonPhone.setOnClickListener(v->{
            Intent intent= new Intent(getApplicationContext(),LoginWithPhoneActivity.class);
            startActivity(intent);
        });

        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        binding.buttonTwitter.setOnClickListener(v -> {
            Toast.makeText(this, "Chúng tôi đang trong quá trình phát triển, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
        });
        LogInGoogle();
        LogInFacebook();

    }
    private void LogInGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.buttonGoogle.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });
    }

    private void LogInFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        binding.buttonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NonNull FacebookException e) {

            }
        });
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //signUp(user);

            } else {
                // signUp(null);
            }
        };


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(@Nullable AccessToken accessToken, @Nullable AccessToken accessToken1) {
                if (accessToken1 == null) {
                    firebaseAuth.signOut();
                }
            }
        };

    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    upDateUI(user);
                } else {

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseGoogleAuth(account);
        } catch (ApiException e) {
            showMessage("Google Sign In failed. Please try again later.");
        }
    }

    private void firebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            upDateUI(user);
                        } else {
                            showMessage("Google Sign In failed. Please try again later.");
                        }
                    }
                });
    }

    private void upDateUI(FirebaseUser user) {
        if (user != null) {
            String userId = user.getUid();

            // Check if user signed in with email/password
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                preferenceManager.putString(Constants.KEY_USER_ID, user.getUid());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                        }
                    } else {
                        showMessage("Failed to login. Please try again later.");
                        // Check if user signed in with Google
                        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        if (googleAccount != null) {
                            singInGoogle(user, googleAccount);
                        }

                        // Check if user signed in with Facebook
                        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
                        if (facebookAccessToken != null) {
                            SingIn(user, Constants.PROVIDER_FACEBOOK);
                        }
                    }
                }
            });
        }
    }

    private void SingIn(FirebaseUser user, String provider) {
        String userId = user.getUid();
        String fullName = user.getDisplayName();
        String email = user.getEmail();
        String phone = user.getPhoneNumber();
        User user1 = new User();
        user1.setUsername(fullName);
        if(email!=null){
            user1.setEmail(email);
        }else {
            user1.setEmail(phone);
        }
        user1.setProvider(provider);
        user1.setId(userId);
        user1.setImage(user.getPhotoUrl()+"?type=large");
        user1.setImage_cover(Constants.IMAGE_COVER);

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                .document(userId).set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, userId);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            showMessage("Failed to login. Please try again later.");
                        }
                    }
                });

    }

    private void singInGoogle(FirebaseUser user, GoogleSignInAccount account) {
        String userId = user.getUid();
        String fullname = account.getDisplayName();
        String email = account.getEmail();

        User user1 = new User();
        user1.setUsername(fullname);
        user1.setEmail(email);
        user1.setProvider(Constants.PROVIDER_GOOGLE);
        user1.setId(userId);
        user1.setImage(account.getPhotoUrl().toString());
        user1.setImage_cover(Constants.IMAGE_COVER);

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                .document(userId).set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, userId);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            showMessage("Failed to login. Please try again later.");
                        }
                    }
                });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (googleAccount != null) {
            firebaseGoogleAuth(googleAccount);
        }

        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
        if (facebookAccessToken != null) {
            handleFacebookToken(facebookAccessToken);
        }

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        accessTokenTracker.stopTracking();
    }



}