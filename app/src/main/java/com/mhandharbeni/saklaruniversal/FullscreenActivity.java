package com.mhandharbeni.saklaruniversal;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.mhandharbeni.saklaruniversal.databinding.ActivityFullscreenBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FullscreenActivity extends BaseActivity implements BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback {
    private final String TAG = FullscreenActivity.class.getSimpleName();

    private ActivityFullscreenBinding binding;
    private MainPartPagerAdapter pagerAdapter;
    public List<DataScreen> listScreen = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {});
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initAdapter();
        populateScreen();
        initTriggered();

        service.setOnScanCallback(this);
        service.setOnEventCallback(this);
//        service.write();
    }

    byte[] generateDataOn(){
        List<Byte> lByte = new ArrayList<>();
        List<String> lCommand = new ArrayList<>(
                Arrays.asList(
                        "01",
                        "01",
                        "01",
                        "01",
                        "01",
                        "Test"
                )
        );
        int total = 0;
        for (String s : lCommand) {
            for (char c : s.toCharArray()) {
                total += c;
            }
        }
        total = total & 0xFF;
        total = (~total + 1) & 0xFF;
        String hexs = Integer.toHexString(total);
        lCommand.add(hexs);
        lByte.add((byte) 0xAA);
        for (String s: lCommand) {
            for (char c : s.toCharArray()) {
                String hex = String.format("%04x", (int) c);
                lByte.add((byte) c);
            }
        }
        lByte.add((byte) 0x0D);
        lByte.add((byte) 0x0A);

        byte[] rByte = new byte[lByte.size()];
        for (int i = 0; i < lByte.size(); i++) {
            rByte[i] = lByte.get(i);
        }
        return rByte;
    }

    byte[] generateDataOff() {
        List<Byte> lByte = new ArrayList<>();
        List<String> lCommand = new ArrayList<>(
                Arrays.asList(
                        "02",
                        "01",
                        "01",
                        "01",
                        "01",
                        "Test"
                )
        );
        int total = 0;
        for (String s : lCommand) {
            for (char c : s.toCharArray()) {
                total += c;
            }
        }
        total = total & 0xFF;
        total = (~total + 1) & 0xFF;
        String hexs = Integer.toHexString(total);
        lCommand.add(hexs);
        lByte.add((byte) 0xAA);
        for (String s: lCommand) {
            for (char c : s.toCharArray()) {
                String hex = String.format("%04x", (int) c);
                lByte.add((byte) c);
            }
        }
        lByte.add((byte) 0x0D);
        lByte.add((byte) 0x0A);

        byte[] rByte = new byte[lByte.size()];
        for (int i = 0; i < lByte.size(); i++) {
            rByte[i] = lByte.get(i);
        }
        return rByte;
    }

    void initAdapter() {
        pagerAdapter = new MainPartPagerAdapter(getSupportFragmentManager());
        binding.mainPart.setAdapter(pagerAdapter);
    }

    void initTriggered() {
        binding.secondPart.setOnClickListener(this::startBluetooth);
        binding.on.setOnClickListener(view -> {
            service.write(generateDataOn());
            try {
            } catch (Exception ignored) {}
        });
        binding.off.setOnClickListener(view -> {
            service.write(generateDataOff());
            try {
            } catch (Exception ignored) {}
        });
    }

    void populateScreen() {
        for (int i = 0; i < 5; i++) {
            List<DataDetailScreen> listDetail = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                DataDetailScreen dataDetailScreen = new DataDetailScreen();
                dataDetailScreen.setAddressButton("Address "+i);
                dataDetailScreen.setTextButton("Button "+i);
                dataDetailScreen.setTypeButton("Type "+i);
                listDetail.add(dataDetailScreen);
            }

            DataScreen dataScreen = new DataScreen();
            dataScreen.setTitle("DATA SCREEN "+i);
            dataScreen.setContent(ScreenFragment.newInstance(listDetail));
            dataScreen.setData(listDetail);
            listScreen.add(dataScreen);
        }
        pagerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        Log.d(TAG, "onDeviceDiscovered: "+device.getName());
        try {
            if (device.getName().equalsIgnoreCase("Tagtic-SPP")) {
                service.connect(device);
            }
        } catch (Exception ignored) {}
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStartScan() {
        Log.d(TAG, "onStartScan: ");
        bluetoothAdapter.startDiscovery();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStopScan() {
        Log.d(TAG, "onStopScan: ");
        bluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {

    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: "+status.toString());
    }

    @Override
    public void onDeviceName(String deviceName) {
        Log.d(TAG, "onDeviceName: "+deviceName);
    }

    @Override
    public void onToast(String message) {

    }

    @Override
    public void onDataWrite(byte[] buffer) {

    }

    public class MainPartPagerAdapter extends FragmentStatePagerAdapter{

        public MainPartPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listScreen.get(position).getContent();
        }

        @Override
        public int getCount() {
            return listScreen.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return listScreen.get(position).getTitle();
        }
    }

    public static class DataScreen implements Serializable {
        String title;
        Fragment content;
        List<DataDetailScreen> data;

        public DataScreen() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Fragment getContent() {
            return content;
        }

        public void setContent(Fragment content) {
            this.content = content;
        }

        public List<DataDetailScreen> getData() {
            return data;
        }

        public void setData(List<DataDetailScreen> data) {
            this.data = data;
        }
    }
    
    public static class DataDetailScreen implements Serializable {
        String textButton;
        String typeButton;
        String addressButton;

        public DataDetailScreen() {
        }

        public String getTextButton() {
            return textButton;
        }

        public void setTextButton(String textButton) {
            this.textButton = textButton;
        }

        public String getTypeButton() {
            return typeButton;
        }

        public void setTypeButton(String typeButton) {
            this.typeButton = typeButton;
        }

        public String getAddressButton() {
            return addressButton;
        }

        public void setAddressButton(String addressButton) {
            this.addressButton = addressButton;
        }
    }
}