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

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @mail dev@ddark008.ru
 *
 * @author ddark008
 */
public class SystemOut {

    private static Logger log = Logger.getLogger(SystemOut.class.getName());

    public static void SetCharset() {
        log.debug(MessageFormat.format(Main.localization.getString("SYSTEM {0}"), getPlatform()));
        if (getPlatform().ordinal() == 2) {
            try {
                System.setOut(new PrintStream(System.out, true, "cp866"));
                System.setErr(new PrintStream(System.err, true, "cp866"));
                log.debug(Main.localization.getString("ENCODED CHANGE FROM UTF-8 TO CP866"));
            } catch (UnsupportedEncodingException ex) {
                log.error(ex);
            }
        }
    }

    private static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OS.windows;
        }
        if (osName.contains("mac")) {
            return OS.macos;
        }
        if (osName.contains("solaris")) {
            return OS.solaris;
        }
        if (osName.contains("sunos")) {
            return OS.solaris;
        }
        if (osName.contains("linux")) {
            return OS.linux;
        }
        if (osName.contains("unix")) {
            return OS.linux;
        }
        return OS.unknown;
    }

    public static void setLog(Level lv) {
        log.setLevel(lv);
    }

    private static enum OS {
        linux, solaris, windows, macos, unknown;
    }
}
