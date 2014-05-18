package dashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

class DSWindow extends JComponent {
    private static final long serialVersionUID = 1L;
    public static int windowWidth = 1366;
    public static int windowHeight = 725;
    boolean noBall;
    boolean ballGood;
    int blobCount;
    double shootDistance;
    double gyro;
    public DSWindow(boolean noBall, boolean ballGood, int blobCount, double shootDistance, double gyro) {
        this.noBall = noBall;
        this.ballGood = ballGood;
        this.blobCount = blobCount;
        this.shootDistance = shootDistance;
        this.gyro = gyro;
    }
    @Override
    public void paint (Graphics g) {
        ImageIcon dsImageIcon = null;
        try {
            dsImageIcon = new ImageIcon(Class.forName("dashboard.Dashboard").getResource("DS.png"));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DSWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        Image dsImage = dsImageIcon.getImage();
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.getHSBColor((float)3.51, (float) 0.61, 1));
        g2.fillRect(0,0,windowWidth,windowHeight);
        g2.drawImage(dsImage, 0, 505, null);
        //Get Ball
        String ballStatus;
        if (noBall) {
            ballStatus = "Grab a Ball";
            g2.setColor(Color.RED);
        } else {
            if (ballGood) {
                ballStatus = "Good To Go";
                g2.setColor(Color.GREEN);
            } else {
                ballStatus = "Kick It, Son!";
                g2.setColor(Color.YELLOW);
            }
        }
        g2.fillRoundRect(50, 50, 966, 200, 24, 24);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(50, 50, 966, 200, 24, 24);
        g2.scale(2.0, 2.0);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(ballStatus, g2);
        g2.setColor(Color.WHITE);
        g2.drawString(ballStatus, (int) (25 + (windowWidth-400)/4 - rect.getWidth()/2), (int) (25 + 200/4 + rect.getHeight()/2));
        g2.scale(1.0, 1.0);
        
        String goodDistance;
        if (shootDistance < 70 && shootDistance > 30) {
            goodDistance = "Ready To Shoot";
            g2.setColor(Color.GREEN);
        } else {
            goodDistance = "Don't Shoot";
            g2.setColor(Color.RED);
        }
        g2.scale(0.5, 0.5); //Back to normal scaling
        g2.fillRoundRect(50, 300, 966, 200, 24, 24);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(50, 300, 966, 200, 24, 24);
        g2.scale(2.0, 2.0);
        FontMetrics fm2 = g2.getFontMetrics();
        Rectangle2D rect2 = fm2.getStringBounds(goodDistance, g2);
        FontMetrics fm3 = g2.getFontMetrics();
        Rectangle2D rect3 = fm3.getStringBounds("(Distance: " + Math.round(shootDistance) + ")", g2);
        g2.setColor(Color.WHITE);
        g2.drawString(goodDistance, (int) (25 + (windowWidth-400)/4 - rect2.getWidth()/2), (int) (150 + 200/4 + rect2.getHeight()/2));
        g2.drawString("(Distance: " + Math.round(shootDistance) + ")", (int) (25 + (windowWidth-400)/4 - rect3.getWidth()/2), (int) (150 + 200/4 + rect3.getHeight() + 20));
        //Gyro stuff
        g2.scale(1.0, 1.0);
        g2.translate(535, 205);
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(0, 0, 150, 150, 24, 24);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, 150, 150, 24, 24);
        g2.setColor(Color.WHITE);
        g2.drawOval(20, 20, 110, 110);
        g2.rotate(Math.toRadians(gyro - 90), 75, 75);
        g2.drawLine(75, 75, 130, 75);
        g2.fillOval(120, 65, 20, 20);
        
        if (blobCount < 0) {
        }
    }
}
public class Dashboard {
    static GraphicsDevice device = GraphicsEnvironment
	.getLocalGraphicsEnvironment().getScreenDevices()[0];
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Launching...");
        new Dashboard().run();
    }
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        NetworkTable.setClientMode();
         System.out.println("Set to client mode...");
        NetworkTable.setIPAddress("10.0.0.11");
         System.out.println("Set IP address...");
        NetworkTable dashTable = NetworkTable.getTable("SmartDashboard");
         System.out.println("Set up network tables...");
        JFrame window = new JFrame();
        window.revalidate ();
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setTitle("Dashboard");
	window.setResizable(false);
	window.setName("Dashboard");
	window.setBounds(0, 0, 1366, 725);
        System.out.println("Set up window...");
        double shootDistance = 0.0;
        double gyro = 0.0;
        boolean noBall = true;
        boolean ballGood = false;
        int blobCount = -1;
        while (true) {
            noBall = dashTable.getBoolean("No Ball", noBall);
            ballGood = dashTable.getBoolean("Ball Good", ballGood);
            blobCount = Integer.parseInt(dashTable.getString("BLOB_COUNT", "-1"));
            shootDistance = dashTable.getNumber("UltraSonic", shootDistance);
            gyro = dashTable.getNumber("Gyro", (gyro + 10.0));
            window.getContentPane().removeAll();
            window.getContentPane().add(new DSWindow(noBall, ballGood, blobCount, shootDistance, gyro));
            window.setVisible(true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        
    }
    
}
