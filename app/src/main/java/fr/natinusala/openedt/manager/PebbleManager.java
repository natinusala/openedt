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
 */

package fr.natinusala.openedt.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Button;

import com.getpebble.android.kit.*;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.UUID;

import fr.natinusala.openedt.activity.MainActivity;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;

/**
 * Created by Maveist on 19/02/16.
 */
public class PebbleManager {

    private static UUID PEBBLE_APP_UUID = UUID.fromString("00f5db3f-43da-4229-9368-14aa35422398");
    private Activity activity;
    private PebbleKit.PebbleDataReceiver receiver;
    private ArrayList<Week> weeksList;

    public PebbleManager(Activity act){
        activity = act;
        receiver = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {

            @SuppressLint("SetTextI18n")
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                ArrayList<String> dataToSend = getNextEventPebble();
                /*AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(dataToSend.get(0) + "\n\n" + dataToSend.get(1) + "\n\n" + dataToSend.get(2));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();*/

                PebbleDictionary msg = new PebbleDictionary();

                msg.addString(0, dataToSend.get(0));
                msg.addString(128, dataToSend.get(1));
                msg.addString(256, dataToSend.get(2));
                PebbleKit.sendDataToPebble(activity.getApplicationContext(), PEBBLE_APP_UUID, msg);
            }
        };
        PebbleKit.registerReceivedDataHandler(activity.getApplicationContext(), receiver);
    }

    public void setWeekList(ArrayList<Week> weeks){
        weeksList = weeks;
    }

    public ArrayList<String> getNextEventPebble(){
        Calendar cal = Calendar.getInstance();
        boolean found = false;
        boolean weekend = false;
        Iterator<Week> week = weeksList.iterator();
        int weekID = TimeUtils.getIdWeek(cal.get(Calendar.WEEK_OF_YEAR));
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { weekend = true; }
        Week w = null;
        if(!weekend) {
            while (week.hasNext() && !found) {
                w = week.next();
                if (w.id == weekID) {
                    found = true;
                }
            }
        }else{
            while (week.hasNext() && !found) {
                w = week.next();
                if (w.id == weekID+1) {
                    found = true;
                }
            }
        }
        ArrayList<String> currentEventList = w.getNextModulePebble(weekend);
        while(currentEventList.size() < 3){
            currentEventList.addAll(week.next().getNextModulePebble(true));
        }

        return currentEventList;
    }
}
