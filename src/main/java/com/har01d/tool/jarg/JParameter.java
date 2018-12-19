package com.har01d.tool.jarg;

public class JParameter {
    private final String name;
    private boolean required;

    private String value;

    public JParameter(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public JParameter required() {
        required = true;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        if (required) {
            return name.toUpperCase();
        } else {
            return "[" + name.toUpperCase() + "]";
        }
    }

}
