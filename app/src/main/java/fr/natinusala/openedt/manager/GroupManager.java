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

public class GroupManager
{
    private final static String PREF_IDENTIFIER = "groups";
    private final static String GROUPS_LIST_IDENTIFIER = "groups_list";
    private final static String SELECTED_GROUP_IDENTIFIER = "selected_group";

    public static ArrayList<Group> readGroups(Context c)
    {
        SharedPreferences pref = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE);

        if (pref.contains(GROUPS_LIST_IDENTIFIER))
        {
            return new Gson().fromJson(pref.getString(GROUPS_LIST_IDENTIFIER, ""), new TypeToken<ArrayList<Group>>(){}.getType());
        }
        else
        {
            return new ArrayList<>();
        }
    }

    public static void addGroup(Context c, Group g)
    {
        ArrayList<Group> groups = readGroups(c);
        groups.add(g);
        saveGroups(c, groups.toArray(new Group[1]));
    }

    public static void saveGroups(Context c, Group[] groups)
    {
        SharedPreferences.Editor edit = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE).edit();

        edit.putString(GROUPS_LIST_IDENTIFIER, new Gson().toJson(groups, Group[].class));

        edit.apply();
    }

    public static Group getSelectedGroup(Context c)
    {
        SharedPreferences pref = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE);
        return new Gson().fromJson(pref.getString(SELECTED_GROUP_IDENTIFIER, ""), Group.class);
    }

    public static void saveSelectedGroup(Context c, Group g)
    {
        SharedPreferences.Editor edit = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE).edit();
        edit.putString(SELECTED_GROUP_IDENTIFIER, new Gson().toJson(g, Group.class));
        edit.apply();
    }
}
