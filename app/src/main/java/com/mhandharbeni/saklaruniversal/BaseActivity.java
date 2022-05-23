package com.mhandharbeni.saklaruniversal;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BaseActivity extends AppCompatActivity implements MultiplePermissionsListener {
    private final String TAG = BaseActivity.class.getSimpleName();

    ActivityResultLauncher<Intent> launcher;
    BluetoothAdapter bluetoothAdapter;
    BluetoothConfiguration config;
    BluetoothService service;

    @Override
    protected void onStart() {
        super.onStart();
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (service.getStatus() == BluetoothStatus.NONE) {
                            service.startScan();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        initBluetooth();
    }

    void requestPermission() {
        List<String> listPermission = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listPermission.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        listPermission.add(Manifest.permission.BLUETOOTH);
        listPermission.add(Manifest.permission.BLUETOOTH_ADMIN);
        listPermission.add(Manifest.permission.BLUETOOTH_PRIVILEGED);
        listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        Dexter.withContext(this)
                .withPermissions(listPermission)
                .withListener(this)
                .check();
    }

    void initBluetooth() {
        config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = getResources().getString(R.string.app_name);
        config.callListenersInMainThread = true;

        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Required

        BluetoothService.init(config);
        service = BluetoothService.getDefaultInstance();
    }

    void startBluetooth(View view) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                launcher.launch(enableBtIntent);
            } else {
                service.startScan();
            }
        }

    }

    void stopBluetooth() {
        try {
            service.stopScan();
        } catch (Exception ignored) {}
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
        if (multiplePermissionsReport.areAllPermissionsGranted()) {
            initBluetooth();
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

    }
}
