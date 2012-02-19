package com.pingme.adapters;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.pingme.PingMeApplication;
import com.pingme.R;
import com.pingme.model.POIData;

public class ImageAdapter extends BaseAdapter {

	int mGalleryItemBackground;
    private Context mContext;
    private List<String> datas;

    public ImageAdapter(Context c, POIData poiData) {
        mContext = c;
        datas = poiData.getUrlsImages();
        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.HelloGallery);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.HelloGallery_android_galleryItemBackground, 0);
        attr.recycle();
    }

    public int getCount() {
        return datas.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);

        imageView.setImageResource(R.drawable.temp_img);
        PingMeApplication.getImageDownloader().download(datas.get(position), imageView, null, "ImageAdapter");
        
        imageView.setLayoutParams(new Gallery.LayoutParams(200, 150));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundResource(mGalleryItemBackground);

        return imageView;
    }

}
