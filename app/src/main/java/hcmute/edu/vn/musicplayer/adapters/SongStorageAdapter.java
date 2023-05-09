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
import hcmute.edu.vn.musicplayer.events.SongListener;
import hcmute.edu.vn.musicplayer.models.Song;

public class SongStorageAdapter extends RecyclerView.Adapter<SongStorageAdapter.MyViewHolder> {
    List<Song> songList;
    SongListener listener;

    public SongStorageAdapter(List<Song> songList, SongListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_storage_item, parent, false);
        return new SongStorageAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.textView.setText(song.getTitle());
        holder.textViewId.setText(String.valueOf(position));
        holder.txtArtistNameList.setText(song.getArtist());
        Glide
                .with(holder.itemView)
                .load(song.getImage())
                .centerCrop()
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSongClickListener(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(songList !=null){
            return songList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView, textViewId, txtArtistNameList;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgViewSong);
            textView = itemView.findViewById(R.id.txtSongTitleList);
            textViewId = itemView.findViewById(R.id.txtID);
            txtArtistNameList = itemView.findViewById(R.id.txtArtistNameList);
        }

    }
}
