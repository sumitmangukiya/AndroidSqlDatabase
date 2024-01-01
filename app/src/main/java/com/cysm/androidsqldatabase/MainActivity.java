package com.cysm.androidsqldatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cysm.androidsqldatabase.database.SqliteDatabase;
import com.cysm.androidsqldatabase.databinding.ActivityMainBinding;
import com.cysm.androidsqldatabase.model.Contacts;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    Uri imageUri;
    SqliteDatabase mDatabase;
    ActivityMainBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnViewData.setOnClickListener(v -> {
            clearText();
            startActivity(new Intent(this, ViewDataActivity.class));
        });

        mDatabase = new SqliteDatabase(this);
        binding.btnInsert.setOnClickListener(v -> {
            final String name = binding.edtName.getText().toString();
            final String ph_no = binding.edtNumber.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ph_no) || imageUri == null) {
                Toast.makeText(MainActivity.this, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
            } else {
                Contacts newContact = new Contacts(name, ph_no, imageUri.toString());
                mDatabase.addContacts(newContact);
                Toast.makeText(this, "Contact Added Successfully", Toast.LENGTH_SHORT).show();
                clearText();
            }
        });

        binding.imgSelectImage.setOnClickListener(v -> {
            ImagePicker.with(this).compress(512).maxResultSize(512, 512).start();
        });

    }

    private void clearText() {
        binding.edtName.setText("");
        binding.edtNumber.setText("");
        binding.imgSelectImage.setImageDrawable(getDrawable(R.drawable.baseline_cameraswitch_24));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        binding.progressBar.setVisibility(View.VISIBLE);
        Glide.with(this).load(imageUri).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                binding.progressBar.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                binding.imgSelectImage.setImageDrawable(resource);
                binding.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(binding.imgSelectImage);
    }

}
