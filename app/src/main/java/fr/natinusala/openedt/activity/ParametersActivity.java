package fr.natinusala.openedt.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import fr.natinusala.openedt.R;



public class ParametersActivity extends Activity {
    public static final String SMART_ALARM_PREF = "SMART_ALARM";
    //public static final String SMART_ALARM_TIME = "SMART_ALARM_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        Switch smartAlarm = (Switch) this.findViewById(R.id.switch_smart_alarm);
        SharedPreferences preferences = getSharedPreferences(SMART_ALARM_PREF, 0);
        boolean isEnabled = preferences.getBoolean("isEnabled", false);
        smartAlarm.setChecked(isEnabled);
        checkSmartAlarmEnabled(smartAlarm);
        smartAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch smartAlarm = (Switch) v;
                checkSmartAlarmEnabled(smartAlarm);
                SharedPreferences preferences = getSharedPreferences(SMART_ALARM_PREF, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isEnabled", smartAlarm.isChecked());
                editor.commit();
            }
        });


        //select time

        Button validateButton = (Button) findViewById(R.id.validate_time);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner hours = (Spinner) findViewById(R.id.hours_spinner);
                Spinner minutes = (Spinner) findViewById(R.id.minutes_spinner);
                setTime(hours, minutes);
            }
        });
    }

    public void setTime(Spinner hours, Spinner minutes){
        SharedPreferences preferences = getSharedPreferences(SMART_ALARM_PREF, 0);
        SharedPreferences.Editor editor = preferences.edit();
        int nb_hours = Integer.parseInt(hours.getSelectedItem().toString());
        int nb_minutes = Integer.parseInt(minutes.getSelectedItem().toString());
        int pref_time= nb_minutes + nb_hours * 60;
        editor.putInt("time", pref_time);
        editor.commit();
        String str = "Alarme réglée à";
        if(nb_hours > 0){
            str = str + " " + Integer.toString(nb_hours)+ "heure(s)";
        }
        if(nb_minutes > 0){
            str = str + " " + Integer.toString(nb_minutes) + "minute(s)";
        }
        Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
    }

    public void checkSmartAlarmEnabled(Switch smartAlarm){
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.time_picker);
        if(smartAlarm.isChecked()){

            ArrayList<String> hours = new ArrayList<String>();
            for (int i = 0; i <= 24; i++){
                hours.add(Integer.toString(i));
            }
            ArrayList<String> minutes = new ArrayList<String>();
            for (int i = 0; i <= 60; i++){
                minutes.add(Integer.toString(i));
            }
            Spinner spinnerHours = (Spinner) this.findViewById(R.id.hours_spinner);
            ArrayAdapter<String> hoursAdapter = new ArrayAdapter<String>
                    (getApplicationContext(), android.R.layout.simple_spinner_item, hours);

            hoursAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnerHours.setAdapter(hoursAdapter);

            Spinner spinnerMinutes = (Spinner) this.findViewById(R.id.minutes_spinner);
            ArrayAdapter<String> minutesAdapter = new ArrayAdapter<String>
                    (getApplicationContext(), android.R.layout.simple_spinner_item, minutes);

            hoursAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnerMinutes.setAdapter(minutesAdapter);
            layout.setVisibility(View.VISIBLE);
        }else{
            layout.setVisibility(View.GONE);
        }
    }

}
