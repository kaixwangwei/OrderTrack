package me.lxl.expresstrack.zxing;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ZXING_CAMERA_PERMISSION = 1;
    private static final int LOGIN_SUCCESS = 0;
    private static final int LOGIN_FAIL = 1;
    private static final String TAG = "ExpressTrack";

    private Class<?> mClss;

    private Button mLogin;
    private EditText mUSerName;
    private EditText mPassWord;
    private CheckBox mCheckBox;
    private boolean mSaveUserName = false;
    private SharedPreferences mSharedPreferences;
    private MyHandler mHandler = null;
    private static ProgressDialog mPdialog;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.login_activity);
        mHandler = new MyHandler(this);
        setupToolbar();
        mSharedPreferences = getPreferences(AppCompatActivity.MODE_PRIVATE);
        initView();
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView() {
        mLogin = (Button) findViewById(R.id.mButtonLogin);
        mLogin.setOnClickListener(this);
        mCheckBox = (CheckBox) findViewById(R.id.mCheckBox);
        mCheckBox.setOnClickListener(this);
        mUSerName = (EditText) findViewById(R.id.mUserName);
        mPassWord = (EditText) findViewById(R.id.mPassWord);
        String userName = mSharedPreferences.getString("username", "");
        String passWord = mSharedPreferences.getString("password", "");
        mUSerName.setText(userName);
        mPassWord.setText(passWord);
        mCheckBox.setChecked(mSharedPreferences.getBoolean("savepassword", false));
        if(passWord != "" && userName != "") {
            showDiag();
            LoginThread login = new LoginThread(this, mHandler, userName, passWord);
            login.start();
        }
    }

    public void startMainActivity(View v)
    {
        StaticParam.offlineMode = true;
        launchActivity(MainActivity.class);
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mButtonLogin:
                loginFunc();
                break;
            case R.id.mCheckBox:
                mSaveUserName = mCheckBox.isChecked();
                break;
            default:
                break;
        }
    }

    private void showDiag() {
        mPdialog = new ProgressDialog(this, 0);
        mPdialog.setButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        mPdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });
        // 设置对话框是否可以取消
        mPdialog.setTitle(R.string.login_title);
        mPdialog.setMessage(getString(R.string.login_message));
        mPdialog.setCancelable(true);
        mPdialog.setMax(100);
        mPdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPdialog.show();
    }

    private void loginFunc() {
        String password = mPassWord.getText().toString();
        String username = mUSerName.getText().toString();
        if (password == null || username == null
                || password.length() < 3 || password.length() >= 16
                || username.length() < 3 || username.length() >= 16) {
            Toast.makeText(this, getString(R.string.user_pass_input_fail), Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if(mCheckBox.isChecked()) {
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putBoolean("savepassword", true);

        } else {
            editor.remove("username");
            editor.remove("password");
            editor.putBoolean("savepassword", false);
        }
        editor.commit();
        showDiag();
        LoginThread login = new LoginThread(this, mHandler, password, username);
        login.start();
    }

    private class MyHandler extends Handler {

        //对Activity的弱引用
        private final Context mActivity;

        public MyHandler(Context activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    Toast.makeText(mActivity, R.string.login_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    mActivity.startActivity(intent);

                    Intent i = new Intent(mActivity, SyncService.class);
                    i.setAction(StaticParam.SYNC_ACTION);
                    startService(i);

                    mPdialog.dismiss();
                    LoginActivity.this.finish();
                    break;
                case LOGIN_FAIL:
                    if (mPdialog != null && mPdialog.isShowing()) {
                        Toast.makeText(mActivity, R.string.login_fail, Toast.LENGTH_SHORT).show();
                        mPdialog.dismiss();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


    private static class LoginThread extends Thread {
        SoftReference<AppCompatActivity> context;
        private Context mContext;
        private String mUserName;
        private String mPassWord;
        private String urlPath = StaticParam.LOGIN_ADDR;
        private Handler mHandler;

        LoginThread(AppCompatActivity activity, Handler handler, String username, String password) {
            context = new SoftReference<>(activity);
            mContext = activity;
            mUserName = username;
            mPassWord = password;
            mHandler = handler;
        }

        @Override
        public void run() {

            URL url = null;
            try {
                url = new URL(urlPath);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                JSONObject payLoadJson = new JSONObject();
                payLoadJson.put("username", mUserName);//对其添加一个数据
                payLoadJson.put("password", mPassWord);//对其添加一个数据

                String payLoad = payLoadJson.toString();

                Log.d(TAG, "String.valueOf(payLoadJson) = " + payLoad);

                //设置连接超时,2000ms
                httpURLConnection.setConnectTimeout(2000);
                //设置指定时间内服务器没有返回数据的超时，5000ms
                httpURLConnection.setReadTimeout(5000);
                //设置允许输出
                httpURLConnection.setDoOutput(true);

                httpURLConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
                httpURLConnection.setRequestMethod("POST");

                OutputStream out = new
                        BufferedOutputStream(httpURLConnection.getOutputStream());

                byte[] body = payLoadJson.toString().getBytes("utf-8");
                out.write(body, 0, body.length);

                out.flush();//立即刷新

                out.close();

                int code = httpURLConnection.getResponseCode();
                Log.d(TAG, "code = " + code);
                if (code == 200) {
                    InputStream is = httpURLConnection.getInputStream();

                    //连接服务器后，服务器做出响应返回的数据
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                    String serverResponse = "";
                    String line = "";
                    while ((line = bufReader.readLine()) != null) {
                        serverResponse += line;
                    }
                    is.close();

                    Log.d(TAG, "serverResponse = " + serverResponse + " , " + serverResponse.equalsIgnoreCase("success"));

                    //对返回的数据serverResponse进行操作
                    if (serverResponse.equalsIgnoreCase("success")) {
                        //启动mainactivity
                        Log.d(TAG, "0002 serverResponse = " + serverResponse);
                        StaticParam.mUserName = mUserName;
                        StaticParam.mPassword = mPassWord;
                        mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                        return;
                    }
                }
                httpURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(LOGIN_FAIL);
            }
            mHandler.sendEmptyMessage(LOGIN_FAIL);
        }
    }
}