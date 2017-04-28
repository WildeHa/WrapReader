package osshao.wrapreader;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.GregorianCalendar;
import java.util.UUID;

import osshao.wrapreader.interfacez.IBleProvider;

/**
 * Created by LSCM on 2017/4/28.
 */

public class BleActionExecutionService extends Service {

    public static final String ACTION_CONNECTION_STATE_CHANGED = "com.example.oshao.ACTION_CONNECTION_STATE_CHANGED";
    public static final String ACTION_PAIRED = "com.example.oshao.ACTION_PAIRED";
    public static final String ACTION_REALTIME_STEPS = "com.example.oshao.ACTION_REALTIME_STEPS";
    public static final String ACTION_STEPS = "com.example.oshao.ACTION_STEPS";
    public static final String ACTION_HEART_RATE = "com.example.oshao.ACTION_HEART_RATE";
    public static final String ACTION_SYNC_COMPLETED = "com.example.oshao.ACTION_SYNC_COMPLETED";

    public static final String EXTRA_CONNECTION_STATE = "com.example.oshao.EXTRA_CONNECTION_STATE";
    public static final String EXTRA_PAIRING_RESULT_CODE = "com.example.oshao.EXTRA_PAIRING_RESULT_CODE";
    public static final String EXTRA_DEVICE_ADDRESS = "com.example.oshao.EXTRA_DEVICE_ADDRESS";
    public static final String EXTRA_STEPS = "com.example.oshao.EXTRA_STEPS_NUM";
    public static final String EXTRA_HEART_RATE = "com.example.oshao.EXTRA_HEART_RATE";

    public static final int EXTRA_CONNECTION_STATE_CONNECTED = 0x100;
    public static final int EXTRA_CONNECTION_STATE_DISCONNECTED = 0x101;
    public static final int EXTRA_PAIRING_RESULT_CODE_SUCCESS = 0x200;
    public static final int EXTRA_PAIRING_RESULT_CODE_FAILURE = 0x201;

    private static final Object monitor = new Object();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MiBandProvider extends BluetoothGattCallback implements IBleProvider {

        private BluetoothGatt gatt;
        private boolean isServiceDiscovered;

        @Override
        public void connect(BluetoothDevice bluetoothDevice) {
            this.gatt = bluetoothDevice.connectGatt(BleActionExecutionService.this, false, this);
        }

        @Override
        public void disconnect() {
            if (this.gatt == null) {
                return;
            }

            gatt.disconnect();
        }

        @Override
        public boolean isConnected() {
            return isServiceDiscovered;
        }

        @Override
        public void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
            if (gatt == null || !isServiceDiscovered) {
                Log.i("MiBandProvider", "writeCharacteristic: device is not connected or services are not discovered");
                return;
            }

            BluetoothGattCharacteristic characteristic = gatt.getService(serviceUUID)
                    .getCharacteristic(characteristicUUID);

            characteristic.setValue(value);

            if (!gatt.writeCharacteristic(characteristic)) {
                Log.i("MiBandProvider", "writeCharacteristic:false");
            }
        }

        @Override
        public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
            if (gatt == null || !isServiceDiscovered) {
                Log.i("MiBandProvider", "readCharacteristic: device is not connected of services are not discovered");
                return;
            }

            BluetoothGattCharacteristic characteristic = gatt.getService(serviceUUID)
                    .getCharacteristic(characteristicUUID);

