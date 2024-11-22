package net.woorisys.UltraSetApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.woorisys.UltraSetApp.Adapter.BeaconAdapter;
import net.woorisys.UltraSetApp.SingletonData.BeaconSingleton;
import net.woorisys.UltraSetApp.ble.BeaconDomain;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public class BleScannerActivity extends AppCompatActivity implements BeaconConsumer {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BeaconManager beaconManager;
    private ListView listView;
    private EditText editTextCompany;
    Button connectBtnCompany, startScanBtn, stopScanBtn;
    FloatingActionButton rescanBtn;
    TextView scanState;

    String selectedLocation = null;
    private BeaconSingleton beaconSingleton = BeaconSingleton.getInstance();
    private boolean isScanning = true;
    private RangeNotifier rangeNotifier;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scanner);

        editTextCompany = findViewById(R.id.editTextCompany);
        connectBtnCompany = findViewById(R.id.connectBtnCompany);
        rescanBtn = findViewById(R.id.fab);
        listView = findViewById(R.id.listview);
        startScanBtn = findViewById(R.id.startScanBtn);  // 스캔 시작 버튼
        stopScanBtn = findViewById(R.id.stopScanBtn);    // 스캔 중지 버튼
        scanState = findViewById(R.id.scanState);


        if(isScanning) {
            scanState.setText("Start");
        } else {
            scanState.setText("Stop");
        }


        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        startScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScanning) {
                    startBeaconScanning();
                    isScanning = true;
                    scanState.setText("Start");
                    Toast.makeText(BleScannerActivity.this, "스캔을 시작합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BleScannerActivity.this, "이미 스캔 중입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stopScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning) {
                    stopBeaconScanning();
                    isScanning = false;
                    scanState.setText("Stop");
                    Toast.makeText(BleScannerActivity.this, "스캔을 중지합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BleScannerActivity.this, "스캔이 이미 중지되어 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // MacAddress를 입력 후 직접 연동하기 버튼 등 기존 코드...
        // rescanBtn 클릭 이벤트 등 기존 코드...

        //MacAddress를 입력 후 직접 연동하기
        connectBtnCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextString = editTextCompany.getText().toString();
                int count = 0; //list에 있는지 확인
                String beaconSingleton_macAddress; //싱글톤에 들어있는 macAddress.
                String beaconMacAddress ="";

                if(editTextCompany.length() <= 0){
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

//                selectedLocation += ":"+editTextString1 + ":" + editTextString2;
//                System.out.println("선택한 macAddress -------> "+selectedLocation);   //macAddressTest

                for(int i = 0; i < listView.getCount(); i++){
                    beaconSingleton_macAddress = beaconSingleton.getBeaconDomainList().get(i).getMacAddress();

                    String siteBeacon = beaconSingleton_macAddress.substring(0,beaconSingleton_macAddress.length() - 5);

                    siteBeacon += editTextString1 + ":" + editTextString2;

                    System.out.println("siteBeacon ==> : " + siteBeacon);
                    beaconMacAddress = siteBeacon;
                    ++count;
                }
                if(count <= 0){
                    Toast.makeText(BleScannerActivity.this, "통신이상\n현장을 확인후 다시 진행해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent DeviceControl = new Intent(BleScannerActivity.this, DeviceControlActivity.class);
                    DeviceControl.putExtra("DEVICE_ADDRESS", beaconMacAddress);
                    startActivity(DeviceControl);
                }
            }
        });

        //리스트뷰 클릭시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent DeviceControl = new Intent(BleScannerActivity.this,DeviceControlActivity.class);

                BeaconDomain selectedBeacon = (BeaconDomain) parent.getItemAtPosition(position);
                String macAddress = selectedBeacon.getMacAddress();

                DeviceControl.putExtra("DEVICE_ADDRESS", macAddress);

                startActivity(DeviceControl);
            }
        });

        rescanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BeaconSingleton의 비콘 리스트 초기화
                beaconSingleton.resetBeaconDomainList(); // 비콘 데이터 초기화 (기존 코드 유지)

                // 어댑터에 빈 리스트를 전달하여 리스트뷰 초기화
                BeaconAdapter beaconAdapter = new BeaconAdapter(BleScannerActivity.this, beaconSingleton.getBeaconDomainList());
                listView.setAdapter(beaconAdapter);
                beaconAdapter.notifyDataSetChanged();  // 어댑터 갱신하여 리스트 초기화 반영
            }
        });

    }

    // 비콘 스캔 시작 메소드
    private void startBeaconScanning() {
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // 비콘 스캔 중지 메소드
    private void stopBeaconScanning() {
        try {
            beaconManager.stopRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            Log.d("BleScannerActivity", "Scanning stopped.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        // 이전에 추가된 RangeNotifier 제거
        beaconManager.removeRangeNotifier(rangeNotifier);

        // 새로운 RangeNotifier 정의
        rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        String macAddress = beacon.getBluetoothAddress();
                        String[] macArray = macAddress.split(":");

                        int num1 = 0;
                        int num2 = 0;

                        try {
                            num1 = Integer.parseInt(macArray[4]);
                            num2 = Integer.parseInt(macArray[5]);
                        } catch (NumberFormatException e) {
                            num1 = Integer.parseInt(macArray[4], 16);
                            num2 = Integer.parseInt(macArray[5], 16);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        int serialNumber = (num1 * 100) + num2;

                        if (beacon.getRssi() >= -70) {
                            if (beaconSingleton.getBeaconDomainList().isEmpty()) {
                                beaconSingleton.getBeaconDomainList().add(new BeaconDomain(macAddress, serialNumber));
                            }

                            for (Iterator<BeaconDomain> iterator = beaconSingleton.getBeaconDomainList().iterator(); iterator.hasNext(); ) {
                                BeaconDomain beaconDomain = iterator.next();
                                if (beacon.getBluetoothAddress().equals(beaconDomain.getMacAddress())) {
                                    iterator.remove();
                                }
                            }

                            if (beaconSingleton.getBeaconDomainList().size() < 30) {
                                beaconSingleton.getBeaconDomainList().add(new BeaconDomain(macAddress, serialNumber));
                            }

                            // UI 업데이트를 UI 스레드에서 실행
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BeaconAdapter beaconAdapter = new BeaconAdapter(BleScannerActivity.this, beaconSingleton.getBeaconDomainList());
                                    listView.setAdapter(beaconAdapter);
                                    beaconAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        };

        // RangeNotifier를 추가하고 비콘 스캔 시작
        beaconManager.addRangeNotifier(rangeNotifier);
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // 숫자 정규표현식
    public boolean isNumeric(String str) {
        return Pattern.matches("^[0-9]*$", str);
    }
}
