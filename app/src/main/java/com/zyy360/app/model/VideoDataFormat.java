package com.zyy360.app.model;

import android.database.Cursor;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.Serializable;

/**
 * @author Foxhu
 * @version 1.0
 */
public class VideoDataFormat implements Serializable {
    public  Integer id;
    public  String name;//视频名称
    public  String path;//视频地址
    public  String video_pic;//视频图片
    public  String video_thumbpic; //视频缩略图
    public  String introduce;//视频简介
    public  String create_time;

    //缩略图地址 http://192.168.0.108:8080/uploads/videopics/20131025/IMG_BjTA_25_s.jpg
    //大图地址 http://192.168.0.108:8080/uploads/videopics/20131025/IMG_BjTA_25_b.jpg
    //视频地址 http://192.168.0.108:8080/uploads/videofiles/2013/10/25_152747_61dP.flv
    private boolean IsWatched;
    private final String VideoUrlFormat = "http://192.168.0.108:8080/uploads/videofiles/%s";
    private final String PicUrlFormat = "http://192.168.0.108:8080/uploads/videopics/%s";


    public static final String NONE_VALUE = "-1";
    private VideoDataFormat(Integer id, String name, String path,String video_pic,
                            String video_thumbPic,String introduce,String create_time)
    {
        super();
        this.id = id;
        this.name = name;
        this.path = String.format(VideoUrlFormat, path);//根据原始地址构建完整url地址
        this.video_pic = String.format(PicUrlFormat, video_pic);
        this.video_thumbpic = String.format(PicUrlFormat, video_thumbPic);
        this.introduce = introduce;
        this.create_time = create_time;
    }

    private VideoDataFormat(JSONObject object){
        id = Integer.valueOf(getValue(object,"___key_id"));
        name = getValue(object, "name");
        path = String.format(VideoUrlFormat, getValue(object, "path"));
        video_pic = String.format(PicUrlFormat, getValue(object,"video_pic"));
        video_thumbpic = String.format(PicUrlFormat, getValue(object,"video_thumbpic"));
        introduce = getValue(object,"introduce");
        create_time = getValue(object,"create_time");
        IsWatched = false;
    }

    private static String getValue(JSONObject object, String key) {
        try {
            return object.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return NONE_VALUE;
    }

    public boolean isWatched() {
        return IsWatched;
    }

    public void setWatched(Boolean watch) {
        IsWatched = watch;
    }

    public static VideoDataFormat build(JSONObject object) {
        return new VideoDataFormat(object);
    }
    public static VideoDataFormat build(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String path = cursor.getString(cursor.getColumnIndex("path"));
        String video_pic = cursor.getString(cursor.getColumnIndex("video_pic"));
        String video_thumbPic = cursor.getString(cursor.getColumnIndex("video_thumbpic"));
        String introduce = cursor.getString(cursor.getColumnIndex("introduce"));
        String create_time = cursor.getString(cursor.getColumnIndex("create_time"));

        return new VideoDataFormat(id, name, path,video_pic,video_thumbPic,introduce,create_time);
    }
}
