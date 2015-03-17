# Introduction #
These are the basic guidelines for useful bug reports.


# Details #

Here is what you should do:
  * **Don't** use just "It doesn't work"
  * Describe where the problem is (the about dialog, when I click roll for the first time)
  * Describe what the problem is (buttons don't work, about dialog can't close)
  * Choose a good summarizing title

Here is what you should do **if you have the tools installed**:
_Note: Your device must be connected to your computer over USB and mounted as a USB mass storage device for these commands to work. You can find these commands in the android SDK package, assuming you have platform-tools and tools installed._

  * Run the command ` adb bugreport ` and save its output to a **plain text** file. Attach this file to the bug report.
  * Open ` ddms ` and take a screenshot of the error, or where it is located. Attach the screenshot.