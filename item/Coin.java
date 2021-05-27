package item;

/**
 * This class represents the salary in number of coins of one player in Cluedo game.
 *
 * Note that this class is also used to symbolically represent one number.
 *
 * @author G7EAS
 */
public class Coin {
    private int coins;

    public Coin(int numberOfCoins) {
        this.coins = numberOfCoins;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int numberOfCoins) {
        this.coins = numberOfCoins;
    }

    /**
     *Before any operation you wish to carry out, you should first check whether there is sufficient funds available for this method.
     *
     * @param discount --- rate charged for the operation
     * @return --- return if you have sufficient balance to carry out a transaction.
     */
    public boolean viableOperation(int discount) {
        return coins - discount >= 0;
    }
}
