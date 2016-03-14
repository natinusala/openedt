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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.utils.TimeUtils;

public class EventView extends LinearLayout
{
    @Bind(R.id.eventview_module) TextView module;
    @Bind(R.id.eventview_hour) TextView heure;
    @Bind(R.id.eventview_rooms) TextView salle;
    @Bind(R.id.eventview_staff) TextView professeurs;
    @Bind(R.id.eventview_date) TextView date;

    public static int HIDE_DATE = 1;
    public static int NO_CATEGORY = 2;
    public static int CONDENSED_TEXTS = 4;

    private int flags;

    public EventView(Context c, EventViewType type)
    {
        this(c, type, 0);
    }

    public EventView(Context c, EventViewType type, int flags)
    {
        super(c);
        inflate(c, type.layout, this);
        ButterKnife.bind(this);

        this.flags = flags | type.flags;

        if (checkFlag(HIDE_DATE))
        {
            date.setVisibility(GONE);
            professeurs.setPadding(date.getPaddingLeft(), date.getPaddingTop(), date.getPaddingRight(), date.getPaddingBottom());
        }
    }

    boolean checkFlag(int flag)
    {
        return (flags & flag) == flag;
    }

    public EventView setData(Event event, Week week)
    {
        //Données
        Date dayDate = TimeUtils.createDateForDay(event.day, week);
        SimpleDateFormat sdf = TimeUtils.createDateFormat();

        if (checkFlag(CONDENSED_TEXTS))
        {
            date.setText(String.format("Le %s", sdf.format(dayDate)));
            professeurs.setText(event.getPrettyStaff());
        }
        else
        {
            date.setText(String.format("Le %s (semaine %d)", sdf.format(dayDate), week.num));
            professeurs.setText(String.format("Avec %s", event.getPrettyStaff()));
        }

        module.setBackgroundColor(Color.parseColor(event.colour));
        salle.setText(String.format("En salle %s", event.getPrettyRoom()));
        heure.setText(String.format("De %s à %s", event.starttime, event.endtime));
        module.setText(event.createCategoryModule(!checkFlag(NO_CATEGORY)));

        return this;
    }

    public enum EventViewType
    {
        REGULAR(R.layout.eventview_regular, 0),
        CONDENSED(R.layout.eventview_condensed, NO_CATEGORY | CONDENSED_TEXTS);

        public int layout;
        public int flags;

        EventViewType(int layout, int flags) {
            this.layout = layout;
            this.flags = flags;
        }
    }
}
