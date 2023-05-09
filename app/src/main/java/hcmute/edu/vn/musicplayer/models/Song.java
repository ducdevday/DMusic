package hcmute.edu.vn.musicplayer.models;

import java.io.Serializable;
import java.util.Objects;

public class Song implements Serializable {
    private String title;
    private String artist;
    private String image;
    private String resource;
    private String duration;

    public Song(){

    }

    public Song(String title, String artist, String image, String resource) {
        this.title = title;
        this.artist = artist;
        this.image = image;
        this.resource = resource;
    }

    public Song(String title, String artist, String image, String resource, String duration) {
        this.title = title;
        this.artist = artist;
        this.image = image;
        this.resource = resource;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", image='" + image + '\'' +
                ", resource='" + resource + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return Objects.equals(getTitle(), song.getTitle()) && Objects.equals(getArtist(), song.getArtist()) && Objects.equals(getImage(), song.getImage()) && Objects.equals(getResource(), song.getResource()) && Objects.equals(getDuration(), song.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getArtist(), getImage(), getResource(), getDuration());
    }
}
