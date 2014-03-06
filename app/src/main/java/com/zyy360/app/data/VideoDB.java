package com.zyy360.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.zyy360.app.model.VideoDataFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author daimajia
 * @modified Foxhu
 * @version 1.0
 */
public class VideoDB extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String NAME = "Zyy360";

    public static final String TABLE_VIDEO_NAME = "Video";
    public static final String TABLE_WATCHED_NAME = "Watched";
    public  Integer id;
    public  String name;//视频名称
    public  String path;//视频地址
    public  String video_pic;//视频图片
    public  String video_thumbpic; //视频缩略图
    public  String introduce;//视频简介
    public  String create_time;

    private static final String DATABASE_VIDEO_CREATE = "create table Video(_id integer primary key autoincrement, "
            + "id text not null UNIQUE,"
            + "name text not null,"
            + "path text not null,"
            + "video_pic text not null,"
            + "video_thumbpic text not null,"
            + "introduce text not null,"
            + "create_time text not null" + ");";
    private static final String DATABASE_FAV_WATCHED = "create table Watched("
            + "_id integer primary key autoincrement," + "vid integer UNIQUE)";


    public VideoDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_VIDEO_CREATE);
        db.execSQL(DATABASE_FAV_WATCHED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertVideo(JSONObject video) {
        insertVideo(VideoDataFormat.build(video));
    }

    public void insertVideos(JSONArray videos) {
        for (int i = 0; i < videos.length(); i++) {
            try {
                insertVideo(videos.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public long insertVideo(VideoDataFormat video) {
        ContentValues values = new ContentValues();
        values.put("id", video.id);
        values.put("name", video.name);
        values.put("path", video.path);
        values.put("video_pic", video.video_pic);
        values.put("video_thumbpic", video.video_thumbpic);
        values.put("introduce", video.introduce);
        values.put("create_time", video.create_time);
        return getWritableDatabase().insertWithOnConflict(TABLE_VIDEO_NAME,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public long insertWatched(VideoDataFormat video) {
        ContentValues values = new ContentValues();
        values.put("vid", video.id);
        return getWritableDatabase().insertWithOnConflict(TABLE_WATCHED_NAME,
                null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean isWatched(VideoDataFormat video) {
        SQLiteStatement statement = getReadableDatabase().compileStatement(
                "select count(*) from " + TABLE_WATCHED_NAME + " where vid="
                        + video.id);
        if (statement.simpleQueryForLong() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public VideoDataFormat getVideoDetail(int _id) {
        Cursor cursor = getReadableDatabase().query(TABLE_VIDEO_NAME, null,
                "_id=?", new String[] { String.valueOf(_id) }, null, null,
                null, "1");//1代表只取1条数据
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            return VideoDataFormat.build(cursor);
        } else {
            return null;
        }
    }

    public long getVideosCount() {
        SQLiteStatement statement = getReadableDatabase().compileStatement(
                "select count(*) from " + TABLE_VIDEO_NAME);
        return statement.simpleQueryForLong();
    }

    public Cursor getVideos(int count) {
        return getReadableDatabase().query(TABLE_VIDEO_NAME, null, null, null,
                null, null, null, String.valueOf(count));
    }


}
