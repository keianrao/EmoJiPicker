
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Backend {

//  Interface   //  \\  //  \\  //  \\  //  \\

public List<Emoji> getAllEmojis() {
    /*
    List<Emoji> returnee = new ArrayList<>();
    returnee.addAll(smileys);
    return returnee;
    */

    return Collections.emptyList();
}

public List<Emoji> getSmileyEmojis() {
    /*
    return new ArrayList<Emoji>(smileys);
    */

    return Collections.emptyList();
}

public void addSmileyEmojis(List<Emoji> emojis) {
    /*
    smileys.addAll(emojis);
    */

    return;
}


//  Structs     //  \\  //  \\  //  \\  //  \\

public static class Emoji {
    String value;
    // Using a dedicated class just in case we need to extend later..
}


//  Private data    \\  //  \\  //  \\  //  \\

private final List<Emoji> smileys;


//  Constructors    \\  //  \\  //  \\  //  \\

Backend() {
    int likelyAverageNumberOfEmojisPerCategory = 64;
    smileys = new ArrayList<>(likelyAverageNumberOfEmojisPerCategory);
}

}
