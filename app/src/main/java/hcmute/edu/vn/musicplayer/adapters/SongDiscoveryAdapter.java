package hcmute.edu.vn.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.events.DownloadListener;
import hcmute.edu.vn.musicplayer.events.SongListener;
import hcmute.edu.vn.musicplayer.models.Song;

public class SongDiscoveryAdapter extends RecyclerView.Adapter<SongDiscoveryAdapter.MyViewHolder> {
    List<Song> songList;
    SongListener listener;
    DownloadListener downloadListener;



    public SongDiscoveryAdapter(List<Song> songList, SongListener songListener,DownloadListener downloadListener ) {
        this.songList = songList;
        this.listener = songListener;
        this.downloadListener =  downloadListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.textViewSong.setText(song.getTitle());
        holder.textViewArtist.setText(song.getArtist());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSongClickListener(song);
            }
        });
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadListener.onDownloadClickListener(song);
            }
        });
        Glide
                .with(holder.itemView)
                .load(song.getImage())
                .centerCrop()
                .into(holder.imageView);
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
        TextView textViewSong, textViewArtist;
        ImageButton btnDownload;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgViewSong);
            textViewSong = itemView.findViewById(R.id.txtSongTitleList);
            textViewArtist = itemView.findViewById(R.id.txtArtistNameList);
            btnDownload = itemView.findViewById(R.id.btnDownloadSong);
        }
    }
}
