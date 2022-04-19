package com.codepath.apps.restclienttemplate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.GameDetailFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ProfileGameHistoryItemBinding;
import com.codepath.apps.restclienttemplate.databinding.SelfProfileGameHistoryItemBinding;
import com.codepath.apps.restclienttemplate.interfaces.MainActivityNavigator;
import com.codepath.apps.restclienttemplate.models.Game;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileGameHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ParseUser user;
    private final ArrayList<Game> games;
    private final MainActivityNavigator mainActivityNavigator;

    private static final int SELF = 0;
    private static final int OTHER = 1;

    private interface GameBiddableViewHolder {
        void bind(Game game);
    }


    public ProfileGameHistoryAdapter(ParseUser user, ArrayList<Game> games, MainActivityNavigator mainActivityNavigator) {
        this.user = user;
        this.games = games;
        this.mainActivityNavigator = mainActivityNavigator;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        switch (viewType) {
            case SELF:
                itemView = inflater.inflate(
                        R.layout.self_profile_game_history_item,
                        parent,
                        false
                );
                viewHolder = new SelfProfileGameHistoryItem(itemView);
                break;
            default:
                itemView = inflater.inflate(
                        R.layout.profile_game_history_item,
                        parent,
                        false
                );
                viewHolder = new OtherProfileGameHistoryItem(itemView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GameBiddableViewHolder gameBiddableViewHolder = (GameBiddableViewHolder) holder;
        gameBiddableViewHolder.bind(games.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return ParseUser.getCurrentUser() == user ? SELF : OTHER;
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class SelfProfileGameHistoryItem extends RecyclerView.ViewHolder implements GameBiddableViewHolder {
        final SelfProfileGameHistoryItemBinding binding;

        public SelfProfileGameHistoryItem(@NonNull View itemView) {
            super(itemView);
            binding = SelfProfileGameHistoryItemBinding.bind(itemView);
        }

        @Override
        public void bind(Game game) {
            binding.setGame(game);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivityNavigator.requestOverlay(GameDetailFragment.newInstance(game));
                }
            });
        }
    }

    public class OtherProfileGameHistoryItem extends RecyclerView.ViewHolder implements GameBiddableViewHolder {
        final ProfileGameHistoryItemBinding binding;

        public OtherProfileGameHistoryItem(@NonNull View itemView) {
            super(itemView);
            binding = ProfileGameHistoryItemBinding.bind(itemView);
        }

        @Override
        public void bind(Game game) {
            binding.setGame(game);
        }
    }
}
