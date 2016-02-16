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

package fr.natinusala.openedt.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.natinusala.openedt.activity.MainActivity;


/**
 * Created by Maveist on 12/02/2016.
 */
public class GroupManager{

    private Activity AddGroupActivity;
    private Map<String, String> groupUrl;
    private String URLIndex;
    private String URLBranch;

    public GroupManager(Activity addGroup, String index) throws IOException {
        this.AddGroupActivity = addGroup;
        this.groupUrl = new HashMap<String,String>();
        this.URLIndex = index;
        this.URLBranch = index.replace("gindex.html", "");
        this.fillGroupUrl();
    }

    protected void fillGroupUrl() throws IOException{
        Document doc = Jsoup.connect(this.URLIndex).get();
        for (Element e : doc.select("option")){

            String nameGroup = e.text();
            String urlGroup = this.URLBranch + e.attr("value");
            urlGroup = urlGroup.replace(".html", ".xml");
            this.groupUrl.put(nameGroup, urlGroup);

        }
    }

    public ArrayList<String> getKeys(){
        ArrayList<String> list =  new ArrayList<String>(this.groupUrl.keySet());
        Collections.sort(list);
        return list;
    }

    public void setGroup(String groupSelected){
        String urlSelected = this.groupUrl.get(groupSelected);
        SharedPreferences pref = AddGroupActivity.getSharedPreferences(MainActivity.SCRAPPER_SAVE, 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString("groupUrl", urlSelected);
        prefEditor.putString("groupName", groupSelected);
        prefEditor.commit();
    }

}
