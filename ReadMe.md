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

```
cd galactype (root folder in line with pom.xml)
mvn compile
```

Run the application: // TODO for future reference 

```
cd galactype (root folder in line with pom.xml)
mvn javafx:run
```

## Git Workflow (Team Conventions)

All development should occur on the **dev branch** unless otherwise instructed.

Before starting work each session:

```
git switch dev
git pull
```

This ensures your local copy is up to date before making changes.

### Committing Changes

After making changes: 

```
cd group22 / cd ~/Desktop/Projects/Galactype/group22 (make sure you are in group22 outermost folder)
git add <file>
git commit -m "Short description of change"
git push
```

Example:

```
git add ReadMe.md
git commit -m "Added compile and run instructions"
git push
```

### General Rules

- Always `git pull` before starting work.
- Make **small commits** with clear messages.
- Avoid editing the same lines of code as other developers when possible.
- Do not commit build artifacts such as the `target/` directory.
- Ensure the project compiles (`mvn compile`) before pushing.