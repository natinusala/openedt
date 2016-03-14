package fr.natinusala.openedt.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.data.WrapperEventWeek;
import fr.natinusala.openedt.manager.DataManager;
import fr.natinusala.openedt.manager.GroupManager;
import fr.natinusala.openedt.manager.WeekManager;
import fr.natinusala.openedt.utils.TimeUtils;
import fr.natinusala.openedt.utils.UIUtils;

public class WidgetProvider extends AppWidgetProvider
{
    //TODO Configuration du groupe et de l'intervalle
    //TODO Actualiser au toucher
    //TODO Améliorer la taille par défaut (widget_provider_info.xml) - le système de cellules/dp est naze

    @Override
    public void onUpdate (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int wId : appWidgetIds)
        {
            Group group = GroupManager.getSelectedGroup(context);
            new Task(group, context, appWidgetManager, wId).execute();
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions)
    {
        Group group = GroupManager.getSelectedGroup(context);
        new Task(group, context, appWidgetManager, appWidgetId).execute();
    }

    class Task extends AsyncTask<Void, Void, ArrayList<Week>>
    {
        //TODO Spinner

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
            Bundle options = appWidgetManager.getAppWidgetOptions(wId);

            int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

            RemoteViews views;

            if (width >= 250 && height >= 110)
            {
                views = new RemoteViews(context.getPackageName(), R.layout.eventview_regular);
            }
            else
            {
                views = new RemoteViews(context.getPackageName(), R.layout.eventview_condensed);
            }
            int padding = UIUtils.dp(context, 5);
            if (width > 210)
            {
                views.setViewVisibility(R.id.eventview_group, View.VISIBLE);
                views.setViewPadding(R.id.eventview_module, padding, 0, padding, padding);
            }
            else
            {
                views.setViewVisibility(R.id.eventview_group, View.GONE);
                views.setViewPadding(R.id.eventview_module, padding, padding, padding, padding);
            }

            appWidgetManager.updateAppWidget(wId, views);

            if (weeks != null)
            {

                ArrayList<WrapperEventWeek> wrappers = WeekManager.getNextEvents(weeks);
                Event event = wrappers.get(0).getEvent();
                Week week = wrappers.get(0).getWeek();
                if (event != null)
                {
                    Date dayDate = TimeUtils.createDateForDay(event.day, week);
                    SimpleDateFormat sdf = TimeUtils.createDateFormat();

                    views.setTextViewText(R.id.eventview_date, String.format("Le %s (semaine %d)", sdf.format(dayDate), week.num));
                    views.setInt(R.id.eventview_title, "setBackgroundColor", Color.parseColor(event.colour));
                    views.setTextViewText(R.id.eventview_rooms, String.format("En salle %s", event.getPrettyRoom()));
                    views.setTextViewText(R.id.eventview_hour, String.format("De %s à %s", event.starttime, event.endtime));
                    views.setTextViewText(R.id.eventview_module, event.createCategoryModule());
                    views.setTextViewText(R.id.eventview_staff, event.getPrettyStaff());
                    views.setTextViewText(R.id.eventview_group, group.name + " - prochain cours :");
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
