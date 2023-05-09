package hcmute.edu.vn.musicplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private String title;

    public DownloadImageAsyncTask(String title) {
        this.title = title;
    }


    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = null;
        try {
            //Tạo ra một URL từ đường dẫn được cung cấp
            URL url = new URL(strings[0]);
            //Mở kết nối đến URL này sử dụng lớp HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            //Đọc dữ liệu hình ảnh từ InputStream
            InputStream input = connection.getInputStream();
            //Để chuyển đổi dữ liệu này thành một đối tượng Bitmap
            bitmap = BitmapFactory.decodeStream(input);

            File musicPlayerDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MusicPlayer");
            if (!musicPlayerDir.exists()) {
                musicPlayerDir.mkdirs();
            }
            // Save bitmap to storage
            //Mở một OutputStream để ghi dữ liệu vào bộ nhớ của thiết bị.
//            OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + titleSong + ".jpg");
            OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/MusicPlayer/" + title + ".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return bitmap;
    }
}