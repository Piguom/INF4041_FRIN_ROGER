package com.example.pedro.tdappandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private String name_tx, lat_tx, lng_tx, id_gare_tx;
    Intent i = null;
    GoogleMap gMap;
    float[] dist = new float[1];
    int cpt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        i = getIntent();
        name_tx = i.getStringExtra("name_gare");
        lat_tx = i.getStringExtra("lat");
        lng_tx = i.getStringExtra("lng");
        id_gare_tx = i.getStringExtra("id_gare");

        Button change = (Button) findViewById(R.id.button2);
        if(lat_tx == null && lng_tx == null) {
            change.setText(R.string.select);
            cpt ++;
        }
        else {
            change.setText(R.string.change);
            cpt = 0;
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cpt==0)
                    finish();
                else{
                    Intent i = new Intent(getApplicationContext(), Listing.class);
                    finish();
                    startActivity(i);
                }
            }
        });
    }

    public void onMapReady(GoogleMap map) {
        gMap = map;
          if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
          }else{
            if(!gMap.isMyLocationEnabled())
                gMap.setMyLocationEnabled(true);

             GPSTracker gps = new GPSTracker(this);
              if(gps.canGetLocation()){
                  if(i!=null && name_tx!=null) {
                      double lat = Double.parseDouble(lat_tx);
                      double lng = Double.parseDouble(lng_tx);

                      LatLng stationLocation = new LatLng(lat, lng);
                      gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stationLocation, 14), 1500, null);

                      Location.distanceBetween(gps.getLatitude(),gps.getLongitude(),lat, lng, dist);

                      addMArker(gMap, name_tx,getString(R.string.distance)+" : "+dist[0]/1000+" km", lat, lng );
                  }
                  else{
                      LatLng userLocation = new LatLng(gps.getLatitude(), gps.getLongitude());
                      gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
                  }
              }else{
                  if(i!=null && name_tx!=null) {
                      double lat = Double.parseDouble(lat_tx);
                      double lng = Double.parseDouble(lng_tx);

                      addMArker(gMap, name_tx,getString(R.string.idgare)+" : "+ id_gare_tx +" -- Latitude : "+lat+" | Longitude : "+lng, lat, lng );

                      LatLng userLocation = new LatLng(lat, lng);
                      gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
                  }
              }
        }
      }

    public void addMArker (GoogleMap map, String title, String desc, double lat, double lng){
        map.addMarker(new MarkerOptions()
                .title(title)
                .snippet(desc)
                .position(new LatLng(lat, lng)));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    Intent i = new Intent(getApplicationContext(), Dpartures.class);
                                    i.putExtra("name_gare",name_tx);
                                    i.putExtra("id",id_gare_tx);
                                    startActivity(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setMessage(R.string.depart)
                        .setPositiveButton(R.string.oui, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });
    }






}
