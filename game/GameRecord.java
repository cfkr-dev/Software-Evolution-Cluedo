package game;

import card.Card;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class GameRecord implements Serializable {

    private Date dateGame;
    private Suggestion solution;
    private String gameWinner;
    private List<Card> cardsPlayer;

    public GameRecord(Suggestion solution, String gameWinner, List<Card> cardsPlayer) {

        this.dateGame = new Date(System.currentTimeMillis());;
        this.solution = solution;
        this.gameWinner = gameWinner;
        this.cardsPlayer = cardsPlayer;
    }

    public String getDateGame() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        return formatter.format(dateGame);
    }

    public void setDateGame(Date dateGame) {
        this.dateGame = dateGame;
    }

    public Suggestion getSolution() {
        return solution;
    }

    public void setSolution(Suggestion solution) {
        this.solution = solution;
    }

    public String getGameWinner() {
        return gameWinner;
    }

    public void setGameWinner(String gameWinner) {
        this.gameWinner = gameWinner;
    }

    public List<Card> getCardsPlayer() {
        return cardsPlayer;
    }

    public void setCardsPlayer(List<Card> cardsPlayer) {
        this.cardsPlayer = cardsPlayer;
    }

    @Override
    public String toString() {
        return "Date : " + getDateGame() +
                ", \nSolution : " + solution.toString() +
                ", \nGameWinner : '" + gameWinner + '\'' +
                ", \nCardsPlayer : " + cardsPlayer.toString();
    }
}
