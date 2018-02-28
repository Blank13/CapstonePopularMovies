package com.mes.udacity.capstonepopularmovies.loginactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mes.udacity.capstonepopularmovies.R;
import com.mes.udacity.capstonepopularmovies.moviepostersactivity.MoviePostersActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.InputStream;

/**
 * Created by moham on 2/27/2018.
 */

public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 0;

    private SignInButton signInButton;
    private Button signOutButton;
    private Button enterAppButton;
    private ProgressDialog mProgressDialog;
    private TextView loginTextView;
    private CircularImageView userImageView;
    private GoogleApiClient mGoogleApiClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        signInButton = view.findViewById(R.id.login_google_button);
        signOutButton = view.findViewById(R.id.login_signout_button);
        enterAppButton = view.findViewById(R.id.login_enter_app_button);
        userImageView = view.findViewById(R.id.login_user_image);
        loginTextView = view.findViewById(R.id.login_user_name);
        userImageView.setImageResource(R.drawable.no_user);
        SignInOnClickListener signInOnClickListener = new SignInOnClickListener();
        signInButton.setOnClickListener(signInOnClickListener);
        SignOutOnClickListener signOutOnClickListener = new SignOutOnClickListener();
        signOutButton.setOnClickListener(signOutOnClickListener);
        EnterAppOnClickListener enterAppOnClickListener = new EnterAppOnClickListener();
        enterAppButton.setOnClickListener(enterAppOnClickListener);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String text = getString(R.string.user_welcome) + " " + account.getDisplayName();
            loginTextView.setText(text);
            if(account.getPhotoUrl() != null){
                new FetchUserProfilePicture(userImageView).execute(account.getPhotoUrl().toString());
            }
            updateUIViews(true);
        } else {
            updateUIViews(false);
        }
    }

    private void updateUIViews(boolean signedIn) {
        if (signedIn) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            enterAppButton.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            enterAppButton.setVisibility(View.GONE);
            loginTextView.setText(getString(R.string.welcome));
            userImageView.setImageResource(R.drawable.no_user);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(),getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private class SignInOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private class SignOutOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            updateUIViews(false);
                        }
                    });
        }
    }

    private class EnterAppOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), MoviePostersActivity.class);
            startActivity(intent);
        }
    }

    private class FetchUserProfilePicture extends AsyncTask<String,Void,Bitmap> {

        private CircularImageView circularImageView;

        public FetchUserProfilePicture(CircularImageView circularImageView) {
            this.circularImageView = circularImageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                circularImageView.setImageBitmap(bitmap);
            }
        }
    }
}
