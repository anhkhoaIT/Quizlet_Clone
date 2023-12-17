package com.example.cuoikiandroid.Hung;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuoikiandroid.R;
import java.util.ArrayList;
import java.util.Locale;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder>{

    Context mcontext;
    ArrayList<TypeWord> typeList;
    TextToSpeech audioForSelectedOption;
    public TypeAdapter(Context mcontext, ArrayList<TypeWord> typeList) {
        this.mcontext = mcontext;
        this.typeList = typeList;

        audioForSelectedOption = new TextToSpeech(mcontext, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Chọn ngôn ngữ
                audioForSelectedOption.setLanguage(Locale.ENGLISH);
            } else {
                Toast.makeText(mcontext, "Thực hiện phát âm thanh không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TypeViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.typing_word_layout,parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        holder.question.setText(typeList.get(position).getQuestion());
        holder.typing_word.setText(typeList.get(position).getTyping());
        holder.audio_test.setOnClickListener(v -> {
            String textFromTyping = holder.typing_word.getText().toString();
            audioForSelectedOption.speak(textFromTyping, TextToSpeech.QUEUE_FLUSH, null, null);
        });

        holder.typing_word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                typeList.get(holder.getAdapterPosition()).setTyping(s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }
    public class TypeViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        EditText typing_word;
        ImageView audio_test;
        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.vn_word);
            typing_word = itemView.findViewById(R.id.type_en_word);
            audio_test = itemView.findViewById(R.id.audio_test);
        }

    }
}