package me.lxl.expresstrack.zxing;



import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectHelper
{
    private final static String TAG = "ExpressTrack";

    public static String post(JSONArray jsonArray, String urlStr)
    {
        URL url = null;
        String ret = "";
        try {
            url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            //设置连接超时,2000ms
            httpURLConnection.setConnectTimeout(2000);
            //设置指定时间内服务器没有返回数据的超时，5000ms
            httpURLConnection.setReadTimeout(5000);
            //设置允许输出
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
            httpURLConnection.setRequestMethod("POST");

            OutputStream out = new
                    BufferedOutputStream(httpURLConnection.getOutputStream());

            byte[] body = jsonArray.toString().getBytes("utf-8");
            out.write(body, 0, body.length);

            out.flush();//立即刷新

            out.close();

            int code = httpURLConnection.getResponseCode();
            Log.d(TAG, "code = " + code);
            if (code == 200) {
                InputStream is = httpURLConnection.getInputStream();

                //连接服务器后，服务器做出响应返回的数据
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String serverResponse = "";
                String line = "";
                while ((line = bufReader.readLine()) != null) {
                    serverResponse += line;
                }
                is.close();

                ret = serverResponse;

                Log.d(TAG, "serverResponse = " + serverResponse + " , " + serverResponse.equalsIgnoreCase("success"));
                return ret;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String post(JSONObject jsonObject, String urlStr)
    {
        URL url = null;
        String ret = "";
        try {
            url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            //设置连接超时,2000ms
            httpURLConnection.setConnectTimeout(2000);
            //设置指定时间内服务器没有返回数据的超时，5000ms
            httpURLConnection.setReadTimeout(5000);
            //设置允许输出
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
            httpURLConnection.setRequestMethod("POST");

            OutputStream out = new
                    BufferedOutputStream(httpURLConnection.getOutputStream());

            byte[] body = jsonObject.toString().getBytes("utf-8");
            out.write(body, 0, body.length);

            out.flush();//立即刷新

            out.close();

            int code = httpURLConnection.getResponseCode();
            Log.d(TAG, "code = " + code);
            if (code == 200) {
                InputStream is = httpURLConnection.getInputStream();

                //连接服务器后，服务器做出响应返回的数据
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String serverResponse = "";
                String line = "";
                while ((line = bufReader.readLine()) != null) {
                    serverResponse += line;
                }
                is.close();

                ret = serverResponse;

                Log.d(TAG, "serverResponse = " + serverResponse + " , " + serverResponse.equalsIgnoreCase("success"));
                return ret;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}