package hcmute.edu.vn.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.events.ArtistListener;
import hcmute.edu.vn.musicplayer.models.Artist;

public class TopArtistAdapter extends RecyclerView.Adapter<TopArtistAdapter.MyViewHolder> {
    List<Artist> artistList;
    ArtistListener listener;
    public TopArtistAdapter(List<Artist> artistList, ArtistListener artistListener) {
        this.artistList = artistList;
        this.listener = artistListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item_home, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.textView.setText(artist.getName());
        Glide
                .with(holder.itemView)
                .load(artist.getImage())
                .centerCrop()
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArtistClickListener(artist);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(artistList != null)
            return artistList.size();
        return 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgArtistHome);
            textView = itemView.findViewById(R.id.txtArtistHome);
        }
    }

}
