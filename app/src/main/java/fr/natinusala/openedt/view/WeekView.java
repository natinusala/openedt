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

package fr.natinusala.openedt.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Scale;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;
import fr.natinusala.openedt.utils.UIUtils;

@SuppressLint("ViewConstructor")
public class WeekView extends CardView
{
    private Week week;
    Scale scale;

    Context context;

    ArrayList<RelativeLayout> daysLayouts = new ArrayList<>();

    int dp(int value)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public WeekView(Context context, Week week)
    {
        super(context);

        LinearLayout linearLayout = new LinearLayout(context);

        this.week = week;
        this.context = context;

        scale = new Scale(week.maximumEndTimeUnits + Scale.START);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        int eventsPadding = dp(5) + dp(33);
        int eventsWidth = UIUtils.getScreenWidth(context) - eventsPadding - dp(5);

        //Ajout des jours
        for (int i = 0; i < 7; i++)
        {
            RelativeLayout dayLayout = new RelativeLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(40));
            params.leftMargin = dp(5);
            params.topMargin = (i == 0) ? dp(5) : 0;
            params.bottomMargin = dp(5);
            params.rightMargin = dp(5);
            dayLayout.setLayoutParams(params);

            DayFrame frame = new DayFrame(context, TimeUtils.createDateForDay(i, week));
            dayLayout.addView(frame);

            if (i == 5 || i == 6)
            {
                dayLayout.setVisibility(View.GONE);
            }

            daysLayouts.add(dayLayout);
            linearLayout.addView(dayLayout);
        }

        //Ajout des events
        for (Event e : week.events)
        {
            EventFrame frame = new EventFrame(context, e);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(scale.scalify(e.durationUnits, eventsWidth), RelativeLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = eventsPadding + scale.scalify(e.startTimeUnits, eventsWidth);

            frame.setLayoutParams(params);

            if (e.day == 5 || e.day == 6)
            {
                daysLayouts.get(e.day).setVisibility(View.VISIBLE);
            }

            daysLayouts.get(e.day).addView(frame);
        }

        this.addView(linearLayout);
    }

    class EventDialog extends AlertDialog
    {
        @SuppressLint("SetTextI18n")
        public EventDialog(Context context, Event event)
        {
            super(context);

            //Animation et position
            getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            WindowManager.LayoutParams wmlp = getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM;
            wmlp.y = dp(16);

            //Layout
            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(params);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setBackgroundColor(Color.WHITE);

            LinearLayout encart = new LinearLayout(context);
            encart.setOrientation(LinearLayout.VERTICAL);
            encart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            encart.setPadding(dp(16), dp(8), dp(25), dp(8));
            layout.addView(encart);

            TextView module = new TextView(context);
            module.setTextSize(28);
            encart.addView(module);

            TextView heure = new TextView(context);
            heure.setTextSize(16);
            heure.setPadding(dp(16), dp(16), dp(16), 0);
            heure.setTypeface(null, Typeface.BOLD);
            layout.addView(heure);

            TextView salle = new TextView(context);
            salle.setPadding(dp(16), 0, dp(16), 0);
            salle.setTextSize(16);
            salle.setTypeface(null, Typeface.BOLD);
            layout.addView(salle);

            TextView professeurs = new TextView(context);
            professeurs.setPadding(dp(16), dp(16), dp(16), 0);
            layout.addView(professeurs);

            TextView date = new TextView(context);
            date.setPadding(dp(16), 0, dp(16), dp(16));
            layout.addView(date);

            //Buttons
            this.setButton(AlertDialog.BUTTON_NEUTRAL, "Fermer", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });

            //Données
            Date dayDate = TimeUtils.createDateForDay(event.day, week);
            SimpleDateFormat sdf = TimeUtils.createDateFormat();

            date.setText("Le " + sdf.format(dayDate) + " (semaine " + week.num + ")");

            encart.setBackgroundColor(Color.parseColor(event.colour));
            salle.setText("En salle " + event.getPrettyRoom());
            heure.setText("De " + event.starttime + " à " + event.endtime);
            module.setText(event.createCategoryModule());
            professeurs.setText("Avec " + event.getPrettyStaff());

            setView(layout);
        }
    }

    class EventFrame extends Button
    {
        public Event event;
        public EventFrame(Context c, final Event e)
        {
            super(c);
            this.event = e;
            this.setGravity(Gravity.CENTER);

            this.setBackgroundColor(UIUtils.hexstringToColor(e.colour));

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EventDialog(context, e).show();
                }
            });

            setText(e.createCategoryModule());
            setTypeface(null, Typeface.NORMAL);
            setTextSize(8);
            setPadding(dp(1), dp(1), dp(1), dp(1));
            setTransformationMethod(null);
        }
    }

    class DayFrame extends LinearLayout
    {
        @SuppressLint("SetTextI18n")
        public DayFrame(Context c, Date day)
        {
            super(c);
            this.setOrientation(VERTICAL);

            LayoutParams params = new LayoutParams(dp(33), LayoutParams.MATCH_PARENT);
            this.setLayoutParams(params);

            this.setGravity(Gravity.CENTER);

            Calendar cal = Calendar.getInstance();
            cal.setTime(day);

            TextView dayView = new TextView(c);
            dayView.setTextSize(10);
            dayView.setGravity(Gravity.CENTER);
            dayView.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.FRANCE));

            TextView dateView = new TextView(c);
            dateView.setGravity(Gravity.CENTER);
            dateView.setTextSize(12);
            dateView.setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));

            if (TimeUtils.isToday(day))
            {
                dayView.setTextColor(Color.WHITE);
                dateView.setTextColor(Color.WHITE);
                this.setBackgroundColor(Color.DKGRAY);
            }
            else
            {
                this.setBackgroundColor(Color.LTGRAY);
            }

            this.addView(dayView);
            this.addView(dateView);
        }
    }
}