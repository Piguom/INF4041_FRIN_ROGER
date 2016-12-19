package com.example.pedro.tdappandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Listing extends AppCompatActivity {

    public static final String STATIONS_UPDATE = "com.example.pedro.tdappandroid.action.STATIONS_UPDATE";

    RecyclerView rv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        IntentFilter intentFilter = new IntentFilter(STATIONS_UPDATE);
        LocalBroadcastManager.getInstance(Listing.this).registerReceiver(new Listing.StationsUpdate(),intentFilter);
        GetListServices.startActionStations(getApplicationContext());

        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rv.setAdapter(new StationsAdapter());
    }

    public class StationsUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("GetListServices", " "+getIntent().getAction());
            ((StationsAdapter) rv.getAdapter()).setStations(getStationsFromFile());
        }
    }

    public JSONArray getStationsFromFile() {
        try{
            InputStream is = new FileInputStream(getCacheDir()+"/"+"stations.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONArray(new String(buffer,"UTF-8"));
        }catch (IOException e){
            e.printStackTrace();
            return new JSONArray();
        }catch (JSONException e){
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationHolder> {

        JSONArray stations = new JSONArray();

        @Override
        public StationsAdapter.StationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_element, parent, false);
            return new StationHolder(v);
        }

        @Override
        public void onBindViewHolder(StationsAdapter.StationHolder holder, int position) {
            try {
                holder.name.setText((stations.getJSONObject(position).getString("name_gare")));
                holder.lat.setText((stations.getJSONObject(position).getString("lat")));
                holder.lng.setText((stations.getJSONObject(position).getString("lng")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return stations.length();
        }

        public void setStations(JSONArray json) {
            stations = json;
            notifyDataSetChanged();
        }

        public class StationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView name = null;
            public TextView lng = null;
            public TextView lat = null;
            public ImageView image_name = null;

            public StationHolder(View itemView) {
                super(itemView);

                if (itemView == null) {
                    // inflate your menu
                    itemView.inflate(Listing.this, 0, null);
                    itemView.setTag(0, null);
                } else {
                    itemView.setOnClickListener(this);
                    name = (TextView) itemView.findViewById(R.id.rv_bier_element_name);
                    lng = (TextView) itemView.findViewById(R.id.rv_bier_element_lat);
                    lat = (TextView) itemView.findViewById(R.id.rv_bier_element_lng);
                }
            }

            @Override
            public void onClick(View v) {
                try {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    try {
                                        Intent i = new Intent(getApplicationContext(), MapActivity.class);
                                        i.putExtra("name_gare", stations.getJSONObject(getPosition()).getString("name_gare"));
                                        i.putExtra("lat", stations.getJSONObject(getPosition()).getString("lat"));
                                        i.putExtra("lng", stations.getJSONObject(getPosition()).getString("lng"));
                                        i.putExtra("id_gare", stations.getJSONObject(getPosition()).getString("id_gare"));

                                        startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Listing.this);
                    builder.setMessage(getString(R.string.bind)+"\n" + stations.getJSONObject(getPosition()).getString("name_gare") + "\nLatitude : " +
                            stations.getJSONObject(getPosition()).getString("lat") + "\nLongitude : " + stations.getJSONObject(getPosition()).getString("lng"))
                            .setPositiveButton(R.string.oui, dialogClickListener)
                            .setNegativeButton(R.string.no, dialogClickListener).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(),Setting.class);
            startActivity(i);
            return true;
        }

        if(id == R.id.action_replay){
            Intent i = new Intent(this,Listing.class);
            finish();
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
