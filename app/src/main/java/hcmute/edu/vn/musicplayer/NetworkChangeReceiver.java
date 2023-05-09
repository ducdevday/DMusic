package hcmute.edu.vn.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.musicplayer.events.NetworkChangeListener;

public class NetworkChangeReceiver extends BroadcastReceiver {
    //Truyền dữ liệu giữa các receiver để nhận sự kiện
    private List<NetworkChangeListener> listeners = new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Internet đã được kết nối
                Toast.makeText(context,"Internet connected", Toast.LENGTH_LONG).show();
                for (NetworkChangeListener listener : listeners) {
                    listener.onNetworkChanged(true);
                }
            } else {
                // Internet đã bị ngắt kết nối
                Toast.makeText(context,"Internet disconnected", Toast.LENGTH_LONG).show();
                for (NetworkChangeListener listener : listeners) {
                    listener.onNetworkChanged(false);
                }
            }
        }
    }
    public void addListener(NetworkChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NetworkChangeListener listener) {
        listeners.remove(listener);
    }
}