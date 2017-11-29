/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerPack;

import AlgoClustering.Centroid;
import MyPack.MySingleImage;
import MyPack.MySingleLog;
import MyPack.userInfo;
import java.io.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Vector;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author PC 1
 */
@WebService(serviceName = "ServerWebService")
public class ServerWebService {

    Statement stmt;
    String user = "root";
    String password = "root";
    String ssql;
    String connection = "jdbc:mysql://localhost/DDOS";
    Connection con = null;
    ResultSet rs;
    int currOutput = 0;
    Vector<String> SqlInjection;
    Vector<SingleIP> allLogIP = new Vector<SingleIP>();

    @WebMethod(operationName = "VerifyDOS")
    public MySingleImage VerifyDOS(@WebParam(name = "Si") MySingleImage Si) {
        //TODO write your implementation code here:
        Calendar cal = Calendar.getInstance();
        MySingleLog log = new MySingleLog();
        log.ip = Si.ip;
        log.currtime = cal.getTimeInMillis();
        log.timeSinceLast = getLastTimeStamp(log.ip);
        log.reqtimediff = Math.abs(cal.getTimeInMillis() - getLastTimeStamp(log.ip));
        log.isblock = 1;
        log.size = Si.HH * Si.WW;



        //  System.out.println("Black List IP");

        if (checkDOSAttack(log.reqtimediff) == 0) {// 0 stand For this is Normal Request....
            log.attackType = "Normal Request";
            log.reasonblock = "This is Normal Request!!";
            insertLog(log);
            if (Si.type == 0) {
                return grayScale(Si);
            } else if (Si.type == 1) {
                return Thresold(Si);
            } else {
                return Blur(Si);
            }

        } else {//  If This is Attack The Send Image As it is..

            log.attackType = "DDOS Request";
            log.reasonblock = "Found DDOS ATTACK";
            insertLog(log);
            insertBlackListIP(log.ip);
            System.out.println("This is DOS ATTACK!!!");
            return Si;
        }

    }

    Vector<SingleIP> showAllLogIP() {
        allLogIP = new Vector<SingleIP>();
        SingleIP log = new SingleIP();
        initdatabase();
        try {

            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from blackList");
            while (rs.next()) {
                log = new SingleIP();
                log.id = rs.getInt(1);
                log.IP = rs.getString(2);
                allLogIP.addElement(log);
            }
        } catch (Exception e) {

            System.out.println("Error Showing Contents: " + e);
            e.printStackTrace();
        }
        return allLogIP;
    }

    boolean insertBlackListIP(String IP) {
        boolean found = false;

        showAllLogIP();
        for (SingleIP sip : allLogIP) {
            if (sip.IP.equals(IP)) {
                found = true;
                break;
            }
        }
        if (!found) {

            try {
                initdatabase();
                stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                String query = "insert into blackList  values (null,'" + IP + "')";
                // System.out.println("Query IS " + query);
                stmt.executeUpdate(query);
                // System.out.println("Data Inserted In Log");
            } catch (Exception e) {
                System.out.println(" Error Found: " + e);
                e.printStackTrace();
            }

        }
        return found;

    }

    int checkDOSAttack(long reqDIff) {
        int userCentroid, robotCentroid;
        Centroid cd = new Centroid();

        try {
            ObjectInputStream out = new ObjectInputStream(new FileInputStream(new File(Setting.globalPath + "\\centroid.dat")));
            cd = (Centroid) out.readObject();
            out.close();

        } catch (Exception e) {
            System.out.println("No Training File is Found!!");
            // return 0;
            //  System.out.println("Error Found:  " + e);

        }
        if (cd == null) {
            System.out.println("Please Train Again!");
            return 0;
        }
        double diff1 = Math.abs(cd.highCentroid - reqDIff);
        double diff2 = Math.abs(cd.lowCentroid - reqDIff);
        // System.out.println("  "+reqDIff+"  "+diff1+"  "+diff2+"  "+cd.highCentroid+"  "+cd.lowCentroid);
        if (diff1 < diff2) {
            return 0;
        } else {
            return 1;
        }

    }

