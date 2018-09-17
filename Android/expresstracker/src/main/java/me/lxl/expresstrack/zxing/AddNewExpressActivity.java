package me.lxl.expresstrack.zxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddNewExpressActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ExpressTrack";
    private static final int RESULT_FOR_SCANCODE = 0x001;

    private Button mScanButton;
    private Button mAddComplete;
    private EditText mExpressCodeEdit;
    private EditText mRecipientEdit;
    private EditText mSendExpressDateEdit;
    private EditText mExpressMoneyEdit;
    private ExpressDBHelper mExpressDBHelper;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.add_new_express);
        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mExpressDBHelper = ExpressDBHelper.getInstance(this);
        initView();
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView()
    {
        mScanButton = (Button)findViewById(R.id.scan_express_code_button);
        mScanButton.setOnClickListener(this);
        mAddComplete = (Button)findViewById(R.id.add_new_complete);
        mAddComplete.setOnClickListener(this);
        mExpressCodeEdit = (EditText)findViewById(R.id.express_code_edittext);
        mRecipientEdit = (EditText)findViewById(R.id.recipient_edittext);
        mSendExpressDateEdit = (EditText)findViewById(R.id.send_express_date_edittext);
        mExpressMoneyEdit = (EditText)findViewById(R.id.express_money_exittext);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_FOR_SCANCODE:
                if (data != null) {
                    String scanResult = data.getExtras().getString("scanResult");
                    Log.d(TAG, " onActivityResult scanResult = " + scanResult);
                    if (mExpressCodeEdit != null) {
                        mExpressCodeEdit.setText(scanResult);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.scan_express_code_button:
                startScalingScanner();
                break;
            case R.id.add_new_complete:
                AddExpressItem();
                break;
            default:
                break;
        }
    }

    private void AddExpressItem()
    {
        String expressCode = mExpressCodeEdit.getText().toString();
        String recipter = mRecipientEdit.getText().toString();
        String sendExpressDate = mSendExpressDateEdit.getText().toString();
        String money = mExpressMoneyEdit.getText().toString();
        Log.d(TAG, "AddExpressItem:expressCode:" + expressCode + ",recipter:"+ recipter + ",sendExpressDate:" + sendExpressDate + ",money:" + money);
        ExpressInfo expressInfo = new ExpressInfo(expressCode);

        long id = mExpressDBHelper.insert(expressInfo);
        if(id > 0) {
            finish();
        } else {
            Toast.makeText(this, R.string.insert_express_fail, Toast.LENGTH_SHORT).show();
        }
    }

    private void startScalingScanner()
    {
        Intent intent = new Intent(this, ScalingScannerActivity.class);
        startActivityForResult(intent, RESULT_FOR_SCANCODE);
    }


}