package reception_dashboard.swing.table;

import reception_dashboard.model.ModelPerson;

public interface EventAction {

    public void delete(ModelPerson person);

    public void update(ModelPerson person);
}
