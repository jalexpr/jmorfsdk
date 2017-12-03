package jmorfsdk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BDSqlite {

    private Statement statmt;
    
    //dictionary.format.wordFormString.db
    public BDSqlite(String nameBD) {
        Connection conn;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + nameBD);
            statmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(BDSqlite.class.getName()).log(Level.SEVERE, "База не подключена!", ex);
        }

        System.out.println("База Подключена!");
    }
    
    public void execute(String query) {
        try {
            statmt.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(BDSqlite.class.getName()).log(Level.SEVERE, "Не удалось выполнить запрос: " + query, ex);
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            return statmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(BDSqlite.class.getName()).log(Level.SEVERE, "Не удалось выполнить запрос: " + query, ex);
            throw new RuntimeException();
        }
    }

    public void closeDB(){
        try {
            statmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(BDSqlite.class.getName()).log(Level.SEVERE, "Соединения закрыты с ошибкой", ex);
        }

        System.out.println("Соединения c бд закрыта!");
    }
}
