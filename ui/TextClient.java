package ui;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import card.Card;
import card.Character;
import card.Location;
import card.Weapon;
import configs.Configs;
import game.Game;
import game.GameRecord;
import game.Suggestion;
import tile.Position;
import tile.Room;

/**
 * A text based client for Cluedo. It use console to print out game board, and players use
 * console input to control the game process.
 * 
 * @author EASG7
 *
 */
public class TextClient {

    /**
     * System.in wrapped in scanner to get user input.
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Main function of this programme.
     *
     */
    public static void main(String[] args) {
        // show some welcome message
        welcomeMsg();
        // set up the game, dealing card, joining players, etc.
        Game game = setupGame();
        // game running!
        runGame(game);
        // when game stops, some clean-up process.
        gameStop(game);

        SCANNER.close();
    }

    /**
     * This method shows some welcome message
     */
    private static void welcomeMsg() {
        System.out.println("==============Cluedo text-based client v0.1==============");
        System.out.println("Some welcome message");
    }

    /**
     * This function initialises the game, creates solutions, deals cards, and joins
     * players.
     * 
     * @return --- the initialised, running game
     */
    private static Game setupGame() {
        Configs configurations = Configs.getConfiguration();
        // set how many players
        System.out.println("How many players?");
        int numPlayers = parseInt(configurations.getMinPlayer(), configurations.getMaxPlayer(), null);
        Game game = new Game(numPlayers, configurations.getNumDice());

        // let players choose which character to play with
        int playerIndex = 0;
        while (playerIndex != numPlayers) {
            playerIndex++;
            // list all choosable cards
            System.out.println("Please choose player " + playerIndex + " character:");

            List<Character> playableCharacters = game.getPlayableCharacters();
            int size = playableCharacters.size();
            for (int i = 0; i < size; i++) {
                System.out.println("" + (i + 1) + ". " + playableCharacters.get(i).toString());
            }

            // make a choice
            int choice = parseInt(1, size, game);

            System.out.println("What is your name?");
            String line = SCANNER.nextLine();

            // join this player in
            game.joinPlayer(playableCharacters.get(choice - 1), line);
        }

        // set which character is first to move
        game.decideWhoMoveFirst();
        // create solution
        game.creatSolution();
        // evenly deal cards
        game.dealCard();

        return game;
    }

    /**
     * Run the game
     * 
     * @param game
     *            --- the game
     */
    private static void runGame(Game game) {
        while (game.isGameRunning()) {
            // print board
            System.out.println(game.getBoardString());
            StepRun(game);
        }
    }

