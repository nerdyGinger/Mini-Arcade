package apps.snyder.mini_arcade;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.InputStream;
import java.util.UUID;

/*
This activity is the actual game; handles data from bluetooth and passes to GameView for sprite animation.
Updated On: 12/3/18
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
    private static final UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //base uuid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets GameView, gets bluetooth address from MainMenu activity, starts bluetooth connection
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent inIntent = getIntent();
        address = inIntent.getStringExtra("address");

        new Connect().execute();
    }


    void dataListener() {
        //listener for bluetooth data
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
                                    //at end of data line, decode data
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    //what to do with the data
                                    String[] values = data.split(",");
                                    xValue = Integer.parseInt(values[0]);
                                    yValue = Integer.parseInt(values[1]);
                                    buttonValue = Integer.parseInt(values[2]);
                                    gameView.setXY(xValue, yValue, buttonValue);

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
        //begins bluetooth connection; displays progress dialog during connection process
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {
            //bring up progress dialog
            progress = ProgressDialog.show(GamePage.this, "Connecting...", "Please wait...");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            //actually do the connecting
            try {
                if (btSocket == null || !connection) {
                    //initial try for connection
                    bt = BluetoothAdapter.getDefaultAdapter();
                    arduino = bt.getRemoteDevice(address);
                    btSocket = arduino.createInsecureRfcommSocketToServiceRecord(myUuid);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                    mInputStream = btSocket.getInputStream();
                }
            } catch (Exception ex){
                //try fallback connection (not as good, but might work)
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
                //connection is successful! Begin listening for data for game
                Toast.makeText(GamePage.this, "Connected", Toast.LENGTH_SHORT).show();
                connection = true;

                dataListener();
            }
            //...and get rid of that progress dialog now
            progress.dismiss();
        }
    }

}
