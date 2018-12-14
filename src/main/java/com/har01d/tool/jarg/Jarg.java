package com.har01d.tool.jarg;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Jarg extends JCommand {

    private static final Logger logger = LoggerFactory.getLogger(Jarg.class);

    private final List<JCommand> commands = new ArrayList<>();

    private final List<String> arguments = new ArrayList<>();
    private boolean autoHelp;
    private JCommand command;

    public Jarg(String name) {
        super(name, null);
    }

    public Jarg(String name, String description) {
        super(name, description);
    }

    public void setAutoHelp(boolean autoHelp) {
        this.autoHelp = autoHelp;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getArgument(int index) {
        return arguments.get(index);
    }

    public boolean isCommand(String name) {
        return command != null && name.equals(command.getName());
    }

    public JCommand getCommand() {
        return command;
    }

    public JCommand getCommand(String name) {
        for (JCommand command : commands) {
            if (command.getName().equals(name)) {
                return command;
            }
        }
        return null;
    }

    public String getCommandName() {
        return command == null ? null : command.getName();
    }

    public JCommand addCommand(String name, String description) {
        JCommand command = new JCommand(name, description, this);
        command.addOption("--help", "Show the help text", false);
        commands.add(command);
        return command;
    }

    public void parse(String[] args) {
        boolean checkedCommand = false;
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            String name = null;
            String value = null;

            if (arg.startsWith("--")) {
                name = arg.substring(2);
                int index = arg.indexOf('=');
                if (index > -1) {
                    name = arg.substring(2, index);
                    value = arg.substring(index + 1);
                }
            } else if (arg.startsWith("-")) {
                name = arg.substring(1);
            }

            if (name != null) {
                JOption option = null;
                if (map.containsKey(name)) {
                    option = map.get(name);
                } else if (command != null && command.hasOption(name)) {
                    option = command.getOption(name);
                }

                if (option != null) {
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
            } else if (!checkedCommand) {
                for (JCommand command : commands) {
                    if (arg.equals(command.getName())) {
                        this.command = command;
                        break;
                    }
                }

                checkedCommand = true;
                if (this.command == null) {
                    arguments.add(arg);
                }
            } else {
                arguments.add(arg);
            }
        }

        if (args.length == 0) {
            printHelp(System.out);
            System.exit(1);
        }

        if (autoHelp) {
            if (isCommand("help")) {
                printHelp(System.out);
                System.exit(0);
            } else if (isPresent("help")) {
                if (command != null) {
                    command.printHelp(System.out);
                } else {
                    printHelp(System.out);
                }
                System.exit(0);
            }
        }
    }

    public JCommand requireCommand() {
        if (command == null) {
            printHelp(System.out);
            System.exit(1);
        }
        return command;
    }

    public void printHelp(PrintStream printStream) {
        if (!arguments.isEmpty()) {
            String name = arguments.get(0);
            JCommand command = getCommand(name);
            if (command != null) {
                command.printHelp(printStream);
                return;
            }
        }

        printStream.println("NAME");
        printStream.println(indent(4) + getName() + (getDescription() == null ? "" : "  -    " + getDescription()));

        printOptions(printStream);
        printCommands(printStream);
    }

    private void printCommands(PrintStream printStream) {
        if (!this.commands.isEmpty()) {
            printStream.println("COMMANDS");
            int max = 0;
            for (JCommand command : commands) {
                max = Math.max(max, command.getName().length());
            }

            int m = max;
            this.commands.forEach(o -> {
                printStream.println(indent(4) + o.getName() + indent(8) + indent(m - o.getName().length()) + o.getDescription());
            });
        }
    }

    public JOption getOption(String name) {
        if (command != null && command.hasOption(name)) {
            return command.getOption(name);
        }

        if (map.containsKey(name)) {
            return map.get(name);
        }

        throw new IllegalArgumentException("Unknown option: " + name);
    }

    public boolean isPresent(String name) {
        JOption option = null;
        if (command != null && command.hasOption(name)) {
            option = command.getOption(name);
        }

        if (map.containsKey(name)) {
            option = map.get(name);
        }

        if (option == null) {
            logger.warn("Unknown option: {}", name);
        }
        return option != null && option.isPresent();
    }

}
