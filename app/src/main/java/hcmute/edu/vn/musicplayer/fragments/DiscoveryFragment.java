package hcmute.edu.vn.musicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.musicplayer.activities.UploadActivity;
import hcmute.edu.vn.musicplayer.services.DownloadService;
import hcmute.edu.vn.musicplayer.services.MyService;
import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.activities.MainActivity;
import hcmute.edu.vn.musicplayer.adapters.SongDiscoveryAdapter;
import hcmute.edu.vn.musicplayer.events.DownloadListener;
import hcmute.edu.vn.musicplayer.events.SongListener;
import hcmute.edu.vn.musicplayer.models.Song;

public class DiscoveryFragment extends Fragment  {
    RecyclerView recyclerView;
    SongDiscoveryAdapter songDiscoveryAdapter;
    List<Song> songList = new ArrayList<>();
    MainActivity mainActivity;
    MaterialToolbar topToolbar;
    MenuItem uploadMenuItem;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://musicplayer-b04ab-default-rtdb.firebaseio.com/");
    DatabaseReference myRef = database.getReference("discovery");

    public DiscoveryFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TAG", "Discovery onViewCreated");

        mainActivity =(MainActivity) getActivity();
        topToolbar = view.findViewById(R.id.top_toolbar);
        uploadMenuItem = topToolbar.getMenu().findItem(R.id.btnTopBarUpload);
        recyclerView = view.findViewById(R.id.song_list_recycler_view);

        uploadMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Xử lý khi menu item "Upload" được click
                Intent i = new Intent(mainActivity, UploadActivity.class);
                startActivity(i);
                return true; // Trả về true để cho biết sự kiện đã được xử lý
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Xóa đi list cũ để cập nhật list mới
                if(!songList.isEmpty()){
                    songList.clear();
                }
                for (DataSnapshot songSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    Song song = songSnapshot.getValue(Song.class);
                    songList.add(song);
                }
                songDiscoveryAdapter = new SongDiscoveryAdapter(songList, new SongListener() {
                    @Override
                    public void onSongClickListener(Song song) {
                        Log.e("TAG", "onSongClickListener");
                        Intent i = new Intent(view.getContext(), MyService.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("song", song);
                        bundle.putSerializable("songList", (Serializable) songList);
                        i.putExtras(bundle);
                        mainActivity.startService(i);
                    }
                }, new DownloadListener() {
                    @Override
                    public void onDownloadClickListener(Song song) {
                        Log.e("TAG", "onDownloadClickListener");
                        Intent intent = new Intent(view.getContext(), DownloadService.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("song", song);
                        intent.putExtras(bundle);
                        mainActivity.startService(intent);
                    }
                });
                recyclerView.setAdapter(songDiscoveryAdapter);
                songDiscoveryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
