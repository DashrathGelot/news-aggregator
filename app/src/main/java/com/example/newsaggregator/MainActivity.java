package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsaggregator.model.News;
import com.example.newsaggregator.model.NewsSource;
import com.example.newsaggregator.services.NewsService;
import com.example.newsaggregator.services.Service;
import com.example.newsaggregator.services.SourcesService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayAdapter<String> arrayAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private NewsSource[] newsSources;
    private String[] sources;
    ArrayList<News> articles;
    private NewsService newsService;
    private NewsAdapter newsAdapter;
    RecyclerView recyclerView;

    private SubMenu topics;
    private SubMenu countries;
    private SubMenu language;

    Map<String, String> countriesMap;
    Map<String, String> languagesMap;
    Map<Integer, String> filterMap = new HashMap<>();

    private Service service;

    private void assignStart() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        recyclerView = findViewById(R.id.newslist);

        countriesMap = loadJsonData(R.raw.country_codes, "countries");
        languagesMap = loadJsonData(R.raw.language_codes, "languages");

        filterMap.put(1, "all");
        filterMap.put(2, "all");
        filterMap.put(3, "all");

        SourcesService sourcesService = new SourcesService(this, countriesMap, languagesMap);
        new Thread(sourcesService).start();
        newsService = new NewsService(this);

        service = new Service();
        service.setColor(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setNewsSource(NewsSource[] newsSources) {
        this.newsSources = newsSources;
        sources = new String[newsSources.length];

        for (int i = 0; i < sources.length; i++)
            sources[i] = newsSources[i].getName();

        this.setTitle("News Aggregator " + "(" +sources.length+")");

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list, sources) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(service.getColor(newsSources[position].getCategory()));
                return super.getView(position, convertView, parent);
            }
        };
        drawerList.setAdapter(arrayAdapter);
        updateMenu(newsSources);
    }

    public void setArticles(ArrayList<News> articles) {
        this.articles = articles;
        newsAdapter = new NewsAdapter(this);
        newsAdapter.setNewsList(articles);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectItem(int position) {
        NewsSource newsSource = Arrays.stream(newsSources).filter(newsSource1 -> newsSource1.getName().equals(sources[position])).findFirst().get();
        newsService.setSource(newsSource.getId());
        this.setTitle(newsSource.getName());
        new Thread(newsService).start();
        findViewById(R.id.content_frame).setBackgroundColor(Color.parseColor("#ffffff"));
        drawerLayout.closeDrawer(drawerList);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignStart();

        drawerList.setOnItemClickListener((parent, view, position, id) -> selectItem(position));
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateData(String item, int id) {
        if (id == 0) return;

        String prev = filterMap.get(id);
        filterMap.put(id, item);

        NewsSource[] filteredSources = Arrays.stream(newsSources)
                .filter(source -> (source.getCategory().equals(filterMap.get(1)) || filterMap.get(1).equals("all"))
                        && (source.getCountry().equals(filterMap.get(2)) || filterMap.get(2).equals("all"))
                        && (source.getLanguage().equals(filterMap.get(3)) || filterMap.get(3).equals("all")))
                .toArray(NewsSource[]::new);

        String []nSources = Arrays.stream(filteredSources).map(NewsSource::getName).toArray(String[]::new);

        if (nSources.length == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("No News Sources")
                    .setMessage("no news sources exist that match the specified Topic, Language and/or Country")
                    .setPositiveButton("OK", (dialog, which) -> filterMap.put(id, prev)).show();
        } else {
            sources = nSources;
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list, sources) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = view.findViewById(android.R.id.text1);
                    text.setTextColor(service.getColor(filteredSources[position].getCategory()));
                    return super.getView(position, convertView, parent);
                }
            };

            drawerList.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            setTitle("News Aggregator (" + sources.length + ")");
        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMenu(NewsSource[] newsSources) {
        Arrays.stream(newsSources).map(NewsSource::getCategory).distinct().forEach((topic) -> {
            SpannableString str = new SpannableString(topic);
            str.setSpan(new ForegroundColorSpan(service.getColor(topic)), 0, str.length(), 0);
            topics.add(1, 0, 0, str);
        });
        Arrays.stream(newsSources).map(NewsSource::getCountry).distinct().forEach(country -> countries.add(2, 0, 0, country));
        Arrays.stream(newsSources).map(NewsSource::getLanguage).distinct().forEach(lang -> language.add(3, 0, 0, lang));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        topics = menu.addSubMenu("Topics");
        topics.add(1, 0, 0, "all");
        countries = menu.addSubMenu("Countries");
        countries.add(2, 0, 0, "all");
        language = menu.addSubMenu("Languages");
        language.add(3, 0, 0,"all");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: DrawerToggle " + item);
            return true;
        }

        updateData(item.getTitle().toString(), item.getGroupId());
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public void apiFailedToFetch() {
        showToast("Failed to Fetch the news sources");
    }

    @Override
    public void onClick(View v) {
//        Intent web = new Intent(this, NewsWeb.class);
        int position = recyclerView.getChildAdapterPosition(v);
//        web.putExtra("url", articles.get(position).getNewsUrl());
//        startActivity(web);
//        showToast(articles.get(position).getNewsUrl());
//        WebView webView = new WebView(this);
//        webView.loadUrl(articles.get(position).getNewsUrl());

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(articles.get(position).getNewsUrl())));
    }

    public Map<String, String> loadJsonData(int resource, String key) {
        Log.d(TAG, "loadFile: Loading JSON File");
        Map<String, String> data = new HashMap<>();

        try {
            InputStream inputStream = getResources().openRawResource(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONObject(sb.toString()).getJSONArray(key);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                data.put(jsonObject.getString("code"), jsonObject.getString("name"));
            }

            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File Not Found: JSON File not found");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}