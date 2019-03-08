package com.teksun.serialporttest;



import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class MainActivity extends Activity {

    private static final String TAG = "rxl";

    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    private ReadThread mReadThread;

    private EditText editTextRecDisp;

    private class ReadThread extends Thread
    {
        @Override
        public void run()
        {
            super.run();

            while(!isInterrupted())
            {
                int size;
                Toast.makeText(MainActivity.this,"接收线程已经开启...",Toast.LENGTH_SHORT).show();
                Log.v("debug", "接收线程已经开启");
                try
                {
                    byte[] buffer = new byte[512];

                    if (mInputStream == null)
                        return;

                    size = mInputStream.read(buffer);

                    if (size > 0)
                    {
                        onDataReceived(buffer, size);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // mLog = (TextView)findViewById(R.id.log);
        //mEditText = (EditText)findViewById(R.id.message);
        editTextRecDisp=(EditText)findViewById(R.id.editTextRecDisp);

    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            Toast.makeText(MainActivity.this,"正在初始化串口...",Toast.LENGTH_SHORT).show();
            mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 9600, 0);//这里串口地址和比特率记得改成你板子要求的值。
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            Toast.makeText(MainActivity.this,"启动失败...",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,"启动失败...",Toast.LENGTH_SHORT).show();
            Log.v("test", "启动失败");
            e.printStackTrace();
        }

    }


    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable(){
            public void run(){
                String recinfo = new String(buffer, 0, size);
                StringBuilder sMsg=new StringBuilder();

                sMsg.append(MyFunc.ByteArrToHex(recinfo.getBytes()));
                sMsg.append("\r\n");
                editTextRecDisp.append(sMsg);

                //String temp=MyFunc.ByteArrToHex(recinfo.getBytes());
                Log.v("debug", "接收到串口信息======>" + recinfo.getBytes());
            }
        });
    }



}
