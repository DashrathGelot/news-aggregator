package com.example.newsaggregator.services;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.newsaggregator.MainActivity;
import com.example.newsaggregator.model.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class NewsService extends Service implements Runnable {
    private static final String TAG = "News Service Runnable";
    MainActivity mainActivity;
    String source;

    public NewsService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<News> parseJSON(String s) {
        ArrayList<News> newsList = new ArrayList<>();
        try {
            JSONObject jMain = new JSONObject(s);
            JSONArray jSources = jMain.getJSONArray("articles");

            for (int i = 0; i < jSources.length(); i++) {
                JSONObject article = (JSONObject) jSources.get(i);

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm", Locale.ENGLISH);

                newsList.add(new News(article.getString("title"),
                        article.getString("author"),
                        article.getString("description"),
                        ZonedDateTime.parse(article.getString("publishedAt")).format(dateTimeFormatter),
                        article.getString("urlToImage"),
                        article.getString("url")
                ));
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return newsList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleResult(String result) {
        if (result.isEmpty()) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::apiFailedToFetch);
            return;
        }

        ArrayList<News> articles = parseJSON(result);
        mainActivity.runOnUiThread(() -> {
            if (articles != null) mainActivity.setArticles(articles);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        String API_TOKEN = "6fac9434bdfc49408d7df9e0ff2b6f7f";
        String API_URL = "https://newsapi.org/v2/top-headlines?sources="+source+"&apiKey="+API_TOKEN;
        handleResult(getResult(API_URL));
    }
}