    /**
     * This method represents a single step of game process. A single step means: system
     * prompt options to the player, and the player choose an option to act.
     * 
     * @param game
     *            --- the running game
     */
    private static void StepRun(Game game) {

        Character currentPlayer = game.getCurrentPlayer();
        int remainingSteps = game.getRemainingSteps(currentPlayer);

        System.out.println(currentPlayer.toString() + "'s move.");

        // if this player hasn't roll a dice, roll dice
        if (remainingSteps == 0) {
            int movements = NumberOfMovements(game);
            System.out.println("Do you want to repeat the roll? (cost: 1 coin)");
            System.out.println("1. Yes");
            System.out.println("2. No");

            int yesNo = parseInt(1, 2, game);
            if (yesNo == 1) {
                if (game.extractSalaryPlayer(currentPlayer, 1)) {
                    movements = NumberOfMovements(game);
                }
                else {
                    System.out.println("I'm sorry but you don't have enough salary to do it");
                }
            }
            game.setRemainingSteps(currentPlayer, movements);
            remainingSteps = movements;
        }

        System.out.println("You have " + remainingSteps + " steps left.");

        // check what positions the player can move to
        Position currentPos = game.getPlayerPosition(currentPlayer);
        List<Position> movablePos = game.getMovablePositions(currentPlayer);

        // two helper flags
        boolean hasSuggestionOption = false;
        boolean hasNowhereToGo = false;
        int menuNo = 1; // menu number

        // prompt options of movable positions
        for (Position destination : movablePos) {
            System.out.println("" + menuNo + ". " + currentPos.optionString(destination));
            menuNo++;
        }

        // prompt accusation option
        System.out.println("" + menuNo + ". Make accusation.");
        menuNo++;

        // prompt suggestion option if the player is in a room
        if (currentPos instanceof Room) {
            System.out.println("" + menuNo + ". Make suggestion.");
            hasSuggestionOption = true;
            menuNo++;
        }

        // if the player has no position to move (blocked by others)
        if (movablePos.isEmpty()) {
            System.out.println("" + menuNo + ". Nowhere to move, end turn.");
            hasNowhereToGo = true;
            menuNo++;
        }

        // get player's choice
        menuNo--;
        int choice = parseInt(1, menuNo, game);

        if (choice <= movablePos.size()) {
            // player chose to move to one of movable positions

            // move the player
            Position destination = movablePos.get(choice - 1);
            game.movePlayer(currentPlayer, destination);

            // if player has entered a room, he can make a suggestion
            if (destination instanceof Room) {
                // first update the board display
                System.out.println(game.getBoardString());
                // move into a room, now the player can make suggestion
                Suggestion suggestion = makeSuggestion(game, destination);
                // now compare the suggestion, and other players try to reject it
                System.out.println(game.refuteSuggestion(suggestion));

                System.out.println("Would you like to make another suggestion? (cost: 2 coins)");
                System.out.println("1. Yes");
                System.out.println("2. No");

                int yesNo = parseInt(1, 2, game);
                if (yesNo == 1) {
                    if (game.extractSalaryPlayer(currentPlayer, 2)) {
                        System.out.println("Realise the new suggestion");
                        // move into a room, now the player can make suggestion
                        suggestion = makeSuggestion(game, destination);
                        // now compare the suggestion, and other players try to reject it
                        System.out.println(game.refuteSuggestion(suggestion));
                    }
                    else {
                        System.out.println("I'm sorry but you don't have enough salary to do it");
                        System.out.println("You can make another suggestion on your next turn");
                    }
                }

                accusationChoice(game);
                remainingSteps = 0;

            } else {
                // move to another tile, remainingSteps--
                remainingSteps--;
            }

        } else if (choice == movablePos.size() + 1) {
            // player chose to make an accusation
            makeAccusation(game);
            remainingSteps = 0;

        } else if (choice == movablePos.size() + 2) {

            if (hasSuggestionOption && !hasNowhereToGo) {
                // player chose to make a suggestion
                Suggestion suggestion = makeSuggestion(game, currentPos);
                // now other players try to reject it
                System.out.println(game.refuteSuggestion(suggestion));

                accusationChoice(game);

                remainingSteps = 0;

            } else if (!hasSuggestionOption && hasNowhereToGo) {
                // has nowhere to go, the player choose to end turn
                remainingSteps = 0;
            }

        } else if (choice == movablePos.size() + 3) {
            // has nowhere to go, the player choose to end turn
            remainingSteps = 0;
        }

        // update the player's remaining steps
        game.setRemainingSteps(currentPlayer, remainingSteps);

        // if current player has no step left, it's next player's turn
        if (remainingSteps == 0) {
            game.currentPlayerEndTurn();
        }
    }

    /**
     * Reviews the data structure where the results of the different dice are stored
     * and calculates the total.
     *
     * @param game --- the running game
     * @return --- the number of moves a player can make in a turn
     */
    private static int NumberOfMovements(Game game) {
        int[] roll = game.rollDice();
        int total = 0;
        for (int j : roll) {
            total += (j + 1);
        }
        System.out.println("You rolled " + total + ".");
        return total;
    }


    /**
     * This procedure picks up if the player wants to make a accusation
     *
     * @param game
     *             --- the running game
     */
    private static void accusationChoice(Game game) {
        // prompt if the player want to make accusation now
        System.out.println("Do you want to make an accusation now?");
        System.out.println("1. Yes");
        System.out.println("2. No");

        int yesNo = parseInt(1, 2, game);
        if (yesNo == 1) {
            // made an accusation
            makeAccusation(game);
        }
    }


    /**
     * This method let the player to make a suggestion.
     * 
     * @param game
     *            --- the running game
     * @param destination
     *            --- where to move to
     * @return --- the suggestion made
     */
    private static Suggestion makeSuggestion(Game game, Position destination) {
        // safe cast
        Location location = ((Room) destination).getRoom();

        System.out.println("What suggestion do you want to make in " + location.toString() + "?");


        // choice a character suspect
        Character suspect = choiceSuspect();

        // choice of the object used
        Weapon weapon = choiceObject();

        Suggestion suggestion = new Suggestion(suspect, weapon, location);
        game.moveTokensInvolvedInSuggestion(suggestion);

        // now the player has made a suggestion
        System.out.println(
                "Your suggestion is:\nSuspect: " + suspect + "\nWeapon: " + weapon + "\nLocation: " + location);

        return suggestion;
    }