    public long getLastTimeStamp(String currIP) {
        Vector<MySingleLog> allLog = new Vector<MySingleLog>();
        MySingleLog log = new MySingleLog();
        allLog = showAllLog();
        String lastIP = "";
        long lastTime;
        int lastIndex = -1;

        // db = loadDB();
        for (int i = 0; i < allLog.size(); i++) {
            lastIP = allLog.elementAt(i).ip;
            if (lastIP.equals(currIP)) {
                lastIndex = i;
                // System.out.println(" Ip Found::");
            }
        }
        if (lastIndex == -1) {
            lastTime = -1;
        } else {
            lastTime = allLog.elementAt(lastIndex).currtime;
        }
        // System.out.println(" Last Time :   "+lastTime);
        return lastTime;
    }

    Vector<MySingleLog> showAllLog() {

        Vector<MySingleLog> allLog = new Vector<MySingleLog>();
        MySingleLog log = new MySingleLog();
        initdatabase();
        try {


            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from log");
            while (rs.next()) {
                log = new MySingleLog();
                log.logid = rs.getInt(1);
                log.ip = rs.getString(2);
                log.attackType = rs.getString(3);
                log.currtime = rs.getLong(4);
                log.reasonblock = rs.getString(5);
                log.timeSinceLast = rs.getLong(6);
                log.reqtimediff = rs.getLong(7);
                log.isblock = rs.getInt(8);
                allLog.addElement(log);
                // System.out.println(log.logid + " " + log.ip + "  " + log.attackType + "  " + log.timeSinceLast + "  " + log.reqtimediff);
            }
            ResultSetMetaData rsmd = rs.getMetaData();
            rs.close();
            con.close();
        } catch (Exception e) {

            System.out.println("Error Showing Contents: " + e);
            e.printStackTrace();
        }

        return allLog;

    }

