package com.codepath.apps.restclienttemplate.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.ProfileFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.LeaderboardTopPlayerItemBinding;
import com.codepath.apps.restclienttemplate.interfaces.MainActivityOverlay;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardPlayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final ArrayList<ParseUser> users;
    public static final String TAG = "LeaderboardAdapter";
    public MainActivityOverlay mainActivityOverlay;

    private interface UserBiddableViewHolder {
        void bind(ParseUser user);
    }

    public LeaderboardPlayersAdapter(ArrayList<ParseUser> users, MainActivityOverlay mainActivityOverlay) {
        this.users = users;
        this.mainActivityOverlay = mainActivityOverlay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.leaderboard_top_player_item,parent,false);
        return new OtherTopPlayerItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserBiddableViewHolder userBiddableViewHolder = (UserBiddableViewHolder) holder;
        userBiddableViewHolder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear(){
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ParseUser> newUsers){
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    public class OtherTopPlayerItem extends RecyclerView.ViewHolder implements UserBiddableViewHolder {
        final LeaderboardTopPlayerItemBinding binding;

        public OtherTopPlayerItem(@NonNull View itemView){
            super(itemView);
            binding = LeaderboardTopPlayerItemBinding.bind(itemView);
        }

        @Override
        public void bind(ParseUser user) {
            binding.setUser(user);
            binding.llPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mainActivityOverlay.requestOverlay(ProfileFragment.newInstance(user,mainActivityOverlay));
                        }
                    });
                }
            });
        }
    }

}
