package com.har01d.tool.jarg;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCommand {

    private static final Logger logger = LoggerFactory.getLogger(JCommand.class);

    protected final Map<String, JOption> map = new HashMap<>();
    protected final List<JOption> options = new ArrayList<>();
    private final String name;
    private final String description;
    private final JCommand parent;

    public JCommand(String name, String description, JCommand parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
    }

    public JCommand(String name, String description) {
        this(name, description, null);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public JOption addOption(String value, String description) {
        return addOption(value, description, true);
    }

    public JOption addOption(String value, String description, boolean hasValue) {
        JOption option = new JOption(value, description, hasValue);
        option.getOptions().forEach(name -> {
            if (map.containsKey(name)) {
                throw new IllegalArgumentException("Duplicate option name: " + name);
            }
            map.put(name, option);
        });
        options.add(option);
        return option;
    }

    public boolean hasOption(String name) {
        return map.containsKey(name);
    }

    public JOption getOption(String name) {
        if (!map.containsKey(name)) {
            throw new IllegalArgumentException("Unknown option: " + name);
        }
        return map.get(name);
    }

    public boolean isPresent(String name) {
        JOption option = map.get(name);

        if (option == null) {
            logger.warn("Unknown option: {}", name);
        }
        return option != null && option.isPresent();
    }

    public String getValue(String name) {
        JOption option = getOption(name);
        return option.getValue();
    }

    public String getValue(String name, String defaultValue) {
        JOption option = getOption(name);
        return option.isPresent() ? option.getValue() : defaultValue;
    }

    public boolean getBooleanValue(String name) {
        return "true".equalsIgnoreCase(getValue(name));
    }

    public byte getByteValue(String name) {
        return Byte.parseByte(getValue(name));
    }

    public byte getByteValue(String name, byte defaultValue) {
        return Byte.parseByte(getValue(name, String.valueOf(defaultValue)));
    }

    public int getIntValue(String name) {
        return Integer.parseInt(getValue(name));
    }

    public int getIntValue(String name, int defaultValue) {
        return Integer.parseInt(getValue(name, String.valueOf(defaultValue)));
    }

    public long getLongValue(String name) {
        return Long.parseLong(getValue(name));
    }

    public long getLongValue(String name, long defaultValue) {
        return Long.parseLong(getValue(name, String.valueOf(defaultValue)));
    }

    public float getFloatValue(String name) {
        return Float.parseFloat(getValue(name));
    }

    public float getFloatValue(String name, float defaultValue) {
        return Float.parseFloat(getValue(name, String.valueOf(defaultValue)));
    }

    public double getDoubleValue(String name) {
        return Double.parseDouble(getValue(name));
    }

    public double getDoubleValue(String name, double defaultValue) {
        return Double.parseDouble(getValue(name, String.valueOf(defaultValue)));
    }

    private List<String> getValues(String name) {
        JOption option = getOption(name);
        return option.getValues();
    }

    public List<String> getStringValues(String name) {
        List<String> values = getValues(name);
        if (values.size() == 1) {
            return Arrays.asList(values.get(0).split("[;|,]"));
        } else {
            return values;
        }
    }

    public List<Integer> getIntValues(String name) {
        List<Integer> result = new ArrayList<>();
        List<String> values = getValues(name);
        if (values.size() == 1) {
            for (String val : values.get(0).split("[;|,]")) {
                result.add(Integer.parseInt(val));
            }
        } else {
            for (String val : values) {
                result.add(Integer.parseInt(val));
            }
        }
        return result;
    }

    public List<Long> getLongValues(String name) {
        List<Long> result = new ArrayList<>();
        List<String> values = getValues(name);
        if (values.size() == 1) {
            for (String val : values.get(0).split("[;|,]")) {
                result.add(Long.parseLong(val));
            }
        } else {
            for (String val : values) {
                result.add(Long.parseLong(val));
            }
        }
        return result;
    }

    public List<Float> getFloatValues(String name) {
        List<Float> result = new ArrayList<>();
        List<String> values = getValues(name);
        if (values.size() == 1) {
            for (String val : values.get(0).split("[;|,]")) {
                result.add(Float.parseFloat(val));
            }
        } else {
            for (String val : values) {
                result.add(Float.parseFloat(val));
            }
        }
        return result;
    }

    public List<Double> getDoubleValues(String name) {
        List<Double> result = new ArrayList<>();
        List<String> values = getValues(name);
        if (values.size() == 1) {
            for (String val : values.get(0).split("[;|,]")) {
                result.add(Double.parseDouble(val));
            }
        } else {
            for (String val : values) {
                result.add(Double.parseDouble(val));
            }
        }
        return result;
    }

    protected String joinString(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (String str : values) {
            if (sb.length() > 0) {
                sb.append(", ").append(str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    protected String indent(int number) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < number; ++i) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public void printHelp(PrintStream printStream) {
        printStream.println("COMMAND");
        printStream.println(indent(4) + name + indent(8) + description);
        printOptions(printStream);
    }

    protected void printOptions(PrintStream printStream) {
        List<JOption> options = new ArrayList<>();
        if (parent != null) {
            options.addAll(parent.options);
        }
        options.addAll(this.options);
        if (options.isEmpty()) {
            return;
        }

        printStream.println("OPTIONS");
        options.forEach(e -> {
            List<String> allOptions = new ArrayList<>(e.getShortOptions());
            if (e.isHasValue()) {
                e.getLongOptions().forEach(o -> allOptions.add(o + " <" + e.getValueName() + ">"));
            } else {
                allOptions.addAll(e.getLongOptions());
            }

            printStream.println(indent(4) + joinString(allOptions));
            printStream.println(indent(8) + e.getDescription());
        });
    }

}
