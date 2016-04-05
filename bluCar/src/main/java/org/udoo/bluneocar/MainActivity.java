package org.udoo.bluneocar;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.udoo.udooblulib.interfaces.IBleDeviceListener;
import org.udoo.udooblulib.manager.UdooBluManager;

import org.udoo.bluneocar.fragment.BlueNeoFragment;

public class MainActivity extends AppCompatActivity {
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        address = getIntent().getStringExtra("address");
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final UdooBluManager udooBluManager = ((BluNeoCarApplication) getApplication()).getBluManager();

                udooBluManager.connect(address, new IBleDeviceListener() {
                    @Override
                    public void onDeviceConnected() {
                        udooBluManager.discoveryServices(address);
                    }

                    @Override
                    public void onServicesDiscoveryCompleted() {
                        getFragmentManager().beginTransaction().replace(R.id.container, BlueNeoFragment.Builder(address)).commit();
                    }

                    @Override
                    public void onDeviceDisconnect() {

                    }
                });

            }
        }, 1000);
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
}
