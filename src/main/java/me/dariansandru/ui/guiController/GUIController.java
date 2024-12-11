package me.dariansandru.ui.guiController;

import me.dariansandru.controller.ChessController;
import me.dariansandru.domain.chess.chessEngine.ChessEngine;
import me.dariansandru.domain.chess.piece.PieceColour;
import me.dariansandru.domain.validator.exception.ValidatorException;
import me.dariansandru.io.exception.InputException;
import me.dariansandru.round.ChessRound;
import me.dariansandru.ui.consoleUI.ChessConsoleUI;
import me.dariansandru.ui.gui.ChessGUI;

import javax.swing.*;
import java.awt.*;

public class GUIController {

    private final ChessRound chessRound;
    private final ChessEngine chessEngine;
    private int turnCounter;

    public GUIController(ChessConsoleUI chessConsoleUI) {
        ChessController chessController = chessConsoleUI.getChessController();
        this.chessRound = chessController.getChessRound();
        this.chessEngine = new ChessEngine();
        chessEngine.setChessRound(chessRound);
    }

    public void run() {
        JFrame frame = new JFrame("Chess GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        ChessGUI chessPanel = new ChessGUI(chessRound);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("Insert move (White's turn):");
        JTextField moveInput = new JTextField();
        JButton submitButton = new JButton("Submit");
        JButton resetButton = new JButton("Reset Game");
        JLabel error = new JLabel();
        JLabel evaluationLabel = new JLabel("Position Evaluation: Equal");

        Font smallFont = new Font("Arial", Font.PLAIN, 14);
        instructionLabel.setFont(smallFont);
        moveInput.setFont(smallFont);
        submitButton.setFont(smallFont);
        resetButton.setFont(smallFont);
        error.setFont(smallFont);
        evaluationLabel.setFont(smallFont);

        moveInput.setPreferredSize(new Dimension(instructionLabel.getPreferredSize().width, 20));
        submitButton.setPreferredSize(new Dimension(80, 30));
        resetButton.setPreferredSize(new Dimension(100, 30));
        evaluationLabel.setToolTipText(evaluationLabel.getText());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 1, 2, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        inputPanel.add(instructionLabel);
        inputPanel.add(moveInput);
        inputPanel.add(submitButton);
        inputPanel.add(resetButton);
        inputPanel.add(evaluationLabel);
        inputPanel.add(error);

        rightPanel.add(inputPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            try {
                String move = moveInput.getText();
                PieceColour currentColour = (turnCounter % 2 == 0) ? PieceColour.WHITE : PieceColour.BLACK;
                PieceColour nextColour = (turnCounter % 2 == 0) ? PieceColour.BLACK : PieceColour.WHITE;

                if (!chessRound.checkMovePiece(move, currentColour)) {
                    error.setText("Invalid move");
                    return;
                }

                System.out.println("Move is valid: " + move);
                var startLocation = chessRound.getStartLocation(move, currentColour);
                chessRound.movePiece(move, currentColour);
                chessPanel.updateBoard(move, currentColour, startLocation);
                turnCounter++;

                instructionLabel.setText("Insert move (" + (nextColour == PieceColour.WHITE ? "White's turn" : "Black's turn") + "):");

                int evaluation = chessRound.computeAdvantage();
                String evaluationText = evaluation > 0 ? "White is ahead " + evaluation : evaluation < 0 ? "Black is ahead " + evaluation : "Equal";
                evaluationLabel.setText("Position Evaluation: " + evaluationText);
                evaluationLabel.setToolTipText("Position Evaluation: " + evaluationText);

                if (chessRound.isCheckmate(nextColour)) {
                    String winner = (turnCounter % 2 == 0) ? "Black" : "White";
                    JOptionPane.showMessageDialog(frame, winner + " won by checkmate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);

                    turnCounter = 0;
                    chessRound.resetBoard();
                    chessPanel.drawBoard();
                    moveInput.setText("");
                    error.setText("");
                    instructionLabel.setText("Insert move (White's turn):");
                    evaluationLabel.setText("Position Evaluation: Equal");
                }

                moveInput.setText("");
                error.setText("");

            } catch (ValidatorException | InputException ex) {
                throw new RuntimeException(ex);
            }
        });

        resetButton.addActionListener(e -> {
            try {
                chessRound.resetBoard();
            } catch (InputException ex) {
                throw new RuntimeException(ex);
            }
            chessPanel.drawBoard();
            turnCounter = 0;
            instructionLabel.setText("Insert move (White's turn):");
            evaluationLabel.setText("Position Evaluation: Equal");
            moveInput.setText("");
            error.setText("");
        });

        frame.setLayout(new BorderLayout());
        frame.add(chessPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
