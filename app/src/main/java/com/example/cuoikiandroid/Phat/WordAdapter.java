package com.example.cuoikiandroid.Phat;

import android.content.Context;
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
    ArrayList<Word> Words;
    private int selectedPosition = RecyclerView.NO_POSITION;
    public WordAdapter(Context context, ArrayList<Word> Words) {
        this.context = context;
        this.Words = Words;
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
        holder.term.setText(Words.get(position).getFrontText());
        holder.definition.setText(Words.get(position).getBackText());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setSelectedPosition(holder.getAdapterPosition());
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return Words.size();
    }

    public class wordViewHolder extends RecyclerView.ViewHolder {
        TextView term, definition;
        public wordViewHolder(@NonNull View itemView) {
            super(itemView);
            term = itemView.findViewById(R.id.term_add_word);
            definition = itemView.findViewById(R.id.definition_add_word);


        }

    }

}
