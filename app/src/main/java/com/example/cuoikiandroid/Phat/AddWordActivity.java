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
    ArrayList<Word> Words;

    FirebaseFirestore db;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.word_recyclerView);
        btn_addWord_Topic = findViewById(R.id.addWordForTopic_btn);
        topicName = findViewById(R.id.topicName_editText);
        confirm_add_topic = findViewById(R.id.confirm_add_topic);

        Words = new ArrayList<>();

        wadapter = new WordAdapter(this, Words);
        rv.setAdapter(wadapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        btn_addWord_Topic.setOnClickListener(v -> {
            Words.add(new Word());
            wadapter.notifyItemInserted(Words.size() - 1);
            rv.scrollToPosition(Words.size() - 1);
        });

        confirm_add_topic.setOnClickListener(v -> {
            comfirm_addTopic();
        });
    }

    public void comfirm_addTopic(){
        Topic newTopic = new Topic(topicName.getText().toString(),String.valueOf(Words.size()));
        Intent intent = new Intent(AddWordActivity.this, ListTopicActivity.class);
        intent.putExtra("new_topic",newTopic);
        startActivity(intent);
        setResult(RESULT_OK);
        finish();

//        term = findViewById(R.id.term_add_word);
//        definition = findViewById(R.id.definition_add_word);

//        Map<String, Object> word = new HashMap<>();
//        word.put("term", term.getText().toString());
//        word.put("definition", definition.getText().toString());
//
//
//        Word newWord = new Word(
//                newWord.get("name").toString(),
//                newWord.get("id").toString(),
//                newWord.get("email").toString(),
//                newWord.get("phone").toString()
//        );
//
//        db.collection("topic")
//                .add(newWord)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("Add Word", "DocumentSnapshot added with ID: " + documentReference.getId());
//                        Intent intent = new Intent(AddWordActivity.this, ListTopicActivity.class);
//                        intent.putExtra("Word", newWord);
//                        startActivity(intent);
//                        setResult(RESULT_OK);
//                        Toast.makeText(AddWordActivity.this,"Thêm từ thành công", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(AddWordActivity.this,"Thêm từ vựng thất bại", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }


}
