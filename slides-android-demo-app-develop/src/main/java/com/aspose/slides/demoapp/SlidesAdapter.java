package com.aspose.slides.demoapp;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SlidesAdapter extends RecyclerView.Adapter<SlidesAdapter.ViewHolder> {
    private Size slidesListViewSize;
    private View.OnClickListener onClickListener;
    private List<Bitmap> slides = new ArrayList<>();

    public SlidesAdapter(Size slidesListViewSize, View.OnClickListener onClickListener) {
        this.slidesListViewSize = slidesListViewSize;
        this.onClickListener = onClickListener;
    }

    public void setSlides(List<Bitmap> slides) {
        this.slides = slides;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_list_row, parent, false);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(slidesListViewSize.getWidth(), slidesListViewSize.getHeight()));
        itemView.setOnClickListener(onClickListener);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameView.setText(String.valueOf(position + 1));
        holder.thumbView.setImageBitmap(slides.get(position));
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameView;
        private ImageView thumbView;

        ViewHolder(View itemView) {
            super(itemView);
            this.nameView = itemView.findViewById(R.id.slide_name);
            this.thumbView = itemView.findViewById(R.id.slide_thumb);

        }
    }
}
