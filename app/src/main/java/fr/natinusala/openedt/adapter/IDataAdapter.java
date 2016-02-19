package fr.natinusala.openedt.adapter;

import java.io.IOException;

import fr.natinusala.openedt.data.Component;
import fr.natinusala.openedt.data.Group;
import fr.natinusala.openedt.data.Week;

public interface IDataAdapter
{
    Group[] getGroupsList(Component c) throws IOException;
    Week[] getWeeks(Group g) throws IOException;
}
