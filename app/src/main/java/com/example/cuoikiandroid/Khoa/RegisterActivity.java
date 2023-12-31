package com.example.cuoikiandroid.Khoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword, edtConfirmPass;
    private Button btnRegister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edtMail);
        edtPassword = findViewById(R.id.registerPass);
        edtConfirmPass = findViewById(R.id.edtPass);
        btnRegister = findViewById(R.id.btnRegister);



        btnRegister.setOnClickListener(v -> {
            register();

        });
    }

    private void register() {
        String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        String confirmPass = edtConfirmPass.getText().toString();
        if(!(validate(email, pass, confirmPass))) {
            Toast.makeText(this, "Đăng ký tài khoản không thành công. Vui lòng nhập đủ ô hoặc hai mật khẩu phải giống nhau!", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> userAccount = new HashMap<>();
                    userAccount.put("email", email);
                    userAccount.put("name", "");
                    userAccount.put("age", 0);
                    userAccount.put("phone", "");
                    db.collection("users")
                            .add(userAccount)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error adding document", e);
                                }
                            });
                    Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Đăng ký tài khoản không thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validate(String email, String pass, String confirmPass) {
        if(email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            return false;
        }
        if(!pass.equals(confirmPass)) {
            return false;
        }
        return true;
    }
}