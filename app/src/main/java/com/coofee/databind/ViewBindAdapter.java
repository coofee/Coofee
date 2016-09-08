package com.coofee.databind;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.coofee.R;

import java.util.Locale;

/**
 * Created by zhaocongying on 16/9/3.
 */
public class ViewBindAdapter {
    private static final String TAG = "ViewBindAdapter";

    @BindingAdapter("url")
    public static void loadUrl(final ImageView imageView, String url) {
        Log.d(TAG, "loadUrl: url=" + url);
        Glide.with(imageView.getContext())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new LoggingListener<String, Bitmap>())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.image_failed)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
//                        BitmapShader shader = new BitmapShader(resource, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//                        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
//                        shapeDrawable.getPaint().setShader(shader);
//                        shapeDrawable.setBounds(0, 0, );
                    }
                });
    }

    private static class LoggingListener<T, R> implements RequestListener<T, R> {
        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            android.util.Log.d("GLIDE", String.format(Locale.ROOT,
                    "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            android.util.Log.d("GLIDE", String.format(Locale.ROOT,
                    "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, isFromMemoryCache, isFirstResource));
            return false;
        }
    }
}
