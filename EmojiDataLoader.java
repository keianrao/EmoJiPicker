
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

public static void loadEmojiData(List<String> linesFromTestDataFile,
Backend backend) {
    // We will assemble these, to load into the backend later..
    class EmojiGroup {
        String groupID;
        final List<Backend.Emoji> emojis = new LinkedList<>();
    }
    List<EmojiGroup> groups = new LinkedList<EmojiGroup>();

    // Filter out all the lines we don't want to parse.
    List<String> lines =
         filterForGroupCommentsAndDataLines(linesFromTestDataFile);
    if (lines.size() == 0) return;

    // Okay, let's start building the emoji groups.
    EmojiGroup currentGroup = null;
    for (String line: lines) {
        // Split it like a group comment line, see what happens.
        String[] groupNameFields = line.split("group: ");
        if (groupNameFields.length > 1) {
            // The split worked, so this is a group comment line.

            // If not in our first group, then we finished this one,
            // submit it.
            if (currentGroup != null) {
                groups.add(currentGroup);
                currentGroup = new EmojiGroup();
            }

            // Okay, started new group. Set its ID.
            currentGroup.groupID = groupNameFields[1].trim();
            continue;
        }

        // Otherwise, this line is a data line.
        line = line.replaceAll("#.*$", ""); // Delete EOL comment
        // Okay, now we have 'code points; status'
        String[] dataLineFields = line.split(";");
        assert dataLineFields.length > 1;

        // Ignore emoji sequence if its status is not fully qualified
        String status = dataLineFields[1].trim();
        if (!status.equals("fully-qualified")) {
            continue;
        }

        // Okay, parse the code points and create the emoji.
        // Then insert it in the current group.
        StringBuilder qualifiedSequence = new StringBuilder();
        for (String unparsedCodePoint: dataLineFields[0].split("\\w+")) {
            int parsedCodePoint = parseUnicodeScalar(unparsedCodePoint);
            qualifiedSequence.append(parsedCodePoint);
        }
        Backend.Emoji currentEmoji = new Backend.Emoji();
        currentEmoji.qualifiedSequence = qualifiedSequence.toString();
        assert currentGroup != null;
        // This assertion can fail when the first line is a data line
        // rather than a group name comment (which would've started
        // the first group). How should we handle it?
        currentGroup.add(currentEmoji);
    }

    // Okay, finished all our lines. Our current group is the last one
    // and hasn't been submitted, so submit it now.
    groups.add(currentGroup);


    // Finally, load all the groups into the backend.
    for (EmojiGroup group: groups) {
        backend.addToEmojiGroup(group.groupID, group.emojis);
    }
}



public static List<String> readAllLinesFromFile(File file) throws IOException {
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

    return lines;
}

public static List<String> filterForGroupCommentsAndDataLines(List<String> lines) {
    List<String> filteredLines = new ArrayList<>();

    for (String line: lines) {
        boolean isGroupComment = line.matches("^# *group:.*");
        boolean isDataLine = line.matches("^[^#].*");
        boolean isBlankLine = line.matches("^\\s*$");
        if (isBlankLine) continue;
        if (!isGroupComment && !isDataLine) continue;
        filteredLines.add(line);
    }

    return filteredLines;
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
