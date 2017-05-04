package osshao.wrapreader;

import android.bluetooth.BluetoothDevice;

public interface IBleDeviceDiscoverer {

    boolean discover();

    boolean isDiscovering();

    void cancelDiscovering();

    void setOnDeviceDiscoveredCallback(OnDeviceDiscoveredCallback callback);

    void setOnCanceledCallback(OnCanceledCallback callback);

    interface OnDeviceDiscoveredCallback {
        void onDeviceDiscovered(BluetoothDevice device);
    }

    interface OnCanceledCallback {
        void onCanceled(boolean timeout);
    }
}
