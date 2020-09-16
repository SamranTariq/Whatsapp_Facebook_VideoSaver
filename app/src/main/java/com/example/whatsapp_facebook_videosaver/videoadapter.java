package com.example.whatsapp_facebook_videosaver;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.zip.Inflater;

public class videoadapter extends RecyclerView.Adapter<videoadapter.videoviwholder> {
    private final List<model> videolist;
    Context context;
    Videos videos;
    Uri uri;
    public videoadapter(Context context,List<model> videolist,Videos videos){
        this.context=context;
        this.videolist=videolist;
        this.videos=videos;
    }

    @NonNull
    @Override
    public videoviwholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      //  Toast.makeText(context,"oncreate",Toast.LENGTH_LONG).show();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.videoitem,parent,false);
        return new videoviwholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull videoviwholder holder, int position) {
        //Toast.makeText(context,"onbind",Toast.LENGTH_LONG).show();
//videoview.setLayoutParams(new FrameLayout.LayoutParams(550,550));
        model model=videolist.get(position);
        uri=model.getUri();
       // MediaController mediaController=new MediaController(context);
        //VideoView.setVideoURI(imageuri);
        holder.videoView.setVideoPath(String.valueOf(uri));
        holder.videoView.setVisibility(View.VISIBLE);
        holder.videoView.seekTo( 1 );
        //holder.videoView.setMediaController(mediaController);
        //holder.videoView.setZOrderOnTop(true);
       // holder.videoView.setBackgroundColor(Color.TRANSPARENT);
        /*holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0,0);
            }
        });
        holder.videoView.start();*/
        //holder.imageView.setImageBitmap(model.getThumb());
        /*String path=model.getPath();
        Uri uri= Uri.parse(path);
        holder.imageView.setVideoURI(uri);
        holder.imageView.seekTo(1);
        MediaController mediaController=new MediaController(context);
        holder.imageView.setMediaController(mediaController);*/
    }

    @Override
    public int getItemCount() {
        return videolist.size();
    }

    public class videoviwholder extends RecyclerView.ViewHolder{
        VideoView videoView;
        ImageView play,stop,share,status;
        ImageView imageButton;
        public videoviwholder(@NonNull View itemView) {
            super(itemView);
            share=itemView.findViewById(R.id.videoshare);
            videoView=itemView.findViewById(R.id.image);
            play=itemView.findViewById(R.id.playing);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.start();
                    play.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);
                }
            });
            stop=itemView.findViewById(R.id.stop);
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.pause();
                    stop.setVisibility(View.INVISIBLE);
                    play.setVisibility(View.VISIBLE);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=videolist.get(getAdapterPosition());
                    if (model!=null){
                        videos.share(model);
                    }
                }
            });
            status=itemView.findViewById(R.id.videostatus);
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=videolist.get(getAdapterPosition());
                    if (model!=null){
                        videos.status(model);
                    }
                }
            });
        //    play=itemView.findViewById(R.id.videoplay);
          /*  play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=videolist.get(getAdapterPosition());
                    //Log.d("mode", String.valueOf(model));
                    if (model!=null){
                        videos.show(model);
                    }
                }
            });*/
            imageButton=itemView.findViewById(R.id.imagesavebutton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=videolist.get(getAdapterPosition());
                    if (model!=null){
                        try {
                            videos.download(model);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull videoviwholder holder) {
        //Toast.makeText(context,"on",Toast.LENGTH_LONG).show();
        holder.videoView.seekTo( 1 );
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull videoviwholder holder) {
        //Toast.makeText(context,"n",Toast.LENGTH_LONG).show();
        holder.videoView.seekTo( 1 );
        super.onViewDetachedFromWindow(holder);
    }

}
