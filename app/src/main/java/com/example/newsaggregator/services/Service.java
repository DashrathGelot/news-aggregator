package com.example.newsaggregator.services;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.example.newsaggregator.MainActivity;
import com.example.newsaggregator.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Service {
    private static final String TAG = "Service";

    private Map<String, Integer> colors;

    public String getResult(String API_URL) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(Uri.parse(API_URL).toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent", "");
            conn.connect();

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return "";
            }

            BufferedReader reader = new BufferedReader((new InputStreamReader(conn.getInputStream())));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "data: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return"";
        }

        return sb.toString();
    }

    public void setColor(MainActivity mainActivity) {
        colors = new HashMap<>();
        Resources r = mainActivity.getResources();

        colors.put("general", r.getColor(R.color.general));
        colors.put("business", r.getColor(R.color.business));
        colors.put("technology", r.getColor(R.color.technology));
        colors.put("sports", r.getColor(R.color.sports));
        colors.put("entertainment", r.getColor(R.color.entertainment));
        colors.put("health", r.getColor(R.color.health));
        colors.put("science", r.getColor(R.color.science));
    }

    public int getColor(String type) {
        return colors.get(type);
    }

}
