/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ddark008.sonycollections;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Java
 */
public class Sqllite {

    private Connection connection = null;

    public Sqllite(File db) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + db);

        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public int getBookID(int bookSize, String bookName) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("SELECT _id FROM books WHERE file_size = ? AND file_name = ?");
            st.setInt(1, bookSize);
            st.setString(2, bookName);
            st.setQueryTimeout(30);
            ResultSet rs = st.executeQuery();
            return rs.getInt("_id");
        } catch (SQLException ex) {
            Logger.getLogger(Sqllite.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(Sqllite.class.getName()).log(Level.SEVERE, null, ex);
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
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(Sqllite.class.getName()).log(Level.SEVERE, null, ex);
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
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(Sqllite.class.getName()).log(Level.SEVERE, null, ex);
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
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(Sqllite.class.getName()).log(Level.SEVERE, null, ex);
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
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(Sqllite.class.getName()).log(Level.SEVERE, null, ex);
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
            // connection close failed.
            System.err.println(e);
        }
    }
}
