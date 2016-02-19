package fr.natinusala.openedt.data;

import fr.natinusala.openedt.adapter.CelcatAdapter;
import fr.natinusala.openedt.adapter.IDataAdapter;

public enum DataSourceType
{
    CELCAT(new CelcatAdapter());

    DataSourceType(IDataAdapter adapter)
    {
        this.adapter = adapter;
    }

    public IDataAdapter adapter;
}
