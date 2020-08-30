
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class EmojiDataLoader {

//  Interface   //  \\  //  \\  //  \\  //  \\

public void loadEmojiData(Backend backend) throws FileNotFoundException, IOException {
    if (!testDataFile.canRead()) {
        throw new FileNotFoundException(
            "Could not open emoji 'test data' file - " +
            "does it exist, and is it readable..?"
        );
        // Don't print the file name or path, err on privacy's side
    }
    loadEmojiData(
        readAllLinesFromFile(testDataFile),
        backend
    );
}

public void setTestDataFile(String filepath) {
    testDataFile = new File(filepath);
}



//  Private data    \\  //  \\  //  \\  //  \\

private File testDataFile = new File("data/emoji-test.txt");




//  Helper functions    //  \\  //  \\  //  \\

public static void loadEmojiData(String[] linesFromTestDataFile,
Backend backend) {
    String[] lines = linesFromTestDataFile;

    class EmojiGroup {
        String groupID;
        final List<Backend.Emoji> emojis = new LinkedList<>();
    }
    List<EmojiGroup> groups = new LinkedList<EmojiGroup>();

    EmojiGroup currentGroup = new EmojiGroup();
    Backend.Emoji currentEmoji = new Backend.Emoji();
    int currentOffset = 0;

    // Seek to the first '# group:' line.
    while (!lines[currentOffset].matches("^# *group:"))
        currentOffset++;

    // Now step back..
    currentOffset--;
    /*
    * I'm actually not sure how to parse the file in a stream-like manner.
    * There's a big header section which is what we were seeking past,
    * but they're comments exactly like the comments *within* the data that
    * we actually want to look at. So we can't just 'filter all comments'.
    * Right now, my approach is to do this, iterate through the
    * beginning lines and stopping once it seems like the data has started.
    */


    // Okay, now we're in the massive data section. Enter massive loop.
    while (true) {
        String currentLine = lines[++currentOffset];

        if (currentLine == null || currentLine.matches("^# *EOF")) {
            break;
        }

        // If group name comment, then finish previous group,
        // start a new group, then set its ID to what we see.

        // If any other comment, ignore.

        // Otherwise, it's data. Parse according to the format.
        // Convert the code points to int using parseUnicodeScalar.
        // Then convert each int to char[] to append to a StringBuilder,
        // which once it's done sets currentEmoji.qualifiedSequence.
    }


    // We finished the data section.
    // Add in our current group, the last one.
    groups.add(currentGroup);

    // Now load it into backend, using #addToEmojiGroup.
    for (EmojiGroup group: groups) {
        backend.addToEmojiGroup(group.groupID, group.emojis);
    }

    // And our job is done.
    return;
}


public static String[] readAllLinesFromFile(File file) throws IOException {
    /*
    * A better and modern way would be to give Stream<String>.
    * There is even BufferedReader#lines for this.
    * But we'll stick to pre-1.8 methods for now.
    */

    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
    }

    return lines.toArray(new String[0]);
}



public static int parseUnicodeScalar(String string) {
    assert string.matches("^[0-9a-fA-F]+$");
    // We can use \p{XDigit} but this is clearer
    // If this is failing for you, check that you didn't input O instead of 0

    try {
        return Integer.parseInt(string, 16);
    }
    catch (NumberFormatException eNf) {
        assert false;
        return 0;
    }
}

}
