package com.ble.demo4;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ble.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by cyril on 2018/4/5.
 */

public class MainActivity4 extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private ListView bleLV;
    private Button searchBtn;
    private MyHandler handler;
    private ArrayAdapter<String> adapter;
    private BluetoothDevice device;
    private BluetoothSocket clientSocket;
    private OutputStream os;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter2);

        handler = new MyHandler();

        bleLV = (ListView) findViewById(R.id.ble_list);
        searchBtn = (Button) findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isDiscovering()) {//如果正好在搜索，则先取消搜索
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();
                searchBtn.setText("正在搜索蓝牙设备");
            }
        });

        bleLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String s = adapter.getItem(position);
                //获得要连接的蓝牙设备的地址
                String address = s.substring(s.indexOf(":") + 1).trim();
                if (null == device) {
                    //获得蓝牙设备，相当于网路客户端制定的socketip地址
                    device = bluetoothAdapter.getRemoteDevice(address);
                }
                if (null == clientSocket) {
                    try {
                        clientSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));
                        //开始连接蓝牙设备
                        clientSocket.connect();
                        os = clientSocket.getOutputStream();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (null != os) {
                        //向服务器端发送一个字符串
                        try {
                            os.write("这是另一台手机发送过来的数据".getBytes("utf-8"));
                            Toast.makeText(MainActivity4.this, "发送成功", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(MainActivity4.this, "发送失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //开启服务端线程
//        new AcceptThread().start();
    }

    //更新界面的Handler类
    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            Toast.makeText(MainActivity4.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //创建接收数据的线程
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        private InputStream is;
        private OutputStream outputStream;

        public AcceptThread() {
            //创建BluetoothServerSocket对象
            try {
                serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("name", UUID.fromString("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            //等待接受蓝牙客户端的请求
            try {
                socket = serverSocket.accept();
                is = socket.getInputStream();
                outputStream = socket.getOutputStream();
                while (true) {
                    byte[] buffer = new byte[512];
                    int count = is.read(buffer);
                    Message msg = new Message();
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("searchBLE", "开始搜索");
            //获得已经搜索到的蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("searchBLE", device.getName() + ":" + device.getAddress() + "\n");
                //搜索到的设备不是已经绑定的设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //将搜索到的新蓝牙设备和名称显示到textview中
                    Set<BluetoothDevice> paireDevices = bluetoothAdapter.getBondedDevices();
                    if (paireDevices.size() > 0) {//若存在
                        String[] data = new String[paireDevices.size()];
                        int count = 0;
                        for (BluetoothDevice bluetoothDevice : paireDevices) {
                            data[count++] = bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress();//得到绑定蓝牙设备的名称和地址
                        }
                        adapter = new ArrayAdapter<String>(MainActivity4.this, android.R.layout.simple_expandable_list_item_1, data);

                        bleLV.setAdapter(adapter);
                    }
                }
            } else if (bluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//说明搜索已经完成
//                setProgressBarIndeterminateVisibility(false);
                searchBtn.setText("搜索蓝牙设备");
            }
        }
    };

    public void onDestroy() {
        super.onDestroy();
        //解除注册
        unregisterReceiver(mReceiver);
    }
}
