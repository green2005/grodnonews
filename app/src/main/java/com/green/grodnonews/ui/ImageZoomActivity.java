package com.green.grodnonews.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.green.grodnonews.R;
import com.green.grodnonews.ThemeHelper;
import com.green.grodnonews.loader.ImageLoader;
import com.green.grodnonews.mvp.ThemePresenter;

import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageZoomActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "url";
    private String mUrl;
    private ImageView mImageView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zoomimageview_activity, menu);
        return true;
    }

    private Uri getImagePath() {
        Drawable d = mImageView.getDrawable();
        if (d == null) {
            return null;
        }
        Bitmap bmp = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        File f = new File(getExternalFilesDir("").getAbsolutePath());
        f.mkdirs();
        File outFile = new File(f.getAbsolutePath() + File.separator + "out.png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return Uri.fromFile(outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void share() {
        final Handler h = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Uri imageUri = getImagePath();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imageUri != null) {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.setType("image/*");
                            startActivity(Intent.createChooser(shareIntent, "Share Image"));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.share) {
            share();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePresenter themePresenter = ViewModelProviders.of(this).get(ThemePresenter.class);
        themePresenter.onViewCreated(this);
        setTheme(themePresenter.getTheme());

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle args = intent.getExtras();
            if (args != null) {
                mUrl = args.getString(IMAGE_URL);
            }
        }
        setContentView(R.layout.activity_image_zoom);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int color = ThemeHelper.getColor(this, R.color.color_toolbar_title_collapsed);
        toolbar.setTitleTextColor(color);
        mImageView = findViewById(R.id.imageView);
        ImageLoader imageLoader = ImageLoader.get(this);
        imageLoader.loadImage(mImageView, mUrl);
        PhotoViewAttacher attacher = new PhotoViewAttacher(mImageView);
        attacher.setZoomable(true);
    }
}
