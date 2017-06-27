# BusyLightAD

This is an ACE Direct interface to the BusyLight device. There are two options: _BusyLightAD_ and _lightserver_. 

## BusyLightAD

The BusyLightAD program is a GUI for integrating a BusyLight with the ACE Direct platform. This is part of the busylightapi Java Eclipse project. BusyLightAD requires a URL from the ACE Direct agent portal. This is how it establishes a connection to ACE Direct. _This will soon be replaced by lightserver._

## lightserver

The lightserver program is another GUI for integrating a BusyLight with the ACE Direct platform. This is part of the busylightapi Java Eclipse project. The lightserver application runs a server on localhost, and it is only available locally on the agent's desktop computer. The ACE Direct portal makes the initial connection to lightserver, and all requests to localhost will work after that. This is an improvement on the BusyLightAD program, since a unique URL is not required.

### Requirements

* busylightapi GitHub project
* JAX-RS 2.0 libraries: https://jersey.github.io/download.html
    * aopalliance-repackaged-2.5.0-b32.jar
    * hk2-api-2.5.0-b32.jar
    * hk2-locator-2.5.0-b32.jar
    * hk2-utils-2.5.0-b32.jar
    * javassist-3.20.0-GA.jar
    * javax.annotation-api-1.2.jar
    * javax.inject-2.5.0-b32.jar
    * javax.servlet-api-3.0.1.jar
    * javax.ws.rs-api-2.0.1.jar
    * jaxb-api-2.2.7.jar
    * jersey-client.jar
    * jersey-common.jar
    * jersey-container-servlet.jar
    * jersey-container-servlet-core.jar
    * jersey-guava-2.25.1.jar
    * jersey-media-jaxb.jar
    * jersey-server.jar
    * jsonassert-1.3.0.jar
    * jsonassert-1.3.0-sources.jar
    * json-org.jar
    * org.osgi.core-4.2.0.jar
    * osgi-resource-locator-1.0.1.jar
    * persistence-api-1.0.jar
    * validation-api-1.1.0.Final.jar

### Installation and Configuration

From Eclipse, build a runnable JAR file for the BusyLightAD or lightserver class.

### Running

```java
java -jar busylightad.jar _or_
java -jar lightserver.jar 
```


