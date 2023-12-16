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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditTopicActivity extends AppCompatActivity {
    RecyclerView rv;
    EditText topicName;
    EditText term;
    EditText definition;
    FloatingActionButton btn_addWord_Topic;
    ImageView save_btn;
    WordAdapter wadapter;
    ArrayList<Word> words;
    FirebaseFirestore db;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topic);

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.recyclerView_listTopicInFolder);
        btn_addWord_Topic = findViewById(R.id.addWordForTopic_btn_editTopic);
        topicName = findViewById(R.id.topicName_editText_editTopic);
        save_btn = findViewById(R.id.edit_button_listTopicInFolder);

        words = new ArrayList<>();

        wadapter = new WordAdapter(this, words);
        rv.setAdapter(wadapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        topicName.setEnabled(false);
        String editedTopicName = getIntent().getStringExtra("EDITED_TOPIC");
        topicName.setText(editedTopicName);

        loadWord();
        wadapter.notifyDataSetChanged();


        btn_addWord_Topic.setOnClickListener(v -> {
            words.add(new Word());
            wadapter.notifyItemInserted(words.size() - 1);
            rv.scrollToPosition(words.size() - 1);
        });

        save_btn.setOnClickListener(v -> {
            String editTopicName = topicName.getText().toString();

            if (editTopicName.isEmpty()) {
                topicName.setError("Vui lòng nhập tên chủ đề");
            }else {
                EditSelectedWord(editTopicName);
            }
        });
    }

    public void EditSelectedWord(String editTopicName){
        ArrayList<Map<String, Object>> wordsData = new ArrayList<>();
        Topic newTopic = new Topic(editTopicName, String.valueOf(words.size()) + " từ");

        // Duyệt qua từng item trong RecyclerView để lấy dữ liệu từ EditText
        for(int i = 0; i < words.size(); i++){
            Map<String, Object> wordTopic = new HashMap<>();
            wordTopic.put("BackText", words.get(i).getBackText());
            wordTopic.put("FrontText", words.get(i).getFrontText());
            wordsData.add(wordTopic);
        }

        // Thêm dữ liệu vào Firestore
        Map<String, Object> topicData = new HashMap<>();
        topicData.put("topicName", newTopic.getTopicName());
        topicData.put("wordAmount",newTopic.getWordAmount());
        topicData.put("words", wordsData);


        db.collection("topics")
                .whereEqualTo("topicName",editTopicName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();

                            db.collection("topics")
                                    .document(documentID)
                                    .update(topicData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Intent editIntent = new Intent(EditTopicActivity.this, ListTopicActivity.class);
                                            startActivity(editIntent);
                                            Toast.makeText(EditTopicActivity.this,"Chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditTopicActivity.this,"Chỉnh sửa thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    public void saveTopic(){
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
        Topic newTopic = new Topic(topicNameText, String.valueOf(words.size() + " từ"));

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
                        Intent intent = new Intent(EditTopicActivity.this, ListTopicActivity.class);
                        intent.putExtra("topic", newTopic);
                        startActivity(intent);
                        setResult(RESULT_OK);
                        Toast.makeText(EditTopicActivity.this,"Thêm từ cho chủ đề thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditTopicActivity.this,"Thêm từ cho chủ đề thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadWord(){
        db.collection("topics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot maindoc : task.getResult()) {
                                String topicNameDoc = maindoc.getString("topicName");
                                String topicNameText = topicName.getText().toString();
                                if(topicNameDoc.equals(topicNameText)){
                                    words.clear();

                                    List<Map<String, String>> wordList = (List<Map<String, String>>) maindoc.get("words");
                                    for (Map<String, String> word : wordList) {
                                        String frontText = word.get("FrontText");
                                        String backText = word.get("BackText");
                                        words.add(new Word(backText, frontText)); //back trước front sau do trong 'Word.class' bị
                                    }
                                    wadapter.notifyDataSetChanged();
                                    wadapter.setWords(words); // load word on Firestore to words array in adapter
                                    break;
                                }
                            }
                        } else {
                            Log.w("Firestore data", "Lỗi truy cập main document", task.getException());
                        }
                    }
                });
    }
}
