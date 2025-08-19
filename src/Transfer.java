import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Transfer extends JFrame {
    Transfer(String username) {
        Font f = new Font("Futura", Font.BOLD, 30);
        Font f2 = new Font("Calibri", Font.PLAIN, 18);

        JLabel title = new JLabel("Transfer Funds", JLabel.CENTER);
        JLabel l1 = new JLabel("Receiver:");
        JTextField t1 = new JTextField(10);

        JLabel l2 = new JLabel("Amount:");
        JTextField t2 = new JTextField(10);

        JButton b1 = new JButton("Transfer");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        l1.setFont(f2);
        t1.setFont(f2);
        l2.setFont(f2);
        t2.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        int labelX = 200, fieldX = 400, yStart = 80, width = 150, height = 30, gap = 40;

        title.setBounds(250, 20, 300, 40);

        l1.setBounds(labelX, yStart, width, height);
        t1.setBounds(fieldX, yStart, width, height);

        l2.setBounds(labelX, yStart + gap, width, height);
        t2.setBounds(fieldX, yStart + gap, width, height);

        b1.setBounds(250, yStart + 2 * gap, 120, 40);
        b2.setBounds(400, yStart + 2 * gap, 120, 40);

        c.add(title);
        c.add(l1);
        c.add(t1);
        c.add(l2);
        c.add(t2);
        c.add(b1);
        c.add(b2);

        b2.addActionListener(
                a->{
                    new Home(username);
                    dispose();
                }
        );
        b1.addActionListener(
                a->{
                    String samnevala=t1.getText();
                    String s1=t2.getText();
                    if(samnevala.isEmpty() || s1.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null, "Please enter all the fields");
                    }
                    //ROUND 1
                    double amount=Double.parseDouble(s1);
                    double balance=Fetchbalance(username);
                    if(amount>balance){
                        JOptionPane.showMessageDialog(null, "Insufficient balance");
                        return;
                    }


                    //ROUND 2
                    String url="jdbc:mysql://localhost:3306/3dec";
                    String uname="root";
                    String upassword="oracle";
                    try(Connection conn= DriverManager.getConnection(url,uname,upassword))
                    {
                        String sql="update users set balance=? where username=?";
                        try(PreparedStatement ps=conn.prepareStatement(sql)){
                            ps.setDouble(1,balance-amount);
                            ps.setString(2,username);
                            ps.executeUpdate();
                            updatepassbook(username,"Transfered to "+samnevala,-amount,balance-amount);

                        }
                    }
                    catch(Exception e){
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }

                    //ROUND 3
                    balance=Fetchbalance(samnevala);

                    //ROUND 4
                    try(Connection conn= DriverManager.getConnection(url,uname,upassword))
                    {
                        String sql="update users set balance=? where username=?";
                        try(PreparedStatement ps=conn.prepareStatement(sql)){
                            ps.setDouble(1,balance+amount);
                            ps.setString(2,samnevala);
                            ps.executeUpdate();
                           JOptionPane.showMessageDialog(null, "Transfer Successful");
                           t1.setText("");
                           t2.setText("");
                            updatepassbook(samnevala,"Withdrawn from "+username,amount,balance+amount);
                        }
                    }
                    catch(Exception e){
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }


                }
        );

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Transfer Funds");
    }

    double Fetchbalance(String username)
    {
        double balance=0.0;
        String url="jdbc:mysql://localhost:3306/3dec";
        String uname="root";
        String upassword="oracle";
        try(Connection conn= DriverManager.getConnection(url,uname,upassword))
        {
            String sql="select balance from users where username =?";
            try(PreparedStatement ps=conn.prepareStatement(sql)){
                ps.setString(1,username);
                ResultSet rs=ps.executeQuery();
                if(rs.next()){
                    balance=rs.getDouble("balance");
                }
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return balance;
    }
    void updatepassbook(String username,String desc,double amount,double balance){

        String url="jdbc:mysql://localhost:3306/3dec";
        String uname="root";
        String upassword="oracle";
        try(Connection conn= DriverManager.getConnection(url,uname,upassword))
        {
            String sql="insert into transactions (username,description,amount,balance) values(?,?,?,?)";
            try(PreparedStatement ps=conn.prepareStatement(sql)){
                ps.setString(1,username);
                ps.setString(2,desc);
                ps.setDouble(3,amount);
                ps.setDouble(4,balance);
                ps.executeUpdate();
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Transfer("parth");
    }
}
