/* Don-CODE */
package engine;

import java.awt.Font;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Game {

    public Board board;
    private Random random;
    private functions.Constants constants;

    public Game() {
        initializeGame();
        makeFirstMove();
        constants = new functions.Constants();
    }

    private void initializeGame() {
        this.board = new Board();
        this.random = new Random();
        this.board.setupBoard();
    }

    private void makeFirstMove() {
        if (!main.Main.variables.getUserFirstPlaying()) {
            Cell cell = new Cell(random.nextInt(Constants.BOARD_SIZE), random.nextInt(Constants.BOARD_SIZE));
            board.move(cell, CellState.COMPUTER);
            board.displayBoard();
        }
    }

    public void playGame() {
        Cell userCell = new Cell(main.Main.variables.getBtn_xValue(), main.Main.variables.getBtn_yValue());
        board.move(userCell, CellState.USER);
        board.displayBoard();
        if (board.isRunning()) {
            board.callMinimax(0, CellState.COMPUTER);
            board.move(board.getBestMove(), CellState.COMPUTER);
            board.displayBoard();
        }
        chackStatus();
    }
    
    /* Get current data & time */
    private String getDate() {
        java.text.SimpleDateFormat todaysDate = new java.text.SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return todaysDate.format((java.util.Calendar.getInstance()).getTime());
    }

    public void chackStatus() {
        if (!board.isRunning()) {
            if (board.isWinning(CellState.COMPUTER)) {
                main.Main.variables.setGameStart(false);
                main.Main.variables.setScoreList(getDate()+"@"+main.Main.variables.getClickCount()+"@Game loose");
                displayStatus("You loose");
            } else if (board.isWinning(CellState.USER)) {
                main.Main.variables.setGameStart(false);
                main.Main.variables.setScoreList(getDate()+"@"+main.Main.variables.getClickCount()+"@Game win");
                displayStatus("You won");
            } else {
                main.Main.variables.setGameStart(false);
                main.Main.variables.setScoreList(getDate()+"@"+main.Main.variables.getClickCount()+"@Game draw");
                displayStatus("Game draw");
            }
        }
    }

    private void displayStatus(String status) {
        main.Main.variables.getCard_main().show(main.Main.mainPanel, "panelFive");
        displayStatusThread(status);
        main.Main.variables.getWinLooseDisplayThread().start();
    }

    private void displayStatusThread(String text) {
        main.Main.variables.setWinLooseDisplayThread(new Thread(new Runnable() {
            public void run() {
                int i = 20;
                String soundSource = null;
                switch (text) {
                    case "You won":
                        soundSource = constants.getSOUND_win();
                        break;
                    case "You loose":
                    case "Game draw":
                        soundSource = constants.getSOUND_loose();
                        break;
                }
                if (main.Main.variables.getGameMusic()) {
                    try {                        
                        main.Main.variables.setClipsetWinLooseDisplay(AudioSystem.getClip());
                        main.Main.variables.getClipsetWinLooseDisplay().open(AudioSystem.getAudioInputStream(main.Main.class.getClassLoader().getResource(soundSource)));
                        main.Main.variables.getClipsetWinLooseDisplay().start();
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                while (true) {
                    if (i % 2 == 0) {
                        main.Main.label_WinLooseDrawDisplay.setForeground(constants.getRed());
                    } else {
                        main.Main.label_WinLooseDrawDisplay.setForeground(constants.getBlue());
                    }
                    main.Main.label_WinLooseDrawDisplay.setFont(new Font("Tempus Sans ITC", Font.BOLD, i));
                    main.Main.label_WinLooseDrawDisplay.setText(text);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    i++;
                    if (i >= 50) {
                        i = 18;
                    }
                }
            }
        }));
    }
}
