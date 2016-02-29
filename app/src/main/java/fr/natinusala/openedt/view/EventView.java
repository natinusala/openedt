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
 */

package fr.natinusala.openedt.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;
import fr.natinusala.openedt.utils.UIUtils;

public class EventView extends LinearLayout
{
    TextView module;
    TextView salle;
    TextView professeurs;
    TextView date;
    TextView heure;

    public EventView(Context c)
    {
        super(c);

        //Layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(Color.WHITE);

        module = new TextView(c);
        module.setPadding(UIUtils.dp(c, 16), UIUtils.dp(c, 8), UIUtils.dp(c, 25), UIUtils.dp(c, 8));
        module.setTextSize(28);
        this.addView(module);

        heure = new TextView(c);
        heure.setTextSize(16);
        heure.setPadding(UIUtils.dp(c, 16), UIUtils.dp(c, 16), UIUtils.dp(c, 16), 0);
        heure.setTypeface(null, Typeface.BOLD);
        this.addView(heure);

        salle = new TextView(c);
        salle.setPadding(UIUtils.dp(c, 16), 0, UIUtils.dp(c, 16), 0);
        salle.setTextSize(16);
        salle.setTypeface(null, Typeface.BOLD);
        this.addView(salle);

        professeurs = new TextView(c);
        professeurs.setPadding(UIUtils.dp(c, 16), UIUtils.dp(c, 16), UIUtils.dp(c, 16), 0);
        this.addView(professeurs);

        date = new TextView(c);
        date.setPadding(UIUtils.dp(c, 16), 0, UIUtils.dp(c, 16), UIUtils.dp(c, 16));
        this.addView(date);
    }

    public EventView setData(Event event, Week week)
    {
        //Données
        Date dayDate = TimeUtils.createDateForDay(event.day, week);
        SimpleDateFormat sdf = TimeUtils.createDateFormat();

        date.setText(String.format("Le %s (semaine %d)", sdf.format(dayDate), week.num));

        module.setBackgroundColor(Color.parseColor(event.colour));
        salle.setText(String.format("En salle %s", event.getPrettyRoom()));
        heure.setText(String.format("De %s à %s", event.starttime, event.endtime));
        module.setText(event.createCategoryModule());
        professeurs.setText(String.format("Avec %s", event.getPrettyStaff()));

        return this;
    }
}
