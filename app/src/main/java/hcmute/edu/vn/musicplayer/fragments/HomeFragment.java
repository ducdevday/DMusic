package hcmute.edu.vn.musicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.musicplayer.services.MyService;
import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.activities.MainActivity;
import hcmute.edu.vn.musicplayer.adapters.TopArtistAdapter;
import hcmute.edu.vn.musicplayer.events.ArtistListener;
import hcmute.edu.vn.musicplayer.events.SongListener;
import hcmute.edu.vn.musicplayer.models.Artist;
import hcmute.edu.vn.musicplayer.models.Song;
import hcmute.edu.vn.musicplayer.adapters.TopSongAdapter;

public class HomeFragment extends Fragment  {
    List<Song> songList = new ArrayList<>();
    TopSongAdapter topSongAdapter;
    RecyclerView topSongsRecycleView;

    List<Artist> artistList = new ArrayList<>();
    TopArtistAdapter topArtistAdapter;
    RecyclerView topArtistsRecycleView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("artist");



    MainActivity mainActivity;
    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TAG", "Home onViewCreated");

        mainActivity = (MainActivity) getActivity();
        topArtistsRecycleView = view.findViewById(R.id.topArtistsRecycleView);
        topArtistsRecycleView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        topSongsRecycleView = view.findViewById(R.id.topSongsRecycleView);
        topSongsRecycleView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!artistList.isEmpty()){
                    artistList.clear();
                }
                if(!songList.isEmpty()){
                    songList.clear();
                }
                for (DataSnapshot singerSnapshot : snapshot.getChildren()) {
                    // TODO: handle the post
                    Artist artist = singerSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                    List<Song> tempSongs = new ArrayList<>();
                    for (DataSnapshot songSnapShot : singerSnapshot.child("songs").getChildren()) {
                        Song song = songSnapShot.getValue(Song.class);
                        tempSongs.add(song);
                    }
                    songList.add(tempSongs.get(0));
                    assert artist != null;
                    artist.setSongList(tempSongs);
                }
                topSongAdapter = new TopSongAdapter(songList, new SongListener() {
                    @Override
                    public void onSongClickListener(Song song) {
                        Intent i = new Intent(view.getContext(), MyService.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("song", song);
                        bundle.putSerializable("songList", (Serializable) songList);
                        i.putExtras(bundle);
                        mainActivity.startService(i);
                    }
                });
                topSongsRecycleView.setAdapter(topSongAdapter);
                topSongAdapter.notifyDataSetChanged();

                topArtistAdapter = new TopArtistAdapter(artistList, new ArtistListener() {
                    @Override
                    public void onArtistClickListener(Artist artist) {
                        Fragment fragment = new ArtistFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("artist", artist);
                        fragment.setArguments(bundle);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction()
                                .replace(R.id.frameLayout, fragment)
                                .commit();

                        mainActivity.findViewById(R.id.frameLayout).setVisibility(View.VISIBLE);
                        mainActivity.findViewById(R.id.viewPager).setVisibility(View.GONE);
                    }
                });
                topArtistsRecycleView.setAdapter(topArtistAdapter);
                topArtistAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
