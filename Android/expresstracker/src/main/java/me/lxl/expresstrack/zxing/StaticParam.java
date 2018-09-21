package me.lxl.expresstrack.zxing;

public class StaticParam
{
    public static String mUserName = "";
    public static String mPassword = "";
    public static final String TAG = "ExpressTrack";
    public static final String SYNC_ACTION = "me.lxl.expresstrack.zxing.SyncService.startsync";
    public static final String CLIENT_TO_SERVER_ADDR = "http://192.168.100.112:9000/client/insertrecord";
    public static final String SYNC_FROM_SERVER_ADDR = "http://192.168.100.112:9000/client/getdata";
    public static final String LOGIN_ADDR = "http://192.168.100.112:9000/client/login";
}