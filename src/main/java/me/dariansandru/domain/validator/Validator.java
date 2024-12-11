package me.dariansandru.domain.validator;

import me.dariansandru.domain.validator.exception.ValidatorException;
import me.dariansandru.round.ChessRound;
import me.dariansandru.domain.chess.piece.Piece;
import me.dariansandru.utilities.ChessUtils;

import java.util.Objects;

public class Validator {
    public static boolean validMovePieceNotation(char piece) throws ValidatorException {
        if (!ChessUtils.isValidPiece(piece)){
            throw new ValidatorException("Piece can only be K, Q, R, B or N!");
        }
        return true;
    }

    public static boolean validMoveRankNotation(char rank) throws ValidatorException {
        if ('a' > rank || rank > 'h'){
            throw new ValidatorException("Rank must be between 'a' and 'h'!");
        }
        return true;
    }

    public static boolean validMoveFileNotation(char file) throws ValidatorException {
        if ('0' > file || file > '8'){
            throw new ValidatorException("File must be between '0' and '8'!");

        }
        return true;
    }

    public static boolean validMoveOptionalNotation(char optional) throws ValidatorException {
        if (optional != '+' && optional != '#'){
            throw new ValidatorException("End of move optionals can only be '+' or '#'!");

        }
        return true;
    }

    public static boolean validMoveNotation(String move) throws ValidatorException {
        if (move.isEmpty()) return false;

        char piece;
        char rank;
        char file;
        char optional;
        String[] moveTakeParts = move.split("[xX]");

        if (moveTakeParts.length == 2){
            if (moveTakeParts[1].length() < 2 || moveTakeParts[1].length() > 3) return false;
            if (moveTakeParts[0].isEmpty()) piece = ' ';
            else piece = moveTakeParts[0].charAt(0);

            if (moveTakeParts[1].length() == 2){
                rank = moveTakeParts[1].charAt(0);
                file = moveTakeParts[1].charAt(1);

                if (!validMovePieceNotation(piece)) return false;
                if (!validMoveRankNotation(rank)) return false;
                return validMoveFileNotation(file);
            }
            else {
                rank = moveTakeParts[1].charAt(0);
                file = moveTakeParts[1].charAt(1);
                optional = moveTakeParts[1].charAt(2);

                if (!validMovePieceNotation(piece)) return false;
                if (!validMoveRankNotation(rank)) return false;
                if (!validMoveFileNotation(file)) return false;
                return validMoveOptionalNotation(optional);
            }
        }

        if (move.length() < 2 || move.length() > 4) return false;

        if (move.length() == 2){
            rank = move.charAt(0);
            file = move.charAt(1);

            if (!validMoveRankNotation(rank)) return false;
            return validMoveFileNotation(file);
        }
        else if (move.length() == 3){
            piece = move.charAt(0);
            rank = move.charAt(1);
            file = move.charAt(2);

            if (!validMovePieceNotation(piece)) return false;
            if (!validMoveRankNotation(rank)) return false;
            return validMoveFileNotation(file);
        }
        else {
            piece = move.charAt(0);
            rank = move.charAt(1);
            file = move.charAt(2);
            optional = move.charAt(3);

            if (!validMovePieceNotation(piece)) return false;
            if (!validMoveRankNotation(rank)) return false;
            if (!validMoveFileNotation(file)) return false;
            return validMoveOptionalNotation(optional);
        }
    }

    private static boolean validateDiagonalMove(ChessRound chessRound, int currentRow, int currentCol, int newRow, int newCol){
        Piece[][] pieces = chessRound.getPieces();

        // Case 1 - Up-right
        if (newRow > currentRow && newCol > currentCol){
            currentRow++;
            currentCol++;
            while (currentRow != newRow && currentCol != newCol){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentRow++;
                currentCol++;
            }
        }

        // Case 2 - Up-left
        if (newRow > currentRow && newCol < currentCol){
            currentRow++;
            currentCol--;
            while (currentRow != newRow && currentCol != newCol){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentRow++;
                currentCol--;
            }
        }

        // Case 3 - Down-right
        if (newRow < currentRow && newCol > currentCol){
            currentRow--;
            currentCol++;
            while (currentRow != newRow && currentCol != newCol){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentRow--;
                currentCol++;
            }
        }

        // Case 4 - Down-left
        if (newRow < currentRow && newCol < currentCol){
            currentRow--;
            currentCol--;
            while (currentRow != newRow && currentCol != newCol){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentRow--;
                currentCol--;
            }
        }

        return true;
    }

    private static boolean validateRowMove(ChessRound chessRound, int currentRow, int currentCol, int newRow, int newCol){
        Piece[][] pieces = chessRound.getPieces();

        // Case 0 - Did not move on the row
        if (currentCol != newCol) return false;

        // Case 1 - Moves down
        if (currentRow > newRow){
            currentRow--;
            while (currentRow != newRow){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentRow--;
            }
        }

        // Case 2 - Moves up
        if (currentRow < newRow){
            currentRow++;
            while (currentRow != newRow){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentRow++;
            }
        }

        return true;
    }

    private static boolean validateColMove(ChessRound chessRound, int currentRow, int currentCol, int newRow, int newCol){
        Piece[][] pieces = chessRound.getPieces();

        // Case 0 - Did not move on the column
        if (currentRow != newRow) return false;

        // Case 1 - Moves right
        if (currentCol > newCol){
            currentCol--;
            while (currentCol != newCol){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentCol--;
            }
        }

        // Case 2 - Moves left
        if (currentCol < newCol){
            currentCol++;
            while (currentCol != newCol){
                if (!Objects.equals(pieces[currentRow][currentCol].getName(), "None")) return false;
                currentCol++;
            }
        }

        return true;
    }

    public static boolean validateObstruction(ChessRound chessRound, String piece, int currentRow, int currentCol, int newRow, int newCol){
        // Obstruction validation required only for Bishop, Rook and Queen.
        // Pawns and King have validation implemented in their legalMove logic.
        // Knights do not require obstruction checking.
        switch(piece){
            case "B" -> {
                return validateDiagonalMove(chessRound, currentRow, currentCol, newRow, newCol);
            }
            case "R" -> {
                return (validateRowMove(chessRound, currentRow, currentCol, newRow, newCol) ||
                        validateColMove(chessRound, currentRow, currentCol, newRow, newCol));
            }
            case "Q" -> {
                return (validateRowMove(chessRound, currentRow, currentCol, newRow, newCol) ||
                        validateColMove(chessRound, currentRow, currentCol, newRow, newCol) ||
                        validateDiagonalMove(chessRound, currentRow, currentCol, newRow, newCol));
            }
        }

        return true;
    }
}
