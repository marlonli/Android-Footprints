package com.example.jingyuan.footprints;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * Created by jingyuan on 12/6/17.
 */

public class GalleryPreview extends AppCompatActivity {

    ImageView GalleryPreviewImg;
    String image_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.gallery_preview);
        Intent intent = getIntent();
        image_string = intent.getStringExtra("image_name");
        byte[] image_byte = Base64.decode(image_string,Base64.DEFAULT);
        GalleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        Glide.with(GalleryPreview.this)
                .asBitmap()
                .load(image_byte) // Uri of the picture
                .into(GalleryPreviewImg);
    }
}
