package com.bradenhart.hcnavigationview.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.bradenhart.hcnavigationview.Constants.*;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by bradenhart on 25/06/15.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private final String LOGTAG = "ProfileFragment";
    private final String KEY_EDIT_MODE = "edit_mode";
    private final String KEY_PREVIEW = "preview";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private View headerView;
    private TextView userNameTv, previewLabel;
    private String userName;
    private FloatingActionButton fab;
    private CircleImageView profilePic, previewPic;
    private FragmentManager manager;
    private boolean editMode = false;
    private ScrollView editModeLayout;
    private EditText newNameEditText;
    private ImageView fromGalleryBtn, fromCameraBtn;
    private final int CAMERA = 1101, GALLERY = 1011;
    private static Bitmap image = null;
    private static Bitmap rotateImage = null;
    private Uri mImageUri;
    private DatabaseHandler dbHandler;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showSavedProfilePicture();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        spEdit = sharedPreferences.edit();

        dbHandler = DatabaseHandler.getInstance(context);

        profilePic = (CircleImageView) view.findViewById(R.id.profile_profile_pic);

        previewPic = (CircleImageView) view.findViewById(R.id.profile_preview_image);
        previewLabel = (TextView) view.findViewById(R.id.profile_preview_label);

        userNameTv = (TextView) view.findViewById(R.id.profile_username_textview);
        userName = sharedPreferences.getString(KEY_USER_NAME, defaultName);
        userNameTv.setText(userName);

        fab = (FloatingActionButton) view.findViewById(R.id.fab_profile_button);
        fab.setOnClickListener(this);

        editModeLayout = (ScrollView) view.findViewById(R.id.edit_mode_layout);
        newNameEditText = (EditText) view.findViewById(R.id.edit_input_name);
        newNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        newNameEditText.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);
        newNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (newNameEditText.getText().toString().length() == 0) {
//                        newNameEditText.setFocusable(false);
//                        newNameEditText.setFocusableInTouchMode(false);
//                        newNameEditText.setFocusable(true);
//                        newNameEditText.setFocusableInTouchMode(true);
                    }
                }
                return false;
            }
        });

        fromGalleryBtn = (ImageView) view.findViewById(R.id.picture_from_gallery);
        fromCameraBtn = (ImageView) view.findViewById(R.id.picture_from_camera);

        fromGalleryBtn.setOnClickListener(this);
        fromCameraBtn.setOnClickListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_EDIT_MODE)) editModeLayout.setVisibility(View.VISIBLE);
            if (savedInstanceState.getBoolean(KEY_PREVIEW)) {
                previewLabel.setVisibility(View.VISIBLE);
                previewPic.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fab_profile_button:
                if (editModeLayout.getVisibility() == View.GONE) {
                    fab.setImageResource(R.drawable.ic_done_white_18dp);
                    newNameEditText.setText("");
                    ///newNameEditText.clearFocus();
                    editModeLayout.setVisibility(View.VISIBLE);
                } else {
                    handleNewName();
                    handleNewProfilePic();
                    previewPic.setVisibility(View.GONE);
                    previewLabel.setVisibility(View.GONE);
                    fab.setImageResource(R.drawable.ic_mode_edit_white_18dp);
                    editModeLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.picture_from_gallery:
                openGallery();
                break;
            case R.id.picture_from_camera:
                openCamera();
                break;
            default:
                break;
        }

    }

    private void handleNewName() {
        String oldName = sharedPreferences.getString(KEY_USER_NAME, defaultName);
        String newName = newNameEditText.getText().toString();
        if (newName.length() > 0 && !newName.equals(oldName)) {
            spEdit.putString(KEY_USER_NAME, newName).apply();
            userNameTv.setText(newName);
        }
    }

    private Bitmap convertByteArrayToBitmap(byte[] array) {
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    private void openGallery() {
        recycleBitmaps();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA);
        } else {
            Toast.makeText(context, "Sorry, no suitable apps were found to perform this action.", Toast.LENGTH_LONG).show();
        }
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
                    if (rotateImage != null)
                        rotateImage.recycle();
                    rotateImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                    showImagePreview(rotateImage);
                    rotateImage = resizeBitmap(rotateImage);
                    image = null;
                } else {
                    showImagePreview(image);
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
            showImagePreview(image);
        }
    }

    public static int getOrientation(Context context, Uri photoUri) {
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

    private void showImagePreview(Bitmap image) {
        previewLabel.setVisibility(View.VISIBLE);
        previewPic.setVisibility(View.VISIBLE);
        previewPic.setImageBitmap(image);
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // resize image (below)
                //* ----- *//
                // convert image to byte array
                byte[] array = convertBitmapToByteArray(bmp);
                // save byte array in db
                saveByteArrayToDb(array);
                Log.e(LOGTAG, "finished profile picture thread");
            }
        });
        thread.start();
    }

    private void handleNewProfilePic() {
        if (getValidBitmap() != null) { // a dp has been chosen
            profilePic.setImageBitmap(getValidBitmap());
            startProfilePictureThread(getValidBitmap());
        } else {
            if (dbHandler.retrieveByteArrayFromDb() == null) { // no pic chosen, no pic saved, set default
                profilePic.setImageResource(defaultPic);
            }
        }
    }

    private void showSavedProfilePicture() {
        byte[] array = dbHandler.retrieveByteArrayFromDb();
        if (array == null) {
            profilePic.setImageResource(defaultPic);
        } else {
            Bitmap bitmap = convertByteArrayToBitmap(array);
            if (bitmap == null) {
                Log.e(LOGTAG, "set image as default");
                profilePic.setImageResource(defaultPic);
            } else {
                Log.e(LOGTAG, "set image from database");
                profilePic.setImageBitmap(bitmap);
                //bitmap.recycle();
                //profilePic.setImageResource(defaultPic); TEST that picture can be set
            }
        }
    }

/*    private void showSavedProfilePicture() {
        byte[] array = dbHandler.retrieveByteArrayFromDb();
        Bitmap bitmap = convertByteArrayToBitmap(array);
        if (bitmap == null) {
            profilePic.setImageResource(defaultPic);
        } else {
            profilePic.setImageBitmap(bitmap);
            bitmap.recycle();
        }
    }*/


    private void startGetPhotoThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSavedProfilePicture();
                Log.e(LOGTAG, "finished run on ui thread photo thread");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_EDIT_MODE, isVisibile(editModeLayout));
        outState.putBoolean(KEY_PREVIEW, isVisibile(previewPic));
    }

    private boolean isVisibile(View v) {
        return v.getVisibility() == View.VISIBLE;
    }
}

