package osshao.wrapreader;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import osshao.wrapreader.interfacez.IBleAction;
import osshao.wrapreader.interfacez.IBleActionExecutionServiceControlPoint;

/**
 * Created by LSCM on 2017/4/28.
 */

public class BleActionExecutionServiceControlPoint implements IBleActionExecutionServiceControlPoint {

    private static final String TAG = "BAException";

    private static BleActionExecutionServiceControlPoint instance;

    private Context context;
    private ServiceConnection serviceConnection;
    private boolean isServiceConnected;
    private IBleActionExecutionServiceControlPoint controlPoint;
    private List<IBleAction> buffer;

    private BleActionExecutionServiceControlPoint(Context context) {
        this.context = context;
        this.buffer = new LinkedList<>();

        this.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                isServiceConnected = true;
                controlPoint = (IBleActionExecutionServiceControlPoint) service;

                if (buffer.size() != 0) {
                    addToQueue(buffer);
                    buffer.clear();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected");
                isServiceConnected = false;
            }
        };
    }

    public synchronized static BleActionExecutionServiceControlPoint getInstance(Context context) {
        if (instance == null) {
            instance = new BleActionExecutionServiceControlPoint(context);
        }

        return instance;
    }

    public boolean bind() {
        Intent intent = new Intent(this.context, BleActionExecutionService.class);
        return this.context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        this.context.unbindService(serviceConnection);
    }

    @Override
    public boolean addToQueue(IBleAction bleAction) {
        if (!isServiceConnected) {
            buffer.add(bleAction);
            return false;
        }
        return controlPoint.addToQueue(bleAction);
    }

    @Override
    public boolean addToQueue(List<IBleAction> bleActions) {

        if (!isServiceConnected) {
            buffer.addAll(bleActions);
            return false;
        }
        return controlPoint.addToQueue(bleActions);
    }
}
