package com.har01d.tool.jarg;

import java.io.Console;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

public final class Jarg extends JCommand {

    public static final String AUTHOR = "AUTHOR";
    public static final String REPORTING_BUGS = "REPORTING BUGS";
    public static final String COPYRIGHT = "COPYRIGHT";
    public static final String SEE_ALSO = "SEE ALSO";

    private static final Logger logger = Logger.getLogger(Jarg.class.getName());

    private final List<JCommand> commands = new ArrayList<JCommand>();

    private final List<String> argument = new ArrayList<String>();
    private boolean autoHelp;
    private JCommand command;

    private final HashMap<String, String> sections = new LinkedHashMap<String, String>();

    public Jarg(String name) {
        super(name, null);
    }

    public Jarg(String name, String description) {
        super(name, description);
    }

    /**
     * Print help message and exit when:
     * 1. --help option is present, or
     * 2. is help command
     *
     * @return this <code>Jarg</code>
     */
    public Jarg autoHelp() {
        this.autoHelp = true;
        return this;
    }

    /**
     * Add section for help message, such as, author, copyright.
     *
     * @param title   The section title
     * @param content The section content
     * @return this <code>Jarg</code>
     */
    public Jarg addSection(String title, String content) {
        sections.put(title, content);
        return this;
    }

    public List<String> getArguments() {
        return argument;
    }

    public String getArgument(int index) {
        return argument.get(index);
    }

    public String getArgument(String name) {
        if (command != null) {
            return command.getArgument(name);
        }
        return super.getArgument(name);
    }

    /**
     * Required one command is present. otherwise, exit with code 1.
     * @return the present <code>JCommand</code>
     */
    public JCommand requireCommand() {
        if (command == null) {
            System.err.println("Missing required command!");
            printHelp(System.out);
            System.exit(1);
        }
        return command;
    }

    /**
     * Print error message, help message and exit with code 1.
     *
     * @param e The exception
     */
    public void handleError(Exception e) {
        System.err.println(e.getMessage());
        printHelp(System.out);
        System.exit(1);
    }

    public boolean isCommand(String name) {
        return command != null && command.aliases.contains(name);
    }

    public JCommand getCommand() {
        return command;
    }

    private JCommand getCommand(String name) {
        for (JCommand command : commands) {
            if (command.aliases.contains(name)) {
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
        if (autoHelp) {
            command.addOption("--help", "Show the help text", false);
        }
        commands.add(command);
        return command;
    }

    public void parse(String[] args) {
        boolean checkedCommand = false;
        boolean optionsEnd = false;
        List<JOption> prompts = new ArrayList<JOption>();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            String name = null;
            String value = null;

            if (optionsEnd) {
                argument.add(arg);
                continue;
            }

            if (arg.equals("--")) {
                optionsEnd = true;
                continue;
            }

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
                            if (option.isInteractive()) {
                                if (i + 1 == args.length || isOption(args[i + 1])) {
                                    option.setPresent(true);
                                    prompts.add(option);
                                    continue;
                                }
                            }
                            if (i + 1 == args.length) {
                                throw new IllegalArgumentException(
                                        "Missing required value for option " + option.getName());
                            }
                            value = args[++i];
                        } else {
                            value = Boolean.TRUE.toString();
                        }
                    }
                    option.setPresent(true);
                    option.setValue(value);
                } else {
                    logger.warning("Unknown option: " + name);
                }
            } else if (!checkedCommand) {
                for (JCommand command : commands) {
                    if (command.aliases.contains(arg)) {
                        this.command = command;
                        break;
                    }
                }

                checkedCommand = true;
                if (this.command == null) {
                    argument.add(arg);
                }
            } else {
                argument.add(arg);
            }
        }

//        if (args.length == 0) {
//            printHelp(System.out);
//            System.exit(1);
//        }

        if (autoHelp) {
            if (isPresent("help")) {
                if (command != null) {
                    command.printHelp(System.out);
                } else {
                    printHelp(System.out);
                }
                System.exit(0);
            } else if (isCommand("help")) {
                printHelp(System.out);
                System.exit(0);
            }
        }

        if (!isPresent("help") && !isPresent("version")) {
            List<JParameter> parameters = getParameters();
            for (int i = 0; i < parameters.size(); ++i) {
                JParameter parameter = parameters.get(i);
                if (i < this.argument.size()) {
                    parameter.setValue(this.argument.get(i));
                } else if (parameter.isRequired()) {
                    throw new IllegalArgumentException("Missing required argument " + parameter.getName());
                }
            }
        }

        for (JOption option : prompts) {
            Console console = System.console();
            if (console == null) {
                System.err.println("Cannot access the console device");
                System.err.println("Specific value in command line for option " + option.getName());
                System.exit(1);
            }
            char[] password = console.readPassword("Enter value of %s:", option.getName());
            option.setValue(new String(password));
        }
    }

    private List<JParameter> getParameters() {
        if (command != null) {
            return command.parameters;
        }
        return parameters;
    }

    private boolean isOption(String name) {
        if (name.startsWith("--")) {
            name = name.substring(2);
        } else if (name.startsWith("-")) {
            name = name.substring(1);
        } else {
            return false;
        }

        if (map.containsKey(name)) {
            return true;
        } else {
            return command != null && command.hasOption(name);
        }
    }

    public void printHelp(PrintStream printStream) {
        if (!argument.isEmpty()) {
            String name = argument.get(0);
            JCommand command = getCommand(name);
            if (command != null) {
                command.printHelp(printStream);
                return;
            }
        }

        printStream.println("NAME");
        printStream.println(indent(4) + getName() + (getSummary() == null ? "" : "  -  " + getSummary()));
        printStream.println();

        if (synopsis == null) {
            generateSynopsis();
        }
        if (synopsis != null) {
            printStream.println("SYNOPSIS");
            printStream.println(indentLines(synopsis, 4));
        }

        if (description != null) {
            printStream.println("DESCRIPTION");
            printStream.println(indentLines(description, 4));
        }

        printOptions(printStream);
        printCommands(printStream);

        for (Map.Entry<String, String> entry : sections.entrySet()) {
            printStream.println(entry.getKey().toUpperCase());
            printStream.println(indentLines(entry.getValue(), 4));
        }
    }

    private void generateSynopsis() {
        if (commands.isEmpty()) {
            synopsis = getName() + " [OPTION]... " + joinString(parameters, " ");
        } else {
            synopsis = getName() + " COMMAND [OPTION]... " + joinString(parameters, " ");
        }
    }

    private void printCommands(PrintStream printStream) {
        if (!this.commands.isEmpty()) {
            printStream.println("COMMANDS");
            int max = 0;
            for (JCommand command : commands) {
                max = Math.max(max, command.getName().length());
            }

            int m = max;
            for (JCommand o : this.commands) {
                printStream.println(indent(4) + o.getName() + indent(8) + indent(m - o.getName().length()) + o.getSummary());
            }
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
            logger.fine("Unknown option: " + name);
        }
        return option != null && option.isPresent();
    }

}
