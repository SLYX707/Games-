// 导入所需的库
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JPanel implements KeyListener {
    // 游戏窗口尺寸
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    // 单元格大小
    private static final int UNIT_SIZE = 20;

    // 游戏单元格数量
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    // 游戏刷新延迟
    private static final int DELAY = 100;

    // 蛇的坐标
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];

    // 蛇的初始长度
    private int bodyParts = 6;

    // 吃到的苹果数量
    private int applesEaten = 0;

    // 苹果的坐标
    private int appleX;
    private int appleY;

    // 障碍物坐标
    private List<Integer> obstaclesX = new ArrayList<>();
    private List<Integer> obstaclesY = new ArrayList<>();

    // 特殊道具的坐标和类型
    private int specialItemX;
    private int specialItemY;
    private char specialItemType;

    // 蛇的移动方向
    private char direction = 'R';

    // 游戏是否正在运行
    private boolean running = false;

    // 游戏难度级别
    private int difficulty = 1; // 初始难度为1

    // 计时器
    private Timer timer;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
        startGame();
    }

    private void startGame() {
        newApple();
        newSpecialItem();
        createObstacles();
        running = true;
        timer = new Timer(DELAY, e -> gameLoop());
        timer.start();
    }

    private void gameLoop() {
        if (running) {
            move();
            checkApple();
            checkSpecialItem();
            checkCollision();
            repaint();
        }
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkSpecialItem() {
        if (x[0] == specialItemX && y[0] == specialItemY) {
            // 根据特殊道具类型执行相应的操作
            switch (specialItemType) {
                case 'S':
                    // 缩小道具：减少蛇的身体长度
                    if (bodyParts > 1) {
                        bodyParts--;
                    }
                    break;
                case 'F':
                    // 加速道具：增加游戏刷新延迟，加快蛇的移动速度
                    if (DELAY > 50) {
                        DELAY -= 10;
                        timer.setDelay(DELAY);
                    }
                    break;
                // 其他特殊道具类型的处理...
            }

            newSpecialItem();
        }
    }

    private void checkCollision() {
        // 检查蛇头是否撞到边界
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            gameOver();
        }

        // 检查蛇头是否撞到自己的身体
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                gameOver();
            }
        }

        // 检查蛇头是否撞到障碍物
        for (int i = 0; i < obstaclesX.size(); i++) {
            if (x[0] == obstaclesX.get(i) && y[0] == obstaclesY.get(i)) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        running = false;
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + applesEaten, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void newApple() {
        Random random = new Random();
        appleX = random.nextInt((WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void newSpecialItem() {
        Random random = new Random();
        specialItemX = random.nextInt((WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        specialItemY = random.nextInt((HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

        // 随机生成特殊道具类型
        int itemType = random.nextInt(3);
        switch (itemType) {
            case 0:
                specialItemType = 'S'; // 缩小道具
                break;
            case 1:
                specialItemType = 'F'; // 加速道具
                break;
            // 其他特殊道具类型...
        }
    }

    private void createObstacles() {
        // 在随机位置生成障碍物
        Random random = new Random();
        int numObstacles = difficulty * 5; // 随着难度级别增加障碍物数量
        for (int i = 0; i < numObstacles; i++) {
            int obstacleX = random.nextInt((WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            int obstacleY = random.nextInt((HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

            // 确保障碍物不与蛇、苹果和特殊道具重叠
            while (obstacleX == appleX && obstacleY == appleY || obstacleX == specialItemX && obstacleY == specialItemY
                    || isSnakeOverlap(obstacleX, obstacleY)) {
                obstacleX = random.nextInt((WIDTH / UNIT_SIZE)) * UNIT_SIZE;
                obstacleY = random.nextInt((HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            }

            obstaclesX.add(obstacleX);
            obstaclesY.add(obstacleY);
        }
    }

    private boolean isSnakeOverlap(int x, int y) {
        for (int i = 0; i < bodyParts; i++) {
            if (this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // 绘制苹果
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // 绘制特殊道具
            g.setColor(Color.orange);
            g.fillOval(specialItemX, specialItemY, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.black);
            g.drawString(Character.toString(specialItemType), specialItemX + 7, specialItemY + 15);

            // 绘制障碍物
            g.setColor(Color.gray);
            for (int i = 0; i < obstaclesX.size(); i++) {
                g.fillRect(obstaclesX.get(i), obstaclesY.get(i), UNIT_SIZE, UNIT_SIZE);
            }

            // 绘制蛇
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // 绘制得分和难度级别
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Score: " + applesEaten, 10, 20);
            g.drawString("Difficulty: " + difficulty, 10, 40);
        } else {
            // 游戏结束时显示游戏结束的提示信息
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!running) {
                    resetGame();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void resetGame() {
        // 重置游戏状态和参数
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        difficulty = 1;
        obstaclesX.clear();
        obstaclesY.clear();
        newApple();
        newSpecialItem();
        createObstacles();
        running = true;
        timer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
                  

