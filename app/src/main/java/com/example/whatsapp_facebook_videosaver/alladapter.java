package com.example.whatsapp_facebook_videosaver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
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

public class alladapter extends RecyclerView.Adapter<alladapter.allviwholder> {
    private final List<model> alllist;
    Context context;
    AllStatus allStatus;
    public alladapter(Context context,List<model> videolist,AllStatus allStatus){
        this.context=context;
        this.alllist=videolist;
        this.allStatus=allStatus;
    }
    @NonNull
    @Override
    public allviwholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.allitem,parent,false);
        return new allviwholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull allviwholder holder, int position) {
        if (position==0){
            Log.d("p","0");
            holder.imageButton.setVisibility(View.INVISIBLE);
       //     holder.cardView.setVisibility(View.INVISIBLE);
        }
        model model=alllist.get(position);
    //    Uri s=model.getUri();
      //  Toast.makeText(context, String.valueOf(s),Toast.LENGTH_LONG).show();
     //   alllist.remove(null); // here I remove any preview added null value as first (0 position) element
        //alllist.add(1, null);
       // Bitmap bitmap=model.getThumb();
        //if (bitmap.equals(null)){
          //  Toast.makeText(context,"e",Toast.LENGTH_LONG).show();
        //}
        holder.imageView.setImageBitmap(model.getThumb());
        //holder.imageView.setImageURI(model.getUri());
        if (model.isVideo()==true){
            holder.imageViewplay.setVisibility(View.VISIBLE);
        }else {
            holder.imageViewplay.setVisibility(View.INVISIBLE);
        }
        /*String path=model.getPath();
        Uri uri= Uri.parse(path);
        holder.imageView.setVideoURI(uri);
        holder.imageView.seekTo(1);
        MediaController mediaController=new MediaController(context);
        holder.imageView.setMediaController(mediaController);*/
    }

    @Override
    public int getItemCount() {
        return alllist.size();
    }

    public class allviwholder extends RecyclerView.ViewHolder{
        ImageView imageView,imageViewplay;
        ImageButton imageButton;
        CardView cardView;
        public allviwholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            imageButton=itemView.findViewById(R.id.alldownload);
            imageViewplay=itemView.findViewById(R.id.videoplay);
            cardView=itemView.findViewById(R.id.card);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=alllist.get(getAdapterPosition());
                    if (model!=null){
                        try {
                            allStatus.download(model);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=alllist.get(getAdapterPosition());
                    if (model!=null){
                        if (model.isVideo()==true){
                            allStatus.videoshow(model);
                        }else {
                            allStatus.show(model);
                        }
                    }
                }
            });
        }
    }
}
