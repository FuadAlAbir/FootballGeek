package com.geekstudios.footballgeekblog;

import java.util.Date;

public class classBlogItem {

    private String user_id;
    private String image_url;
    private String desc;
    private String title;
    private String image_thumb;
    private String clap_counter;

    private Date time;


    public classBlogItem() {}

    public classBlogItem(String user_id, String image_url, String desc, String title, String image_thumb, String claps, Date time) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.title = title;
        this.image_thumb = image_thumb;
        this.clap_counter = claps;
        this.time = time;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getClap_counter() {
        return clap_counter;
    }

    public void setClap_counter(String clap_counter) {
        this.clap_counter = clap_counter;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
