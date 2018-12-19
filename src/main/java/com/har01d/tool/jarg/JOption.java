package com.har01d.tool.jarg;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class JOption {

    private static final Logger logger = Logger.getLogger(JOption.class.getName());

    private final List<String> longOptions = new ArrayList<String>();
    private final List<String> shortOptions = new ArrayList<String>();
    private final List<String> options = new ArrayList<String>();

    private String description;
    private final boolean hasValue;

    private boolean interactive;
    private boolean present;
    private String label;
    private String value;
    private List<String> values = new ArrayList<String>();

    /**
     * Construct a <code>JOption</code>.
     * The options represent by a string, separate by "|", e.g.: "-f|--file".
     * The short options start with "-";
     * The long options start with "--;
     * The first long option is the primary name if exist;
     * Otherwise, the first short option is the primary name.
     * <p>
     * When the option has value, there are 3 possible ways to specific the value:
     * 1. -f app.conf
     * 2. --file=app.conf
     * 3. --file app.conf
     * If the option doesn't have value, the option present indicate a true flag.
     *
     * @param option      the string of options, separate by "|", e.g.: "-a|--all".
     * @param description the option description
     * @param hasValue    if this option has a value
     */
    JOption(String option, String description, boolean hasValue) {
        this.description = description;
        this.hasValue = hasValue;
        this.init(option);
    }

    private void init(String option) {
        if (option == null || option.isEmpty()) {
            throw new IllegalArgumentException("Missing options");
        }

        String[] array = option.split("\\|");
        for (String op : array) {
            if (op.startsWith("--") && op.length() > 2) {
                longOptions.add(op);
                options.add(op.substring(2));
                if (label == null) {
                    label = op.substring(2).toUpperCase();
                }
            } else if (op.startsWith("-") && op.length() > 1) {
                shortOptions.add(op);
                options.add(op.substring(1));
            } else {
                logger.warning("Unsupported option: " + op);
            }
        }

        if (label == null) {
            label = "VALUE";
        }
    }

    /**
     * Get the primary name.
     *
     * @return the name
     */
    public String getName() {
        if (!longOptions.isEmpty()) {
            return longOptions.get(0);
        }
        return shortOptions.get(0);
    }

    /**
     * Reads a password or passphrase from the console with echoing disabled if value is not provided.
     * If cannot access the system console will exit.
     *
     * @return this <code>JOption</code>
     */
    public JOption interactive() {
        if (!hasValue) {
            throw new IllegalStateException("Option " + getName() + " doesn't have value, cannot support interactive");
        }
        this.interactive = true;
        return this;
    }

    /**
     * Indicate if the option read value by interactive mode.
     *
     * @return the interactive mode
     */
    public boolean isInteractive() {
        return interactive;
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

    public String getLabel() {
        return label;
    }

    public JOption setLabel(String label) {
        this.label = label;
        return this;
    }

    public JOption defaultValue(Object value) {
        if (!hasValue) {
            throw new IllegalStateException("Option " + getName() + " doesn't have value");
        }
        this.value = String.valueOf(value);
        description = description + " (Default: " + this.value + ")";
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
