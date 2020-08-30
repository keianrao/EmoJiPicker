
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

    List<String> original = linesFromFileAsList(testDataFile);
    List<SerialisedEmojiGroup> assembled = 
        assembleSerialisedEmojiGroups(original);
    List<EmojiGroup> deserialised = deserialiseEmojiGroups(assembled);

    for (EmojiGroup emojiGroup: deserialised) {
        backend.addToEmojiGroup(emojiGroup.groupID, emojiGroup.emojis);
    }
}

public void setTestDataFile(String filepath) {
    testDataFile = new File(filepath);
}



//  Structs \\  //  \\  //  \\  //  \\  //  \\

public static class EmojiGroup {
    String groupID;
    List<Backend.Emoji> emojis;
}

public static class SerialisedEmojiGroup {
    String groupNameLine;
    List<String> dataLines;
}



//  Private data    \\  //  \\  //  \\  //  \\

private File testDataFile = new File("data/emoji-test.txt");




//  Helper functions    //  \\  //  \\  //  \\

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

public static List<String> linesFromFileAsList(File file) throws IOException {
    /*
    * Since 1.8 there is the Streams API, which is recommended for
    * this kind of purpose since it lazily loads.
    * But anyways I am sticking to pre-1.8 methods.
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

public static List<SerialisedEmojiGroup> assembleSerialisedEmojiGroups(List<String> lines) {
    // We'll go through the lines sequentially. We'll assume that
    // *below* each group name line are data lines belonging to the group.
    // We are fine with lines we don't recognise, we will just ignore them.
    List<SerialisedEmojiGroup> groups = new LinkedList<>();
    SerialisedEmojiGroup currentGroup = null;
    for (String line: lines) {
        if (isGroupNameLine(line)) {
            if (currentGroup != null) {
                groups.add(currentGroup);
            }
            currentGroup = new SerialisedEmojiGroup();
            currentGroup.groupNameLine = line;
        }
        else if (isDataLine(line)) {
            if (currentGroup == null) {
                // There's no group name line preceeding this data line,
                // that would've started a group we can enter this line into.
                // What should we do?
                continue;
            }
            currentGroup.dataLines.add(line);
        }
        // Lines of any other type, ignore them.
        else continue;
    }
    return groups;
}

public static List<EmojiGroup> deserialiseEmojiGroups(List<SerialisedEmojiGroup> serialisedEmojiGroups) {
    List<EmojiGroup> deserialisedGroups = new LinkedList<>();

    for (SerialisedEmojiGroup serialisedGroup: serialisedEmojiGroups) {
        EmojiGroup deserialisedGroup = new EmojiGroup();
        
        // Parse the group name line.
        String[] groupNameLineFields = 
            serialisedGroup.groupNameLine.split(":", 1);
        assert groupNameLineFields.length == 2;
        deserialisedGroup.groupID = groupNameLineFields[1].trim();
 
        // Parse each data line.
        for (String dataLine: serialisedGroup.dataLines) {
            String[] dataLineFields = dataLine
                .replaceAll("#[^#]*$", "") // Remove EOL comment
                .split(";");
            assert dataLineFields.length == 2;

            String status = dataLineFields[1].trim();
            if (!status.equals("fully-qualified")) {
                // We ignore emoji sequences that aren't fully qualified.
                continue;
            }

            String[] serialisedCodePoints = dataLineFields[0].split("\\s+");
            StringBuilder qualifiedSequenceSb = new StringBuilder();
            for (String serialisedCodePoint: serialisedCodePoints) {
                int deserialisedCodePoint = 
                    parseUnicodeScalar(serialisedCodePoint);
                qualifiedSequenceSb
                    .append(Character.toChars(deserialisedCodePoint));
            }

            Backend.Emoji emoji = new Backend.Emoji();
            emoji.qualifiedSequence = qualifiedSequenceSb.toString();
            deserialisedGroup.emojis.add(emoji);
        }
    }

    return deserialisedGroups;
}

public static boolean isGroupNameLine(String line) {
        return line.matches("#\\s*group:.*");
}

public static boolean isDataLine(String line) {
        return line.matches("[^#].+;.+");
}


}
