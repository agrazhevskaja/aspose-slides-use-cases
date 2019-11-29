package com.aspose.slides.demoapp;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Size;
import android.util.SizeF;

import com.aspose.slides.IPresentation;
import com.aspose.slides.ISlide;
import com.aspose.slides.ISlideCollection;

import java.util.ArrayList;
import java.util.List;


public class SlidesApp extends Application {
    private IPresentation presentation;
    private Size slideThumbnailSize;
    private Size slideListThumbnailSize;
    private List<Bitmap> slideList;
    private Bitmap currSlide;
    private int currSlideIdx = 0;

    public void setPresentation(IPresentation presentation, Size slideViewSize, Size slideListViewSize) {
        this.slideList = null;
        this.currSlide = null;

        this.presentation = presentation;
        calcThumbnailSizes(slideViewSize, slideListViewSize);
        initSlideList();
    }

    private void calcThumbnailSizes(Size slideViewSize, Size slideListViewSize) {
        SizeF srcSize = presentation.getSlideSize().getSize();
        this.slideThumbnailSize = calcFittedSize(srcSize, slideViewSize);
        this.slideListThumbnailSize = calcFittedSize(srcSize, slideListViewSize);
    }

    private Size calcFittedSize(SizeF srcSize, Size dstSize) {
        float ratio = srcSize.getWidth() / srcSize.getHeight();
        int minDstSize = Math.min(dstSize.getWidth(), dstSize.getHeight());

        if(ratio > 1f) {
            return new Size(minDstSize, (int)(minDstSize / ratio));
        } else {
            return new Size((int)(minDstSize * ratio), minDstSize);
        }
    }

    private void initSlideList() {
        ISlideCollection slides = this.presentation.getSlides();
        this.slideList = new ArrayList<>(slides.size());
        for (ISlide slide : slides) {

            this.slideList.add(slide.getThumbnail(this.slideListThumbnailSize));
        }
    }

    public Bitmap selectSlide(int idx) {
        if(currSlideIdx == idx && currSlide != null && !currSlide.isRecycled()) {
            return currSlide;
        }

        this.currSlideIdx = idx;

        this.currSlide = this.presentation.getSlides().get_Item(idx).getThumbnail(this.slideThumbnailSize);
        return this.currSlide;
    }

    public List<Bitmap> getSlideList() {
        return slideList;
    }
}
