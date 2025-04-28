package admin_dashboard.swing.table;

import admin_dashboard.model.ModelPerson;

import java.sql.SQLException;

public interface EventAction<T> {
    void delete(T data) throws SQLException;
    void update(T data);
    void view(T data);
}
