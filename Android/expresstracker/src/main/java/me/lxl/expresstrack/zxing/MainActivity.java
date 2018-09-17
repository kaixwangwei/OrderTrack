package me.lxl.expresstrack.zxing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "ExpressTrack";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    private ExpressDBHelper mExpressDBHelper;
    private List<ExpressInfo> list;

    private ListView mExpressListView;
    //适配器
    private MyAdapter adapter;


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
        adapter.notifyDataSetChanged();
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initListView()
    {

        mExpressListView = (ListView)findViewById(R.id.list_view);
        //添加监听器，监听条目点击事件
        mExpressListView.setOnItemClickListener(new MyOnItemClickListener());

        mExpressDBHelper = ExpressDBHelper.getInstance(this);
        //从数据库查询出所有数据
        list = mExpressDBHelper.queryAll();
        adapter = new MyAdapter();
        mExpressListView.setAdapter(adapter);//给ListView添加适配器(自动把数据生成条目)
        Log.d(TAG, "MainActivity initListView");
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

            //根据当前位置获取 ExpressInfo 对象
            final ExpressInfo expressInfo = list.get(position);

            //把Account对象中的数据放到TextView中
            expressCode.setText(expressInfo.getExpressCode() + "");

            return item;
        }
    }

    //ListView的Item点击事件
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ExpressInfo a = (ExpressInfo) parent.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(), a.toString(), Toast.LENGTH_SHORT).show();
        }
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