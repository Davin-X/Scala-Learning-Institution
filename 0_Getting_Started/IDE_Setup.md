# 🛠️ IDE Setup Guide

Choose your integrated development environment. VS Code is recommended for most beginners.

## Option 1: VS Code + Metals (Recommended) ⭐⭐⭐⭐⭐

VS Code is lightweight, free, and has excellent Scala support through the Metals language server.

### Installation

1. **Install VS Code**: https://code.visualstudio.com/download
2. **Install Scala (Metals) extension**:
   - Open VS Code
   - Go to Extensions (Ctrl+Shift+X)
   - Search for "Scala (Metals)" and install

### Configure Metals

1. **Open Command Palette** (Ctrl+Shift+P)
2. **Run: Metals: New Scala Project**
3. **Run: Developer: Reload Window** after initial setup

### Features You'll Love

- **IntelliSense**: Smart code completion
- **Go to Definition**: Jump to function/method definitions
- **Inline Debugging**: Step through your code
- **Import Management**: Automatic imports
- **Refactoring**: Rename variables, extract methods

### Troubleshooting

**Metals Stuck on Import**
```bash
# Restart Metals via Command Palette
# Run: Metals: Restart Build Server
```

**No Scala Version Detected**
1. Create `build.sbt` in root:
```scala
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file(".")).settings(name := "scala-learning")
```

## Option 2: IntelliJ IDEA Community Edition ⭐⭐⭐⭐

IntelliJ IDEA is the most powerful Scala IDE, built by JetBrains.

### Installation

1. **Download IDEA Community**: https://www.jetbrains.com/idea/download/
2. **Install Scala Plugin**: File → Settings → Plugins → Scala → Install
3. **Restart IDEA**

### Create New Project

1. **File → New → Project → Scala**
2. **Choose: sbt** (recommended)
3. **Project Name**: `scala-learning`
4. **Finish**

### Useful Shortcuts

- **Run**: Shift+F10
- **Debug**: Shift+F9
- **Find File**: Ctrl+Shift+N (Cmd+Shift+O on Mac)
- **Go to Declaration**: Ctrl+B (Cmd+B on Mac)
- **Format Code**: Ctrl+Alt+L (Cmd+Opt+L on Mac)

### SBT Console
```bash
# Open SBT Console: View → Tool Windows → sbt shell
# Run commands like:
compile
run
test
console  # Interactive Scala REPL
```

## Option 3: Command Line + vim/neovim ⭐⭐⭐

Perfect for advanced users who prefer keyboard-driven development.

### vim Setup

1. **Install vim/neovim**
2. **Add Scala syntax highlighting**:
```bash
# Vim-Plug users add to .vimrc:
Plug 'derekwyatt/vim-scala'

# Packer users add to config:
use 'derekwyatt/vim-scala'
```

3. **Install Metals for LSP**:
```bash
# Using vim-lsp:
:LspInstallServer metals
```

### Basic vim Commands for Scala

```vim
# Compile Scala file
:!scalac %.scala

# Run compiled class
:!scala %:r

# Format code (requires scalafmt)
:!scalafmt --stdout %.scala

# Go to definition (with LSP)
gd

# Find references
gr
```

## Project Structure Standards

All our projects follow a consistent structure:

```
your-project/
├── src/
│   ├── main/
│   │   ├── scala/
│   │   │   └── com/yourcompany/
│   │   │       └── Main.scala
│   │   └── resources/
│   └── test/
│       └── scala/
│           └── com/yourcompany/
│               └── MainTest.scala
├── build.sbt
└── README.md
```

## Running Scala Files

### Single File Script (Scala CLI)
```bash
# Run directly
scala-cli hello.scala

# Or with dependencies
scala-cli --dependency com.lihaoyi::os-lib:0.8.1 script.sc
```

### sbt Projects
```bash
# Compile
sbt compile

# Run main class
sbt run

# Run tests
sbt test

# Interactive REPL
sbt console
```

### IntelliJ IDEA
- **Run**: Green play button or Shift+F10
- **Debug**: Red bug button or Shift+F9
- **Run Tests**: Right-click test file → Run

## Scala CLI vs sbt: When to Use What

| Scenario | Scala CLI | sbt |
|----------|-----------|-----|
| Quick script | ✅ Perfect | ❌ Overkill |
| Small single-file program | ✅ Great | ❌ Overkill |
| Multi-file project | ⚠️ Possible | ✅ Ideal |
| Dependencies management | ✅ Good | ✅ Excellent |
| Testing | ❌ Basic | ✅ Comprehensive |
| Production builds | ❌ Not designed | ✅ Perfect |

## Development Workflow

1. **Write code** in your IDE
2. **Compile** to check syntax: `sbt compile` or Metals auto-compiles
3. **Run** your program: `sbt run`
4. **Write tests**: `src/test/scala/...Test.scala`
5. **Run tests**: `sbt test`

## Essential Extensions (VS Code)

```json
{
  "recommendations": [
    "scalameta.metals",
    "scala-lang.scala",
    "ms-vscode.vscode-json",
    "ms-vscode.vscode-typescript-next",
    "redhat.vscode-yaml"
  ]
}
```

## Advanced Configuration

### Custom Scala Version
Create `.scala-version` file:
```
2.13.8
```

### format Code Automatically
Add to build.sbt:
```scala
ThisBuild / scalafmtOnCompile := true
```

### Java Memory Settings
Add to `.jvmopts`:
```
-Xmx4G
-Xms1G
-XX:+UseG1GC
```

## Getting Help

- **VS Code + Metals**: https://scalameta.org/metals/docs/
- **IntelliJ IDEA**: https://www.jetbrains.com/help/idea/discover-intellij-idea-for-scala.html
- **Scala CLI**: `scala-cli --help`

## Next Steps

Once your IDE is set up, move to:
1. [Your First Scala Program](First_Scala_Program.ipynb)
2. [Beginner Phase: Variables & Data Types](../1_Beginner/01_Variables_Data_Types.ipynb)

🎉 **Your development environment is ready! Let's start coding!**
