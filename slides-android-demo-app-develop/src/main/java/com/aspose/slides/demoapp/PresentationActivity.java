package com.aspose.slides.demoapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aspose.slides.Presentation;

import java.io.IOException;
import java.io.InputStream;

public class PresentationActivity extends AppCompatActivity {
    private static final String TAG = "Aspose.Slides.PresentationActivity";

    private static final int READ_REQUEST_CODE = 42;

    private SlidesApp app;
    private ProgressBar progressBar;
    private RecyclerView slideList;
    private SlidesAdapter slidesAdapter;
    private ImageView currSlide;

    private Size currSlideViewSize;
    private Size slidesListViewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Size displaySize = new Size(displayMetrics.widthPixels - 100, displayMetrics.heightPixels - 100);

        this.currSlideViewSize = new Size(displaySize.getWidth() * 3 / 4, displaySize.getHeight() * 3 / 4);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.slidesListViewSize = new Size(displaySize.getWidth() / 4, displaySize.getWidth() / 4);
        } else {
            this.slidesListViewSize = new Size(displaySize.getHeight() / 4, displaySize.getHeight() / 4);
        }


        this.app = (SlidesApp) getApplication();
        this.progressBar = findViewById(R.id.progress_bar);

        this.slideList = findViewById(R.id.slide_list);
        this.currSlide = findViewById(R.id.curr_slide);

        this.slidesAdapter = new SlidesAdapter(slidesListViewSize, new OnSlideClickListener(this));

        int slideListOrientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?  RecyclerView.VERTICAL : RecyclerView.HORIZONTAL;
        this.slideList.setAdapter(this.slidesAdapter);
        this.slideList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), slideListOrientation, false));
        this.slideList.setItemAnimator(new DefaultItemAnimator());
        this.slideList.addItemDecoration(new DividerItemDecoration(this, slideListOrientation));

        execFilePicker();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK && resultData != null) {
            Uri uri = resultData.getData();
            try {
                execLoadPresentation(uri);
            } catch (IOException e) {
                Log.e(TAG, "IOException while processing uri: " + uri.toString());
                execGoBack();
            }
        } else {
            execGoBack();
        }
    }

    private void execFilePicker() {
        Intent mRequestFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mRequestFileIntent.setType("*/*");
        startActivityForResult(mRequestFileIntent, READ_REQUEST_CODE);
    }

    private void execLoadPresentation(Uri uri) throws IOException {
        InputStream input = getContentResolver().openInputStream(uri);
        new LoadPresentationTask(this).execute(input);
    }

    private void execGoBack() {
        //go back to MainActivity
        Intent openPresentationIntent = new Intent(this, MainActivity.class);
        startActivity(openPresentationIntent);
    }

    private void selectSlide(int pos) {
        new SelectSlideTask(this).execute(pos);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    private static class OnSlideClickListener implements View.OnClickListener {
        private final PresentationActivity presentationActivity;

        private OnSlideClickListener(PresentationActivity presentationActivity) {
            this.presentationActivity = presentationActivity;
        }

        @Override
        public void onClick(View view) {
            int itemPos = presentationActivity.slideList.getChildLayoutPosition(view);
            presentationActivity.selectSlide(itemPos);
        }
    }

    private static class LoadPresentationTask extends AsyncTask<InputStream, Void, Presentation> {
        private final PresentationActivity presentationActivity;

        private LoadPresentationTask(PresentationActivity presentationActivity) {
            this.presentationActivity = presentationActivity;
        }

        @Override
        protected Presentation doInBackground(InputStream... inputs) {
            InputStream input = inputs[0];
            try {
                Presentation p = input == null ? new Presentation() : new Presentation(input);
                presentationActivity.app.setPresentation(p, presentationActivity.currSlideViewSize, presentationActivity.slidesListViewSize);
                return p;
            } finally {
                try {
                    if(input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException while closing input stream", e);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            presentationActivity.showProgressBar();
        }

        @Override
        protected void onPostExecute(Presentation presentation) {
            presentationActivity.hideProgressBar();
            presentationActivity.slidesAdapter.setSlides(presentationActivity.app.getSlideList());
            presentationActivity.selectSlide(0);
        }

        @Override
        protected void onCancelled(Presentation presentation) {
            presentationActivity.hideProgressBar();
        }

        @Override
        protected void onCancelled() {
            presentationActivity.hideProgressBar();
        }
    }

    private static class SelectSlideTask extends AsyncTask<Integer, Void, Bitmap> {
        private final PresentationActivity presentationActivity;

        public SelectSlideTask(PresentationActivity presentationActivity) {
            this.presentationActivity = presentationActivity;
        }

        @Override
        protected Bitmap doInBackground(Integer... ints) {
            int pos = ints[0];
            return presentationActivity.app.selectSlide(pos);
        }

        @Override
        protected void onPreExecute() {
            presentationActivity.showProgressBar();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            presentationActivity.hideProgressBar();
            presentationActivity.currSlide.setImageBitmap(result);
        }

        @Override
        protected void onCancelled(Bitmap result) {
            presentationActivity.hideProgressBar();
        }

        @Override
        protected void onCancelled() {
            presentationActivity.hideProgressBar();
        }
    }
}