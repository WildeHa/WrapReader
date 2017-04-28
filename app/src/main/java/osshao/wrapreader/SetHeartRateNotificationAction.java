package osshao.wrapreader;

import java.util.UUID;

import osshao.wrapreader.interfacez.IBleProvider;
import osshao.wrapreader.interfacez.SetCharaNotificationAction;

/**
 * Created by LSCM on 2017/4/28.
 */

public class SetHeartRateNotificationAction extends SetCharaNotificationAction{

    protected SetHeartRateNotificationAction() {
        super(Constants.services.UUID_SERVICE_HEART_RATE, Constants.charateristics.UUID_CHAR_HEART_RATE);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
