package osshao.wrapreader;

import java.util.UUID;

import osshao.wrapreader.interfacez.IBleAction;
import osshao.wrapreader.interfacez.IBleProvider;

/**
 * Created by LSCM on 2017/5/4.
 */

public class ReadAction implements IBleAction {
    protected UUID serviceUUID;
    protected UUID characteristicUUID;

    public ReadAction(MiBandValue value) {
        this.serviceUUID = Constants.services.UUID_SERVICE_MILI;
        if (value == MiBandValue.STEPS) {
            characteristicUUID = Constants.charateristics.UUID_CHAR_REALTIME_STEPS;
        }
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        if (provider.isConnected()) {
            provider.readCharacteristic(serviceUUID, characteristicUUID);
            return true;
        }
        return false;
    }
}
