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

public enum Component
{
    IUT_NANTES("IUT Nantes", "https://edt.univ-nantes.fr/iut_nantes/gindex.html"),
    MEDECINE("Médecine, Pharmacie", "https://edt.univ-nantes.fr/medecine/gindex.html");

    private Component(String name, String groups_url)
    {
        this.name = name;
        this.groups_url = groups_url;
    }

    public String name;
    public String groups_url;
}