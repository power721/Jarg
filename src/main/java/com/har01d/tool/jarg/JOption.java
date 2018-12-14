package com.har01d.tool.jarg;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JOption {

    private static final Logger logger = LoggerFactory.getLogger(JOption.class);

    private final List<String> longOptions = new ArrayList<String>();
    private final List<String> shortOptions = new ArrayList<String>();
    private final List<String> options = new ArrayList<String>();

    private final String option;
    private final String description;
    private final boolean hasValue;

    private boolean present;
    private String valueName = "VALUE";
    private String value;
    private List<String> values = new ArrayList<String>();

    JOption(String option, String description, boolean hasValue) {
        this.option = option;
        this.description = description;
        this.hasValue = hasValue;
        this.init();
    }

    private void init() {
        if (option == null || option.isEmpty()) {
            throw new IllegalArgumentException("Missing options");
        }

        String[] array = option.split("\\|");
        for (String op : array) {
            if (op.startsWith("--") && op.length() > 2) {
                longOptions.add(op);
                options.add(op.substring(2));
            } else if (op.startsWith("-") && op.length() > 1) {
                shortOptions.add(op);
                options.add(op.substring(1));
            } else {
                logger.warn("Unsupported option: " + op);
            }
        }
    }

    public String getName() {
        if (!longOptions.isEmpty()) {
            return longOptions.get(0);
        }
        return shortOptions.get(0);
    }

    public List<String> getLongOptions() {
        return longOptions;
    }

    public List<String> getShortOptions() {
        return shortOptions;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public boolean isPresent() {
        return present;
    }

    public String getValueName() {
        return valueName;
    }

    public JOption setValueName(String valueName) {
        this.valueName = valueName;
        return this;
    }

    public String getValue() {
        return value;
    }

    public List<String> getValues() {
        return values;
    }

    void setValue(String value) {
        this.value = value;
        this.values.add(value);
    }

}
