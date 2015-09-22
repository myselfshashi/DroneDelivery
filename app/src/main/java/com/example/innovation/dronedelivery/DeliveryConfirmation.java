package com.example.innovation.dronedelivery;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class DeliveryConfirmation extends ActionBarActivity {
    private String TAG = Helper.TAG;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_confirmation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delivery_confirmation, menu);
        MenuItem menuItem = menu.add(Menu.NONE,R.id.action_bar,101,R.string.app_name);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
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

    public void btnAccept(View view){
        Helper.deliveryAccepted = true;
        Log.i(TAG, "deliveryAccepted in btnAccept: " + Helper.deliveryAccepted);
        Toast.makeText(this, "Thanks for accepting delivery", Toast.LENGTH_SHORT)
                .show();
        Helper.disableConfirmButton = true;
        Helper.stopFindingDrone = true;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void btnReject(View view){
        Helper.deliveryAccepted = false;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
