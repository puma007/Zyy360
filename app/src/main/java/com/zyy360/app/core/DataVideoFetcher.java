package com.zyy360.app.core;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * @author daimajia
 * @modified Foxhu
 * @version 1.0
 */
public class DataVideoFetcher {
    private static DataVideoFetcher mInstance;
    //request url with parameter page
    private static final String mRequestListUrl = "http://192.168.0.108:8080/action/api/videoList?page=%d";

    private DataVideoFetcher() {
    }
    public static DataVideoFetcher instance() {
        if (mInstance == null) {
            mInstance = new DataVideoFetcher();
        }
        return mInstance;
    }

    /**
     * get data from server by AsyncHttpClient
     * @param page
     * @param handler
     */
    public void getList(int page,JsonHttpResponseHandler handler){
        AsyncHttpClient client = new AsyncHttpClient();
        String request = String.format(mRequestListUrl,page);
        //get json data from server
        client.get(request,null,handler);
    }
}