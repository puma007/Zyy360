package com.zyy360.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zyy360.app.R;
import com.zyy360.app.data.VideoDB;
import com.zyy360.app.model.VideoDataFormat;
import com.zyy360.app.utils.OrientationHelper;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.CenterLayout;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.widget.RelativeLayout.LayoutParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author daimajia
 * @modified Foxhu
 * @version 1.0
 */
public class PlayActivity extends ActionBarActivity  implements OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, Target {
    private TextView mTitleTextView;//标题
    private TextView mContentTextView;//内容
    private TextView mAutherTextView;//作者
    private ShareActionProvider mShareActionProvider;//分享按钮
    private VideoView mVideoView;//视频播放界面
    private Button mZoomButton;//放大按钮
    private ImageView mDetailImageView;//图片视图
    private ImageButton mPlayButton;//播放按钮
    //private GifMovieView mLoadingGif;
    private MediaController mVideoControls;//视频控制
    private RelativeLayout mHeaderWrapper;
    private int mCurrentScape;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private VideoDB mVideoDB;
    private View mVideoAction;
    private VideoDataFormat mVideoInfo;
    private OrientationEventListener mOrientationEventListener;
    private MenuItem mFavMenuItem;
    private Long mPreviousPlayPosition = 0l;
    private Bitmap mDetailPicture;
    private final String mDir = "Zyy360";
    private final String mShareName = "zyy360-share.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check if there exist VitamioLibs
        if (!LibsChecker.checkVitamioLibs(this)){
            return;
        }
        mContext = this;
        mVideoDB = new VideoDB(mContext, VideoDB.NAME, null, VideoDB.VERSION);
        mVideoInfo = (VideoDataFormat) (getIntent().getExtras().getSerializable("VideoInfo"));
        setContentView(R.layout.activity_play);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mVideoControls = (MediaController) findViewById(R.id.media_play_controler);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVideoView.setMediaController(mVideoControls);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.pause();
        mVideoView.setVideoPath(mVideoInfo.path);//设置视频路径
        mCurrentScape = OrientationHelper.PORTRAIT;//设置当前播放模式为PORTRAIT竖屏
        mTitleTextView = (TextView) findViewById(R.id.title);
        mContentTextView = (TextView) findViewById(R.id.content);
        mDetailImageView = (ImageView) findViewById(R.id.detailPic);
        mVideoAction = (View) findViewById(R.id.VideoAction);
        mAutherTextView = (TextView) findViewById(R.id.author);
        mPlayButton = (ImageButton) findViewById(R.id.play_button);
        //mLoadingGif = (GifMovieView) findViewById(R.id.loading_gif);
        mHeaderWrapper = (RelativeLayout) findViewById(R.id.HeaderWrapper);
        mZoomButton = (Button) findViewById(R.id.screen_btn);
        mZoomButton.setOnClickListener(this);
        mTitleTextView.setText(mVideoInfo.name);
        mContentTextView.setText(mVideoInfo.introduce);

