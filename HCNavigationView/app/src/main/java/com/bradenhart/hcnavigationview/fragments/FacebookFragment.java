package com.bradenhart.hcnavigationview.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcnavigationview.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.bradenhart.hcnavigationview.R;

import de.hdodenhof.circleimageview.CircleImageView;

import com.bradenhart.hcnavigationview.Constants.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String userId;

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
        loginBtn.setText("Hello");
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
                userId = profile.getId();
                if (!token.getDeclinedPermissions().contains("user_photos")) {
                    Log.e(LOGTAG, "accepted photos permission");
                    graphCall();
                }
            } else {
                Log.e(LOGTAG, "profile is null");
                Toast.makeText(getActivity(), "Something went wrong, try logging in again.", Toast.LENGTH_SHORT).show();
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

    public void graphCall() {
        /* make the parameters for the HTTP request*/
        Bundle params = new Bundle();
        params.putBoolean("redirect", false); // will prevent request redirecting, forces return of url
        params.putString("fields", "url");
        params.putString("type", "large"); // small, normal, large, square
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+ userId + "/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Log.e(LOGTAG, response.toString());
                        Log.e(LOGTAG, "completed graph call");
                        JSONObject jsonDataObject = response.getJSONObject();
                        if (jsonDataObject != null) {
                            Log.e(LOGTAG, "json object is NOT null");
                            JSONObject dataObject = jsonDataObject.optJSONObject("data");
                            Log.e("LOGTAG", "data object: " + jsonDataObject.toString());
                            if (dataObject.optJSONObject("url")!= null) Log.e(LOGTAG, "url is mapped to a jsonobject");
                            final String urlString = dataObject.optString("url");

                            new DownloadFacebookImageTask().execute(urlString);


                        } else {
                            Log.e(LOGTAG, "json object is null");
                        }
                    }
                }
        ).executeAsync();
    }


    private class DownloadFacebookImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL imageUrl = null;
            Bitmap bitmap = null;
            try {
                imageUrl = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                fbProfilePic.setImageBitmap(bitmap);
            } else {
                fbProfilePic.setImageResource(R.drawable.ic_person_white_48dp);
            }
        }
    }
}
