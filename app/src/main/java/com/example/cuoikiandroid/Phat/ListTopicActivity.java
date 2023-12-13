package com.example.cuoikiandroid.Phat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuoikiandroid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListTopicActivity extends AppCompatActivity {

    RecyclerView rv;
    FloatingActionButton btn_addTopic;
    TopicAdapter topicAdapter;
    ArrayList<Topic>  topicItems;

    private static final int ADD_TOPIC_REQUEST = 111;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_topic);

        rv = findViewById(R.id.myQuizListView);
        btn_addTopic = findViewById(R.id.addTopic_btn);

        topicItems = new ArrayList<>();

        topicAdapter = new TopicAdapter(this,topicItems);
        rv.setAdapter(topicAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        btn_addTopic.setOnClickListener(v -> {
            Intent intent = new Intent(ListTopicActivity.this, AddWordActivity.class);
            startActivityForResult(intent,ADD_TOPIC_REQUEST);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TOPIC_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("new_topic")) {
                Topic newTopic = data.getParcelableExtra("new_topic");
                topicItems.add(newTopic);
                topicAdapter.notifyDataSetChanged();
            }
        }
    }
}
