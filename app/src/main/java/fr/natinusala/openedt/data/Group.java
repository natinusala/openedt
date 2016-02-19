package fr.natinusala.openedt.data;

public class Group {
    public String name;
    public String dataSource;
    public DataSourceType dataSourceType;
    public Component component;

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
