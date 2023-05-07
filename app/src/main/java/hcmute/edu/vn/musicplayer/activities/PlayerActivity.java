package hcmute.edu.vn.musicplayer.activities;

import static hcmute.edu.vn.musicplayer.services.MyService.mediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import hcmute.edu.vn.musicplayer.MyReceiver;
import hcmute.edu.vn.musicplayer.NetworkChangeReceiver;
import hcmute.edu.vn.musicplayer.events.NetworkChangeListener;
import hcmute.edu.vn.musicplayer.services.MyService;
import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.models.Song;
import hcmute.edu.vn.musicplayer.utils.DownloadImageTask;

public class PlayerActivity extends AppCompatActivity implements NetworkChangeListener {
    ImageView imgAlbumCoverPlayer;
    TextView txtSongTitlePlayer;
    TextView txtArtistPlayer;
    MaterialToolbar materialToolbar;

    SeekBar seekBar;
    ImageButton btnPreviousPlayer;
    ImageButton btnPlayPausePlayer;
    ImageButton btnNextPlayer;
    TextView txtCurrentTime;
    TextView txtFinalTime;
    // Handlers
    Handler handler = new Handler();
    // Variables
    double startTime;
    double finalTime;
    private Song currentSong;
    private List<Song> songList;
    private Boolean isPlaying;
    private int actionMusic;

    // Tạo một biến để theo dõi xem người dùng đang kéo seekBar hay không
    private boolean isUserSeeking = false;

    private NetworkChangeReceiver networkChangeReceiver;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.e("TAG", "onReceive");
            if (bundle != null) {
                currentSong = (Song) bundle.get("song");
                songList = (List<Song>) bundle.get("songList");
                isPlaying = bundle.getBoolean("status_player");
                actionMusic = bundle.getInt("action_music");
                handleMusicPlayer(actionMusic);
                handleSeekBar();
                showInfoSong();
            }
        }
    };

    private void handleMusicPlayer(int actionMusic) {
        switch (actionMusic) {
            case MyService.ACTION_START:
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_PAUSE:
            case MyService.ACTION_RESUME:
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                finish();
                break;
            default:
                break;
        }
    }

    private void showInfoSong() {
        if (currentSong != null) {
            Glide.with(getApplicationContext()).load(currentSong.getImage()).centerCrop().into(imgAlbumCoverPlayer);
            txtSongTitlePlayer.setText(currentSong.getTitle());
            txtArtistPlayer.setText(currentSong.getArtist());

            //Code change background color base on img
            // Load the image

            Bitmap bitmap = null;
            if(currentSong.getImage().startsWith("file")){
                bitmap = BitmapFactory.decodeFile( currentSong.getImage());
            }
            else{
                DownloadImageTask task = new DownloadImageTask();
                try {
                bitmap = task.execute(currentSong.getImage()).get();
                } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                }
            }

            if(!currentSong.getImage().startsWith("file")){
                // Create a Palette object from the image
                Palette palette = Palette.from(bitmap).generate();

                // Get the primary color of the image
                int primaryColor = palette.getDominantColor(ContextCompat.getColor(this, android.R.color.black));
                View layout = findViewById(R.id.activity_player);
                layout.setBackgroundColor(primaryColor);
                Log.e("TAG", "showInfoSong");
            }
        }
    }

    private void setStatusButtonPlayOrPause() {
        if (isPlaying) {
            btnPlayPausePlayer.setBackgroundResource(R.drawable.ic_pause);
        } else {
            btnPlayPausePlayer.setBackgroundResource(R.drawable.ic_play);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Log.e("TAG", "onCreate");
        initView();
        handleEvent();

        networkChangeReceiver =new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        networkChangeReceiver.addListener(this);
    }

    private void initView() {
        imgAlbumCoverPlayer = findViewById(R.id.imgAlbumCoverPlayer);
        txtSongTitlePlayer = findViewById(R.id.txtSongTitlePlayer);
        txtArtistPlayer = findViewById(R.id.txtArtistPlayer);
        materialToolbar = findViewById(R.id.topAppBarPlayer);


        seekBar = findViewById(R.id.sliderMainPlayer);
        txtCurrentTime = findViewById(R.id.currentTime);
        txtFinalTime = findViewById(R.id.finalTime);

        btnPreviousPlayer = findViewById(R.id.btnPreviousPlayer);
        btnPlayPausePlayer = findViewById(R.id.btnPlayPausePlayer);
        btnNextPlayer = findViewById(R.id.btnNextPlayer);

        materialToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Lấy bundle lần đầu trong lần khởi tạo
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentSong = (Song) bundle.get("song");
            songList = (List<Song>) bundle.get("songList");
            isPlaying = bundle.getBoolean("status_player");
            actionMusic = bundle.getInt("action_music");
            handleMusicPlayer(actionMusic);
            showInfoSong();
        }

        handleSeekBar();

        //Đăng ký receiver
        IntentFilter filter = new IntentFilter("send_data_to_activity");
        registerReceiver(broadcastReceiver, filter);

        Log.e("TAG", "initView");
        //callService();
    }

    private void handleSeekBar() {
        startTime = mediaPlayer.getCurrentPosition();
        finalTime = mediaPlayer.getDuration();
        Log.e("TAG", String.valueOf(startTime));
        Log.e("TAG", String.valueOf(finalTime));
        seekBar.setMax((int) finalTime);

        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((long) finalTime);
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes
                                ((long) finalTime)));
        String minutesString = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String secondsString = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        txtFinalTime.setText(String.format(Locale.US, "%s:%s", minutesString, secondsString));

        seekBar.setProgress((int) startTime);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                   // mediaPlayer.seekTo(progress);
                    handler.removeCallbacks(UpdateSongTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Đặt mã xử lý seekBar ở đây
                if (isUserSeeking) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    handler.postDelayed(UpdateSongTime, 100);
                }
                isUserSeeking = false;
            }
        });
        handler.postDelayed(UpdateSongTime, 100);
    }
    // Creating the Runnable
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer == null)
                return;
            startTime = mediaPlayer.getCurrentPosition();
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((long) startTime);
            int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(minutes));
            String minutesString = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
            String secondsString = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
            txtCurrentTime.setText(String.format(Locale.US, "%s:%s", minutesString, secondsString));
            seekBar.setProgress((int) startTime);
            handler.postDelayed(this, 100);
        }
    };

    private void handleEvent() {
        btnPlayPausePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    sendActionToService(MyService.ACTION_PAUSE);
                } else {
                    sendActionToService(MyService.ACTION_RESUME);
                }
            }
        });

        btnPreviousPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(MyService.ACTION_PREVIOUS);
            }
        });

        btnNextPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(MyService.ACTION_NEXT);
            }
        });

        Log.e("TAG", "handleEvent");
    }

    private void sendActionToService(int action) {
        Intent i = new Intent(this, MyService.class);
        i.putExtra("action_music_service", action);
        startService(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if(!isConnected){
//            mediaPlayer.stop();
//            Intent intent = new Intent(this, MyService.class);
//            stopService(intent);
//            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(networkChangeReceiver);
        handler.removeCallbacks(UpdateSongTime);
        Log.e("TAG","onDestroy" );
    }


}