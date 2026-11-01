package practical.CarPlay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

class Car {
    int lane;
    int y;
    boolean isPlayer;
    Image image;

    public Car(int lane, int y, String imagePath, boolean isPlayer) {
        this.lane = lane;
        this.y = y;
        this.isPlayer = isPlayer;

        try {
            image = new ImageIcon(
                    getClass().getResource(imagePath)
            ).getImage();
        } catch (Exception e) {
            image = null;
        }
    }

    public void move(int dy) {
        y += dy;
    }

    public void draw(Graphics g, int laneWidth) {
        int x = lane * laneWidth + 10;
        if (image != null) {
            g.drawImage(image, x, y, laneWidth - 20, 60, null);
        } else {
            g.setColor(isPlayer ? Color.RED : Color.BLUE);
            g.fillRect(x, y, laneWidth - 20, 60);
        }
    }
}

public class CarPlay extends JPanel
        implements ActionListener, KeyListener {

    int laneCount = 6;
    int laneWidth = 100;
    int roadHeight = 600;

    int baseSpeed = 5;
    int speed = baseSpeed;

    int roadOffset = 0;
    int ticks = 0;
    int score = 0;

    Car playerCar;
    ArrayList<Car> aiCars = new ArrayList<>();
    Random rand = new Random();
    Timer timer;

    int maxAISpawn = 10;

    public CarPlay() {
        setPreferredSize(new Dimension(laneCount * laneWidth, roadHeight));
        setFocusable(true);
        addKeyListener(this);

        playerCar = new Car(
                2,
                roadHeight - 120,
                "/practical/CarPlay/images/car2.jpeg",
                true
        );

        String[] aiImages = {
                "/practical/CarPlay/images/car1.jpeg",
                "/practical/CarPlay/images/car.jpeg",
                "/practical/CarPlay/images/car3.jpeg"
        };

        spawnAICars(5, aiImages);

        timer = new Timer(30, this);
        timer.start();
    }

    private void spawnAICars(int count, String[] images) {
        for (int i = 0; i < count; i++) {
            int lane = rand.nextInt(laneCount);
            int y = -rand.nextInt(600);
            String img = images[rand.nextInt(images.length)];
            aiCars.add(new Car(lane, y, img, false));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, laneCount * laneWidth, roadHeight);

        g.setColor(Color.WHITE);
        int lineHeight = 40;

        for (int i = 1; i < laneCount; i++) {
            for (int y = -lineHeight + roadOffset;
                 y < roadHeight;
                 y += lineHeight * 2) {

                g.fillRect(i * laneWidth - 2, y, 4, lineHeight);
            }
        }

        playerCar.draw(g, laneWidth);
        for (Car c : aiCars) c.draw(g, laneWidth);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ticks++;
        roadOffset += speed;
        if (roadOffset >= roadHeight) roadOffset = 0;

        if (ticks % 500 == 0) speed++;

        for (Car c : aiCars) {
            c.move(speed);
            if (c.y > roadHeight) {
                c.y = -rand.nextInt(400);
                c.lane = rand.nextInt(laneCount);
            }
        }

        if (ticks % 1000 == 0 && aiCars.size() < maxAISpawn) {
            String[] aiImages = {
                    "/practical/CarPlay/images/car1.jpeg",
                    "/practical/CarPlay/images/car.jpeg",
                    "/practical/CarPlay/images/car3.jpeg"
            };
            spawnAICars(1, aiImages);
        }

        for (Car c : aiCars) {
            if (c.lane == playerCar.lane &&
                    Math.abs(c.y - playerCar.y) < 50) {

                timer.stop();
                JOptionPane.showMessageDialog(
                        this,
                        "Game Over\nScore: " + score
                );
                System.exit(0);
            }
        }

        score++;
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerCar.lane > 0)
            playerCar.lane--;

        if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerCar.lane < laneCount - 1)
            playerCar.lane++;

        if (e.getKeyCode() == KeyEvent.VK_UP)
            speed = baseSpeed + 5;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP)
            speed = baseSpeed;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Car Game");
        frame.add(new CarPlay());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}