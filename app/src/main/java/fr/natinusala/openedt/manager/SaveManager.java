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

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;

import fr.natinusala.openedt.activity.MainActivity;
import fr.natinusala.openedt.scrapping.CelcatEventScrapper;

public class SaveManager {
    private SharedPreferences pref;

    public SaveManager(MainActivity mainActivity){
        pref = mainActivity.getSharedPreferences(MainActivity.SCRAPPER_SAVE, 0);
    }

    public boolean isScrapperSaved()
    {
        return pref.contains("scrapper");
    }

    public int readHour(){
        return pref.getInt("hour", -1);
    }

    public CelcatEventScrapper readScrapper(){
        Gson gson = new Gson();
        String json = pref.getString("scrapper", "");
        return gson.fromJson(json, CelcatEventScrapper.class);
    }

    public void saveHour(int hour){
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putInt("hour", hour);
        prefEditor.apply();
    }

    public void saveScrapper(CelcatEventScrapper scrapper){
        SharedPreferences.Editor prefEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scrapper);
        prefEditor.putString("scrapper", json);
        prefEditor.apply();
    }

    public CelcatEventScrapper getEventScrapper() throws IOException {
        Calendar cal = Calendar.getInstance();
        int hourNow = cal.get(Calendar.HOUR_OF_DAY);
        int hourLastSave = readHour();
        CelcatEventScrapper eventScrapper;
        if((hourLastSave == -1) || (hourNow - hourLastSave > 1))
        {
            eventScrapper = new CelcatEventScrapper(pref.getString("groupUrl", ""));
            saveHour(hourNow);
            saveScrapper(eventScrapper);
        }
        else{
            eventScrapper = readScrapper();
        }
        return eventScrapper;
    }

    public CelcatEventScrapper refresh() throws IOException
    {
        CelcatEventScrapper scrap = new CelcatEventScrapper(pref.getString("groupUrl", ""));
        saveScrapper(scrap);
        return scrap;
    }
}
