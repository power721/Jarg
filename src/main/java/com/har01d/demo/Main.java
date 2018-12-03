package com.har01d.demo;

import com.har01d.tool.jarg.Jarg;

public class Main {
    public static void main(String[] args) {
        Jarg jarg = new Jarg("test");
        jarg.addOption("--user|-u", "The username");
        jarg.addOption("--password|-p", "The password");
        jarg.addOption("--version|-v", "Show the version", false);
        jarg.addOption("--help|-h", "Show this help text", false);

        jarg.parse(args);
        if (jarg.isPresent("help")) {
            jarg.printHelp(System.out);
        } else if (jarg.isPresent("version")) {
            System.out.println("1.0.0");
        } else {
            System.out.println("username: " + jarg.getValue("user"));
            System.out.println("password: " + jarg.getValue("password"));
            System.out.println("arguments: " + jarg.getArguments());
        }
    }
}
