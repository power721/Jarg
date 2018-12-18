package com.har01d.tool.jarg;

import java.io.Console;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class Jarg extends JCommand {

    private static final Logger logger = Logger.getLogger(Jarg.class.getName());

    private final List<JCommand> commands = new ArrayList<JCommand>();

    private final List<String> arguments = new ArrayList<String>();
    private boolean autoHelp;
    private JCommand command;

    private String author;
    private String copyright;

    public Jarg(String name) {
        super(name, null);
    }

    public Jarg(String name, String description) {
        super(name, description);
    }

    public Jarg setAutoHelp(boolean autoHelp) {
        this.autoHelp = autoHelp;
        return this;
    }

    public Jarg setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Jarg setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getArgument(int index) {
        return arguments.get(index);
    }

    public JCommand requireCommand() {
        if (command == null) {
            printHelp(System.out);
            System.exit(1);
        }
        return command;
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
        List<JOption> prompts = new ArrayList<JOption>();
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

        for (JOption option : prompts) {
            Console console = System.console();
            if (console == null) {
                System.err.println("Cannot access the console device");
                System.err.println("Specific value in command line for option " + option.getName());
                System.exit(0);
            }
            char[] password = console.readPassword("Enter value of %s:", option.getName());
            option.setValue(new String(password));
        }
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
        if (!arguments.isEmpty()) {
            String name = arguments.get(0);
            JCommand command = getCommand(name);
            if (command != null) {
                command.printHelp(printStream);
                return;
            }
        }

        printStream.println("NAME");
        printStream.println(indent(4) + getName() + (getSummary() == null ? "" : "  -    " + getSummary()));

        if (synopsis != null) {
            printStream.println("SYNOPSIS");
            printStream.print(indentLines(synopsis, 4));
        }

        printOptions(printStream);
        printCommands(printStream);

        if (author != null) {
            printStream.println("AUTHOR");
            printStream.print(indentLines(author, 4));
        }

        if (copyright != null) {
            printStream.println("COPYRIGHT");
            printStream.print(indentLines(copyright, 4));
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
