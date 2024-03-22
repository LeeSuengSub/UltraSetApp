package net.woorisys.UltraSetApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //뒤로가기 두번클릭
    private final long finishtimeed = 1000;
    private long presstime = 0;
    Button siteBtn;
    Button companyBtn;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        siteBtn = (Button) findViewById(R.id.siteButton);
        companyBtn = (Button) findViewById(R.id.companyButton);

        siteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent  = new Intent(MainActivity.this, SiteBleScannerActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        companyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent  = new Intent(MainActivity.this, BleScannerActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        AutoPermissions.Companion.loadAllPermissions(this, 101);

        if (bluetoothAdapter.isEnabled()) {
            // 블루투스 관련 실행 진행
        } else {
            // 블루투스 활성화 하도록
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
    }
/*
    public void clickBtn(View v) {

        try{
            //try-catch 추가 2023-01-31
            Intent intent  = new Intent(MainActivity.this, BleScannerActivity.class);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
 */
    @Override
    public void onDenied(int i, @NonNull String[] strings) {

    }

    @Override
    public void onGranted(int i, @NonNull String[] strings) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
//        Toast.makeText(this, "requestCode : "+requestCode+"\n  permissions : "+permissions+"\n  grantResults :"+grantResults, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;
        if (0 <= intervalTime && finishtimeed >= intervalTime) {
            finishAffinity();
            super.onBackPressed();
        } else {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}