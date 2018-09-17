package me.lxl.expresstrack.zxing;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ExpressDBHelper {

    private static String TAG = "ExpressTrack";

    public static ExpressDBHelper mInstance = null;
    private SQLiteOpenHelper helper;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "expresstrack.db";

    public static String TABLE_NAME = "express_list";
    public static String COL_ID = "_id";
    public static String EXPRESS_CODE = "express_code";
    public static String RECIPIENT = "recipient";
    public static String SEND_EXPRESS_DATE = "send_express_date";
    public static String EXPRESS_MONEY = "express_money";
    public static String PROVIDER_NAME = "me.lxl.expresstrack.zxing.AppNetAccessProvider";
    public static String URI = "content://" + PROVIDER_NAME + "/" + TABLE_NAME;


    private final String CREATE_EXPRESS_DB = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " ( " + COL_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + EXPRESS_CODE + " TEXT, "
            + RECIPIENT + " TEXT, "
            + SEND_EXPRESS_DATE + " TEXT, "
            + EXPRESS_MONEY + " FLOAT )";

    public ExpressDBHelper(Context context) {
        helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.d(TAG, "[ExpressDBHelper]onCreate");
                db.execSQL(CREATE_EXPRESS_DB);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.d(TAG, "[ExpressDBHelper]onUpgrade");
            }
        };
    }

    public static synchronized ExpressDBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ExpressDBHelper(context);
        }
        return mInstance;
    }

    public long insert(ExpressInfo expressInfo) {
        //获取数据库对象
        SQLiteDatabase db = helper.getWritableDatabase();
        //用来装载要插入的数据的map<列名，列的值>
        ContentValues values = new ContentValues();
        values.put(EXPRESS_CODE, expressInfo.getExpressCode());
        //向account表插入数据values
        long id = db.insert(TABLE_NAME, null, values);
        Log.d(TAG, "[insert] id = " + id);
        expressInfo.setId(id);
        db.close();//关闭数据库
        return id;
    }

    //根据id 删除数据
    public int delete(String expressCode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //按条件删除指定表中的数据，返回受影响的行数
        int count = db.delete(TABLE_NAME, EXPRESS_CODE + "=?", new String[]{expressCode});
        db.close();
        return count;
    }

    //更新数据
    public int update(ExpressInfo expressInfo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();//要修改的数据
        values.put(EXPRESS_CODE, expressInfo.getExpressCode());
        int count = db.update(TABLE_NAME, values, EXPRESS_CODE +"=?", new String[]{expressInfo.getExpressCode() + ""});//更新并得到行数
        db.close();
        return count;
    }

    //查询所有数据倒序排列
    public List<ExpressInfo> queryAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, "_id DESC");
        List<ExpressInfo> list = new ArrayList<ExpressInfo>();
        while (c.moveToNext()) {
            //可以根据列名获取索引
            long id = c.getLong(c.getColumnIndex("_id"));
            String expressCode = c.getString(1);
//            int balance = c.getInt(2);
            Log.d(TAG, "queryAll expressCode = " + expressCode);
            list.add(new ExpressInfo(id, expressCode));
        }
        c.close();
        db.close();
        return list;
    }
}