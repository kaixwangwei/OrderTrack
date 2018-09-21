package me.lxl.expresstrack.zxing;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.lxl.expresstrack.zxing.widget.XListView;

public class MainActivity extends AppCompatActivity implements XListView.IXListViewListener{

    private static String TAG = "ExpressTrack";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    private ExpressDBHelper mExpressDBHelper;
    private List<ExpressInfo> list;
    private Handler mHandler;

    private XListView mExpressListView;
    //适配器
    private MyAdapter mAdapter;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        setupToolbar();
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list = mExpressDBHelper.queryAll();
        mAdapter.notifyDataSetChanged();
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initListView()
    {
        mHandler = new Handler();
        mExpressListView = (XListView)findViewById(R.id.list_view);
        mExpressListView.setPullRefreshEnable(true);
        mExpressListView.setPullLoadEnable(true);
        mExpressListView.setAutoLoadEnable(true);
        mExpressListView.setXListViewListener(this);
        mExpressListView.setRefreshTime(getTime());
        //添加监听器，监听条目点击事件
        mExpressListView.setOnItemClickListener(new MyOnItemClickListener());

        mExpressDBHelper = ExpressDBHelper.getInstance(this);
        //从数据库查询出所有数据
        list = mExpressDBHelper.queryAll();
        mAdapter = new MyAdapter();
        mExpressListView.setAdapter(mAdapter);//给ListView添加适配器(自动把数据生成条目)
        Log.d(TAG, "MainActivity initListView");
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        syncExpressList();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                list = mExpressDBHelper.queryAll();
                mAdapter.notifyDataSetChanged();

                mExpressListView.setAdapter(mAdapter);
                onLoad();
            }
        }, 2500);
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                geneItems();
                mAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2500);
    }

    private void onLoad() {
        mExpressListView.stopRefresh();
        mExpressListView.stopLoadMore();
        mExpressListView.setRefreshTime(getTime());
    }

//    用于分批次加载
//    private void geneItems() {
//        for (int i = 0; i != 20; ++i) {
//            items.add("Test XListView item " + (++mIndex));
//        }
//    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    //自定义一个适配器（把适配器装到ListView的工具)
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {//获取条目总数
            return list.size();
        }

        @Override
        public Object getItem(int position) {//根据位置获取对象
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {//根据位置获取ID
            return position;
        }

        @Override
        //获取一个条目视图
        public View getView(int position, View convertView, ViewGroup parent) {
            //重用convertView
            View item = convertView!=null?convertView : View.inflate(getApplicationContext(), R.layout.express_item, null);
            //获取视图中的TextView
            TextView expressCode = (TextView) item.findViewById(R.id.express_code);
            TextView receiver = (TextView) item.findViewById(R.id.receiver);
            TextView mailingTime = (TextView) item.findViewById(R.id.mailingtime);
            TextView currentState = (TextView) item.findViewById(R.id.current_state);
            TextView lastLogisticStatus = (TextView) item.findViewById(R.id.last_logistics_state);

            //根据当前位置获取 ExpressInfo 对象
            final ExpressInfo expressInfo = list.get(position);

            //把Account对象中的数据放到TextView中
            expressCode.setText(expressInfo.getExpressCode() + "");
            receiver.setText(expressInfo.getReceiver());
            mailingTime.setText(expressInfo.getExpressDate());
            int syncResId;
            if(expressInfo.getSyncToServer() == 1){
                syncResId = R.string.have_sync;
            } else {
                syncResId = R.string.no_sync;
            }
            currentState.setText(getString(syncResId));
            lastLogisticStatus.setText(expressInfo.getLastLogisticsInfo());


            return item;
        }
    }

    //ListView的Item点击事件
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ExpressInfo expressInfo = (ExpressInfo) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, ExpressDetail.class);
            intent.putExtra("ExpressInfo",expressInfo);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), expressInfo.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshExpressList(View v) {
        list = mExpressDBHelper.queryAll();
        mAdapter.notifyDataSetChanged();

//        NotificationManager mNotificationManager;
//        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        String POPULATE_NORMAL_NOTIFICATION_CHANNEL = "populate_normal_notification_channel";
//        Notification.Builder mBuilders = null;
//        if (mBuilders == null) {
//            mBuilders = new Notification.Builder(this);
//            mBuilders.setContentTitle(getString(R.string.add_express_error_title));
//            mBuilders.setAutoCancel(false);
//            mBuilders.setOngoing(true);
//            mBuilders.setShowWhen(false);
//        }
//
//        Notification xx = mBuilders.build();
//        if(mNotificationManager != null) {
//            mNotificationManager.notify(3, xx);
//        }
//        xx.getLargeIcon()
//        mNotificationManager.
    }

    public void syncExpressList() {
        Intent i = new Intent(this, SyncService.class);
        i.setAction(StaticParam.SYNC_ACTION);
        startService(i);
    }

    public void launchAddNewExpress(View v) {
        launchActivity(AddNewExpressActivity.class);
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
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}