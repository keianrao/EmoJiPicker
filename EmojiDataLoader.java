/*
* This file is part of EmoJiPicker.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
    final List<Backend.Emoji> emojis = new LinkedList<>();
}

public static class SerialisedEmojiGroup {
    String groupNameLine;
    final List<String> dataLines = new LinkedList<>();
}



//  Private data    \\  //  \\  //  \\  //  \\

private File testDataFile = new File("data/emoji-test.txt");




//  Helper functions    //  \\  //  \\  //  \\

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
    SerialisedEmojiGroup currentGroup = new SerialisedEmojiGroup();
    currentGroup.groupNameLine = "# group: Ungrouped";
    // The code in this module relies heavy on a certain syntax,
    // so it's okay for us to write our own line like this, probably.
    for (String line: lines) {
        if (isGroupNameLine(line)) {
            groups.add(currentGroup);
            currentGroup = new SerialisedEmojiGroup();
            currentGroup.groupNameLine = line;
        }
        else if (isDataLine(line)) {
            currentGroup.dataLines.add(line);
        }
        // Lines of any other type, ignore them.
        else continue;
    }
    groups.add(currentGroup);
    return groups;
}

public static boolean isGroupNameLine(String line) {
    return line.matches("#\\s*group:.*");
}

public static boolean isDataLine(String line) {
    return line.matches("[^#].+;.+");
}



public static List<EmojiGroup> deserialiseEmojiGroups(List<SerialisedEmojiGroup> serialisedEmojiGroups) {
    List<EmojiGroup> deserialisedGroups = new LinkedList<>();

    for (SerialisedEmojiGroup serialisedGroup: serialisedEmojiGroups) {
        EmojiGroup deserialisedGroup = new EmojiGroup();

        // Parse the group name line.
        String[] groupNameLineFields =
            serialisedGroup.groupNameLine.split(":", 2);
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
            StringBuilder qualifiedSequenceBuilder = new StringBuilder();
            for (String serialisedCodePoint: serialisedCodePoints) {
                int deserialisedCodePoint =
                    parseUnicodeScalar(serialisedCodePoint);
                qualifiedSequenceBuilder
                    .append(Character.toChars(deserialisedCodePoint));
            }

            Backend.Emoji emoji = new Backend.Emoji();
            emoji.qualifiedSequence = qualifiedSequenceBuilder.toString();
            deserialisedGroup.emojis.add(emoji);
        }

        deserialisedGroups.add(deserialisedGroup);
    }

    return deserialisedGroups;
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
