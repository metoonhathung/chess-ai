package game;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String storeDir = "dat";
    public static final String storeFile = "matches_played.dat";

    public static List<Match> matchesPlayed = new ArrayList<>();
    public static List<Move> currMovesPlayed = null;
    public static int playbackIndex = 0;

    public Database() {
    }

    public static void writeMatchesPlayed(List<Match> matchesPlayed) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
        oos.writeObject(matchesPlayed);
    }

    public static List<Match> readMatchesPlayed() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
        List<Match> matchesPlayed = (List<Match>) ois.readObject();
        return matchesPlayed;
    }
}
