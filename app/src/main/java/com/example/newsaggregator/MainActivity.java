package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.newsaggregator.model.News;
import com.example.newsaggregator.model.NewsSource;
import com.example.newsaggregator.services.NewsService;
import com.example.newsaggregator.services.SourcesService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String[] sources;
    private NewsSource[] newsSources;
    ArrayList<News> articles;
    private NewsService newsService;
    private NewsAdapter newsAdapter;
    RecyclerView recyclerView;

    private void assignStart() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        recyclerView = findViewById(R.id.newslist);

        SourcesService sourcesService = new SourcesService(this);
        new Thread(sourcesService).start();
        newsService = new NewsService(this);
    }

    public void setNewsSource(NewsSource[] newsSources) {
        this.newsSources = newsSources;
        sources = new String[newsSources.length];
        for (int i = 0; i < sources.length; i++)
            sources[i] = newsSources[i].getName();

        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list, sources));
    }

    public void setArticles(ArrayList<News> articles) {
        this.articles = articles;
        newsAdapter = new NewsAdapter(this);
        newsAdapter.setNewsList(articles);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void selectItem(int position) {
        newsService.setSource(newsSources[position].getId());
        new Thread(newsService).start();
        findViewById(R.id.content_frame).setBackgroundColor(Color.parseColor("#ffffff"));
        drawerLayout.closeDrawer(drawerList);
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        String selection = "";
        if (item.getItemId() == R.id.menuA) {
            selection = "You want to do A";
        } else if (item.getItemId() == R.id.menuB) {
            selection = "You have chosen B";
        } else if (item.getItemId() == R.id.menuC) {
            selection = "C is your selection";
        }

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
        showToast(articles.get(position).getNewsUrl());

        WebView webView = new WebView(this);
        webView.loadUrl(articles.get(position).getNewsUrl());

    }
}