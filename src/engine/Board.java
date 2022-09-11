/* Don-CODE */
package engine;

import java.util.ArrayList;
import java.util.Scanner;

public class Board {

    private ArrayList<Cell> emptyCells;
    private ArrayList<Cell> rootValues;
    private Scanner scanner;
    private CellState[][] board;
    private functions.Constants constants;

    public Board() {
        constants = new functions.Constants();
        initializeBoard();
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    private ArrayList<Cell> getEmptyCells() {
        emptyCells = new ArrayList<>();
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (this.board[i][j] == CellState.EMPTY) {
                    emptyCells.add(new Cell(i, j));
                }
            }
        }
        return emptyCells;
    }

    public void setEmptyCells(ArrayList<Cell> emptyCells) {
        this.emptyCells = emptyCells;
    }

    public ArrayList<Cell> getRootValues() {
        return rootValues;
    }

    public void setRootValues(ArrayList<Cell> rootValues) {
        this.rootValues = rootValues;
    }

    public int returnMin(ArrayList<Integer> list) {
        int min = Integer.MAX_VALUE;
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) < min) {
                min = list.get(i);
                index = i;
            }
        }
        return list.get(index);
    }

    public int returnMax(ArrayList<Integer> list) {
        int max = Integer.MIN_VALUE;
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) > max) {
                max = list.get(i);
                index = i;
            }
        }
        return list.get(index);
    }

    private void initializeBoard() {
        this.rootValues = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.board = new CellState[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
    }

    public boolean isRunning() {
        if (isWinning(CellState.COMPUTER)) {
            return false;
        }
        if (isWinning(CellState.USER)) {
            return false;
        }
        if (getEmptyCells().isEmpty()) {
            return false;
        }
        return true;
    }

    public void makeUserInput() {
        int x = main.Main.variables.getBtn_xValue();
        int y = main.Main.variables.getBtn_yValue();
        Cell cell = new Cell(x, y);
        move(cell, CellState.USER);
    }

    public void setupBoard() {
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                board[i][j] = CellState.EMPTY;
            }
        }
    }

    public boolean isWinning(CellState player) {
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        return false;
    }

    public void move(Cell cell, CellState player) {
        this.board[cell.getX()][cell.getY()] = player;
    }

    public Cell getBestMove() {
        int max = Integer.MIN_VALUE;
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < rootValues.size(); ++i) {
            if (max < rootValues.get(i).getMinimaxValue()) {
                max = rootValues.get(i).getMinimaxValue();
                best = i;
            }
        }
        return rootValues.get(best);
    }

    private void displayVisually(int i, int j, String value) {
        String name = Integer.toString(i) + Integer.toString(j);
        switch (name) {
            case "00":
                if (main.Main.variables.getGameButtonCanSelect(0)) {
                    main.Main.variables.setGameButtonCanSelect(0, false);
                    main.Main.label_00.setForeground(constants.getRed());
                    main.Main.label_00.setText(value);
                }
                break;
            case "01":
                if (main.Main.variables.getGameButtonCanSelect(1)) {
                    main.Main.variables.setGameButtonCanSelect(1, false);
                    main.Main.label_01.setForeground(constants.getRed());
                    main.Main.label_01.setText(value);
                }
                break;
            case "02":
                if (main.Main.variables.getGameButtonCanSelect(2)) {
                    main.Main.variables.setGameButtonCanSelect(2, false);
                    main.Main.label_02.setForeground(constants.getRed());
                    main.Main.label_02.setText(value);
                }
                break;
            case "10":
                if (main.Main.variables.getGameButtonCanSelect(3)) {
                    main.Main.variables.setGameButtonCanSelect(3, false);
                    main.Main.label_10.setForeground(constants.getRed());
                    main.Main.label_10.setText(value);
                }
                break;
            case "11":
                if (main.Main.variables.getGameButtonCanSelect(4)) {
                    main.Main.variables.setGameButtonCanSelect(4, false);
                    main.Main.label_11.setForeground(constants.getRed());
                    main.Main.label_11.setText(value);
                }
                break;
            case "12":
                if (main.Main.variables.getGameButtonCanSelect(5)) {
                    main.Main.variables.setGameButtonCanSelect(5, false);
                    main.Main.label_12.setForeground(constants.getRed());
                    main.Main.label_12.setText(value);
                }
                break;
            case "20":
                if (main.Main.variables.getGameButtonCanSelect(6)) {
                    main.Main.variables.setGameButtonCanSelect(6, false);
                    main.Main.label_20.setForeground(constants.getRed());
                    main.Main.label_20.setText(value);
                }
                break;
            case "21":
                if (main.Main.variables.getGameButtonCanSelect(7)) {
                    main.Main.variables.setGameButtonCanSelect(7, false);
                    main.Main.label_21.setForeground(constants.getRed());
                    main.Main.label_21.setText(value);
                }
                break;
            case "22":
                if (main.Main.variables.getGameButtonCanSelect(8)) {
                    main.Main.variables.setGameButtonCanSelect(8, false);
                    main.Main.label_22.setForeground(constants.getRed());
                    main.Main.label_22.setText(value);
                }
                break;
        }

    }

    public void displayBoard() {
        main.Main.variables.setGameWindowGreenButtonEnable(false);
        main.Main.panel_MsgInWelcomeOkGreen.setBackground(constants.getGray());
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                if (this.board[i][j].toString().equals("X") || this.board[i][j].toString().equals("O")) {
                    displayVisually(i, j, this.board[i][j].toString());
                }
            }
        }
    }

    public void callMinimax(int depth, CellState palyer) {
        rootValues.clear();
        minimax(depth, palyer);
    }

    private int minimax(int depth, CellState player) {
        if (isWinning(CellState.COMPUTER)) {
            return +1;
        }
        if (isWinning(CellState.USER)) {
            return -1;
        }

        ArrayList<Cell> availableCells = getEmptyCells();
        if (availableCells.isEmpty()) {
            return 0;
        }

        ArrayList<Integer> scores = new ArrayList<>();
        for (int i = 0; i < availableCells.size(); i++) {
            Cell point = availableCells.get(i);
            if (player == CellState.COMPUTER) {
                move(point, CellState.COMPUTER);
                int currrentScore = minimax(depth + 1, CellState.USER);
                scores.add(currrentScore);
                if (depth == 0) {
                    point.setMinimaxValue(currrentScore);
                    rootValues.add(point);
                }
            } else if (player == CellState.USER) {
                move(point, CellState.USER);
                scores.add(minimax(depth + 1, CellState.COMPUTER));
            }
            board[point.getX()][point.getY()] = CellState.EMPTY;
        }
        if (player == CellState.COMPUTER) {
            return returnMax(scores);
        }
        return returnMin(scores);

    }

}
