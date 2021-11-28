package com.example.newsaggregator;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsaggregator.model.News;
import com.example.newsaggregator.services.ImageService;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private ArrayList<News> newsList;
    private MainActivity mainActivity;

    public NewsAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setNewsList(ArrayList<News> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news, parent, false);
        view.setOnClickListener(mainActivity);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.title.setText(news.getTitle());
        holder.desc.setText(news.getDesc());
        holder.author.setText(news.getAuthor());
        holder.time.setText(news.getTime());
        holder.pageCount.setText((position + 1) + " of " + newsList.size());

        if (!news.getUrlImage().equals("null")) {
            new ImageService(holder.imageView).execute(news.getUrlImage());
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView time;
        TextView title;
        TextView desc;
        TextView pageCount;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.newstitle);
            desc = itemView.findViewById(R.id.desc);
            pageCount = itemView.findViewById(R.id.pagecount);
            imageView = itemView.findViewById(R.id.newsimage);
        }

    }
}
