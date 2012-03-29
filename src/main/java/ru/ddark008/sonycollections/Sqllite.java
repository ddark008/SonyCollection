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

/**
 * @mail dev@ddark008.ru
 *
 * @author ddark008
 */
import java.io.*;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Sqllite {

    private Connection connection = null;
    private static Logger log = Logger.getLogger(Sqllite.class.getName());

    public static void setLog(Level lv) {
        log.setLevel(lv);
    }

    public Sqllite(File db) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            log.error(ex);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + db);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    /**
     *
     * @param bookSize
     * @param bookName
     * @return
     */
    public int getBookID(long bookSize, String bookName) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("SELECT _id FROM books WHERE file_size = ? AND file_name = ?");
            st.setLong(1, bookSize);
            st.setString(2, bookName);
            st.setQueryTimeout(30);
            ResultSet rs = st.executeQuery();
            return rs.getInt("_id");
        } catch (SQLException ex) {
            log.debug(ex);
            return -1;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    public int getCoolectionId(String CollName) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("SELECT _id FROM collection WHERE title = ?");
            st.setString(1, CollName);
            st.setQueryTimeout(30);
            ResultSet rs = st.executeQuery();
            return rs.getInt("_id");
        } catch (SQLException ex) {
            log.debug(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return -1;
    }

    public String getCoolectionName(int CollectionsID) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("SELECT title FROM collection WHERE  _id = ?");
            st.setInt(1, CollectionsID);
            st.setQueryTimeout(30);
            ResultSet rs = st.executeQuery();
            return rs.getString(1);
        } catch (SQLException ex) {
            log.debug(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return Main.localization.getString("THE INVISIBLE COLLECTION");
    }

    /**
     *
     * @param CollName
     * @return
     */
    public int addCollection(String CollName) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO collection  VALUES ( NULL, ? , NULL , 0 , NULL )");
            st.setString(1, CollName);
            st.setQueryTimeout(30);
            st.executeUpdate();

            st = connection.prepareStatement("SELECT _id FROM collection WHERE title = ?");
            st.setString(1, CollName);
            ResultSet rs = st.executeQuery();
            return rs.getInt("_id");
        } catch (SQLException ex) {
            log.error(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return -1;
    }

    public int isBookInCollection(int ColId, int BookId) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("SELECT _id FROM collections WHERE content_id = ? AND collection_id = ?");
            st.setInt(1, BookId);
            st.setInt(2, ColId);
            st.setQueryTimeout(30);
            ResultSet rs = st.executeQuery();
            return rs.getInt("_id");
        } catch (SQLException ex) {
            log.debug(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return -1;
    }

    public boolean isCollectionHaveBooks(int ColId) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("SELECT content_id FROM collections WHERE collection_id = ?");
            st.setInt(1, ColId);
            st.setQueryTimeout(30);
            ResultSet rs = st.executeQuery();
            rs.getInt(1);
            return true;
        } catch (SQLException ex) {
            log.debug(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return false;
    }

    /**
     * Возвращает список ID всех коллекций
     *
     * @return
     */
    public ArrayList<Integer> getCollectionsID() {
        PreparedStatement st = null;
        ArrayList<Integer> collList = new ArrayList<Integer>();
        try {
            st = connection.prepareStatement("SELECT _id FROM collection");
            st.setQueryTimeout(30);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                collList.add(rs.getInt(1));
                log.debug(MessageFormat.format(Main.localization.getString("COLLECTIONS FIND : {0}"), rs.getInt(1)));
            }
            return collList;
        } catch (SQLException ex) {
            log.debug(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return collList;

    }

    public boolean deleteCollection(int CollId) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM collection  WHERE _id = ?");
            st.setInt(1, CollId);
            st.setQueryTimeout(30);
            st.executeUpdate();
            log.debug(MessageFormat.format(Main.localization.getString("COLLECTION {0}DELETED"), CollId));
            return true;
        } catch (SQLException ex) {
            log.error(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return false;
    }

    public boolean addBook(int CollId, int BookId) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO collections  VALUES ( NULL, ? , ? , NULL )");
            st.setInt(1, CollId);
            st.setInt(2, BookId);
            st.setQueryTimeout(30);
            st.executeUpdate();
            return true;
        } catch (SQLException ex) {
            log.error(ex);
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return false;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public boolean backup(File f1) {
        try {
            //Удаляем старые архивы оставляем последние 5
            File[] root = f1.getParentFile().listFiles();
            ArrayList<File> v = new ArrayList();
            for (File file: root) {
                if (file.getName().endsWith(" books.db")) {
                    v.add(file);
                }
                }
            Collections.sort(v);
            for (int i = 1; i <= v.size() -5; i++ ){
                v.get(i).delete();
                log.debug(MessageFormat.format(Main.localization.getString("BACKUP {0} DETETED"), v.get(i)));
            }

            java.util.Date today = new java.util.Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            String formattedDate = formatter.format(today);

            File f2 = new File(f1.getParentFile() + "/" + formattedDate + " " + f1.getName());

            log.debug(MessageFormat.format(Main.localization.getString("INPUT FILE {0}"), f1.getAbsolutePath()));
            log.debug(MessageFormat.format(Main.localization.getString("OUTPUT FILE {0}"), f2.getAbsolutePath()));

            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2, true);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            log.info(MessageFormat.format(Main.localization.getString("BACKUP DATABASE {0} COMPLETE"), f2.getName()));
            return true;
        } catch (FileNotFoundException ex) {
            log.fatal(ex.getMessage() + Main.localization.getString(" IN THE SPECIFIED DIRECTORY."));
            System.exit(0);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }
}
