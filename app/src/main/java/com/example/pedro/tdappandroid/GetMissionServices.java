package com.example.pedro.tdappandroid;

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

import static com.example.pedro.tdappandroid.Dpartures.MISSIONS_UPDATE;
import static com.example.pedro.tdappandroid.Listing.STATIONS_UPDATE;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetMissionServices extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_ALL_MISSION = "com.example.pedro.tdappandroid.action.GET_ALL_MISSION";
    private static final String GET_MISSION_SERVICES = "GetMissionServices";

    public GetMissionServices() {
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
        Intent intent = new Intent(context, GetMissionServices.class);
        intent.setAction(ACTION_GET_ALL_MISSION);
        context.startService(intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Dpartures.MISSIONS_UPDATE));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_ALL_MISSION.equals(action)) {
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
        URL url;
        try{
            url = new URL("http://www.raildar.fr/json/next_missions?id_gare="+Dpartures.id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(),new File(getCacheDir(),"missions.json"));
                Log.i(GET_MISSION_SERVICES,"Json downloaded !");
                createNotification(getString(R.string.missdone),getString(R.string.refrechlist));
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
        //Do nothing when click on it just dismiss it
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), intent.FLAG_ACTIVITY_CLEAR_TOP, intent, 0);

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
