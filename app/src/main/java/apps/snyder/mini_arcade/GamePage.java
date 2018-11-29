package apps.snyder.mini_arcade;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.UUID;

/*
GameThread and GameView code from genius who wrote this tutorial: http://www.edu4java.com/en/androidgame/androidgame4.html
Retrofitted for my own personal purposes.
Edited On: 11/26/2018
*/

public class GamePage extends AppCompatActivity {
    String address = null;
    ProgressDialog progress;
    private boolean connection = false;
    BluetoothAdapter bt;
    BluetoothDevice arduino;
    private BluetoothSocket btSocket = null;
    InputStream mInputStream;
    int readBufferPosition;
    boolean stop;
    Integer xValue = 512;
    Integer yValue = 503;
    Integer buttonValue;
    private GameView gameView;
    //static final UUID myUuid = UUID.fromString("ce1cd918-e6ed-11e8-9f32-f2801f1b9fd1"); //generated uuid
    private static final UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //base uuid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);

        Intent inIntent = getIntent();
        address = inIntent.getStringExtra("address");

        new Connect().execute();
    }


    //listener for bluetooth data
    void dataListener() {
        final byte delimiter = 10;
        stop = false;
        readBufferPosition = 0;
        final byte[] readBuffer = new byte[1024];
        Thread listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stop) {
                    try {
                        int bytes = mInputStream.available();
                        if (bytes > 0) {
                            byte[] packet = new byte[bytes];
                            mInputStream.read(packet);
                            for(int i=0; i<bytes; i++) {
                                byte b = packet[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    //what to do with the data
                                    String[] values = data.split(",");
                                    xValue = Integer.parseInt(values[0]);
                                    yValue = Integer.parseInt(values[1]);
                                    buttonValue = Integer.parseInt(values[2]);
                                    gameView.setXY(xValue, yValue);

                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("-----Important!----->", ex.toString());
                        stop = true;
                    }
                }
            }
        });
        listenerThread.start();
    }


    //Bluetooth connection class
    private class Connect extends AsyncTask<Void, Void, Void> {
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(GamePage.this, "Connecting...", "Please wait...");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !connection) {
                    bt = BluetoothAdapter.getDefaultAdapter();
                    arduino = bt.getRemoteDevice(address);
                    //btSocket = arduino.createRfcommSocketToServiceRecord(UUID.fromString(arduino.getUuids()[0].toString()));
                    btSocket = arduino.createInsecureRfcommSocketToServiceRecord(myUuid);
                    //btSocket = arduino.createRfcommSocketToServiceRecord(myUuid);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                    mInputStream = btSocket.getInputStream();
                }
            } catch (Exception ex){
                //try fallback connection
                Log.e("-----Important!----->", "Trying fallback: " + ex.toString());
                try {
                    btSocket = (BluetoothSocket) arduino.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(arduino,1);
                    btSocket.connect();
                    mInputStream = btSocket.getInputStream();
                } catch (Exception ex2) {
                    //connection failed
                    connectSuccess = false;
                    Log.e("-----Important!----->", ex2.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!connectSuccess) {
                //could not connect to bluetooth device, send back to MainMenu
                Toast.makeText(GamePage.this, "Connection failed.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(GamePage.this, "Connected", Toast.LENGTH_SHORT).show();
                connection = true;

                dataListener();
            }
            progress.dismiss();
        }
    }

}
