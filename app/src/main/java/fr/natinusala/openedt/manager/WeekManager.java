/*
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 *    Created by Maveist on 27/02/16.
 */

package fr.natinusala.openedt.manager;




import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;

/**
 *
 */
public class WeekManager {


    public static ArrayList<Event> getNextEvents(ArrayList<Week> weeks){
        Calendar cal = Calendar.getInstance();
        boolean found = false;
        boolean weekend = false;
        Iterator<Week> week = weeks.iterator();
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
        ArrayList<Event> currentEventList = null;
        if(w != null) {
            currentEventList = w.getNextEvents(weekend);
            while (currentEventList.size() < 3) {
                currentEventList.addAll(week.next().getNextEvents(true));
            }
        }
        return currentEventList;
    }

    public static ArrayList<String> getNextEventsForPebble(ArrayList<Week> weekArray){
        ArrayList<Event> events = WeekManager.getNextEvents(weekArray);
        ArrayList<String> strToReturn = new ArrayList<>();
        for(Event event : events){
            strToReturn.add(event.toPebbleString());
        }
        return strToReturn;
    }
}
