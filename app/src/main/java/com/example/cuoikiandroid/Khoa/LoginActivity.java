package com.example.cuoikiandroid.Khoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuoikiandroid.MainActivity;
import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnRegister;
    private TextView forgotPassword;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtMail);
        edtPassword = findViewById(R.id.edtPass);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.forgotPass);

        //Sự kiện nút register
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        //Sự kiện nút login
        btnLogin.setOnClickListener(v -> {
            login();
        });

        //Sự kiện tv forgot password
        forgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
            EditText emailBox = dialogView.findViewById(R.id.forgotEmail);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            //sự kiện nút reset
            dialogView.findViewById(R.id.btnReset).setOnClickListener(v1 -> {
                String userMail = emailBox.getText().toString();
                if(userMail.isEmpty()) {
                    Toast.makeText(this, "Please enter your email to reset", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(userMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Fail to send", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });

            //Sự kiện nút cancel
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            if(dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });
    }

    private void login() {
        String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        if(email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập không thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}