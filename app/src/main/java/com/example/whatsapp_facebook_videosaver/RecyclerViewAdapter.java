package com.example.whatsapp_facebook_videosaver;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geniusforapp.fancydialog.FancyAlertDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Tushar on 9/10/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Files> filesList;

    public RecyclerViewAdapter(Context context, ArrayList<Files> filesList) {
        this.context = context;
        this.filesList = filesList;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row,null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {
        final Files files = filesList.get(position);
        final Uri uri = Uri.parse(files.getUri().toString());
        final File file = new File(uri.getPath());
        holder.userName.setText(files.getName());
        if(files.getUri().toString().endsWith(".mp4"))
        {
            holder.playIcon.setVisibility(View.VISIBLE);
        }else{
            holder.playIcon.setVisibility(View.INVISIBLE);
        }
        Glide.with(context)
                .load(files.getUri())
                .into(holder.savedImage);
        holder.savedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(files.getUri().toString().endsWith(".mp4")){
                    Uri VideoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",file);
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setDataAndType(VideoURI, "video/*");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                    }
                }else if(files.getUri().toString().endsWith(".jpg")){
                    Uri VideoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",file);
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setDataAndType(VideoURI, "image/*");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        holder.deleteID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = filesList.get(position).getPath();
                final File file = new File(path);
                FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                        .setTextTitle("DELETE?")
                        .setBody("Are you sure you want to delete this file?")
                        .setNegativeColor(R.color.colorNegative)
                        .setNegativeButtonText("Cancel")
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButtonText("Delete")
                        .setPositiveColor(R.color.colorPositive)
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                try {
                                    if (file.exists()) {
                                        boolean del = file.delete();
                                        filesList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, filesList.size());
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "File was Deleted", Toast.LENGTH_SHORT).show();
                                        if(del){
                                            MediaScannerConnection.scanFile(
                                                    context,
                                                    new String[]{ path, path},
                                                    new String[]{ "image/jpg","video/mp4"},
                                                    new MediaScannerConnection.MediaScannerConnectionClient()
                                                    {
                                                        public void onMediaScannerConnected()
                                                        {
                                                        }
                                                        public void onScanCompleted(String path, Uri uri)
                                                        {
                                                            Log.d("Video path: ",path);
                                                        }
                                                    });
                                        }
                                    }
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    // TODO let the user know the file couldn't be deleted
                                    e.printStackTrace();
                                }
                            }
                        })
                        .build();
                alert.show();
            }
        });
        holder.shareID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mainUri = Uri.fromFile(file);
                if(files.getUri().toString().endsWith(".jpg")){
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                    }
                }else if(files.getUri().toString().endsWith(".mp4")){
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView savedImage;
        ImageView playIcon;
        ImageView shareID, deleteID;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.profileUserName);
            savedImage = (ImageView) itemView.findViewById(R.id.mainImageView);
            playIcon = (ImageView) itemView.findViewById(R.id.playButtonImage);
            shareID = (ImageView) itemView.findViewById(R.id.shareID);
            deleteID = (ImageView) itemView.findViewById(R.id.deleteID);
        }
    }
}
