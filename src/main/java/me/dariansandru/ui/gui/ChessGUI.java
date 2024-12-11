package me.dariansandru.ui.gui;

import me.dariansandru.domain.chess.piece.Piece;
import me.dariansandru.domain.chess.piece.PieceColour;
import me.dariansandru.domain.chess.piece.Pawn;
import me.dariansandru.round.ChessRound;
import me.dariansandru.utilities.ChessUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static me.dariansandru.utilities.ChessUtils.getColRow;
import me.dariansandru.utilities.Pair;

/**
 * Using this class allows the user to use a custom-made GUI for a Chess game.
 */
public class ChessGUI extends JPanel {
    
    private Piece[][] pieces;
    private final ChessRound chessRound;
    private Point selectedSquare;

    public ChessGUI(ChessRound chessRound) {
        this.chessRound = chessRound;
        this.pieces = chessRound.getPieces();
        this.setLayout(new GridLayout(8, 8));
        drawBoard();
    }

    /**
     * Fetches the image of a piece from the images directory to display on the board.
     *
     * @param piece Piece to display.
     * @return Image of the piece with the correct colour.
     */
    private Image getImageFromPiece(Piece piece) {
        if (Objects.equals(piece.getName(), "None"))
            return null;
        PieceColour colour = piece.getColour();
        String imageName = "images/" + piece.getName() + colour.toString() + ".jpeg";
        ImageIcon icon = new ImageIcon(imageName);
        return icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
    }

    /**
     * Draws the board in the initial state. Should only be used once because
     * of the high cost of the operation.
     */
    public void drawBoard() {
        this.removeAll();
        pieces = chessRound.getPieces();

        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                JLabel square = new JLabel();
                square.setOpaque(true);

                if ((row + col) % 2 == 0) {
                    square.setBackground(Color.PINK);
                } else {
                    square.setBackground(Color.WHITE);
                }

                Image scaledImage = getImageFromPiece(pieces[row][col]);
                if (scaledImage != null) {
                    square.setIcon(new ImageIcon(scaledImage));
                }
                this.add(square);
            }
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Updates the board to increase efficiency of displays by only updating
     * the moved piece.
     *
     * @param move Move that was played.
     * @param colour Colour of the piece that was moved.
     * @param startLocation The start location of the moved piece.
     */
    public void updateBoard(String move, PieceColour colour, Pair<Integer, Integer> startLocation) {
        try {
            System.out.println(move);
            int destinationCol = getColRow(move).getValue1();
            int destinationRow = getColRow(move).getValue2();

            int startRow = startLocation.getValue1();
            int startCol = startLocation.getValue2();

            Piece pieceToMove = ChessUtils.getPiece(String.valueOf(move.charAt(0)), colour);
            if (Objects.equals(pieceToMove.getName(), "None"))
                pieceToMove = new Pawn(colour);

            int startSquareIndex = (7 - startRow) * 8 + startCol;
            int destinationSquareIndex = (7 - destinationRow) * 8 + destinationCol;
            
            System.out.println(startRow + " " + startCol);
            System.out.println(destinationCol + " " + destinationRow);
            System.out.println(startSquareIndex + " " + destinationSquareIndex);

            JLabel startSquare = (JLabel) this.getComponent(startSquareIndex);
            startSquare.setIcon(null);

            JLabel destinationSquare = (JLabel) this.getComponent(destinationSquareIndex);
            Image scaledImage = getImageFromPiece(pieceToMove);
            if (scaledImage != null) {
                destinationSquare.setIcon(new ImageIcon(scaledImage));
            }

            this.revalidate();
            this.repaint();
            
        } catch (Exception ex) {
            System.err.println("Error updating board: " + ex.getMessage());
        }
    }
}
