package com.example.cuoikiandroid.Hung;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cuoikiandroid.Phat.ListTopicActivity;
import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizScoreActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView quiz_name, total_question,correct_question, uncorrect_question;
    ImageButton leaderboard;

    ImageView back_listTopic;
    List<Map<String, String>> wordsList;
    List<Map<String, String>> resultsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_score);
        db = FirebaseFirestore.getInstance();

        back_listTopic = findViewById(R.id.quizResult_back_listTopic);

        quiz_name = findViewById(R.id.test_quiz_name);
        total_question = findViewById(R.id.total_quiz_question);
        correct_question = findViewById(R.id.correct_quiz_question);
        uncorrect_question = findViewById(R.id.uncorrect_quiz_question);
        leaderboard = findViewById(R.id.go_quiz_leaderboard);

        wordsList = new ArrayList<>();
        resultsList = new ArrayList<>();

        Intent received_quiz = getIntent();
        int TotalQuest = received_quiz.getIntExtra("total_quiz_question", 0);

        quiz_name.setText(received_quiz.getStringExtra("test_quiz_name"));
        total_question.setText(String.valueOf(TotalQuest));

        loadWordFromTopic();
        loadSubmittedQuiz();

        back_listTopic.setOnClickListener( v -> {
            Intent intent = new Intent(QuizScoreActivity.this, ListTopicActivity.class);
            startActivity(intent);
        });
    }

    public void loadWordFromTopic(){
        db.collection("topics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot maindoc : task.getResult()) {
                                String testQuizName = quiz_name.getText().toString();
                                String topicNameDoc = maindoc.getString("topicName");
                                if(topicNameDoc.equals(testQuizName)){
                                    wordsList = (List<Map<String, String>>) maindoc.get("words");
                                    break;
                                }

                            }
                        } else {
                            Log.w("Firestore data", "Lỗi truy cập document chính", task.getException());
                        }
                    }
                });
    }

    public void loadSubmittedQuiz(){
        db.collection("quiz")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot maindoc : task.getResult()) {
                                String testQuizName = quiz_name.getText().toString();
                                String quizNameDoc = maindoc.getString("quizName");
                                if(quizNameDoc.equals(testQuizName)){
                                    resultsList = (List<Map<String, String>>) maindoc.get("results");
                                    compareResultsWithWords();
                                    break;

                                }

                            }
                        } else {
                            Log.w("Firestore data", "Lỗi truy cập document chính", task.getException());
                        }
                    }
                });
    }

    private void compareResultsWithWords() {
        int totalQuestions = Integer.parseInt(total_question.getText().toString());
        int correctCount = 0;

        for (Map<String, String> res : resultsList) {
            String question = res.get("question");
            String selectedText = res.get("selectedText");

            for (Map<String, String> word : wordsList) {
                String frontText = word.get("FrontText");
                String backText = word.get("BackText");

                if (question.equals(backText) && selectedText.equals(frontText)) {
                    // Câu trả lời đúng
                    correctCount++;
                }
            }
        }
        correct_question.setText(String.valueOf(correctCount));
        uncorrect_question.setText(String.valueOf(totalQuestions - correctCount));
    }

}
