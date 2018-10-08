package me.lxl.expresstrack.zxing;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddNewExpressActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ExpressTrack";
    private static final int RESULT_FOR_SCANCODE = 0x001;

    private Button mScanButton;
    private Button mAddComplete;
    private Button mSelectDate;
    private EditText mExpressCodeEdit;
    private EditText mRecipientEdit;
    private EditText mSendExpressDateEdit;
    private EditText mExpressMoneyEdit;
    private ExpressDBHelper mExpressDBHelper;

    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;


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

    private void initView() {
        String today = getCurrentDate();

        mScanButton = (Button) findViewById(R.id.scan_express_code_button);
        mScanButton.setOnClickListener(this);
        mAddComplete = (Button) findViewById(R.id.add_new_complete);
        mAddComplete.setOnClickListener(this);
        mExpressCodeEdit = (EditText) findViewById(R.id.express_code_edittext);
        mRecipientEdit = (EditText) findViewById(R.id.recipient_edittext);
        mSendExpressDateEdit = (EditText) findViewById(R.id.send_express_date_edittext);
        mExpressMoneyEdit = (EditText) findViewById(R.id.express_money_exittext);

        mSendExpressDateEdit.setText(today);
        mSelectDate = (Button) findViewById(R.id.send_express_datepicker);
        mSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(AddNewExpressActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.d(TAG, String.format("%d-%d-%d", i, i1 + 1, i2));
                        mSendExpressDateEdit.setText(String.format("%d-%d-%d", i, i1 + 1, i2));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        添加快递公司列表
        spinner = (Spinner) findViewById(R.id.spinner);

        //数据
        data_list = new ArrayList<String>();
        data_list.add("顺丰");
        data_list.add("EMS");
        data_list.add("圆通");
        data_list.add("申通");
        data_list.add("韵达");

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

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
        switch (v.getId()) {
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

    private void AddExpressItem() {

        String expressCode = mExpressCodeEdit.getText().toString();
        Log.d(TAG, "AddExpressItem expressCode =" + expressCode + ",");
        if (expressCode == null || expressCode.equalsIgnoreCase("")) {
            showErrorDialog(getString(R.string.express_code_error_msg));
            return;
        }


        String receiver = mRecipientEdit.getText().toString();
        Log.d(TAG, "AddExpressItem receiver =" + receiver + ",");
        if (receiver == null || receiver.equalsIgnoreCase("")) {
            showErrorDialog(getString(R.string.add_receiver_error_msg));
            return;
        }

        String sendExpressDate = mSendExpressDateEdit.getText().toString();
        float money = parseMoney(mExpressMoneyEdit.getText().toString());
        Log.d(TAG, "AddExpressItem:expressCode:" + expressCode + ",receiver:" + receiver + ",sendExpressDate:" + sendExpressDate + ",money:" + money);

        ExpressInfo expressInfo = new ExpressInfo(expressCode, receiver, sendExpressDate, money);

        long id = mExpressDBHelper.insert(expressInfo);
        if (id > 0) {
            //启动同步机制
            Intent i = new Intent(this, SyncService.class);
            startService(i);

            finish();
        } else {
            showErrorDialog(getString(R.string.insert_express_fail));
        }
    }

    private void showErrorDialog(String msg) {
        Log.d(TAG, "showErrorDialog msg: " + msg);

        new AlertDialog.Builder(this).setTitle(getString(R.string.add_express_error_title)).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                ).show();
    }

    private float parseMoney(String moneyStr) {
        float money = 0.0f;
        try {
            money = Float.parseFloat(moneyStr);
        } catch (Exception e) {

        }
        return money;
    }

    private String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String dateString = sdf.format(date);
        return dateString;
    }

    private void startScalingScanner() {
        Intent intent = new Intent(this, ScalingScannerActivity.class);
        startActivityForResult(intent, RESULT_FOR_SCANCODE);
    }


}