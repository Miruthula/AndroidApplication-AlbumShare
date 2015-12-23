package com.example.yzhuo.finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Miruthulavarshini on 12/10/2015.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    List<ParseObject> items;
    ParseFile imagefile;
    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout gridAll;
        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageViewphoto);
            gridAll = (RelativeLayout) itemView.findViewById(R.id.Gridall);


        }
    }
    GridAdapter(Context context, List<ParseObject> items){
        this.items = items;
        this.mContext = context;}


        @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }




    @Override
    public void onBindViewHolder(GridAdapter.ViewHolder holder, final int position) {
        imagefile = (ParseFile) items.get(position).get("image");
        final String imageUrl = imagefile.getUrl();//live url
        Uri imageUri = Uri.parse(imageUrl);
         Picasso.with(holder.image.getContext()).load(imageUri.toString()).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoDetailActivity.class);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("objectId", items.get(position).getObjectId().toString());
                String[] parts;
                parts = items.get(position).getCreatedAt().toString().split(" ");
                String date = parts[1] + " " + parts[2]+ " "+parts[5] ;
                intent.putExtra("date", date);
                String uploader = ((ParseObject) items.get(position).get("uploader")).getObjectId();
                intent.putExtra("ownerId", uploader);
                Log.d("uploaderID", uploader);
                mContext.startActivity(intent);
            }
        });


    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
