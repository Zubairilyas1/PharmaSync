# How to Run PharmaSync

## Prerequisites

- **Java Development Kit (JDK)**: Java 11 or higher installed
- **JavaFX SDK**: Version 17 or higher
- **MySQL**: Running locally or configured server connection
- **VS Code** (recommended) with Java extensions installed

## Quick Start

### 1. Open the Project

```bash
# Open the project folder in VS Code
code .
```

### 2. Compile the Project

In VS Code terminal:

```bash
# Windows
javac -d bin --module-path "lib" --add-modules javafx.controls,javafx.fxml -sourcepath src src\App.java

# macOS/Linux
javac -d bin --module-path "lib" --add-modules javafx.controls,javafx.fxml -sourcepath src src/App.java
```

### 3. Run the Application

In VS Code terminal:

```bash
# Windows
java --module-path "lib" --add-modules javafx.controls,javafx.fxml -cp bin App

# macOS/Linux
java --module-path "lib" --add-modules javafx.controls,javafx.fxml -cp bin App
```

## Using VS Code Java Extension

1. Open the project in VS Code
2. Go to the **JAVA PROJECTS** view (left sidebar)
3. Ensure all dependencies are loaded from the `lib` folder
4. Click the **Run** button next to the `App` class in the file explorer

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Module not found | Verify `lib` folder path and JavaFX SDK is installed |
| Cannot find symbol | Ensure all Java files compile; check class imports |
| MongoDB connection error | Verify MongoDB is running locally on `localhost:27017` |
| Class not found | Check that `bin` folder contains compiled `.class` files |

## Database Setup

Make sure MongoDB is running:

```bash
# Windows (if installed locally)
mongod

# Or use MongoDB Atlas for cloud database
```

## Notes

- First run requires authentication (see LoginPage credentials)
- Logs are stored in the audit logs module
- Configuration can be modified in `.vscode/settings.json`