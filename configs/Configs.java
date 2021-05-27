package configs;

import java.util.ArrayList;

import card.Location;
import tile.Room;
import game.GameRecord;
import java.io.*;

/**
 * This class contains most of configurations to construct a game board.
 *
 * @author G7EAS
 *
 */
public class Configs {
    /**
     * The number of dices used in game
     */
    private final int NUM_DICE = 2;

    /**
     * Minimum player needed
     */
    private final int MIN_PLAYER = 3;

    /**
     * Maximum player to join into game.
     */
    private final int MAX_PLAYER = 6;

    /**
     * the horizontal boundary coordinate of Cluedo game board.
     */
    private int BOARD_WIDTH = 48;

    /**
     * the vertical boundary coordinate of Cluedo game board.
     */
    private int BOARD_HEIGHT = 25;

    /**
     * The  object of all locations
     */
    private ArrayList<Room> ROOMS = new ArrayList<>();

    private ArrayList<GameRecord> gameRecords;

    {
        try {
            gameRecords = Deserialize();
        } catch (IOException | ClassNotFoundException ignored) {

        }
    }

    private static Configs configurations;

    public void Serialize() {
        try {
            FileOutputStream file = new FileOutputStream("record.txt");
            ObjectOutputStream oos = new ObjectOutputStream(file);
            oos.writeObject(gameRecords);
            oos.close();
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<GameRecord> Deserialize() throws IOException, ClassNotFoundException {
        try {
            FileInputStream file = new FileInputStream("record.txt");
            ObjectInputStream ois = new ObjectInputStream(file);
            ArrayList<GameRecord> gameRecords = (ArrayList<GameRecord>) ois.readObject();
            ois.close();
            file.close();
            return gameRecords;
        }
        catch (IOException e){
            return new ArrayList<>();
        }
    }

    public ArrayList<GameRecord> getRecords(){
        return gameRecords;
    }

    /**
     *Dynamically sets the width and height values of the map on which the game is to be played.
     */
    public void DimensionCounter(){
        int character= 0;
        int height= 0;
        int width;
        for(int i= 0; i< BOARD_STRING_B.length(); i++){
            character++;
            if(BOARD_STRING_B.charAt(i) == '\n'){
                height++;
            }
        }

        width= (character/height) - 1 ;

        BOARD_HEIGHT= height;
        BOARD_WIDTH= width;
    }


    private Configs(){
        for (int i = 0; i < Location.getNumberOfLocations(); i++) {
            Room room = new Room(Location.get(i),null);
            ROOMS.add(room);
        }
        assignShortcuts(ROOMS);
        DimensionCounter();
    }

    /**
     * Method that sets the Singleton pattern of this java class.
     * If an instance of the class is already created, it returns the same one, otherwise it is created.
     *
     * @return --- instance of the Configs class
     */
    public static Configs getConfiguration() {
        if (configurations == null) {
            configurations = new Configs();
        }
        
        return configurations;
    }

    /**
     * This method take into the all Room objects and assign the necessary shortcuts
     *
     * @param rooms
     *          --- Array of rooms
     */
    private void assignShortcuts(ArrayList<Room> rooms) {
        rooms.get(0).setSecPasTo(Location.get(5));
        rooms.get(5).setSecPasTo(Location.get(0));
        rooms.get(2).setSecPasTo(Location.get(7));
        rooms.get(7).setSecPasTo(Location.get(2));
    }


    /**
     * This method take into the symbolic location (integer), and returns the
     * corresponding Room object.
     *
     * @param symbolLocation
     *            --- symbolic location (integer)
     * @return --- the corresponding,  Room object
     */
    public  Room getRoom(int symbolLocation) {
        return ROOMS.get(symbolLocation);
    }

    /**
     *  a string used to print out text-based UI. This is used as a canvas.
     */
    private  final String UI_STRING_A =
            "■■■■■■■■■ ■■■■ ■■■■■■■■■\n" +
            "┌KIT─□■   ┌──┐   ■┌CSTY┐\n" +
            "│    │  B─L  R─M  │    │\n" +
            "│    │  │      │  │    │\n" +
            "│    │  │      │  ↑│  ┌┘\n" +
            "└┐   │  →      ←   └──□■\n" +
            "■└──↑┘  │      │        \n" +
            "        └↑────↑┘       ■\n" +
            "■                 ┌────┐\n" +
            "┌───┐             →    B\n" +
            "D   └──┐  ┌───┐   │    L\n" +
            "I      │  │B  │   │    D\n" +
            "N      ←  │A M│   └───↑┘\n" +
            "I      │  │S E│        ■\n" +
            "N      │  │E N│   ┌─↓─┐■\n" +
            "└─────↑┘  │  T│  ┌┘   └┐\n" +
            "■         └───┘  →     │\n" +
            "                 └┐   ┌┘\n" +
            "■        ┌─↓↓─┐   └LIB┘■\n" +
            "□─────↓  │    │         \n" +
            "│     │  │    ←        ■\n" +
            "L     │  │    │  ↓─────□\n" +
            "O     │  │    │  │     │\n" +
            "U    ┌┘  │    │  └┐    │\n" +
            "N─G─E┘■ ■└HALL┘■ ■└STUDY\n";

    /**
     *  an alternative string used to print out text-based UI. It uses two characters to
     *  represent one tile position, so that the ASCII board is not so skinny.<br>
     *  <br>
     *  NOTE: it's not used currently.
     */
    private  final String UI_STRING_B =
            "■■■■■■■■■■■■■■■■■■  ■■■■■■■■  ■■■■■■■■■■■■■■■■■■\n" +
            "┌──────────□■■      ┌──────┐      ■■┌──────────┐\n" +
            "│ KITCHEN  │    ┌───┘      └───┐    │          │\n" +
            "│          │    │    B A L L   │    │  CONSERVA│\n" +
            "│          │    │    R O O M   │    └↑┐TORY  ┌─┘\n" +
            "└─┐        │    →              ←      └──────□■■\n" +
            "■■└─────↑ ─┘    │              │                \n" +
            "                └─↑ ────────↑ ─┘              ■■\n" +
            "■■                                  ┌──────────┐\n" +
            "┌────────┐                          →          │\n" +
            "│        └─────┐    ┌────────┐      │          │\n" +
            "│              │    │        │      │ BILLARD  │\n" +
            "│ D I N I N G  ←    │        │      └─────── ↑─┘\n" +
            "│ R O O M      │    │ CRIME  │                ■■\n" +
            "│              │    │ SCENE  │      ┌───↓ ───┐■■\n" +
            "└───────────↑ ─┘    │        │    ┌─┘        └─┐\n" +
            "■■                  └────────┘    →            │\n" +
            "                                  └─┐ LIBRARY┌─┘\n" +
            "■■                ┌─── ↓↓ ───┐      └────────┘■■\n" +
            "□───────────↓┐    │          │                  \n" +
            "│            │    │          ←                ■■\n" +
            "│ L O U N G E│    │          │    ┌↓───────────□\n" +
            "│            │    │ H A L L  │    │            │\n" +
            "│          ┌─┘    │          │    └─┐ S T U D Y│\n" +
            "└──────────┘■■  ■■└──────────┘■■  ■■└──────────┘\n";

    /**
     * a string used to construct the board.<br>
     * <br>
     * '0' : walkable tiles, tiles that are out of all rooms.<br>
     * 'x' : (space) represents walls and unenterable tiles.<br>
     * 1-9 : represents nine rooms on board.<br>
     * ';' : represents Scarlet's start position.<br>
     * '<' : represents Mustard's start position.<br>
     * '=' : represents White's start position.<br>
     * '>' : represents Green's start position.<br>
     * '?' : represents Peacock's start position.<br>
     * '@' : represents Plum's start position.<br>
     * a-i : represents entrance to each room, 'a' is entrance to room '1',
     * 'b' to room '2', and so on.<br>
     */
    private  final String BOARD_STRING_A =
            "         =    >         \n" +
            "       000    000       \n" +
            " 1111 00   22   00 3333 \n" +
            " 1111 00 222222 00 3333 \n" +
            " 1111 00 222222 00 3333 \n" +
            "  111 0b 222222 b0c     \n" +
            "      00 222222 0000000?\n" +
            "0000a000        0000000 \n" +
            " 00000000b0000b000      \n" +
            "     000000000000d 4444 \n" +
            " 999    00     000 4444 \n" +
            " 999999 00     000 4444 \n" +
            " 999999 i0     000      \n" +
            " 999999 00     00000e0d \n" +
            " 999999 00     000      \n" +
            "        00     00  555  \n" +
            " 00000i000     0e 55555 \n" +
            "0<000000000gg0000 5555  \n" +
            " 00000h00      000      \n" +
            "       00 7777 00000000@\n" +
            " 88888 00 77777g0f00000 \n" +
            " 88888 00 7777 00       \n" +
            " 88888 00 7777 00 66666 \n" +
            " 8888  00 7777 00  6666 \n" +
            "       ;        0   666 \n";

    private  final String BOARD_STRING_B =
            "                  =          >                  \n" +
            "              000000        000000              \n" +
            "  11111111  0000      2222      0000  33333333  \n" +
            "  11111111  0000  222222222222  0000  33333333  \n" +
            "  11111111  0000  222222222222  0000  33333333  \n" +
            "    111111  0000b 222222222222  b000c 333333    \n" +
            "            0000  222222222222  000000000000000?\n" +
            "000000000a000000                00000000000000  \n" +
            "  0000000000000000b0000000000b000000            \n" +
            "          0000000000000000000000000d  44444444  \n" +
            "  999999        0000          000000  44444444  \n" +
            "  999999999999  0000          000000  44444444  \n" +
            "  999999999999  i000          000000            \n" +
            "  999999999999  0000          0000000000e0000d  \n" +
            "  999999999999  0000          000000            \n" +
            "                0000          0000    555555    \n" +
            "  00000000000i000000          000e  5555555555  \n" +
            "0<000000000000000000000gg000000000  55555555    \n" +
            "  0000000000h00000            000000            \n" +
            "              0000  77777777  00000000000000000@\n" +
            "  8888888888  0000  7777777777g0000f0000000000  \n" +
            "  8888888888  0000  77777777  0000              \n" +
            "  8888888888  0000  77777777  0000  6666666666  \n" +
            "  88888888    0000  77777777  0000    66666666  \n" +
            "              ;                  0      666666  \n";


    public  int getNumDice() {
        return NUM_DICE;
    }

    public  int getMinPlayer() {
        return MIN_PLAYER;
    }

    public  int getMaxPlayer() {
        return MAX_PLAYER;
    }

    public  int getBoardWidth() {
        return BOARD_WIDTH;
    }

    public  int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    public  String getUiStringB() {
        return UI_STRING_B;
    }

    public  String getBoardStringB() {
        return BOARD_STRING_B;
    }

    public ArrayList<GameRecord> getGameRecords() {
        return gameRecords;
    }

}