            if (!gatt.readCharacteristic(characteristic)) {
                Log.i("MiProvider", "readCharacteristic: false");
            }
        }

        @Override
        public void setCharacteristicNotification(UUID serviceUUID, UUID characteristicUUID) {
            if (gatt == null || isServiceDiscovered) {
                Log.i("MiBandProvider", "setCharacteristicNotification: device is not connected of services are not discovered");]
                return;
            }
            BluetoothGattCharacteristic characteristic = gatt.getService(serviceUUID).getCharacteristic(characteristicUUID);

            gatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Constants.descriptors.CLIENT_CHARACTERISTIC_CONFIG);

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);

            if (!gatt.writeDescriptor(descriptor)) {
                Log.i("MiBandProvider", "writeDescriptor: false");
            }

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                this.gatt = gatt;

                if (!gatt.discoverServices()) {
                    Log.i("MiBandProvider", "services discovery processes not been started");
                    close();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i("MiBandProvider", "disconnected" + gatt.getDevice().getAddress());
                    Intent intent = new Intent(ACTION_CONNECTION_STATE_CHANGED)
                            .putExtra(EXTRA_CONNECTION_STATE, EXTRA_CONNECTION_STATE_DISCONNECTED);
                    LocalBroadcastManager.getInstance(BleActionExecutionService.this).sendBroadcast(intent);
                    close();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                this.gatt = gatt;
                isServiceDiscovered = true;

                Log.i("MiBandProvider", "connected : " + gatt.getDevice().getAddress());

                Intent intent = new Intent(ACTION_CONNECTION_STATE_CHANGED)
                        .putExtra(EXTRA_CONNECTION_STATE, EXTRA_CONNECTION_STATE_CONNECTED);

                LocalBroadcastManager.getInstance(BleActionExecutionService.this).sendBroadcast(intent);
            } else {
                Log.i("MinBandProvider", "onServicesDiscovered, Status : " + status);
                close();
            }

            yield();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i("MiBandProvider", "onCharacteristicRead,Status : " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Intent intent = new Intent();
                byte[] data = characteristic.getValue();

                if (characteristic.getUuid().equals(Constants.charateristics.UUID_CHAR_REALTIME_STEPS)) {
                    int steps = handleStepsNotification(data);
                    intent.setAction(ACTION_STEPS).putExtra(EXTRA_STEPS, steps);
                }

                LocalBroadcastManager.getInstance(BleActionExecutionService.this).sendBroadcast(intent);
            }

            yield();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i("MiBandProvider", "onCharacteristicWriter,status: " + status);

            yield();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i("MiBandProvider", "onDescriptorWrite,status: " + status);

            yield();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);


            Intent intent = new Intent();
            byte[] data = characteristic.getValue();

            if (characteristic.getUuid().equals(Constants.charateristics.UUID_CHAR_REALTIME_STEPS)) {
                int steps = handleStepsNotification(data);
                intent.setAction(ACTION_REALTIME_STEPS).putExtra(EXTRA_STEPS, steps);
            } else if (characteristic.getUuid().equals(Constants.charateristics.UUID_CHAR_HEART_RATE)) {
                int heartRate = handleHeartRateNotification(data);
                intent.setAction(ACTION_HEART_RATE).putExtra(EXTRA_HEART_RATE, heartRate);
            } else if (characteristic.getUuid().equals(Constants.charateristics.UUID_CHAR_ACTIVITY)) {

            }
        }

        private void close() {
            if (gatt == null) {
                return;
            }

            gatt.close();
            gatt = null;
            isServiceDiscovered = false;
        }

    }

    private static void yield() {
        synchronized (monitor) {
            monitor.notify();
        }
    }

    private int handleStepsNotification(byte[] data) {
        return data[3] << 24 | (data[2] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[0] & 0xFF);
    }

    private int handleHeartRateNotification(byte[] data) {
        int heartRate = -1;

        if (data.length == 2 && data[0] == 6) {
            heartRate = data[1] & 0xFF;
        }
        return heartRate;
    }

    private static final int activityDataHolderSize = 3 * 60 * 4;

    // From here I dont know what is going on!!!!!!!
    
//    private static class ActivityStruct {
//        public byte[] activityDataHolder = new byte[activityDataHolderSize];
//        public int activityDataHolderProgress = 0;
//        public int activityDataRemainingBytes = 0;
//        public int activityDataUntilNextHeader = 0;
//        public GregorianCalendar activityDataTimestampProgress = null;
//        public GregorianCalendar activityDataTimestampToAck = null;
//    }
//
//    private ActivityStruct activityStruct;
//
//    private void handleActivityDataNotification(byte[] data) {
//        boolean firstChunk = activityStruct == null;
//        if (firstChunk) {
//            activityStruct = new ActivityStruct();
//        }
//
//        if (data.length == 11) {
//            int dataType = data[0];
//
//            GregorianCalendar timestamp = parseTimestamp(data, 1);
//
//            int totalDataToRead = (data[7] & 0xFF) | ((data[8] & 0xFF) << 8);
//            totalDataToRead *= (dataType == 1) ? 3 : 1;
//
//            int dataUntilNextHeader = (data[9] & 0xFF) | ((data[10] & 0xFF) << 8);
//            dataUntilNextHeader *= (dataType == 1) ? 3 : 1;
//
//            activityStruct.activityDataRemainingBytes = activityStruct.activityDataUntilNextHeader = dataUntilNextHeader;
//            activityStruct.activityDataTimestampToAck = (GregorianCalendar) timestamp.clone();
//            activityStruct.activityDataTimestampProgress = timestamp;
//        } else {
//            bufferActivityData(data);
//        }
//
//        if (activityStruct.activityDataRemainingBytes == 0) {
//            sendAckDataTransfer(activityStruct.activityDataTimestampToAck, activityStruct.activityDataUntilNextHeader);
//        }
//    }
//
//    private void bufferActivityData(byte[] value) {
//        if (activityStruct.activityDataRemainingBytes >= value.length) {
//            if (value.length == 20 || value.length == activityStruct.activityDataRemainingBytes) {
//                System.arraycopy(value, 0, activityStruct.activityDataHolder, activityStruct.activityDataHolderProgress, value.length);
//                activityStruct.activityDataHolderProgress += value.length;
//                activityStruct.activityDataRemainingBytes -= value.length;
//
//                if (this.activityDataHolderSize == activityStruct.activityDataHolderProgress) {
//                    flushActivityDataHolder();
//                }
//            }
//        }
//    }


}
