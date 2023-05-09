package hcmute.edu.vn.musicplayer.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.activities.MainActivity;
import hcmute.edu.vn.musicplayer.adapters.SongDiscoveryAdapter;
import hcmute.edu.vn.musicplayer.adapters.SongStorageAdapter;
import hcmute.edu.vn.musicplayer.events.SongListener;
import hcmute.edu.vn.musicplayer.models.Song;
import hcmute.edu.vn.musicplayer.services.MyService;


public class StorageFragment extends Fragment {
    RecyclerView recyclerView;
    SongStorageAdapter songStorageAdapter;
    List<Song> songList;
    MainActivity mainActivity;

    List<String> imageUrls;
    List<String> resourceUrls;
    List<String> titleList;
    List<String> artistList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_storage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("TAG", "Storage onViewCreated");

        songList = new ArrayList<>();
        imageUrls = new ArrayList<>();
        resourceUrls= new ArrayList<>();
        titleList= new ArrayList<>();
        artistList= new ArrayList<>();

        // Tạo đối tượng File cho thư mục Download
        File downloadDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/MusicPlayer/");

        // Lấy danh sách tất cả các tập tin trong thư mục Download
        File[] files = downloadDirectory.listFiles();

        // Lặp qua tất cả các tập tin và kiểm tra nếu tập tin là MP3, chuyển đổi thành URL và thêm vào mảng mp3Urls
        if(files != null){
            for (File file : files) {
                if (file.getName().endsWith(".mp3")) {
                    try {
                        URL url = file.toURI().toURL();
                        resourceUrls.add(url.toString());
                        String[] temp = file.getName().split("-");
                        titleList.add(temp[0].replaceAll("_"," "));
                        artistList.add(temp[1].replaceAll("\\.mp3$", "").replaceAll("_"," "));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (File file : files) {
                if (file.getName().endsWith(".jpg")) {
                    try {
                        URL url = file.toURI().toURL();
                        imageUrls.add(url.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }


            for(int i = 0; i < imageUrls.size(); i++ ){
                String title = titleList.get(i);
                String artist = artistList.get(i);
                String image = imageUrls.get(i);
                String resource = resourceUrls.get(i);
                Song s = new Song(title, artist, image, resource);
                songList.add(s);
            }

            mainActivity =(MainActivity) getActivity();
            recyclerView = view.findViewById(R.id.song_list_recycler_view);

            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
            songStorageAdapter = new SongStorageAdapter(songList, new SongListener() {
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
            });
            recyclerView.setAdapter(songStorageAdapter);
        }
    }
}