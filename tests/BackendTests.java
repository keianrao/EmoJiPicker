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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class BackendTests {

public static void testAll() {
    testInterfaceNeverGivesNull();
    testAdditionInterface();
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

public static void testAdditionInterface() {
    Backend backendInstance = new Backend();
    List<Backend.Emoji> originalAllEmojis = backendInstance.getAllEmojis();

    Backend.Emoji newEmoji1, newEmoji2, newEmoji3;
    newEmoji1 = new Backend.Emoji();
    newEmoji2 = new Backend.Emoji();
    newEmoji1.qualifiedSequence = "🎇";
    newEmoji2.qualifiedSequence = "👌";
    List<Backend.Emoji> newEmojis = new ArrayList<Backend.Emoji>();
    newEmojis.add(newEmoji1);
    newEmojis.add(newEmoji2);
    backendInstance.addToEmojiGroup("Test", newEmojis);
    List<Backend.Emoji> savedGroup = backendInstance.getEmojiGroup("Test");
    assert savedGroup.size() == 2;

    newEmoji3 = new Backend.Emoji();
    newEmoji3.qualifiedSequence = "🗻 ";
    newEmojis.clear();
    newEmojis.add(newEmoji3);
    savedGroup = backendInstance.getEmojiGroup("Test");
    assert savedGroup.size() == 2;
    backendInstance.addToEmojiGroup("Test", newEmojis);
    savedGroup = backendInstance.getEmojiGroup("Test");
    assert savedGroup.size() == 3;

    List<Backend.Emoji> newAllEmojis = backendInstance.getAllEmojis();
    assert newAllEmojis.size() == originalAllEmojis.size() + 3;
}

//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String[] args) {
    testAll();
}

}
