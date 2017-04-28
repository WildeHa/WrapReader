package osshao.wrapreader;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.support.v4.content.LocalBroadcastManager;

import java.util.LinkedList;
import java.util.List;

import osshao.wrapreader.db.HrMeasurementHistoryDbHelper;
import osshao.wrapreader.interfacez.IBleAction;
import osshao.wrapreader.interfacez.IHeartRatePresenter;
import osshao.wrapreader.model.HrMeasurementInfo;
import osshao.wrapreader.model.UserProfile;
import osshao.wrapreader.view.IHeartRateView;

/**
 * Created by LSCM on 2017/4/28.
 */

public class HeartRatePresenterImpl implements IHeartRatePresenter {

    private static final int HR_MEASURMENT_INTO_LIMIT = 20;

    // TODO: 2017/4/28 Open the interface if further ui requirement is needed
    private IHeartRateView heartRateView;

    private BleActionExecutionServiceControlPoint controlPoint;
    private BroadcastReceiver broadcastReceiver;
    private HrMeasurementHistoryDbHelper hrMeasurementHistoryDbHelper;

    public HeartRatePresenterImpl(IHeartRateView view) {
        this.heartRateView = view;
        heartRateView.setHeartRate(0);
        controlPoint = BleActionExecutionServiceControlPoint.getInstance(view.getContext());
        hrMeasurementHistoryDbHelper = HrMeasurementHistoryDbHelper.getInstance(view.getContext());

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int heartRate = intent.getIntExtra(BleActionExecutionService.EXTRA_HEART_RATE, 0);
                heartRateView.stopMeasuringAnimation();
                heartRateView.setHeartRate(heartRate, 1000L);
                HrMeasurementInfo info = new HrMeasurementInfo(heartRate, Calendar.getInstance().getTime(), heartRate < 90);
                hrMeasurementHistoryDbHelper.InsertHrMeasurementInfo(info);
                heartRateView.showFab();
            }
        };

        view.addMeasurementInfo(hrMeasurementHistoryDbHelper.SelectHrMeasurementInfo(HR_MEASURMENT_INTO_LIMIT));
        LocalBroadcastManager.getInstance(heartRateView.getContext())
                .registerReceiver(broadcastReceiver, new IntentFilter(BleActionExecutionService.ACTION_HEART_RATE));
    }


    @Override
    public void startHeartRateMeasuring() {
        heartRateView.hideFab();
        heartRateView.startMeasuringAnimation();

        BluetoothDevice bluetoothDevice = UserPreferences.getInstance().loadBondedDevice(heartRateView.getContext());
        UserProfile userProfile = new UserProfile(10000000, UserProfile.GENDER_MALE, 21, 182, 76, "Oscar", 0);

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new ConnectAction(bluetoothDevice));
        bleActions.add(new SetUserProfileAction(userProfile, bluetoothDevice.getAddress()));
        bleActions.add(new SetHeartRateNotificationAction());
        bleActions.add(new StartHeartRateMeasuringAction());

        controlPoint.addToQueue(bleActions);

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(heartRateView.getContext()).unregisterReceiver(broadcastReceiver);
        heartRateView = null;
    }
}
