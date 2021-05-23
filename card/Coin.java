package card;

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

    public boolean viableOperation(int discount) {
        return coins - discount >= 0;
    }
}
