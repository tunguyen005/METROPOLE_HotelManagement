package reception_dashboard.model;

import reception_dashboard.swing.table.ModelProfile;
import reception_dashboard.swing.table.ModelAction;
import reception_dashboard.swing.table.EventAction;

import javax.swing.*;
import java.text.DecimalFormat;

public class ModelPerson {
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public ModelPerson(Icon icon, String name, String gender, String course, double fees) {
        this.icon = icon;
        this.name = name;
        this.gender = gender;
        this.course = course;
        this.fees = fees;
    }

    public ModelPerson() {
    }

    private Icon icon;
    private String name;
    private String gender;
    private String course;
    private double fees;

    public Object[] toRowTable(EventAction event) {
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        return new Object[]{new ModelProfile(icon, name), gender, course, df.format(fees), new ModelAction(this, event)};
    }
}

