package hcmute.edu.vn.musicplayer.models;

import java.io.Serializable;
import java.util.List;

public class Artist implements Serializable {
    private String name;
    private String bio;
    private String image;
    private List<Song> songs;

    public Artist() {
    }

    public Artist(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public Artist(String name, String bio, String image, List<Song> songList) {
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.songs = songList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Song> getSongList() {
        return songs;
    }

    public void setSongList(List<Song> songList) {
        this.songs = songList;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", image='" + image + '\'' +
                ", songList=" + songs +
                '}';
    }
}
