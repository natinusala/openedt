package fr.natinusala.openedt.adapter;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import fr.natinusala.openedt.data.Component;
import fr.natinusala.openedt.data.DataSourceType;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;

/**
 * Created by Maveist on 19/09/2016.
 */
public class ICSAdapter implements IDataAdapter{

    @Override
    public ArrayList<Group> getGroupsList(Component c) throws IOException {
        String url = c.groups_url;
        ArrayList<Group> liste = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        for (Element e : doc.select("option[value$=.html]"))
        {
            Group groupe = new Group();
            groupe.name = e.text();
            groupe.dataSourceType = DataSourceType.CELCAT; //TODO: A voir si on change pour DataSourceType.ICS
            groupe.dataSource = url.replaceAll("gindex.html", e.attr("value").replaceAll(".html", ".ics"));
            groupe.component = c;
            liste.add(groupe);
        }
        return liste;
    }

    @Override
    public ArrayList<Week> getWeeks(Group g) throws IOException {
        ArrayList<Week> semaines = new ArrayList<>();

        FileInputStream file = new FileInputStream(g.dataSource);
        CalendarBuilder builder = new CalendarBuilder();

        try {
            Calendar calendar = builder.build(file);
        } catch (ParserException e) {
            e.printStackTrace();
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

        return semaines;
    }
}
