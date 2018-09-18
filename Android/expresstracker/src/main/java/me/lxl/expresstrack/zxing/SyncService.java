package me.lxl.expresstrack.zxing;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SyncService extends IntentService
{

    private final String SYNC_ACTION = "me.lxl.expresstrack.zxing.SyncService.startsync";
    private final static String TAG = StaticParam.TAG;
    private static boolean isSyncing = false;

    private ExpressDBHelper mExpressDBHelper;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncService(String name) {
        super(name);
    }

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mExpressDBHelper = ExpressDBHelper.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG,"call onHandleIntent...");
        String action = intent.getAction();
        if(action != null && action.equals(SYNC_ACTION) && !isSyncing){
            if(StaticParam.mUserName != "" && StaticParam.mPassword != ""){
                isSyncing = true;
                Log.i(TAG,"  SYNC_ACTION ...");
                syncToServer();
                isSyncing = false;
            }
        }
    }

    private void syncToServer()
    {
        List<ExpressInfo> list;
        list = mExpressDBHelper.queryAllNeedSync();

        JSONArray expressList = new JSONArray();

        for(int i = 0 ; i < list.size(); i++)
        {
            JSONObject jsonObject = new JSONObject();
            ExpressInfo expressInfo = list.get(i);
            Log.d(TAG, "expressInfo :" + expressInfo.toString());
            try {
                jsonObject.put("owner", StaticParam.mUserName);
                jsonObject.put("expressCode", expressInfo.getExpressCode());
                jsonObject.put("receiver", expressInfo.getReceiver());
                jsonObject.put("expressDate", expressInfo.getExpressDate());
                jsonObject.put("expressMoney", expressInfo.getExpressMoney());
                jsonObject.put("syncToServer", expressInfo.getSyncToServer());
                jsonObject.put("expressCode", expressInfo.getExpressCode());
                expressList.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG,"  SYNC_ACTION expressList : " + expressList.toString());

        String serverResponse = HttpConnectHelper.post(expressList);

        Log.i(TAG,"  SYNC_ACTION serverResponse : " + serverResponse);

    }
}