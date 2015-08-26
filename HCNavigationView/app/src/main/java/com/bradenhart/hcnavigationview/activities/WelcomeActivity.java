package com.bradenhart.hcnavigationview.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.mrengineer13.snackbar.SnackBar;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 30/06/15.
 */
public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LOGTAG = "Welcome Activity";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;

    private DrawerLayout rootLayout;
    private int accentColor, whiteColor;
    private TextView loadingPrompt;

    /*From fragment code*/
    private EditText nameInput;
    private Button doneBtn;
    private ImageView cameraBtn, galleryBtn;
    private String userName;
    private final int CAMERA = 1101, GALLERY = 1011;
    private CircleImageView picImageView;
    private Bitmap image = null, rotateImage = null;
    private Uri mImageUri;
    private DatabaseHandler dbHandler;

    private CallbackManager callbackManager;
    private LoginButton loginBtn;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.fragment_welcome);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        spEdit = sharedPreferences.edit();

        dbHandler = DatabaseHandler.getInstance(this);

        loadingPrompt = (TextView) findViewById(R.id.loading_prompt);

        accentColor = getResources().getColor(R.color.accent);

        rootLayout = (DrawerLayout) findViewById(R.id.welcome_screen_root);
        rootLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));

        nameInput = (EditText) findViewById(R.id.input_name2);
        nameInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        nameInput.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_USER_NAME)) {
                userName = savedInstanceState.getString(KEY_USER_NAME);
                nameInput.setText(userName);
            }
        }

        nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                userName = nameInput.getText().toString();
                spEdit.putString(KEY_USER_NAME, userName);
            }
        });

        picImageView = (CircleImageView) findViewById(R.id.profile_preview_image_2);

        cameraBtn = (ImageView) findViewById(R.id.picture_from_camera_2);
        galleryBtn = (ImageView) findViewById(R.id.picture_from_gallery_2);
        doneBtn = (Button) findViewById(R.id.setup_done);
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);

        loginBtn = (LoginButton) findViewById(R.id.fb_login_button_2);
        loginBtn.setReadPermissions("user_photos");
        loginBtn.registerCallback(callbackManager, callback);

    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            final AccessToken token = loginResult.getAccessToken();
            final Profile profile = Profile.getCurrentProfile();
            Log.e(LOGTAG, "success");
            if (profile != null) {
                nameInput.setText(profile.getName());
                userId = profile.getId();
                if (!token.getDeclinedPermissions().contains("user_photos")) {
                    Log.e(LOGTAG, "accepted photos permission");
                    graphCall();
                }
            } else {
                Log.e(LOGTAG, "profile is null");
                Toast.makeText(getApplicationContext(), "Something went wrong, try logging in again.", Toast.LENGTH_SHORT).show();
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
                            loadingPrompt.setVisibility(View.VISIBLE);
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
            URL imageUrl;
            try {
                imageUrl = new URL(strings[0]);
                image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                picImageView.setImageBitmap(bitmap);
                loadingPrompt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.picture_from_camera_2:
                openCamera();
                break;
            case R.id.picture_from_gallery_2:
                openGallery();
                break;
            case R.id.setup_done:
                if (getValidBitmap() != null) {
                    startProfilePictureThread(getValidBitmap());
                }
                userName = nameInput.getText().toString();
                spEdit.putString(KEY_USER_NAME, userName).apply();
                // spEdit.putString(KEY_SETUP_STAGE, stageSocial).apply();


                break;
            default:
                break;
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA);
        } else {
            Toast.makeText(this, "Sorry, no suitable apps were found to perform this action.", Toast.LENGTH_LONG).show();
        }
    }

    private void openGallery() {
        recycleBitmaps();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
    }

    private void recycleBitmaps() {
        if (image != null) image.recycle();
        if (rotateImage != null) rotateImage.recycle();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // User has returned from Gallery
        if (requestCode == GALLERY && resultCode != 0) {
            mImageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                if (getOrientation(this, mImageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(this, mImageUri));

                    if (rotateImage != null) {
                        rotateImage.recycle();
                    }

                    rotateImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                    picImageView.setImageBitmap(rotateImage);
                    rotateImage = resizeBitmap(rotateImage);
                    image = null;
                } else {
                    picImageView.setImageBitmap(image);
                    image = resizeBitmap(image);
                    rotateImage = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // User has returned from Camera
        if (requestCode == CAMERA && resultCode != 0) {
            Bundle extras = data.getExtras();
            if (image != null && !image.isRecycled()) image.recycle();
            image = (Bitmap) extras.get("data");
            picImageView.setImageBitmap(image);
        }
    }

    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    private Bitmap resizeBitmap(Bitmap image) {
        int maxSizeInPix = convertDptoPx(this, 100); // get the pixel value for 100dp
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int factor = imageHeight/maxSizeInPix;

        Log.e(LOGTAG, "resized bitmap");
        return Bitmap.createScaledBitmap(image, imageWidth/factor, imageHeight/factor, true);
    }

    private byte[] convertBitmapToByteArray(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] array = stream.toByteArray();
        Log.e(LOGTAG, "converted bitmap to byte array");
        return array;
    }

    private void saveByteArrayToDb(byte[] array) {
        dbHandler.saveByteArrayToDb(array);
        Log.e(LOGTAG, "saved byte array to db");
    }

    private Bitmap getValidBitmap() {
        if (image == null && rotateImage != null) return rotateImage;
        if (image != null && rotateImage == null) return image;
        return null;
    }

    private void startProfilePictureThread(final Bitmap bmp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // resize image (add later)
                /* ----- */
                // convert image to byte array
                byte[] array = convertBitmapToByteArray(bmp);
                // save byte array in db
                saveByteArrayToDb(array);
                Log.e(LOGTAG, "finished profile picture thread");



                Intent intent = new Intent(WelcomeActivity.this, BaseActivity.class);
                intent.putExtra(KEY_REQUEST_ACTION, showNewChallenge);
                startActivity(intent);
            }
        });
//        thread.start();
    }

    @Override
    public void onBackPressed() {

        new SnackBar.Builder(this)
                .withOnClickListener(new SnackBar.OnMessageClickListener() {
                    @Override
                    public void onMessageClick(Parcelable parcelable) {
                        spEdit.remove(KEY_USER_NAME);
                        Intent goHome = new Intent(Intent.ACTION_MAIN);
                        goHome.addCategory(Intent.CATEGORY_HOME);
                        goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(goHome);
                    }
                })
                .withMessage("Cancel setup and quit the app?")
                .withActionMessage("Confirm")
                .withTextColorId(R.color.accent)
                //.withTextColorId(getResources().getColor(R.color.white))
                .withBackgroundColorId(R.color.com_facebook_blue)
                .show();

//      super.onBackPressed();
    }

}
