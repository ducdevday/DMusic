package hcmute.edu.vn.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        createChanelNotification();
    }

    public void createChanelNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (ContextCompat.checkSelfPermission(getApplicationContext(), POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{POST_NOTIFICATIONS},101);
//            }
//            else {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CHANNEL_ID", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
//            }
    }


}
