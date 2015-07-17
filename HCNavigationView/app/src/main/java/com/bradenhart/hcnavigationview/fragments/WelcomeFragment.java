package com.bradenhart.hcnavigationview.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.activities.BaseActivity;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 29/06/15.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private final String LOGTAG = "Welcome Fragment";
    private Context context;
    private EditText nameInput;
    private Button doneBtn;
    private ImageView cameraBtn, galleryBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private String userName;
    private final int CAMERA = 1101, GALLERY = 1011;
    private CircleImageView picImageView;
    private Bitmap image = null, rotateImage = null;
    private Uri mImageUri;
    private DatabaseHandler dbHandler;
    private RelativeLayout progressScreen;

    public WelcomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_test, container, false);

        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        spEdit = sharedPreferences.edit();

        dbHandler = DatabaseHandler.getInstance(context);

        nameInput = (EditText) view.findViewById(R.id.input_name2);
        nameInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        nameInput.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);

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

        picImageView = (CircleImageView) view.findViewById(R.id.profile_preview_image_2);

        cameraBtn = (ImageView) view.findViewById(R.id.picture_from_camera_2);
        galleryBtn = (ImageView) view.findViewById(R.id.picture_from_gallery_2);
        doneBtn = (Button) view.findViewById(R.id.setup_done);
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);

        progressScreen = (RelativeLayout) view.findViewById(R.id.progress_screen);

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = null;

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

                progressScreen.setVisibility(View.VISIBLE);
                break;
            case R.id.welcome_skip:
                /* Don't save anything if user skips, set default name and picture
                if (getValidBitmap() != null) {
                    startProfilePictureThread(getValidBitmap());
                }*/
//                spEdit.putString(KEY_USER_NAME, defaultName).apply();
//                spEdit.putString(KEY_SETUP_STAGE, stageSocial).apply();
//                fragment = new SocialFragment();
//                fragmentManager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                break;
            case R.id.welcome_next:

                break;
            default:
                break;
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA);
        } else {
            Toast.makeText(context, "Sorry, no suitable apps were found to perform this action.", Toast.LENGTH_LONG).show();
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
        // User has returned from Gallery
        if (requestCode == GALLERY && resultCode != 0) {
            mImageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
                if (getOrientation(getActivity(), mImageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(getActivity(), mImageUri));

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
        int maxSizeInPix = convertDptoPx(context, 100); // get the pixel value for 100dp
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // resize image (add later)
                /* ----- */
                // convert image to byte array
                byte[] array = convertBitmapToByteArray(bmp);
                // save byte array in db
                saveByteArrayToDb(array);
                Log.e(LOGTAG, "finished profile picture thread");

                progressScreen.setVisibility(View.GONE);

                Intent intent = new Intent(getActivity().getApplicationContext(), BaseActivity.class);
                intent.putExtra(KEY_REQUEST_ACTION, showNewChallenge);
                startActivity(intent);
            }
        });
//        thread.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save whatever the user has typed in the name field.
        // will reload them on orientation change.
        spEdit.putString(KEY_USER_NAME, nameInput.getText().toString()).apply();
    }

}
