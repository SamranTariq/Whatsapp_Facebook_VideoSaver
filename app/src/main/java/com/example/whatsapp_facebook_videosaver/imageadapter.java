package com.example.whatsapp_facebook_videosaver;

import android.content.Context;
import android.graphics.ImageFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.zip.Inflater;

public class imageadapter extends RecyclerView.Adapter<imageadapter.imageviwholder> {
    private final List<model> imagelist;
    Context context;
    Images images;
    public imageadapter(Context context,List<model> imagelist,Images images){
        this.context=context;
        this.imagelist=imagelist;
        this.images=images;
    }
    @NonNull
    @Override
    public imageviwholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.imageitem,parent,false);
        return new imageviwholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull imageviwholder holder, int position) {
        model model=imagelist.get(position);
        if (position==0){
            Log.d("p","0");
            holder.imageButton.setVisibility(View.INVISIBLE);
            //     holder.cardView.setVisibility(View.INVISIBLE);
        }
        holder.imageView.setImageBitmap(model.getThumb());
    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class imageviwholder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageButton imageButton;
        public imageviwholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            imageButton=itemView.findViewById(R.id.imagesavebutton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=imagelist.get(getAdapterPosition());
                    if (model!=null){
                        try {
                            images.download(model);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model model=imagelist.get(getAdapterPosition());
                    //Log.d("mode", String.valueOf(model));
                    if (model!=null){
                        images.show(model);
                    }
                }
            });

        }
    }
}
