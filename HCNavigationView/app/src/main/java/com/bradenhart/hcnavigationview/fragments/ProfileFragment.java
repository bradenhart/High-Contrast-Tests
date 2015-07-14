package com.bradenhart.hcnavigationview.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private final String TAG_EDIT_FRAGMENT = "tag_edit";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private View headerView;
    private TextView userNameTv, previewLabel;
    private String userName;
    private FloatingActionButton fab;
    private CircleImageView profilePic, previewPic;
    private FragmentManager manager;
    private boolean editMode = false;
    private RelativeLayout editModeLayout;
    private EditText newNameEditText;
    private ImageView fromGalleryBtn, fromCameraBtn;
    private final int GALLERY = 1;
    private static Bitmap Image = null;
    private static Bitmap rotateImage = null;
    private Uri mImageUri;
    private DatabaseHandler dbHandler;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!sharedPreferences.contains(KEY_PROFILE_PIC)) {
            profilePic.setImageResource(defaultPic);
        } else {
            showSavedProfilePicture();
        }
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

        editModeLayout = (RelativeLayout) view.findViewById(R.id.edit_mode_layout);
        newNameEditText = (EditText) view.findViewById(R.id.edit_input_name);

        fromGalleryBtn = (ImageView) view.findViewById(R.id.picture_from_gallery);
        fromCameraBtn = (ImageView) view.findViewById(R.id.picture_from_camera);

        fromGalleryBtn.setOnClickListener(this);
        fromCameraBtn.setOnClickListener(this);

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

    private void showSavedProfilePicture() {
        byte[] array = dbHandler.retrieveByteArrayFromDb();
        Bitmap bitmap = convertByteArrayToBitmap(array);
        if (bitmap == null) {
            profilePic.setImageResource(defaultPic);
        } else {
            profilePic.setImageBitmap(bitmap);
            bitmap.recycle();
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
                    showImagePreview(rotateImage);
                    Image = null;
                } else {
                    showImagePreview(Image);
                    rotateImage = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void handleNewProfilePic() {
        if (getValidBitmap() != null) {
            profilePic.setImageBitmap(getValidBitmap());
            startProfilePictureThread(getValidBitmap());
        }
    }



}

