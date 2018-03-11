import template.wrapper.classes.BDSqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestBD {
    public static void main(String[] args) {
//        BufferedReader reader = FileOpen.openBufferedReaderStream("dictionary.format.wordFormString.txt", "Windows-1251");
        BDSqlite myBD = new BDSqlite("dictionary.format.wordFormString.bd");
//        myBD.execute("CREATE TABLE if not exists 'WordForm' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'))");
//        try {
//            int count = 0;
//            myBD.execute("BEGIN TRANSACTION");
//            while(reader.ready()) {
//                myBD.execute(String.format("INSERT INTO 'WordForm' ('id','StringForm') VALUES (%d, '%s'); ", count, reader.readLine()));
//                count++;
//            }
//            myBD.execute("END TRANSACTION");
//        } catch (IOException ex) {
//            Logger.getLogger(BDSqlite.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        int id = 1500000;
//        long start = new Date().getTime();
//        ResultSet resultSet = myBD.executeQuery(String.format("SELECT * FROM  'WordForm' where id = %d", id));
//        long end = new Date().getTime();
//        System.out.println("Время выполнения запроса: " + (end - start));
//        
//                
//        try {
//            while (resultSet.next()) {
//                System.out.println(resultSet.getObject("StringForm") + " id " + resultSet.getObject("id"));
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(BDSqlite.class.getName()).log(Level.SEVERE, null, ex);
//        }

        ResultSet resultSet = myBD.executeQuery(String.format("SELECT count(*) FROM  'Form' "));


        try {
            while (resultSet.next()) {
                System.out.println(resultSet.getObject("count(*)"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDSqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
