package hcmute.edu.vn.musicplayer.events;

import hcmute.edu.vn.musicplayer.models.Song;

public interface DownloadListener {
    void onDownloadClickListener(Song song);
}
