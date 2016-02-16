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

package fr.natinusala.openedt.scrapping;

import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Periode;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;


public class CelcatEventScrapper
{
	@SerializedName("semaines")
	public ArrayList<Week> semaines = new ArrayList<>();

	@SerializedName("periodes")
	public ArrayList<Periode> periodes = new ArrayList<>();
	
	public CelcatEventScrapper(String url) throws IOException
	{
		Document doc = Jsoup.connect(url).get();
		
		//Périodes		
		for (Element e : doc.select("period"))
		{
			Periode periode = new Periode();
			periode.id = Integer.parseInt(e.attr("id"));
			periode.col = Integer.parseInt(e.attr("col"));
			periode.times = e.attr("times");
			periodes.add(periode);
		}
		
		//Semaines
		Week actuelle;
		int i = 0;
		
		for (Element e : doc.select("title"))
		{
			actuelle = new Week(Integer.parseInt(e.text()));
			semaines.add(actuelle);
		}
		
		for (Element e : doc.select("span"))
		{
			semaines.get(i).id = Integer.parseInt(e.attr("id"));
			semaines.get(i).date = e.attr("date");
			i++;
		}
		
		//Events
		for (Element e : doc.select("event"))
		{
			//Données
			Event event = new Event(Integer.parseInt(e.attr("id")), Integer.parseInt(e.attr("timesort")), e.attr("colour"));
			event.day = Integer.parseInt(e.select("day").text());
			event.prettytimes = e.select("prettytimes").text();
			event.starttime = e.select("starttime").text();
			event.endtime = e.select("endtime").text();
			event.category = e.select("category").text();
			event.weekid = e.select("rawweeks").text().indexOf('Y');
			
			for (Element roomItem : e.select("resources room item"))
			{
				event.room.add(roomItem.text());
			}
			
			for (Element roomItem : e.select("resources module item"))
			{
				event.module.add(roomItem.text());
			}
			
			for (Element roomItem : e.select("resources staff item"))
			{
				event.staff.add(roomItem.text());
			}
			
			for (Element roomItem : e.select("resources group item"))
			{
				event.group.add(roomItem.text());
			}
			
			//Calcul des unités de temps
			event.startTimeUnits = TimeUtils.convertFormattedTimeToUnits(event.starttime);
			event.endTimeUnits = TimeUtils.convertFormattedTimeToUnits(event.endtime);
			
			event.durationUnits = event.endTimeUnits - event.startTimeUnits;
			
			//Calcul du temps maximal de la semaine
			if (event.endTimeUnits > semaines.get(event.weekid).maximumEndTimeUnits)
			{
				semaines.get(event.weekid).maximumEndTimeUnits = event.endTimeUnits;
			}

			//Suppression des trucs sales
			if (event.category.startsWith("XXX-"))
			{
				event.category = event.category.substring(4);
			}

			//Inconnus
			if (event.staff.isEmpty())
			{
				event.staff.add("Inconnu");
			}

			if (event.room.isEmpty())
			{
				event.room.add("Inconnue");
			}

			//Ajout à la bonne liste
			semaines.get(event.weekid).events.add(event);
		}
	}

	public ArrayList<String> getNextModulePebble(){
		//title
		boolean found = false;
		boolean weekend = false;
		Calendar cal = Calendar.getInstance();
		Iterator<Week> week = semaines.iterator();
		int weekID = Week.getIdWeek(Calendar.WEEK_OF_YEAR) + 3;
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
		ArrayList<String> currentModuleList = w != null ? w.getNextModulePebble(weekend) : new ArrayList<String>();
		while(currentModuleList.size() < 3){
			currentModuleList.addAll(week.next().getNextModulePebble(true));
		}

		return currentModuleList;
	}
}
