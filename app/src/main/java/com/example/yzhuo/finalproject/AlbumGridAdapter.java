package com.example.yzhuo.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yzhuo on 12/13/2015.
 */
public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.ViewHolder> {

    List<ParseObject> items;
    Context mContext;
    String albumid;




    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView image;
        TextView title, createDate, updateDate, creator;


        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.albumCV_grid);
            image = (ImageView) itemView.findViewById(R.id.imageView_gv_image);
            title = (TextView) itemView.findViewById(R.id.textView_gv_title);
        }
    }


    AlbumGridAdapter(Context context, List<ParseObject> items){
        this.items = items;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_grid_view,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //setting view
        holder.title.setText(items.get(position).get("title").toString());
        ParseFile thisImage = (ParseFile) items.get(position).get("cover");
        String imageUrl = thisImage.getUrl();
        Picasso.with(mContext)
                .load(imageUrl)
                .error(R.drawable.ic_action_default_icon)
                .into(holder.image);
        //setting view done


        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumid = items.get(position).getObjectId();
                Intent intent = new Intent(mContext,Createalbum_Activity.class);
                intent.putExtra("albumid",albumid);
                mContext.startActivity(intent);

                Toast.makeText(mContext, "Open Photo View", Toast.LENGTH_SHORT).show();
            }
        });

        holder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                albumid = items.get(position).getObjectId();
                Intent intent = new Intent(mContext,settingsalbumActivity.class);
                intent.putExtra("albumid",albumid);
                mContext.startActivity(intent);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
