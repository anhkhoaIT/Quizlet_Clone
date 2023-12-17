package com.example.cuoikiandroid.Hung;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.cuoikiandroid.R;

import java.util.ArrayList;
public class FlashcardAdapter extends PagerAdapter {
    Context mcontext;
    ArrayList<Flashcard> flashcards;
    AnimatorSet front_animate, back_animate;
    TextToSpeech audioForText;
    public FlashcardAdapter(Context mcontext, ArrayList<Flashcard> flashcards) {
        this.mcontext = mcontext;
        this.flashcards = flashcards;
    }

    public void setTextToSpeech(TextToSpeech audioForText) {
        this.audioForText = audioForText;
    }

    @Override
    public int getCount() {
        return flashcards.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.flashcard_layout,container,false);

        front_animate = (AnimatorSet) AnimatorInflater.loadAnimator(mcontext, R.animator.frontcard_animator);
        back_animate = (AnimatorSet) AnimatorInflater.loadAnimator(mcontext, R.animator.backcard_animator);

        TextView front_card = view.findViewById(R.id.front_card);
        TextView back_card = view.findViewById(R.id.back_card);
        CardView cv = view.findViewById(R.id.flash_card);

        front_card.setText(flashcards.get(position).getFrontText());
        back_card.setText(flashcards.get(position).getBackText());

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip(front_card,back_card,cv);
            }
        });
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    public void flip( TextView front_card,TextView back_card,CardView cv){
        if(front_card.getAlpha() != 0){
            // Nếu đang ở mặt trước, chạy hoạt ảnh lật sang mặt sau
            front_animate.setTarget(front_card);
            back_animate.setTarget(back_card);
            front_animate.start();
            back_animate.start();
            ObjectAnimator flipCard = ObjectAnimator.ofFloat(cv, "rotationX", 180, 0);
            flipCard.setDuration(500);
            flipCard.start();
        }
        else {
            // Nếu đang ở mặt sau, chạy hoạt ảnh lật sang mặt trước
            front_animate.setTarget(back_card);
            back_animate.setTarget(front_card);
            front_animate.start();
            back_animate.start();
            ObjectAnimator flipCard = ObjectAnimator.ofFloat(cv, "rotationX", 180, 0);
            flipCard.setDuration(500);
            flipCard.start();
        }

    }

    public void speakFrontText(int position) {
        if (audioForText != null) {
            audioForText.speak(flashcards.get(position).getFrontText(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

}