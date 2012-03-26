package ru.ddark008.sonycollections;

import java.io.File;
import java.net.CookieHandler;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        System.out.println(booksPath.getParentFile().getName());

        CreateCollection(booksPath);


        db.close();
    }

    private static void Exit(String exp) {
        System.err.println(exp);
        db.close();
        System.exit(0);
    }

    private static void CreateCollection(File rootPath) {
        String collName = rootPath.getName();
        int collectionID = -1;
        if (db.getCoolectionId(collName) > 0) {
            collectionID = db.getCoolectionId(collName);
        } else {
            db.addCollection(collName);
            collectionID = db.getCoolectionId(collName);
        }

        if (collectionID == -1) {
            Exit("Error: Can't create collection");
        } else {
            System.out.println("Collection: " + collName);
        }

        File[] fileList = rootPath.listFiles();
        for (File file : fileList) {
            System.out.println(file);
            if (file.isFile()) {
                addBook(file, collectionID);
            } else {
                CreateCollection(file);
            }
        }

    }
    private static boolean addBook(File book, int CollectionID){
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
}
