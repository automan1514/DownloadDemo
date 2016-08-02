package com.txyapp.downloaddemo.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.txyapp.downloaddemo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        btn = (TextView) findViewById(R.id.btn);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    private void initListener() {
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                download();
                break;
            default:
                break;
        }
    }

    private void download() {
        // execute this when the downloader must be fired
        new DownloadTask(MainActivity.this).execute("http://imtt.dd.qq.com/16891/21CED18FFFA21735E5E4527243331FDB.apk?fsname=com.mgyun.shua.su_3.2.3_86.apk&csr=4d5s");
    }

    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private InputStream inputStream;
        private OutputStream outputStream;
        private HttpURLConnection httpURLConnection;
        private PowerManager.WakeLock wakeLock;

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
                        Environment.getExternalStorageDirectory().getAbsolutePath(), "ccc.apk"));

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
            progressBar.setMax(100);
            progressBar.setProgress(values[0]);
    }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            wakeLock.release();
            Toast.makeText(context, "下載完成"+s, Toast.LENGTH_SHORT).show();
        }
    }
}
