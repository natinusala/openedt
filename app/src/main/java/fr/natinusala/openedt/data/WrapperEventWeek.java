package fr.natinusala.openedt.data;

/**
 * Created by SELIMFIXE on 11/03/2016.
 */
public class WrapperEventWeek {
    private Week week;
    private Event event;

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public WrapperEventWeek(Event e, Week w){
        week = w;
        event = e;

    }
}
