package org.example;

public class App
{
    public static void main(final String[] args) throws Exception {
        System.out.println("Manipulating HTML files.");
        String configfile = "HTMLmanipulator.cfg";
        if (args.length > 0) {
            configfile = args[0];
        }
        final HTMLmanipulator html_manipulator = new HTMLmanipulator();
        html_manipulator.run(configfile);
        html_manipulator.done();
    }
}
