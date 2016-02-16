/*
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    Contributors : natinusala, Maveist
 */

package fr.natinusala.openedt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Context;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import fr.natinusala.openedt.R;
import fr.natinusala.openedt.manager.SaveManager;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.scrapping.CelcatEventScrapper;
import fr.natinusala.openedt.views.WeekView;

public class MainActivity extends ActionBarActivity
{
    public static final String SCRAPPER_SAVE = "SmarterEDTData";
    public static final String TITLE = "SmarterEDT - ";
    private static final UUID WATCHAPP_UUID = UUID.fromString("00f5db3f-43da-4229-9368-14aa35422398");
    CelcatEventScrapper scrapper;
    MainActivity instance = this;
    LinearLayout testWeek;
    SaveManager  saveManager ;

    int cptTest = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        saveManager = new SaveManager(this);

        if (!saveManager.isScrapperSaved())
        {
            Intent intent = new Intent(this, AddGroupActivity.class);
            this.startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        new Task(false).execute();

        //Pebble compatibility
        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(WATCHAPP_UUID) {

            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                Button btn = new Button(getApplicationContext());
                btn.setText("Just received a message! " /*+ Long.toString((data.getInteger(1)))*/);
                testWeek.addView(btn);
                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);

                ArrayList<String> dataToSend = scrapper.getNextModulePebble();

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(dataToSend.get(0)+"\n\n"+dataToSend.get(1)+"\n\n"+dataToSend.get(2));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                PebbleDictionary msg = new PebbleDictionary();

                msg.addString(0, dataToSend.get(0));
                msg.addString(128, dataToSend.get(1));
                msg.addString(256, dataToSend.get(2));
                PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, msg);
            }
        });


    }

    public void getCours(){
        String str = "";
        ArrayList<String> dataToSend = scrapper.getNextModulePebble();
        for(String s : dataToSend){
            str += "\n\n" + s;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(str);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

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
        if(id == R.id.change_group){
            this.startActivity(new Intent(this, AddGroupActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        Log.v("debug", "on resume");
        refresh();
    }

    public void refresh(){
        SharedPreferences pref = getSharedPreferences(SCRAPPER_SAVE, 0);
        String grpName = pref.getString("groupName", "Inconnu");
        setTitle(TITLE  + grpName);
        Log.v("debug", TITLE + grpName);
        ProgressView pv = (ProgressView) findViewById(R.id.progress_circle);
        pv.setVisibility(View.VISIBLE);
        new Task(true).execute();
    }

    private class Task extends AsyncTask<Void, Void, Void> {

        private boolean refresh;

        public Task(boolean toRefresh){
            this.refresh = toRefresh;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                if(!this.refresh) {
                    scrapper = saveManager.getEventScrapper();
                }else{
                    scrapper = saveManager.refresh();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                ProgressView pv = (ProgressView) findViewById(R.id.progress_circle);
                pv.setVisibility(View.GONE);
                Calendar cal = Calendar.getInstance();
                int weekCal = cal.get(Calendar.WEEK_OF_YEAR) + 1;
                int week = Week.getIdWeek(weekCal);

                LinearLayout weeksList = (LinearLayout) findViewById(R.id.weekContainer);
                weeksList.removeAllViews();
                testWeek = weeksList;


                /* For debug
                 //TODO a checkbox in setting to display some button to access for debug's methods
                Button btnString = new Button(getApplicationContext());
                btnString.setText("Get current module");
                btnString.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCours();

                    }
                });
                boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
                weeksList.addView(btnString);
                if (!connected) {
                    Button btn = new Button(getApplicationContext());
                    btn.setText("Not connected");
                    weeksList.addView(btn);
                }
                 END FOR DEBUG */
                weeksList.addView(new WeekView(instance, scrapper.semaines.get(week - 1)));
                weeksList.addView(new WeekView(instance, scrapper.semaines.get(week)));
                weeksList.addView(new WeekView(instance, scrapper.semaines.get(week + 1)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }


    }
    public void displayMessageDebug(String str) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(str);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
       /* Toast.makeText(getApplicationContext(), str,
                Toast.LENGTH_SHORT).show();*/
    }
}
