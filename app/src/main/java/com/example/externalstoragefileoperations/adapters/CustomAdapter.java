package com.example.externalstoragefileoperations.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.externalstoragefileoperations.MainActivity;
import com.example.externalstoragefileoperations.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m794802 on 3/25/18.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.DataHolder> {

    private Context mcontext;
    private List fileName;
    private List fileSize;

    public CustomAdapter(MainActivity mainActivity, List<String> fileNamesUpdated, List<Long> fileSizeUpdated) {
        this.mcontext = mainActivity;
        this.fileName = fileNamesUpdated;
        this.fileSize = fileSizeUpdated;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View layout = layoutInflater.inflate(R.layout.item_adapter, parent, false);
        return new DataHolder(layout);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        holder.fileName.setText(fileName.get(position).toString());
        holder.fileSize.setText(fileSize.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return fileName.size();
    }

    public List getFileName(){
        return fileName;
    }

    public List getFileSize(){
        return fileSize;
    }

    public class DataHolder extends RecyclerView.ViewHolder {

        TextView fileName, fileSize;

        public DataHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);

        }
    }
}
