package gui.geoPosition;

import javax.imageio.*;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class GUIClass extends JFrame {
    private JButton resetRouteButton;
    private static MyDrawPanel panel;
    private static Distance totalDistance;
    private JLabel distanceLabel, latLonLabel, xyCoord;
    private JPanel panelButton, panelCompts, mainPanel;

    public GUIClass() {
        // set the frame
        super("Geographical Routes");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(50, 50);

        // create button
        resetRouteButton = new JButton("Reset Route");

        // panel to hold the Reset Button
        panelButton = new JPanel();
        panelButton.add(resetRouteButton);

        // he labels with default texts
        distanceLabel = new JLabel("Distance: ");
        latLonLabel = new JLabel("(lat, lon)  = ");
        xyCoord = new JLabel("(X, Y)  = ");

        // panel to hold the components
        panelCompts = new JPanel();
        panelCompts.setLayout(new GridLayout(1, 4));

        panelCompts.add(panelButton); // add panelButton to panelCompts
        panelCompts.add(distanceLabel);
        panelCompts.add(latLonLabel);
        panelCompts.add(xyCoord);

        // panel to hold drawing
        panel = new MyDrawPanel();

        // add all panels to the main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(panelCompts);
        mainPanel.add(panel);

        // add main panel to the frame
        add(mainPanel);

        // for distance
        totalDistance = new Distance();

        // mouse clicked events
        HandlerClass handler = new HandlerClass();
        panel.addMouseListener(handler);

        // for button to reset route
        HandlerClass2 handler2 = new HandlerClass2();
        resetRouteButton.addActionListener(handler2);

        pack();
        setVisible(true);
    }

    // Panel to draw on.
    class MyDrawPanel extends JPanel {
        private BufferedImage image = null;
        // Arrays holding the coordinates of the polygon's points
        private ArrayList<Integer> listCoordsX = new ArrayList<Integer>();
        private ArrayList<Integer> listCoordsY = new ArrayList<Integer>();

        public MyDrawPanel() {

            // read image file
            try {
                image = ImageIO.read(new File("src/gui/geoPosition/OSM_Map.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Set panel size.
        public Dimension getPreferredSize() {
            return new Dimension(image.getWidth(null), image.getHeight(null));
        }

        /**
         * @param x x coordinate of point to add
         * @param y y coordinate of point to add
         */
        public void addPoint(int x, int y) {
            listCoordsX.add(x);
            listCoordsY.add(y);
            repaint();
        }

        // Clear list and repaint.
        public void clear() {
            listCoordsX.clear();
            listCoordsY.clear();
            repaint();
        }
        /**
         * Repaint: Draw polygon in panel.
         *
         * @param g Graphics to paint on
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.setColor(Color.RED);
            graphics2D.setStroke(new BasicStroke(3));
            graphics2D.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
            int numberPoints = listCoordsX.size();
            if (numberPoints > 1) {
                for (int i = 1; i < numberPoints; i++) {
                    g.drawLine(listCoordsX.get(i - 1), listCoordsY.get(i - 1), listCoordsX.get(i), listCoordsY.get(i));
                }
            }
        }
    }

    class Distance {
        // Arrays holding the latitudes and longitudes
        private ArrayList<Double> lat = new ArrayList<Double>();
        private ArrayList<Double> lon = new ArrayList<Double>();

        // Add to list
        public void addPoint(double x, double y) {
            lat.add(x);
            lon.add(y);
        }

        // Clear list
        public void clear() {
            lat.clear();
            lon.clear();
        }

        // calculates the distance in km
        public double distanceInKm(double latitude1, double longitude1, double latitude2, double longitude2) {
            return 6378.388 * Math.acos(Math.sin(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2))
                    + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                    * Math.cos(Math.toRadians(longitude2) - Math.toRadians(longitude1)));
        }

        // calculates total distance
        public double getDistance() {
            double distance = 0.0;
            double coordinate = lat.size();
            if (coordinate > 1) {
                for (int i = 1; i < coordinate; i++) {
                    distance += distanceInKm(lat.get(i - 1), lon.get(i - 1), lat.get(i), lon.get(i));
                }
            }
            return distance;
        }
    }

    private class HandlerClass implements MouseListener, MouseMotionListener {
        // methods for MouseListener
        // Events for mouse
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == 1) {
                Double newGetX = 8.4375 + 0.002743896484 * event.getX();
                Double newGetY = 54.5720556 - 0.001614 * event.getY();

                panel.addPoint(event.getX(), event.getY());

                totalDistance.addPoint(newGetY, newGetX);

                distanceLabel.setText("Distance: " + Math.round(totalDistance.getDistance() * 100) / 100D + "Km");
                latLonLabel.setText("(lat, lon)  =  (" + Math.round(newGetY * 1000) / 1000D + ", "
                        + Math.round(newGetX * 1000) / 1000D + ")");
                xyCoord.setText("(X, Y)  = " + "(" + event.getX() + ", " + event.getY() + ")");
            }
        }

        public void mousePressed(MouseEvent event) {
        }

        public void mouseReleased(MouseEvent event) {
        }

        public void mouseEntered(MouseEvent event) {
        }

        public void mouseExited(MouseEvent event) {
        }

        // methods for MouseMotionListener
        public void mouseDragged(MouseEvent event) {
        }

        public void mouseMoved(MouseEvent event) {
        }
    }

    // Event for JButton
    private class HandlerClass2 implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // clear values and set display to 0
            panel.clear();
            totalDistance.clear();
            distanceLabel.setText("Distance: " + 0 + "Km");
            latLonLabel.setText("(lat, lon)  =  (" + 0 + ", " + 0 + ")");
            xyCoord.setText("(X, Y)  = " + "(" + 0 + ", " + 0 + ")");
        }
    }
}