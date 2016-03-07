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

package fr.natinusala.openedt.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;

public class DataManager
{
    static final String PREF_IDENTIFIER = "data";

    static String createGroupIdentifier(Group g)
    {
        return "component_" + g.component.name + "_group_" + g.name;
    }

    public static ArrayList<Week> getWeeksForGroup(Context c, Group g)
    {
        String groupIdentifier = createGroupIdentifier(g);

        SharedPreferences pref = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE);

        //On essaye d'abord de le récupérer depuis internet
        try
        {
            //On télécharge la liste
            ArrayList<Week> weeks = g.dataSourceType.adapter.getWeeks(g);

            SharedPreferences.Editor edit = pref.edit();

            edit.putString(groupIdentifier, new Gson().toJson(weeks, new TypeToken<ArrayList<Week>>(){}.getType()));
            edit.apply();

            return weeks;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Depuis internet ça n'a pas marché, on le récupère depuis la sauvegarde

            //Si on l'a de sauvegardé
            if (pref.contains(groupIdentifier))
            {
                return new Gson().fromJson(pref.getString(groupIdentifier, ""), new TypeToken<ArrayList<Week>>(){}.getType());
            }

            //Sinon... désolé bro, on peut rien pour toi
            return null;
        }
    }
}
