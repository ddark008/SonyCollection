/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ddark008.sonycollections;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Java
 */
public class Sqllite {

    private static Logger log = Logger.getLogger(Sqllite.class.getName());

    /**
     * @param aLog the log to set
     */
    public static void setLog(Level lv) {
        log.setLevel(lv);
    }

    private Connection connection = null;

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

    public int bookInCollection(int ColId, int BookId) {
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

    /**
     *
     */
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
            java.util.Date today = new java.util.Date();
            SimpleDateFormat formatter = new SimpleDateFormat("ss.mm.HH.dd.MM.yyyy");
            String formattedDate = formatter.format(today);

            File f2 = new File(f1.getParentFile() + "/" + formattedDate + " " + f1.getName());

            log.debug("Input file " + f1.getAbsolutePath());
            log.debug("Output file " + f2.getAbsolutePath());

            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2, true);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            log.info("Backup database " + f2.getName() + " complete");
            return true;
        } catch (FileNotFoundException ex) {
            log.fatal(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }
}
