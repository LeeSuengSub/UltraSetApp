package net.woorisys.UltraSetApp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.woorisys.UltraSetApp.Data.SiteMacAddr;
import net.woorisys.UltraSetApp.SingletonData.BeaconSingleton;
import net.woorisys.UltraSetApp.ble.BeaconDomain;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class BleScannerActivity extends AppCompatActivity implements BeaconConsumer {

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;

    private BeaconManager beaconManager;

    private ListView listView;
    private EditText editText;
    Button connectBtn;
    Button rescanBtn;

    String selectedLocation = null;

    //싱글톤
    private BeaconSingleton beaconSingleton = BeaconSingleton.getInstance();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scanner);

        editText = (EditText) findViewById(R.id.editText);
        connectBtn = (Button) findViewById(R.id.connectBtn);
        rescanBtn = (Button) findViewById(R.id.rescanBtn);
        listView = (ListView) findViewById(R.id.listview);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if(bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        //Spinner(ComboBox)
        Spinner spinner_field = (Spinner) findViewById(R.id.comboBox);

        String[] comboArray = getResources().getStringArray(R.array.spinnerArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, comboArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_field.setAdapter(adapter);

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(comboArray[position].equals("동탄")) {
                    selectedLocation = SiteMacAddr.DONGTAN.addr();
                }else if(comboArray[position].equals("F19")) {
                    selectedLocation = SiteMacAddr.F19.addr();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //리스트뷰 클릭시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent DeviceControl = new Intent(BleScannerActivity.this,DeviceControlActivity.class);
                startActivity(DeviceControl);
            }
        });

        //MacAddress를 입력 후 직접 연동하기
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextString = editText.getText().toString();
                int count = 0; //list에 있는지 확인
                String beaconSingleton_macAddress; //싱글톤에 들어있는 macAddress.

                if(editText.length() <= 0){
                    Toast.makeText(BleScannerActivity.this, "시리얼번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!isNumeric(editTextString)) {
                    Toast.makeText(BleScannerActivity.this, "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(editTextString.length() < 4){
                    while(editTextString.length() < 4){
                        editTextString = "0" + editTextString;
                    }
                }

                //==두자리씩 잘라내기
                String editTextString1 = editTextString.substring(0,2);
                String editTextString2 = editTextString.substring(2);

                selectedLocation+= ":"+editTextString1 + ":" + editTextString2;
                System.out.println("선택한 macAddress -------> "+selectedLocation);   //macAddressTest

                for(int i = 0; i < listView.getCount(); i++){
                    beaconSingleton_macAddress = beaconSingleton.getBeaconDomainList().get(i).getMacAddress();

                    if(selectedLocation.equals(beaconSingleton_macAddress)){
                        ++count;
                        System.out.println("count -->"+count);
                    }
                }

                if(count <= 0){
                    Toast.makeText(BleScannerActivity.this, "사이트를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    spinner_field.setSelection(0);
                }else{
                    Intent intent = new Intent(BleScannerActivity.this, DeviceControlActivity.class);
                    intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, selectedLocation);
                    spinner_field.setSelection(0);
                    startActivity(intent);
                }
            }
        });
        rescanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        if(!bluetoothAdapter.isEnabled()) {
            if(bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private class BeaconAdapter extends BaseAdapter {

        public ArrayList<BeaconDomain> beaconArrayList = new ArrayList<BeaconDomain>();

        public BeaconAdapter(ArrayList<BeaconDomain> beacon) {
            this.beaconArrayList = beacon;
        }

        @Override
        public int getCount(){
            return beaconArrayList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return beaconArrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_list_item, parent, false);

            TextView macAddress = (TextView) convertView.findViewById(R.id.macAddress);
            TextView serialNumber = (TextView) convertView.findViewById(R.id.serialNumber);

            macAddress.setText("MacAddress : " + beaconArrayList.get(position).getMacAddress());
            serialNumber.setText("serialNumber : "+ beaconArrayList.get(position).getSerialNumber());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BleScannerActivity.this, DeviceControlActivity.class);
                    intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, beaconSingleton.getBeaconDomainList().get(position).getMacAddress());
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }


    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                // [비콘이 감지되면 해당 함수가 호출]
                // TODO [비콘들에 대응하는 Region 객체가 들어옴]

                int count = 0;
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        System.out.println("beacon : " + beacon.getBluetoothAddress() + " rssi : " + beacon.getRssi());

                        String macAddress = beacon.getBluetoothAddress();
                        String[] macArray = macAddress.split(":");

//                        String macArrayString = macArray[4]+macArray[5]; //2023-02-10 (test코드) String으로 전부 더한 다음 정수로 변환(String -> hex -> int)

                        int num1 = 0;
                        int num2 = 0;

                        try{

                            num1 = Integer.parseInt(macArray[4]);
                            num2 = Integer.parseInt(macArray[5]);

//                            System.out.println("try -> : "+ Integer.parseInt(macArrayString, 16)); //2023-02-10에 추가 (test코드)

                        }catch (NumberFormatException e){

                            num1 = Integer.parseInt(macArray[4], 16);
                            num2 = Integer.parseInt(macArray[5], 16);

//                            System.out.println("catch -> : "+ Integer.parseInt(macArrayString, 16)); //2023-02-10에 추가 (test코드)

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        int serialNumber = (num1 * 100) + num2;

                        System.out.println("serialNumber : => "+serialNumber);

                        if (beacon.getRssi() >= -60) { //-60
                            if (beaconSingleton.getBeaconDomainList().isEmpty()) {
                                beaconSingleton.getBeaconDomainList().add(new BeaconDomain(macAddress, serialNumber));
                                ++count;
                            }
                            //리스트 중복 체크
                            for (Iterator<BeaconDomain> iterator = beaconSingleton.getBeaconDomainList().iterator(); iterator.hasNext(); ) {
                                BeaconDomain beaconDomain = iterator.next();

                                if (beacon.getBluetoothAddress().equals(beaconDomain.getMacAddress())) {
                                    iterator.remove();
                                }
                            }

                            if (count <= 30) {
                                beaconSingleton.getBeaconDomainList().add(new BeaconDomain(macAddress, serialNumber));
                                ++count;
                            }

                            if(count >= 25) {
                                try {
                                    beaconManager.stopMonitoringBeaconsInRegion(region);
                                    beaconManager.stopRangingBeaconsInRegion(region);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }

                            BeaconAdapter beaconAdapter = new BeaconAdapter(beaconSingleton.getBeaconDomainList());
                            listView.setAdapter(beaconAdapter);
                            beaconAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // 숫자 정규표현식
    public boolean isNumeric(String str) {
        return Pattern.matches("^[0-9]*$", str);
    }

}