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

package fr.natinusala.openedt.adapter;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

import fr.natinusala.openedt.data.Component;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;

public interface IDataAdapter
{
    ArrayList<Group> getGroupsList(Component c, Context context) throws IOException;
    ArrayList<Group> getGroupsList(Component c, Context context, String id, String pwd) throws IOException;
    ArrayList<Week> getWeeks(Group g, Context context) throws IOException;

}