        mPlayButton.setOnClickListener(this);
        Picasso.with(mContext).load(mVideoInfo.video_pic)
                .placeholder(R.drawable.big_bg).into(this);
        mOrientationEventListener = new OrientationEventListener(mContext) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (mVideoView.isPlaying()) {
                    int tending = OrientationHelper.userTending(orientation,
                            mCurrentScape);
                    if (tending != OrientationHelper.NOTHING) {
                        if (tending == OrientationHelper.LANDSCAPE) {
                            setFullScreenPlay();
                        } else if (tending == OrientationHelper.PORTRAIT) {
                            setSmallScreenPlay();
                        }
                    }
                }
            }
        };

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }


    private void setFullScreenPlay() {
        mVideoControls.hide();
        if (Build.VERSION.SDK_INT >= 9) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setPlayerWindowSize(FULL_WIDTH, FULL_HEIGHT, false);
        mCurrentScape = OrientationHelper.LANDSCAPE;
        mZoomButton.setBackgroundResource(R.drawable.screensize_zoomin_button);
    }

    private void setSmallScreenPlay() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setPlayerWindowSize(FULL_WIDTH,
                getResources().getDimensionPixelSize(R.dimen.player_height),
                true);
        mCurrentScape = OrientationHelper.PORTRAIT;
        mZoomButton.setBackgroundResource(R.drawable.screensize_zoomout_button);
    }
    private final int FULL_WIDTH = -1;
    private final int FULL_HEIGHT = -1;

    private void setPlayerWindowSize(int width, int height,
                                     boolean actionbarVisibility) {
        if (actionbarVisibility) {
            getSupportActionBar().show();
        } else {
            getSupportActionBar().hide();
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams headerParams = (LayoutParams) mHeaderWrapper
                .getLayoutParams();
        CenterLayout.LayoutParams videoParams = (io.vov.vitamio.widget.CenterLayout.LayoutParams) mVideoView
                .getLayoutParams();
        if (width == FULL_WIDTH) {
            headerParams.width = metrics.widthPixels;
            videoParams.width = metrics.widthPixels;
        } else {
            headerParams.width = width;
            videoParams.width = width;
        }
        if (height == FULL_HEIGHT) {
            headerParams.height = metrics.heightPixels;
            videoParams.height = metrics.heightPixels;
        } else {
            headerParams.height = height;
            videoParams.height = height;
        }
        mHeaderWrapper.setLayoutParams(headerParams);
        mHeaderWrapper.requestLayout();

        mVideoView.setLayoutParams(videoParams);
        mVideoView.requestFocus();
        mVideoView.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("VideoPosition", mVideoView.getCurrentPosition());
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
    public void onPrepared(MediaPlayer mp) {
        mp.setPlaybackSpeed(0.999999f);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_button) {
            v.setVisibility(View.INVISIBLE);
            //mVideoView.setCanBePlayed(true);
            mVideoAction.setVisibility(View.INVISIBLE);
            mVideoView.start();
            setPlayerWindowSize(FULL_WIDTH, getResources()
                    .getDimensionPixelSize(R.dimen.player_height), true);
        }
        if (v.getId() == R.id.screen_btn) {
            if (mOrientationEventListener != null) {
                mOrientationEventListener.disable();
            }

            if (mCurrentScape == OrientationHelper.LANDSCAPE) {
                setSmallScreenPlay();
            } else if (mCurrentScape == OrientationHelper.PORTRAIT) {
                setFullScreenPlay();
            }
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //mLoadingGif.setVisibility(View.INVISIBLE);
                mPlayButton.setVisibility(View.VISIBLE);
            } else {
                mVideoControls.hide();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurrentScape == OrientationHelper.LANDSCAPE) {
                setSmallScreenPlay();
                return true;
            } else {
                prepareStop();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.setVolume(1.0f, 1.0f);
        mVideoView.seekTo(mPreviousPlayPosition);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView.isPlaying() == false) {
            mVideoView.setVolume(0.0f, 0.0f);
        } else {
            mPreviousPlayPosition = mVideoView.getCurrentPosition();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrientationEventListener != null)
            mOrientationEventListener.disable();
    }

    /**
     * 这是播放器的一个Bug,要是直接退出就会出现杂音，一定要在播放状态退出 才不会有杂音
     */
    private void prepareStop() {
        mVideoView.setVolume(0.0f, 0.0f);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        mDetailImageView.setImageBitmap(bitmap);
        mDetailPicture = bitmap;

        mPlayButton.setVisibility(View.VISIBLE);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        mDetailPicture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File dir = new File(Environment.getExternalStorageDirectory()
                + File.separator + mDir);
        if (dir.exists() == false || dir.isDirectory() == false)
            dir.mkdir();

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + mDir + File.separator + mShareName);
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBitmapFailed() {

    }


}
