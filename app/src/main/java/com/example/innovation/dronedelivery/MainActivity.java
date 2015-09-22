package com.example.innovation.dronedelivery;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.manager.ProximityManager;


public class MainActivity extends ActionBarActivity {

    private KeepMeRunningService srv;
    String TAG = Helper.TAG;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            KeepMeRunningService.MyBinder b = (KeepMeRunningService.MyBinder) binder;
            srv = b.getService();
/*            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                    .show();*/
            Log.i(TAG, "I am in call for onServiceConneted ");
        }

        public void onServiceDisconnected(ComponentName className) {
            srv = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean deliveryArrived = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Helper.mainContext = this;
/*        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));*/

        // Start the Service
        startService(new Intent(this, KeepMeRunningService.class));
        if (Helper.deliveryArrived == false && Helper.disableConfirmButton == true){
            //Disbale the confirm delivery button
            View button = findViewById(R.id.buttonConfirmDelivery);
            button.setEnabled(false);
        }
        else {
            View button = findViewById(R.id.buttonConfirmDelivery);
            button.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.add(Menu.NONE,R.id.action_bar,101,R.string.app_name);
        MenuItemCompat.setShowAsAction(menuItem,MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(R.mipmap.ic_launcher);
        super.onCreateOptionsMenu(menu);
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
        Log.i(TAG, "I am in onResume");
        Intent intent= new Intent(this, KeepMeRunningService.class);
        View button = findViewById(R.id.buttonConfirmDelivery);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        if (Helper.deliveryArrived == true && Helper.disableConfirmButton == false){
            //Disbale the confirm delivery button
            button.setEnabled(true);
        }
        else {
            button.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "I am in onPause");
        super.onPause();
        View button = findViewById(R.id.buttonConfirmDelivery);
        if (Helper.deliveryArrived == true && Helper.disableConfirmButton == false){
            //Disbale the confirm delivery button
            button.setEnabled(true);
        }
        else {
            button.setEnabled(false);
        }
        unbindService(mConnection);
    }

    //This will enable stop the service once button is cliecked
    public void btnStopDeviceFinding(View view){
        stopService(new Intent(this, KeepMeRunningService.class));
    }

    public void btnConfirmDelivery(View view){
        Intent intent = new Intent(this,DeliveryConfirmation.class);
        startActivity(intent);
    }

    public void chkBoxForceDelivery(View view){
        CheckBox checkBoxForceDelivery = (CheckBox) findViewById(R.id.chkBoxForceDelivery);
        if (checkBoxForceDelivery.isChecked()){
            View button = findViewById(R.id.buttonConfirmDelivery);
            button.setEnabled(true);
        }

    }
}
