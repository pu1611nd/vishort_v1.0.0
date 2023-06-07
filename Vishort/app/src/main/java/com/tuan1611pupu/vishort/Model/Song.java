package com.tuan1611pupu.vishort.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Song implements Serializable {

    private String artist;
    private String audio;
    private String details;
    private String albumImage;
    private String id;
    private String title;
    private int duration;

    public Song() {
    }

    public Song(String artist, String audio, String details, String albumImage, String id, String title, int duration) {
        this.artist = artist;
        this.audio = audio;
        this.details = details;
        this.albumImage = albumImage;
        this.id = id;
        this.title = title;
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}