    /**
     * This method let the player make an accusation. If the accusation is correct, win;
     * if wrong, the player is out.
     *
     */
    private static void makeAccusation(Game game) {

        System.out.println("What accusation do you want to make:");

        // choice a character suspect
        Character suspect = choiceSuspect();

        // choice of the object used
        Weapon weapon = choiceObject();

        System.out.println("...in:");

        // prompt all rooms
        for(int i = 0; i < Location.getNumberOfLocations(); i++){
            System.out.println("" + (i + 1) + ". " + Location.get(i).toString());
        }

        int choiceRoom = parseInt(1, Location.getNumberOfLocations(), game);

        // get player's choice
        Location location = Location.get(choiceRoom - 1);

        // the player has made an accusation
        System.out.println("Your accusation is:\nSuspect: " + suspect + "\nWeapon: " + weapon.toString() + "\nLocation: " + location.toString());

        Suggestion accusation = new Suggestion(suspect, weapon, location);

        // now we should check whether the accusation is correct
        if (game.checkAccusation(accusation)) {
            // win!!
            System.out.println("You Win!");
        } else {
            // the player is out
            System.out.println("You are wrong!");

            System.out.println("Do you want to continue playing? (cost: 5 coins)");
            System.out.println("1. Yes");
            System.out.println("2. No");

            Character currentPlayer = game.getCurrentPlayer();
            int yesNo = parseInt(1, 2, game);
            if (yesNo == 1) {
                if (game.extractSalaryPlayer(currentPlayer, 5)) {
                    System.out.println("You have finished your turn. You are still playing");
                }
                else {
                    System.out.println("I'm sorry but you don't have enough salary to do it");
                    System.out.println("You are removed from the game");
                    game.kickPlayerOut(currentPlayer);
                }
            }
            else {
                System.out.println("You are removed from the game");
                game.kickPlayerOut(currentPlayer);
            }
        }
    }

    /**
     * This method returns the character chosen as a suspect
     *
     * @return  --- suspect
     */
    private static Character choiceSuspect() {
        // prompt all characters
        for(int i = 0; i < Character.getNumberOfCharacters(); i++) {
            System.out.println("" + (i + 1) + ". " + Character.get(i).toString());
        }

        int choiceCharacter = parseInt(1, Character.getNumberOfCharacters(), null);

        // get player's choice
        Character suspect = Character.get(choiceCharacter - 1);

        System.out.println(suspect.toString() + " commited crime with:");
        return suspect;
    }

    /**
     * This method returns the weapon chosen as a murder weapon
     *
     * @return  --- weapon
     */
    private static Weapon choiceObject() {
        // prompt all weapons
        for(int i = 0; i < Weapon.getNumberOfWeapons(); i++) {
            System.out.println("" + (i + 1) + ". " + Weapon.get(i).toString());
        }

        int choiceWeapon = parseInt(1,  Weapon.getNumberOfWeapons(), null);

        // get player's choice
        return Weapon.get(choiceWeapon - 1);
    }

    /**
     * This method ends the game, prompt the winner give players an option to restart a
     * new game.
     * 
     * @param game
     *            --- the running game
     */
    private static void gameStop(Game game) {
        // TODO set game stop, prompt the winner
        Configs configurations = Configs.getConfiguration();
        Character winner = game.getWinner();
        configurations.getRecords().add(new GameRecord(game.getSolution(), game.getPlayerByCharacter(game.getWinner()).getName(), game.getPlayerByCharacter(game.getCurrentPlayer()).getCards()));
        System.out.println("Winner is " + winner.toString() + " (" + game.getPlayerByCharacter(game.getCurrentPlayer()).getName() + ") !");
        configurations.Serialize();
        System.out.println("Do you want to see the solution? (1 = Yes, 2 = No)");
        int yesNo = parseInt(1, 2, game);
        if (yesNo == 1){
            displaySolution(game.getSolution());
        }
    }

