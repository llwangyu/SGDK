package com.mwj.lhn.sgdk.pub;

/**
 * Created by LHN on 2017/11/15.
 */

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.mwj.lhn.sgdk.R;

import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Url管理类这里只写了一个，实际开发中有很多请求
 *
 */
public class URLManage {


    public final static String HOST = Basic.myurl_host;

   private  static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    static {
        client.setTimeout(31000);
//        client.setMaxConnections(2);
    }



    public static void showInfos( String string,RequestParams params ,JsonHttpResponseHandler res) {
        String urlString = HOST + string;
        params.setContentEncoding(HTTP.UTF_8);
        get(client, urlString, params, res);
    }


    /**
     * 拼接地址并请求
     *
     * @param urlString
     * @param params
     * @param res
     */
    private static void get(AsyncHttpClient client, String urlString, RequestParams params, JsonHttpResponseHandler res) {
        System.out.println("--------------------"+(urlString + "?" + params.toString()));//可以看下请求的地址
        client.get(urlString, params, res);
    }
}
