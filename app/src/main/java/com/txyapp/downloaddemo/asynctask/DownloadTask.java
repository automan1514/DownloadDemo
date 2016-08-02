package com.txyapp.downloaddemo.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liuxiaofeng on 2016/8/2.
 */
public class DownloadTask extends AsyncTask<String,Integer,String>{

    private Context context;
    private InputStream inputStream;
    private OutputStream outputStream;
    private HttpURLConnection httpURLConnection;
    public PowerManager.WakeLock wakeLock;

    public DownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.acquire();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server return HTTP " + httpURLConnection.getResponseCode() + " " +
                        httpURLConnection.getResponseMessage();
            }

            int contentLength = httpURLConnection.getContentLength();

            inputStream = httpURLConnection.getInputStream();
            outputStream = new FileOutputStream(new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath(), "file_name.apk"));

            byte[] data = new byte[4096];
            int total = 0;
            int length = 0;
            while ((length = inputStream.read(data)) != -1) {
                if (isCancelled()) {
                    inputStream.close();
                    return null;
                }
                total += length;
                outputStream.write(data, 0, length);
                if (contentLength > 0)
                    publishProgress((int) (100 * total / contentLength));
            }
            System.out.println(total);
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                return e.toString();
            }

            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        wakeLock.release();
    }
}
