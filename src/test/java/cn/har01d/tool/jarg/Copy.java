package cn.har01d.tool.jarg;

public class Copy {
    public static void main(String[] args) {
        Jarg jarg = new Jarg("cp", "copy files and directories");
        jarg.setDescription("Copy SOURCE to DEST, or multiple SOURCE(s) to DIRECTORY.");
        jarg.addSection(Jarg.AUTHOR, "Written by Torbjorn Granlund, David MacKenzie, and Jim Meyering.");
        jarg.addSection(Jarg.REPORTING_BUGS, "GNU coreutils online help: <http://www.gnu.org/software/coreutils/>\nReport cp translation bugs to <http://translationproject.org/team/>");
        jarg.addSection(Jarg.COPYRIGHT, "Copyright © 2017 Free Software Foundation, Inc.  License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\nThis is free software: you are free to change and redistribute it.  There is NO WARRANTY, to the extent permitted by law.");
        jarg.addSection(Jarg.SEE_ALSO, "Full documentation at: <http://www.gnu.org/software/coreutils/cp>\nor available locally via: info '(coreutils) cp invocation'");
        jarg.addOption("-a|--archive", "same as -dR --preserve=all", false);
        jarg.addOption("--attributes-only", "don't copy the file data, just the attributes", false);
        jarg.addOption("-f|--force", "if an existing destination file cannot be opened, remove it and try again (this option is ignored when the -n option is also used)", false);
        jarg.addOption("-i|--interactive", "prompt before overwrite (overrides a previous -n option)", false);
        jarg.addOption("-p", "same as --preserve=mode,ownership,timestamps", false);
        jarg.addOption("--preserve", "preserve the specified attributes (default: mode,ownership,timestamps), if possible additional attributes: context, links, xattr, all").setLabel("ATTR_LIST");
        jarg.addOption("-R|-r|--recursive", "copy directories recursively", false);
        jarg.addOption("--version", "output version information and exit", false);
        jarg.addOption("--help", "display this help and exit", false);
        jarg.addParameter("source").required();
        jarg.addParameter("directory").required();

        try {
            jarg.parse(args);
        } catch (Exception e) {
            jarg.handleError(e);
        }
    }
}
