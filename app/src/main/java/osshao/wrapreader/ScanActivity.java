package osshao.wrapreader;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import osshao.wrapreader.presenter.IScanPresenter;
import osshao.wrapreader.presenter.ScanPresenterImpl;
import osshao.wrapreader.view.IScanView;

public class ScanActivity extends AppCompatActivity implements IScanView {

    private static final String ATTRIBUTE_DEVICE_NAME = "device_name";
    private static final String ATTRIBUTE_DEVICE_ADDRESS = "device_address";

    private IScanPresenter mScanPresenter;
    private ListView mLvDiscoveredDevices;
    private CircularProgressView mProgressIndicator;
    private ArrayList<Map<String, String>> mData;
    private SimpleAdapter mAdapter;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.scan_activity_title);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        int permissionCheck = 1001;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},permissionCheck);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("ScanActivity", "Should use permission granted");
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This App needs location access");
                builder.setMessage("Please grant location access so this App can detect beacons");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        this.mData = new ArrayList<>();

        this.mLvDiscoveredDevices = (ListView) findViewById(R.id.lv_discovered_devices);

        this.mLvDiscoveredDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> item = (Map<String, String>) parent.getAdapter().getItem(position);
                ScanActivity.this.mScanPresenter.bindDevice(position, item.get(ATTRIBUTE_DEVICE_ADDRESS));
            }
        });

        this.mLvDiscoveredDevices.addHeaderView(getLayoutInflater().inflate(R.layout.discovered_devices_list_header, null), null, false);
        this.mLvDiscoveredDevices.addFooterView(getLayoutInflater().inflate(R.layout.discovered_devices_list_footer, null), null, false);

        String[] from = {ATTRIBUTE_DEVICE_NAME, ATTRIBUTE_DEVICE_ADDRESS};
        int[] to = {R.id.tv_title, R.id.tv_subtitle};

        this.mAdapter = new SimpleAdapter(this, this.mData, R.layout.discovered_device_info_item, from, to);
        this.mLvDiscoveredDevices.setAdapter(this.mAdapter);

        this.mProgressIndicator = (CircularProgressView) findViewById(R.id.progress_indicator);

        this.mScanPresenter = new ScanPresenterImpl(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        this.mScanPresenter.startScan();
    }

    @Override
    protected void onDestroy() {
        this.mScanPresenter.onDestroy();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isScanning = this.mScanPresenter.isScanning();

        menu.findItem(R.id.action_start_scan).setEnabled(!isScanning);
        menu.findItem(R.id.action_cancel_scan).setEnabled(isScanning);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_start_scan) {
            this.mScanPresenter.startScan();
            return true;
        } else if (id == R.id.action_cancel_scan) {
            this.mScanPresenter.stopScan();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addDeviceInfoItem(String name, String address) {
        Map<String, String> deviceInfo = new HashMap<>();
        deviceInfo.put(ATTRIBUTE_DEVICE_NAME, name);
        deviceInfo.put(ATTRIBUTE_DEVICE_ADDRESS, address);
        this.mData.add(deviceInfo);
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setItemSubtitle(int pos, String text) {
        ((TextView) this.mLvDiscoveredDevices.getChildAt(pos).findViewById(R.id.tv_subtitle))
                .setText(text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.mLvDiscoveredDevices.setEnabled(enabled);
    }

    @Override
    public void clear() {
        this.mData.clear();
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void startProgressAnimation() {
        this.mProgressIndicator.startAnimation();
        this.mProgressIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgressAnimation() {
        this.mProgressIndicator.stopAnimation();
        this.mProgressIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void returnResult(int resultCode, Intent data) {
        setResult(resultCode, data);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScanActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ScanActivity", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