    public void initdatabase() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(connection, user, password);
            //  System.out.println("Database Connection OK");
        } catch (Exception e) {
            System.out.println("Error opening database : " + e);

        }
    }

    void fillSQlInjectionQuery() {

        SqlInjection = new Vector<String>();
        try {


            FileReader fr = new FileReader(Setting.globalPath + "\\SqlInjection.txt");
            BufferedReader br = new BufferedReader(fr);
            String s;
            while ((s = br.readLine()) != null) {
                SqlInjection.addElement(s);
                //  System.out.println(s);
            }
        } catch (Exception e) {

            System.out.println("Error +" + e);
        }


    }

    void insertLog(MySingleLog log) {

        try {
            initdatabase();
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String query = "insert into log  values (null,'" + log.ip + "','" + log.attackType + "'," + log.currtime + ",'" + log.reasonblock + "'," + log.timeSinceLast + "," + log.reqtimediff + "," + log.isblock + "," + log.size + ")";
            // System.out.println("Query IS " + query);
            stmt.executeUpdate(query);
            // System.out.println("Data Inserted In Log");

        } catch (Exception e) {

            System.out.println(" Error Found: " + e);
            e.printStackTrace();
        }


    }

    void insertLogBruteForce(MySingleLog log) {

        try {
            initdatabase();
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String query = "insert into logMultiple  values (null,'" + log.ip + "','" + log.attackType + "','" + log.reasonblock + "'," + log.isblock + ")";
            System.out.println("Query IS " + query);
            stmt.executeUpdate(query);
            // System.out.println("Data Inserted In Log");

        } catch (Exception e) {

            System.out.println(" Error Found: " + e);
            e.printStackTrace();
        }


    }

    MySingleImage grayScale(MySingleImage Si) {

        int hh = Si.HH;
        int ww = Si.WW;
        int index = 0;
        for (int i = 0; i < hh; i++) {
            for (int j = 0; j < ww; j++) {
                int val = Si.img[index];
                int b = (val & 0xff);
                int g = ((val >> 8) & 0xff);
                int r = ((val >> 16) & 0xff);
                int avg = ((r + g + b) / 3) & 0xff;
                Si.img[index] = (avg & 0xffffff) | ((avg << 8) & 0xffffff) | ((avg << 16) & 0xffffff);
                index++;
            }
        }
        return Si;
    }

    MySingleImage Thresold(MySingleImage Si) {

        int hh = Si.HH;
        int ww = Si.WW;
        int index = 0;
        for (int i = 0; i < hh; i++) {
            for (int j = 0; j < ww; j++) {
                int val = Si.img[index];
                int b = (val & 0xff);
                int g = ((val >> 8) & 0xff);
                int r = ((val >> 16) & 0xff);
                int avg = ((r + g + b) / 3);
                if (avg < 128) {
                    Si.img[index] = 0;
                } else {
                    Si.img[index] = 0xffffffff;
                }
                index++;
            }
        }
        return Si;
    }

    MySingleImage Blur(MySingleImage Si) {

        int hh = Si.HH;
        int ww = Si.WW;
        int index = 0;
        int sumR = 0;
        int sumG = 0;
        int sumB = 0;
        int b = 0;
        int g = 0;
        int r = 0;
        for (int i = 2; i < hh - 2; i++) {
            for (int j = 2; j < ww - 2; j++) {

                int currIndex = 0;
                sumR = sumG = sumB = 0;
                for (int ii = 0; ii < 5; ii++) {
                    for (int jj = 0; jj < 5; jj++) {
                        int val = Si.img[index + currIndex];
                        b = (val & 0xff);
                        g = ((val >> 8) & 0xff);
                        r = ((val >> 16) & 0xff);
                        sumR += r;
                        sumG += g;
                        sumB += b;
                        currIndex++;
                    }
                }
                r = sumR / 25;
                g = sumG / 25;
                b = sumB / 25;
                Si.img[index] = (b | (g << 8) | (r << 16));//(avg & 0xffffff) | ((avg << 8) & 0xffffff) | ((avg << 16) & 0xffffff);
                index++;
            }
        }

        return Si;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "addUser")
    public Boolean addUser(@WebParam(name = "ui") userInfo ui) {
        //TODO write your implementation code here:
        initdatabase();
        boolean flag = false;

        String ssql = "insert into user (uid,fname,mname,lname,address,mobile,email,password,misCnt)values('" + ui.userId + "','" + ui.firstName + "','" + ui.middleName + "','" + ui.lastName + "','" + ui.Add + "','" + ui.mobile + "','" + ui.email + "','" + ui.password + "',0)";
        System.out.println("Query :" + ssql);
        try {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.executeUpdate(ssql);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR in ENTERING DATA" + e);
        }
        return flag;
    }

    @WebMethod(operationName = "getUserEmailID")
    public String getUserEmailID(@WebParam(name = "uname") String uname) {
        //TODO write your implementation code here:
        initdatabase();
        boolean flag = false;
        flag = false;
        String email = "";
        ssql = "select email from user where uid='" + uname + "'";
        try {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(ssql);
            if (rs.next()) {
                email = rs.getString(1);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            email = "";

        }
        return email;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "loginUser")
    public Boolean loginUser(@WebParam(name = "uname") String uname, @WebParam(name = "pass") String pass, @WebParam(name = "ip") String ip) {
        //TODO write your implementation code here:
        int th = 3;
        initdatabase();
        boolean flag = false;
        int misCnt = -1;

        initdatabase();
        flag = false;
        ssql = "select * from user where uid='" + uname + "' and password='" + pass + "'";
        try {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(ssql);
            if (rs.next()) {
                flag = true;
                initdatabase();
                String query = "update user set misCnt = ? where uid= ?";
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setInt(1, 0);
                preparedStmt.setString(2, uname);
                preparedStmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;

        }

        String ssql = "select misCnt from user where uid='" + uname + "'";
        try {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(ssql);
            if (rs.next()) {
                misCnt = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR in ENTERING DATA" + e);
        }

        if (misCnt > th) {
            MySingleLog log = new MySingleLog();
            log.isblock = 1;
            log.ip = ip;
            log.attackType = "Found BruteForce Attack";
            log.reasonblock = "Due To Mutiple Wrong Password";
            insertLogBruteForce(log);
            insertBlackListIP(log.ip);
            return false;
        } else {
            initdatabase();
            flag = false;
            ssql = "select * from user where uid='" + uname + "' and password='" + pass + "'";
            // System.out.println("Query :" + ssql);
            try {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(ssql);
                if (rs.next()) {
                    flag = true;
                } else {
                    misCnt++;
                    initdatabase();
                    try {
                        String query = "update user set misCnt = ? where uid= ?";
                        System.out.println("QUERY: " + query + "  " + uname);
                        PreparedStatement preparedStmt = con.prepareStatement(query);
                        preparedStmt.setInt(1, misCnt);
                        preparedStmt.setString(2, uname);
                        preparedStmt.executeUpdate();
                    } catch (Exception e) {
                        System.out.println("Error:" + e);
                        e.printStackTrace();
                    }
                }
                if (flag) {
                    initdatabase();
                    try {
                        String query = "update user set misCnt = ? where uid= ?";
                        PreparedStatement preparedStmt = con.prepareStatement(query);
                        preparedStmt.setInt(1, 0);
                        preparedStmt.setString(2, uname);
                        preparedStmt.executeUpdate();
                    } catch (Exception e) {
                        System.out.println("Error:" + e);
                        e.printStackTrace();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR in ENTERING DATA" + e);
            }



        }



        return flag;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "CheckSqlInjection")
    public Vector<userInfo> CheckSqlInjection(@WebParam(name = "inputQuery") String inputQuery) {
        //TODO write your implementation code here:
        System.out.println("Called SQL INJECTION: ");
        boolean found = false;
        fillSQlInjectionQuery();
        if (inputQuery.contains("select * from ")) {
            return null;
        } else {
            for (String str : SqlInjection) {
                if (inputQuery.toLowerCase().contains(str.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            System.out.println("FOUND:  " + found);
            if (found) {
                return null;
            } else {
                //  System.out.println("Calling To");
                return SQLInjection(inputQuery);
            }

        }



    }

    Vector<userInfo> SQLInjection(String query) {
        Vector<userInfo> allUsers = new Vector<userInfo>();
        try {
            initdatabase();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);
            // System.out.println("Query: " + query + " Total Col " + rs.getFetchSize());
            rs.last();
            ResultSetMetaData rsmd = rs.getMetaData();
            rs.beforeFirst();

            int colCount = rsmd.getColumnCount();
            while (rs.next()) {
                userInfo uInfo = new userInfo();
                for (int i = 0; i < colCount; i++) {
                    uInfo.allData.add(rs.getString(i + 1));
                }
                allUsers.add(uInfo);
            }
            rs.close();
            con.close();
            return allUsers;
        } catch (Exception e) {

            // System.out.println("Error Showing Contents: " + e);
            //  e.printStackTrace();
            return null;
        }




    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "updatePassword")
    public boolean updatePassword(@WebParam(name = "uid") String uid, @WebParam(name = "newPass") String newPass) {
        //TODO write your implementation code here:

        initdatabase();
        try {
            String query = "update user set password = ? where uid= ?";
            //  System.out.println("QUERY: " + query + "  " + uname);
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, newPass);
            preparedStmt.setString(2, uid);
            preparedStmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error:" + e);
            e.printStackTrace();
        }
        return false;
    }
}
