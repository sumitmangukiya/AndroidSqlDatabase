package com.cysm.androidsqldatabase.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cysm.androidsqldatabase.interfaces.EditTaskItemClick;
import com.cysm.androidsqldatabase.R;
import com.cysm.androidsqldatabase.database.SqliteDatabase;
import com.cysm.androidsqldatabase.model.Contacts;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Contacts> listContacts;
    private ArrayList<Contacts> mArrayList;
    private SqliteDatabase mDatabase;

    EditTaskItemClick mEditTaskItemClick;

    public ContactAdapter(Context context, ArrayList<Contacts> listContacts, EditTaskItemClick mEditTaskItemClick) {
        this.context = context;
        this.listContacts = listContacts;
        this.mArrayList = listContacts;
        this.mEditTaskItemClick = mEditTaskItemClick;
        mDatabase = new SqliteDatabase(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_name.setText(listContacts.get(position).getName());
        holder.txt_number.setText(listContacts.get(position).getPhoneNumber());
        Glide.with(context).load(listContacts.get(position).getImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.img_image.setImageDrawable(resource);
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.img_image);
        holder.itemView.setOnLongClickListener(v -> {
            openSelectFieldDialog(position);
            return false;
        });

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listContacts = mArrayList;
                } else {
                    ArrayList<Contacts> filteredList = new ArrayList<>();
                    for (Contacts contacts : mArrayList) {
                        if (contacts.getName().toLowerCase().contains(charString)) {
                            filteredList.add(contacts);
                        }
                    }
                    listContacts = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listContacts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listContacts = (ArrayList<Contacts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_number;
        ImageView img_image;


        ProgressBar progressBar;
        ContactViewHolder(View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_number = itemView.findViewById(R.id.txt_number);
            img_image = itemView.findViewById(R.id.img_image);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    private void openSelectFieldDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_select_field);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        TextView dialog_edit_btn = dialog.findViewById(R.id.dialog_edit_btn);
        TextView dialog_delete_btn = dialog.findViewById(R.id.dialog_delete_btn);

        dialog_delete_btn.setOnClickListener(view -> {
            mDatabase.deleteContact(listContacts.get(position).getId());
            ((Activity) context).finish();
            context.startActivity(((Activity) context).getIntent());
            dialog.dismiss();
        });

        dialog_edit_btn.setOnClickListener(view -> {
            mEditTaskItemClick.onEditClick(listContacts.get(position));
            dialog.dismiss();
        });
    }


}
