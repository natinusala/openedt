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

package fr.natinusala.openedt.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.natinusala.openedt.data.Scale;
import fr.natinusala.openedt.data.Week;

public class TimeUtils
{
    public static int convertMinutesToTimeUnits(int minutes)
    {
        return (int)(((float)minutes / 60f) * 100f);
    }

    public static boolean isSameDay(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static SimpleDateFormat createDateFormat() {return new SimpleDateFormat("dd/MM/yyyy");}

    public static SimpleDateFormat createLongDateFormat() {return new SimpleDateFormat("EEEE d MMMM", Locale.FRANCE);} //lundi 10 février

    public static int convertFormattedTimeToUnits(String time)
    {
        String[] timeSplitted = time.split(":");
        return (Integer.parseInt(timeSplitted[0]) * 100) + TimeUtils.convertMinutesToTimeUnits(Integer.parseInt(timeSplitted[1])) - Scale.START;
    }

    public static int convertFormattedTimeToUnits(int hours, int minutes){
        return(hours*100 + minutes) - Scale.START;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2)
    {
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isToday(Date date)
    {
        return isSameDay(date, Calendar.getInstance().getTime());
    }


    public static int getIdWeek(int trueWeekNumber){
        return  ((52-34)+ trueWeekNumber % 53) + 1;
    }

    public static int getNumberOfDay(int numberDayCalendar){
        return (numberDayCalendar + 5 ) % 7;

    }

    public static Date createDateForDay(int day, Week week)
    {
        SimpleDateFormat sdf = TimeUtils.createDateFormat();
        Calendar c = Calendar.getInstance();
        try
        {
            c.setTime(sdf.parse(week.date));
            c.add(Calendar.DATE, day);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return c.getTime();
    }

    /*public static int dayCalendarToDayCelcat(int dayCal){
        return (dayCal+8)%7;
    }*/

}