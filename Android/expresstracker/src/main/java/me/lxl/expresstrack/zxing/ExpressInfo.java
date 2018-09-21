package me.lxl.expresstrack.zxing;


import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


public class ExpressInfo implements Parcelable {
    
    private  Long id;
    private  String mExpressCode;
    private String mReceiver;
    private String mExpressDate;
    private double mExpressMoney = 0.0f;
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

    public static final Creator<ExpressInfo> CREATOR = new Creator<ExpressInfo>() {
        @Override
        public ExpressInfo createFromParcel(Parcel in) {
            return new ExpressInfo(in);
        }

        @Override
        public ExpressInfo[] newArray(int size) {
            return new ExpressInfo[size];
        }
    };

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

    public void setReceiver(String receiver) {
        mReceiver = receiver;
    }

    public String getExpressDate() {
        return mExpressDate;
    }

    public void setExpressDate(String expressDate) {
        mExpressDate = expressDate;
    }

    public double getExpressMoney() {
        return mExpressMoney;
    }

    public void setExpressMoney(double money){
        mExpressMoney = money;
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

    public ExpressInfo(String expressCode, String receiver, String expressDate, float expressMoney){
        super();
        mExpressCode = expressCode;
        mReceiver = receiver;
        mExpressDate = expressDate;
        mExpressMoney = expressMoney;
    }

    public ExpressInfo(String expressCode, String receiver, String expressDate, String logisticsInfo, double expressMoney){
        super();
        mExpressCode = expressCode;
        mReceiver = receiver;
        mExpressDate = expressDate;
        mExpressMoney = expressMoney;
        mLogisticsInfo = logisticsInfo;
        mSyncToServer = 1;
    }

    public ExpressInfo(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(COL_ID));
        mExpressCode = cursor.getString(cursor.getColumnIndex(EXPRESS_CODE));
        mReceiver = cursor.getString(cursor.getColumnIndex(RECEIVER));
        mExpressDate = cursor.getString(cursor.getColumnIndex(SEND_EXPRESS_DATE));
        mExpressMoney = cursor.getDouble(cursor.getColumnIndex(EXPRESS_MONEY));
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

    @Override
    public int describeContents() {
        return 0;
    }

    protected ExpressInfo(Parcel in) {
        id = in.readLong();
        mExpressCode = in.readString();
        mReceiver = in.readString();
        mExpressDate = in.readString();
        mExpressMoney = in.readDouble();
        mLogisticsInfo = in.readString();
        mSyncToServer = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(mExpressCode);
        parcel.writeString(mReceiver);
        parcel.writeString(mExpressDate);
        parcel.writeDouble(mExpressMoney);
        parcel.writeString(mLogisticsInfo);
        parcel.writeInt(mSyncToServer);
    }
}