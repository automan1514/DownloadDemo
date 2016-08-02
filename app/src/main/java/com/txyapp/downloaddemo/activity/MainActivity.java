package com.txyapp.downloaddemo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.txyapp.downloaddemo.R;
import com.txyapp.downloaddemo.asynctask.DownloadTask;

import java.io.File;


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
        if(btn.getText().equals("下载")){
            downloadTask.execute("http://imtt.dd.qq.com/16891/21CED18FFFA21735E5E4527243331FDB.apk?fsname=com.mgyun.shua.su_3.2.3_86.apk&csr=4d5s");
        }else {
            installApk();
        }
    }

    DownloadTask downloadTask = new DownloadTask(MainActivity.this){
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            btn.setText("安装360Root大师");
        }
    };



    private void installApk(){
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/file_name.apk";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(fileName)),"application/vnd.android.package-archive");
        startActivity(intent);
    }
}
