/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ddark008.sonycollections;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Java
 */
public class SystemOut {

    public static void SetСharset() {
        if (getPlatform().ordinal() == 2){
            try {
                System.setOut(new PrintStream(System.out, true, "cp866"));
                System.setErr(new PrintStream(System.err, true, "cp866"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SystemOut.class.getName()).log(Level.SEVERE, null, ex);
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

    private static enum OS {
		linux, solaris, windows, macos, unknown;
	}
}
