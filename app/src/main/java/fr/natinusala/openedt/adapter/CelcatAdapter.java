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

package fr.natinusala.openedt.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import org.apache.commons.codec.binary.Base64;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import fr.natinusala.openedt.data.Component;
import fr.natinusala.openedt.data.DataSourceType;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.manager.AuthManager;
import fr.natinusala.openedt.utils.TimeUtils;

public class CelcatAdapter implements IDataAdapter
{
    @Override
    public ArrayList<Group> getGroupsList(Component c, Context context) throws IOException {
        ArrayList<Group> liste = new ArrayList<>();


        String url = c.groups_url;
        Connection conn = Jsoup.connect(url);


        if(c.needAuth) {
            AccountManager manager = AccountManager.get(context);
            Account account = AuthManager.getAccount(c.name, context);
            String id =  account.name;
            String pwd = manager.getPassword(account);

            String login = id+":"+pwd;

            String b64login = new String(android.util.Base64.encode(login.getBytes(), android.util.Base64.DEFAULT));
            conn.header("Authorization", "Basic " + b64login);

        }

        Document doc = conn.get();
        for (Element e : doc.select("option[value$=.html]"))
        {
            Group groupe = new Group();
            groupe.name = e.text();
            groupe.dataSourceType = DataSourceType.CELCAT;
            groupe.dataSource = url.replaceAll("gindex.html", e.attr("value").replaceAll(".html", ".xml"));
            groupe.component = c;
            liste.add(groupe);
        }
        return liste;
    }

    @Override
    public ArrayList<Week> getWeeks(Group g) throws IOException
    {
        ArrayList<Week> semaines = new ArrayList<>();

        Document doc = Jsoup.connect(g.dataSource).get();

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
