package edu.uga.cs.countryquiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter for displaying past quizzes in PastQuizzesActivity.java
 */
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private List<Quiz> quizzes;

    public QuizAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, score;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateText);
            score = itemView.findViewById(R.id.scoreText);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.date.setText(quiz.getDate());
        holder.score.setText(String.valueOf(quiz.getResult()));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }
}
