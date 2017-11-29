/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataPackage;

import AlgoClustering.Centroid;
import MyPack.MySingleLog;
import MyPack.MySingleMine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author PC 1
 */
public class ViewLog extends javax.swing.JFrame {

    FrmMainForm parent;
    Object colName[] = {"Sr.No", "IP Adress", "RequestType", "Current Time(milliSec)", "Reason ", "Request Difference", "Size"};
    Object data[][];
    DefaultTableModel tm;
    int index;
    int indexs[];
    Connection con;
    Statement stmt;
    String user = "root";
    String password = "root";
    String ssql;
    ResultSet rs;
    Vector<MySingleLog> allData = new Vector<MySingleLog>();
    String connection = "jdbc:mysql://localhost/DDOS";
    int k, n; // k clusters and n data points
    long dataPoints[][];
    int clusters[];
    int minLimit, maxLimit;
    boolean set = false;
    public Vector<MySingleMine> allLogMining = new Vector<MySingleMine>();
    Centroid cd;
    Object colHeaderBlackList[] = {"Sr.No", "IP Address"};
    Vector<SingleIP> allLogIP = new Vector<SingleIP>();
    DefaultTableModel tmBlackList;

    public ViewLog(FrmMainForm parent) {
        initComponents();
        Dimension sd = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth() / 2, sd.height / 2 - this.getHeight() / 2);
        this.parent = parent;
        tm = new DefaultTableModel();
        //parent.loadDB();
        data = new Object[allData.size()][6];
        // jTable1.setBackground(Color.white);
        //  jTable1.setForeground(Color.black);
        jTableLog.setRowHeight(24);
        jTableLog.setFont(new Font("Arial", Font.BOLD, 11));
        jTableLog.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        jTableLog.getTableHeader().setBackground(new Color(205, 205, 205));
        allData = showAllLog();
        showAllData();
        showBruteForceAttack();


    }

    public void initdatabase() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(connection, user, password);
            // System.out.println("Database Connection OK");
        } catch (Exception e) {
            System.out.println("Error opening database : " + e);

        }
    }

    Vector<MySingleLog> showAllLog() {

        Vector<MySingleLog> allLog = new Vector<MySingleLog>();
        MySingleLog log = new MySingleLog();
        initdatabase();
        try {

            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from log");
            while (rs.next()) {
                if (rs.getLong(7) < 99999999) {
                    log = new MySingleLog();
                    log.logid = rs.getInt(1);
                    log.ip = rs.getString(2);
                    log.attackType = rs.getString(3);
                    log.currtime = rs.getLong(4);
                    log.reasonblock = rs.getString(5);
                    log.timeSinceLast = rs.getLong(6);
                    log.reqtimediff = rs.getLong(7);
                    log.isblock = rs.getInt(8);
                    log.size = rs.getInt(9);

                    //  log.forwho = rs.getInt(9);
                    allLog.addElement(log);
                }
                // System.out.println(log.logid + " " + log.ip + "  " + log.attackType + "  " + log.timeSinceLast + "  " + log.reqtimediff);
            }
        } catch (Exception e) {

            System.out.println("Error Showing Contents: " + e);
            e.printStackTrace();
        }

        return allLog;

    }

    void showBruteForceAttack() {

        Vector<MySingleLog> allLogBruteForce = new Vector<MySingleLog>();
        MySingleLog log = new MySingleLog();
        initdatabase();
        try {

            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from logMultiple");
            while (rs.next()) {
                log = new MySingleLog();
                log.logid = rs.getInt(1);
                log.ip = rs.getString(2);
                log.attackType = rs.getString(3);
                log.reasonblock = rs.getString(4);
                log.isblock = rs.getInt(5);
                allLogBruteForce.addElement(log);
            }
        } catch (Exception e) {
            System.out.println("Error Showing Contents: " + e);
            e.printStackTrace();
        }

        try {

            // parent.loadDB();
            System.out.println(" SIZE:   " + allLogBruteForce.size());
            data = new Object[allLogBruteForce.size()][4];
            for (int i = 0; i < allLogBruteForce.size(); i++) {

                data[i][0] = "" + (i + 1);
                data[i][1] = allLogBruteForce.elementAt(i).ip;
                data[i][2] = allLogBruteForce.elementAt(i).attackType;
                data[i][3] = allLogBruteForce.elementAt(i).reasonblock;
            }
            jTableDb.setModel(new DefaultTableModel(data, new Object[]{"##", "IP", "Attack Type", "Reason Block"}));
            jTableDb.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTableDb.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableDb.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableDb.getColumnModel().getColumn(3).setPreferredWidth(100);


        } catch (Exception e) {
            System.out.println("Error" + e);
            e.printStackTrace();

        }



    }

    Vector<MySingleMine> ShowLogMining() {

        MySingleMine log = new MySingleMine();
        initdatabase();
        try {

            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from miningSet");
            while (rs.next()) {
                log = new MySingleMine();
                log.mid = rs.getInt(1);
                log.reqtimediff = rs.getLong(2);
                log.size = rs.getInt(3);
                log.output = rs.getInt(4);
                allLogMining.addElement(log);

            }
        } catch (Exception e) {

            System.out.println("Error Showing Contents: " + e);
            e.printStackTrace();
        }

        return allLogMining;

    }

    public void showAllData() {
        try {

            // parent.loadDB();
            data = new Object[allData.size()][7];
            for (int i = 0; i < allData.size(); i++) {

                data[i][0] = "" + (i + 1);
                data[i][1] = allData.elementAt(i).ip;
                data[i][2] = allData.elementAt(i).attackType;
                data[i][3] = allData.elementAt(i).currtime;
                data[i][4] = allData.elementAt(i).reasonblock;
                data[i][5] = allData.elementAt(i).reqtimediff;
                data[i][6] = allData.elementAt(i).size;
            }
            jTableLog.setModel(new DefaultTableModel(data, colName));
            jTableLog.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableLog.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableLog.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableLog.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableLog.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableLog.getColumnModel().getColumn(5).setPreferredWidth(100);

        } catch (Exception e) {
            System.out.println("Error" + e);
            e.printStackTrace();

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLog = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableDb = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton9 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(244, 244, 230));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(0));

        jButton2.setFont(new java.awt.Font("Andalus", 0, 18)); // NOI18N
        jButton2.setText("Back");
        jButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        new JavaLib.LoadForm();
        jTabbedPane1.setFont(new java.awt.Font("Andalus", 0, 14)); // NOI18N
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jTableLog.setForeground(new java.awt.Color(0, 0, 255));
        jTableLog.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableLog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableLogMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLog);

        jButton3.setFont(new java.awt.Font("Andalus", 0, 14)); // NOI18N
        jButton3.setText("Refresh");
        jButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("View Log", jPanel4);

        jButton5.setText("SET");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton1.setText("CLUSTERING");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 429, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 115, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton1))
                .addGap(98, 98, 98))
        );

        jTabbedPane1.addTab("Clustering Data SET", jPanel3);

        jTableDb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTableDb);

        jButton4.setText("Show Brute Force");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Brute Force ", jPanel6);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(jTable1);

        jButton6.setFont(new java.awt.Font("Andalus", 0, 18)); // NOI18N
        jButton6.setText("Back");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton7.setText("Refresh");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton8.setText("Un Block");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Black List IP", jPanel7);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(jTable2);

        jButton9.setText("SHOW ");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(299, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton9)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("ALL LOG", jPanel8);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(247, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 813, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(101, 101, 101))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        new JavaLib.LoadForm();
        jLabel1.setFont(new java.awt.Font("Andalus", 1, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("V I E W   L O G");
        jLabel1.setBorder(javax.swing.BorderFactory.createBevelBorder(0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        parent.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTableLogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLogMouseClicked
        // TODO add your handling code here:
        index = jTableLog.getSelectedRow();
        indexs = jTableLog.getSelectedRows();
    }//GEN-LAST:event_jTableLogMouseClicked

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1MouseClicked
    int minDistance(int val, int centroid) {
        return Math.abs(val - centroid);
    }

    void clustering_data() {

        int k, n; // k clusters and n data points
        int dataPoints[][];
        int clusters[];
        int minDist, currDist;
        n = allData.size();
        dataPoints = new int[n][2];

        int totalPointsInCluster[], sumClusterPoints[];


        k = 2;
        clusters = new int[k];
        String data = "";
        int cnt = 0;
        for (int i = 0; i < n; i++) {
            if (((int) allData.get(i).reqtimediff) < 12000) {
                dataPoints[cnt][0] = (int) allData.get(i).reqtimediff;
//               / System.out.println("  "+dataPoints[cnt][0]);
                dataPoints[cnt][1] = -1;
                data = data + "  " + dataPoints[cnt][0];
                //  System.out.println("  "+data);
                cnt++;
            }

        }
        n = n - cnt;
        jTextArea1.setText("Request Differnce Point: \n" + data);
        // calculate minimum and maximum data 
        int maxLimit = dataPoints[0][0];
        int minLimit = dataPoints[0][0];
        for (int i = 0; i < n; i++) {
            minLimit = Math.min(minLimit, dataPoints[i][0]);
            maxLimit = Math.max(maxLimit, dataPoints[i][0]);
        }
        int totalRange = maxLimit - minLimit;

        // initialise cluster
        // either use min & max Limit...
        for (int i = 0; i < k; i++) {
            clusters[i] = ((totalRange / k) * i) + ((totalRange / k) / 2);

            //  add("CLUSTER CENTROID " + i + ":" + clusters[i]);
        }
        System.out.println();


        totalPointsInCluster = new int[k];
        sumClusterPoints = new int[k];

        boolean change = true;
        while (change) {
            change = false;

            // update data points clustering...
            for (int i = 0; i < n; i++) { // data points
                minDist = 99999;
                for (int j = 0; j < k; j++) { // clusters
                    currDist = minDistance(dataPoints[i][0], clusters[j]);
                    if (currDist < minDist) {
                        dataPoints[i][1] = j; // update clustering
                        // System.out.println("Updating cluster for " + dataPoints[i][0] + " as " + dataPoints[i][1]);
                        minDist = currDist;
                    }
                }
            }
            System.out.println("End of loop");

            // update centroids
            for (int i = 0; i < k; i++) {
                sumClusterPoints[i] = 0;
                totalPointsInCluster[i] = 0;
            }
            for (int i = 0; i < n; i++) {
                sumClusterPoints[dataPoints[i][1]] += dataPoints[i][0];
                totalPointsInCluster[dataPoints[i][1]]++;
            }
            for (int i = 0; i < k; i++) {
                if (totalPointsInCluster[i] == 0) {
                    continue;
                }
                int currCentroid = sumClusterPoints[i] / totalPointsInCluster[i];
                if (clusters[i] != currCentroid) {
                    clusters[i] = currCentroid;
                    change = true;
                }
            }
        }
        cd = new Centroid();
        if (clusters[0] > clusters[1]) {
            cd.lowCentroid = clusters[1];
            cd.highCentroid = clusters[0];
        } else {
            cd.lowCentroid = clusters[0];
            cd.highCentroid = clusters[1];
        }
        String output = "Final Centroid Points:: \n";
        for (int i = 0; i < k; i++) {
            output += "*" + clusters[i] + "\n";
        }
        jTextArea2.setText(output);

    }

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.dir") + "\\centroid.dat")));
            out.writeObject(cd);
            out.close();
            JOptionPane.showMessageDialog(this, "Centroid Store Sucessfully");
        } catch (Exception e) {
            System.out.println("Error Found:  " + e);
            JOptionPane.showMessageDialog(this, "Error in Storing centroid!!");
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        clustering_data();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        jTableLog.setRowHeight(24);
        jTableLog.setFont(new Font("Arial", Font.BOLD, 11));
        jTableLog.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        jTableLog.getTableHeader().setBackground(new Color(205, 205, 205));
        allData = showAllLog();
        showAllData();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:

        showBruteForceAttack();

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        parent.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        showBlackList();
    }//GEN-LAST:event_jButton7ActionPerformed

    void showBlackList() {
        allLogIP = new Vector<SingleIP>();
        SingleIP log = new SingleIP();
        initdatabase();
        tmBlackList = new DefaultTableModel(colHeaderBlackList, 0);
        jTable1.setModel(tmBlackList);
        try {

            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from blackList");
            while (rs.next()) {
                log = new SingleIP();
                log.id = rs.getInt(1);
                log.IP = rs.getString(2);
                allLogIP.addElement(log);
                Object row[] = new Object[2];
                row[0] = log.id;
                row[1] = log.IP;
                tmBlackList.addRow(row);
            }
            jTable1.setModel(tmBlackList);
        } catch (Exception e) {

            System.out.println("Error Showing Contents: " + e);
            e.printStackTrace();
        }

    }
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        int index = jTable1.getSelectedRow();
        if (index == -1) {
            return;
        }
        try {
            initdatabase();
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String query = "delete  from blackList where bid=" + allLogIP.get(index).id;
            // System.out.println("Query IS " + query);
            stmt.executeUpdate(query);
            // System.out.println("Data Inserted In Log");

        } catch (Exception e) {

            System.out.println(" Error Found: " + e);
            e.printStackTrace();
        }

        showBlackList();

    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel tmFinalLog;
        Object col[] = new Object[]{"MAC ADDRESS", "IP ADDRESS"};

        tmFinalLog = new DefaultTableModel(col, 0);
        jTable2.setModel(tmFinalLog);


        initdatabase();
        try {

            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from log");
            while (rs.next()) {
                Object row[] = new Object[2];

                if (!rs.getString(3).contains("Normal")) {
                    row[1] = rs.getString(3);
                    row[0] = rs.getString(2);
                    tmFinalLog.addRow(row);
                }

            }
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select *from logMultiple");
            while (rs.next()) {
                Object row[] = new Object[2];
                row[1] = rs.getString(3);
                row[0] = rs.getString(2);
                tmFinalLog.addRow(row);

            }

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        jTable2.setModel(tmFinalLog);


    }//GEN-LAST:event_jButton9ActionPerformed

    void delete(int mid) {
        try {
            initdatabase();
            ssql = "delete from miningSet where mid=" + mid + " ";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.executeUpdate(ssql);

        } catch (Exception e) {
            System.out.println("ERROR in ENTERING DATA" + e);
            e.printStackTrace();

        }

    }

    void insertLog(double diff, int size, int output) {

        try {
            initdatabase();
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String query = "insert into miningSet  values (null," + diff + "," + size + "," + output + ")";
            // System.out.println("Query IS " + query);
            stmt.executeUpdate(query);
            // System.out.println("Data Inserted In Log");

        } catch (Exception e) {

            System.out.println(" Error Found: " + e);
            e.printStackTrace();
        }


    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTableDb;
    private javax.swing.JTable jTableLog;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
