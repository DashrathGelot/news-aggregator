package com.example.newsaggregator.services;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Service {
    private static final String TAG = "Service";

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
}
