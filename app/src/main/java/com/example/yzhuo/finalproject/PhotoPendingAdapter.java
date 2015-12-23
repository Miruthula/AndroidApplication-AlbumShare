package com.example.yzhuo.finalproject;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yzhuo on 12/16/2015.
 */
public class PhotoPendingAdapter  extends RecyclerView.Adapter<PhotoPendingAdapter.ViewHolder> {

    List<ParseObject> items;
    Context mContext;
    FloatingActionButton fab;
    float historicX = Float.NaN, historicY = Float.NaN;


    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView image,delete;
        TextView createDate, creator;


        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.pendingCV);
            image = (ImageView) itemView.findViewById(R.id.imageView_pending_image);
            createDate = (TextView) itemView.findViewById(R.id.textView_pending_createDate);
            creator = (TextView) itemView.findViewById(R.id.textView_pending_creator);
            delete = (ImageView) itemView.findViewById(R.id.imageView_pending_delete);
        }
    }

    PhotoPendingAdapter(Context context, List<ParseObject> items, FloatingActionButton fab){
        this.items = items;
        this.mContext = context;
        this.fab = fab;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_pending_lv, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("phot id", items.get(position).getObjectId().toString());
        String imageUrl = ((ParseFile)items.get(position).get("image")).getUrl();
        Picasso.with(mContext)
                .load(imageUrl)
                .error(R.drawable.ic_action_default_icon)
                .into(holder.image);
        String[] parts = items.get(position).getCreatedAt().toString().split(" ");
        String createAt = parts[1] + " " + parts[2]+ " "+parts[5] ;
        holder.createDate.setText("Create at: " + createAt);


        ParseUser thisPerson = (ParseUser) items.get(position).get("uploader");
        final ParseQuery<ParseUser> uploader = new ParseQuery<ParseUser>("_User");
        uploader.whereEqualTo("objectId", thisPerson.getObjectId());
        uploader.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    holder.creator.setText("Author: " + objects.get(0).getString("name"));
                }
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(position).deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(mContext, "Deny", Toast.LENGTH_SHORT).show();
                            items.remove(position);
                            notifyDataSetChanged();
                        } else {
                            Log.d("delete photo", e.toString());
                        }
                    }
                });
            }
        });


        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                items.get(position).put("verify", true);
                items.get(position).saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(mContext, "Approved", Toast.LENGTH_SHORT).show();
                            InfoPush push = new InfoPush();
                            push.notification(((ParseUser) items.get(position).get("uploader")).getObjectId(), "Photo Approved");
                            items.remove(position);
                            notifyDataSetChanged();
                        } else {
                            Log.d("update photo", e.toString());
                        }
                    }
                });
            }
        });


        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
