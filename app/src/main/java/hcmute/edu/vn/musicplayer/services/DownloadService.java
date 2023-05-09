package hcmute.edu.vn.musicplayer.services;

import static hcmute.edu.vn.musicplayer.MyApplication.CHANNEL_ID;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.events.DownloadCallback;
import hcmute.edu.vn.musicplayer.models.Song;
import hcmute.edu.vn.musicplayer.utils.DownloadFileAsyncTask;
import hcmute.edu.vn.musicplayer.utils.DownloadImageAsyncTask;

public class DownloadService extends Service implements DownloadCallback {
    NotificationCompat.Builder notification;
    final int progressMax = 100;
    Song s;
    public DownloadService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG", "DownloadService onStartCommand");
        sendNotification(intent);
        return START_NOT_STICKY;
    }

    private void sendNotification(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            s = (Song) b.get("song");
            DownloadImageAsyncTask downloadImageAsyncTask = new DownloadImageAsyncTask(s.getTitle().replaceAll("\\s+","_")+"-"+s.getArtist().replaceAll("\\s+","_"));
            downloadImageAsyncTask.execute(s.getImage());

            DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(this, s.getTitle().replaceAll("\\s+","_")+"-"+s.getArtist().replaceAll("\\s+","_"));
            downloadFileAsyncTask.execute(s.getResource());


        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDownloadStarted() {
        assert s != null;
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_play)
                .setContentTitle("Download " + s.getTitle())
                .setContentText("Download in progress")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, true);

        startForeground(2, notification.build());
    }
    @Override
    public void onDownloadProcessing(int progress) {
        notification.setProgress(progressMax, progress, false);
        startForeground(2, notification.build());
    }
    @Override
    public void onDownloadFinished() {
        notification.setContentText("Download finished")
                .setProgress(0, 0, false)
                .setOngoing(false);
        startForeground(2, notification.build());
        SystemClock.sleep(3000);
        Toast.makeText(getApplicationContext(), "Download finished", Toast.LENGTH_SHORT).show();
        stopSelf();
    }
}