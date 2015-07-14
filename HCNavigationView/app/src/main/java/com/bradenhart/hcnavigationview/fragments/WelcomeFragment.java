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
import android.widget.Toast;

import com.bradenhart.hcnavigationview.R;
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
    private Button cameraBtn, galleryBtn, skipBtn, nextBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private String userName;
    private final int GALLERY = 1;
    private CircleImageView picImageView;
    private Bitmap Image = null, rotateImage = null;
    private Uri mImageUri;
    private DatabaseHandler dbHandler;

    public WelcomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        spEdit = sharedPreferences.edit();

        dbHandler = DatabaseHandler.getInstance(context);

        nameInput = (EditText) view.findViewById(R.id.input_name);
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
                if (sharedPreferences.getString(KEY_USER_NAME, defaultName).equals(userName)) {
                    Toast.makeText(context, userName, Toast.LENGTH_SHORT).show();
                }
            }
        });

        picImageView = (CircleImageView) view.findViewById(R.id.welcome_profile_pic);

        cameraBtn = (Button) view.findViewById(R.id.welcome_select_camera);
        galleryBtn = (Button) view.findViewById(R.id.welcome_select_gallery);

        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);

        skipBtn = (Button) view.findViewById(R.id.welcome_skip);
        nextBtn = (Button) view.findViewById(R.id.welcome_next);

        skipBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = null;

        switch (id) {
            case R.id.welcome_select_camera:

                break;
            case R.id.welcome_select_gallery:
                openGallery();
                break;
            case R.id.welcome_skip:
                if (getValidBitmap() != null) {
                    startProfilePictureThread(getValidBitmap());
                }
                spEdit.putString(KEY_USER_NAME, defaultName).apply();
                spEdit.putString(KEY_SETUP_STAGE, stageSocial).apply();
                fragment = new SocialFragment();
                fragmentManager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                break;
            case R.id.welcome_next:
                if (getValidBitmap() != null) {
                    startProfilePictureThread(getValidBitmap());
                }
                userName = nameInput.getText().toString();
                spEdit.putString(KEY_USER_NAME, userName).apply();
                spEdit.putString(KEY_SETUP_STAGE, stageSocial).apply();
                fragment = new SocialFragment();
                fragmentManager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                break;
            default:
                break;
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
        if (Image != null) Image.recycle();
        if (rotateImage != null) rotateImage.recycle();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY && resultCode != 0) {
            mImageUri = data.getData();
            try {
                Image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
                if (getOrientation(getActivity(), mImageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(getActivity(), mImageUri));
                    if (rotateImage != null)
                        rotateImage.recycle();
                    rotateImage = Bitmap.createBitmap(Image, 0, 0, Image.getWidth(), Image.getHeight(), matrix, true);
                    picImageView.setImageBitmap(rotateImage);
                    Image = null;
                } else {
                    picImageView.setImageBitmap(Image);
                    rotateImage = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    private Bitmap resizeBitmap(Bitmap image) {
        Log.e(LOGTAG, "resized bitmap");
        return null;
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
        if (Image == null && rotateImage != null) return rotateImage;
        if (Image != null && rotateImage == null) return Image;
        return null;
    }

    private void startProfilePictureThread(final Bitmap bmp) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // resize image (add later)
                /* ----- */
                // convert image to byte array
                byte[] array = convertBitmapToByteArray(bmp);
                // save byte array in db
                saveByteArrayToDb(array);
                Log.e(LOGTAG, "finished profile picture thread");
            }
        });
        thread.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save whatever the user has typed in the name field.
        // will reload them on orientation change.
        spEdit.putString(KEY_USER_NAME, nameInput.getText().toString()).apply();
    }
}
