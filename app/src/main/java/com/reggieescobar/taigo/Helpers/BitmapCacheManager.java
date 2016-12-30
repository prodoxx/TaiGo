package com.reggieescobar.taigo.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


//http://www.scriptscoop2.com/t/e04791af3034/android-how-to-stop-images-reloading-in-recyclerview-when-scrolling.html

public class BitmapCacheManager {
    private static LruCache<Object, Bitmap> cache = null;
    private final Context context;
    private static final int KB = 1024;
  //  private final Drawable placeHolder;


    public BitmapCacheManager(Context context) {
        this.context = context;
      //  placeHolder = context.getResources().getDrawable(R.drawable.unknown);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / KB);
        int cacheSize = maxMemory / 7;
        cache = new LruCache<Object, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Object albumId, Bitmap bitmap) {
                return (bitmap.getRowBytes() * bitmap.getHeight() / KB);
            }

            protected void entryRemoved(boolean evicted, Object key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
            }
        };
    }

    public void addBitmapToMemoryCache(Object key, Bitmap bitmap) {
        if (bitmap != null && key != null && cache.get(key) == null)
            cache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(Object key) {
        return cache.get(key);
    }


}