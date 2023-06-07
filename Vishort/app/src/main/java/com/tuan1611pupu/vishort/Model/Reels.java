package com.tuan1611pupu.vishort.Model;

import java.io.Serializable;

public class Reels implements Serializable {
    private String reelsId;
    private String reelsBy;
    private long reelsAt;
    private String caption;
    private String video;
    private int likes;
    private int comments;

    public String getReelsId() {
        return reelsId;
    }

    public void setReelsId(String reelsId) {
        this.reelsId = reelsId;
    }

    public String getReelsBy() {
        return reelsBy;
    }

    public void setReelsBy(String reelsBy) {
        this.reelsBy = reelsBy;
    }

    public long getReelsAt() {
        return reelsAt;
    }

    public void setReelsAt(long reelsAt) {
        this.reelsAt = reelsAt;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
