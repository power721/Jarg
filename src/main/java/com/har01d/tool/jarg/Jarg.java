package com.har01d.tool.jarg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Jarg {

    private static final Logger logger = LoggerFactory.getLogger(Jarg.class);

    private final Map<String, JOption> map = new HashMap<>();
    private final List<JOption> options = new ArrayList<>();
    private final List<String> arguments = new ArrayList<>();

    private final String name;

    public Jarg(String name) {
        this.name = name;
    }

    public void parse(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            String name = null;
            String value = null;

            if (arg.startsWith("--")) {
                name = arg.substring(2);
                int index = name.indexOf('=');
                if (index > -1) {
                    name = name.substring(0, index);
                    value = name.substring(index + 1);
                }
            } else if (arg.startsWith("-")) {
                name = arg.substring(1);
            }

            if (name != null) {
                if (map.containsKey(name)) {
                    JOption option = map.get(name);
                    if (value == null) {
                        if (option.isHasValue()) {
                            value = args[++i];
                        } else {
                            value = Boolean.TRUE.toString();
                        }
                    }
                    option.setPresent(true);
                    option.setValue(value);
                } else {
                    logger.warn("Unknown option: " + name);
                }
            } else {
                arguments.add(arg);
            }
        }
    }

    public void printHelp(PrintStream printStream) {
        printStream.println("NAME");
        printStream.println("       " + name);

        printStream.println("OPTIONS");
        this.options.forEach(e -> {
            List<String> options = new ArrayList<>(e.getShortOptions());
            if (e.isHasValue()) {
                e.getLongOptions().forEach(o -> options.add(o + " <VALUE>"));
            } else {
                options.addAll(e.getLongOptions());
            }
            printStream.println("       " + joinString(options));
            printStream.println("              " + e.getDescription());
        });
    }

    private String joinString(List<String> values) {
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

    public List<String> getArguments() {
        return arguments;
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
        JOption option = getOption(name);
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

    public List<String> getValues(String name) {
        JOption option = getOption(name);
        return option.getValues();
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

}
