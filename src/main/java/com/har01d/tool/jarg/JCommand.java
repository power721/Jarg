package com.har01d.tool.jarg;

import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

public class JCommand {

    private static final Logger logger = Logger.getLogger(JCommand.class.getName());

    protected final Map<String, JOption> map = new HashMap<String, JOption>();
    protected final List<JOption> options = new ArrayList<JOption>();
    final List<String> aliases = new ArrayList<String>();
    private final String name;
    private final String summary;
    private final JCommand parent;
    protected String synopsis;
    protected String description;

    public JCommand(String name, String summary, JCommand parent) {
        this.name = name;
        this.summary = summary;
        this.parent = parent;
        this.aliases.add(name);
    }

    public JCommand(String name, String summary) {
        this(name, summary, null);
    }

    public JCommand aliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public JCommand setSynopsis(String synopsis) {
        this.synopsis = synopsis;
        return this;
    }

    public JCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public JOption addOption(String value, String description) {
        return addOption(value, description, true);
    }

    public JOption addOption(String value, String description, boolean hasValue) {
        JOption option = new JOption(value, description, hasValue);
        return addOption(option);
    }

    public JOption addOption(JOption option) {
        for (String name : option.getOptions()) {
            if (map.containsKey(name)) {
                if (name.equals("help")) {
                    return map.get(name);
                }
                throw new IllegalArgumentException("Duplicate option name: " + name);
            }
            map.put(name, option);
        }
        options.add(option);
        return option;
    }

    public JCommand addOptions(Iterable<JOption> options) {
        for (JOption option : options) {
            addOption(option);
        }
        return this;
    }

    public boolean hasOption(String name) {
        return map.containsKey(name);
    }

    public List<JOption> getOptions() {
        return options;
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
            logger.fine("Unknown option: " + name);
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
        List<Integer> result = new ArrayList<Integer>();
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
        List<Long> result = new ArrayList<Long>();
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
        List<Float> result = new ArrayList<Float>();
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
        List<Double> result = new ArrayList<Double>();
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

    protected String indentLines(String text, int number) {
        String indent = indent(number);
        String[] lines = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.isEmpty()) {
                sb.append("\n");
            } else {
                sb.append(indent).append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public void printHelp(PrintStream printStream) {
        printStream.println("COMMAND");
        printStream.println(indent(4) + joinString(aliases) + indent(8) + summary);

        if (name.equals("help")) {
            printStream.println("SYNOPSIS");
            printStream.println(indent(4) + "COMMAND --help");
            printStream.println(indent(4) + "help COMMAND");
        } else if (synopsis != null) {
            printStream.println("SYNOPSIS");
            printStream.print(indentLines(synopsis, 4));
        }

        if (description != null) {
            printStream.println("DESCRIPTION");
            printStream.print(indentLines(description, 4));
        }

        printOptions(printStream);
    }

    protected void printOptions(PrintStream printStream) {
        List<JOption> options = new ArrayList<JOption>();
        if (parent != null) {
            options.addAll(parent.options);
        }
        options.addAll(this.options);
        if (options.isEmpty()) {
            return;
        }

        printStream.println("OPTIONS");
        for (JOption e : options) {
            List<String> allOptions = new ArrayList<String>(e.getShortOptions());
            if (e.isHasValue()) {
                for (String o : e.getLongOptions()) {
                    allOptions.add(o + " <" + e.getValueName() + ">");
                }
            } else {
                allOptions.addAll(e.getLongOptions());
            }

            printStream.println(indent(4) + joinString(allOptions));
            printStream.println(indent(8) + e.getDescription());
        }
    }

}
