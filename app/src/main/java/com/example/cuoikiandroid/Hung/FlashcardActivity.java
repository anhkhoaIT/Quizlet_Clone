package com.example.cuoikiandroid.Hung;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FlashcardActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ViewPager vpager;
    FlashcardAdapter flashcardAdapter;
    ArrayList<Flashcard> wordlist;
    ImageView back,next, shuffle, audio;
    TextView count_order, topic_title_flashcard;
    private int page = 0;
    private boolean isShuffle = false;
    TextToSpeech audioForText;
    Button test_quiz, test_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = FirebaseFirestore.getInstance();

        vpager = findViewById(R.id.viewPager);
        wordlist = new ArrayList<>();

        flashcardAdapter = new FlashcardAdapter(this,wordlist);
        vpager.setAdapter(flashcardAdapter);
        flashcardAdapter.setTextToSpeech(audioForText);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        shuffle = findViewById(R.id.shuffle);
        audio = findViewById(R.id.audio);
        count_order = findViewById(R.id.count_order);
        topic_title_flashcard = findViewById(R.id.topic_title_flash_card);

        test_quiz = findViewById(R.id.btn_test_quiz);
        test_type = findViewById(R.id.btn_test_type);

        // Lấy tên từ chủ đề đã nhấn vào
        String topicName = getIntent().getStringExtra("topicName");
        topic_title_flashcard.setText(topicName);

        loadWord();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpager.setCurrentItem(getWord(-1),true);
                flashcardAdapter.notifyDataSetChanged();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpager.setCurrentItem(getWord(+1),true);
                flashcardAdapter.notifyDataSetChanged();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShuffle){
                    origin();
                    Toast.makeText(FlashcardActivity.this, "Trộn thẻ: TẮT", Toast.LENGTH_SHORT).show();
                }else{
                    shuffle();
                    Toast.makeText(FlashcardActivity.this, "Trộn thẻ: BẬT", Toast.LENGTH_SHORT).show();
                }
            }
        });

        audioForText = new TextToSpeech(this, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Chọn ngôn ngữ
                    audioForText.setLanguage(Locale.ENGLISH);
                } else {
                    Toast.makeText(FlashcardActivity.this, "Thực hiện phát âm thanh không thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = vpager.getCurrentItem();
                flashcardAdapter.speakFrontText(currentPage);
            }
        });

        vpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                page = position + 1;
                countOrderCard();
            }
            @Override
            public void onPageSelected(int position) {
                View view = vpager.getChildAt(position);
                if (view != null) {
                    TextView front_card = view.findViewById(R.id.front_card);
                    TextView back_card = view.findViewById(R.id.back_card);
                    front_card.setAlpha(1.0f);
                    back_card.setAlpha(0.0f);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        test_quiz.setOnClickListener(v -> {
            Intent intent = new Intent(FlashcardActivity.this, StartQuizActivity.class);
            intent.putExtra("topic_name",topic_title_flashcard.getText().toString());
            intent.putExtra("question_amount", String.valueOf(wordlist.size()));
            startActivity(intent);
        });

        test_type.setOnClickListener( v -> {
            Intent intent = new Intent(FlashcardActivity.this, StartTypeActivity.class);
            intent.putExtra("topic_name",topic_title_flashcard.getText().toString());
            intent.putExtra("question_amount", String.valueOf(wordlist.size()));
            startActivity(intent);
        });
    }

    // Khi tắt app, hủy tính năng phát âm thanh
    @Override
    protected void onDestroy() {
        if (audioForText != null) {
            audioForText.stop();
            audioForText.shutdown();
        }
        super.onDestroy();
    }

    public int getWord(int position){
        return vpager.getCurrentItem() + position;
    }
    public void countOrderCard(){
        count_order.setText( page + "/" + wordlist.size() );
    }

    public void origin(){
        // Dùng ArrayList gốc để hiển thị danh sách mặc định
        int currentPage = vpager.getCurrentItem();
        flashcardAdapter = new FlashcardAdapter(this, wordlist);
        vpager.setAdapter(flashcardAdapter);
        // Phát âm thanh khi tắt trộn thẻ
        flashcardAdapter.setTextToSpeech(audioForText);
        flashcardAdapter.notifyDataSetChanged();

        vpager.setCurrentItem(currentPage, true);
        isShuffle = false;
        countOrderCard();
    }
    public void shuffle(){
        // Lưu trang hiện tại đang ở
        int currentPage = vpager.getCurrentItem();
        // ArrayList phục vụ cho trộn từ danh sách gốc
        ArrayList<Flashcard> shuffleList = new ArrayList<>();
        shuffleList.addAll(wordlist);
        // Sử dụng trong việc xáo trộn đảm bảo tính ngẫu nhiên không trùng lặp.
        long seed = System.nanoTime();
        Collections.shuffle(shuffleList, new Random(seed));

        flashcardAdapter = new FlashcardAdapter(this, shuffleList);
        vpager.setAdapter(flashcardAdapter);
        // Phát âm thanh khi bật trộn thẻ
        flashcardAdapter.setTextToSpeech(audioForText);
        flashcardAdapter.notifyDataSetChanged();

        // Sau trộn sẽ vẫn ở vị trí trang đã lưu với item đã được trộn
        vpager.setCurrentItem(currentPage, true);
        isShuffle = true;
        countOrderCard();
    }

    public void loadWord(){
        db.collection("topics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot maindoc : task.getResult()) {
                                String topicName = topic_title_flashcard.getText().toString();
                                String topicNameDoc = maindoc.getString("topicName");
                                if(topicNameDoc.equals(topicName)){
                                    wordlist.clear();

                                    List<Map<String, String>> words = (List<Map<String, String>>) maindoc.get("words");
                                    for (Map<String, String> word : words) {
                                        String frontText = word.get("FrontText");
                                        String backText = word.get("BackText");
                                        wordlist.add(new Flashcard(frontText, backText));
                                    }
                                    flashcardAdapter.notifyDataSetChanged();
                                    flashcardAdapter.setTextToSpeech(audioForText);
                                    break;

                                }

                            }
                        } else {
                            Log.w("Firestore data", "Lỗi truy cập document chính", task.getException());
                        }
                    }
                });
    }
}