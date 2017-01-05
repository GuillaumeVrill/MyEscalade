package com.guillaume.myescaladedemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity {

    private Button btnTopo;
    private Button btnPerf;
    private TextView footer;

    private BeaconManager beaconManager;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTopo = (Button)findViewById(R.id.btn_topo);
        btnPerf = (Button)findViewById(R.id.btn_perf);
        footer = (TextView)findViewById(R.id.link_footer);

        btnTopo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TopoguideActivity.class);
                startActivity(i);
            }
        });

        btnPerf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PerformanceActivity.class);
                startActivity(i);
            }
        });

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.addCategory(Intent.CATEGORY_BROWSABLE);
                i.setData(Uri.parse("https://fr.linkedin.com/in/guillaume-vrilliaux-7412a5109"));
                startActivity(i);
            }
        });

        //Gestion des beacons:
        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setForegroundScanPeriod(5000, 0);

        // add this below:
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    String beaconDetected = nearestBeacon.getMajor() + ":" + nearestBeacon.getMinor();

                    double PowerByRssi = (double) nearestBeacon.getMeasuredPower() / (double) nearestBeacon.getRssi();
                    if (Double.toString(PowerByRssi).length() > 5) {
                        PowerByRssi = Double.parseDouble(Double.toString(PowerByRssi).substring(0, 5));
                    }

                    if(PowerByRssi >= 1.42) {
                        switch (beaconDetected) {
                            //Mint Cocktail
                            case "1857:60524":
                                //Toast.makeText(getApplicationContext(), "Vert: Rssi/Power = "+PowerByRssi+ " mètres", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), TopoguideActivity.class);
                                startActivity(i);
                                break;
                            //Icy Marshmallow
                            case "5526:19125":
                                //Toast.makeText(getApplicationContext(), "Bleu: Rssi/Power = "+PowerByRssi+ " mètres", Toast.LENGTH_SHORT).show();
                                break;
                            //Blueberry Pie
                            case "17828:47111":
                                //Toast.makeText(getApplicationContext(), "Violet: Rssi/Power = "+PowerByRssi+ " mètres", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause(){
        beaconManager.stopRanging(region);
        super.onPause();
    }
}
