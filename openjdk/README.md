# BusyLight Tool Project (OpenJDK/FX Version)

A project for developing the OpenJDK/FX version of the BusyLight Tool

## Prerequisites

* macOS or Windows OS 64-bit
* Terminal (macOS) or [Git Bash](https://git-scm.com/download/win) (Windows)

## Remove old Java versions

### Mac users

Open a _Terminal_ window and execute the following commands:

```
$  sudo rm -fr /Library/Internet\ Plug-Ins/JavaAppletPlugin.plugin 
$  sudo rm -fr /Library/PreferencePanes/JavaControlPanel.prefPane 
$  sudo rm -fr ~/Library/Application\ Support/Oracle/Java
$  sudo rm -r ~/"Library/Application Support/Oracle/Java"
$  cd /Library/Java/JavaVirtualMachines
$  sudo rm -rf jdk-10.0.2.jdk  # and any other JDK
```
    
### Windows users

* Find the _Add or remove programs_ window
* Sort by _Publisher_ *Oracle Corporation*
* Uninstall/remove all programs for _Java_

## Install Maven

* Download from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
* Download the Binary zip archive, for example: *apache-maven-3.6.1-bin.zip*
* Extract the zip file and copy the extracted folder, for example *apache-maven-3.6.1* to your home folder
* In your `~/.bash_profile` file, add the `MAVEN_HOME` environment variable. For example, if you are user `mjones`:

    ```
    # On Mac...
    MAVEN_HOME=/Users/mjones/apache-maven-3.6.1

    # On Windows...
    MAVEN_HOME=/c/Users/mjones/apache-maven-3.6.1
    ```

## Install OpenJDK

* Download OpenJDK 11 LTS:

    * Visit [https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot)
	* Select the Version: _OpenJDK 11(LTS)_
    * Select the JVM: _HotSpot_
    * Click _Latest release_ to download the file.
    * Click *Download JDK* and download _macOS x64_ (Mac) or _Windows x64_ (Windows).
    * Extract the downloaded ZIP file

* Copy the extracted folder to another location, for example:

    * On Mac, copy *jdk-11.0.3+7* to `/Library/Java/JavaVirtualMachines/`
    * On Windows, copy *jdk-11.0.3+7* to your home folder

* Add/replace Java environment variables in `~/.bash_profile`, for example:

    ```
    # On Mac...
	JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.3+7/Contents/Home

    # On Windows, if you are mjones...
    JAVA_HOME=/c/Users/mjones/jdk-11.0.3+7

    # Both platforms
	JDK_HOME=$JAVA_HOME
	JRE_HOME=$JAVA_HOME/bin
    ```

## Install OpenFX

OpenFX is the open source version of JavaFX. Two OpenFX libraries are required:

* Go to [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/).
* Download _JavaFX SDK_ for your platform.
* Download _JavaFX jmods_ for your platform.
* Extract both ZIP files in your home folder. For example, for user `mjones` on macOS will have:

    ```
    /Users/mjones/javafx-jmods-11.0.2
    /Users/mjones/javafx-sdk-11.0.2
    ```

* Add OpenFX environment variables in `~/.bash_profile`, for example:

    ```
    # On Mac...
    OPENFX_SDK=/Users/mjones/javafx-sdk-11.0.2
    OPENFX_JMODS=/Users/mjones/javafx-jmods-11.0.2

    # On Windows, if you are mjones...
    OPENFX_SDK=/c/Users/mjones/javafx-sdk-11.0.2
    OPENFX_JMODS=/c/Users/mjones/javafx-jmods-11.0.2
    ```

## Update CLASSPATH

In `~/.bash_profile` add the CLASSPATH variable:

```
CLASSPATH=.:$JAVA_HOME/lib:$OPENFX_JMODS:$OPENFX_SDK/lib
```

## Update the PATH variable

The PATH environment variable indicates where executable programs are. In the `~/.bash_profile` file, add the Maven and Java `bin` folders to the `PATH` variable:

```
PATH=${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}
```

## Export all environment variables

Finally, in `~/.bash_profile`, export all the environment variables:

```
export JAVA_HOME JDK_HOME JRE_HOME OPENFX_SDK OPENFX_JMODS CLASSPATH PATH	
```

## Proxy settings

If you are running inside a corporate network, set proxies in `~/.m2/settings.xml`.

## Quick test

Open a Terminal (Mac) or Git Bash window (Windows), then execute these commands:

```
$  cd  # go to home
$  . .bash_profile
$  # you should see version output for the next three commands
$  java -version   
$  javac -version
$  mvn -version
```

## Install JAR files

From a terminal in the obusylight root folder:

```
$  cd openjdk
$  mvn install:install-file -Dfile=../lib/java-json.jar -DgroupId=org.json -DartifactId=json -Dversion=1 -Dpackaging=jar
$  mvn install:install-file -Dfile=../lib/javax.ws.rs-api-2.0.1.jar -DgroupId=javax -DartifactId=javax.ws.rs-api-jar -Dversion=2.0.1 -Dpackaging=jar
```

## Running the project

* Clone this repo to your computer
* Plug in a BusyLight device to a USB port on your computer (make sure it lights up briefly)
* Run this project, following one of the three methods below.

### In VS Code

* Start _VS Code_
* Open this folder: File > Open... > openjdk
* Terminal > New Terminal
* From the Terminal, go to this folder: `cd openjdk`
* Run the project: `mvn clean javafx:run`
* Build the JAR: `mvn clean install assembly:single package`

### From the terminal

```
$  cd openjdk
$  ./run.sh
$  mvn clean javafx:run  # or run it this way
$  mvn clean install assembly:single package  # build jar file
```

### Eclipse

* File > Import... > Maven > Existing Maven Projects
* Navigate to `openjdk`
* Right-click project > Run As... > Maven build...
* Goal - Run from Eclipse: `clean javafx:run`
* Goal - Build executable JAR: `mvn clean install assembly:single package` 
* Run

### Running the JAR file

```
$  cd obusylight
$  java --module-path ${OPENFX_SDK}/lib --add-modules=javafx.controls -jar target/obusylight-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Deployment

See the `deploy` folder `README.md` file.



_End._