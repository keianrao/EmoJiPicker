
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

public class Backend {

//  Interface   //  \\  //  \\  //  \\  //  \\

public List<Emoji> getAllEmojis() {
    /*
    List<Emoji> returnee = new ArrayList<>();
    for (List<Emoji> emojiGroup: emojiGroups.values()) {
        returnee.addAll(emojiGroup);
    }
    return returnee;
    */

    return Collections.emptyList();
}

public List<Emoji> getEmojiGroup(String groupID) {
    /*
    return new ArrayList<Emoji>(smileys);
    */

    return Collections.emptyList();
}

public void addToEmojiGroup(String groupID, List<Emoji> emojis) {
    /*
    smileys.addAll(emojis);
    */

    return;
}



//  Structs     //  \\  //  \\  //  \\  //  \\

public static class Emoji {
    String qualifiedSequence;
    /*
    * Obtuse name to be clear what should be inside.. This is the whole emoji,
    * you can embed it inside strings or use it as a label, etc.
    */

    // Using a dedicated class just in case we need to extend later..
}



//  Private data    \\  //  \\  //  \\  //  \\

private final Map<String, List<Emoji>> emojiGroups;



//  Constructors    \\  //  \\  //  \\  //  \\

Backend() {
    emojiGroups = new LinkedHashMap<>();
}

}
