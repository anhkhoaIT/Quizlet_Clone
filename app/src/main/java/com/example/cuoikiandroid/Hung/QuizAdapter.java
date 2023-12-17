package com.example.cuoikiandroid.Hung;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.cuoikiandroid.Phat.WordAdapter;
import com.example.cuoikiandroid.R;

import java.util.ArrayList;
import java.util.Locale;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    Context mcontext;
    ArrayList<Quiz> quizzes;
    private SparseArray<Button> selectedOptions = new SparseArray<>();
    private Button selectedButton;
    TextToSpeech audioForSelectedOption;
    public QuizAdapter(Context mcontext, ArrayList<Quiz> quizzes) {
        this.mcontext = mcontext;
        this.quizzes = quizzes;

        audioForSelectedOption = new TextToSpeech(mcontext, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Chọn ngôn ngữ
                audioForSelectedOption.setLanguage(Locale.ENGLISH);
            } else {
                Toast.makeText(mcontext, "Thực hiện phát âm thanh không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuizViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.question_layout,parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAdapter.QuizViewHolder holder, int position) {
        holder.question.setText(quizzes.get(position).getQuestion());
        holder.first_option.setText(quizzes.get(position).getA());
        holder.second_option.setText(quizzes.get(position).getB());
        holder.third_option.setText(quizzes.get(position).getC());
        holder.fourth_option.setText(quizzes.get(position).getD());

        holder.first_option.setOnClickListener(v -> {
            selectedButton = holder.first_option;
            selectedOption(holder.first_option, 1, position);
        });

        holder.second_option.setOnClickListener(v -> {
            selectedButton = holder.second_option;
            selectedOption(holder.second_option, 2, position);
        });

        holder.third_option.setOnClickListener(v -> {
            selectedButton = holder.third_option;
            selectedOption(holder.third_option, 3, position);
        });

        holder.fourth_option.setOnClickListener(v -> {
            selectedButton = holder.fourth_option;
            selectedOption(holder.fourth_option, 4, position);
        });

        holder.audio_test.setOnClickListener(v -> {
            if (selectedButton != null) {
                String textToRead = selectedButton.getText().toString();
                audioForSelectedOption.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        Button first_option, second_option, third_option, fourth_option;
        ImageView audio_test;
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.content_question);
            first_option = itemView.findViewById(R.id.option_A);
            second_option = itemView.findViewById(R.id.option_B);
            third_option = itemView.findViewById(R.id.option_C);
            fourth_option = itemView.findViewById(R.id.option_D);
            audio_test = itemView.findViewById(R.id.audio_test);
        }

    }

    // Thay đổi màu nhận diện cho đáp án khi nhấn chọn
    public void selectedOption(Button b, int num_option, int quesPosition){

        Button preselectedButton = selectedOptions.get(quesPosition);
        // Lần chọn đáp án trước sẽ chuyển thành không chọn nữa
        if(preselectedButton != null){
            preselectedButton.setBackgroundResource(R.drawable.custom_stroke_btn);
        }

        // Lần chọn đáp án hiện tại sẽ được nhận diện đuợc chọn
        b.setBackgroundResource(R.drawable.selected_btn);
        selectedOptions.put(quesPosition,b);

        quizzes.get(quesPosition).setSelectedOption(num_option);

    }

}