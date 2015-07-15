package com.bradenhart.hcnavigationview.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bradenhart.hcnavigationview.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.bradenhart.hcnavigationview.R;

import de.hdodenhof.circleimageview.CircleImageView;

import com.bradenhart.hcnavigationview.Constants.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bradenhart on 15/07/15.
 */
public class FacebookFragment extends Fragment {

    private final String LOGTAG = "Facebook Fragment";
    private TextView fbUsername;
    private CircleImageView fbProfilePic;
    private LoginButton loginBtn;
    private CallbackManager callbackManager;

    public FacebookFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);

        fbUsername = (TextView) view.findViewById(R.id.fb_username);
        fbProfilePic = (CircleImageView) view.findViewById(R.id.fb_profile_picture);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginBtn = (LoginButton) view.findViewById(R.id.fb_login_button);
        loginBtn.setReadPermissions("user_photos");
        loginBtn.setFragment(this);
        loginBtn.registerCallback(callbackManager, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            final AccessToken token = loginResult.getAccessToken();
            final Profile profile = Profile.getCurrentProfile();
            Log.e(LOGTAG, "success");
            if (profile != null) {
                fbUsername.setText(profile.getFirstName());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!token.getDeclinedPermissions().contains("user_photos")) {
                            URL imageUrl = null;
                            try {
                                imageUrl = new URL("https://graph.facebook.com/" + profile.getId() +"/picture");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            try {
                                Bitmap image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } else {
                Log.e(LOGTAG, "profile is null");
            }


        }

        @Override
        public void onCancel() {
            Log.e(LOGTAG, "facebook login cancelled");
        }

        @Override
        public void onError(FacebookException e) {
            Log.e(LOGTAG, "error occurred during login");
            e.printStackTrace();
        }
    };
}
