# Developer ReadMe
## Conventions
ALL file paths must be written with java.nio.file.Path to ensure cross platform compatability. 
EX:
```java
// Bad :(
String path = "resources\\levels\\level1.json";  // windows
String path = "resources/levels/level1.json";  // UNIX

// Good ;)
Path levelFile = Paths.get("resources", "levels", "level1.json");
String content = Files.readString(levelFile);
```

## File Structure

```
.
├── ReadMe.md
└── galactype
    ├── pom.xml
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── group22
    │   │   │       └── Main.java
    │   │   └── resources
    │   └── test
    │       └── java
    └── target
        ├── classes
        │   └── group22
        │       └── Main.class
        └── test-classes
```

Tentatively, all 3 JSON datafiles are to be written diretly into reasources




## Running the Project

This project uses Maven.

Compile the project in terminal : 
1 : cd galactype (root folder in line with pom.xml)
2 : mvn compile

Run the application: // TODO for future reference 
1 : mvn javafx:run