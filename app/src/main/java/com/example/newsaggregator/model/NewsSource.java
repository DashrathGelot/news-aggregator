package com.example.newsaggregator.model;

public class NewsSource {
    String id;
    String name;
    String category;
    String country;
    String language;

    public NewsSource() {

    }

    public NewsSource(String id, String name, String category, String country, String language) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.country = country;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "NewsSource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
