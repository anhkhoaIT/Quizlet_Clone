package com.example.cuoikiandroid.Phat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListTopicActivity extends AppCompatActivity {

    RecyclerView rv;
    FloatingActionButton btn_addTopic;
    TopicAdapter topicAdapter;
    ArrayList<Topic>  topics;

    FirebaseFirestore db;

    private static final int ADD_TOPIC_REQUEST = 111;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_topic);

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.myQuizListView);
        btn_addTopic = findViewById(R.id.addTopic_btn);

        topics = new ArrayList<>();

        topicAdapter = new TopicAdapter(this,topics);
        rv.setAdapter(topicAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        loadTopic();

        btn_addTopic.setOnClickListener(v -> {
            Intent intent = new Intent(ListTopicActivity.this, AddWordActivity.class);
            startActivityForResult(intent,ADD_TOPIC_REQUEST);
        });

        registerForContextMenu(rv);
        rv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                getMenuInflater().inflate(R.menu.context_menu,menu);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TOPIC_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("new_topic")) {
                Topic newTopic = data.getParcelableExtra("new_topic");
                topics.add(newTopic);
                topicAdapter.notifyDataSetChanged();
            }
        }
    }

    public void loadTopic(){
        db.collection("topics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Fire store data", document.getId() + " => " + document.getData());
                                topics.add(document.toObject(Topic.class));
                            }
                            topicAdapter.notifyDataSetChanged();
                        }
                        else {
                            Log.w("Fire store data", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // Click option on context menu listener:
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = topicAdapter.getSelectedPosition();
        if(position != RecyclerView.NO_POSITION){
            if(item.getItemId() == R.id.selected_delete){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete this topic ?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteSelectedTopic(position);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
            if(item.getItemId() == R.id.selected_about){

            }
        }
        return true;
    }

    public void DeleteSelectedTopic(int position) {
        if(position != - 1){
            Topic t = topics.get(position);
            String topicID = t.getTopicName();

            db.collection("topics")
                    .whereEqualTo("topicName",topicID)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();
                                db.collection("topics")
                                        .document(documentID)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                topics.remove(position);
                                                topicAdapter.notifyItemRemoved(position);
                                                Toast.makeText(ListTopicActivity.this, "Xóa chủ đề thành công",Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ListTopicActivity.this, "Xóa chủ đề thất bại",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
        }
    }


}
