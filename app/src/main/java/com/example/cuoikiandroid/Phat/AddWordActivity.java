package com.example.cuoikiandroid.Phat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddWordActivity extends AppCompatActivity {
    RecyclerView rv;
    EditText topicName;
    EditText term;
    EditText definition;
    FloatingActionButton btn_addWord_Topic;
    ImageView confirm_add_topic;
    WordAdapter wadapter;
    ArrayList<Word> words;

    FirebaseFirestore db;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.word_recyclerView);
        btn_addWord_Topic = findViewById(R.id.addWordForTopic_btn);
        topicName = findViewById(R.id.topicName_editText);
        confirm_add_topic = findViewById(R.id.confirm_add_topic);

        words = new ArrayList<>();

        wadapter = new WordAdapter(this, words);
        rv.setAdapter(wadapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        btn_addWord_Topic.setOnClickListener(v -> {
            words.add(new Word());
            wadapter.notifyItemInserted(words.size() - 1);
            rv.scrollToPosition(words.size() - 1);
        });

        confirm_add_topic.setOnClickListener(v -> {
            String topicNameText = topicName.getText().toString();

            if (topicNameText.isEmpty()) {
                topicName.setError("Vui lòng nhập tên chủ đề");
            }else {
                comfirm_addTopic();
            }

        });
    }

    public void comfirm_addTopic(){
        // Tạo một ArrayList để lưu trữ dữ liệu từ RecyclerView
        ArrayList<Map<String, Object>> wordsData = new ArrayList<>();

        // Duyệt qua từng item trong RecyclerView để lấy dữ liệu từ EditText
        for(int i = 0; i < words.size(); i++){
            Map<String, Object> wordTopic = new HashMap<>();
            wordTopic.put("BackText", words.get(i).getBackText());
            wordTopic.put("FrontText", words.get(i).getFrontText());
            wordsData.add(wordTopic);
        }

        String topicNameText = topicName.getText().toString();
        Topic newTopic = new Topic(topicNameText, String.valueOf(words.size()));

        // Thêm dữ liệu vào Firestore
        Map<String, Object> topicData = new HashMap<>();
        topicData.put("topicName", newTopic.getTopicName());
        topicData.put("wordAmount", newTopic.getWordAmount());
        topicData.put("words", wordsData);

        db.collection("topics")
                .add(topicData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(AddWordActivity.this, ListTopicActivity.class);
                        intent.putExtra("topic", newTopic);
                        startActivity(intent);
                        setResult(RESULT_OK);
                        Toast.makeText(AddWordActivity.this,"Thêm từ cho chủ đề thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddWordActivity.this,"Thêm từ cho chủ đề thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
