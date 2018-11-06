package apps.snyder.mini_arcade;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Set;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void connect(View view) {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (bluetooth == null) {
            //check for bluetooth availability
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (!bluetooth.isEnabled()) {
            //check if bluetooth is enabled; send request if not
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);
        }
        else {
            Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
            if (devices.size() == 0) {
                //check that there are actually devices around
                Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show();
            }
            else {
                //here, we present list of devices to choose from
                Toast.makeText(this, "We can do stuff now!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
