Grendel Version 0.1
By: Mouse961

Folder Contains:
-Gradle File Folder
-Build.gradle
-Build.Properties
-Gradlew.bat
-Readme.txt
-version.xml

Get these and install/configure on your own:
-gradle-1.10 for Forge 9.11.1.964 and below
-gradle-2.0 for Forge 10.0+

To use this package, just drop it into the same folder your source code.
Make sure only one mod is in the folder.
No folders can have spaces in ther names.

Before you can use this there are a few modifications you have to make.

First:
Open up build.gradle and use find and replace. There are 4 things you are going to want to replace.

A) Find and Replace "abriv" with a shortened version of your mod.
example:
industrial craft 2 = ic2
equivilant exchange 3 = ee3

B) Find and Replace "Title" with the full name of your mod.

C) Find and Replace "BNum" with the build number of your mod.

D) Find and Replace "address" with the location of your source code. We will go over that in more detail later.

Thats it for that file. Make sure to save before you close.

Second:
Open Build.Properties add the version of your mod you are after "mod_version="
example:
mod_version=1.1

#############################################################################################
---------------------------------------------------------------------------------------------
#############################################################################################

Next before you re-compile you need to make sure your file structure is set correctly. Your file structure should be set as follows using grendel as my mod name:

Grendel
-src
--main
---java
----"random name folder"  [This is usually your mods name]
-----"your source code"   
---resources
----assets
-----"your assets"
-----"your mcmod.info"

Address is the name of the random folder.
Source code goes insie the random folder.

#############################################################################################
---------------------------------------------------------------------------------------------
#############################################################################################

Now you are ready to start compiling.

Open commandprompt in administrative mode and type in gradle. If it shows somthing other than errors you installed it right.
Now type: cd "location you put contents of Grendel"
example: 
cd C:\Users\Mouse\Desktop\Editing\Mods\Grendel

When there first type: gradle setupCiWorkspace

This step will take a few minutes to complete.

Afterwards type: gradle build

As long as you saw a BUILD COMPLETE after the last 2 steps you are done!
You should fin your jar file in build/libs




#############################################################################################
---------------------------------------------------------------------------------------------
#############################################################################################


Advanced folder layout:


Grendel
-src
--main
---java
----com
-----"your name"
------"mod name"
-------"your source code"
---resources
----assets
-----"your assets"
-----"your mcmod.info"

Address would be "com.yourname.yourmod"


