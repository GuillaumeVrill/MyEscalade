package com.guillaume.myescaladedemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.List;
import java.util.UUID;

public class RegisterPerformanceActivity extends Activity {

    private ListView listview;
    private Button btn_go;
    private Chronometer chrono;

    private long lastPause;

    private boolean pause;

    private BeaconManager beaconManager;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_performance);

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

                    if (PowerByRssi >= 1.42) {
                        switch (beaconDetected) {
                            //Mint Cocktail
                            case "1857:60524":
                                //Toast.makeText(getApplicationContext(), "Vert: PREMIER POINT - "+chrono.getText(), Toast.LENGTH_SHORT).show();
                                ((TextView)listview.getChildAt(listview.getChildCount() - 1).findViewById(R.id.time_point)).setText(chrono.getText());
                                break;
                            //Icy Marshmallow
                            case "5526:19125":
                                //Toast.makeText(getApplicationContext(), "Bleu: SECOND POINT - Rssi/Power = "+PowerByRssi+ " mètres", Toast.LENGTH_SHORT).show();
                                ((TextView)listview.getChildAt(listview.getChildCount() - 2).findViewById(R.id.time_point)).setText(chrono.getText());
                                break;
                            //Blueberry Pie
                            case "17828:47111":
                                //Toast.makeText(getApplicationContext(), "Violet: TROISIEME POINT - Rssi/Power = "+PowerByRssi+ " mètres", Toast.LENGTH_SHORT).show();
                                ((TextView)listview.getChildAt(listview.getChildCount() - 3).findViewById(R.id.time_point)).setText(chrono.getText());
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
        });
        //end of Beacon gesture

        pause = true;

        listview = (ListView) findViewById(R.id.perf_timelist);
        listview.setAdapter(new ListViewCustomAdapter(this, new String[]{"", "", ""}));

        chrono = (Chronometer) findViewById(R.id.perf_chrono);
        chrono.setBase(SystemClock.elapsedRealtime());
        lastPause = SystemClock.elapsedRealtime();

        btn_go = (Button) findViewById(R.id.perf_gobtn);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause) {
                    btn_go.setBackground(getResources().getDrawable(R.drawable.btn_perf_border2, null));
                    btn_go.setText(getResources().getText(R.string.perf_btn_pause));
                    pause = false;
                    //lancer le chrono:
                    chrono.setBase(chrono.getBase() + SystemClock.elapsedRealtime() - lastPause);
                    chrono.start();
                    start(); //Relance la recherche de beacons
                } else {
                    btn_go.setBackground(getResources().getDrawable(R.drawable.btn_perf_border, null));
                    btn_go.setText(getResources().getText(R.string.perf_btn_go));
                    pause = true;
                    //stopper le chrono:
                    lastPause = SystemClock.elapsedRealtime();
                    chrono.stop();
                    pause(); //arrete la recherche de beacons
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_performance, menu);
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
        start();
    }

    @Override
    protected void onPause(){
        pause();
        super.onPause();
    }

    public void pause(){
        beaconManager.stopRanging(region);
    }

    public void start(){
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    class ListViewCustomAdapter extends BaseAdapter {

        Context context;
        String[] data;
        private LayoutInflater inflater = null;

        public ListViewCustomAdapter(Context context, String[] data) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.list_view_perf_register_item, null);
            TextView tv_point = (TextView) vi.findViewById(R.id.num_point);
            tv_point.setText("Point "+(data.length - position));
            TextView tv_time = (TextView) vi.findViewById(R.id.time_point);
            tv_time.setText("--:--");
            ImageView point_img = (ImageView) vi.findViewById(R.id.point_img);
            if(position == 0)
                point_img.setImageResource(R.drawable.point_a);
            else if(position == data.length - 1)
                point_img.setImageResource(R.drawable.point_d);
            return vi;
        }
    }

}
