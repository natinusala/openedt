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
import fr.natinusala.openedt.data.WrapperEventWeek;
import fr.natinusala.openedt.utils.TimeUtils;

/**
 *
 */
public class WeekManager {


    public static ArrayList<WrapperEventWeek> getNextEvents(ArrayList<Week> weeks, int count)
    {
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
       ArrayList<WrapperEventWeek> currentEventList = null;
        if(w != null) {
            currentEventList = w.getNextEvents(weekend, count);
            while (week.hasNext() && currentEventList.size() < count) {
                currentEventList.addAll(week.next().getNextEvents(true, count));
            }
        }

        if (currentEventList != null){
            return new ArrayList<>(currentEventList.subList(0, count));
        }
        else {
            return new ArrayList<>(new ArrayList<WrapperEventWeek>());
        }
    }


    public static ArrayList<String> getNextEventsForPebble(ArrayList<Week> weekArray){
        ArrayList<WrapperEventWeek> wrappers = WeekManager.getNextEvents(weekArray, 3);
        ArrayList<String> strToReturn = new ArrayList<>();
        for(WrapperEventWeek wrap : wrappers){
            strToReturn.add(wrap.getEvent().toPebbleString());
        }
        return strToReturn;
    }

    public static Week getCurrentWeek(ArrayList<Week> weeks){
        Calendar cal = Calendar.getInstance();
        int weekID = TimeUtils.getIdWeek(cal.get(Calendar.WEEK_OF_YEAR));
        int today = cal.get(Calendar.DAY_OF_WEEK);
        if(today == Calendar.SUNDAY || today == Calendar.SATURDAY){ weekID++; }
        for(Week w : weeks){
            if(w.id == weekID){
                return w;
            }
        }
        return null;
    }

    //Transforme la liste des events par semaine en liste des events par jour
    public static ArrayList<ArrayList<Event>> getEventPerDay(Week week){
        ArrayList<ArrayList<Event>> days = new ArrayList<>();
        ArrayList<Event> events = new ArrayList<>();
        if(week.events.size() > 0) {
            int dayCursor = week.events.get(0).day;//initialsation avec le premier event
            ArrayList<Event> weekEvent = week.events;
            for (Event e : weekEvent) {
                if (e.day == dayCursor) {
                    events.add(e);
                } else if (e.day > dayCursor) {
                    days.add(events);
                    events = new ArrayList<>();
                    dayCursor = e.day;
                    events.add(e);
                }
            }
        }else{
            return null;
        }
        return days;
    }
}
