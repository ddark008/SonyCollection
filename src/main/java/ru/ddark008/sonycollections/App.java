package ru.ddark008.sonycollections;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {

    public static File booksDB = null;
    public static File booksPath = null;
    public static Sqllite db = null;

    public static void main(String[] args) {
        System.out.println("SonyCollections starting..");

        File[] roots = File.listRoots();
        for (File file : roots) {
            File tmpPath = new File(file.getAbsolutePath() + "Sony_Reader/database/books.db");
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

        System.out.print("Database init ... ");
        db = new Sqllite(booksDB);
        System.out.println("success");

        System.out.print("Database BackUp ... ");
        db.backup(booksDB);

        try {
            booksPath = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Looking collection in " + booksPath.getParentFile());

        File[] fileList = booksPath.listFiles();
        for (File file : fileList) {
            if (file.isDirectory() && !isDirExcluded(file)) {
                System.out.println("ROOTDIR" + file);
                CreateCollection(file, "");
            }
        }
        db.close();
    }

    private static void Exit(String exp) {
        System.err.println(exp);
        db.close();
        System.exit(0);
    }

    private static void CreateCollection(File rootPath, String PartName) {

        //Проверяем есть ли в папке файлы
        File[] fileList = rootPath.listFiles();
        if (fileList.length > 0) {
            //Имя коллекциии имя_1_коолекции ~ имя_2_коллекции
            String collectionName = PartName + rootPath.getName();
            int collectionID = db.getCoolectionId(collectionName);

            if (collectionID < 0) {
                collectionID = db.addCollection(collectionName);
            }

            //Если не удалось создать коллекцию
            if (collectionID == -1) {
                Exit("Error: Can't create collection");
            } else {
                System.out.println("Collection: " + collectionName);
            }

            //Добаляем все книги в папке и подпапках рекурсивно в коллекцию
            addBooksRecursive(rootPath, collectionID);

            //Для каждой папки создаём дочернюю коллекцию вида имя_1_коолекции ~ имя_2_коллекции
            for (File file : fileList) {
                if (file.isDirectory() && !isDirExcluded(file)) {
                    System.out.println("CRCOLL " + file);
                    CreateCollection(file, collectionName + " ~ ");
                }
            }
        }
    }

    private static void addBooksRecursive(File rootPath, int CollectionID) {
        File[] fileList = rootPath.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                System.out.println("ADDBR" + file);
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
            System.err.println("Book don't cashed");
            return false;
        }
        if (db.bookInCollection(CollectionID, ID) > 0) {
            System.out.println("Book " + name + " already in collection");
            return true;
        } else {
            if (db.addBook(CollectionID, ID)) {
                System.out.println("Book " + name + " added to collection");
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
