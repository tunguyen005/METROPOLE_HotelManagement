package admin_dashboard.swing.table;

public class ModelAction<T> {
    private T data;
    private EventAction<T> event;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public EventAction<T> getEvent() {
        return event;
    }

    public void setEvent(EventAction<T> event) {
        this.event = event;
    }

    public ModelAction(T data, EventAction<T> event) {
        this.data = data;
        this.event = event;
    }

    public ModelAction() {
    }
}