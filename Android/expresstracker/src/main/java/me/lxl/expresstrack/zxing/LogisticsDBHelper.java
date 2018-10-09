package me.lxl.expresstrack.zxing;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LogisticsDBHelper {

    private static String TAG = "ExpressTrack";

    public static LogisticsDBHelper mInstance = null;
    private SQLiteOpenHelper helper;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "expresstrack.db";

    public static String TABLE_NAME = "logistics_list";
//    public static String COL_ID = "_id";
//    //物流编号ID
//    public static String LOGISTICS_CODE = "logisticsCode";
//    public static String SHIPPER_CODE = "shipperCode";
//    //收件者
//    public static String RECEIVER = "receiver";
//    public static String CREATER = "creater";
//    //寄件时间
//    public static String SHIPDATE = "shipDate";
//    //寄件费用
//    public static String SHIPPING_MONEY = "shippingMoney";
//    //物流信息
//    public static String LOGISTICS_INFO = "logisticsInfo";
//    //最新物流信息
//    public static String LAST_LOGISTICS_INFO = "latestLogisticsInfo";
//    //物流的状态
//    public static String LOGISTICS_STATE = "logisticsState";
//    //最新的物流信息更新时间
//    public static String LOGISTICS_UPDATE_TIME = "logisticsUpdateTime";
//    //是否已经同步到服务器
//    public static String SYNC_TO_SERVER = "sync_to_server";
    public static String PROVIDER_NAME = "me.lxl.expresstrack.zxing.AppNetAccessProvider";
    public static String URI = "content://" + PROVIDER_NAME + "/" + TABLE_NAME;


    private final String CREATE_EXPRESS_DB = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " ( " + LogisticsInfo.COL_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + LogisticsInfo.LOGISTICS_CODE + " TEXT NOT NULL UNIQUE, "
            + LogisticsInfo.SHIPPER_CODE + " TEXT NOT NULL , "
            + LogisticsInfo.RECEIVER + " TEXT, "
            + LogisticsInfo.CREATER + " TEXT, "
            + LogisticsInfo.SHIPDATE + " TEXT NOT NULL, "
            + LogisticsInfo.LOGISTICS_INFO + " TEXT, "
            + LogisticsInfo.LAST_LOGISTICS_INFO + " TEXT, "
            + LogisticsInfo.LOGISTICS_STATE + " INTEGER NOT NULL DEFAULT 0, "
            + LogisticsInfo.LOGISTICS_UPDATE_TIME + " TEXT, "
            + LogisticsInfo.SYNC_TO_SERVER + " INTEGER NOT NULL DEFAULT 0,"
            + LogisticsInfo.SHIPPING_MONEY + " DOUBLE)";

    public LogisticsDBHelper(Context context) {
        helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.d(TAG, "[LogisticsDBHelper]onCreate");
                db.execSQL(CREATE_EXPRESS_DB);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.d(TAG, "[LogisticsDBHelper]onUpgrade");
            }
        };
    }

    public static synchronized LogisticsDBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LogisticsDBHelper(context);
        }
        return mInstance;
    }

    public long insert(LogisticsInfo logisticsInfo) {
        //获取数据库对象
        if(logisticsInfo == null || "".equalsIgnoreCase(logisticsInfo.getLogisticsCode())) {
            return -1;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        //用来装载要插入的数据的map<列名，列的值>
        ContentValues values = new ContentValues();
        values.put(LogisticsInfo.LOGISTICS_CODE, logisticsInfo.getLogisticsCode());
        values.put(LogisticsInfo.SHIPPER_CODE, logisticsInfo.getShipperCode());
        values.put(LogisticsInfo.RECEIVER, logisticsInfo.getReceiver());
        values.put(LogisticsInfo.CREATER, logisticsInfo.getCreater());
        values.put(LogisticsInfo.SHIPDATE, logisticsInfo.getShipDate());
        values.put(LogisticsInfo.SHIPPING_MONEY, logisticsInfo.getShippingMoney());
        values.put(LogisticsInfo.LOGISTICS_INFO, logisticsInfo.getLogisticsInfo());
        values.put(LogisticsInfo.LAST_LOGISTICS_INFO, logisticsInfo.getLastLogisticsInfo());
        values.put(LogisticsInfo.LOGISTICS_UPDATE_TIME, logisticsInfo.getLogisticsState());
        values.put(LogisticsInfo.SYNC_TO_SERVER, logisticsInfo.getSyncToServer());

        //向account表插入数据values
        long id = db.insert(TABLE_NAME, null, values);
        Log.d(TAG, "[insert] id = " + id);
        logisticsInfo.setId(id);
//        db.close();//关闭数据库
        return id;
    }

    //根据id 删除数据
    public int delete(String expressCode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //按条件删除指定表中的数据，返回受影响的行数
        int count = db.delete(TABLE_NAME, LogisticsInfo.LOGISTICS_CODE + "=?", new String[]{expressCode});
//        db.close();
        return count;
    }

    //更新数据
    public int update(LogisticsInfo logisticsInfo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();//要修改的数据
        values.put(LogisticsInfo.SHIPPER_CODE, logisticsInfo.getShipperCode());
        values.put(LogisticsInfo.RECEIVER, logisticsInfo.getReceiver());
        values.put(LogisticsInfo.CREATER, logisticsInfo.getCreater());
        values.put(LogisticsInfo.SHIPDATE, logisticsInfo.getShipDate());
        values.put(LogisticsInfo.SHIPPING_MONEY, logisticsInfo.getShippingMoney());
        values.put(LogisticsInfo.LOGISTICS_INFO, logisticsInfo.getLogisticsInfo());
        values.put(LogisticsInfo.LAST_LOGISTICS_INFO, logisticsInfo.getLastLogisticsInfo());
        values.put(LogisticsInfo.LOGISTICS_STATE, logisticsInfo.getLogisticsState());
        values.put(LogisticsInfo.LOGISTICS_UPDATE_TIME, logisticsInfo.getLogisticsUpdateTime());
        values.put(LogisticsInfo.SYNC_TO_SERVER, logisticsInfo.getSyncToServer());

        int count = db.update(TABLE_NAME, values, LogisticsInfo.LOGISTICS_CODE +"=?", new String[]{logisticsInfo.getLogisticsCode() + ""});//更新并得到行数
//        db.close();
        return count;
    }

    public int updateHaveSync(String expressCode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();//要修改的数据
        values.put(LogisticsInfo.SYNC_TO_SERVER, 1);
        int count = db.update(TABLE_NAME, values, LogisticsInfo.LOGISTICS_CODE +"=?", new String[]{expressCode + ""});//更新并得到行数
//        db.close();
        return count;
    }


    //查询所有数据倒序排列
    public List<LogisticsInfo> queryAllNeedSync() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, "sync_to_server = ?", new String[]{"0"}, null, null, "_id DESC");
        List<LogisticsInfo> list = new ArrayList<LogisticsInfo>();
        while (c.moveToNext()) {
            //可以根据列名获取索引
            long id = c.getLong(c.getColumnIndex("_id"));
            String expressCode = c.getString(1);
//            int balance = c.getInt(2);
            Log.d(TAG, "queryAllNeedSync expressCode = " + expressCode);
            list.add(new LogisticsInfo(c));
        }
        c.close();
//        db.close();
        return list;
    }

    //查询所有数据倒序排列
    public List<LogisticsInfo> queryAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, "_id DESC");
        List<LogisticsInfo> list = new ArrayList<LogisticsInfo>();
        while (c.moveToNext()) {
            //可以根据列名获取索引
            long id = c.getLong(c.getColumnIndex("_id"));
            String expressCode = c.getString(1);
//            int balance = c.getInt(2);
            Log.d(TAG, "queryAll expressCode = " + expressCode);
            list.add(new LogisticsInfo(c));
        }
        c.close();
//        db.close();
        return list;
    }
}