package com.fuzzyapps.gustoboliviano;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class nav_profileFragment extends Fragment {
    //UI VARIABLES
    private TextView profileReviewsNumber, profileFollowersNumber, profileFollowingNumber, profileStatus, profileUserName, profileUploadStatus;
    private FancyButton profileFollowButton;
    private CircularImageView profileImage;
    private ProgressBar profileImageProgressBar, chargeEditStatus;
    private Button profileEditButton;
    private EditText editProfileName, editProfileStatus, editProfileEmail, editProfilePhone, statusEdit;
    private ProgressBar chargeEditProfile;
    private LinearLayout profileLinearLayout, statusLinearLayout;
    private Spinner editProfileGender;
    private ImageButton profileEditStatus;
    private TabLayout profileOptions;
    private LayoutInflater layoutInflater;
    //PARAMETER VARIABLES
    private int RESULT_LOAD_IMAGE = 1;
    private int REQUEST_IMAGE_CAPTURE = 2;
    private int CHOICE_AVATAR_FROM_CAMERA_CROP = 3;
    //FIREBASE VARIABLES
    //private Button upload;private EditText urlUpload; TextView progressText;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private StorageReference imagesRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public nav_profileFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nav_profile_fragment, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        profileReviewsNumber = (TextView) view.findViewById(R.id.profileReviewsNumber);
        profileFollowersNumber = (TextView) view.findViewById(R.id.profileFollowersNumber);
        profileFollowingNumber = (TextView) view.findViewById(R.id.profileFollowingNumber);
        profileStatus = (TextView) view.findViewById(R.id.profileStatus);
        profileUserName = (TextView) view.findViewById(R.id.profileUserName);
        profileUploadStatus = (TextView) view.findViewById(R.id.profileUploadStatus);

        profileImageProgressBar = (ProgressBar) view.findViewById(R.id.profileImageProgressBar);

        profileEditButton = (Button) view.findViewById(R.id.profileEditButton);
        profileEditStatus = (ImageButton) view.findViewById(R.id.profileEditStatus);
        profileOptions = (TabLayout) view.findViewById(R.id.profileOptions);
        profileImage = (CircularImageView) view.findViewById(R.id.profileImage);


        profileUploadStatus.setVisibility(View.GONE);
        profileImageProgressBar.setVisibility(View.GONE);

        layoutInflater = getActivity().getLayoutInflater();
        /*upload = (Button) view.findViewById(R.id.upload);
        urlUpload = (EditText) view.findViewById(R.id.urlUpload);
        progressText = (TextView) view.findViewById(R.id.progressText);*/
        //DATABASE REFERENCE
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        //STORGAE REFERENCE
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://gustoboliviano-d8941.appspot.com");
        imagesRef = storageRef.child("images");
        /*upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });*/
        profileEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusDialog();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog();
            }
        });
        profileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileDialog();
            }
        });
        //RESEÑAS
        profileOptions.addTab(profileOptions.newTab().setIcon(R.mipmap.ic_rate_review_black_24dp), true);
        //FAVORITOS
        profileOptions.addTab(profileOptions.newTab().setIcon(R.mipmap.ic_favorite_black_24dp));
        //CONFIGURAR
        profileOptions.addTab(profileOptions.newTab().setIcon(R.mipmap.ic_settings_black_24dp));
        profileOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setProfileTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //SETTING FIRST FRAGMENT
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.profileFrame, new profile_nav_review())
                .commit();
        //FIREBASE LISTENER
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                try{
                    profileStatus.setText(user.getStatus());
                }catch (Exception e){}
                try{
                    profileUserName.setText(user.getName());
                }catch (Exception e){}
                try{
                    profileReviewsNumber.setText(user.getReviewsNumber()+"");
                }catch (Exception e){
                    profileReviewsNumber.setText(R.string.zero);
                }
                try{
                    profileFollowersNumber.setText(user.getFollowersNumber()+"");
                }catch (Exception e){
                    profileFollowersNumber.setText(R.string.zero);
                }
                try{
                    profileFollowingNumber.setText(user.getFollowingNumber()+"");
                }catch (Exception e){
                    profileFollowingNumber.setText(R.string.zero);
                }
                try{
                    Log.e("nav_profileFragment1",user.getImage_url()+"");
                    if(user.getImage_url() != null){
                        Log.e("not empty",user.getImage_url()+"");
                        Picasso.with(getActivity()).load(user.getImage_url()).into(profileImage);
                    }else{
                        Log.e("empty",user.getImage_url()+"");
                        Picasso.with(getActivity()).load(R.drawable.defaultprofile).into(profileImage);
                    }
                }catch (Exception e){
                    Log.e("nav_profileFragment2",e.getMessage());
                    Picasso.with(getActivity()).load(R.drawable.defaultprofile).into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("nav_profileFragment", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("users").child(Globals.userID).addValueEventListener(postListener);
    }

    private void statusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.status);
        View view= layoutInflater.inflate(R.layout.item_status_edit, null);
        builder.setView(view);
        statusLinearLayout = (LinearLayout) view.findViewById(R.id.statusLinearLayout);
        chargeEditStatus = (ProgressBar) view.findViewById(R.id.chargeEditStatus);
        statusEdit = (EditText) view.findViewById(R.id.statusEdit);

        //Re stablish state of visible or gone
        chargeEditStatus.setVisibility(View.VISIBLE);
        statusLinearLayout.setVisibility(View.GONE);

        setUpStatusFromUser();
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateUserStatus(statusEdit.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void editProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.profileInformation);
        View view= layoutInflater.inflate(R.layout.item_profile_edit, null);
        builder.setView(view);
        editProfileGender = (Spinner) view.findViewById(R.id.editProfileGender);
        profileLinearLayout = (LinearLayout) view.findViewById(R.id.profileLinearLayout);
        chargeEditProfile = (ProgressBar) view.findViewById(R.id.chargeEditProfile);
        editProfileName = (EditText) view.findViewById(R.id.editProfileName);
        editProfileStatus = (EditText) view.findViewById(R.id.editProfileStatus);
        editProfileEmail = (EditText) view.findViewById(R.id.editProfileEmail);
        editProfilePhone = (EditText) view.findViewById(R.id.editProfilePhone);

        //Re stablish state of visible or gone
        chargeEditProfile.setVisibility(View.VISIBLE);
        profileLinearLayout.setVisibility(View.GONE);

        String[] genderArray = getResources().getStringArray(R.array.genre_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, genderArray);
        editProfileGender.setAdapter(adapter);
        setUpDataFromUser();
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateUserData(
                        editProfileGender.getSelectedItemPosition()+"",
                        editProfileName.getText().toString(),
                        editProfileStatus.getText().toString(),
                        editProfileEmail.getText().toString(),
                        editProfilePhone.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void updateUserStatus(String status) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("status", status);
        mDatabase.child("users").child(Globals.userID).updateChildren(childUpdates);
    }
    private void updateUserData(String gender, String name, String status, String email, String phone) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("name", name);
        childUpdates.put("status", status);
        childUpdates.put("email", email);
        childUpdates.put("gender", gender);
        childUpdates.put("phone", phone);
        mDatabase.child("users").child(Globals.userID).updateChildren(childUpdates);
    }
    private void setUpStatusFromUser() {
        mDatabase.child("users").child(Globals.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                chargeEditStatus.setVisibility(View.GONE);
                statusLinearLayout.setVisibility(View.VISIBLE);
                try {
                    statusEdit.setText(user.getStatus());
                    statusEdit.setSelection(statusEdit.getText().length());
                }catch (Exception e){}
            }
            public void onCancelled(DatabaseError firebaseError) { }
        });
    }
    private void setUpDataFromUser() {
        mDatabase.child("users").child(Globals.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                chargeEditProfile.setVisibility(View.GONE);
                profileLinearLayout.setVisibility(View.VISIBLE);
                try {
                    editProfileName.setText(user.getName());
                }catch (Exception e){}
                try {
                    editProfileStatus.setText(user.getStatus());
                    editProfileStatus.setSelection(editProfileStatus.getText().length());
                }catch (Exception e){}
                try {
                    editProfileEmail.setText(user.getEmail());
                }catch (Exception e){}
                try {
                    editProfilePhone.setText(user.getPhone());
                }catch (Exception e){}
                try {
                    if(user.getGender().equals("0")){
                        editProfileGender.setSelection(0);
                    }else{
                        if(user.getGender().equals("1")){
                            editProfileGender.setSelection(1);
                        }else{
                            if(user.getGender().equals("2")){
                                editProfileGender.setSelection(2);
                            }else{
                                editProfileGender.setSelection(0);
                            }
                        }
                    }
                }catch (Exception e){}
                //editProfileGender

            }
            public void onCancelled(DatabaseError firebaseError) { }
        });
    }
    private void setProfileTab(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (position){
            case 0:
                //RESEÑAS
                fragmentManager.beginTransaction()
                        .replace(R.id.profileFrame, new profile_nav_review())
                        .commit();
                break;
            case 1:
                //FAVORITOS
                fragmentManager.beginTransaction()
                        .replace(R.id.profileFrame, new profile_nav_favorites())
                        .commit();
                break;
            case 2:
                //CONFIGURAR
                fragmentManager.beginTransaction()
                        .replace(R.id.profileFrame, new profile_nav_settings())
                        .commit();
                break;
            default:
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //from gallery
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                Uri uri = data.getData();
                intent.setDataAndType(uri, "image/*");
                startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_CAMERA_CROP);
            }
            //from camera
            if (requestCode == REQUEST_IMAGE_CAPTURE && null != data) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                Uri uri = data.getData();
                intent.setDataAndType(uri, "image/*");
                startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_CAMERA_CROP);
            }
            //cortarimagen
            if (requestCode == CHOICE_AVATAR_FROM_CAMERA_CROP && null != data) {
                Bitmap bitmap = getBitmapFromData(data);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataB = baos.toByteArray();
                profileImage.setImageBitmap(bitmap);
                UploadTask uploadTask = storageRef.child("images/"+Globals.userID).putBytes(dataB);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        profileImageProgressBar.setVisibility(View.VISIBLE);
                        profileImageProgressBar.setProgress((int) progress);
                        //progressText.setText("Upload is " + progress + "% done");

                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("image_url", downloadUrl.toString());
                        mDatabase.child("users").child(Globals.userID).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                profileUploadStatus.setVisibility(View.VISIBLE);
                                profileImageProgressBar.setVisibility(View.GONE);
                            }
                        });
                                //.updateChildren(childUpdates);
                    }
                });
            }
        }
    }
    //FUNCTIONS FOR OBTAINING IMAGE FROM CAMERA OR GALLERY
    private void imageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.selectImageFrom);
        View view= layoutInflater.inflate(R.layout.item_image, null);
        ImageButton camera = (ImageButton)view.findViewById(R.id.camera);
        ImageButton gallery = (ImageButton)view.findViewById(R.id.gallery);
        builder.setView(view);
        final AlertDialog alert = builder.create();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
                openCamera();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
                openGallery();
            }
        });
        alert.show();
    }
    private Intent getCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("return-data", true);
        return intent;
    }
    private void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }
    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    public static Bitmap getBitmapFromData(Intent data) {
        Bitmap photo = null;
        Uri photoUri = data.getData();
        if (photoUri != null) {
            photo = BitmapFactory.decodeFile(photoUri.getPath());
        }
        if (photo == null) {
            Bundle extra = data.getExtras();
            if (extra != null) {
                photo = (Bitmap) extra.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
        }
        return photo;
    }
}
