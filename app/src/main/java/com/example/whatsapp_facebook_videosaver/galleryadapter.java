package com.example.whatsapp_facebook_videosaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geniusforapp.fancydialog.FancyAlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class galleryadapter extends RecyclerView.Adapter<galleryadapter.ViewHolder> {
    private Context context;
    private ArrayList<gallerymodel> arrayList;
    private Activity parentActivity;

    public galleryadapter(Context context, ArrayList<gallerymodel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.galleryitem,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final gallerymodel gallerymodel=arrayList.get(position);
        final Uri uri = Uri.parse(gallerymodel.getUri().toString());
        final File file = new File(uri.getPath());
        if(gallerymodel.isVideo()==true)
        {
            holder.galleryplay.setVisibility(View.VISIBLE);
        }else{
            holder.galleryplay.setVisibility(View.INVISIBLE);
        }
        holder.galleryimage.setImageBitmap(gallerymodel.getThumb());
        holder.galleryimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gallerymodel.isVideo()){
                    Uri path=gallerymodel.getUri();
                    Intent intent=new Intent(context,fullscreenvideo.class);
                    intent.setData(path);
                    intent.putExtra("valuemodel", gallerymodel.getPath());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //    intent.putExtra("valuemodel", model);
                    context.startActivity(intent);
                }else {
                    File filesaver=new File(gallerymodel.getPath());
                    Intent intent=new Intent(context,fullscreen.class);
                    intent.setData(Uri.fromFile(filesaver));
                    intent.putExtra("valuemodel", gallerymodel.getPath());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
/*        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gallerymodel.getUri().toString().endsWith(".mp4")){
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
                }else if(gallerymodel.getUri().toString().endsWith(".jpg")){
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
        holder.repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mainUri = Uri.fromFile(file);
                if(gallerymodel.getUri().toString().endsWith(".jpg")){
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setPackage("com.whatsapp");
                    try {
                        context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                    }
                }else if(gallerymodel.getUri().toString().endsWith(".mp4")){
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setPackage("com.whatsapp");
                    try {
                        context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });*/
        holder.gallerydelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = arrayList.get(position).getPath();
                final File file = new File(path);
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(new ContextThemeWrapper(v.getRootView().getContext(),R.style.Widget_Sphinx_ActionBar_Solid));
                alertbox.setMessage("Are you sure you want to delete this file?");
                alertbox.setTitle("Warning");
                alertbox.setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                try {
                                    if (file.exists()) {
                                        boolean del = file.delete();
                                        arrayList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, arrayList.size());
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "File was Deleted", Toast.LENGTH_SHORT).show();
                                        if (del) {
                                            MediaScannerConnection.scanFile(
                                                    context,
                                                    new String[]{path, path},
                                                    new String[]{"image/jpg", "video/mp4"},
                                                    new MediaScannerConnection.MediaScannerConnectionClient() {
                                                        public void onMediaScannerConnected() {
                                                        }

                                                        public void onScanCompleted(String path, Uri uri) {
                                                            Log.d("Video path: ", path);
                                                        }
                                                    });
                                        }
                                    }
                                    alertbox.setCancelable(true);
                                } catch (Exception e) {
                                    // TODO let the user know the file couldn't be deleted
                                    e.printStackTrace();
                                }
                            }
                        });
                alertbox.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            alertbox.setCancelable(true);
                            }
                        }).show();
            }
            });/*
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mainUri = Uri.fromFile(file);
                if(gallerymodel.getUri().toString().endsWith(".jpg")){
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                    }
                }else if(gallerymodel.getUri().toString().endsWith(".mp4")){
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
*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView galleryimage,gallerydelete,galleryplay;
     /*   TextView user;
        ImageView Image;
        ImageView play;
        ImageView repost, share, delete;*/
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
         /*   user = (TextView) itemView.findViewById(R.id.profileUserName);
            Image = (ImageView) itemView.findViewById(R.id.mainImageView);
            play = (ImageView) itemView.findViewById(R.id.playButtonImage);
            repost = (ImageView) itemView.findViewById(R.id.repostID);
            share = (ImageView) itemView.findViewById(R.id.shareID);
            delete = (ImageView) itemView.findViewById(R.id.deleteID);*/
         galleryimage=(ImageView)itemView.findViewById(R.id.imageviewgalleryitem);
         gallerydelete=(ImageView)itemView.findViewById(R.id.imageViewdelete);
         galleryplay=(ImageView)itemView.findViewById(R.id.playgalleryvideo);
        }
    }
}
