package com.example.cuoikiandroid.Khoa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    EditText edt1, edt2, edt3;
    Button btnBack, btnOK;
    String fullEmail;
    Uri imageUri;
    ImageView myImageView2, myImageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    String role;
    final int REQUEST_CODE_IMAGE = 1;
    TextView tvName, tvEmail;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Chỉnh tiêu đề action bar
        getSupportActionBar().setTitle("Detail User");
        //Add hình ảnh vào imageView
        myImageView = findViewById(R.id.imageView);
        myImageView2 = findViewById(R.id.imageView2);
        myImageView.setImageResource(R.drawable.background_profile);

        //Khoi tao storage va storageReference
        storage = FirebaseStorage.getInstance();

        getSupportActionBar().setDisplayShowHomeEnabled(true);




        //Chỉnh read only cho các edit text
        edt1 = findViewById(R.id.edtReadOnly_1);
        edt2 = findViewById(R.id.edtReadOnly_2);
        edt3 = findViewById(R.id.edtReadOnly_3);
        tvName = findViewById(R.id.textView2);
        tvEmail = findViewById(R.id.textView3);

        //Ẩn 2 button khi chưa click edit
        btnBack = findViewById(R.id.btnBack);
        btnOK = findViewById(R.id.btnOK);
        btnBack.setVisibility(View.GONE);
        btnOK.setVisibility(View.GONE);

        edt1.setInputType(InputType.TYPE_NULL);
        edt2.setInputType(InputType.TYPE_NULL);
        edt3.setInputType(InputType.TYPE_NULL);

        Intent intent = getIntent();
        fullEmail = intent.getStringExtra("email");
        getInformationUser(fullEmail);

        ImageView edit = findViewById(R.id.imgEdit);
        //Sự kiện click khi edit

        edit.setOnClickListener(v -> {
            edt1.setInputType(InputType.TYPE_CLASS_TEXT);
            edt2.setInputType(InputType.TYPE_CLASS_TEXT);
            edt3.setInputType(InputType.TYPE_CLASS_TEXT);

            btnBack.setVisibility(View.VISIBLE);
            btnOK.setVisibility(View.VISIBLE);
        });

        //Sự kiện nút back
        btnBack.setOnClickListener(v -> {
            edt1.setInputType(InputType.TYPE_NULL);
            edt2.setInputType(InputType.TYPE_NULL);
            edt3.setInputType(InputType.TYPE_NULL);

            btnBack.setVisibility(View.GONE);
            btnOK.setVisibility(View.GONE);
        });

        //Sự kiện nút OK
        btnOK.setOnClickListener(v -> {
            updateInformationUser(fullEmail);
        });

        //Sự kiện click thay đổi picture profile
        ImageView imgPhoto = findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(v -> {
            //Gọi ra camera của máy
            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent1, REQUEST_CODE_IMAGE);
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.change_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.changePass) {
            createDialogChangePass();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDialogChangePass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText oldPassBox = dialogView.findViewById(R.id.oldPass);
        EditText newPassBox = dialogView.findViewById(R.id.newPass);
        EditText confirmPassBox = dialogView.findViewById(R.id.confirmPass);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        //sự kiện nút reset
        dialogView.findViewById(R.id.btnChange).setOnClickListener(v1 -> {
                    String oldPass = oldPassBox.getText().toString();
                    String newPass = newPassBox.getText().toString();
                    String confirmPass = confirmPassBox.getText().toString();
                    if (oldPass.isEmpty()) {
                        Toast.makeText(this, "Please enter your old password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.isEmpty()) {
                        Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (confirmPass.isEmpty()) {
                        Toast.makeText(this, "Please enter your confirm password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(this, "The new password must match the confirmed password.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Đổi password
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Mật khẩu đã được cập nhật.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Không thể cập nhật mật khẩu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Mật khẩu đã được cập nhật.", Toast.LENGTH_SHORT).show();
                    }
                });

            //Sự kiện nút cancel
            dialogView.findViewById(R.id.btnProfileCancel).setOnClickListener(v -> {
                dialog.dismiss();
            });
            if(dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            myImageView2.setImageBitmap(bitmap);
            uploadImagetoStorage();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImagetoStorage() {
        storageReference = storage.getReferenceFromUrl("gs://quizlet-76da3.appspot.com").child("image/");
        Uri imageUri = getImageUriFromImageView(myImageView2);
        UploadTask uploadTask = storageReference.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();

                // Khởi tạo Firebase Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Thêm đường dẫn vào Firestore
                Map<String, Object> user = new HashMap<>();
                user.put("image", downloadUrl);
                //Lấy document id
                db.collection("users").whereEqualTo("email", fullEmail)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String documentId = document.getId();

                                    //Lưu string url ảnh vào database
                                    db.collection("users").document(documentId)
                                            .set(user, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getApplicationContext(), "Lưu hình ảnh thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Lưu hình ảnh thất bại", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            }
                        });
            });
        });

    }


    private Uri getImageUriFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);
        return Uri.parse(path);
    }

    private void updateInformationUser(String fullEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newData = new HashMap<>();
        String newName = edt1.getText().toString();
        int newAge = Integer.parseInt(edt2.getText().toString());
        String newPhone = edt3.getText().toString();
        newData.put("name", newName);
        newData.put("age", newAge);
        newData.put("phone", newPhone);
        db.collection("users").whereEqualTo("email", fullEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();

                            db.collection("users").document(documentId).update(newData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileActivity.this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }



    private void getInformationUser(String fullEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("email", fullEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String name = document.getString("name");
                            int age = document.getLong("age").intValue();
                            String phone = document.getString("phone");
                            String urlImage = document.getString("image");

                            edt1.setText(name);
                            edt2.setText(age + "");
                            edt3.setText(phone);
                            tvName.setText(name);
                            tvEmail.setText(fullEmail);
                            Glide.with(getApplicationContext()).load(urlImage).into(myImageView2);
                        }
                    }
                });
    }
}