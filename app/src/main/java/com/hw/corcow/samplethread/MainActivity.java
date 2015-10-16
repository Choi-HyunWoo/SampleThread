package com.hw.corcow.samplethread;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.channels.spi.AbstractSelectionKey;

public class MainActivity extends AppCompatActivity {

    TextView messageView;
    ProgressBar progressDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = (TextView)findViewById(R.id.text_message);
        progressDownload = (ProgressBar)findViewById(R.id.progress_download);

        progressDownload.setMax(100);

        Button btn = (Button)findViewById(R.id.btn_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });

        btn = (Button)findViewById(R.id.btn_runnable);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRunnable();
            }
        });

        btn = (Button)findViewById(R.id.btn_asynctask);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAsyncTask task = new MyAsyncTask();
                task.setOnDownloadListener(new MyAsyncTask.OnDownloadListener() {
                    @Override
                    public void onProgressUpdate(int progress) {
                        messageView.setText("progress : " + progress);
                        progressDownload.setProgress(progress);
                    }
                    @Override
                    public void onProgressDone(boolean success) {
                        messageView.setText("progress done");
                    }
                });
                task.execute();
            }
        });
    }

    /*
    class MyTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            int count = 0;
            while(count <= 100) {
                publishProgress(count);         // << Main Thread에 보낼 메세지
                count+= 5;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        // Main Thread에서 실행
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            messageView.setText("progress done");
        }

        // doInBackGround에서 호출한 publishProgress를 받는 함수. (Main Thread에서 실행)
        @Override
        protected void onProgressUpdate(Integer... values) {            // 개수를 동적으로 (= Integer array)
            super.onProgressUpdate(values);
            int progress = values[0];
            messageView.setText("progress : " );
            progressDownload.setProgress(progress);
        }
    }
    */

    public static final int MESSAGE_PROGRESS = 1;
    public static final int MESSAGE_DONE = 2;
    public static final int MESSAGE_BACK_TIMEOUT = 3;
    public static final int TIME_BACK_TIMEOUT = 2000;

    private boolean isBackPressed = false;


    Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PROGRESS :
                    int progress = msg.arg1;
                    messageView.setText("progress : " + progress);
                    progressDownload.setProgress(progress);
                    break;
                case MESSAGE_DONE :
                    messageView.setText("progress done");
                    break;
                case MESSAGE_BACK_TIMEOUT :
                    isBackPressed = false;
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            mHandler.removeMessages(MESSAGE_BACK_TIMEOUT);
            super.onBackPressed();
        }
        else {
            isBackPressed = true;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(MESSAGE_BACK_TIMEOUT, TIME_BACK_TIMEOUT);
        }
    }

    private void startDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while(count <= 100) {
                    Message msg = mHandler.obtainMessage(MESSAGE_PROGRESS, count, 0);
                    mHandler.sendMessage(msg);
                    count+= 5;
//                    messageView.setText("download : " + count);
//                    progressDownload.setProgress(count);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                messageView.setText("progress done");
                mHandler.sendEmptyMessage(MESSAGE_DONE);
            }
        }).start();
    }

    private void startRunnable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while(count <= 100) {
                    mHandler.post(new ProgressRunnable(count));
                    count+= 5;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mHandler.post(new DoneRunnable());
            }
        }).start();
    }

    class ProgressRunnable implements Runnable {
        int progress;
        public ProgressRunnable(int progress) {
            this.progress = progress;
        }

        @Override
        public void run() {
            messageView.setText("progress : " + progress);
            progressDownload.setProgress(progress);
        }
    }

    class DoneRunnable implements Runnable {
        @Override
        public void run() {
            messageView.setText("progress done");
        }
    }

}
