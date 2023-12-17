package com.example.cuoikiandroid.Hung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cuoikiandroid.R;

public class StartTypeActivity extends AppCompatActivity {
    TextView test_title, typing_questions;
    Button btn_start_type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_type);

        test_title = findViewById(R.id.typing_test_title);
        typing_questions = findViewById(R.id.typing_questions);
        btn_start_type = findViewById(R.id.start_typing_button);

        Intent intent = getIntent();
        test_title.setText(intent.getStringExtra("topic_name"));
        typing_questions.setText(intent.getStringExtra("question_amount"));

        btn_start_type.setOnClickListener(v -> {
            Intent startType = new Intent(StartTypeActivity.this, TypeActivity.class);
            startType.putExtra("type_name", test_title.getText().toString());
            startActivity(startType);
        });
    }

}
