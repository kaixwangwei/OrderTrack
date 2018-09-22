package me.lxl.expresstrack.zxing;

public class StaticParam
{
    public static boolean offlineMode = false;
    public static String mUserName = "";
    public static String mPassword = "";
    public static final String TAG = "ExpressTrack";
    public static final String SYNC_ACTION = "me.lxl.expresstrack.zxing.SyncService.startsync";
    public static final String CLIENT_TO_SERVER_ADDR = "http://192.168.100.112:9000/client/insertrecord";
    public static final String SYNC_FROM_SERVER_ADDR = "http://192.168.100.112:9000/client/getdata";
    public static final String LOGIN_ADDR = "http://192.168.100.112:9000/client/login";
    public static final String DELETE_ADDR = "http://192.168.100.112:9000/client/delete";
    public static final String UPDATE_ADDR = "http://192.168.100.112:9000/client/update";


    public static float parseMoney(String moneyStr) {
        float money = 0.0f;
        try {
            money = Float.parseFloat(moneyStr);
        } catch (Exception e) {

        }
        return money;
    }

}