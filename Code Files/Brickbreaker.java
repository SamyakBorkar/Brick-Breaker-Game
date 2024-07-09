import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Brick class
class Brick {
    int x, y, width, height;
    boolean isBroken = false;

    public Brick(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        if (!isBroken) {
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
        }
    }
}

// Paddle class
class Paddle {
    int x, y, width, height;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(int dx) {
        x += dx;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}

// Ball class
class Ball {
    int x, y, diameter;
    int dx = 2; // X direction
    int dy = -2; // Y direction

    public Ball(int x, int y, int diameter) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void bounceX() {
        dx = -dx;
    }

    public void bounceY() {
        dy = -dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, diameter, diameter);
    }
}

// Main game panel
class GamePanel extends JPanel implements KeyListener {
    Paddle paddle;
    Ball ball;
    List<Brick> bricks = new ArrayList<>();
    Timer timer;
    boolean gameOver = false;

    public GamePanel() {
        paddle = new Paddle(200, 400, 80, 20);
        ball = new Ball(200, 380, 15);

        // Create bricks
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                bricks.add(new Brick(50 * col + 20, 30 * row + 50, 45, 20));
            }
        }

        // Set up timer for the game loop
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    update();
                    repaint();
                }
            }
        });

        timer.start();
        addKeyListener(this);
        setFocusable(true);
    }

    // Update game state
    public void update() {
        ball.move();

        // Ball collision with paddle
        if (ball.x + ball.diameter >= paddle.x && ball.x <= paddle.x + paddle.width &&
                ball.y + ball.diameter >= paddle.y && ball.y <= paddle.y + paddle.height) {
            ball.bounceY();
        }

        // Ball collision with bricks
        for (Brick brick : bricks) {
            if (!brick.isBroken &&
                    ball.x + ball.diameter >= brick.x &&
                    ball.x <= brick.x + brick.width &&
                    ball.y + ball.diameter >= brick.y &&
                    ball.y <= brick.y + brick.height) {
                brick.isBroken = true;
                ball.bounceY();
            }
        }

        // Ball collision with walls
        if (ball.x <= 0 || ball.x + ball.diameter >= getWidth()) {
            ball.bounceX();
        }

        if (ball.y <= 0) {
            ball.bounceY();
        }

        // Ball falling off the screen (game over)
        if (ball.y + ball.diameter >= getHeight()) {
            gameOver = true;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameOver) {
            paddle.draw(g);
            ball.draw(g);
            for (Brick brick : bricks) {
                brick.draw(g);
            }
        } else {
            g.setColor(Color.BLACK);
            g.drawString("Game Over", getWidth() / 2 - 30, getHeight() / 2);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            paddle.move(-20);
        } else if (key == KeyEvent.VK_RIGHT) {
            paddle.move(20);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}

// Main frame to run the game
class BrickBreaker extends JFrame {
    public BrickBreaker() {
        setTitle("Brick Breaker");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new BrickBreaker();
    }
}