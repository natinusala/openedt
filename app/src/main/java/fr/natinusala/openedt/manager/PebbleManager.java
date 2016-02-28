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
 *
 * Created by Maveist on 19/02/16.
 */
package fr.natinusala.openedt.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.getpebble.android.kit.*;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.UUID;
import fr.natinusala.openedt.data.Week;



public class PebbleManager {

    private static UUID PEBBLE_APP_UUID = UUID.fromString("00f5db3f-43da-4229-9368-14aa35422398");
    private ArrayList<Week> weeksList;
    private Activity acti;
    public PebbleManager(Activity act){
        acti = act;
        PebbleKit.PebbleDataReceiver receiver = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
            @SuppressLint("SetTextI18n")
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                ArrayList<String> dataToSend = WeekManager.getNextEventsForPebble(weeksList);
                PebbleDictionary msg = new PebbleDictionary();
                msg.addString(0, dataToSend.get(0));
                msg.addString(128, dataToSend.get(1));
                msg.addString(256, dataToSend.get(2));
                PebbleKit.sendDataToPebble(acti.getApplicationContext(), PEBBLE_APP_UUID, msg);
            }
        };
        PebbleKit.registerReceivedDataHandler(act.getApplicationContext(), receiver);
    }

    public void setWeekList(ArrayList<Week> weeks){
        weeksList = weeks;
    }
}
