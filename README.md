
This is a speed programming project (1-2 working days worth of hours) for a emoji-picking GUI window. It does not perform any rendering, all the buttons have the Unicode emoji character as a text label.

<hr>

The program has two parts.

The first part is the backend and data loader - the latter tries to parse the 'Unicode data files' obtained from www.unicode.org (see 'data' folder in project root), into "renderable" lists of emoji in the former.

The second part is the GUI, which should be a fairly typical and straightforward AWT/Swing GUI.

<hr>

The first part is 100% functions or open objects, so they should be accompanied by unit tests.

The second part for now will only be manually tested.

<hr>

Latest demo of the program:

![The emoji picker is a tall native GUI window. It has a text field at the top, a button box below it for emoji groups, then a bigger button box for the emojis within the currently selected group. The button for an emoji group is clicked to select it, updating the emoji buttons below. Then the emoji buttons are clicked, which appends the emoji to the text field. The text in the field is selected and copied to the clipboard.](Demo.gif)
