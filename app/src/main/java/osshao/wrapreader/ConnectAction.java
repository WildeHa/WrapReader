package osshao.wrapreader;

import android.bluetooth.BluetoothDevice;

import osshao.wrapreader.interfacez.IBleAction;
import osshao.wrapreader.interfacez.IBleProvider;

/**
 * Created by LSCM on 2017/4/28.
 */

public class ConnectAction implements IBleAction {

    private BluetoothDevice bluetoothDevice;

    public ConnectAction(BluetoothDevice device) {
        if (device == null) {
            throw new NullPointerException("Device is null");
        }
        this.bluetoothDevice = device;
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        if (!provider.isConnected()) {
            provider.connect(bluetoothDevice);
            return true;
        }
        return false;
    }
}
