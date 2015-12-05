package src.bttest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import src.bttest.bluetooth_interface.Bluetooth;
import src.bttest.bluetooth_interface.IBluetoothHandler;

public class MainActivity extends AppCompatActivity implements IBluetoothHandler {

    private final static int REQUEST_ENABLE_BT = 1;

    Bluetooth btcomm = null;

    final ArrayList<BluetoothDevice> allDevices = new ArrayList<BluetoothDevice>();
    final ArrayList<String> pairedDeviceList = new ArrayList<String>();
    ListView mPairList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Bluetooth object and initialize it
        btcomm = Bluetooth.getInstance();
        btcomm.init(this);
        btcomm.addBluetoothHandler(this);

        // A list UI to show paired devices
        mPairList = (ListView) findViewById(R.id.pairList);

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, pairedDeviceList);

        // Get paired device list
        allDevices.addAll(btcomm.getPairedDeviceList());

        // Fill the list with paired device names
        if (allDevices != null) {
            for (BluetoothDevice device : allDevices) {
                pairedDeviceList.add(device.getName() + " - paired!");
                adapter.notifyDataSetChanged();
                mPairList.setAdapter(adapter);
            }
        }

        Button mDiscoverButton = (Button) findViewById(R.id.discoButton);

        mDiscoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start discovering new devices
                btcomm.discoverDevices();
            }
        });

        final Intent intent = new Intent(this, DeviceConnection.class);

        mPairList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btcomm.connect(allDevices.get((int) id));

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // It is better to close btcomm object to prevent memory leaks caused by registered receivers
        btcomm.close();
    }

    /*
        onActivityResult handler is used to track Bluetooth turn on status.
        If the user answers no to enable activity, we will handle it here
        and show user a message, perhaps exit the application.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if ( requestCode == REQUEST_ENABLE_BT ) {
            if (resultCode != RESULT_OK) {
                // Warn user or kill the app
                Toast.makeText(getApplicationContext(), "You must enable Bluetooth to use this application.", Toast.LENGTH_LONG).show();
                // System.exit(0);
            }
        }
    }

    @Override
    public void discoveryHandler(BluetoothDevice device) {
        Toast.makeText(this, "Discovered device : " + device.getName() + "--" + device.getAddress(), Toast.LENGTH_SHORT).show();

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, pairedDeviceList);

        // Check if we already have this device in our list
        for (BluetoothDevice indDevice : allDevices) {
            // If we have skip this device
            if (device.equals(indDevice))
                return;

        }

        // The device is not in our list, add it to allDevice list and listview list
        allDevices.add(device);
        pairedDeviceList.add(device.getName());
        adapter.notifyDataSetChanged();
        mPairList.setAdapter(adapter);
    }

    @Override
    public void receiveHandler(byte[] message) {
        // Message received
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
    }


}
