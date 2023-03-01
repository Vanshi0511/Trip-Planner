package com.adventure.tripplanner;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import java.util.ArrayList;
import java.util.Objects;

//adapter to inflate image on ViewPager when creating a new trip by organizer.
//getting pictures from gallery.
public class ImageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Uri> imagesUri;
    private LayoutInflater layoutInflater;

    public ImageAdapter(Context context, ArrayList<Uri> imagesUri) {
        this.context = context;
        this.imagesUri = imagesUri;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
            return imagesUri.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= layoutInflater.inflate(R.layout.image_recycler_view,container,false);
        ImageView imageView=view.findViewById(R.id.imageView);

        imageView.setImageURI(imagesUri.get(position));
        Objects.requireNonNull(container).addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
