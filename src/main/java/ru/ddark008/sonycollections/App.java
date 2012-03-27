package ru.ddark008.sonycollections;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger log = Logger.getLogger(App.class.getName());
    private static Arguments parametres = null;
    private static Sqllite db = null;

    public static void main(String[] args) {
        File booksDB = null;
        File booksPath = null;

        //Устанавливаем кодировку cp866 для Windows
        SystemOut.SetCharset();

        Arguments parametres = new Arguments(args);


        //Настраиваем уровень логгера
        log.setLevel(Level.INFO);
        if (parametres.isVerbose()) {
            log.setLevel(Level.DEBUG);
        }
        if (parametres.isSilent()) {
            log.setLevel(Level.OFF);
        }

        //Парсим параметры


        //Показываем помощь
        if (parametres.isHelp()) {
            parametres.showHelp();
        }

        log.info("SonyCollections запускается...");

        File[] roots = File.listRoots();
        for (File file : roots) {
            File tmpPath = new File(file.getAbsolutePath() + "Sony_Reader/database/books.db");
            log.debug(tmpPath);
            if (tmpPath.exists() && tmpPath.length() > 0) {
                booksDB = tmpPath;
            }
        }
        if (booksDB == null) {
            Exit("The base collection is not found, connect the book");
        }
        if (!booksDB.canWrite()) {
            Exit("The base collection is not available to write");
        }

        log.debug("Database init ... ");
        db = new Sqllite(booksDB);
        log.debug("success");

        //Делаем бекап, на всякий пожарный
        db.backup(booksDB);

        try {
            booksPath = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException ex) {
            log.error(ex);
        }

        log.info("Looking collection in " + booksPath);

        File[] fileList = booksPath.listFiles();
        for (File file : fileList) {
            if (file.isDirectory() && !isDirExcluded(file)) {
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
                Exit("Error: Can't create collection");
            } else {
                log.info("Collection: " + collectionName);
            }

            //Добаляем все книги в папке и подпапках рекурсивно в коллекцию
            addBooksRecursive(rootPath, collectionID);

            //Для каждой папки создаём дочернюю коллекцию вида имя_1_коолекции ~ имя_2_коллекции
            for (File file : fileList) {
                if (file.isDirectory() && !isDirExcluded(file)) {
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
            } else if (!isDirExcluded(file)) {
                addBooksRecursive(file, CollectionID);
            }
        }
    }

    private static boolean addBook(File book, int CollectionID) {
        String name = book.getName();
        long size = book.length();
        int ID = db.getBookID(size, name);
        if (ID < 0) {
            log.error("Book " + name + " don't cashed");
            return false;
        }
        if (db.bookInCollection(CollectionID, ID) > 0) {
            log.info("Book " + name + " already in collection");
            return true;
        } else {
            if (db.addBook(CollectionID, ID)) {
                log.info("Book " + name + " added to collection");
                return true;
            }
        }

        return false;

    }

    private static boolean isDirExcluded(File dir) {
        //TODO: Добавить чтение из файла, убрать ru
        if (dir.getName().startsWith("~!") || dir.getName().startsWith("ru")) {
            return true;
        }
        return false;
    }
}
