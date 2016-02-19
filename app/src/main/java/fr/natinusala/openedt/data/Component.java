package fr.natinusala.openedt.data;

public enum Component
{
    IUT_NANTES("IUT Nantes", "https://edt.univ-nantes.fr/iut_nantes/gindex.html", DataSourceType.CELCAT),
    MEDECINE("Médecine, Pharmacie", "https://edt.univ-nantes.fr/medecine/gindex.html", DataSourceType.CELCAT);

    Component(String name, String groups_url, DataSourceType sourceType)
    {
        this.name = name;
        this.groups_url = groups_url;
        this.sourceType = sourceType;
    }

    public String name;
    public String groups_url;
    public DataSourceType sourceType;
}
