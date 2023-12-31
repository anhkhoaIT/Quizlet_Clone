package com.example.cuoikiandroid.Phat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuoikiandroid.R;
import java.util.ArrayList;
import com.example.cuoikiandroid.Hung.FlashcardActivity;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.topicViewHolder> {
    Context context;
    ArrayList<Topic> topic;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public TopicAdapter(Context context, ArrayList<Topic> topic) {
        this.context = context;
        this.topic = topic;
    }

    @NonNull
    @Override
    public topicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new topicViewHolder(LayoutInflater.from(context).inflate(R.layout.topic_item,parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull topicViewHolder holder, int position) {
        holder.topicName.setText(topic.get(position).getTopicName());
        holder.wordAmount.setText(topic.get(position).getWordAmount());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setSelectedPosition(holder.getAdapterPosition());
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topic selectedTopic = topic.get(holder.getAdapterPosition());
                String topicName = selectedTopic.getTopicName();

                Intent intent = new Intent(context, FlashcardActivity.class);
                intent.putExtra("topicName", topicName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topic.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public class topicViewHolder extends RecyclerView.ViewHolder {
        TextView topicName, wordAmount;
        public topicViewHolder(@NonNull View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.topicName);
            wordAmount = itemView.findViewById(R.id.wordAmount);
        }
    }
}
