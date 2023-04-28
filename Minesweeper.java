import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minesweeper extends Application {
    private static final int SIZE = 10; // 扫雷网格大小
    private static final int MINES = 15; // 地雷数量

    private Button[][] buttons; // 扫雷按钮
    private boolean[][] revealed; // 已揭示的格子
    private boolean[][] flagged; // 已标记的格子
    private int remaining; // 剩余未揭示的格子数量

    public void start(Stage primaryStage) {
        buttons = new Button[SIZE][SIZE];
        revealed = new boolean[SIZE][SIZE];
        flagged = new boolean[SIZE][SIZE];
        remaining = SIZE * SIZE - MINES;

        // 创建网格布局
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(2);
        gridPane.setVgap(2);

        // 初始化按钮和事件处理
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button button = new Button();
                button.setPrefSize(40, 40);
                button.setFont(Font.font("Arial", 18));
                button.setOnMouseClicked(e -> handleButtonClick(row, col, e.getButton().toString()));
                buttons[row][col] = button;
                gridPane.add(button, col, row);
            }
        }

        // 布置地雷
        placeMines();

        // 创建场景并设置舞台
        Scene scene = new Scene(gridPane);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void placeMines() {
        Random random = new Random();
        int count = 0;
        while (count < MINES) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            if (!revealed[row][col]) {
                revealed[row][col] = true;
                flagged[row][col] = false;
                buttons[row][col].setText("X");
                buttons[row][col].setTextFill(Color.RED);
                count++;
            }
        }
    }

    private void handleButtonClick(int row, int col, String mouseButton) {
        if (!revealed[row][col]) {
            if (mouseButton.equals("PRIMARY")) {
                if (flagged[row][col]) {
                    return; // 忽略已标记的格子
                }

                revealCell(row, col);
                if (remaining == 0) {
                    revealAll();
                    showGameOverAlert("Congratulations! You win!");
                } else if (buttons[row][col].getText().equals("X")) {
                    revealAll();
                    showGameOverAlert("Game over! You stepped on a mine.");
                }
            } else if (mouseButton.equals("SECONDARY")) {
                if (revealed[row][col]) {
                    return; // 忽略已揭示的格子
                }

                flagged[row][col] = !flagged[row][col];
                if (flagged[row][col]) {
                    buttons[row][col].setText("F");
                    buttons[row][col].setTextFill(Color.GREEN);
                } else {
                    buttons[row][col].setText("");
                }
            }
        }
    }

    private void revealCell(int row, int col) {
        if (!revealed[row][col] && !flagged[row][col]) {
            revealed[row][col] = true;
            remaining--;

            int mines = countAdjacentMines(row, col);
            if (mines > 0) {
                buttons[row][col].setText(Integer.toString(mines));
            } else {
                buttons[row][col].setText("");
                revealNeighbors(row, col);
            }
        }
    }

    private void revealNeighbors(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < SIZE && j >= 0 && j < SIZE) {
                    revealCell(i, j);
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < SIZE && j >= 0 && j < SIZE && buttons[i][j].getText().equals("X")) {
                    count++;
                }
            }
        }
        return count;
    }

    private void revealAll() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!revealed[row][col]) {
                    revealCell(row, col);
                }
            }
        }
    }

    private void showGameOverAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
