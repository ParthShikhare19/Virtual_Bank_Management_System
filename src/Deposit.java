
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

class Deposit extends JFrame
{
    Deposit(String username)
    {

        Font f = new Font("Futura", Font.BOLD, 40);
        Font f2 = new Font("Calibri", Font.PLAIN, 22);

        JLabel title = new JLabel("Deposit Money", JLabel.CENTER);
        JLabel label = new JLabel("Enter Amount:");
        JTextField t1 = new JTextField(10);
        JButton b1 = new JButton("Deposit");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        label.setFont(f2);
        t1.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        title.setBounds(200, 30, 400, 50);
        label.setBounds(250, 120, 300, 30);
        t1.setBounds(250, 160, 300, 30);
        b1.setBounds(300, 220, 200, 40);
        b2.setBounds(300, 280, 200, 40);

        c.add(title);
        c.add(label);
        c.add(t1);
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
                    String s1=t1.getText();
                    if(s1.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null, "Enter Amount to Deposit");
                    }
                    else {
                        double amount=Double.parseDouble(t1.getText());
                        if(amount<0)
                        {
                            JOptionPane.showMessageDialog(null, "Enter valid amount");
                        }
                        else
                        {
                            double total=balance+amount;
                            try(Connection conn= DriverManager.getConnection(url,uname,upassword))
                            {
                                String sql="update users set balance=? where username=?";
                                try(PreparedStatement ps=conn.prepareStatement(sql)){
                                    ps.setDouble(1,total);
                                    ps.setString(2,username);
                                    ps.executeUpdate();
                                    JOptionPane.showMessageDialog(null, "Deposit Successful");
                                    t1.setText("");
                                    updatepassbook(username,"Deposit",amount,amount+balance);
                                }
                            }
                            catch(Exception e){
                                JOptionPane.showMessageDialog(null, e.getMessage());
                            }
                        }

                    }


                }
        );

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Deposit Money");
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
        new Deposit("parth");
    }
}