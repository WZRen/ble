package com.ble.demo1.utils;

public class ConstantUtils {
	//��Ϣ����
	public final static int WM_STOP_SCAN_BLE=1;
	public final static int WM_UPDATE_BLE_LIST=2;
	//��������״̬�ı�
	public final static int  WM_BLE_CONNECTED_STATE_CHANGE=3;
	//���ܵ�������������Ϣ
	public final static int WM_RECEIVE_MSG_FROM_BLE=4;
	//�Ͽ����ӻ�δ���ӳɹ�
	public final static int WM_STOP_CONNECT=5;
	
	//intent��action��
	public final static String ACTION_UPDATE_DEVICE_LIST="action.update.device.list";//�����豸�б�
	public final static String  ACTION_CONNECTED_ONE_DEVICE="action.connected.one.device";//������ĳ���豸ʱ���Ĺ㲥
	public final static String ACTION_RECEIVE_MESSAGE_FROM_DEVICE="action.receive.message";
	public final static String ACTION_STOP_CONNECT="action.stop.connect";
	
	
	//UUID	
		public final static  String UUID_SERVER="0000ffe0-0000-1000-8000-00805f9b34fb";
		public final static  String UUID_NOTIFY="0000ffe1-0000-1000-8000-00805f9b34fb";

}
