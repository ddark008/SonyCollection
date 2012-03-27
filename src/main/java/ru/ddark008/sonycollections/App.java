package ru.ddark008.sonycollections;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Level;
// java.util.logging.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import static org.kohsuke.args4j.ExampleMode.ALL;
import org.kohsuke.args4j.Option;

/**
 * Hello world!
 *
 */
public class App {

    public static File booksDB = null;
    public static File booksPath = null;
    public static Sqllite db = null;
    //Устанавливаем параметры
    @Option(name = "-?", usage = "Наиболее подробный вывод")
    private boolean help;
    @Option(name = "-v", usage = "Наиболее подробный вывод")
    private boolean verbose;
    @Option(name = "-s", usage = "Без вывода в консоль")
    private boolean silent;
    @Option(name = "-t", usage = "Знак разделения названий коллекций, по умолчанию <~>")
    private String tilde = "(default value)";
    @Option(name = "-r", usage = "глубина <N> рекурсивного добавления книг, по умолчанию <-1> (бесконечна). Например # -r 0 # Для отключения")
    private int recursive = -1;
    @Argument
    private List<String> arguments = new ArrayList<String>();

   // private static final Logger log = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        //Настраиваем уровень логгера
      //  log.setLevel(Level.INFO);

        //Парсим параметры
        CmdLineParser parser = new CmdLineParser(args);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");

            parser.printUsage(System.err);
            System.err.println();

            System.err.println(" Example: java SampleMain" + parser.printExample(ALL));
        }



        //Устанавливаем кодировку cp866 для Windows
     //   SystemOut.SetCharset();

        System.out.println("Запускается");
     //   log.info("SonyCollections запускается");
        System.exit(0);

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
            booksPath = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException ex) {
       //FIXME: log.error(ex);
        }
        System.out.println("Looking collection in " + booksPath.getParentFile());
        System.out.println("Looking collection in " + booksPath);

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
