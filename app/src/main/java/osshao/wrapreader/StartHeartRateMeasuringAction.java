package osshao.wrapreader;

import osshao.wrapreader.interfacez.IBleProvider;

/**
 * Created by LSCM on 2017/4/28.
 */

public class StartHeartRateMeasuringAction extends WriteAction {

    public StartHeartRateMeasuringAction() {
        super(Constants.services.UUID_SERVICE_HEART_RATE,
                Constants.charateristics.UUID_CHAR_HEART_RATE_CONTROL_POINT,
                Constants.protocol.START_HEART_RATE_SCAN);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
