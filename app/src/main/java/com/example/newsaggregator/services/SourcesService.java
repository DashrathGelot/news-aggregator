package com.example.newsaggregator.services;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.newsaggregator.MainActivity;
import com.example.newsaggregator.model.NewsSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class SourcesService extends Service implements Runnable {
    private static final String TAG = "Source Service Runnable";
    MainActivity mainActivity;
    Map<String, String> countriesMap;
    Map<String, String> languagesMap;

    public SourcesService(MainActivity mainActivity, Map<String, String> countriesMap, Map<String, String> languagesMap) {
        this.mainActivity = mainActivity;
        this.countriesMap = countriesMap;
        this.languagesMap = languagesMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NewsSource[] parseJSON(String s) {
        try {
            JSONObject jMain = new JSONObject(s);
            JSONArray jSources = jMain.getJSONArray("sources");
            int sourcesLength = jSources.length();
            NewsSource[] sources = new NewsSource[sourcesLength];

            for (int i = 0; i < sourcesLength; i++) {
                JSONObject source = (JSONObject) jSources.get(i);
                sources[i] = new NewsSource(source.getString("id"),
                        source.getString("name"),
                        source.getString("category"),
                        countriesMap.get(source.getString("country").toUpperCase()),
                        languagesMap.get(source.getString("language").toUpperCase())
                );
            }

            return sources;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return new NewsSource[]{};
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleResult(String result) {
        if (result.isEmpty()) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::apiFailedToFetch);
            return;
        }

        NewsSource[] newsSource = parseJSON(result);

        mainActivity.runOnUiThread(() -> {
            if (newsSource != null) mainActivity.setNewsSource(newsSource);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        String API_URL = "https://newsapi.org/v2/top-headlines/sources?apiKey=929aff506ba44e939989bcde945477a3";
        handleResult(getResult(API_URL));
    }
}
