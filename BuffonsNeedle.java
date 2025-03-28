import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class BuffonsNeedle extends JFrame {

    private static final int t = 50;
    private static final int len = 25;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final double TWO_PI = 2 * Math.PI;
    private static final double PI = Math.PI;

    private int total = 0;
    private int intersecting = 0;
    private JLabel piLabel;
    private Timer timer;
    private BufferedImage needlesImage;
    private NeedlePanel drawingPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BuffonsNeedle demo = new BuffonsNeedle();
            demo.setVisible(true);
        });
    }

    public BuffonsNeedle() {
        setTitle("Buffon's Needle");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        needlesImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = needlesImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < WIDTH; i += t) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(i, 0, i, HEIGHT);
        }
        g2d.dispose();

        drawingPanel = new NeedlePanel();
        add(drawingPanel, BorderLayout.CENTER);

        piLabel = new JLabel("0.00000");
        piLabel.setFont(new Font("Helvetica", Font.PLAIN, 48));
        piLabel.setHorizontalAlignment(JLabel.CENTER);
        piLabel.setBackground(Color.WHITE);
        add(piLabel, BorderLayout.SOUTH);

        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSimulation();
                drawingPanel.repaint();
            }
        });
        timer.start();
    }

    private void updateSimulation() {
        Graphics2D g2d = needlesImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < 100; i++) {
            double angle = Math.random() * PI;
            int x = (int) (Math.random() * WIDTH);
            int y = (int) (Math.random() * HEIGHT);

            int closestLine = Math.round(x / (float)t);
            double d = Math.abs(closestLine * t - x);

            if (d < (len * Math.sin(Math.abs(angle))) / 2) {
                g2d.setColor(new Color(0, 255, 0, 255));
                g2d.setStroke(new BasicStroke(2));
                intersecting++;
            } else {
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.setStroke(new BasicStroke(1));
            }
            total++;

            drawRotatedNeedle(g2d, x, y, angle);
        }
        g2d.dispose();

        double prob = (double) intersecting / total;
        double pie = (2 * len) / (prob * t);

        DecimalFormat df = new DecimalFormat("0.00000");
        piLabel.setText(df.format(pie));
    }

    private void drawRotatedNeedle(Graphics2D g2d, int x, int y, double angle) {

        AffineTransform originalTransform = g2d.getTransform();

        g2d.translate(x, y);
        g2d.rotate(angle);

        g2d.drawLine(0, -len/2, 0, len/2);

        g2d.setTransform(originalTransform);
    }

    private class NeedlePanel extends JPanel {
        public NeedlePanel() {
            setDoubleBuffered(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (needlesImage != null) {
                g.drawImage(needlesImage, 0, 0, this);
            }
        }
    }
}
