package com.ferit.kele.wallchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

/**
 * Created by Kele on 14.7.2017..
 */

class MsgAdapter extends BaseAdapter{

    ArrayList<Msg> mMessages;

    public MsgAdapter(ArrayList<Msg> messages) {
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return this.mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder msgViewHolder;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.message, parent, false);
            msgViewHolder = new ViewHolder(convertView);
            convertView.setTag(msgViewHolder);
        } else {
            msgViewHolder = (ViewHolder) convertView.getTag();
        }
        Msg msg = this.mMessages.get(position);
        msgViewHolder.tvNickname.setText(msg.getNickname());
        if (msg.getType()==1){
            msgViewHolder.tvMessage.setText(msg.getMessage());
            msgViewHolder.ivPic.setImageBitmap(null);
        } else {
            byte[] imgBytes = Base64.decode(msg.getMessage().getBytes(), Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            Log.e(TAG, String.valueOf(image) + image.getWidth() + image.getHeight());
            msgViewHolder.ivPic.setImageBitmap(Bitmap.createScaledBitmap(image, 640 , 480, false));
            msgViewHolder.tvMessage.setText("");
        }
        msgViewHolder.tvLocTim.setText(msg.getLocation() + ", " + msg.getTime());
        return convertView;
    }

    public void clear() {
        mMessages.clear();
    }

    private class ViewHolder {
        TextView tvLocTim, tvNickname, tvMessage;
        ImageView ivPic;

        public ViewHolder(View msgView) {
            ivPic = (ImageView) msgView.findViewById(R.id.ivPic);
            tvLocTim = (TextView) msgView.findViewById(R.id.tvLocTim);
            tvNickname = (TextView) msgView.findViewById(R.id.tvNickname);
            tvMessage = (TextView) msgView.findViewById(R.id.tvMessage);
        }
    }

    public void add(Msg message){
        this.mMessages.add(message);
        this.notifyDataSetChanged();
    }
}
