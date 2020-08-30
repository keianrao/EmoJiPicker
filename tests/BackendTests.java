
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class BackendTests {

public static void testAll() {
    testInterfaceNeverGivesNull();
    testAddingToEmojiGroup();
}

public static void testInterfaceNeverGivesNull() {
    Backend backendInstance = new Backend();

    assert backendInstance.getEmojiGroup("claypotのputera") != null;
    assert backendInstance.getEmojiGroup("11011010aaabb") != null;
    assert backendInstance.getAllEmojis() != null;

    Backend.Emoji e = new Backend.Emoji();
    e.qualifiedSequence = "🔵";
    backendInstance.addToEmojiGroup("test", Collections.singletonList(e));
    assert backendInstance.getEmojiGroup("test") != null;
    assert backendInstance.getAllEmojis() != null;
}

public static void testAddingToEmojiGroup() {
    Backend.Emoji newEmoji1, newEmoji2;
    newEmoji1 = new Backend.Emoji();
    newEmoji2 = new Backend.Emoji();
    newEmoji1.qualifiedSequence = "🎇";
    newEmoji2.qualifiedSequence = "👌";
    List<Backend.Emoji> newEmojis = new ArrayList<Backend.Emoji>();
    newEmojis.add(newEmoji1);
    newEmojis.add(newEmoji2);

    Backend backendInstance = new Backend();
    backendInstance.addToEmojiGroup("test", newEmojis);
    List<Backend.Emoji> savedSmileys = backendInstance.getEmojiGroup("test");
    assert savedSmileys.size() == 0;
    // Right now we expect addition to fail
}

//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String[] args) {
    testAll();
}

}
