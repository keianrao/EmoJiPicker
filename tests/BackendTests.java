
import java.util.List;
import java.util.ArrayList;


public class BackendTests {

public static void testAll() {
    testInterfaceNeverGivesNull();
    testAddingSmileys();
}

public static void testInterfaceNeverGivesNull() {
    Backend backendInstance = new Backend();
    assert backendInstance.getSmileyEmojis() != null;
    assert backendInstance.getAllEmojis() != null;
}

public static void testAddingSmileys() {
    Backend.Emoji newSmiley1, newSmiley2;
    newSmiley1 = new Backend.Emoji();
    newSmiley1.value = "this is not an emoji";
    newSmiley2 = new Backend.Emoji();
    newSmiley2.value = "👌";
    List<Backend.Emoji> newSmileys = new ArrayList<Backend.Emoji>();
    newSmileys.add(newSmiley1);
    newSmileys.add(newSmiley2);

    Backend backendInstance = new Backend();
    backendInstance.addSmileyEmojis(newSmileys);
    List<Backend.Emoji> savedSmileys = backendInstance.getSmileyEmojis();
    assert savedSmileys.size() == 0;
    // Right now we expect addition to fail
}

//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String[] args) {
    testAll();
}

}
