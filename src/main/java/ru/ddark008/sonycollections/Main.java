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

import java.io.File;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @mail dev@ddark008.ru
 * @author ddark008
 */
public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());
    private static Arguments parametres = null;
    private static Sqllite db = null;
    public static final ResourceBundle localization = ResourceBundle.getBundle("localization/en");
    private static IgnoredDirs ignor = null;

    public static void main(String[] args) {
        File booksDB = null;
        File booksPath = null;

        //Устанавливаем кодировку cp866 для Windows
        SystemOut.SetCharset();

        //Настраиваем уровень логгера
        log.setLevel(Level.INFO);
        SystemOut.setLog(Level.INFO);
        Sqllite.setLog(Level.INFO);
        IgnoredDirs.setLog(Level.INFO);

        //Парсим параметры
        parametres = new Arguments(args);

        if (parametres.isVerbose()) {
            log.setLevel(Level.DEBUG);
            SystemOut.setLog(Level.DEBUG);
            Sqllite.setLog(Level.DEBUG);
            IgnoredDirs.setLog(Level.DEBUG);
        }

        if (parametres.isSilent()) {
            log.setLevel(Level.OFF);
            SystemOut.setLog(Level.OFF);
            Sqllite.setLog(Level.OFF);
            IgnoredDirs.setLog(Level.OFF);
        }

        //Показываем помощь
        if (parametres.isHelp()) {
            parametres.showHelp();
        }
        log.info(localization.getString("SONYCOLLECTIONS STARTING..."));

        File[] roots = File.listRoots();
        for (File file : roots) {
            File tmpPath = new File(file.getAbsolutePath() + "Sony_Reader/database/books.db");
            log.debug(MessageFormat.format(localization.getString("LOOKING DATABASE {0}"), tmpPath));
            if (tmpPath.exists() && tmpPath.length() > 0) {
                booksDB = tmpPath;
            }
        }
        if (booksDB == null) {
            Exit(localization.getString("THE BASE COLLECTION IS NOT FOUND, CONNECT THE BOOK"));
        }
        if (!booksDB.canWrite()) {
            Exit(localization.getString("THE BASE COLLECTION IS NOT AVAILABLE TO WRITE"));
        }

        log.debug(localization.getString("DATABASE INIT ... "));
        db = new Sqllite(booksDB);
        log.debug(localization.getString("SUCCESS"));

        //Делаем бекап, на всякий пожарный
        db.backup(booksDB);

        try {
            booksPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException ex) {
            log.error(ex);
        }

        //Парсим папки исключения
        ignor = new IgnoredDirs(booksPath);

        log.info(MessageFormat.format(localization.getString("LOOKING COLLECTION IN {0}"), booksPath));

        File[] fileList = booksPath.listFiles();
        for (File file : fileList) {
            if (file.isDirectory() && !ignor.isDirExcluded(file)) {
                log.debug("ROOTDIR: " + file);
                CreateCollection(file, "");
            }
        }
        db.close();
    }

    private static void Exit(String exp) {
        log.error(exp);
        db.close();
        System.exit(0);
    }

    private static void CreateCollection(File rootPath, String PartName) {

        //Проверяем есть ли в папке файлы
        File[] fileList = rootPath.listFiles();
        if (fileList.length > 0) {
            String collectionName = PartName + rootPath.getName();
            int collectionID = db.getCoolectionId(collectionName);

            if (collectionID < 0) {
                collectionID = db.addCollection(collectionName);
            }

            //Если не удалось создать коллекцию
            if (collectionID == -1) {
                Exit(localization.getString("ERROR: CAN'T CREATE COLLECTION"));
            } else {
                log.info(MessageFormat.format(localization.getString("COLLECTION: {0}"), collectionName));
            }

            //Добаляем все книги в папке и подпапках рекурсивно в коллекцию
            if (!parametres.getRecursive()) {
                addBooksRecursive(rootPath, collectionID);
            }

            //Для каждой папки создаём дочернюю коллекцию вида имя_1_коолекции ~ имя_2_коллекции
            for (File file : fileList) {
                if (file.isDirectory() && !ignor.isDirExcluded(file)) {
                    log.debug("CRCOLL: " + file);
                    CreateCollection(file, collectionName + " " + parametres.getTilde() + " ");
                }
            }
        }
    }

    private static void addBooksRecursive(File rootPath, int CollectionID) {
        File[] fileList = rootPath.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                log.debug("ADDBR" + file);
                addBook(file, CollectionID);
            } else if (!ignor.isDirExcluded(file)) {
                addBooksRecursive(file, CollectionID);
            }
        }
    }

    private static boolean addBook(File book, int CollectionID) {
        String name = book.getName();
        long size = book.length();
        int ID = db.getBookID(size, name);
        if (ID < 0) {
            log.error(MessageFormat.format(localization.getString("BOOK {0} DON'T CASHED"), name));
            return false;
        }
        if (db.bookInCollection(CollectionID, ID) > 0) {
            log.info(MessageFormat.format(localization.getString("BOOK {0} ALREADY IN COLLECTION"), name));
            return true;
        } else {
            if (db.addBook(CollectionID, ID)) {
                log.info(MessageFormat.format(localization.getString("BOOK {0} ADDED TO COLLECTION"), name));
                return true;
            }
        }

        return false;

    }
}
