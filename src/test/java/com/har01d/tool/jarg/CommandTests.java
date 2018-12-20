package com.har01d.tool.jarg;

public class CommandTests {
    public static void main(String[] args) {
        Jarg jarg = new Jarg("Jarg", "Java arguments parser");
        jarg.autoHelp();
        jarg.setSynopsis("jarg COMMAND [OPTIONS]...");
        jarg.addCommand("help", "Show this help text");
        jarg.addCommand("version", "Show the version");
        jarg.addOption("--debug|-d", "Show debug message", false);

        JCommand login = jarg.addCommand("test", "Test user credentials").aliases("login");
        login.addOption("--user|-u", "The username").defaultValue("admin").setLabel("USERNAME");
        login.addOption("--password|-p", "The password");
        login.addParameter("host", true);
        login.addParameter("port", true);

        jarg.parse(args);

        if (jarg.isCommand("version")) {
            System.out.println("1.0.0");
        } else {
            JCommand command = jarg.requireCommand();
            System.out.println("Command: " + jarg.getCommandName());
            System.out.println("\nOptions:");
            System.out.println("username: " + command.getValue("user"));
            System.out.println("password: " + command.getValue("password"));
            System.out.println("\nArguments:");
            System.out.println("host: " + command.getArgument("host"));
            System.out.println("port: " + command.getArgument("port"));
            if (command.getArgumentSize() > 2) {
                System.out.println("Argument 3: " + command.getArgument(2));
            }
            System.out.println("arguments: " + command.getArguments());
        }
    }
}
