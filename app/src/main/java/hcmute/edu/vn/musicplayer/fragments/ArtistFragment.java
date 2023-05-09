package hcmute.edu.vn.musicplayer.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;

import hcmute.edu.vn.musicplayer.NetworkChangeReceiver;
import hcmute.edu.vn.musicplayer.events.NetworkChangeListener;
import hcmute.edu.vn.musicplayer.services.MyService;
import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.activities.MainActivity;
import hcmute.edu.vn.musicplayer.adapters.SongArtistAdapter;
import hcmute.edu.vn.musicplayer.events.SongListener;
import hcmute.edu.vn.musicplayer.models.Artist;
import hcmute.edu.vn.musicplayer.models.Song;

public class ArtistFragment extends Fragment implements SongListener, NetworkChangeListener {
    ImageView imageViewArtist;
    TextView textViewName, textViewBio;

    RecyclerView recyclerView;
    SongArtistAdapter songArtistAdapter;
    ImageButton btnBack;

    Artist artist;

    MainActivity mainActivity;

    private NetworkChangeReceiver networkChangeReceiver;

    public ArtistFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TAG", "Artist onViewCreated");

        initView(view);
        fetchData(view);

        networkChangeReceiver =new NetworkChangeReceiver();
        mainActivity.registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        networkChangeReceiver.addListener(this);
    }

    private void fetchData(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            artist = (Artist) bundle.getSerializable("artist");
            Log.e("TAG", artist.toString());
        }
        Glide.with(view.getContext())
                .load(artist.getImage())
                .centerCrop()
                .into(imageViewArtist);
        imageViewArtist.setImageResource(R.drawable.img_cover);
        textViewName.setText(artist.getName());
        textViewBio.setText(artist.getBio());
        songArtistAdapter = new SongArtistAdapter(artist.getSongList(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(songArtistAdapter);
    }

    public void initView( View view){
        imageViewArtist = view.findViewById(R.id.imgArtistCover);
        textViewName = view.findViewById(R.id.txtArtistBigNameList);
        textViewBio = view.findViewById(R.id.txtArtistBioList);
        recyclerView = view.findViewById(R.id.song_list_recycler_view);
        btnBack = view.findViewById(R.id.btnBack);
        mainActivity = (MainActivity) getActivity();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });
    }

    @Override
    public void onSongClickListener(Song song) {
        Intent i = new Intent(getContext(), MyService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);
        bundle.putSerializable("songList", (Serializable) artist.getSongList());
        i.putExtras(bundle);
        mainActivity.startService(i);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if(!isConnected){
            mainActivity.onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.unregisterReceiver(networkChangeReceiver);
    }
}