package reception_dashboard.swing.table;


import reception_dashboard.model.ModelPerson;
import reception_dashboard.swing.table.EventAction;

public class ModelAction {

    public ModelPerson getPerson() {
        return person;
    }

    public void setPerson(ModelPerson person) {
        this.person = person;
    }

    public EventAction getEvent() {
        return event;
    }

    public void setEvent(EventAction event) {
        this.event = event;
    }

    public ModelAction(ModelPerson person, EventAction event) {
        this.person = person;
        this.event = event;
    }

    public ModelAction() {
    }

    private ModelPerson person;
    private EventAction event;
}
