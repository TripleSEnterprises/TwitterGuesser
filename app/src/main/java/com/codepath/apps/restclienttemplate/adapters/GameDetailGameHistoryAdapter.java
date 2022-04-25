package com.codepath.apps.restclienttemplate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.GameDetailQuestionItemBinding;
import com.codepath.apps.restclienttemplate.models.GameDeserialized;

public class GameDetailGameHistoryAdapter extends RecyclerView.Adapter<GameDetailGameHistoryAdapter.GameDetailQuestionItem> {
    private final GameDeserialized gameDeserialized;

    public GameDetailGameHistoryAdapter(GameDeserialized gameDeserialized) {
        this.gameDeserialized = gameDeserialized;
    }

    @NonNull
    @Override
    public GameDetailQuestionItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.game_detail_question_item, parent, false);
        return new GameDetailQuestionItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameDetailQuestionItem holder, int position) {
        holder.bind(gameDeserialized.getQuestion(position));
    }

    @Override
    public int getItemCount() {
        return gameDeserialized.questionCount();
    }

    public class GameDetailQuestionItem extends RecyclerView.ViewHolder {
        GameDetailQuestionItemBinding binding;

        public GameDetailQuestionItem(@NonNull View itemView) {
            super(itemView);
            binding = GameDetailQuestionItemBinding.bind(itemView);
        }

        public void bind(GameDeserialized.Question question) {
            binding.setQuestion(question);
            binding.setTweet(question.getTweet());
        }
    }
}
