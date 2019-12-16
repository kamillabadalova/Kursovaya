package Kursovaya;

import javafx.fxml.FXML;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseHandler extends Configs {
    public static final String table = "data";
    public static final String year = "Годы";
    public static final String num = "Единиц";
   static   Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + 
                "?useUnicode=true&serverTimezone=UTC&useSSL=true&verifyServerCertificate=false";
        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString,
                dbUser, dbPass);
        return dbConnection;
    }

    public void signUpUser(String firstName, String surName, String login, String password, String mail, String location) {
            String insert = "INSERT INTO " + Const.USER_TABLE + "(" +
            Const.USERS_NAME + "," + Const.USERS_SURNAME + ","+
            Const.USERS_LOGIN + "," + Const.USERS_PASSWORD + ","+
            Const.USERS_MAIL + "," + Const.USERS_LOCATION + ")" + "VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1,firstName);
            prSt.setString(2,surName);
            prSt.setString(3,login);
            prSt.setString(4,password);
            prSt.setString(5,mail);
            prSt.setString(6,location);
            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public  void addtable() {
    try {
        // так как мы уже скачали
        File file = new File("C:\\Users\\Kamilla Badalova\\Desktop\\data.csv");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String line = reader.readLine();
        String delimiter=";";
        while (line != null) {
            String[] fields = line.split(delimiter, 2);
            String insert = "INSERT INTO " + table + "(" + year + "," + num +")"+ " VALUES(?,?)";
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, fields[0]);
            prSt.setString(2, fields[1]);
            prSt.executeUpdate();
            line = reader.readLine();
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (SQLException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    }
   @FXML
    public void fileDownload()  {
        try {
            URL url = new URL("https://github.com/kamillabadalova/lab9/blob/master/data.csv");
            InputStream inputStream = url.openStream();
            File file=new File("C:\\Users\\Kamilla Badalova\\Desktop\\data.csv");
            if(!file.exists()) {
                Files.copy(inputStream, new File("C:\\Users\\Kamilla Badalova\\Desktop\\data.csv").toPath());
            }else{
                System.out.println("Файл уже существует");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
