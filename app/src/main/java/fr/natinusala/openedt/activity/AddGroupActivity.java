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

package fr.natinusala.openedt.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Component;
import fr.natinusala.openedt.manager.GroupManager;


public class AddGroupActivity extends AppCompatActivity {

    private GroupManager groupManager;
    private AddGroupActivity instance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ArrayList<String> spinnerArray = new ArrayList<>();
        for (Component c : Component.values())
        {
            spinnerArray.add(c.name);
        }

        android.widget.Spinner spinner = (android.widget.Spinner) findViewById(R.id.component_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String urlSelected = getURLSelected(parent.getSelectedItem().toString());
                callTask(urlSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String urlSelected = getURLSelected(parent.getSelectedItem().toString());
                callTask(urlSelected);
            }
        });
    }

    public void callTask(String urlSelected){
        new Task().execute(urlSelected);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public String getURLSelected(String componentSelected){
        String str = "";
        for(Component c : Component.values()){
            if(c.name.equals(componentSelected)){
                str = c.groups_url;
                break;
            }
        }
        return str;
    }

    public void fillGroupSpinner(){
        //Show group cards and submit button
        CardView cardView = (CardView) findViewById(R.id.branchCardView);
        cardView.setVisibility(View.VISIBLE);
        Button submit = (Button) findViewById(R.id.save_group);
        submit.setVisibility(View.VISIBLE);
        final android.widget.Spinner spinnerGroup = (android.widget.Spinner) findViewById(R.id.group_spinner);
        ArrayList<String> groups = groupManager.getKeys();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupManager.setGroup(spinnerGroup.getSelectedItem().toString());
                finish();
            }
        });
    }



    private class Task extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... url) {
            try {
                groupManager = new GroupManager(instance, url[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            fillGroupSpinner();
        }


    }
}
