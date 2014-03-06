package com.zyy360.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zyy360.app.core.DataVideoFetcher;
import com.zyy360.app.ui.StartActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;

/**
 * @author daimajia
 * @modified Foxhu
 * @version 1.0
 */
public class LoadActivity extends ActionBarActivity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        mContext = this;

        setContentView(R.layout.activity_load);

        if (NetworkUtils.isWifi(mContext) == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle(R.string.only_wifi_title).setMessage(R.string.only_wifi_body);
            builder.setCancelable(false);
            //if user click ok then init data
            builder.setPositiveButton(R.string.only_wifi_ok,
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            init();
                        }
                    });
            //if user click quit then finish()
            builder.setNegativeButton(R.string.only_wifi_cancel,
                    new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.create().show();
        }else{
            init();
        }
    }

    /**
     * init data
     */
    private void init(){
        DataVideoFetcher.instance().getList(0,new JsonHttpResponseHandler(){
            /**
             * The server returns data format like
             * [{"name":"冬虫夏草","path":"2013/10/25_152747_61dP.flv","video_pic":"20131025/IMG_9La6_25_b.jpg","video_thumbpic":"20131025/IMG_BjTA_25_s.jpg","introduce":"冬虫夏草多种功效。","___key_id":25},
             * {"name":"防风﻿","path":"2013/10/25_152557_pmYc.flv","video_pic":"20131025/IMG_H0zO_24_b.jpg","video_thumbpic":"20131025/IMG_3b76_24_s.jpg","introduce":"解表药、祛风药","___key_id":24}]
             * reference documnets
             * http://loopj.com/android-async-http/doc/com/loopj/android/http/JsonHttpResponseHandler.html
             * @param statusCode
             * @param response
             */
            @Override
            public void onSuccess(int statusCode, JSONArray response) {

                super.onSuccess(statusCode, response);
                System.out.println("jsonArray->>"+response);
                Intent intent = new Intent(LoadActivity.this,StartActivity.class);

                if (statusCode == 200 && response.length()>0){
                    try {
                        intent.putExtra("LoadData",response.toString());
                        startActivity(intent);
                        finish();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{}
            }
            /*
            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                Toast.makeText(getApplicationContext(), R.string.error_load,
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mContext, StartActivity.class));
                finish();
            }
            */

            @Override
            public void onFailure(Throwable e, JSONArray errorResponse) {
                super.onFailure(e, errorResponse);
                System.out.println("jsonArray->>"+errorResponse);
                Toast.makeText(getApplicationContext(), R.string.error_load,
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mContext, StartActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



}
