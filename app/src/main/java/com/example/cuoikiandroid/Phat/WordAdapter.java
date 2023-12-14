package com.example.cuoikiandroid.Phat;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuoikiandroid.R;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.wordViewHolder> {
    Context context;
    ArrayList<Word> words;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public WordAdapter(Context context, ArrayList<Word> Words) {
        this.context = context;
        this.words = Words;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @NonNull
    @Override
    public wordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new wordViewHolder(LayoutInflater.from(context).inflate(R.layout.add_word,parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull wordViewHolder holder, int position) {

        Word word = words.get(position);

        // Set data to EditText fields in your ViewHolder
        holder.term.setText(word.getFrontText());
        holder.definition.setText(word.getBackText());

        holder.term.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                words.get(holder.getAdapterPosition()).setFrontText(s.toString());
            }
        });

        holder.definition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                words.get(holder.getAdapterPosition()).setBackText(s.toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class wordViewHolder extends RecyclerView.ViewHolder {
        TextView term, definition;
        public wordViewHolder(@NonNull View itemView) {
            super(itemView);
            term = itemView.findViewById(R.id.term_add_word);
            definition = itemView.findViewById(R.id.definition_add_word);
        }
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
        notifyDataSetChanged();
    }
}
