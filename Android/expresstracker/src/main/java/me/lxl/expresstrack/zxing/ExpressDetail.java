package me.lxl.expresstrack.zxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ExpressDetail extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = StaticParam.TAG;
    private LogisticsInfo mLogisticsInfo = null;
    private Button mDeleteButton = null;
    private Button mModifyButton = null;
    private Button mCancelButton = null;
    private Button mSaveButton = null;
    private EditText mExpressCode = null;
    private EditText mMoney = null;
    private EditText mReceiver = null;
    private EditText mSendDate = null;
    private EditText mLogisticsStatus = null;
    private final int MODEFY_MODE = 0;
    private final int NORMAL_MODE = 1;

    private static LogisticsDBHelper mLogisticsDBHelper;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.express_detail);
        Intent intent = getIntent();
        if(intent == null || intent.hasExtra("LogisticsInfo") == false) {
            finish();
        }
        mLogisticsInfo = intent.getParcelableExtra("LogisticsInfo");
        mLogisticsDBHelper = LogisticsDBHelper.getInstance(this);
        setupToolbar();
        initView();
        initValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView()
    {
        mDeleteButton = (Button)findViewById(R.id.delete_express);
        mModifyButton = (Button)findViewById(R.id.modify_express);
        mCancelButton = (Button)findViewById(R.id.cancel_express);
        mSaveButton = (Button)findViewById(R.id.save_express);
        mExpressCode = (EditText)findViewById(R.id.express_code_edittext);
        mMoney = (EditText)findViewById(R.id.detail_express_money_exittext);
        mReceiver = (EditText)findViewById(R.id.receiver);
        mSendDate = (EditText)findViewById(R.id.send_express_date_edittext);
        mLogisticsStatus = (EditText)findViewById(R.id.current_logistics_state_edittext);
        mDeleteButton.setOnClickListener(this);
        mModifyButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
    }

    private void initValue()
    {
        mExpressCode.setText(mLogisticsInfo.getLogisticsCode());
        mReceiver.setText(mLogisticsInfo.getReceiver());
        mMoney.setText(mLogisticsInfo.getShippingMoney() + "");
        mSendDate.setText(mLogisticsInfo.getShipDate());
        mLogisticsStatus.setText(mLogisticsInfo.getLastLogisticsInfo());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_express:
                deleteExpress();
                break;
            case R.id.modify_express:
                modifyExpress();
                break;
            case R.id.cancel_express:
                cancelModify();
                break;
            case R.id.save_express:
                saveExpress();
                break;
            default:
                break;
        }
    }

    private void cancelModify()
    {
        changeMode(NORMAL_MODE);
        initValue();
    }

    private void saveExpress()
    {
        changeMode(NORMAL_MODE);
        mLogisticsInfo.setLogisticsCode(mSendDate.getText().toString());
        mLogisticsInfo.setReceiver(mReceiver.getText().toString());
        mLogisticsInfo.setShippingMoney(StaticParam.parseMoney(mMoney.getText().toString()));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("owner", StaticParam.mUserName);
            jsonObject.put("expressCode", mLogisticsInfo.getLogisticsCode());
            jsonObject.put("receiver", mLogisticsInfo.getReceiver());
            jsonObject.put("expressDate", mLogisticsInfo.getShipDate());
            jsonObject.put("expressMoney", mLogisticsInfo.getShippingMoney());
            jsonObject.put("syncToServer", mLogisticsInfo.getSyncToServer());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverResponse = HttpConnectHelper.post(jsonObject, StaticParam.UPDATE_ADDR);

        long id = mLogisticsDBHelper.update(mLogisticsInfo);
    }

    private void deleteExpress()
    {
        Log.d(TAG, "deleteExpress :" + mLogisticsInfo.getLogisticsCode());

        JSONObject jsonOwnerObject = new JSONObject();
        try {
            jsonOwnerObject.put("logisticsCode", mLogisticsInfo.getLogisticsCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"  syncFromServer jsonObject : " + jsonOwnerObject.toString());

        MyThread myThread = new MyThread(this, jsonOwnerObject);
        myThread.start();
    }

    private void modifyExpress()
    {
        Log.d(TAG, "modifyExpress :" + mLogisticsInfo.getLogisticsCode());
        changeMode(MODEFY_MODE);
    }

    private void changeMode(int mode)
    {
        if(mode == MODEFY_MODE) {
            mCancelButton.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.GONE);
            mModifyButton.setVisibility(View.GONE);
            mReceiver.setEnabled(true);
            mSendDate.setEnabled(true);
            mMoney.setEnabled(true);
        } else if(mode == NORMAL_MODE) {
            mCancelButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mModifyButton.setVisibility(View.VISIBLE);
            mReceiver.setEnabled(false);
            mSendDate.setEnabled(false);
            mMoney.setEnabled(false);
        }
    }

    private static class MyThread extends Thread {
        WeakReference<ExpressDetail> mThreadActivityRef;
        JSONObject mJsonOwnerObject;
        String mLogisticsCode = "";
        public MyThread(ExpressDetail activity, JSONObject jsonOwnerObject) {
            mThreadActivityRef = new WeakReference<ExpressDetail>(
                    activity);
            mJsonOwnerObject = jsonOwnerObject;
            mLogisticsCode = mJsonOwnerObject.optString("logisticsCode");
        }

        @Override
        public void run() {
            super.run();
            if (mThreadActivityRef == null)
                return;

            String serverResponse = HttpConnectHelper.post(mJsonOwnerObject, StaticParam.DELETE_ADDR);
            Log.d(TAG, "[ExpressDetail]serverResponse = " + serverResponse);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(serverResponse);
                int resultCode = jsonObject.optInt("code");
                if(resultCode == 1) {
                    mLogisticsDBHelper.delete(mLogisticsCode);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            if (mThreadActivityRef.get() != null) {
                mThreadActivityRef.get().finish();
            }
        }
    }
}
