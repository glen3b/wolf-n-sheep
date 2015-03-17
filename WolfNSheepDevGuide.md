# Introduction #

Some concepts to know when working with the wolf 'n sheep team.


# Details #

Here are the guidelines:

  * Only import used items, and import as many as you need, but avoid `*`
  * The project should have no compiler warnings or errors for any long period of time
  * Any created warnings or errors should be resolved within 2-10 commits/pushes/days (ASAP, but longest is applicable)
  * Before pushing, clean your project (Project > Clean... > Clean projects selected below > WolfNSheep > OK) and commit ("Cleaned project").
  * If there is a conflict in either gen or bin, clean your project (Project > Clean... > Clean projects selected below > WolfNSheep > OK), add (right-click > Team > Add/Add to index) the conflicting folder and commit.
  * Commit to your local git clone often! This saves your changes to version control.
  * Don't be afraid to temporarily branch the project for a multi-commit change.
  * Every so often, push (Right-click on project > Team > Push to upstream). If rejected, pull (Right-click on project > Team > Pull) first, and try to push again.

## Supported IDEs ##

_We only officially support Eclipse with the Android plugin and EGit plugin, but you are welcome to contribute or work from any other IDE or the command line._

## Branches and branching ##

Feel free to branch into new branches with logical names (eg: tiechecker, multiplayer, extras), but merge into master and deprecate the branch ASAP. However, you are free to branch as you need.
Here is a list of currently known branches:

  * master
  * multiplayer

### master ###

The main development branch.

### multiplayer ###

**Deprecated.** Please put multiplayer stuff into a seperate mode in WolfNSheep\_Main on master. **WolfNSheep\_Multiplayer is deprecated too.**
_Due to the nature of the branch status, we probably won't delete the branch, but, AVOID USING IT._


# Get Started #

After you've read the guidelines, you'll want to get started. The first step is to download and setup the JDK, eclipse, the android SDK, and android ADT plugin, as described [here](http://developer.android.com/sdk/installing.html). The next step is to install egit as described [here](http://www.eclipse.org/egit/download/). After everything is set up, you need to add the wolf-n-sheep git repository. Go to Window > Open Perspective > Other > Git Repository Exploring. Once you are there, click on the clone a git repository button. Under URI, enter the URL on the source tab under "Command-line access" **without the "git clone " part**. **COMMITTERS: Under "Authentication", enter your google account username for User and your google code password for Password (you can find your google code password [here](https://code.google.com/hosting/settings)). Make sure to check "Store in Secure Store" to avoid typing in that password every time you push changes.** Click next. Hit "Select All" then click next again. The defaults on this page are fine. Hit finish. Now, right-click on the newly created git repository and choose import projects. Hit next. Make sure the project "WolfNSheep" is selected here and then click finish. You should now have the project, git-linked, in your workspace!