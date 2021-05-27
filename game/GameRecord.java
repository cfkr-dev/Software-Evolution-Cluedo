package game;

import item.card.Card;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class GameRecord implements Serializable {

    private final Date dateGame;
    private final Suggestion solution;
    private final String gameWinner;
    private final List<Card> cardsPlayer;

    public GameRecord(Suggestion solution, String gameWinner, List<Card> cardsPlayer) {
        this.dateGame = new Date(System.currentTimeMillis());
        this.solution = solution;
        this.gameWinner = gameWinner;
        this.cardsPlayer = cardsPlayer;
    }

    public String getDateGame() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        return formatter.format(dateGame);
    }

    public Suggestion getSolution() {
        return solution;
    }

    public String getGameWinner() {
        return gameWinner;
    }

    public List<Card> getCardsPlayer() {
        return cardsPlayer;
    }

    @Override
    public String toString() {
        return "Date : " + getDateGame() +
                ", \nSolution : " + solution.toString() +
                ", \nGameWinner : '" + gameWinner + '\'' +
                ", \nCardsPlayer : " + cardsPlayer.toString();
    }
}
