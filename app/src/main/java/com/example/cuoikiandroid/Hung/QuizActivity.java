package com.example.cuoikiandroid.Hung;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.cuoikiandroid.Phat.AddWordActivity;
import com.example.cuoikiandroid.Phat.ListTopicActivity;
import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView rv;
    QuizAdapter quizAdapter;
    ArrayList<Quiz> quizList;
    TextView test_name;
    Button submit_quiz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        db = FirebaseFirestore.getInstance();
        test_name = findViewById(R.id.test_name);
        submit_quiz = findViewById(R.id.submit_quiz);

        rv = findViewById(R.id.quizRecyclerView);
        quizList = new ArrayList<>();

        quizAdapter = new QuizAdapter(this, quizList);
        rv.setAdapter(quizAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        loadWordForQuestion();

        Intent rep_start_quiz = getIntent();
        test_name.setText(rep_start_quiz.getStringExtra("quiz_name"));

        submit_quiz.setOnClickListener(v -> {
            submitQuiz();
        });
    }

    public void loadWordForQuestion(){
        db.collection("topics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot maindoc : task.getResult()) {
                                String testName = test_name.getText().toString();
                                String testNameDoc = maindoc.getString("topicName");
                                if(testNameDoc.equals(testName)){
                                    quizList.clear();

                                    List<Map<String, String>> words = (List<Map<String, String>>) maindoc.get("words");
                                    List<String> frontTextList = new ArrayList<>();

                                    for (Map<String, String> word : words) {
                                        String backText = word.get("BackText");
                                        String frontText = word.get("FrontText");

                                        frontTextList.add(frontText);

                                        Collections.shuffle(frontTextList);
                                        if (!frontTextList.isEmpty()) {
                                            // Lấy giá trị từ frontTextList nếu có sẵn
                                            String frontText1 = frontTextList.size() > 0 ? frontTextList.get(0) : "hello";
                                            String frontText2 = frontTextList.size() > 1 ? frontTextList.get(1) : "Bonjour";
                                            String frontText3 = frontTextList.size() > 2 ? frontTextList.get(2) : "Zdravstvuyte";
                                            String frontText4 = frontTextList.size() > 3 ? frontTextList.get(3) : "Sawasdee";

                                            quizList.add(new Quiz(backText, frontText1,
                                                    frontText2, frontText3, frontText4));
                                        }
                                    }

                                    quizAdapter.notifyDataSetChanged();
                                    break;
                                }

                            }
                        } else {
                            Log.w("Firestore data", "Lỗi truy cập document chính", task.getException());
                        }
                    }
                });
    }

    public void submitQuiz(){
        ArrayList<Map<String, Object>> quizData = new ArrayList<>();

        for(int i = 0; i < quizList.size(); i++){
            Map<String, Object> quizItem = new HashMap<>();
            Quiz q = quizList.get(i);

            quizItem.put("question", q.getQuestion());
            quizItem.put("selectedOption", q.getSelectedOption());

            String selectedText = getTextFromSelectedOption(q);
            quizItem.put("selectedText", selectedText);

            quizData.add(quizItem);
        }

        Map<String, Object> quizTest = new HashMap<>();
        quizTest.put("quizName",test_name.getText().toString());
        quizTest.put("results",quizData);

        db.collection("quiz")
                .add(quizTest)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent submit_quiz_test = new Intent(QuizActivity.this, QuizScoreActivity.class);
                        submit_quiz_test.putExtra("test_quiz_name", test_name.getText().toString());
                        submit_quiz_test.putExtra("total_quiz_question", quizList.size());
                        startActivity(submit_quiz_test);
                        Toast.makeText(QuizActivity.this,"Nộp bài trắc nghiệm thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuizActivity.this,"Nộp bài trắc nghiệm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getTextFromSelectedOption(Quiz quiz) {

        int selectedOption = quiz.getSelectedOption();

        switch (selectedOption) {
            case 1:
                return quiz.getA();
            case 2:
                return quiz.getB();
            case 3:
                return quiz.getC();
            case 4:
                return quiz.getD();
            default:
                return "";
        }
    }
}