package osshao.wrapreader;

import java.util.UUID;

import osshao.wrapreader.interfacez.IBleAction;
import osshao.wrapreader.interfacez.IBleProvider;

/**
 * Created by LSCM on 2017/4/28.
 */

public class WriteAction implements IBleAction {

    protected byte[] data;
    protected UUID serviceUUID;
    protected UUID characteristicUUID;

    protected WriteAction(UUID serviceUUID, UUID characteristicUUID) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
    }

    protected WriteAction(UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        this(serviceUUID, characteristicUUID);
        this.data = data;
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {

        if (provider.isConnected()) {
            provider.writeCharacteristic(this.serviceUUID, this.characteristicUUID, this.data);
            return true;
        }
        return false;
    }
}
