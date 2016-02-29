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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Event;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;
import fr.natinusala.openedt.manager.DataManager;
import fr.natinusala.openedt.manager.GroupManager;
import fr.natinusala.openedt.manager.PebbleManager;
import fr.natinusala.openedt.manager.WeekManager;
import fr.natinusala.openedt.utils.TimeUtils;
import fr.natinusala.openedt.view.EventView;
import fr.natinusala.openedt.view.WeekView;

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
        TabsAdapter adapter;

        @Override
        protected void onPreExecute()
        {
            viewPager.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            ArrayList<Week> data = DataManager.getWeeksForGroup(MainActivity.this, selectedGroup);

            if (data != null)
            {
                weeks = data;
                adapter = new TabsAdapter(getSupportFragmentManager());
                pebbleManager.setWeekList(data);
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
                viewPager.setAdapter(adapter);

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
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return Fragment.instantiate(MainActivity.this, HomeFragment.class.getName());
                case 1:
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(WeeksFragment.BUNDLE_WEEKS, new Gson().toJson(weeks, new TypeToken<ArrayList<Week>>(){}.getType()));
                    return Fragment.instantiate(MainActivity.this, DaysFragment.class.getName(), bundle1);
                case 2:
                    Bundle bundle2 = new Bundle();
                    bundle2.putString(WeeksFragment.BUNDLE_WEEKS, new Gson().toJson(weeks, new TypeToken<ArrayList<Week>>(){}.getType()));
                    return Fragment.instantiate(MainActivity.this, WeeksFragment.class.getName(), bundle2);
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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.activity_main_home_fragment, container, false);
        }
    }

    public static class DaysFragment extends Fragment {
        public static final String BUNDLE_WEEKS = "weeks";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ArrayList<Week> weeks = new Gson().fromJson(getArguments().getString(BUNDLE_WEEKS, ""), new TypeToken<ArrayList<Week>>() {
            }.getType());
            Week currentWeek = WeekManager.getCurrentWeek(weeks);
            Format formater = TimeUtils.createDateFormat();

            View root = inflater.inflate(R.layout.activity_main_days_fragment, container, false);
            LinearLayout daysContainer = (LinearLayout) root.findViewById(R.id.days_container);


            ArrayList<ArrayList<Event>> days = WeekManager.getEventPerDay(currentWeek);
            if (days != null) {
                for (ArrayList<Event> day : days) {
                    if (day != null) {

                        int dayNumber = day.get(0).day;
                        TextView titleDate = new TextView(getActivity());
                        titleDate.setGravity(Gravity.CENTER_HORIZONTAL);
                        titleDate.setTextSize(16);
                        titleDate.setTypeface(null, Typeface.BOLD);
                        titleDate.setText(formater.format(TimeUtils.createDateForDay(dayNumber, currentWeek)));
                        daysContainer.addView(titleDate);

                        for (Event e : day) {
                            daysContainer.addView(new EventView(getActivity()).setData(e, currentWeek));
                        }

                    }
                }
            }

            return root;
        }
    }

    public static class WeeksFragment extends Fragment
    {
        public static final String BUNDLE_WEEKS = "weeks";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ArrayList<Week> weeks = new Gson().fromJson(getArguments().getString(BUNDLE_WEEKS, ""), new TypeToken<ArrayList<Week>>(){}.getType());
            View root = inflater.inflate(R.layout.activity_main_weeks_fragment, container, false);

            LinearLayout weeksContainer = (LinearLayout) root.findViewById(R.id.weeks_container);

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
