# jujube #

Jujube is a simple JSON-based service framework.

JSON documents are stored in memory using a light, generic representation.  Jujube's
in-memory document representation is compatible with other JSON processors, such as the
MongoDB driver for Java.

Features:

- A lightweight, JSON-based dependency injection mechanism.

## Project and Dependencies ##

Jujube is a Java project.  Maven build files are included.

Jujube depends on the Jackson JSON Processor.

Jujube is designed with Web frameworks in mind, but is independent of any particular Web
framework.

Jujube's in-memory document representation is compatible with the unmapped output of the 
MongoDB driver for Java, but the core Jujube modules do not depend on the MongoDB driver.