    /**
     * This helper method parse user's input as integer, and limits the maximum and
     * minimum boundary of it.
     * 
     * @param min
     *            --- the minimum boundary of input as an integer
     * @param max
     *            --- the maximum boundary of input as an integer
     * @param game
     *            --- the running game
     * @return --- the parsed integer
     */
    private static int parseInt(int min, int max, Game game) {
        while (true) {
            String line = SCANNER.nextLine();
            // if user asked for help, print out help message
            if (line.equals("help")) {
                helpMessage();
                System.out.println("Please choose between " + min + " and " + max + ":");
            }

            if (line.equals("coins help")) {
                coinsHelpMessage();
                System.out.println("Please choose between " + min + " and " + max + ":");
            }

            if(line.equals("suspicious")){
                suspiciousMessage(game);
                System.out.println("Please choose between " + min + " and " + max + ":");
            }

            if (line.equals("record")){
                showRecord();
                System.out.println("Please choose between " + min + " and " + max + ":");
            }

            else {
                try {
                    // parse the input
                    int i = Integer.parseInt(line);
                    if (i >= min && i <= max) {
                        // a good input
                        return i;
                    } else {
                        // a out of boundary input, let the user retry.
                        System.out.println("Please choose between " + min + " and " + max + ":");
                    }
                }
                catch (NumberFormatException e) {
                    // the input is not an integer
                    System.out.println("Please enter an integer:");
                }
            }
        }
    }

    /**
     * This method print out help message, now it only displays the legend
     */
    private static void helpMessage() {
        StringBuilder message = new StringBuilder("[Legend]\n");

        message.append("Characters are represented as a single upper-case character:\n");

        for (int i = 0; i < Character.getNumberOfCharacters(); i++) {
            Character character = Character.get(i);
            message.append(character.toString()).append(":\t\t").append(character.toStringOnBoard()).append("\n");
        }

        message.append("Weapon are represented as a single lower-case character:\n");

        for (int i = 0; i < Weapon.getNumberOfWeapons(); i++) {
            Weapon weapon = Weapon.get(i);
            message.append(weapon.toString()).append(":\t\t").append(weapon.toStringOnBoard()).append("\n");
        }

        System.out.println(message);
    }

    /**
     * This method print out coins help message, which all possible options for spending coins
     */
    private static void coinsHelpMessage() {
        StringBuilder message = new StringBuilder("[Legend]\n");
        message.append("You can use the coins for the following actions:\n");
        message.append("Repeat roll dice (before starting to move). [1 coin]\n");
        message.append("Make another suggestion in the same turn. [2 coins]\n");
        message.append("Have the opportunity to continue playing by failing an accusation. [5 coins]\n");
        System.out.println(message);
    }

    /**
     * This method print out suspicious message, which every card is a possible solution
     */
    private static void suspiciousMessage(Game game) {
        StringBuilder message = new StringBuilder("[Suspicious cards]\n");

        message.append("Characters who are still suspicious:\n");

        Set<Card> getSuspiciousCards = game.getKnownCards();

        Set<Card> cardsWellSuggested = game.getCardsWellSuggested();
        //Suggestion suggestion=
        getSuspiciousCards.addAll(cardsWellSuggested);

        for (int i = 0; i < Character.getNumberOfCharacters(); i++) {
            Character character = Character.get(i);
            if(!getSuspiciousCards.contains(character)){
                message.append(character.toString()).append("\n");
            }
            if(i== Character.getNumberOfCharacters()-1)
                message.append("\n");
        }

        message.append("Weapons which are still suspicious:\n");

        for (int i = 0; i < Weapon.getNumberOfWeapons(); i++) {
            Weapon weapon = Weapon.get(i);
            if(!getSuspiciousCards.contains(weapon)) {
                message.append(weapon.toString()).append("\n");
            }
            if(i== Weapon.getNumberOfWeapons()-1)
                message.append("\n");
        }

        message.append("Rooms where are still suspicious:\n");

        for (int i = 0; i < Location.getNumberOfLocations(); i++) {
            Location location = Location.get(i);
            if(!getSuspiciousCards.contains(location)) {
                message.append(location.toString()).append("\n");
            }
            if(i== Location.getNumberOfLocations()-1)
                message.append("\n");
        }

        System.out.println(message);
    }

    /**
     * This method print out the results of the available item history.
     */
    private static void showRecord(){
        StringBuilder message = new StringBuilder("[Record]\n");
        message.append("The record of games ended is the following one:\n");
        int i = 0;
        for (GameRecord game : Configs.getConfiguration().getRecords()){
            message.append("Game ").append(i).append(" :\n").append(game.toString()).append("\n\n");
            i++;
        }
        System.out.println(message);
    }

    /**
     * This method print out the solution of the current game.
     */
    public static void displaySolution(Suggestion solution){
        StringBuilder message = new StringBuilder();
        message.append("The solution is: \n").append(solution.toString());
    }
}
