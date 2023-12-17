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

public class TypeScoreActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView type_name, total_question,correct_question, uncorrect_question;
    ImageButton leaderboard;

    ImageView back_listTopic;

    List<Map<String, String>> wordsList;
    List<Map<String, String>> resultsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_score);
        db = FirebaseFirestore.getInstance();

        back_listTopic = findViewById(R.id.typeResult_back_listTopic);

        type_name = findViewById(R.id.test_type_name);
        total_question = findViewById(R.id.total_type_question);
        correct_question = findViewById(R.id.correct_type_question);
        uncorrect_question = findViewById(R.id.uncorrect_type_question);
        leaderboard = findViewById(R.id.go_type_leaderboard);

        wordsList = new ArrayList<>();
        resultsList = new ArrayList<>();

        Intent received_type = getIntent();
        int TotalQuest = received_type.getIntExtra("total_type_question", 0);

        type_name.setText(received_type.getStringExtra("test_type_name"));
        total_question.setText(String.valueOf(TotalQuest));

        loadWordFromTopic();
        loadSubmittedQuiz();

        back_listTopic.setOnClickListener( v -> {
            Intent intent = new Intent(TypeScoreActivity.this, ListTopicActivity.class);
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
                                String testTypeName = type_name.getText().toString();
                                String topicNameDoc = maindoc.getString("topicName");
                                if(topicNameDoc.equals(testTypeName)){
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
        db.collection("type")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot maindoc : task.getResult()) {
                                String testTypeName = type_name.getText().toString();
                                String typeNameDoc = maindoc.getString("typeName");
                                if(typeNameDoc.equals(testTypeName)){
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
            String typedText = res.get("typedText").toLowerCase();

            for (Map<String, String> word : wordsList) {
                String frontText = word.get("FrontText").toLowerCase();
                String backText = word.get("BackText");

                if (question.equals(backText) && typedText.contains(frontText)) {
                    // Câu trả lời đúng
                    correctCount++;
                }
            }
        }
        correct_question.setText(String.valueOf(correctCount));
        uncorrect_question.setText(String.valueOf(totalQuestions - correctCount));
    }
}
