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

import java.util.ArrayList;
import java.util.List;
import me.lxl.expresstrack.zxing.R;

public class SyncService extends IntentService
{

    private final String SYNC_ACTION = "me.lxl.expresstrack.zxing.SyncService.startsync";
    private final static String TAG = StaticParam.TAG;
    private static boolean isSyncing = false;

    private LogisticsDBHelper mLogisticsDBHelper;


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
        mLogisticsDBHelper = LogisticsDBHelper.getInstance(this);
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
                Log.i(TAG,"  SYNC_ACTION ... syncFromServer");
                syncFromServer();
                Log.i(TAG,"  SYNC_ACTION ... syncToServer");
                syncToServer();
                isSyncing = false;
            }
        }
    }

    private ArrayList getExpressCodeList()
    {
        List<LogisticsInfo> list;
        list = mLogisticsDBHelper.queryAll();
        ArrayList arrayList = new ArrayList();

        for(int i = 0 ; i < list.size(); i++)
        {
            LogisticsInfo logisticsInfo = list.get(i);
            Log.d(TAG, "logisticsInfo :" + logisticsInfo.toString());
            arrayList.add(logisticsInfo.getLogisticsCode());
        }
        return arrayList;
    }

    private void syncFromServer()
    {
        ArrayList logisticsCodeList = getExpressCodeList();
        JSONObject jsonOwnerObject = new JSONObject();
        try {
            jsonOwnerObject.put("owner", StaticParam.mUserName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.i(TAG,"  syncFromServer jsonObject : " + jsonOwnerObject.toString());

        String serverResponse = HttpConnectHelper.post(jsonOwnerObject, StaticParam.SYNC_FROM_SERVER_ADDR);
        try {
//            Log.d(TAG, "serverResponse = " + serverResponse);
            JSONObject jsonObj = new JSONObject(serverResponse);
            int code = jsonObj.getInt("code");
            String msg = jsonObj.getString("msg");
            String count = jsonObj.getString("count");
            Log.d(TAG, "syncFromServer code = " + code + ", msg = " + msg + ", count = " + count);
            if(code == 0) {

                JSONArray jsonArray = jsonObj.getJSONArray("data");
                Log.d(TAG, "jsonArray : " + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d(TAG, "i = " + i + " , jsonObject : " + jsonObject);
                    if (jsonObject != null) {
                        String logisticsCode = jsonObject.optString("logisticsCode");
                        String shipperCode = jsonObject.optString("shipperCode");
                        String receiver = jsonObject.optString("receiver");
                        String creater = jsonObject.optString("sender");
                        String shipDate = jsonObject.optString("shipDate");
                        double shippingMoney = jsonObject.getDouble("shippingMoney");
                        String logisticsInfoStr = jsonObject.optString("logisticsInfo");
                        String latestLogisticsInfo = jsonObject.getString("latestLogisticsInfo");
                        int logisticsState = jsonObject.optInt("logisticsState");

                        LogisticsInfo logisticsInfo = new LogisticsInfo(logisticsCode, shipperCode, receiver, creater, shippingMoney, shipDate, logisticsInfoStr, latestLogisticsInfo, logisticsState);
                        Log.d(TAG, "logisticsInfo : " + logisticsInfo.toString());
                        Log.d(TAG, "logisticsCode :" + logisticsCode + ", " +logisticsCodeList.toString());
                        if(logisticsCodeList.contains(logisticsCode)) {
                            //update
                            long id = mLogisticsDBHelper.update(logisticsInfo);
                        } else {
                            //insert
                            long id = mLogisticsDBHelper.insert(logisticsInfo);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncToServer()
    {
        List<LogisticsInfo> list;
        list = mLogisticsDBHelper.queryAllNeedSync();

        JSONArray logisticsList = new JSONArray();

        for(int i = 0 ; i < list.size(); i++)
        {
            JSONObject jsonObject = new JSONObject();
            LogisticsInfo logisticsInfo = list.get(i);
            Log.d(TAG, "logisticsInfo :" + logisticsInfo.toString());
            try {
                jsonObject.put("owner", StaticParam.mUserName);
                jsonObject.put("logisticsCode", logisticsInfo.getLogisticsCode());
                jsonObject.put("shipperCode", logisticsInfo.getShipperCode());
                jsonObject.put("receiver", logisticsInfo.getReceiver());
                jsonObject.put("sender", StaticParam.mUserName);
                jsonObject.put("shipDate", logisticsInfo.getShipDate());
                jsonObject.put("shippingMoney", logisticsInfo.getShippingMoney());
                jsonObject.put("syncToServer", logisticsInfo.getSyncToServer());
                logisticsList.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG,"  syncToServer logisticsList : " + logisticsList.toString());

        String serverResponse = HttpConnectHelper.post(logisticsList, StaticParam.CLIENT_TO_SERVER_ADDR);
        try {
            JSONArray jsonArray = new JSONArray(serverResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    String logisticsCode = jsonObject.optString("logisticsCode");
                    String result = jsonObject.optString("result");
                    String reason = jsonObject.optString("reason");
                    Log.d(TAG, "logisticsCode : " + logisticsCode + ", result = " + result + ", reason = " + reason);
                    if(result.equalsIgnoreCase("suxxess") || (result.equalsIgnoreCase("fail") && reason.equalsIgnoreCase("already exit"))) {
                        Log.d(TAG, "syncToServer update local DB");
                        mLogisticsDBHelper.updateHaveSync(logisticsCode);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"  syncToServer serverResponse : " + serverResponse);

    }
}