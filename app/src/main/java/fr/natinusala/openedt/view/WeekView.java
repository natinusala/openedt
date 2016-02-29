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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class WeekView extends CardView
{
    Week week;
    Scale scale;
    LinearLayout linearLayout;
    Context c;

    ArrayList<RelativeLayout> daysLayouts = new ArrayList<>();
    
    public WeekView(Context c)
    {
        super(c);

        this.c = c;
        linearLayout = new LinearLayout(c);
        addView(linearLayout);
    }

    public WeekView setData(Week week)
    {
        this.week = week;

        scale = new Scale(week.maximumEndTimeUnits + Scale.START);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        int eventsPadding = UIUtils.dp(c, 5) + UIUtils.dp(c, 33);
        int eventsWidth = UIUtils.getScreenWidth(c) - eventsPadding - UIUtils.dp(c, 5);

        //Ajout des jours
        for (int i = 0; i < 7; i++)
        {
            RelativeLayout dayLayout = new RelativeLayout(c);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dp(c, 40));
            params.leftMargin = UIUtils.dp(c, 5);
            params.topMargin = (i == 0) ? UIUtils.dp(c, 5) : 0;
            params.bottomMargin = UIUtils.dp(c, 5);
            params.rightMargin = UIUtils.dp(c, 5);
            dayLayout.setLayoutParams(params);

            DayFrame frame = new DayFrame(c, TimeUtils.createDateForDay(i, week));
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
            EventFrame frame = new EventFrame(c, e);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(scale.scalify(e.durationUnits, eventsWidth), RelativeLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = eventsPadding + scale.scalify(e.startTimeUnits, eventsWidth);

            frame.setLayoutParams(params);

            if (e.day == 5 || e.day == 6)
            {
                daysLayouts.get(e.day).setVisibility(View.VISIBLE);
            }

            daysLayouts.get(e.day).addView(frame);
        }

        return this;
    }

    public static class EventDialog extends AlertDialog
    {
        public EventDialog(Context context, Event event, Week week)
        {
            super(context);

            //Animation et position
            getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            WindowManager.LayoutParams wmlp = getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM;
            wmlp.y = UIUtils.dp(context, 16);

            //Buttons
            this.setButton(AlertDialog.BUTTON_NEUTRAL, "Fermer", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });

            //Layout
            EventView eventView = new EventView(context);
            eventView.setData(event, week);
            setView(eventView);
        }

    }

    class EventFrame extends Button
    {
        public Event event;
        public EventFrame(final Context c, final Event e)
        {
            super(c);
            this.event = e;
            this.setGravity(Gravity.CENTER);

            this.setBackgroundColor(UIUtils.hexstringToColor(e.colour));

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EventDialog(c, event, week).show();
                }
            });

            setText(e.createCategoryModule());
            setTypeface(null, Typeface.NORMAL);
            setTextSize(8);
            setPadding(UIUtils.dp(c, 1), UIUtils.dp(c, 1), UIUtils.dp(c, 1), UIUtils.dp(c, 1));
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

            LayoutParams params = new LayoutParams(UIUtils.dp(c, 33), LayoutParams.MATCH_PARENT);
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