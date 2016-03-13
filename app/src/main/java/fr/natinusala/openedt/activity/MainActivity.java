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

package fr.natinusala.openedt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.text.WordUtils;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.data.WrapperEventWeek;
import fr.natinusala.openedt.manager.DataManager;
import fr.natinusala.openedt.manager.GroupManager;
import fr.natinusala.openedt.manager.PebbleManager;
import fr.natinusala.openedt.manager.WeekManager;
import fr.natinusala.openedt.utils.TimeUtils;
import fr.natinusala.openedt.utils.UIUtils;
import fr.natinusala.openedt.view.EventView;
import fr.natinusala.openedt.view.WeekView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String INTENT_SELECT_LAST_ONE = "slo";

    public static int TABS_COUNT = 3;

    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.main_pager) ViewPager viewPager;
    @Bind(R.id.main_root) DrawerLayout drawer;
    @Bind(R.id.main_progressBar) ProgressBar progressBar;

    PebbleManager pebbleManager;

    ArrayList<Group> groups;
    Group selectedGroup;
    ArrayList<Week> weeks;

    TabsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.setTitle("OpenEDT");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        viewPager.setOffscreenPageLimit(TABS_COUNT - 1);

        pebbleManager = new PebbleManager(this);

        //Refresh
        refresh(true);
    }

    public static int DRAWER_GROUP_ID = 42;

    public void refresh(boolean loadFromFile)
    {
        //Chargement de la liste des groupes
        if (loadFromFile) {
            groups = GroupManager.readGroups(this);
        }

        if (groups.isEmpty()) {
            this.startActivity(new Intent(this, AddGroupActivity.class));
            finish();
            return;
        }

        SubMenu subMenu = navigationView.getMenu().getItem(0).getSubMenu();
        subMenu.clear();

        for (int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            subMenu.add(DRAWER_GROUP_ID, i, Menu.FIRST, g.name);
        }
        subMenu.setGroupCheckable(DRAWER_GROUP_ID, true, true);

        navigationView.invalidate();

        Group lastSelectedGroup = GroupManager.getSelectedGroup(this);
        //Si on a un SELECT_LAST_ONE de défini
        if (getIntent().getBooleanExtra(INTENT_SELECT_LAST_ONE, false))
        {
            selectGroup(groups.get(groups.size()-1));
        }
        else if (lastSelectedGroup != null && groups.contains(lastSelectedGroup))
        {
            selectGroup(lastSelectedGroup);
        }
        else
        {
            selectGroup(groups.get(0));
        }
    }

    void selectGroup(Group g)
    {
        if (selectedGroup != null && groups.contains(selectedGroup))
        {
            navigationView.getMenu().getItem(0).getSubMenu().findItem(groups.indexOf(selectedGroup)).setChecked(false);
        }
        navigationView.getMenu().getItem(0).getSubMenu().findItem(groups.indexOf(g)).setChecked(true);
        selectedGroup = g;
        GroupManager.saveSelectedGroup(this, g);
        loadSelectedGroup();
    }

    void loadSelectedGroup()
    {
        this.setTitle(selectedGroup.name);

        //Chargement de la liste des semaines
        new Task().execute();
    }


    class Task extends AsyncTask<Void, Void, Boolean>
    {


        @Override
        protected void onPreExecute()
        {
            viewPager.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            viewPager.setAdapter(null);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            ArrayList<Week> data = DataManager.getWeeksForGroup(MainActivity.this, selectedGroup);

            if (data != null)
            {
                weeks = new ArrayList<>(data);
                return true;
            }
            else
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if (result)
            {
                //Affichage des données
                adapter = new TabsAdapter(getSupportFragmentManager());
                viewPager.setAdapter(adapter);

                pebbleManager.setWeekList(weeks);

                viewPager.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else
            {
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.main_root), "Impossible de charger les données.", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Réessayer", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Task().execute();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_group)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Attention !");
            dialog.setMessage("Etes-vous sûr de vouloir supprimer ce groupe ?");
            dialog.setNegativeButton("Non", null);
            dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    groups.remove(selectedGroup);
                    GroupManager.saveGroups(MainActivity.this, groups.toArray(new Group[groups.size()]));
                    refresh(false);
                }
            });
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (item.getGroupId() == DRAWER_GROUP_ID)
        {
            selectGroup(groups.get(item.getItemId()));
        }
        else if (item.getItemId() == R.id.add_group)
        {
            this.startActivity(new Intent(this, AddGroupActivity.class));
        }
        else if (item.getItemId() == R.id.about)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("A propos d'OpenEDT");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage(Html.fromHtml("OpenEDT est une application libre de consultation des emplois du temps universitaires.<br><br>L'application est distribuée sous la licence libre <a href='http://www.apache.org/licenses/LICENSE-2.0'>Apache 2.0</a>.<br><br>Participez au projet depuis notre <a href='https://github.com/natinusala/openedt'>dépôt GitHub</a> !"));
            builder.setNeutralButton("Fermer", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class TabsAdapter extends FragmentPagerAdapter
    {
        public TabsAdapter(FragmentManager fm) {
            super(fm);

            if (fm.getFragments() != null) {
                fm.getFragments().clear();
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position)
        {
            Bundle bundle = new Bundle();
            bundle.putString(WeeksFragment.BUNDLE_WEEKS, new Gson().toJson(weeks, new TypeToken<ArrayList<Week>>(){}.getType()));
            switch (position)
            {
                case 0:
                    return Fragment.instantiate(MainActivity.this, HomeFragment.class.getName(), bundle);
                case 1:
                    return Fragment.instantiate(MainActivity.this, DaysFragment.class.getName(), bundle);
                case 2:
                    return Fragment.instantiate(MainActivity.this, WeeksFragment.class.getName(), bundle);
            }

            return new Fragment();
        }

        @Override
        public int getCount() {
            return TABS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ACCUEIL";
                case 1:
                    return "JOURS";
                case 2:
                    return "SEMAINES";
            }
            return null;
        }
    }

    public static class HomeFragment extends Fragment
    {
        @Bind(R.id.home_week) TextView week;
        @Bind(R.id.home_date) TextView date;

        @Bind(R.id.home_container) LinearLayout daysContainer;

        public static final String BUNDLE_WEEKS = "weeks";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ArrayList<Week> weeks = new Gson().fromJson(getArguments().getString(BUNDLE_WEEKS, ""), new TypeToken<ArrayList<Week>>() {}.getType());


            View view = inflater.inflate(R.layout.activity_main_home_fragment, container, false);
            ButterKnife.bind(this, view);

            SimpleDateFormat format = TimeUtils.createLongDateFormat();
            Calendar cal = Calendar.getInstance();

            date.setText(WordUtils.capitalize(format.format(new Date())));
            week.setText("Semaine " + cal.get(Calendar.WEEK_OF_YEAR));

            ArrayList<WrapperEventWeek> wrappers = WeekManager.getNextEvents(weeks);

            for (WrapperEventWeek wrap : wrappers)
            {
                CardView card = new CardView(getContext());

                EventView eventView = new EventView(getContext());

                eventView.setData(wrap.getEvent(), wrap.getWeek());
                card.addView(eventView);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(UIUtils.dp(getContext(), 20), UIUtils.dp(getContext(), 10), UIUtils.dp(getContext(), 20), UIUtils.dp(getContext(), 10));
                card.setLayoutParams(params);

                daysContainer.addView(card);
            }

            return view;
        }
    }

    public static class DaysFragment extends Fragment {
        @Bind(R.id.days_list) StickyListHeadersListView list;

        public static final String BUNDLE_WEEKS = "weeks";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ArrayList<Week> weeks = new Gson().fromJson(getArguments().getString(BUNDLE_WEEKS, ""), new TypeToken<ArrayList<Week>>() {}.getType());
            Week currentWeek = WeekManager.getCurrentWeek(weeks);

            View root = inflater.inflate(R.layout.activity_main_days_fragment, container, false);

            ButterKnife.bind(this, root);

            list.setAdapter(new DaysAdapter(currentWeek));
            list.setDivider(null);
            list.setDividerHeight(0);

            return root;
        }

        class DaysAdapter extends BaseAdapter implements StickyListHeadersAdapter
        {
            ArrayList<Event> events;
            ArrayList<Integer> headersId;

            Week currentWeek;

            Format formater = TimeUtils.createLongDateFormat();

            public DaysAdapter(Week currentWeek)
            {
                this.events = new ArrayList<>();
                this.headersId = new ArrayList<>();
                this.currentWeek = currentWeek;

                ArrayList<ArrayList<Event>> eventsPerDay = WeekManager.getEventPerDay(currentWeek);

                if (eventsPerDay != null)
                {
                    int currentHeaderId = 0;
                    for (ArrayList<Event> day : eventsPerDay)
                    {
                        for (Event e : day)
                        {
                            events.add(e);
                            headersId.add(currentHeaderId);
                        }
                        currentHeaderId++;
                    }
                }
            }

            @Override
            public View getHeaderView(int position, View convertView, ViewGroup parent) {
                TextView titleDate = new TextView(getActivity());
                titleDate.setGravity(Gravity.CENTER_HORIZONTAL);
                titleDate.setTextSize(16);
                titleDate.setTypeface(titleDate.getTypeface(), Typeface.BOLD);
                int padding = UIUtils.dp(getActivity(), 10);
                titleDate.setPadding(0, padding, 0, padding);
                titleDate.setBackgroundColor(Color.parseColor("#33B5E5"));
                titleDate.setTextColor(Color.WHITE);
                titleDate.setText(formater.format(TimeUtils.createDateForDay(events.get(position).day, currentWeek)).toUpperCase());
                return titleDate;
            }

            @Override
            public long getHeaderId(int position) {
                return headersId.get(position);
            }

            @Override
            public int getCount() {
                return events.size();
            }

            @Override
            public Object getItem(int position) {
                return events.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                EventView eventView = new EventView(getActivity(), true).setData(events.get(position), currentWeek);
                android.support.v7.widget.CardView card = new android.support.v7.widget.CardView(getContext());
                card.addView(eventView);

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(card);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                int margin = UIUtils.dp(getActivity(), 10);
                params.setMargins(margin * 2, margin, margin * 2, margin);
                card.setLayoutParams(params);

                return layout;
            }
        }
    }

    public static class WeeksFragment extends Fragment
    {
        @Bind(R.id.weeks_container) LinearLayout weeksContainer;

        public static final String BUNDLE_WEEKS = "weeks";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ArrayList<Week> weeks = new Gson().fromJson(getArguments().getString(BUNDLE_WEEKS, ""), new TypeToken<ArrayList<Week>>() {
            }.getType());
            View root = inflater.inflate(R.layout.activity_main_weeks_fragment, container, false);
            ButterKnife.bind(this, root);

            Calendar cal = Calendar.getInstance();
            int weekCal = cal.get(Calendar.WEEK_OF_YEAR) + 1;
            int week = TimeUtils.getIdWeek(weekCal);

            weeksContainer.addView(new WeekView(getActivity()).setData(weeks.get(week - 1)));
            weeksContainer.addView(new WeekView(getActivity()).setData(weeks.get(week)));
            weeksContainer.addView(new WeekView(getActivity()).setData(weeks.get(week + 1)));

            return root;
        }
    }
}
