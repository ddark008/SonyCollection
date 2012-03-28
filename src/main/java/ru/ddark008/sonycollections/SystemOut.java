/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ddark008.sonycollections;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Java
 */
public class SystemOut {
private static Logger log = Logger.getLogger(SystemOut.class.getName());

    public static void SetCharset() {
        log.debug("System " + getPlatform());
        if (getPlatform().ordinal() == 2){
            try {
                System.setOut(new PrintStream(System.out, true, "cp866"));
                System.setErr(new PrintStream(System.err, true, "cp866"));
                log.debug("Encoded change from UTF-8 to cp866");
            } catch (UnsupportedEncodingException ex) {
                log.error(ex);
            }
        }
    }

    private static OS getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
			return OS.windows;
		if (osName.contains("mac"))
			return OS.macos;
		if (osName.contains("solaris"))
			return OS.solaris;
		if (osName.contains("sunos"))
			return OS.solaris;
		if (osName.contains("linux"))
			return OS.linux;
		if (osName.contains("unix"))
			return OS.linux;
		return OS.unknown;
	}

    /**
     * @param aLog the log to set
     */
    public static void setLog(Level lv) {
        log.setLevel(lv);
    }

    private static enum OS {
		linux, solaris, windows, macos, unknown;
	}
}
