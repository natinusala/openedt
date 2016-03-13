package fr.natinusala.openedt.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Component;
import fr.natinusala.openedt.data.DataSourceType;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.data.WrapperEventWeek;
import fr.natinusala.openedt.manager.DataManager;
import fr.natinusala.openedt.manager.GroupManager;
import fr.natinusala.openedt.manager.WeekManager;
import fr.natinusala.openedt.utils.TimeUtils;

public class WidgetProvider extends AppWidgetProvider
{
    //TODO Configuration du groupe et de l'intervalle
    //TODO Actualiser au toucher

    @Override
    public void onUpdate (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int wId : appWidgetIds)
        {
            Group group = GroupManager.getSelectedGroup(context);
            new Task(group, context, appWidgetManager, wId).execute();
        }
    }

    class Task extends AsyncTask<Void, Void, ArrayList<Week>>
    {
        Context context;
        AppWidgetManager appWidgetManager;
        int wId;
        Group group;

        public Task(Group g, Context c, AppWidgetManager manager, int wid)
        {
            this.group = g;
            this.context = c;
            this.appWidgetManager = manager;
            this.wId = wid;
        }

        @Override
        protected ArrayList<Week> doInBackground(Void... params) {
            return DataManager.getWeeksForGroup(context, group);
        }

        @Override
        protected void onPostExecute(ArrayList<Week> weeks)
        {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            if (weeks != null)
            {

                ArrayList<WrapperEventWeek> wrappers = WeekManager.getNextEvents(weeks);
                Event event = wrappers.get(0).getEvent();
                Week week = wrappers.get(0).getWeek();
                if (event != null)
                {
                    Date dayDate = TimeUtils.createDateForDay(event.day, week);
                    SimpleDateFormat sdf = TimeUtils.createDateFormat();

                    views.setTextViewText(R.id.widget_date, String.format("Le %s (semaine %d)", sdf.format(dayDate), week.num));
                    views.setInt(R.id.widget_title, "setBackgroundColor", Color.parseColor(event.colour));
                    views.setTextViewText(R.id.widget_rooms, String.format("En salle %s", event.getPrettyRoom()));
                    views.setTextViewText(R.id.widget_hour, String.format("De %s à %s", event.starttime, event.endtime));
                    views.setTextViewText(R.id.widget_module, event.createCategoryModule());
                    views.setTextViewText(R.id.widget_staff, String.format("Avec %s", event.getPrettyStaff()));
                    views.setTextViewText(R.id.widget_group, group.name + " - prochain cours :");
                }
            }
            else
            {
                Toast.makeText(context, "Impossible d'actualiser le widget d'OpenEDT", Toast.LENGTH_LONG).show();
            }

            appWidgetManager.updateAppWidget(wId, views);
        }
    }

}
