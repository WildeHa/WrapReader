package osshao.wrapreader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import osshao.wrapreader.interfacez.IHeartRatePresenter;
import osshao.wrapreader.model.HrMeasurementInfo;
import osshao.wrapreader.view.IHeartRateView;

/**
 * Created by LSCM on 2017/5/2.
 */

public class HeartRateFragment extends Fragment implements IHeartRateView {

    private static final String ATTRIBUTE_HEART_RATE = "heart_rate";
    private static final String ATTRIBUTE_DATE_TIME = "date_time";
    private static final String ATTRIBUTE_HEART_RATE_STATE = "heart_rate_state";

    private TextView textView;
    private Button buttonStartHeartRate;

    private int heartRate;
    private IHeartRatePresenter mHeartRatePresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);
        textView = (TextView) view.findViewById(R.id.textViewHeartRate);
        buttonStartHeartRate = (Button) view.findViewById(R.id.buttonStartHeartRate);

        buttonStartHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeartRatePresenter.startHeartRateMeasuring();
            }
        });
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_heart_rate);
        mHeartRatePresenter = new HeartRatePresenterImpl(this);
    }

    @Override
    public void startMeasuringAnimation() {

    }

    @Override
    public void stopMeasuringAnimation() {

    }

    @Override
    public void setHeartRate(int heartRate, long animDuration) {

    }

    @Override
    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
        textView.setText(String.valueOf(heartRate));
    }

    @Override
    public void addMeasurementInfo(HrMeasurementInfo info) {

    }

    @Override
    public void addMeasurementInfo(List<HrMeasurementInfo> info) {

    }

    @Override
    public void showFab() {

    }

    @Override
    public void hideFab() {

    }

    @Override
    public void onDestroy() {
        mHeartRatePresenter.onDestroy();
        super.onDestroy();
    }
}
