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

package fr.natinusala.openedt.data;





import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.natinusala.openedt.utils.TimeUtils;

public class Week
{
    public int id;
    public int num;
    public String date;

    public int maximumEndTimeUnits = 0;

    public ArrayList<Event> events = new ArrayList<>();

    public Week(int num)
    {
        this.num = num;
    }

    @Override
    public String toString()
    {
        return "Semaine [id=" + id + ", num=" + num + ", date=" + date + "]";
    }

    //Attention décalage d'au moins une heure dans les times units des modules
    public Map<Event, Week> getNextEvents(boolean isWeekEnd){
        Calendar cal = Calendar.getInstance();
        int today = TimeUtils.getNumberOfDay(cal.get(Calendar.DAY_OF_WEEK));
        // le -1 sur calNow.HOUR_OF_DAY est pour prévenir le décalage.
        int nowHourMinutes = TimeUtils.convertFormattedTimeToUnits(cal.get(Calendar.HOUR_OF_DAY) - 1, cal.get(Calendar.MINUTE));
        Map<Event, Week> nextEvents = new HashMap<>();
        Iterator<Event> e = events.iterator();
        if(isWeekEnd){
            while(e.hasNext() && nextEvents.size() < 3){
                nextEvents.put(e.next(), this);
            }
        }else {
            while (e.hasNext() && nextEvents.size() < 3) {
                Event event = e.next();
                if ((event.day >= today)&& (event.endTimeUnits >= nowHourMinutes)) {
                    nextEvents.put(event, this);
                }
            }
        }
        return nextEvents;

    }




}