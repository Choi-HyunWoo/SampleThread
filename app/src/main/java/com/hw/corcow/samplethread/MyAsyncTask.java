package com.hw.corcow.samplethread;

import android.os.AsyncTask;

/**
 * Created by Tacademy on 2015-10-16.
 */
public class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {

    public interface OnDownloadListener {
        public void onProgressUpdate(int progress);
        public void onProgressDone(boolean success);
    }
    OnDownloadListener mListener;
    public void setOnDownloadListener(OnDownloadListener listener) {
        mListener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        int count = 0;
        while(count <= 100) {
            publishProgress(count);
            count+= 5;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mListener != null) {
            mListener.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (mListener != null) {
            mListener.onProgressDone(aBoolean);
        }
    }

}
