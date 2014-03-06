package com.zyy360.app.listener;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zyy360.app.adapters.VideoListAdapter;
import com.zyy360.app.data.VideoDB;
import com.zyy360.app.model.VideoDataFormat;
import com.zyy360.app.ui.PlayActivity;

/**
 * @author daimajia
 * @modified Foxhu
 * @version 1.0
 */
public class VideoListItemListener implements View.OnClickListener {
    private VideoDataFormat mData;
    private Context mContext;
    private VideoDB mVideoDB;
    private VideoListAdapter mAdapter;

    public VideoListItemListener(Context context, VideoDataFormat data) {
        mData = data;
        mContext = context;
        mVideoDB = new VideoDB(mContext, VideoDB.NAME, null, VideoDB.VERSION);
        mAdapter = null;
    }

    public VideoListItemListener(Context context, VideoListAdapter adapter,
                                 VideoDataFormat data) {
        this(context, data);
        mAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, PlayActivity.class);
        intent.putExtra("VideoInfo", mData);
        mContext.startActivity(intent);
        mVideoDB.insertWatched(mData);
        if (mAdapter != null) {
            if (mData.isWatched() == false)
                mAdapter.setWatched(mData);
        }
    }
}
