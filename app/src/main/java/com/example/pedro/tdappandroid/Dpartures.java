package com.example.pedro.tdappandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Dpartures extends AppCompatActivity{


    public static final String MISSIONS_UPDATE = "com.example.pedro.tdappandroid.action.MISSIONS_UPDATE";

    RecyclerView mission ;

    static String name, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dpartures);

        Intent get = getIntent();
        name = get.getStringExtra("name_gare");
        id = get.getStringExtra("id");

        IntentFilter intentFilter = new IntentFilter(MISSIONS_UPDATE);
        LocalBroadcastManager.getInstance(Dpartures.this).registerReceiver(new Dpartures.MissionsUpdate(),intentFilter);
        GetMissionServices.startActionStations(getApplicationContext());

        mission = (RecyclerView)findViewById(R.id.dpartures);
        mission.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mission.setAdapter(new MissionAdapter());
    }

    public class MissionsUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("getMissionServices", " "+getIntent().getAction());
            ((MissionAdapter) mission.getAdapter()).setStations(getStationsFromFile());
        }
    }

    public JSONArray getStationsFromFile() {
        try{
            InputStream is = new FileInputStream(getCacheDir()+"/"+"missions.json");
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

    private class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionHolder> {

        JSONArray missions = new JSONArray();

        @Override
        public MissionAdapter.MissionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dparture_list_element, parent, false);
            return new MissionAdapter.MissionHolder(v);
        }

        @Override
        public void onBindViewHolder(MissionAdapter.MissionHolder holder, int position) {
            try {
                holder.terminus.setText((missions.getJSONObject(position).getString("terminus")));
                holder.retard.setText((missions.getJSONObject(position).getString("minutes_retard")));
                holder.reel.setText((missions.getJSONObject(position).getString("time_reel")));
                holder.theorique.setText((missions.getJSONObject(position).getString("time_theorique")));
                holder.info.setText((missions.getJSONObject(position).getString("info")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return missions.length();
        }

        public void setStations(JSONArray json) {
            missions = json;
            notifyDataSetChanged();
        }

        public class MissionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView terminus = null;
            public TextView retard = null;
            public TextView reel = null;
            public TextView theorique = null;
            public TextView info = null;

            public MissionHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                terminus = (TextView) itemView.findViewById(R.id.terminus);
                retard = (TextView) itemView.findViewById(R.id.retard);
                reel = (TextView) itemView.findViewById(R.id.reel);
                theorique = (TextView) itemView.findViewById(R.id.theorique);
                info = (TextView) itemView.findViewById(R.id.info);
            }

            @Override
            public void onClick(View v) {
              //Nothing happened for now...
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_depart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_replay){
            Intent i = new Intent(this,Dpartures.class);
            finish();
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
