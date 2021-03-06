package osshao.wrapreader.model;

import java.util.Date;

/**
 * Created by LSCM on 2017/4/28.
 */

public class HrMeasurementInfo {
    private int mHeartRate;
    private Date mDateTime;
    private boolean mHeartRateState;

    public HrMeasurementInfo(int heartRate, Date dateTime, boolean heartRateState) {
        this.mHeartRate = heartRate;
        this.mDateTime = dateTime;
        this.mHeartRateState = heartRateState;
    }

    public int getHeartRate() {
        return this.mHeartRate;
    }

    public boolean isHeartRateStateNormal() {
        return this.mHeartRateState;
    }

    public Date getDateTime() {
        return this.mDateTime;
    }

}
