package me.dariansandru;

import me.dariansandru.controller.ChessController;
import me.dariansandru.domain.validator.exception.ValidatorException;
import me.dariansandru.io.InputDevice;
import me.dariansandru.io.OutputDevice;
import me.dariansandru.domain.*;
import me.dariansandru.domain.chess.Manual;
import me.dariansandru.io.exception.InputException;
import me.dariansandru.io.exception.OutputException;
import me.dariansandru.ui.consoleUI.ChessConsoleUI;
import me.dariansandru.ui.guiController.GUIController;

import java.util.Objects;

public class Main {

    public static void main(String[] args) throws InputException, OutputException, ValidatorException {
        InputDevice inputDevice = new InputDevice();
        OutputDevice outputDevice = new OutputDevice();

        int argLength = args.length;
        if (argLength == 0){
            throw new InputException("No arguments were provided.");
        }

        if (Objects.equals(args[0], "play") && argLength < 3){
            throw new InputException("Too few arguments were provided.");
        }
        else if (Objects.equals(args[0], "play") && argLength > 3){
            throw new InputException("Too many arguments were provided.");
        }

        if (Objects.equals(args[0], "resume") && argLength > 1){
            throw new InputException("Too many arguments were provided.");
        }

        Player p1 = new Player();
        Player p2 = new Player();

        switch (args[0].toLowerCase()) {
            case "gui" -> {
                ChessController chessController = new ChessController(p1, p2);
                ChessConsoleUI chessConsoleUI = new ChessConsoleUI(inputDevice, outputDevice, chessController);
                GUIController guiController = new GUIController(chessConsoleUI);
                guiController.run();
            }
            case "play" -> {
                p1.setUsername(args[1]);
                p2.setUsername(args[2]);

                ChessController chessController = new ChessController(p1, p2);
                ChessConsoleUI chessConsoleUI = new ChessConsoleUI(inputDevice, outputDevice, chessController);
                chessConsoleUI.show();
            }
            case "resume" -> {
                String readFile = "files/chessCurrentGame.txt";
                if (inputDevice.isFileEmpty(readFile)){
                    throw new InputException("There is no current game.");
                }

                p1.setUsername(inputDevice.readLine(readFile, 1));
                p2.setUsername(inputDevice.readLine(readFile, 2));

                ChessController chessController = new ChessController(p1, p2);
                ChessConsoleUI chessConsoleUI = new ChessConsoleUI(inputDevice, outputDevice, chessController);

                chessConsoleUI.showResumedGame();
                chessConsoleUI.show();
            }
            case "rules" -> Manual.showRules(outputDevice);
            default -> throw new IllegalStateException("Could not find command: " + args[0]);
        }
    }
}

//TODO Fix ambiguous moves
//TODO Add en passant and castling
//TODO fix checkmate
//TODO add stalemate
//TODO refactor weights for the engine