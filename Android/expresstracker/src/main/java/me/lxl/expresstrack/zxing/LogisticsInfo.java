package me.lxl.expresstrack.zxing;


import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


public class LogisticsInfo implements Parcelable {
    
    private Long id;
    private String mLogisticsCode;
    private String mShipperCode;
    private String mReceiver;
    private String mCreater;
    private String mShipDate;
    private double mShippingMoney = 0.0f;
    private String mLogisticsInfo = "";
    private String mLastLogisticsInfo = "";
    private int mLogisticsState = 0;
    private String mLogisticsUpdateTime = "";
    private int mSyncToServer;

    public static String COL_ID = "_id";
    //物流编号ID
    public static String LOGISTICS_CODE = "logisticsCode";
    //物流公司编码
    public static String SHIPPER_CODE = "shipperCode";
    //收件者
    public static String RECEIVER = "receiver";
    //创建者
    public static String CREATER = "creater";
    //寄件时间
    public static String SHIPDATE = "shipDate";
    //寄件费用
    public static String SHIPPING_MONEY = "shippingMoney";
    //物流信息
    public static String LOGISTICS_INFO = "logisticsInfo";
    //最新物流信息
    public static String LAST_LOGISTICS_INFO = "latestLogisticsInfo";
    //物流的状态
    public static String LOGISTICS_STATE = "logisticsState";
    //最新的物流信息更新时间
    public static String LOGISTICS_UPDATE_TIME = "logisticsUpdateTime";
    //是否已经同步到服务器
    public static String SYNC_TO_SERVER = "sync_to_server";

    public static final Creator<LogisticsInfo> CREATOR = new Creator<LogisticsInfo>() {
        @Override
        public LogisticsInfo createFromParcel(Parcel in) {
            return new LogisticsInfo(in);
        }

        @Override
        public LogisticsInfo[] newArray(int size) {
            return new LogisticsInfo[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //LOGISTICS_CODE
    public String getLogisticsCode() {
        return mLogisticsCode;
    }

    public void setLogisticsCode(String mLogisticsCode) {
        this.mLogisticsCode = mLogisticsCode;
    }

    //SHIPPER_CODE
    public String getShipperCode() {
        return mShipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.mShipperCode = shipperCode;
    }

    //RECEIVER
    public String getReceiver() {
        return mReceiver;
    }

    public void setReceiver(String receiver) {
        mReceiver = receiver;
    }

    //CREATER
    public String getCreater() {
        return mCreater;
    }

    public void setCreater(String creater) {
        mCreater = creater;
    }

    //SHIPDATE
    public String getShipDate() {
        return mShipDate;
    }

    public void setShipDate(String shipDate) {
        mShipDate = shipDate;
    }

    //SHIPPING_MONEY
    public double getShippingMoney() {
        return mShippingMoney;
    }

    public void setShippingMoney(double money){
        mShippingMoney = money;
    }

    //LOGISTICS_INFO
    public String getLogisticsInfo() {
        return mLogisticsInfo;
    }

    public void setLogisticsInfo(String logisticsInfo) {
        mLogisticsInfo = logisticsInfo;
    }

    //LAST_LOGISTICS_INFO
    public String getLastLogisticsInfo() {
        return mLastLogisticsInfo;
    }

    public void setLastLogisticsInfo(String lastLogisticsInfo) {
        mLastLogisticsInfo = lastLogisticsInfo;
    }

    //LOGISTICS_STATE
    public int getLogisticsState() {
        return mLogisticsState;
    }

    public void setLogisticsState(int state) {
        mLogisticsState = state;
    }

    //LOGISTICS_UPDATE_TIME
    public String getLogisticsUpdateTime() {
        return mLogisticsUpdateTime;
    }

    public void setLogisticsUpdateTime(String logisticsUpdateTime) {
        mLogisticsUpdateTime = logisticsUpdateTime;
    }

    public int getSyncToServer() {
        return mSyncToServer;
    }

    //物流编号ID
    public LogisticsInfo(Long id,String logisticsCode){
        super();
        id=id;
        mLogisticsCode = logisticsCode;
    }

    //logisticsCode, shipperCode, receiver, creater, shipDate, shippingMoney
    public LogisticsInfo(String logisticsCode, String shipperCode, String receiver, String creater, String shipDate, float shippingMoney){
        super();
        mLogisticsCode = logisticsCode;
        mShipperCode = shipperCode;
        mReceiver = receiver;
        mCreater = creater;
        mShipDate = shipDate;
        mShippingMoney = shippingMoney;
    }

    //logisticsCode, shipperCode, receiver, creater, shippingMoney, shipDate, logisticsInfoStr, logisticsState
    public LogisticsInfo(String logisticsCode, String shipperCode, String receiver, String creater, double shippingMoney, String shipDate, String logisticsInfoStr, String latestLogisticsInfo, int logisticsState){
        super();
        mLogisticsCode = logisticsCode;
        mShipperCode = shipperCode;
        mReceiver = receiver;
        mCreater = creater;
        mShipDate = shipDate;
        mShippingMoney = shippingMoney;
        mLogisticsInfo = logisticsInfoStr;
        mLastLogisticsInfo = latestLogisticsInfo;
        mLogisticsState = logisticsState;
        mSyncToServer = 1;
    }

    public LogisticsInfo(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(COL_ID));
        mLogisticsCode = cursor.getString(cursor.getColumnIndex(LOGISTICS_CODE));
        mShipperCode = cursor.getString(cursor.getColumnIndex(SHIPPER_CODE));
        mReceiver = cursor.getString(cursor.getColumnIndex(RECEIVER));
        mCreater = cursor.getString(cursor.getColumnIndex(CREATER));
        mShipDate = cursor.getString(cursor.getColumnIndex(SHIPDATE));
        mShippingMoney = cursor.getDouble(cursor.getColumnIndex(SHIPPING_MONEY));
        mLogisticsInfo = cursor.getString(cursor.getColumnIndex(LOGISTICS_INFO));
        mLastLogisticsInfo = cursor.getString(cursor.getColumnIndex(LAST_LOGISTICS_INFO));
        mLogisticsState = cursor.getInt(cursor.getColumnIndex(LOGISTICS_STATE));
        mLogisticsUpdateTime = cursor.getString(cursor.getColumnIndex(LOGISTICS_UPDATE_TIME));
        mSyncToServer = cursor.getInt(cursor.getColumnIndex(SYNC_TO_SERVER));
    }

    public LogisticsInfo(){
        super();
    }

    @Override
    public String toString() {
        return "快递单号："+mLogisticsCode + ", 收货人：" + mReceiver + ", 寄件时间：" + mShipDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected LogisticsInfo(Parcel in) {
        id = in.readLong();
        mLogisticsCode = in.readString();
        mShipperCode = in.readString();
        mReceiver = in.readString();
        mCreater = in.readString();
        mShipDate = in.readString();
        mShippingMoney = in.readDouble();
        mLogisticsInfo = in.readString();
        mLastLogisticsInfo = in.readString();
        mLogisticsState = in.readInt();
        mLogisticsUpdateTime = in.readString();
        mSyncToServer = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(mLogisticsCode);
        parcel.writeString(mShipperCode);
        parcel.writeString(mReceiver);
        parcel.writeString(mCreater);
        parcel.writeString(mShipDate);
        parcel.writeDouble(mShippingMoney);
        parcel.writeString(mLogisticsInfo);
        parcel.writeString(mLastLogisticsInfo);
        parcel.writeInt(mLogisticsState);
        parcel.writeString(mLogisticsUpdateTime);
        parcel.writeInt(mSyncToServer);
    }
}