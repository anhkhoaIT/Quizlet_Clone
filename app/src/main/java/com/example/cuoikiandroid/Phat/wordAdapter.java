package com.example.cuoikiandroid.Phat;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuoikiandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class wordAdapter extends RecyclerView.Adapter<wordAdapter.wordViewHolder> {
    Context context;
    ArrayList<word> words;
    private int selectedPosition = RecyclerView.NO_POSITION;
    public wordAdapter(Context context, ArrayList<word> words) {
        this.context = context;
        this.words = words;
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
        holder.term.setText(words.get(position).getFrontText());
        holder.definition.setText(words.get(position).getBackText());

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

}
