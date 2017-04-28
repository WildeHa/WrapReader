package osshao.wrapreader.interfacez;

import java.util.UUID;

/**
 * Created by LSCM on 2017/4/28.
 */

public class SetCharaNotificationAction implements IBleAction {

    protected UUID serviceUUID;
    protected UUID characteristicUUID;

    protected SetCharaNotificationAction(UUID serviceUUID, UUID characteristicUUID) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        if (provider.isConnected()){
            provider.setCharacteristicNotification(this.serviceUUID,this.characteristicUUID);
            return true;
        }
        return false;
    }
}
