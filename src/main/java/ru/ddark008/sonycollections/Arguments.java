/*
 * Copyright 2012 ddark008.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/
 * or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *
 * You are free:
 * to Share — to copy, distribute and transmit the work
 * to Remix — to adapt the work
 */

package ru.ddark008.sonycollections;

import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import static org.kohsuke.args4j.ExampleMode.ALL;

/**
 * @mail dev@ddark008.ru
 *
 * @author ddark008
 */
public class Arguments {
    //Устанавливаем параметры

    @Option(name = "-?", usage = "Help")
    private boolean help;
    @Option(name = "-v", usage = "Наиболее подробный вывод")
    private boolean verbose;
    @Option(name = "-s", usage = "Без вывода в консоль")
    private boolean silent;
    @Option(name = "-t", usage = "Знак разделения названий коллекций, по умолчанию <~>")
    private String tilde = "~";
    @Option(name = "-nonr", usage = "Не добавлять в коллекцию книги из подпапок")
    private boolean nonrecursive;
    @Argument
    private List<String> arguments = new ArrayList<String>();
    CmdLineParser parser = null;

    public Arguments(String[] args) {
        parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            showHelp();
        }
    }

    public void showHelp() {
        System.err.println("java SonyCollections [options...] arguments...");
        parser.printUsage(System.err);
        System.err.println();
        System.err.println(" Example: java SonyCollections" + parser.printExample(ALL));
        System.exit(0);
    }

    /**
     * @return the help
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * @return the verbose
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @return the silent
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * @return the tilde
     */
    public String getTilde() {
        return tilde;
    }

    /**
     * @return the recursive
     */
    public boolean getRecursive() {
        return nonrecursive;
    }
}
