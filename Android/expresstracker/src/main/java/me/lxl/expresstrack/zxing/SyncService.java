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
        List<ExpressInfo> list;
        list = mExpressDBHelper.queryAll();
        ArrayList arrayList = new ArrayList();

        for(int i = 0 ; i < list.size(); i++)
        {
            ExpressInfo expressInfo = list.get(i);
            Log.d(TAG, "expressInfo :" + expressInfo.toString());
            arrayList.add(expressInfo.getExpressCode());
        }
        boolean exist = arrayList.contains("tgg");
        Log.d(TAG, "getExpressCodeList exist tgg = " + exist);
        return arrayList;
    }

    private void syncFromServer()
    {
        ArrayList expressCodeList = getExpressCodeList();
        JSONObject jsonOwnerObject = new JSONObject();
        try {
            jsonOwnerObject.put("owner", StaticParam.mUserName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"  syncFromServer jsonObject : " + jsonOwnerObject.toString());

        String serverResponse = HttpConnectHelper.post(jsonOwnerObject, StaticParam.SYNC_FROM_SERVER_ADDR);
        try {

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
                        String expressCode = jsonObject.optString("express_code");
                        String receiver = jsonObject.optString("receiver");
                        String sendExpressDate = jsonObject.optString("express_date");
                        double money = jsonObject.getDouble("express_money");
                        String logisticsInfo = jsonObject.optString("logisticsInfo");

                        ExpressInfo expressInfo = new ExpressInfo(expressCode, receiver, sendExpressDate, logisticsInfo, money);
                        Log.d(TAG, "expressInfo : " + expressInfo.toString());
                        Log.d(TAG, "expressCode :" + expressCode + ", " +expressCodeList.toString());
                        if(expressCodeList.contains(expressCode)) {
                            //update
                            long id = mExpressDBHelper.update(expressInfo);
                        } else {
                            //insert
                            long id = mExpressDBHelper.insert(expressInfo);
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
        Log.i(TAG,"  syncToServer expressList : " + expressList.toString());

        String serverResponse = HttpConnectHelper.post(expressList, StaticParam.CLIENT_TO_SERVER_ADDR);
        try {
            JSONArray jsonArray = new JSONArray(serverResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    String expressCode = jsonObject.optString("expressCode");
                    String result = jsonObject.optString("result");
                    String reason = jsonObject.optString("reason");
                    Log.d(TAG, "expressCode : " + expressCode + ", result = " + result + ", reason = " + reason);
                    if(result.equalsIgnoreCase("suxxess") || (result.equalsIgnoreCase("fail") && reason.equalsIgnoreCase("already exit"))) {
                        Log.d(TAG, "syncToServer update local DB");
                        mExpressDBHelper.updateHaveSync(expressCode);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"  syncToServer serverResponse : " + serverResponse);

    }
}