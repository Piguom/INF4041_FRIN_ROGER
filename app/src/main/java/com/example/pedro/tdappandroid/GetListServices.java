package com.example.pedro.tdappandroid;

/**
 * Created by Pedro on 07/11/2016.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.pedro.tdappandroid.Listing.STATIONS_UPDATE;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetListServices extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_ALL_STATION = "com.example.pedro.tdappandroid.action.GET_ALL_STATION";
    private static final String GET_LIST_SERVICES = "GetListServices";

    public GetListServices() {
        super("GetListServices");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionStations(Context context) {
        Toast.makeText(context, R.string.dl, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, GetListServices.class);
        intent.setAction(ACTION_GET_ALL_STATION);
        context.startService(intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Listing.STATIONS_UPDATE));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_ALL_STATION.equals(action)) {
                handleActionStations();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStations() {
        // TODO: Handle action Foo
        Log.i("GetListServices","Thread service name: " + Thread.currentThread().getName());
        URL url = null;

        double latitude = 0.0;
        double longitude = 0.0;
        GPSTracker gps = new GPSTracker(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);


        try{
            // Center in Ile de France to default with a limit of 5 train station to default
             url = new URL("http://www.raildar.fr/json/gares?lat=48.8361957&lng=2.3805608&dist=50&limit="+pref.getString("lim","5"));
             if(gps.canGetLocation()){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                url = new URL("http://www.raildar.fr/json/gares?lat="+latitude+"&lng="+longitude+"&dist="+pref.getString("dist","10")+"&limit="+pref.getString("lim","5"));
                Log.i(GET_LIST_SERVICES,"User location with preferences");
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(),new File(getCacheDir(),"stations.json"));
                Log.i(GET_LIST_SERVICES,"Json downloaded !");
                //Refreshing list issue, so I added a refresh callback with this notification
                createNotification("Download done !", "Click to refresh liste");
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void copyInputStreamToFile(InputStream in, File file){
        try{
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private final void createNotification(String titre, String text){
        Intent intent = new Intent(getApplicationContext(), Listing.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification myNotification  = new Notification.Builder(getApplicationContext())
                .setContentTitle(titre)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, myNotification);
    }
}
