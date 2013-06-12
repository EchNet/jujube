# jujube #

Jujube: JSON Utilities for Java.

- Serialization and deserialization of JSON
- Framework for organizing, loading, and traversing JSON documents
- A lightweight dependency injection mechanism based on JSON

Current version: 0.1.2

## Project and Dependencies ##

Jujube is a Java project.  Maven build files are included.

Jujube depends internally on the Jackson JSON Processor but does not expose Jackson through
the APIs.

Jujube is designed with Web frameworks in mind, but is independent of any particular Web
framework.

Jujube's in-memory document representation is compatible with the unmapped output of the 
MongoDB driver for Java, but the core Jujube modules do not depend on the MongoDB driver.
