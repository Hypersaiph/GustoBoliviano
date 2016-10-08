package com.fuzzyapps.gustoboliviano;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private Intent intent = null;
    private static final int RC_SIGN_IN = 3;
    //UI Variables
    private TextView textView,fireBaseTextID;
    private LoginButton loginButton;
    private Button firebaseButton;
    private SignInButton mGoogleBtn;
    //Google Variables
    private GoogleApiClient mGoogleApiClient;
    //Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // Facebook Variables
    private CallbackManager mCallbackManager;
    private AccessTokenTracker tracker;
    private ProfileTracker profileTracker;
    private String TAG = "@MyGvs";
    private int c;
    private String userName="";
    private String userID="";
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "facebook:onSuccess:" + loginResult);
            handleFacebookAccessToken(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "facebook:onCancel");

        }

        @Override
        public void onError(FacebookException error) {
            Log.d(TAG, "facebook:onError", error);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Iniciando Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.activity_main);
        //Iniciando Firebase
        mAuth = FirebaseAuth.getInstance();
        //UI Elements
        textView = (TextView) findViewById(R.id.textView);
        fireBaseTextID = (TextView) findViewById(R.id.fireBaseTextID);
        loginButton = (LoginButton) findViewById(R.id.facebook_button);
        firebaseButton = (Button) findViewById(R.id.firebaseButton);
        mGoogleBtn = (SignInButton) findViewById(R.id.googleButton);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        message("hay un error");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signIn();
            }
        });
        setGooglePlusButtonText(mGoogleBtn,R.string.googleSignInText);
        //Facebook
        mCallbackManager = CallbackManager.Factory.create();
        //Permisos de Facebook para obtener Email y Nombre de la Biografia
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, mCallBack);
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                //Aun no manejamos el cambio de tokens
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                almacenarDatosMomentaneamente(newProfile);
            }
        };
        tracker.startTracking();
        profileTracker.startTracking();
        //Firebase
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    fireBaseTextID.setText(user.getUid());
                    message("ID:"+ user.getUid());
                    userID = user.getUid();
                    redirectNextActivity();
                }else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    fireBaseTextID.setText("");
                }
            }
        };
        //UI Elements CODE
        firebaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    private void redirectNextActivity() {
        if (intent == null) {
            intent = new Intent(LoginActivity.this, MainNavigationActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("userID", userID);
            startActivity(intent);
            finish();
        }
    }

    //Change Google text
    protected void setGooglePlusButtonText(SignInButton signInButton, int buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setPadding(0, 0, 0, 0);
                return;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                //setText Google
                //account.getEmail()
                textView.setText("Welcome "+account.getGivenName()+ " "+ account.getFamilyName());
                userName = account.getGivenName()+ " "+ account.getFamilyName();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }else{
            mCallbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            message("Authentication failed.");
                        }
                    }
                });

    }

    private void almacenarDatosMomentaneamente(Profile profile){
        if(profile!= null){
            textView.setText("Welcome "+profile.getName());
            userName = profile.getName();
        }else{

        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        /*
        Después de que un usuario inicia sesión con éxito,
        en el método callback de LoginButton onSuccess,
        obtén un token de acceso para el usuario que inicia sesión,
        intercámbialo por una credencial de Firebase y autentica con Firebase mediante el uso de la credencial de Firebase
         */
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    message("Authentication failed.");
                }
            }
        });
    }
    private void message(String message) {
        c++;Toast.makeText(this,c+""+message,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        //Facebook
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Firebase
        Profile profile = Profile.getCurrentProfile();
        almacenarDatosMomentaneamente(profile);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Facebook
        tracker.stopTracking();
        profileTracker.stopTracking();
        //Firebase
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
