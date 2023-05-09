package hcmute.edu.vn.musicplayer.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import hcmute.edu.vn.musicplayer.events.DownloadCallback;

public class DownloadFileAsyncTask extends AsyncTask<String, String, String> {
    private DownloadCallback callback;
    private String title;
    public DownloadFileAsyncTask(DownloadCallback callback, String title) {
        this.callback = callback;
        this.title = title;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onDownloadStarted();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPreExecute();
        Log.d("Download Completed", "Downloaded file is saved in the specified directory.");
        if (callback != null) {
            callback.onDownloadFinished();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URL url = new URL(strings[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            // Lấy kích thước của tệp tin
            int lengthOfFile = connection.getContentLength();

            // Tạo một đối tượng InputStream để đọc dữ liệu từ URL
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Tạo một đối tượng OutputStream để ghi dữ liệu vào tệp tin
            OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/MusicPlayer/"+title+".mp3");

            // Tạo một buffer để đọc và ghi dữ liệu
            byte[] data = new byte[102400];

            // Tổng số byte đã đọc được
            long total = 0;

            while ((count = input.read(data)) != -1) {
                // Tăng tổng số byte đã đọc được
                total += count;

                // Đăng báo cáo tiến trình tải xuống
                publishProgress("" + (int) ((total * 100) / lengthOfFile));

                // Ghi dữ liệu vào tệp tin
                output.write(data, 0, count);
            }

            // Đóng InputStream và OutputStream
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        // Cập nhật tiến trình tải xuống
        Log.d("Download Progress", progress[0]);
        if (callback != null) {
            callback.onDownloadProcessing(Integer.parseInt(progress[0]));
        }
    }
}