package com.example.chatapp.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.UsersModel;
import com.example.chatapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {
    // firebase varriables
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference dbRoot;

     // required variables
     View root;
    String UID ;
    Uri imageUri;
    ImageView imgProfile;
    EditText edName,edNum;
    ProgressBar progressBar;
    Button btnUpdate;
    private @NonNull FragmentProfileBinding binding;

    // Update button clicked
    public void btnUpdate_Clicked() {
        StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFileExtendion(imageUri));
        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UsersModel model = new UsersModel();
                        model.setUserProfilePic(uri.toString());
                        model.setUserName(edName.getText().toString());
                        model.setUserContactNum(edNum.getText().toString());

                        db.getReference("tblUser").child(UID).setValue(model);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("errorUpload", "onFailure: "+e);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    // get the file type
    private String getFileExtendion(Uri mUri) {
        ContentResolver contentResolver = root.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(mUri));
    }

    public void imgProfilePic_Clicked() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i,2);
    }

    // fill the details
    private void fillFields(){
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference root = db.getReference("tblUser").child(UID);
        root.get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        UsersModel user = dataSnapshot.getValue(UsersModel.class);

                        edName.setText(user.getUserName());
                        edNum.setText(user.getUserContactNum());
                        Glide.with(getContext()).load(user.getUserProfilePic()).into(imgProfile);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        // initilizing variables
        UID = auth.getCurrentUser().getUid();
        imgProfile = root.findViewById(R.id.imgProfilePic);
        edName = root.findViewById(R.id.edUserName);
        edNum = root.findViewById(R.id.edUserNum);
        btnUpdate = root.findViewById(R.id.btnUpdate);
        progressBar = root.findViewById(R.id.progressBar2);

        fillFields();

        // onclick listeners
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgProfilePic_Clicked();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate_Clicked();
            }
        });

        return root;
    }

    // overriding method to see if the image file correct then set image on imageview
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}