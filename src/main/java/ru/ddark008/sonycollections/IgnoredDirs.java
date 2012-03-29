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

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @mail dev@ddark008.ru
 *
 * @author ddark008
 */
public class IgnoredDirs {

    private static List<String> wordList = new ArrayList<String>();
    private static final Logger log = Logger.getLogger(IgnoredDirs.class.getName());

    public IgnoredDirs(File rootDir) {
        File list = new File(rootDir + "/scignore.txt");

        log.debug(list.getAbsoluteFile());

        if (list.exists() && list.isFile() && list.length() > 0) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(list));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        wordList.add(line);
                        log.debug(MessageFormat.format(Main.localization.getString("WORLD {0}ADDED TO IGNOR"), line));
                    }

                }
                br.close();
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            try {
                log.debug(Main.localization.getString("IGNORE LIST IS NOT FOUND, CREATE A NEW"));
                list.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(list, true));
                bw.write(Main.localization.getString("ignore_file_str_1"));
                bw.write(Main.localization.getString("ignore_file_str_2"));
                bw.write(Main.localization.getString("ignore_file_str_3"));
                bw.write("~!");
                bw.close();
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }

    public boolean isDirExcluded(File dir) {
        for (String mask : wordList) {
            if (dir.getName().startsWith(mask)) {
                log.info(MessageFormat.format(Main.localization.getString("DIRECTORY{0}EXCLUDED"), dir));
                return true;
            }
        }
        return false;
    }

    /**
     * @param lv
     *
     */
    public static void setLog(Level lv) {
        log.setLevel(lv);
    }
}
