package com.har01d.tool.jarg;

public class HttpClient {
    public static void main(String[] args) {
        // create Jarg with name and summary. The name and summary is used to generate help.
        Jarg jarg = new Jarg("http-client", "A simple HTTP client");
        jarg.autoHelp(); // Print help and exit when the help option or help command present.

        // set Description for help
        jarg.setDescription("Connect to a HTTP server.\nSupport basic authentication.");
        // add an AUTHOR section for help
        jarg.addSection(Jarg.AUTHOR, "Harold");

        // add an option, support long option "--username" and short option "-u".
        jarg.addOption("--username|-u", "The username");
        // add a password option, the value can read from console with echoing disabled
        jarg.addOption("--password|-p", "The password").interactive();

        // add a flag option which doesn't have value, the option present indicate a true value.
        jarg.addOption("--version|-v", "Show the version", false);
        jarg.addOption("--help|-h", "Show this help text", false);

        // add a mandatory parameter
        jarg.addParameter("host").required();
        // add an optional parameter with default value "80"
        jarg.addParameter("port").defaultValue(80);

        try {
            // parse the arguments
            jarg.parse(args);
        } catch (Exception e) {
            // Print error message, help message and exit with code 1
            jarg.handleError(e);
        }

        // check if the option "version" is present
        if (jarg.isPresent("version")) {
            System.out.println("1.0.0");
        } else {
            // get the option value
            System.out.println("username: " + jarg.getValue("username"));
            System.out.println("password: " + jarg.getValue("password"));

            // get the argument value
            System.out.println("host: " + jarg.getArgument("host"));
            System.out.println("port: " + jarg.getArgument("port"));

            // get all the arguments
            System.out.println("arguments: " + jarg.getArguments());
        }
    }
}
