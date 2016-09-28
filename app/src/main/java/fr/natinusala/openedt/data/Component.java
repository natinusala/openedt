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

public enum Component
{
    //TODO Séparer par universités

    IUT_NANTES("IUT Nantes", "https://edt.univ-nantes.fr/iut_nantes/gindex.html", DataSourceType.CELCAT),
    MEDECINE("Médecine, Pharmacie", "https://edt.univ-nantes.fr/medecine/gindex.html", DataSourceType.CELCAT),
    DROIT("Droit et sciences politiques", "https://www.droit.univ-nantes.fr/ept/gindex.html", DataSourceType.CELCAT),
    IEMNIAE("IEMN-IAE", "https://edt.univ-nantes.fr/iemn_iae/gindex.html", DataSourceType.CELCAT),
    IQUABIAN("Iquabian", "https://edt.univ-nantes.fr/iquabian/gindex.html", DataSourceType.CELCAT),
    ODONTOLOGIE("Odontologie", "https://edt.univ-nantes.fr/odontologie/gindex.html", DataSourceType.CELCAT),
    STAPS("STAPS", "https://edt.univ-nantes.fr/staps/gindex.html", DataSourceType.CELCAT),
    ESPE_LEMANS("ESPE Le Mans", "https://edt.univ-nantes.fr/iufm-mans/gindex.html", DataSourceType.CELCAT),
    ESPE_NANTES("ESPE Nantes", "https://edt.univ-nantes.fr/iufm-nantes/gindex.html", DataSourceType.CELCAT),
    ESPE_ANGERS("ESPE Angers", "https://edt.univ-nantes.fr/espe-angers/", DataSourceType.CELCAT, true),
    LANGUES("Langues - CIL", "https://edt.univ-nantes.fr/langues/gindex.html", DataSourceType.CELCAT),
    IRFFLE("Irffle", "https://edt.univ-nantes.fr/irffle/gindex.html", DataSourceType.CELCAT),
    HISTOIRE("Histoire", "https://edt.univ-nantes.fr/histoire/gindex.html", DataSourceType.CELCAT),
    IGARUN("IGARUN", "https://edt.univ-nantes.fr/igarun/gindex.html", DataSourceType.CELCAT),
    PSYCHOLOGIE("Psychologie", "https://edt.univ-nantes.fr/psycho/gindex.html", DataSourceType.CELCAT),
    LETTRES("Lettres et langages", "https://edt.univ-nantes.fr/lettreslangages/gindex.html", DataSourceType.CELCAT),
    IUT_SAINT_NAZAIRE("IUT Saint Nazaire", "https://edt.univ-nantes.fr/iut_st_naz/gindex.html", DataSourceType.CELCAT),
    UFR("UFR Sciences et Techniques", "https://edt.univ-nantes.fr/sciences/gindex.html", DataSourceType.CELCAT, true),
    CHANTRERIE_GAVY("Chantrerie et Gavy", "https://edt.univ-nantes.fr/chantrerie-gavy/gindex.html", DataSourceType.CELCAT, true),
    CENTRALE_NANTES("Ecole Centrale Nantes", "http://website.ec-nantes.fr/sites/edtemps/pindex.html", DataSourceType.CELCAT);

    Component(String name, String groups_url, DataSourceType sourceType)
    {
        this.name = name;
        this.groups_url = groups_url;
        this.sourceType = sourceType;
        this.needAuth = false;

    }

    Component(String name, String groups_url, DataSourceType sourceType, boolean needAuth ){
        this.name = name;
        this.groups_url = groups_url;
        this.sourceType = sourceType;
        this.needAuth = needAuth;
    }

    public String name;
    public String groups_url;
    public DataSourceType sourceType;
    public boolean needAuth;
}
