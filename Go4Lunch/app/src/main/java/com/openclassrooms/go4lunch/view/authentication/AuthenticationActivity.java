package com.openclassrooms.go4lunch.view.authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.view.BaseActivity;
import com.openclassrooms.go4lunch.view.splash.SplashActivity;
import com.openclassrooms.go4lunch.viewmodel.authentication.AuthenticationViewModel;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.openclassrooms.go4lunch.utilities.Constants.AUTHENTICATION_REQUEST_CODE;
import static com.openclassrooms.go4lunch.utilities.Constants.CURRENT_USER_AUTHENTICATED;
import static com.openclassrooms.go4lunch.utilities.Constants.GOOGLE_SIGN_IN;
import static com.openclassrooms.go4lunch.utilities.Constants.IS_AUTHENTICATION_ACTIVITY_LAUNCH_FROM_MAIN_ACTIVITY;
import static com.openclassrooms.go4lunch.utilities.HelperClass.logErrorMessage;

/**
 * Authentication Activity class implementation
 */
public class AuthenticationActivity extends BaseActivity {
    private AuthenticationViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;

    @BindView(R.id.google_sign_in_button)
    SignInButton googleSignInButton;

    @BindView(R.id.facebook_sign_in_button)
    LoginButton facebookSignInButton;

    @BindView(R.id.twitter_sign_in_button)
    TwitterLoginButton twitterLoginButton;

    @BindView(R.id.email_password_sign_in_button)
    Button emailPasswordButton;

    @BindView(R.id.email_text_input_layout)
    TextInputLayout emailTextInputLayout;

    @BindView(R.id.password_text_input_layout)
    TextInputLayout passwordTextInputLayout;

    @BindView(R.id.email_password_buttons)
    LinearLayout emailPasswordButtonLayout;

    @BindView(R.id.email_sign_in_button)
    Button emailPasswordSignInButton;

    @BindView(R.id.email_create_account_button)
    Button emailPasswordCreateAccountButton;

    private CallbackManager mCallbackManager;

    private boolean isActivityLaunchedFromMainActivity = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTwitterSignIn();
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        initAuthViewModel();
        initFacebookSignIn();
        initSignInButton();
        initGoogleSignInClient();
        initTwitterSignInButton();
        initEmailPasswordButton();

        if(getIntent().getExtras() != null) {
            isActivityLaunchedFromMainActivity = getIntent().getBooleanExtra(IS_AUTHENTICATION_ACTIVITY_LAUNCH_FROM_MAIN_ACTIVITY,
                    false);
        }

        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void initSignInButton() {
        googleSignInButton.setOnClickListener(v -> googleSignIn());
    }

    private void initAuthViewModel() {
        authViewModel = (AuthenticationViewModel) obtainViewModel(AuthenticationViewModel.class);
        authViewModel.userAuthenticated().observe(this, user -> {
            if (user != null) {
                launchSplashActivity(user);
            }
        });

        authViewModel.userAuthenticatedErrorMessage().observe(this, errorMessage -> {
            if(errorMessage != null) {
                Toast.makeText(AuthenticationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Facebook Sign in configuration
    private void initFacebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create();

        facebookSignInButton.setReadPermissions("email", "public_profile");
        facebookSignInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential facebookCredential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                signInWithAuthCredential(facebookCredential);
            }

            @Override
            public void onCancel() {
                Timber.d("facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Timber.d(error,"facebook:onError");
            }
        });
    }

    // Google Sign in configuration
    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void googleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                logErrorMessage(e.getMessage());
            }
        }
        if(requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE){
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithAuthCredential(googleAuthCredential);
    }

    private void signInWithAuthCredential(AuthCredential authCredential) {
        authViewModel.signInWithCredential(authCredential);
    }

    private void launchSplashActivity(User user) {
        AppPreferences.preferences(getApplicationContext()).setPrefKeyCurrentUserUuid(user.getUid());

        Intent intent = new Intent(AuthenticationActivity.this, SplashActivity.class);
        intent.putExtra(CURRENT_USER_AUTHENTICATED, user);

        if (isRunningInstrumentedTest()) {
            startActivity(intent);
        } else if (!isActivityLaunchedFromMainActivity) {
            setResult(AUTHENTICATION_REQUEST_CODE, intent);
        } else {
            startActivity(intent);
        }
        finish();
    }

    // Twitter Sign in configuration
    private void initTwitterSignIn() {
        //This code must be entering before the setContentView to make the twitter login work...
        TwitterAuthConfig mTwitterAuthConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(mTwitterAuthConfig)
                .build();
        Twitter.initialize(twitterConfig);
    }

    private void initTwitterSignInButton() {
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                AuthCredential twitterAuthCredential = TwitterAuthProvider.getCredential(session.getAuthToken().token,
                        session.getAuthToken().secret);
                signInWithAuthCredential(twitterAuthCredential);
            }

            @Override
            public void failure(TwitterException exception) {
                Timber.d(exception,"facebook:onError");
            }
        });
    }

    // Email / Password Sign in configuration
    private void initEmailPasswordButton() {
        emailPasswordButton.setTransformationMethod(null);
        emailPasswordSignInButton.setTransformationMethod(null);
        emailPasswordCreateAccountButton.setTransformationMethod(null);
        emailPasswordButton.setOnClickListener(view -> showEmailPasswordView());

        emailTextInputLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passwordTextInputLayout.getEditText().requestFocus();
            }
            return false;
        });

        emailPasswordSignInButton.setOnClickListener(view -> {
            if (!validateForm()) {
                return;
            }
            authViewModel.signInWithEmailAndPassword(
                    Objects.requireNonNull(emailTextInputLayout.getEditText()).getText().toString(),
                    Objects.requireNonNull(passwordTextInputLayout.getEditText()).getText().toString());
        });

        emailPasswordCreateAccountButton.setOnClickListener(view -> {
            if(!validateForm()) {
                return;
            }
            authViewModel.createUserWithEmailAndPassword(
                    Objects.requireNonNull(emailTextInputLayout.getEditText()).getText().toString(),
                    Objects.requireNonNull(passwordTextInputLayout.getEditText()).getText().toString());
        });
    }

    private void showEmailPasswordView() {
        facebookSignInButton.setVisibility(View.INVISIBLE);
        googleSignInButton.setVisibility(View.INVISIBLE);
        twitterLoginButton.setVisibility(View.INVISIBLE);
        emailPasswordButton.setVisibility(View.INVISIBLE);
        emailTextInputLayout.setVisibility(View.VISIBLE);
        passwordTextInputLayout.setVisibility(View.VISIBLE);
        emailPasswordButtonLayout.setVisibility(View.VISIBLE);
    }

    private void hideEmailPasswordView() {
        facebookSignInButton.setVisibility(View.VISIBLE);
        googleSignInButton.setVisibility(View.VISIBLE);
        twitterLoginButton.setVisibility(View.VISIBLE);
        emailPasswordButton.setVisibility(View.VISIBLE);
        emailTextInputLayout.setVisibility(View.GONE);
        passwordTextInputLayout.setVisibility(View.GONE);
        emailPasswordButtonLayout.setVisibility(View.GONE);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailTextInputLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailTextInputLayout.getEditText().setError("Required.");
            valid = false;
        } else {
            emailTextInputLayout.getEditText().setError(null);
        }

        String password = passwordTextInputLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordTextInputLayout.getEditText().setError("Required.");
            valid = false;
        } else {
            passwordTextInputLayout.getEditText().setError(null);
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        if(emailTextInputLayout.getVisibility() == View.VISIBLE) {
            hideEmailPasswordView();
        } else {
            super.onBackPressed();
        }
    }
}
