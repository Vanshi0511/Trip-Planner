package com.adventure.tripplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

//adapter for set images to view pager from DB when viewing trip info.
public class ImageGetAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> imagesString;
    private LayoutInflater layoutInflater;

    public ImageGetAdapter(Context context, ArrayList<String> imagesString) {
        this.context = context;
        this.imagesString = imagesString;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesString.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= layoutInflater.inflate(R.layout.image_recycler_view,container,false);
        ImageView imageView=view.findViewById(R.id.imageView);
        Picasso.get().load(imagesString.get(position)).into(imageView);
        Objects.requireNonNull(container).addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
