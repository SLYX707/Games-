import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaneGame extends Application {
    private static final int WIDTH = 800; // 游戏窗口宽度
    private static final int HEIGHT = 600; // 游戏窗口高度
    private static final int PLAYER_SIZE = 40; // 玩家飞机尺寸
    private static final int ENEMY_SIZE = 20; // 敌机尺寸
    private static final int BULLET_SIZE = 10; // 子弹尺寸
    private static final int BULLET_SPEED = 5; // 子弹速度
    private static final int ENEMY_SPEED = 2; // 敌机速度
    private static final int SPAWN_INTERVAL = 60; // 敌机生成间隔

    private Pane gamePane;
    private Rectangle player;
    private List<Rectangle> enemies;
    private List<Circle> bullets;
    private Random random;
    private int spawnCounter;
    private boolean gameOver;

    public void start(Stage primaryStage) {
        gamePane = new Pane();
        gamePane.setPrefSize(WIDTH, HEIGHT);

        BorderPane root = new BorderPane();
        root.setCenter(gamePane);

        Label scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font(20));
        HBox topBox = new HBox(scoreLabel);
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));

        primaryStage.setTitle("Plane Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        startGame();
    }

    private void startGame() {
        player = new Rectangle(PLAYER_SIZE, PLAYER_SIZE, Color.BLUE);
        player.setX(WIDTH / 2 - PLAYER_SIZE / 2);
        player.setY(HEIGHT - PLAYER_SIZE - 20);
        gamePane.getChildren().add(player);

        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        random = new Random();
        spawnCounter = 0;
        gameOver = false;

        AnimationTimer timer = new AnimationTimer() {
            public void handle(long now) {
                if (gameOver) {
                    stop();
                    return;
                }

                updatePlayer();
                updateEnemies();
                updateBullets();
                checkCollisions();
                spawnEnemies();

                if (spawnCounter >= Integer.MAX_VALUE - 1) {
                    spawnCounter = 0;
                } else {
                    spawnCounter++;
                }
            }
        };
        timer.start();
    }

    private void updatePlayer() {
        if (player.isPressed(KeyCode.LEFT) && player.getX() > 0) {
            player.setX(player.getX() - 5);
        }
        if (player.isPressed(KeyCode.RIGHT) && player.getX() + PLAYER_SIZE < WIDTH) {
            player.setX(player.getX() + 5);
        }
        if (player.isPressed(KeyCode.UP) && player.getY
                player.setY(player.getY() - 5);
            }
            if (player.isPressed(KeyCode.DOWN) && player.getY() + PLAYER_SIZE < HEIGHT) {
                player.setY(player.getY() + 5);
            }
        }
    }

    private void updateEnemies() {
        for (Rectangle enemy : enemies) {
            enemy.setY(enemy.getY() + ENEMY_SPEED);
            if (enemy.getY() > HEIGHT) {
                gamePane.getChildren().remove(enemy);
            }
        }
        enemies.removeIf(enemy -> enemy.getY() > HEIGHT);
    }

    private void updateBullets() {
        for (Circle bullet : bullets) {
            bullet.setCenterY(bullet.getCenterY() - BULLET_SPEED);
            if (bullet.getCenterY() < 0) {
                gamePane.getChildren().remove(bullet);
            }
        }
        bullets.removeIf(bullet -> bullet.getCenterY() < 0);
    }

    private void checkCollisions() {
        Bounds playerBounds = player.getBoundsInParent();
        for (Rectangle enemy : enemies) {
            Bounds enemyBounds = enemy.getBoundsInParent();
            if (playerBounds.intersects(enemyBounds)) {
                gameOver = true;
                gamePane.getChildren().removeAll(enemies);
                break;
            }
        }

        for (Circle bullet : bullets) {
            Bounds bulletBounds = bullet.getBoundsInParent();
            for (Rectangle enemy : enemies) {
                Bounds enemyBounds = enemy.getBoundsInParent();
                if (bulletBounds.intersects(enemyBounds)) {
                    gamePane.getChildren().removeAll(bullet, enemy);
                    bullets.remove(bullet);
                    enemies.remove(enemy);
                    break;
                }
            }
        }
    }

    private void spawnEnemies() {
        if (spawnCounter % SPAWN_INTERVAL == 0) {
            Rectangle enemy = new Rectangle(ENEMY_SIZE, ENEMY_SIZE, Color.RED);
            enemy.setX(random.nextInt(WIDTH - ENEMY_SIZE));
            enemy.setY(0);
            enemies.add(enemy);
            gamePane.getChildren().add(enemy);
        }
    }

    private void handleKeyPress(KeyCode keyCode) {
        if (keyCode == KeyCode.SPACE) {
            shoot();
        }
    }

    private void handleKeyRelease(KeyCode keyCode) {
        if (keyCode == KeyCode.SPACE) {
            // Release space key handling
        }
    }

    private void shoot() {
        Circle bullet = new Circle(player.getX() + PLAYER_SIZE / 2, player.getY(), BULLET_SIZE, Color.YELLOW);
        bullets.add(bullet);
        gamePane.getChildren().add(bullet);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
