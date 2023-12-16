package com.example.cuoikiandroid.Phat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.internal.EdgeToEdgeUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListFolderActivity extends AppCompatActivity {
    RecyclerView rv;
    FloatingActionButton addFolder_btn;
    FolderAdapter folderAdapter;
    ArrayList<Folder> folders;
    FirebaseFirestore db;
    TextView folderName;
    private static final int ADD_TOPIC_REQUEST = 111;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_folder);

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.folderListView);
        addFolder_btn = findViewById(R.id.createFolder_btn);

        folders = new ArrayList<>();

        folderAdapter = new FolderAdapter(this,folders);
        rv.setAdapter(folderAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayout);

        loadFolder();

        addFolder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog createFolderDiaglog = new Dialog(ListFolderActivity.this);
                createFolderDiaglog.setTitle("Tạo thư mục");
                createFolderDiaglog.setContentView(R.layout.diaglog_add_folder);
                createFolderDiaglog.setCancelable(false);

                // Use createFolderDiaglog.findViewById here
                EditText diaglogFolderName = createFolderDiaglog.findViewById(R.id.editTextFolderName);
                Button okbtn = createFolderDiaglog.findViewById(R.id.buttonOK);
                Button cancelBtn = createFolderDiaglog.findViewById(R.id.buttonCancel);

                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Folder newFolder = new Folder(diaglogFolderName.getText().toString());
                        folders.add(newFolder);
                        folderAdapter.notifyDataSetChanged();
                        createFolderDiaglog.cancel();

                        Map<String, Object> folderData = new HashMap<>();
                        folderData.put("folderName", newFolder.getFolderName());

                        db.collection("folders")
                                .add(folderData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(ListFolderActivity.this,"Tạo thư mục thành công", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ListFolderActivity.this,"Tạo thư mục thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createFolderDiaglog.cancel();
                    }
                });
                createFolderDiaglog.show();
            }
        });

        registerForContextMenu(rv);

        rv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                getMenuInflater().inflate(R.menu.context_menu,menu);
            }
        });
    }

//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == ADD_TOPIC_REQUEST && resultCode == RESULT_OK) {
//            if (data != null && data.hasExtra("new_topic")) {
//                Topic newTopic = data.getParcelableExtra("new_topic");
//                folders.add(newTopic);
//                topicAdapter.notifyDataSetChanged();
//            }
//        }
//    }

    public void loadFolder(){
        db.collection("folders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Fire store data", document.getId() + " => " + document.getData());
                                folders.add(document.toObject(Folder.class));
                            }
                            folderAdapter.notifyDataSetChanged();
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
        int position = folderAdapter.getSelectedPosition();
        if(position != RecyclerView.NO_POSITION){
            if(item.getItemId() == R.id.selected_delete){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete this folder?");
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
            if(item.getItemId() == R.id.selected_edit){
                EditSelectedTopic(position);
            }
        }
        return true;
    }

    public void DeleteSelectedTopic(int position) {
        if(position != - 1){
            Folder f = folders.get(position);
            String folderID = f.getFolderName();

            db.collection("folders")
                    .whereEqualTo("folderName",folderID)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();
                                db.collection("folders")
                                        .document(documentID)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                folders.remove(position);
                                                folderAdapter.notifyItemRemoved(position);
                                                Toast.makeText(ListFolderActivity.this, "Xóa thư mục thành công",Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ListFolderActivity.this, "Xóa thư mục thất bại",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    public void EditSelectedTopic(int position) {
//        Intent intent = new Intent(ListTopicActivity.this, EditTopicActivity.class);
//        Topic t = topics.get(position);
//        String topicName = t.getTopicName();
//        intent.putExtra("EDITED_TOPIC", topicName);
//        startActivity(intent);
    }
}
