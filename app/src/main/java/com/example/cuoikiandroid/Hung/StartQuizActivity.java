package com.example.cuoikiandroid.Hung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cuoikiandroid.R;

public class StartQuizActivity extends AppCompatActivity {

    TextView test_title, questions;
    Button btn_start_quiz;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        test_title = findViewById(R.id.test_title);
        questions = findViewById(R.id.questions);
        btn_start_quiz = findViewById(R.id.button);

        Intent intent = getIntent();
        test_title.setText(intent.getStringExtra("topic_name"));
        questions.setText(intent.getStringExtra("question_amount"));

        btn_start_quiz.setOnClickListener(v -> {
            Intent startQuiz = new Intent(StartQuizActivity.this, QuizActivity.class);
            startQuiz.putExtra("quiz_name", test_title.getText().toString());
            startActivity(startQuiz);
        });
    }
}
