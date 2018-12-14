package com.har01d.tool.jarg;

public class CommandTests {
    public static void main(String[] args) {
        Jarg jarg = new Jarg("Jarg", "Java arguments parser");
        jarg.setAutoHelp(true);
        jarg.addCommand("help", "Show this help text");
        jarg.addCommand("version", "Show the version");
        jarg.addOption("--debug|-d", "Show debug message", false);

        JCommand command = jarg.addCommand("test", "Test user credentials").aliases("login");
        command.addOption("--user|-u", "The username").setValueName("USERNAME");
        command.addOption("--password|-p", "The password").setValueName("PASSWORD");

        jarg.parse(args);
        if (jarg.isCommand("version")) {
            System.out.println("1.0.0");
        } else {
            System.out.println("Command: " + jarg.getCommandName());
            System.out.println("username: " + jarg.getValue("user"));
            System.out.println("password: " + jarg.getValue("password"));
            System.out.println("arguments: " + jarg.getArguments());
        }
    }
}
