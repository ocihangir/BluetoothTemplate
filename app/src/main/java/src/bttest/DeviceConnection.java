package src.bttest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import src.bttest.bluetooth_interface.Bluetooth;
import src.bttest.bluetooth_interface.IBluetoothHandler;

public class DeviceConnection extends AppCompatActivity implements IBluetoothHandler {

    Bluetooth btcomm = null;

    ListView msgList = null;
    ArrayList<String> msgStringList = new ArrayList<>();

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connection);

        btcomm = Bluetooth.getInstance();

        btcomm.addBluetoothHandler(this);

        msgList = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, msgStringList);

        msgList.setAdapter(adapter);

        msgStringList.add("Received Messages");
        adapter.notifyDataSetChanged();
        msgList.setAdapter(adapter);

        // TODO: list received messages in the list.
        // receiveHandler is called from another thread
        // We need to implement an IPC to catch them from
        // view thread.

        final TextView txtMessage = (TextView) findViewById(R.id.textView);


        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btcomm.sendData(txtMessage.getText().toString().getBytes());
            }
        });

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btcomm.disconnect();
    }

    @Override
    public void discoveryHandler(BluetoothDevice device) {

    }

    @Override
    public void receiveHandler(byte[] message) {
        System.out.println("Message received : " + new String(message));
    }

}


