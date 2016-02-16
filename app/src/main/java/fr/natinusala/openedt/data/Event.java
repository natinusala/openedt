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

package fr.natinusala.openedt.data;

import java.util.ArrayList;

public class Event 
{
	public int id = 0;
	public int timesort = 0;
	public int day = 0;
	
	public String prettytimes = "";
	public String starttime = "";
	public String endtime = "";
	
	public int startTimeUnits = 0;
	public int durationUnits = 0;
	public int endTimeUnits = 0;
	
	public int weekid = 0;
	
	public String category = "";
	
	public ArrayList<String> room = new ArrayList<>();
	public ArrayList<String> module = new ArrayList<>();
	public ArrayList<String> staff = new ArrayList<>();
	public ArrayList<String> group = new ArrayList<>();
	
	public String colour = "#FFFFCC";
	
	public Event(int id, int timesort, String colour)
	{
		this.id = id;
		this.timesort = timesort;
		this.colour = colour;

		if (this.colour.equals("#FFFFFF"))
		{
			this.colour = "#FFFFCC";
		}
	}


	public String createCategoryModule()
	{
		if (module.isEmpty() && category.isEmpty())
		{
			return "Inconnu";
		}
		else if (!category.isEmpty() && module.isEmpty())
		{
			return category + " inconnu(e)";
		}
		else if (category.isEmpty())
		{
			return getPrettyStringFromList(module);
		}
		else
		{
			return category + " : " + getPrettyStringFromList(module);
		}
	}

    public String getPrettyRoom()
    {
        return getPrettyStringFromList(room);
    }

    public String getPrettyStaff()
    {
        return getPrettyStringFromList(staff);
    }

	String getPrettyStringFromList(ArrayList<String> strings)
	{
		String r = "";
		for (int i = 0; i < strings.size(); i++)
		{
			if (i != 0)
			{
				r += ", ";
			}

			r += strings.get(i);
		}

		return r;
	}

	public String toPebbleString(){
		return getPrettyStringFromList(module)+"\n"+starttime+"-"+endtime+"\n"+getPrettyStringFromList(room);
	}
	
}
