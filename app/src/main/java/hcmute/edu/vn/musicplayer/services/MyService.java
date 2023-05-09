package hcmute.edu.vn.musicplayer.services;

import static hcmute.edu.vn.musicplayer.MyApplication.CHANNEL_ID;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hcmute.edu.vn.musicplayer.MyReceiver;
import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.activities.MainActivity;
import hcmute.edu.vn.musicplayer.activities.PlayerActivity;
import hcmute.edu.vn.musicplayer.models.Song;
import hcmute.edu.vn.musicplayer.utils.DownloadImageTask;

public class MyService extends Service {
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_START = 4;
    public static final int ACTION_PREVIOUS = 5;
    public static final int ACTION_NEXT = 6;

    public static MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private Song currentSong;
    private List<Song> songList;


    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "MyService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG", "onStartCommand");
        Bundle b = intent.getExtras();
        if (b != null) {
            Song s = (Song) b.get("song");
            List<Song> songs = (List<Song>) b.get("songList");
            if (s != null) {
                currentSong = s;
                startMusic(s);
                sendNotification_with_MediaControls(s);
            }
            if (songs != null)
                songList = songs;
        }
        // Kiểm tra dữ liệu nhận được từ broadcast
        int action_music = intent.getIntExtra("action_music_service", 0);
        handleActionMusic(action_music);
        return START_NOT_STICKY;
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_CLEAR:
                stopMusic();
                break;
            case ACTION_PREVIOUS:
                previousMusic();
                break;
            case ACTION_NEXT:
                nextMusic();
                break;
            default:
                break;
        }
    }

    private void startMusic(Song s) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(s.getResource()));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Gọi phương thức của media player để phát bản nhạc tiếp theo ở đây.
                if (songList.size() == 1) {
                    pauseMusic();
                } else {
                    nextMusic();
                }
            }
        });
        mediaPlayer.start();
        isPlaying = true;
        sendActiontoActivity(ACTION_START);
    }

    private void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification_with_MediaControls(currentSong);
            sendActiontoActivity(ACTION_PAUSE);
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            sendNotification_with_MediaControls(currentSong);
            sendActiontoActivity(ACTION_RESUME);
        }
    }

    private void stopMusic() {
        sendActiontoActivity(ACTION_CLEAR);
        stopSelf();
    }

    public void previousMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        int index = 0;
        for (Song s : songList) {
            if (s == currentSong) {
                index = songList.indexOf(s);
            }
        }
        if (index == 0)
            index = songList.size() - 1;
        else
            index = index - 1;
        currentSong = songList.get(index);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songList.get(index).getResource()));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Gọi phương thức của media player để phát bản nhạc tiếp theo ở đây.
                if (songList.size() == 1) {
                    pauseMusic();
                } else {
                    nextMusic();
                }
            }
        });
        mediaPlayer.start();
        isPlaying = true;
        sendNotification_with_MediaControls(currentSong);
        sendActiontoActivity(ACTION_START);
    }

    private void nextMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        int index = 0;
        for (Song s : songList) {
            if (s == currentSong) {
                index = songList.indexOf(s);
            }
        }
        if (index == songList.size() - 1)
            index = 0;
        else
            index = index + 1;
        currentSong = songList.get(index);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songList.get(index).getResource()));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Gọi phương thức của media player để phát bản nhạc tiếp theo ở đây.
                if (songList.size() == 1) {
                    pauseMusic();
                } else {
                    nextMusic();
                }
            }
        });
        mediaPlayer.start();
        isPlaying = true;
        sendNotification_with_MediaControls(currentSong);
        sendActiontoActivity(ACTION_START);
    }

    private void sendNotification_with_MediaControls(Song s) {
        //Tạo action cho Notification trong Android:
        //- Khi người dùng chạm vào Notification, bạn nên có navigation đến một Activity nào đó.
        // Như vậy, bạn sẽ tạo một action cho Notification.
        // Việc tạo action cho Notification, bạn nên sử dụng PendingIntent bằng cách sau:
        //B1. Tạo Intent với Activity sẽ được hiển thị khi người dùng chạm vào Notification
        //B2. Tạo PendingIntent để mang thông tin của Intent khi gửi cho Notification
        Intent i = new Intent(this, PlayerActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("songList", (Serializable) songList);
        bundle.putSerializable("song", currentSong);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", isPlaying ? ACTION_RESUME : ACTION_PAUSE);
        i.putExtras(bundle);

        //PendingIntent.getActivity() được sử dụng để tạo PendingIntent cho một Activity.
        //PendingIntent.IMMUTABLE thì không gửi bundle được
        //Phải gửi bundle qua lần đầu tiên vì khi này PlayerActivity chưa được khởi tạo nên không lắng nghe bundle được
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        MediaSession mediaSession = new MediaSession(this, "My Session");

        Bitmap bitmap = null;
        if(s.getImage().startsWith("file")){
//            bitmap = BitmapFactory.decodeFile(s.getImage());
//            if(bitmap == null){
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_baseline_library_music );
//            }
        }
        else{
            DownloadImageTask task = new DownloadImageTask();
            try {
                bitmap = task.execute(s.getImage()).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_play)
                // Add media control buttons that invoke intents in your media service
                // Apply the media style template
                .setStyle(new MediaStyle()
                        //.setShowActionsInCompactView(1 /* #1: pause button */)
                        .setShowActionsInCompactView(0, 1, 2 /* #0: prev button, #1: pause button, #2:next button */)
                        .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken())))
                .setContentTitle(s.getTitle())
                .setContentText(s.getArtist())
                .setLargeIcon(bitmap);


        if (isPlaying) {
            notificationBuilder.addAction(R.drawable.ic_previous, "Previous", getPendingIntent(this, ACTION_PREVIOUS)) // #0
                    .addAction(R.drawable.ic_pause, "Pause", getPendingIntent(this, ACTION_PAUSE))  // #1
                    .addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT))     // #2
                    .addAction(R.drawable.ic_close, "Close", getPendingIntent(this, ACTION_CLEAR))
            ;
        } else {
            notificationBuilder.addAction(R.drawable.ic_previous, "Previous", getPendingIntent(this, ACTION_PREVIOUS)) // #0
                    .addAction(R.drawable.ic_play, "Play", getPendingIntent(this, ACTION_RESUME))  // #1
                    .addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT))     // #2
                    .addAction(R.drawable.ic_close, "Close", getPendingIntent(this, ACTION_CLEAR))
            ;
        }
        //Chạy Foreground Service
        startForeground(1, notificationBuilder.build());
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent i = new Intent(this, MyReceiver.class);
        i.putExtra("action_music", action);
        //PendingIntent.getBroadcast() được sử dụng để tạo PendingIntent cho một BroadcastReceiver.
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendActiontoActivity(int action) {
        Intent i = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("songList", (Serializable) songList);
        bundle.putSerializable("song", currentSong);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", action);
        i.putExtras(bundle);
        sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}