package fr.natinusala.openedt.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;

public class DataManager
{
    static final String PREF_IDENTIFIER = "data";

    static String createGroupIdentifier(Group g)
    {
        return "component_" + g.component.name + "_group_" + g.name;
    }

    public static Week[] getWeeksForGroup(Context c, Group g)
    {
        String groupIdentifier = createGroupIdentifier(g);

        SharedPreferences pref = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE);

        //On essaye d'abord de le récupérer depuis internet
        try
        {
            //On télécharge la liste
            Week[] weeks = g.dataSourceType.adapter.getWeeks(g);

            SharedPreferences.Editor edit = pref.edit();

            edit.putString(groupIdentifier, new Gson().toJson(weeks, Week[].class));
            edit.apply();

            return weeks;
        }
        catch (Exception ex)
        {
            //Depuis internet ça n'a pas marché, on le récupère depuis la sauvegarde

            //Si on l'a de sauvegardé
            if (pref.contains(groupIdentifier))
            {
                return new Gson().fromJson(pref.getString(groupIdentifier, ""), Week[].class);
            }

            //Sinon... désolé bro, on peut rien pour toi
            return null;
        }
    }
}
