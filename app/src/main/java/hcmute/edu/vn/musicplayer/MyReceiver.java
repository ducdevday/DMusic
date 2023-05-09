package hcmute.edu.vn.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import hcmute.edu.vn.musicplayer.services.MyService;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int action_music = intent.getIntExtra("action_music", 0);
        // Gửi ngược lại dữ liệu cho Service, gọi lại hàm onStartCommand
        Intent i = new Intent(context, MyService.class);
        i.putExtra("action_music_service", action_music);
        context.startService(i);
    }
}