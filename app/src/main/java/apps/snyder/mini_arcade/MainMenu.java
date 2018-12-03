package apps.snyder.mini_arcade;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

/*
MainMenu is the main menu activity... It handles the initialization of bluetooth and selection
of bluetooth devices.
Updated on: 12/3/18
 */

public class MainMenu extends AppCompatActivity {
    private ListView deviceList;
    ArrayList<String> foundDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        deviceList = findViewById(R.id.deviceList);
    }

    public void testSprite(View view) {
        //onClick for "Test Sprite" button; sends to SpriteTest activity
        Intent test = new Intent(MainMenu.this, SpriteTest.class);
        startActivity(test);
    }

    public void connect(View view) {
        //onClick for "Connect" button; initializes bluetooth and selection list
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (bluetooth == null) {
            //check for bluetooth availability
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
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
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
                if (bluetooth.isDiscovering()) {
                    bluetooth.cancelDiscovery();
                }
                bluetooth.startDiscovery();
                showDevices(devices);
                bluetooth.cancelDiscovery();
            }
        }
    }

    private void showDevices(Set<BluetoothDevice> devices){
        //populates list of paired devices, manages click event of list
        //add paired devices first
        ArrayList<String> list = new ArrayList<>();
        for(BluetoothDevice b : devices) {
            list.add(b.getName() + "\n" + b.getAddress());
        }
        list.addAll(foundDevices);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //onClick for device list, sends to GamePage with selected bluetooth device address
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length()-17);

                //send info to game activity
                Intent intent = new Intent(MainMenu.this, GamePage.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
    }

    //___vvv____UNDER CONSTRUCTION____vvv____
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        //eventually should allow discovery of nearby devices, but this isn't working yet...
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                //device found
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundDevices.add(d.getName() + "\n" + d.getAddress());
            }
        }
    };
    //___^^^___UNDER CONSTRUCTION___^^^___
}
