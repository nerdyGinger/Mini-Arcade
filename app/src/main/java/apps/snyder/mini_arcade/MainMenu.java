package apps.snyder.mini_arcade;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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

public class MainMenu extends AppCompatActivity {
    private ListView deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        deviceList = findViewById(R.id.deviceList);
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
                //Toast.makeText(this, "We can do stuff now!", Toast.LENGTH_SHORT).show();
                showDevices(devices);
            }
        }
    }

    private void showDevices(Set<BluetoothDevice> devices){
        ArrayList<String> list = new ArrayList<>();
        for(BluetoothDevice b : devices) {
            list.add(b.getName() + "\n" + b.getAddress());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length()-17);

                Toast.makeText(MainMenu.this, info, Toast.LENGTH_SHORT).show();

                //send info to game activity
                //Intent intent = new Intent(MainMenu.this, GamePage.class);
                //intent.putExtra(EXTRA_ADDRESS, address);
                //startActivity(intent);
            }
        });
    }
}
