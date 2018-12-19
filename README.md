# Jarg

#### Description
A simple Java arguments parser

#### Software Architecture
Software architecture description

#### Installation

Maven:
```xml
<dependency>
    <groupId>com.har01d.tool</groupId>
    <artifactId>jarg</artifactId>
    <version>1.0</version>
</dependency>
```

#### Instructions

```java
public class HttpClient {
    public static void main(String[] args) {
        Jarg jarg = new Jarg("http-client", "A simple HTTP client");
        jarg.autoHelp();

        jarg.setDescription("Connect to a HTTP server.\nSupport basic authentication.");
        jarg.addSection(Jarg.AUTHOR, "Harold");

        jarg.addOption("--username|-u", "The username");
        jarg.addOption("--password|-p", "The password").interactive();

        jarg.addOption("--version|-v", "Show the version", false);
        jarg.addOption("--help|-h", "Show this help text", false);

        jarg.addParameter("host").required();
        jarg.addParameter("port").defaultValue(80);

        try {
            jarg.parse(args);
        } catch (Exception e) {
            jarg.handleError(e);
        }

        if (jarg.isPresent("version")) {
            System.out.println("1.0.0");
        } else {
            System.out.println("username: " + jarg.getValue("username"));
            System.out.println("password: " + jarg.getValue("password"));

            System.out.println("host: " + jarg.getArgument("host"));
            System.out.println("port: " + jarg.getArgument("port"));

            System.out.println("arguments: " + jarg.getArguments());
        }
    }
}
```

#### Contribution

1. Fork the project
2. Create Feat_xxx branch
3. Commit your code
4. Create Pull Request
