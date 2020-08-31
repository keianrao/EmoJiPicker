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
import java.util.LinkedList;

public class EmojiDataLoaderTests {

public static void testAll() {
    testIsGroupNameLine();
    testIsDataLine();
    testIsIgnoredLine();
    testAssembleSerialisedEmojiGroups();
    testParseUnicodeScalar();
    testDeserialiseEmojiGroups();
}

public static void testIsGroupNameLine() {
    assert EmojiDataLoader.isGroupNameLine("# group: Test I");
    assert EmojiDataLoader.isGroupNameLine("# group:Test II");
    assert EmojiDataLoader.isGroupNameLine("#group: Test III");
}

public static void testIsDataLine() {
    assert EmojiDataLoader.isDataLine("1F448 ; fully-qualified");
    assert EmojiDataLoader.isDataLine("1F448 2708; ");
    assert !EmojiDataLoader.isDataLine("1F448 2708;");
    assert EmojiDataLoader.isDataLine("1F92A ; minimally-qualified");
}

public static void testIsIgnoredLine() {
    for (String ignoredLine: new String[] {
        "# For documentation and usage",
        "# subgroup: face-smiling",
        "#randomcomment",
        "#   • The file is in CLDR order, not codepoint order.",
        "# group",
        "",
        "  "
    }) {
        assert !EmojiDataLoader.isGroupNameLine(ignoredLine);
        assert !EmojiDataLoader.isDataLine(ignoredLine);
    }
}

public static void testAssembleSerialisedEmojiGroups() {
    List<String> quiz1, quiz2;
    List<EmojiDataLoader.SerialisedEmojiGroup> response1a, response1b;
    List<EmojiDataLoader.SerialisedEmojiGroup> response2a, response2b;

    // Conventional behaviour quiz
    quiz1 = new LinkedList<>();
    quiz1.add("# group: Non-emojis");
    quiz1.add("1F703 2324; ");
    quiz1.add("FFFF 0000 ; ");
    response1a = EmojiDataLoader.assembleSerialisedEmojiGroups(quiz1);
    assert response1a != null && response1a.size() == 2;
    // In this test we are assuming "first group is 'Ungrouped' emojis"
    // as expected behaviour. Do we really want that? Should we demand
    // the function leave it out if it's empty, and put it at the end
    // if not?
    assert response1a.get(1).groupNameLine != null;
    assert response1a.get(1).groupNameLine.equals(quiz1.get(0));
    assert response1a.get(1).dataLines.contains(quiz1.get(1));
    assert response1a.get(1).dataLines.contains(quiz1.get(2));
    quiz1.add("# group: Marine animals ");
    quiz1.add("1F433                   ; fully-qualified");
    quiz1.add("1F40B                   ; fully-qualified");
    quiz1.add("1F42C                   ; fully-qualified");
    quiz1.add("1F9AD                   ; fully-qualified");
    quiz1.add("1F41F                   ; fully-qualified");
    response1b = EmojiDataLoader.assembleSerialisedEmojiGroups(quiz1);
    assert response1b.size() == 3;
    assert response1b.get(2).groupNameLine != null;
    assert response1b.get(2).groupNameLine.equals("# group: Marine animals ");
    assert response1b.get(2).dataLines.get(3)
        .equals("1F9AD                   ; fully-qualified");

    // Funny behaviour quiz
    quiz2 = new LinkedList<>();
    quiz2.add("this is not a data line");
    quiz2.add("this is a data line (though unparsable later) ; ");
    quiz2.add("# Also note that we start with no group.");
    response2a = EmojiDataLoader.assembleSerialisedEmojiGroups(quiz2);
    assert response2a.size() == 1;
    assert response2a.get(0).dataLines.size() == 1;
    quiz2.add("# group:fruit??");
    quiz2.add("1F951 ; fully-qualified # E3.0 avocado");
    quiz2.add("1F346 ; fully-qualified # E0.6 eggplant");
    quiz2.add("# group tuber");
    quiz2.add("1F954 ; fully-qualified # E3.0 potato");
    response2b = EmojiDataLoader.assembleSerialisedEmojiGroups(quiz2);
    assert response2b.size() == 2;
    assert response2b.get(1).groupNameLine.equals(quiz2.get(3));
    assert response2b.get(1).dataLines.get(0).equals(quiz2.get(4));

    // Things we did not test:
    // - Resistance to null bombs
    // - Serious checks on whether the function returns null anywhere
    // - Duplicated input
}

public static void testParseUnicodeScalar() {
    // Some examples from emoji-test..
    // Correct answers are copied from GNU bc
    assert EmojiDataLoader.parseUnicodeScalar("1F600") == 128512;
    assert EmojiDataLoader.parseUnicodeScalar("263A") == 9786;
    assert EmojiDataLoader.parseUnicodeScalar("FE0F") == 65039;

    // Sanity checks..
    assert EmojiDataLoader.parseUnicodeScalar("0") == 0;
    assert EmojiDataLoader.parseUnicodeScalar("00") == 0;

    // We won't ask parseUnicodeScalar to reject invalid Unicode scalars.
    // We'll just test that it understands this syntax we're reading.
}

public static void testDeserialiseEmojiGroups() {
    List<String> input1, input2;
    List<EmojiDataLoader.SerialisedEmojiGroup> srGrp1, srGrp2;
    List<EmojiDataLoader.EmojiGroup> response1, response2;

    // Conventional test.
    input1 = new LinkedList<>();
    input1.add("# group: Food & Drink");
    input1.add("");
    input1.add("# subgroup: food-fruit");
    input1.add("1F347; fully-qualified     # E0.6 grapes");
    input1.add("1F348; fully-qualified # E0.6 melon");
    input1.add("1F349 ; fully-qualified     # E0.6 watermelon");
    input1.add("1F34A ; fully-qualified # E0.6 tangerine");
    input1.add("# Food & Drink subtotal:		131	w/o modifiers");
    input1.add("# group: Travel & Places    ");
    input1.add("# subgroup: place-map");
    input1.add("1F30D; fully-qualified");
    input1.add("1F30E; fully-qualified");
    srGrp1 = EmojiDataLoader.assembleSerialisedEmojiGroups(input1);
    assert srGrp1.size() == 3;
    assert srGrp1.get(1).dataLines.size() == 4;
    assert srGrp1.get(2).dataLines.size() == 2;
    response1 = EmojiDataLoader.deserialiseEmojiGroups(srGrp1);
    assert response1 != null && response1.size() == 3;
    assert response1.get(0).groupID.equals("Ungrouped");
    assert response1.get(0).emojis.size() == 0;
    assert response1.get(1).groupID.equals("Food & Drink");
    assert response1.get(1).emojis.size() == 4;
    assert response1.get(2).groupID.equals("Travel & Places");
    assert response1.get(2).emojis.size() == 2;
    // Since we got those lines from the test data file,
    // should we check if the emojis are equal?

    // Unusual test.
    input2 = new LinkedList<>();
    input2.add("1F954 ; fully-qualified # E3.0 potato");
    input2.add("#group:ApparentlyAValidGroup");
    input2.add("# group: ");
    // Not null, but a blank name. Should we really accept this actually?
    input2.add("1F955 1F955 1F955 ; ");
    // A sequence, but not an emoji by our picker's standards..
    // But anyway, no status
    input2.add("1F30D; minimally-qualified");
    srGrp2 = EmojiDataLoader.assembleSerialisedEmojiGroups(input2);
    assert srGrp2.size() == 3;
    response2 = EmojiDataLoader.deserialiseEmojiGroups(srGrp2);
    assert response2.size() == 3;
    assert response2.get(0).emojis.size() == 1;
    assert response2.get(1).groupID.equals("ApparentlyAValidGroup");
    assert response2.get(1).emojis.size() == 0;
    assert response2.get(2).groupID.equals("");
    assert response2.get(2).emojis.size() == 0;
}



//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String[] args) {
    testAll();
}

}
