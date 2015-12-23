package com.example.yzhuo.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by leif on 12/13/2015.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    Context context;
    List<ParseObject> messages;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView sender;
        TextView messagePreview, date;
        LinearLayout clickableLine;

        public ViewHolder(View itemView) {
            super(itemView);
            sender = (TextView)itemView.findViewById(R.id.textView_senderName);
            messagePreview = (TextView)itemView.findViewById(R.id.textView_messagePreview);
            date = (TextView) itemView.findViewById(R.id.textView_message_date);
            clickableLine = (LinearLayout)itemView.findViewById(R.id.messageLayout);
        }
    }

    public InboxAdapter(Context context, List<ParseObject> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_view,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String s = messages.get(position).get("sender").toString();
        final String owner = ((ParseUser)messages.get(position).get("owner")).getObjectId();
        final String m = messages.get(position).get("message").toString();
        final String id = messages.get(position).getObjectId();
        final boolean isRead = (boolean) messages.get(position).get("isRead");
        holder.sender.setText(s);
        Log.d("message", messages.get(position).get("message").toString());
        holder.messagePreview.setText(m);
        String [] parts;
        parts = messages.get(position).getCreatedAt().toString().split(" ");
        String createAt = parts[1] + " " + parts[2]+ " "+parts[5] ;
        holder.date.setText(createAt);
        if(!isRead){
            holder.sender.setTypeface(Typeface.DEFAULT_BOLD);
        }
        holder.clickableLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch new view with message
                ParseObject message = ParseObject.createWithoutData("Message",messages.get(position).getObjectId());
                message.put("isRead", true);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(context, MessageDetailActivity.class);
                            intent.putExtra("senderName", s);
                            intent.putExtra("owner",owner);
                            intent.putExtra("message", m);
                            intent.putExtra("messageId",id);
                            context.startActivity(intent);
                        } else {
                            Log.d("Message Save", e.toString());
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
