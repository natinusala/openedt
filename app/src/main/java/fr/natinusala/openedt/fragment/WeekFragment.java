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

package fr.natinusala.openedt.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

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
public class WeekFragment extends Fragment
{
    private Week week;
    Scale scale;

    ArrayList<RelativeLayout> daysLayouts = new ArrayList<>();

    public static final String BUNDLE_WEEK = "week";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Données
        week = new Gson().fromJson(getArguments().getString(BUNDLE_WEEK), Week.class);

        CardView root = new CardView(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());

        scale = new Scale(week.maximumEndTimeUnits + Scale.START);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        int eventsPadding = UIUtils.dp(getActivity(), 5) + UIUtils.dp(getActivity(), 33);
        int eventsWidth = UIUtils.getScreenWidth(getActivity()) - eventsPadding - UIUtils.dp(getActivity(), 5);

        //Ajout des jours
        for (int i = 0; i < 7; i++)
        {
            RelativeLayout dayLayout = new RelativeLayout(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dp(getActivity(), 40));
            params.leftMargin = UIUtils.dp(getActivity(), 5);
            params.topMargin = (i == 0) ? UIUtils.dp(getActivity(), 5) : 0;
            params.bottomMargin = UIUtils.dp(getActivity(), 5);
            params.rightMargin = UIUtils.dp(getActivity(), 5);
            dayLayout.setLayoutParams(params);

            DayFrame frame = new DayFrame(getActivity(), TimeUtils.createDateForDay(i, week));
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
            EventFrame frame = new EventFrame(getActivity(), e);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(scale.scalify(e.durationUnits, eventsWidth), RelativeLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = eventsPadding + scale.scalify(e.startTimeUnits, eventsWidth);

            frame.setLayoutParams(params);

            if (e.day == 5 || e.day == 6)
            {
                daysLayouts.get(e.day).setVisibility(View.VISIBLE);
            }

            daysLayouts.get(e.day).addView(frame);
        }

        root.addView(linearLayout);
        return root;
    }

    public static class EventDialog extends AppCompatDialogFragment
    {
        public static final String BUNDLE_EVENT = "event";
        public static final String BUNDLE_WEEK = "week";

        LinearLayout layout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            //Layout
            layout = new LinearLayout(getActivity());
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.setId(R.id.event_fragment_layout_id);

            //Data
            Event event = new Gson().fromJson(getArguments().getString(EventDialog.BUNDLE_EVENT), Event.class);
            Week week = new Gson().fromJson(getArguments().getString(EventDialog.BUNDLE_WEEK), Week.class);

            Gson gson = new Gson();
            Bundle bundle = new Bundle();
            bundle.putString(EventFragment.BUNDLE_EVENT, gson.toJson(event, Event.class));
            bundle.putString(EventFragment.BUNDLE_WEEK, gson.toJson(week, Week.class));

            Fragment fragment = Fragment.instantiate(getActivity(), EventFragment.class.getName());
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(layout.getId(), fragment);
            transaction.commit();

            //Animation and position
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM;
            wmlp.y = UIUtils.dp(getActivity(), 16);

            //Buttons
            ((AlertDialog)getDialog()).setButton(AlertDialog.BUTTON_NEUTRAL, "Fermer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });

            return layout;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            return new AlertDialog.Builder(getActivity()).create();
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
                    EventDialog dialog = new EventDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString(EventDialog.BUNDLE_EVENT, new Gson().toJson(event, Event.class));
                    bundle.putString(EventDialog.BUNDLE_WEEK, new Gson().toJson(week, Week.class));
                    dialog.setArguments(bundle);
                    dialog.show(getChildFragmentManager(), "dialog");
                }
            });

            setText(e.createCategoryModule());
            setTypeface(null, Typeface.NORMAL);
            setTextSize(8);
            setPadding(UIUtils.dp(getActivity(), 1), UIUtils.dp(getActivity(), 1), UIUtils.dp(getActivity(), 1), UIUtils.dp(getActivity(), 1));
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

            LayoutParams params = new LayoutParams(UIUtils.dp(getActivity(), 33), LayoutParams.MATCH_PARENT);
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