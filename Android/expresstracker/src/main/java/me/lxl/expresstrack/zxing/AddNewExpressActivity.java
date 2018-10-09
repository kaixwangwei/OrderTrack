package me.lxl.expresstrack.zxing;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private LogisticsDBHelper mLogisticsDBHelper;

    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.add_new_express);
        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mLogisticsDBHelper = LogisticsDBHelper.getInstance(this);
        mSharedPreferences = getPreferences(AppCompatActivity.MODE_PRIVATE);

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
        data_list.add("顺丰速运");
        data_list.add("百世快递");
        data_list.add("中通快递");
        data_list.add("申通快递");
        data_list.add("圆通速递");
        data_list.add("韵达速递");
        data_list.add("邮政快递包裹");
        data_list.add("EMS");
        data_list.add("天天快递");
        data_list.add("京东快递");
        data_list.add("优速快递");
        data_list.add("德邦快递");
        data_list.add("宅急送");
        data_list.add("TNT快递");
        data_list.add("UPS");
        data_list.add("DHL");
        data_list.add("FEDEX联邦(国内件）");
        data_list.add("FEDEX联邦(国际件）");

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

        String shipperStr = mSharedPreferences.getString("shipperStr", "");
        if(shipperStr != null && shipperStr != "") {
            int position = arr_adapter.getPosition(shipperStr);
            spinner.setSelection(position);
        }
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

    private String getShipperCode(String shipperStr)
    {
        String shipperCode = "ERR";
        if("顺丰速运".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "SF";
        } else if("百世快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "HTKY";
        }else if("中通快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "ZTO";
        }else if("申通快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "STO";
        }else if("圆通速递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "YTO";
        }else if("韵达速递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "YD";
        }else if("邮政快递包裹".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "YZPY";
        }else if("EMS".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "EMS";
        }else if("天天快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "HHTT";
        }else if("京东快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "JD";
        }else if("优速快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "UC";
        }else if("德邦快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "DBL";
        }else if("宅急送".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "ZJS";
        }else if("TNT快递".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "TNT";
        }else if("UPS".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "UPS";
        }else if("DHL".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "DHL";
        }else if("FEDEX联邦(国内件）".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "FEDEX";
        }else if("FEDEX联邦(国际件）".compareToIgnoreCase(shipperStr) == 0) {
            shipperCode = "FEDEX_GJ";
        }
        Log.d(TAG, "getShipperCode shipperStr = " + shipperStr + ", shipperCode = " + shipperCode);
        return shipperCode;
    }

    private void AddExpressItem() {

        String logisticsCode = mExpressCodeEdit.getText().toString();
        Log.d(TAG, "AddExpressItem logisticsCode =" + logisticsCode + ",");
        if (logisticsCode == null || logisticsCode.equalsIgnoreCase("")) {
            showErrorDialog(getString(R.string.express_code_error_msg));
            return;
        }

        String shipperStr = spinner.getSelectedItem().toString();
        String shipperCode = getShipperCode(shipperStr);


        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("shipperStr", shipperStr);
        editor.commit();

        String receiver = mRecipientEdit.getText().toString();
        Log.d(TAG, "AddExpressItem receiver =" + receiver + ",");
        if (receiver == null || receiver.equalsIgnoreCase("")) {
            showErrorDialog(getString(R.string.add_receiver_error_msg));
            return;
        }
        String creater = StaticParam.mUserName;
        String shipDate = mSendExpressDateEdit.getText().toString();
        float shippingMoney = parseMoney(mExpressMoneyEdit.getText().toString());
        Log.d(TAG, "AddExpressItem:logisticsCode:" + logisticsCode + ",receiver:" + receiver + ",shipDate:" + shipDate + ",shippingMoney:" + shippingMoney);

        LogisticsInfo logisticsInfo = new LogisticsInfo(logisticsCode, shipperCode, receiver, creater, shipDate, shippingMoney);

        long id = mLogisticsDBHelper.insert(logisticsInfo);
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
        float shippingMoney = 0.0f;
        try {
            shippingMoney = Float.parseFloat(moneyStr);
        } catch (Exception e) {

        }
        return shippingMoney;
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