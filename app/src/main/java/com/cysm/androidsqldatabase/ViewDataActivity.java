package com.cysm.androidsqldatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cysm.androidsqldatabase.adapter.ContactAdapter;
import com.cysm.androidsqldatabase.database.SqliteDatabase;
import com.cysm.androidsqldatabase.databinding.ActivityViewDataBinding;
import com.cysm.androidsqldatabase.interfaces.EditTaskItemClick;
import com.cysm.androidsqldatabase.model.Contacts;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.util.ArrayList;

public class ViewDataActivity extends AppCompatActivity implements EditTaskItemClick {
    Uri imageUri;
    ImageView dialog_edit_image;
    private SqliteDatabase mDatabase;
    public static final String TAG = "ViewDataActivity";
    ActivityViewDataBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mDatabase = new SqliteDatabase(this);
        ArrayList<Contacts> allContacts = mDatabase.listContacts();

        if (allContacts.size() > 0) {
            ContactAdapter mAdapter = new ContactAdapter(this, allContacts, this);
            binding.recyclerViewData.setAdapter(mAdapter);
            binding.recyclerViewData.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Toast.makeText(this, "There is no contact in the database. Start adding now", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onEditClick(Contacts contacts) {
        editTaskDialog(contacts);
    }

    private void editTaskDialog(final Contacts contacts) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_task);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();

        dialog_edit_image = dialog.findViewById(R.id.dialog_edit_image);
        TextView dialog_edit_name = dialog.findViewById(R.id.dialog_edit_name);
        TextView dialog_edit_number = dialog.findViewById(R.id.dialog_edit_number);
        TextView dialog_edit_btn = dialog.findViewById(R.id.dialog_edit_btn);
        ProgressBar progress_bar = dialog.findViewById(R.id.progress_bar);

        String name = contacts.getName();
        String number = contacts.getPhoneNumber();
        imageUri = Uri.parse(contacts.getImage());
        dialog_edit_name.setText(name);
        dialog_edit_number.setText(number);
        Glide.with(this).load(imageUri).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progress_bar.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                dialog_edit_image.setImageDrawable(resource);
                progress_bar.setVisibility(View.GONE);
                return false;
            }
        }).into(dialog_edit_image);

        dialog_edit_image.setOnClickListener(v -> {
            ImagePicker.with((Activity) this).compress(512).maxResultSize(512, 512).start();
        });

        dialog_edit_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialog_edit_name.getText().toString()) || TextUtils.isEmpty(dialog_edit_number.getText().toString())) {
                Toast.makeText(this, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
            } else {
                mDatabase.updateContacts(new Contacts(contacts.getId(), dialog_edit_name.getText().toString(), dialog_edit_number.getText().toString(), imageUri.toString()));
                Toast.makeText(this, "Contact Updated Successfully", Toast.LENGTH_SHORT).show();
                ((Activity) this).finish();
                this.startActivity(((Activity) this).getIntent());
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        Glide.with(this).load(imageUri).into(dialog_edit_image);
    }
}