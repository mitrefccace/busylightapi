# Deploying the BusyLight Tool

* Build the JAR:

    ```
    $  cd obusylight
    $  mvn clean install assembly:single package
    ```

* Copy `busylight.sh` to the target computer.
* Copy `target/obusylight-1.0-SNAPSHOT-jar-with-dependencies.jar` to the same folder as `busylight.sh`
* Run: `./bushlight.sh`