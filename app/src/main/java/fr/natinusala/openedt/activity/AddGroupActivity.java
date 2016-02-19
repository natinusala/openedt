package fr.natinusala.openedt.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import fr.natinusala.openedt.R;
import fr.natinusala.openedt.data.Component;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.manager.GroupManager;

public class AddGroupActivity extends AppCompatActivity
{
    //TODO Ajouter des barres de recherche

    Spinner componentSpinner;
    Spinner groupSpinner;
    CardView groupCard;
    Button valider;
    ArrayAdapter<String> groupAdapter;

    Group[] groups;

    ArrayList<Group> addedGroups;

    AddGroupActivity instance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle("Ajouter un groupe");

        //GroupSpinner
        groupSpinner = (Spinner) this.findViewById(R.id.groupSpinner);
        groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);

        groupCard = (CardView) this.findViewById(R.id.groupCard);
        valider = (Button) this.findViewById(R.id.addGroupButton);

        //Composante
        componentSpinner = (Spinner) this.findViewById(R.id.componentSpinner);
        componentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new Task().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                new Task().execute();
            }
        });
        ArrayAdapter<String> componentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);

        for (Component c : Component.values())
        {
            componentAdapter.add(c.name);
        }

        componentSpinner.setAdapter(componentAdapter);

        addedGroups = new ArrayList<>(Arrays.asList(GroupManager.readGroups(this)));

        //Bouton
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group selectedGroup = groups[groupSpinner.getSelectedItemPosition()];
                if (addedGroups.contains(selectedGroup))
                {
                    final Snackbar snack = Snackbar.make(findViewById(R.id.add_group_root), "Ce groupe a déjà été ajouté.", Snackbar.LENGTH_LONG);
                    snack.show();
                }
                else
                {
                    GroupManager.addGroup(instance, selectedGroup);
                    Intent intent = new Intent(instance, MainActivity.class);
                    intent.putExtra(MainActivity.INTENT_SELECT_LAST_ONE, true);
                    startActivity(intent);
                    finish();
                }

            }
        });

        new Task().execute();
    }

    class Task extends AsyncTask<Void, Void, Boolean>
    {
        Component selectedComponent;

        @Override
        protected void onPreExecute()
        {
            groupCard.setVisibility(View.INVISIBLE);
            valider.setVisibility(View.INVISIBLE);
            selectedComponent = Component.values()[componentSpinner.getSelectedItemPosition()];
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            try
            {
                groups = selectedComponent.sourceType.adapter.getGroupsList(selectedComponent);
                return true;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if (result)
            {
                groupAdapter.clear();
                for (Group g : groups)
                {
                    groupAdapter.add(g.name);
                }

                groupCard.setVisibility(View.VISIBLE);
                valider.setVisibility(View.VISIBLE);
            }
            else
            {
                final Snackbar snack = Snackbar.make(findViewById(R.id.add_group_root), "Une erreur est survenue lors de la récupération des données.", Snackbar.LENGTH_INDEFINITE);
                snack.setAction("Réessayer", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Task().execute();
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        }
    }

}
