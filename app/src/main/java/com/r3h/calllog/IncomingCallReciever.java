package com.r3h.calllog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by sarvjeet on 16/8/16.
 */
public class IncomingCallReciever extends BroadcastReceiver {

    private Context mContext;
    private Intent mIntent;
    private String lastCallState="UND";

    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            String callState = "UNKNOWN";
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                   // Toast.makeText(mContext,"idle",Toast.LENGTH_SHORT).show();
                    callState = "IDLE";
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // -- check international call or not.
                  /*  if (incomingNumber.startsWith("00")) {
                        Toast.makeText(mContext,"International Call- " + incomingNumber,Toast.LENGTH_SHORT).show();
                        callState = "International - Ringing (" + incomingNumber+ ")";
                    } else {
                        Toast.makeText(mContext, "Local Call - " + incomingNumber, Toast.LENGTH_SHORT).show();
                        callState = "Local - Ringing (" + incomingNumber + ")";
                    }*/
                    callState ="BUSY";
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    callState ="BUSY";
                    /* String dialingNumber = mIntent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    Toast.makeText(mContext,"outgoing",Toast.LENGTH_SHORT).show();
                    callState = "International - Dialing (" + dialingNumber+ ")";*/
                    /*if (dialingNumber.startsWith("00")) {
                        Toast.makeText(mContext,"International - " + dialingNumber, Toast.LENGTH_LONG).show();
                        callState = "International - Dialing (" + dialingNumber+ ")";
                    } else {
                        Toast.makeText(mContext, "Local Call - " + dialingNumber,Toast.LENGTH_LONG).show();
                        callState = "Local - Dialing (" + dialingNumber + ")";
                    }*/
                    break;

            }

            super.onCallStateChanged(state, incomingNumber);
            if(!getLastCallState().equalsIgnoreCase(callState)) {
                Log.i(">>>Broadcast", "onCallStateChanged " + callState+" "+lastCallState);
               // lastCallState=callState;
                setLastCallState(callState);
                updateCallStatus(callState);

            }

        }
    };

    public int updateCallStatus(String callState){
        if(isNetworkAvailable())
            Toast.makeText(mContext,"call status :"+callState,Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext,"Not Connected to internet",Toast.LENGTH_SHORT).show();
        return 0;
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    String getLastCallState(){
        SharedPreferences settings = mContext.getSharedPreferences("soundok",android.content.Context.MODE_PRIVATE);
        //Boolean bb = settings.getBoolean("silentMode", true);
        /*if(bb == null){
            setSound(true);
            return true;
        }else{
            return bb;
        }*/

        lastCallState = settings.getString("lastCallState","UND");

        return lastCallState;
        //return settings.getBoolean("silentMode", false);
    }
    void setLastCallState(String status){
        SharedPreferences setting =mContext.getSharedPreferences("soundok", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("lastCallState",status);
        editor.commit();

    }

    private boolean httpPostMessage(String message){

        try {
            // Construct data
            String data = URLEncoder.encode("key1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");
            data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");

            // Send data
            URL url = new URL("http://hostname:80/cgi");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
                Log.d("server reply",line);
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
        }

        return true;
    }

    private boolean httpGetMessage(String message){
        String yourURL = "www.yourwebserver.com?value1=one&value2=two";
        try {
            URL url = new URL(yourURL);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();
        }catch (Exception e){

        }

        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        Log.d("bloody hell","bloody hell" );
        //Toast.makeText(mContext,"launched balel belae eheheh nnhhhhhh",Toast.LENGTH_LONG).show();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int events = PhoneStateListener.LISTEN_CALL_STATE;
        tm.listen(phoneStateListener, events);
    }
}
