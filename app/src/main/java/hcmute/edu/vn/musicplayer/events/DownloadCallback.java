package hcmute.edu.vn.musicplayer.events;

public interface DownloadCallback {
    void onDownloadStarted();
    void onDownloadFinished();
    void onDownloadProcessing(int progress);
}