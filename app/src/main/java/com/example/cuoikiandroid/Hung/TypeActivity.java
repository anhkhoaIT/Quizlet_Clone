package com.example.cuoikiandroid.Hung;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeActivity extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView rv;
    TypeAdapter typeAdapter;
    ArrayList<TypeWord> typeWordList;
    TextView test_name;
    Button submit_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_word);

        db = FirebaseFirestore.getInstance();
        test_name = findViewById(R.id.test_name);
        submit_type = findViewById(R.id.submit_type);

        rv = findViewById(R.id.typeRecyclerView);
        typeWordList = new ArrayList<>();

        typeAdapter = new TypeAdapter(this, typeWordList);
        rv.setAdapter(typeAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        loadWordForQuestion();

        Intent rep_start_type = getIntent();
        test_name.setText(rep_start_type.getStringExtra("type_name"));

        submit_type.setOnClickListener(v -> {
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
                                    typeWordList.clear();

                                    List<Map<String, String>> words = (List<Map<String, String>>) maindoc.get("words");

                                    for (Map<String, String> word : words) {
                                        String backText = word.get("BackText");
                                        typeWordList.add(new TypeWord(backText, ""));
                                    }

                                    typeAdapter.notifyDataSetChanged();
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
        ArrayList<Map<String, Object>> typeData = new ArrayList<>();

        for(int i = 0; i < typeWordList.size(); i++){
            Map<String, Object> typeItem = new HashMap<>();
            TypeWord tw = typeWordList.get(i);

            typeItem.put("question", tw.getQuestion());
            typeItem.put("typedText", tw.getTyping());

            typeData.add(typeItem);
        }

        Map<String, Object> typingTest = new HashMap<>();
        typingTest.put("typeName",test_name.getText().toString());
        typingTest.put("results",typeData);

        db.collection("type")
                .add(typingTest)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent submit_type_test = new Intent(TypeActivity.this, TypeScoreActivity.class);
                        submit_type_test.putExtra("test_type_name", test_name.getText().toString());
                        submit_type_test.putExtra("total_type_question", typeWordList.size());
                        startActivity(submit_type_test);
                        Toast.makeText(TypeActivity.this,"Nộp bài gõ từ thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TypeActivity.this,"Nộp bài gõ từ thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
