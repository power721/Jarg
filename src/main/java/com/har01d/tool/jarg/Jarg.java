package com.har01d.tool.jarg;

import java.io.Console;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

public final class Jarg extends JCommand {

    public static final String AUTHOR = "AUTHOR";
    public static final String COPYRIGHT = "COPYRIGHT";
    public static final String REPORTING_BUGS = "REPORTING BUGS";
    public static final String SEE_ALSO = "SEE ALSO";

    private static final Logger logger = Logger.getLogger(Jarg.class.getName());

    private final HashMap<String, String> sections = new LinkedHashMap<String, String>();
    private final List<JCommand> commands = new ArrayList<JCommand>();
    private final List<String> arguments = new ArrayList<String>();

    private boolean autoHelp;
    private PrintStream output = System.out;
    private JCommand command;

    public Jarg(String name) {
        super(name, null);
    }

    public Jarg(String name, String summary) {
        super(name, summary);
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

    /**
     * Get all the arguments.
     *
     * @return the arguments
     */
    @Override
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * Get the argument size.
     *
     * @return the argument size
     */
    @Override
    public int getArgumentSize() {
        return arguments.size();
    }

    /**
     * Get the positional argument by index.
     *
     * @param index the index
     * @return the positional argument
     */
    @Override
    public String getArgument(int index) {
        return arguments.get(index);
    }

    /**
     * Get the argument by name which is add as <code>JParameter</code>.
     *
     * @param name the argument name
     * @return the argument
     */
    @Override
    public String getArgument(String name) {
        if (command != null) {
            return command.getArgument(name);
        } else {
            return super.getArgument(name);
        }
    }

    /**
     * Print error message, help message and exit with code 1.
     *
     * @param e The exception
     */
    public void handleError(Exception e) {
        System.err.println(e.getMessage());
        printHelp(output);
        System.exit(1);
    }

    /**
     * Check if the command present by name.
     *
     * @param name the command name
     * @return true if the command present
     */
    public boolean isCommand(String name) {
        return command != null && command.aliases.contains(name);
    }

    /**
     * Required one command is present. otherwise, exit with code 1.
     *
     * @return the present <code>JCommand</code>
     */
    public JCommand requireCommand() {
        if (command == null) {
            throw new IllegalArgumentException("Command name is required!");
        } else {
            return command;
        }
    }

    /**
     * Get the current command.
     *
     * @return the command
     */
    public JCommand getCommand() {
        return command;
    }

    /**
     * Get the current command name.
     *
     * @return the command name
     */
    public String getCommandName() {
        return command == null ? null : command.getName();
    }

    /**
     * Add a command.
     *
     * @param name        the command name
     * @param description the command description
     * @return the <code>JCommand</code>
     */
    public JCommand addCommand(String name, String description) {
        JCommand command = new JCommand(name, description, this);
        if (autoHelp) {
            command.addOption("--help", "Show the help and exit", false);
        }
        commands.add(command);
        return command;
    }

    /**
     * Get the <code>JOption</code> by name.
     *
     * @param name the option name
     * @return the <code>JOption</code>
     */
    @Override
    public JOption getOption(String name) {
        if (command != null && command.hasOption(name)) {
            return command.getOption(name);
        }

        if (map.containsKey(name)) {
            return map.get(name);
        }

        throw new IllegalArgumentException("Unknown option: " + name);
    }

    /**
     * Check if the option present in arguments.
     *
     * @param name the option name
     * @return true if the option present
     */
    @Override
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

    /**
     * Parse arguments.
     *
     * @param args the arguments
     */
    public void parse(String[] args) {
        boolean checkedCommand = false;
        boolean optionsEnd = false;
        List<JOption> prompts = new ArrayList<JOption>();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            String name = null;
            String value = null;

            if (optionsEnd) {
                arguments.add(arg);
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

                if (option == null) {
                    logger.warning("Unknown option: " + name);
                    continue;
                }

                if (value == null) {
                    if (option.isHasValue()) {
                        if (option.isInteractive()) {
                            if (i + 1 == args.length || args[i + 1].equals("--") || isOption(args[i + 1])) {
                                option.setPresent(true);
                                prompts.add(option);
                                continue;
                            }
                        }
                        if (i + 1 == args.length || args[i + 1].equals("--")) {
                            throw new IllegalArgumentException("Missing required value for option " + option.getName());
                        }
                        value = args[++i];
                    } else {
                        value = Boolean.TRUE.toString();
                    }
                }
                option.setPresent(true);
                option.setValue(value);
            } else if (!checkedCommand) {
                for (JCommand command : commands) {
                    if (command.aliases.contains(arg)) {
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

        if (autoHelp) {
            if (isPresent("help")) {
                if (command != null) {
                    command.printHelp(output);
                } else {
                    printHelp();
                }
                System.exit(0);
            } else if (isCommand("help")) {
                printHelp();
                System.exit(0);
            }
        }

        if (!isPresent("help") && !isPresent("version")) {
            List<JParameter> parameters = getParameters();
            for (int i = 0; i < parameters.size(); ++i) {
                JParameter parameter = parameters.get(i);
                if (i < this.arguments.size()) {
                    parameter.setValue(this.arguments.get(i));
                } else if (parameter.isRequired()) {
                    throw new IllegalArgumentException("Missing required argument " + parameter.getName());
                }
            }
        }

        for (JOption option : prompts) {
            Console console = System.console();
            if (console == null) {
                throw new IllegalStateException("Cannot access the console device");
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
     * Display the help text, to the "standard" output stream by default.
     */
    public void printHelp() {
        printHelp(output);
    }

    /**
     * Display the help text to PrintStream.
     *
     * @param printStream the PrintStream
     */
    @Override
    public void printHelp(PrintStream printStream) {
        if (!arguments.isEmpty()) {
            String name = arguments.get(0);
            JCommand command = getCommandByName(name);
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

    private JCommand getCommandByName(String name) {
        for (JCommand command : commands) {
            if (command.aliases.contains(name)) {
                return command;
            }
        }
        return null;
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

}
