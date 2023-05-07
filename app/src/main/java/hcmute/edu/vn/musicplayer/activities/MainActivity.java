package hcmute.edu.vn.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hcmute.edu.vn.musicplayer.NetworkChangeReceiver;
import hcmute.edu.vn.musicplayer.events.NetworkChangeListener;
import hcmute.edu.vn.musicplayer.services.MyService;
import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.adapters.ViewPagerAdapter;
import hcmute.edu.vn.musicplayer.models.Song;
import hcmute.edu.vn.musicplayer.utils.DownloadImageTask;

public class MainActivity extends AppCompatActivity implements NetworkChangeListener {
    ViewPager mViewPager;
    BottomNavigationView bottomNavigationBar;
    FrameLayout frameLayout;

    private Song currentSong;
    private List<Song> songList;
    private Boolean isPlaying;
    private int actionMusic;


    private ImageView imgMiniCover;
    private TextView txtMiniSongTitle;
    private TextView txtMiniArtist;
    private ImageView btnMiniPrevious;
    private ImageView btnMiniPlayOrPause;
    private ImageView btnMiniNext;
    private ImageView btnMiniClose;

    private RelativeLayout mini_player;

    private NetworkChangeReceiver networkChangeReceiver;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                currentSong = (Song) bundle.get("song");
                songList = (List<Song>) bundle.get("songList");
                isPlaying = bundle.getBoolean("status_player");
                actionMusic = bundle.getInt("action_music");
                handleMiniPlayer(actionMusic);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // write permission to access the storage
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        networkChangeReceiver =new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        networkChangeReceiver.addListener(this);

        initView();
        handleEvent();
        setupPager();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initView() {

        mViewPager = findViewById(R.id.viewPager);
        bottomNavigationBar = findViewById(R.id.bottomNavigationBar);
        frameLayout = findViewById(R.id.frameLayout);
        mini_player = findViewById(R.id.mini_player);
        imgMiniCover = findViewById(R.id.imgMiniCover);
        txtMiniSongTitle = findViewById(R.id.txtMiniSongTitle);
        txtMiniArtist = findViewById(R.id.txtMiniArtist);
        btnMiniPrevious = findViewById(R.id.btnMiniPrevious);
        btnMiniPlayOrPause = findViewById(R.id.btnMiniPlayOrPause);
        btnMiniNext = findViewById(R.id.btnMiniNext);
        btnMiniClose = findViewById(R.id.btnMiniClose);


        IntentFilter filter = new IntentFilter("send_data_to_activity");
        registerReceiver(broadcastReceiver, filter);

        if(currentSong != null){
            //Code change mini player color base on img
            // Load the image
            DownloadImageTask task = new DownloadImageTask();
            Bitmap bitmap = null;
            try {
                bitmap = task.execute(currentSong.getImage()).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            // Create a Palette object from the image
            assert bitmap != null;
            Palette palette = Palette.from(bitmap).generate();

            // Get the primary color of the image
            int primaryColor = palette.getDominantColor(ContextCompat.getColor(this, android.R.color.black));
            View v = findViewById(R.id.layoutMiniPlayer);
            v.setBackgroundColor(primaryColor);
        }
    }

    public void handleEvent() {
        bottomNavigationBar.setOnItemSelectedListener(onItemNavigationBarSelectedListener);


        btnMiniPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    sendActionToService(MyService.ACTION_PAUSE);
                } else {
                    sendActionToService(MyService.ACTION_RESUME);
                }
            }
        });

        btnMiniClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(MyService.ACTION_CLEAR);
            }
        });

        btnMiniPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(MyService.ACTION_PREVIOUS);
            }
        });

        btnMiniNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(MyService.ACTION_NEXT);
            }
        });

        mini_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("songList", (Serializable) songList);
                bundle.putSerializable("song", currentSong);
                bundle.putBoolean("status_player", isPlaying);
                bundle.putInt("action_music", actionMusic);
                i.putExtras(bundle);
                startActivity(i);
                //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }

    private void setupPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public void handleMiniPlayer(int actionMusic) {
        switch (actionMusic) {
            case MyService.ACTION_START:
                mini_player.setVisibility(View.VISIBLE);
                showInfoSong();
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_PAUSE:
            case MyService.ACTION_RESUME:
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                mini_player.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private void showInfoSong() {
        if (currentSong != null) {
            Glide.with(getApplicationContext()).load(currentSong.getImage()).centerCrop().into(imgMiniCover);
            txtMiniSongTitle.setText(currentSong.getTitle());
            txtMiniArtist.setText(currentSong.getArtist());
        }
    }
    private void setStatusButtonPlayOrPause() {
        if (isPlaying) {
            btnMiniPlayOrPause.setImageResource(R.drawable.ic_pause);
        } else {
            btnMiniPlayOrPause.setImageResource(R.drawable.ic_play_no_circle);
        }
    }
    private void sendActionToService(int action) {
        Intent i = new Intent(this, MyService.class);
        i.putExtra("action_music_service", action);
        startService(i);
    }

    @Override
    public void onBackPressed() {
        if(frameLayout.getVisibility() == View.VISIBLE){
            frameLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(networkChangeReceiver);
        networkChangeReceiver.removeListener(this);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if (isConnected) {
            bottomNavigationBar.setOnItemSelectedListener(onItemNavigationBarSelectedListener);
            mViewPager.addOnPageChangeListener(onPageChangeListener);
        } else {
            bottomNavigationBar.getMenu().findItem(R.id.btnStorage).setChecked(true);
            mViewPager.setCurrentItem(2);

            bottomNavigationBar.setOnItemSelectedListener(null);
            mViewPager.removeOnPageChangeListener(onPageChangeListener);
        }
    }

    public NavigationBarView.OnItemSelectedListener onItemNavigationBarSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            frameLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            switch (item.getItemId()) {
                case R.id.btnHome:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.btnDiscovery:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.btnStorage:
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.btnProfile:
                    mViewPager.setCurrentItem(3);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    public ViewPager.OnPageChangeListener onPageChangeListener =  new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    bottomNavigationBar.getMenu().findItem(R.id.btnHome).setChecked(true);
                    break;
                case 1:
                    bottomNavigationBar.getMenu().findItem(R.id.btnDiscovery).setChecked(true);
                    break;
                case 2:
                    bottomNavigationBar.getMenu().findItem(R.id.btnStorage).setChecked(true);
                    break;
                case 3:
                    bottomNavigationBar.getMenu().findItem(R.id.btnProfile).setChecked(true);
                    break;
            }
        };

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}