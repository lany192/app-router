package com.github.lany192.transfer.complier;

import java.util.ArrayList;
import java.util.List;

public class UIEntity {
    private String packageName;
    private String className;
    private List<Field> fields = new ArrayList<>();

    public void addField(Field field) {
        this.fields.add(field);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}