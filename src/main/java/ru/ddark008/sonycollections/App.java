package ru.ddark008.sonycollections;

import java.io.File;
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

    public static File booksdb = null;

    public static void main(String[] args) {
        System.out.println("SonyCollections starting..");

        File[] roots = File.listRoots();
        for (File file : roots) {
            File tmpPath = new File(file.getAbsolutePath() + "Sony_Reader/database/books.db");
            if (tmpPath.exists() && tmpPath.length() > 0) {
                booksdb = tmpPath;
            }
        }
        if (booksdb == null) {
            Exit("The base collection is not found, connect the book");
        }
        if (!booksdb.canWrite()) {
            Exit("The base collection is not available to write");
        }
        Sqllite db = new Sqllite(booksdb);
        System.out.println(db.getBookID(5850,"download.fb2.zip"));
        db.addBook(db.getCoolectionId("Test"), db.getBookID(5850,"download.fb2.zip"));
        db.close();
    }

    private static void Exit(String exp) {
        System.err.println(exp);
        System.exit(0);
    }

    private static void getBookId(){

    }
}
