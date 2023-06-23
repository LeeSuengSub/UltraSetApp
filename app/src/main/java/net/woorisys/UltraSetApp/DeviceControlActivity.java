package net.woorisys.UltraSetApp;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.woorisys.UltraSetApp.ble.BluetoothLeService;
import net.woorisys.UltraSetApp.ble.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class DeviceControlActivity extends AppCompatActivity {

    private final static String TAG = "DeviceControl";

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String CUSTOM_SERVICE_UUID = "0000FFF0";
    public static final String CUSTOM_CHARACTERISTIC_UUID = "0000FFF1";

    public static final String COMMON_SETTING = "AA020000ACAB";
    public static final String DISORDER_SETTING = "AA020001ADAB";
    public static final String YELLOW_SETTING = "AA020002AEAB";
    public static final String CYAN_SETTING = "AA020002AFAB";
    public static final String PINK_SETTING = "AA020002B0AB";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceAddress;

    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private Button writeButton;
    private Button readButton;
    private Button commonButton;
    private Button disorderButton;
    private Button yellowButton;
    private Button cyanButton;
    private Button pinkButton;

    private final String LIST_UUID = "UUID";
    private final String LIST_MAC_ADDRESS = "MAC_ADDRESS";

    // onCreate가 로드되면서 bind한다.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.d(TAG, "진입합니다.");
            if(!mBluetoothLeService.initialize()) {
                Log.d(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.d(TAG,"connect");

            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        writeButton = findViewById(R.id.writeButton);
        readButton = findViewById(R.id.readButton);
        commonButton = findViewById(R.id.commonButton);
        disorderButton = findViewById(R.id.disorderButton);
        yellowButton = findViewById(R.id.yellowButton);
        cyanButton = findViewById(R.id.cyanButton);
        pinkButton = findViewById(R.id.pinkButton);

        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mConnectionState.setText("연결중");

        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListener);
        mDataField = (TextView) findViewById(R.id.device_data);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //일반
        commonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] data = hexStringToByteArray(COMMON_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //장애인
        disorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] data = hexStringToByteArray(DISORDER_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //노란색
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataField.length() <=0) {
                    Toast.makeText(mBluetoothLeService,"read한 다음 시도해주세요.",Toast.LENGTH_SHORT);
                    return;
                }
                byte[] data = hexStringToByteArray(YELLOW_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //시안색
        cyanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataField.length() <=0) {
                    Toast.makeText(mBluetoothLeService,"read한 다음 시도해주세요.",Toast.LENGTH_SHORT);
                    return;
                }
                byte[] data = hexStringToByteArray(CYAN_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //분홍색
        pinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataField.length() <=0) {
                    Toast.makeText(mBluetoothLeService,"read한 다음 시도해주세요.",Toast.LENGTH_SHORT);
                    return;
                }
                byte[] data = hexStringToByteArray(PINK_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                builder.setTitle("현재 설정값");

                String mDataFieldText = mDataField.getText().toString().toUpperCase(Locale.ROOT);
                String mDataFieldSubString1 = mDataFieldText.substring(5,8);
                int mDataFieldSettingHeight = Integer.parseInt(mDataFieldSubString1,16);

                String mDataFieldSubString2 = mDataFieldText.substring(11,14);
                int mDataFieldMeasurementHeight = Integer.parseInt(mDataFieldSubString2,16);

                String mDataFieldSubString3 = mDataFieldText.substring(9,10);
                int mDataFieldState = Integer.parseInt(mDataFieldSubString3,16);

                String State = null;
                if(mDataFieldState == 0){
                    State = "일반";
                }else if(mDataFieldState == 1){
                    State = "장애인";
                }else if(mDataFieldState == 2){
                    State = "Yellow";
                }else if(mDataFieldState == 3){
                    State = "Cyan";
                }else if(mDataFieldState == 4){
                    State = "Pink";
                }

                builder.setMessage("설정높이 : "+mDataFieldSettingHeight+"\n"+ "측정높이 : "+mDataFieldMeasurementHeight +"\n"+"상태 : "+State);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        AlertDialog.Builder writeBuilder = new AlertDialog.Builder(DeviceControlActivity.this);

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                writeBuilder.setTitle("높이 값 세팅");
                writeBuilder.setMessage("세팅 가능한 범위 : 1000 ~ 4000");

                LayoutInflater inflater = getLayoutInflater();
                View readView = inflater.inflate(R.layout.write_dialog, null);

                writeBuilder.setView(readView);

                writeBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String start = "AA";
                        String road = "01";
                        String end = "AB";

                        EditText heightSet = (EditText)((AlertDialog)dialog).findViewById(R.id.heightSet);

                        String dialogEdit = heightSet.getText().toString();

                        //내용을 적지 않으면 return;
                        if(dialogEdit.length() <= 0){
                            return;
                        }

                        // 정규표현식 숫자만!
                        if(!isNumeric(dialogEdit)){
                            Toast.makeText(mBluetoothLeService, "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int dialogEditInt = Integer.parseInt(dialogEdit);

                        String heightEditHex = Integer.toHexString(dialogEditInt);
                        while(heightEditHex.length() < 4) {
                            heightEditHex = "0"+heightEditHex;
                        }

                        String heightEditHexHeightHigh = heightEditHex.substring(0,2);
                        String heightEditHexHeightLow = heightEditHex.substring(2);

                        int startInt = Integer.parseInt(start,16);
                        int roadInt = Integer.parseInt(road,16);
                        int heightHigh = Integer.parseInt(heightEditHexHeightHigh,16);
                        int heightLow = Integer.parseInt(heightEditHexHeightLow,16);

                        int HexSum = startInt+roadInt+heightHigh+heightLow;
                        String writeSumHex = Integer.toHexString(HexSum);

                        if(writeSumHex.length() > 2) {
                            writeSumHex = writeSumHex.substring(1);
                        }

                        String hexSetting = (start+road+heightEditHex+writeSumHex+end).toUpperCase(Locale.ROOT);

                        byte[] data = hexStringToByteArray(hexSetting);
                        mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic,data);
                    }
                });
                writeBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = writeBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result= "+result);
        }else{
            Log.d(TAG, "????");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG,"Connected");
                mConnected = true;
                mConnectionState.setText("연결됨");
                invalidateOptionsMenu();
            } else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG,"Disconnected");
                mConnected = false;
                mConnectionState.setText("연결 실패");
                invalidateOptionsMenu();
                clearUI();
            } else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            }
            else if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                String protocol = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                Toast.makeText(context, protocol, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final ExpandableListView.OnChildClickListener servicesListClickListener =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        //read
                        if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            System.out.println("PROPERTY_READ");
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }

                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) == charaProp) {
                            Log.e(TAG,"NOTI");
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(characteristic , true);

                        }else{
                            Log.e(TAG,"WRITE");
                            mBluetoothLeService.setCharacteristicNotification(characteristic , true);
                            characteristic.setValue(new byte[] {0x24, 0x52, 0x45, 0x41, 0x44, 0x2C, 0x30, 0x0D, 0x0A});
                            mBluetoothLeService.writeCharacteristic(characteristic);
                        }

                        return true;
                    }
                    return false;
                }
            };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
    }

    private void displayData(String data) {
        if(data != null) {
            mDataField.setText(data);
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if(gattServices == null) return;
        String uuid = null;
        String customServiceString = "Custom SERVICE";
        String customCharaString = "Custom characteristic";

        //Service
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        for(BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            System.out.println("uuid =======> "+uuid);

            String[] uuid_split = uuid.split("-");
            String uuid_split_upper = uuid_split[0].toUpperCase(Locale.ROOT);

            if(uuid_split_upper.equals(CUSTOM_SERVICE_UUID)){
                currentServiceData.put(LIST_UUID, SampleGattAttributes.lookup(uuid, customServiceString.toUpperCase(Locale.ROOT)));
                currentServiceData.put(LIST_MAC_ADDRESS, uuid.toUpperCase(Locale.ROOT));
                gattServiceData.add(currentServiceData);

                //characteristic
                ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

                for(BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();

                    String[] characteristic_uuid_split = uuid.split("-");
                    String characteristic_uuid_split_upper = characteristic_uuid_split[0].toUpperCase(Locale.ROOT);

                    if(characteristic_uuid_split_upper.equals(CUSTOM_CHARACTERISTIC_UUID)){

                        currentCharaData.put(LIST_UUID, SampleGattAttributes.lookup(uuid, customCharaString.toUpperCase(Locale.ROOT)));
                        currentCharaData.put(LIST_MAC_ADDRESS, uuid.toUpperCase(Locale.ROOT));
                        gattCharacteristicGroupData.add(currentCharaData);
                    }
                }
                mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }

        }//for end

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this, gattServiceData, android.R.layout.simple_expandable_list_item_2, new String[]{LIST_UUID, LIST_MAC_ADDRESS}, new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_UUID, LIST_MAC_ADDRESS},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // hex => byteArray
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    // 숫자 정규표현식
    public boolean isNumeric(String str) {
        return Pattern.matches("^[0-9]*$", str);
    }

}