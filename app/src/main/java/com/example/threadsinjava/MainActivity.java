package com.example.threadsinjava;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.threadsinjava.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private final static String TAG = MainActivity.class.getSimpleName();
    Button button_start_thread;
    Button button_stop_thread;
    private boolean stopLoop;
    private int count = 0;

    Handler handler;
    //private MyAsyncTask myAsyncTask;
    private LooperThread looperThread;
    private CustomHandlerThread customHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.i(TAG, "Thread id: " + Thread.currentThread().getId());

        button_start_thread = (Button) findViewById(R.id.button_start);
        button_stop_thread = (Button) findViewById(R.id.button_stop);

        handler = new Handler(getApplicationContext().getMainLooper());

        looperThread = new LooperThread();
        looperThread.start();

        customHandlerThread = new CustomHandlerThread("CustomHandlerThread");
        customHandlerThread.start();

        button_start_thread.setOnClickListener(this);
        button_stop_thread.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_start) {
            stopLoop = true;

            // Using Custom LooperThread
            executeOnCustomLooperWithCustomHandler();

            /*
            // Using AsyncTask
            myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute(count);*/
            /*

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (stopLoop) {
                        try{
                            Thread.sleep(1000);
                            count++;
                        }catch (InterruptedException e){
                            Log.i(TAG, e.getMessage());
                        }
                        Log.i(TAG, "Thread id in while loop: " + Thread.currentThread().getId() + ", Count: " + count);

                        binding.tvCounter.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvCounter.setText(" " + count);
                            }
                        });

                        *//*handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvCounter.setText(" " + count);
                            }
                        });*//*
                    }
                }
            }).start();
            */
        } else if (v.getId() == R.id.button_stop) {
            stopLoop = false;
            /*myAsyncTask.cancel(true);*/
        }
    }

    public void executeOnCustomLooperWithCustomHandler(){

        customHandlerThread.handler.post(new Runnable() {
            @Override
            public void run() {
                while (stopLoop){
                    try {
                        Thread.sleep(1000);
                        count++;
                        Log.i(TAG, "Thread id on Runnable posted: " + Thread.currentThread().getId());
                        runOnUiThread(() -> {
                            Log.i(TAG, "Thread id of runOnUiThread: " + Thread.currentThread().getId() + ", Count: " + count);
                            binding.tvCounter.setText(" " + count);

                        });
                    } catch (InterruptedException e){
                        Log.i(TAG, "Thread for interrupted");
                    }
                }
            }
        });

    }

    public void executeCustomLooper() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (stopLoop) {
                    try {
                        Log.i(TAG, "Thread id of thread that sends message: " + Thread.currentThread().getId());
                        Thread.sleep(1000);
                        count++;
                        Message message = new Message();
                        message.obj = "" + count;
                        customHandlerThread.handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Thread for interrupted");
                    }
                }
            }
        }).start();
    }

    private Message getMessageWithCount(String count){
        Message message = new Message();
        message.obj = "" + count;
        return message;
    }

    /*private class MyAsyncTask extends AsyncTask<Integer, Integer, Integer>{

        private int customCounter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customCounter = 0;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            customCounter = integers[0];
            while (stopLoop) {
                try{
                    Thread.sleep(1000);
                    customCounter++;
                    publishProgress(customCounter);
                }catch (InterruptedException e){
                    Log.i(TAG, e.getMessage());
                }
                if (isCancelled()){
                    break;
                }
            }
            return customCounter;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            binding.tvCounter.setText(" " + values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            binding.tvCounter.setText(" " + integer);
            count = integer;
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (looperThread != null && looperThread.isAlive()){
            looperThread.handler.getLooper().quit();
        }

        if (customHandlerThread != null){
            customHandlerThread.getLooper().quit();
        }
    }
}