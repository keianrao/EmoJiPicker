
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

public class Backend {

//  Interface   //  \\  //  \\  //  \\  //  \\

public List<Emoji> getAllEmojis() {
    List<Emoji> returnee = new ArrayList<>();
    for (List<Emoji> emojiGroup: emojiGroups.values()) {
        returnee.addAll(emojiGroup);
    }
    return returnee;
}

public List<Emoji> getEmojiGroup(String groupID) {
    assert groupID != null;

    List<Emoji> group = emojiGroups.get(groupID);
    if (group == null) return Collections.emptyList();
    else return new ArrayList<>(group);
}

public void addToEmojiGroup(String groupID, List<Emoji> emojis) {
    assert groupID != null;
    assert emojis != null;
    if (emojis.size() == 0) return;

    List<Emoji> group = emojiGroups.get(groupID);
    if (group == null) {
        group = new ArrayList<>();
        emojiGroups.put(groupID, group);
    }
    group.addAll(emojis);
}

public List<String> getEmojiGroupIDs() {
    return new ArrayList<>(emojiGroups.keySet());
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
