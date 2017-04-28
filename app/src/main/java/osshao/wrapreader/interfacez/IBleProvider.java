package osshao.wrapreader.interfacez;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Created by LSCM on 2017/4/28.
 */

public interface IBleProvider {

    void connect(BluetoothDevice bluetoothDevice);

    void disconnect();

    boolean isConnected();

    void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value);

    void readCharacteristic(UUID serviceUUID, UUID characteristicUUID);

    void setCharacteristicNotification(UUID serviceUUID, UUID characteristicUUID);

}
