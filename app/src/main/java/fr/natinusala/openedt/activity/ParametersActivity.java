package fr.natinusala.openedt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

import fr.natinusala.openedt.R;

public class ParametersActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        Switch smartAlarm = (Switch) this.findViewById(R.id.switch_smart_alarm);
        smartAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSmartAlarmEnabled((Switch) v);
            }
        });
    }

    public void checkSmartAlarmEnabled(Switch smartAlarm){
        if(smartAlarm.isActivated()){
            LinearLayout layout = (LinearLayout) this.findViewById(R.id.time_picker);
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
        }
    }

}
