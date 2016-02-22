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

package fr.natinusala.openedt.fragment;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;
import fr.natinusala.openedt.utils.UIUtils;

public class EventFragment extends Fragment
{
    public static final String BUNDLE_EVENT = "event";
    public static final String BUNDLE_WEEK = "week";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Event
        Event event = new Gson().fromJson(getArguments().getString(BUNDLE_EVENT), Event.class);
        Week week = new Gson().fromJson(getArguments().getString(BUNDLE_WEEK), Week.class);

        //Layout
        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);

        LinearLayout encart = new LinearLayout(getActivity());
        encart.setOrientation(LinearLayout.VERTICAL);
        encart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        encart.setPadding(UIUtils.dp(getActivity(), 16), UIUtils.dp(getActivity(), 8), UIUtils.dp(getActivity(), 25), UIUtils.dp(getActivity(), 8));
        layout.addView(encart);

        TextView module = new TextView(getActivity());
        module.setTextSize(28);
        encart.addView(module);

        TextView heure = new TextView(getActivity());
        heure.setTextSize(16);
        heure.setPadding(UIUtils.dp(getActivity(), 16), UIUtils.dp(getActivity(), 16), UIUtils.dp(getActivity(), 16), 0);
        heure.setTypeface(null, Typeface.BOLD);
        layout.addView(heure);

        TextView salle = new TextView(getActivity());
        salle.setPadding(UIUtils.dp(getActivity(), 16), 0, UIUtils.dp(getActivity(), 16), 0);
        salle.setTextSize(16);
        salle.setTypeface(null, Typeface.BOLD);
        layout.addView(salle);

        TextView professeurs = new TextView(getActivity());
        professeurs.setPadding(UIUtils.dp(getActivity(), 16), UIUtils.dp(getActivity(), 16), UIUtils.dp(getActivity(), 16), 0);
        layout.addView(professeurs);

        TextView date = new TextView(getActivity());
        date.setPadding(UIUtils.dp(getActivity(), 16), 0, UIUtils.dp(getActivity(), 16), UIUtils.dp(getActivity(), 16));
        layout.addView(date);

        //Données
        Date dayDate = TimeUtils.createDateForDay(event.day, week);
        SimpleDateFormat sdf = TimeUtils.createDateFormat();

        date.setText(String.format("Le %s (semaine %d)", sdf.format(dayDate), week.num));

        encart.setBackgroundColor(Color.parseColor(event.colour));
        salle.setText("En salle " + event.getPrettyRoom());
        heure.setText("De " + event.starttime + " à " + event.endtime);
        module.setText(event.createCategoryModule());
        professeurs.setText("Avec " + event.getPrettyStaff());

        return layout;
    }
}
