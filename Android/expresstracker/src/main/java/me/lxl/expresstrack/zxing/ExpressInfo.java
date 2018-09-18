package me.lxl.expresstrack.zxing;


import android.database.Cursor;


public class ExpressInfo {
    
    private  Long id;
    private  String mExpressCode;
    private String mReceiver;
    private String mExpressDate;
    private float mExpressMoney = 0.0f;
    private String mLogisticsInfo = "";
    private int mSyncToServer;

    public static String COL_ID = "_id";
    //物流编号ID
    public static String EXPRESS_CODE = "express_code";
    //收件者
    public static String RECEIVER = "receiver";
    //寄件时间
    public static String SEND_EXPRESS_DATE = "send_express_date";
    //寄件费用
    public static String EXPRESS_MONEY = "express_money";
    //快递状态
    public static String LOGISTICS_INFO = "logistics_info";
    //是否已经同步到服务器
    public static String SYNC_TO_SERVER = "sync_to_server";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpressCode() {
        return mExpressCode;
    }

    public void setExpressCode(String mExpressCode) {
        this.mExpressCode = mExpressCode;
    }

    public String getReceiver() {
        return mReceiver;
    }

    public String getExpressDate() {
        return mExpressDate;
    }

    public float getExpressMoney() {
        return mExpressMoney;
    }

    public String getLogisticsInfo() {
        return mLogisticsInfo;
    }

    public String getLastLogisticsInfo() {
        return mLogisticsInfo;
    }

    public int getSyncToServer() {
        return mSyncToServer;
    }

    //物流编号ID
    public ExpressInfo(Long id,String expressCode){
        super();
        id=id;
        mExpressCode = expressCode;
    }

    public ExpressInfo(String expressCodem, String receiver, String expressDate, float expressMoney){
        super();
        mExpressCode = expressCodem;
        mReceiver = receiver;
        mExpressDate = expressDate;
        mExpressMoney = expressMoney;
    }

    public ExpressInfo(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(COL_ID));
        mExpressCode = cursor.getString(cursor.getColumnIndex(EXPRESS_CODE));
        mReceiver = cursor.getString(cursor.getColumnIndex(RECEIVER));
        mExpressDate = cursor.getString(cursor.getColumnIndex(SEND_EXPRESS_DATE));
        mExpressMoney = cursor.getFloat(cursor.getColumnIndex(EXPRESS_MONEY));
        mLogisticsInfo = cursor.getString(cursor.getColumnIndex(LOGISTICS_INFO));
        mSyncToServer = cursor.getInt(cursor.getColumnIndex(SYNC_TO_SERVER));
    }

    public ExpressInfo(){
        super();
    }

    @Override
    public String toString() {
        return "快递单号："+mExpressCode + ", 收货人：" + mReceiver + ", 寄件时间：" + mExpressDate;
    }
}