package com.example.newsaggregator.model;

public class News {
    String title;
    String author;
    String desc;
    String time;
    String newsUrl;
    String urlImage;

    public News(String title, String author, String desc, String time, String urlImage, String newsUrl) {
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.time = time;
        this.urlImage = urlImage;
        this.newsUrl = newsUrl;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
    }

    public String getTime() {
        return time;
    }

    public String getUrlImage() {
        return urlImage;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", desc='" + desc + '\'' +
                ", time='" + time + '\'' +
                ", urlImage='" + urlImage + '\'' +
                '}';
    }
}
