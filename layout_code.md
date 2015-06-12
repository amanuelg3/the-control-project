**This Page Is UNDER CONSTRUCTION**

In Control layout code is used to create layouts. For more information about basic Controller stuff please read Writing\_A\_Controller .
In layout code each parameter is separated by an `_` (underscore) and each element is separated by a `~` (tilde).

# Need To Know #

## Sizes ##

There are 3 ways to define size:

  * In pixels as if on a 160dpi screen (as an integer)

  * `-1` to match the size of the parent container

  * `-2` to fit content

## Weight ##

When using the dynamic sizes weights can help control sizes. Weights control the sizes relative to other elements in the same Layout Container. For example if you had 2 buttons and they each had a weight of 1 then they would be equally sizes. If one of them was changed to a weight of 2 it would take 2/3 of the screen and the other would take 1/3. Unlike other layout parameters, layout weights are floats not integers.

## Layout Containers ##

The basis of a layout in control are layout containers (linear layouts in android speak). They act as containers for other element to be arranged inside. Every layout container is given a number. The root layout container is always 0, the first defined layout container is 1, the second is 2 and so on. There is a limit of 32 layout containers including root (only 31 can be added).


Layout containers are defined like this:
```
1_the number of the container this is placed in_direction to place elements; 0 for across and 1 for vertically_gravity (explained below)_width_height_weight
```

For example, this would create a layout container that fills the screen, puts elements in the center and orders them from left to right:
```
1_0_0_17_-1_-1_1
```

|**Tip** | To be able to use the gravity feature and keep compatibility with all devices use match size (-1) and weights to make your layout containers|
|:-----------------------------------------------------------------------------------------------------------------------------------------------------|

## Gravity ##

Gravity defines where the elements are placed. Here is a list of gravitates and there values:
|**Gravity**|**Value**|
|:----------|:--------|
|Bottom     |80       |
|Top        |48       |
|Left       |3        |
|Right      |5        |
|Center     |17       |
|Horizontal Center|1        |
|Vertical Center|16       |

This list only covers some of the more commonly used gravitates. For a complete list please see http://developer.android.com/reference/android/view/Gravity.html

## Tags ##

Each interactive component has a tag that is used to identify the object when it has been changed. The tag is sent back with the object exactly the same as defines. For example a button with the tag `BNT1:` being pressed would send `BNT1:1`. A button with the tag `Button` being released would send back `Boutton0`.

|**Tip** | Try to use a `:` at the end of the tag so you can easily use `x.split(":")` or equivalent in your language. Control also uses `:`s to separate values in sensors and virtual analog sticks.|
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

# Components #

## Buttons ##

Buttons in Control can be pressed and released by the user. The syntax is:

```
0_parent layout container_content (text)_tag_width_height_weight
```

For example a small button inside root with the label `hi` would be:

```
0_0_hi_HiBnt:_-2_-2_1
```

The button has 2 events that it sends information back about; the press event represented by `1` and the release event represented by `0`. For example when the tag is `B1-` the press event sends `B1-1` and the release event sends `B1-0`.

## Toggle Buttons ##

A toggle button can be either on or off. It has a different label for each state. The syntax is:

```
3_parent layout_1 (on first) or 0 (off first)_label for on state_label for off state_tag_width_height_weight
```

This button returns `tag1` if it is changed to on and `tag0` if it is switched off.

|**Usage** | Great for play/pause buttons in games|
|:------------------------------------------------|

## Seek Bars ##

Seek bars are bars that users can drag a dot along. This data can then be sent back to the computer. The seek bar has 1000 units; so halfway is 500. The syntax is:

```
2_parent layout_dot start position (out of 1000)_Tag_width_height_weight
```

The seek bar returns a message whenever it's state changes. The syntax is `TagPosition`. So if there was a seek bar with the tag `SB:` and the user dragged it 1/4 length from the start you would receive `SB:250`.

## Analog Sticks ##

Analog sticks are circles that user can move in any direction, like in the gaming controller. The syntax is:

```
4_parent layout_tag_width_height_weight
```

Values from the analog stick are returned in the `TagX:Y` format. These are how far the circle of the analog stick is moved. The X and Y values are between 100 and -100. X 100 is fully up and -100 is fully down. The extremes of these values should be treated as normal since there is padding around the analog stick that allows users to drag it fully.