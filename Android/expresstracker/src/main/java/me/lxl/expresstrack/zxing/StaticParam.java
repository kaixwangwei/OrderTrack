package me.lxl.expresstrack.zxing;

public class StaticParam
{
    public static boolean offlineMode = false;
    public static String mUserName = "";
    public static String mPassword = "";
    public static final String TAG = "ExpressTrack";
    public static final String SYNC_ACTION = "me.lxl.expresstrack.zxing.SyncService.startsync";

    public static final String SERVER_IP = "132.232.23.28";
    public static final String SERVER_PORT = "9000";
    public static final String CLIENT_TO_SERVER_ADDR = "http://" + SERVER_IP + ":" + SERVER_PORT + "/client/insertrecord";
    public static final String SYNC_FROM_SERVER_ADDR = "http://" + SERVER_IP + ":" + SERVER_PORT + "/client/getdata";
    public static final String LOGIN_ADDR = "http://" + SERVER_IP + ":" + SERVER_PORT + "/client/login";
    public static final String DELETE_ADDR = "http://" + SERVER_IP + ":" + SERVER_PORT + "/client/delete";
    public static final String UPDATE_ADDR = "http://" + SERVER_IP + ":" + SERVER_PORT + "/client/update";


    public static float parseMoney(String moneyStr) {
        float money = 0.0f;
        try {
            money = Float.parseFloat(moneyStr);
        } catch (Exception e) {

        }
        return money;
    }

}