package com.ble.demo2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ble.R;

/**
 * Created by cyril on 2018/4/4.
 */
public class MainActivity2 extends Activity {
    //定义
    private BluetoothAdapter mBluetoothAdapter;
    private TextView text, text2, text3;
    private Button botton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        text = (TextView) this.findViewById(R.id.textView);  //已配对
        text2 = (TextView) this.findViewById(R.id.textView2); //状态信息
        text3 = (TextView) this.findViewById(R.id.textView3); //未配对
        botton = (Button) this.findViewById(R.id.button);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter2);

        botton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }

                mBluetoothAdapter.startDiscovery();
                text2.setText("正在搜索...");
                text.setText("已配对蓝牙设备如下：");
                text3.setText("未配对蓝牙设备如下：");
            }
        });
    }


    public void onDestroy() {
        super.onDestroy();
        //解除注册
        unregisterReceiver(mReceiver);
    }


    //定义广播接收
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {    //显示已配对设备
                    text.append("\n" + device.getName() + "==>" + device.getAddress() + "\n");
                } else if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    text3.append("\n" + device.getName() + "==>" + device.getAddress() + "\n");
                }

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                text2.setText("搜索完成...");
            }

        }
    };

}