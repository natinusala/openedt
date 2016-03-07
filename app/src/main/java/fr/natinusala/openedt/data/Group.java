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

package fr.natinusala.openedt.data;

public class Group {
    public String name;
    public String dataSource;
    public DataSourceType dataSourceType;
    public Component component;

    public Group(){}

    public Group(String name, String dataSource, DataSourceType dataSourceType, Component component) {
        this.name = name;
        this.dataSource = dataSource;
        this.dataSourceType = dataSourceType;
        this.component = component;
    }

    public Group setName(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Group)
        {
            return name.equals(((Group) o).name) && dataSource.equals(((Group) o).dataSource) && dataSourceType.equals(((Group) o).dataSourceType) && component.equals(((Group) o).component);
        }
        else
        {
            return super.equals(o);
        }
    }
}
