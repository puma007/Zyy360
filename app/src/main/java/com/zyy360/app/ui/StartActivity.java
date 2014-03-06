package com.zyy360.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AbsListView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.zyy360.app.R;
import com.zyy360.app.adapters.VideoListAdapter;
import com.zyy360.app.core.DataVideoFetcher;
import com.zyy360.app.data.VideoDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author daimajia
 * @modified Foxhu
 * @version 1.0
 */

public class StartActivity extends ActionBarActivity implements
        AbsListView.OnScrollListener {
    private ListView mVideoList;
    private VideoListAdapter mVideoAdapter;
    private Context mContext;
    private int mCurrentPage = 1;
    private Boolean mUpdating = false;
    private LayoutInflater mLayoutInflater;//layout object
    private View mLoadView;
    private VideoDB mVideoDB;
    private int mDefaultPrepareCount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //初始化数据库
        mVideoDB = new VideoDB(mContext, VideoDB.TABLE_VIDEO_NAME, null,VideoDB.VERSION);
        setContentView(R.layout.activity_start);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mVideoList = (ListView) findViewById(R.id.videoList);//获得listview组件
        mLayoutInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoadView = mLayoutInflater.inflate(R.layout.load_item, null);//设置正在加载进度条
        mVideoList.setOnScrollListener(this);
        if (getIntent().hasExtra("LoadData")) {
            init(getIntent().getStringExtra("LoadData"));
        } else {
            init();
        }
    }

    public void init() {
        Cursor cursor = mVideoDB.getVideos(mDefaultPrepareCount);
        mVideoAdapter = VideoListAdapter.build(mContext, cursor, true);
        mVideoList.setAdapter(mVideoAdapter);
    }

    public void init(String data) {
        try {
            JSONArray videoList = new JSONArray(data);
            if (videoList != null) {
                new AddToDBThread(videoList).start();
            }
            mVideoAdapter = VideoListAdapter.build(mContext, videoList, true);
            mVideoList.setAdapter(mVideoAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mUpdating == false && totalItemCount != 0
                && view.getLastVisiblePosition() == totalItemCount - 1) {
            mUpdating = true;
            DataVideoFetcher.instance().getList(mCurrentPage++, new LoadMoreJSONListener());
        }
    }

    private class LoadMoreJSONListener extends JsonHttpResponseHandler {

        private View mFooterView;

        public LoadMoreJSONListener() {
            mUpdating = true;
        }

        @Override
        public void onSuccess(int statusCode, JSONArray response) {
            super.onSuccess(statusCode, response);
            if (statusCode == 200 && response.length()>0) {
                try {
                    if (mCurrentPage < 3) {
                        new AddToDBThread(response).start();
                    }
                    mVideoAdapter.addVideosFromJsonArray(response);
                    mVideoAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            mCurrentPage--;
        }

        @Override
        public void onStart() {
            super.onStart();
            mFooterView = mLayoutInflater.inflate(R.layout.load_item, null);
            mVideoList.addFooterView(mFooterView);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mUpdating = false;
            mVideoList.removeFooterView(mFooterView);
        }
    }


    public class AddToDBThread extends Thread {
        private JSONArray mVideos;
        private Boolean mReoveAllWithoutFav;

        public AddToDBThread(JSONArray videos) {
            mVideos = videos;
        }

        @Override
        public void run() {
            super.run();
            mVideoDB.insertVideos(mVideos);
        }
    }
}
