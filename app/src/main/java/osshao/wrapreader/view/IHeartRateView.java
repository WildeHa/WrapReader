package osshao.wrapreader.view;

import android.content.Context;

import java.util.List;

import osshao.wrapreader.model.HrMeasurementInfo;

/**
 * Created by LSCM on 2017/4/28.
 */

public interface IHeartRateView {

    void startMeasuringAnimation();

    void stopMeasuringAnimation();

    void setHeartRate(int heartRate, long animDuration);

    void setHeartRate(int heartRate);

    void addMeasurementInfo(HrMeasurementInfo info);

    void addMeasurementInfo(List<HrMeasurementInfo> info);

    void showFab();

    void hideFab();

    Context getContext();

}
