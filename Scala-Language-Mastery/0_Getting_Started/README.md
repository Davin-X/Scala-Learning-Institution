# Getting Started with Scala

Set up your Scala development environment and write your first programs.

## Quick Setup (30 minutes)

### 1. Install Java JDK
Scala runs on the JVM - you'll need Java 8 or 11.

```bash
# macOS
brew install openjdk@11

# Ubuntu
sudo apt install openjdk-11-jdk

# Windows: Download from adoptium.net/temurin
```

### 2. Install Scala CLI (Recommended)
```bash
# Download and install
curl -fL https://github.com/VirtusLab/scala-cli/releases/latest/download/scala-cli-x86_64-pc-linux.gz | gzip -d > scala-cli
chmod +x scala-cli
sudo mv scala-cli /usr/local/bin/

# Verify installation
scala-cli --version
```

### 3. Your First Scala Program
```bash
# Create hello.sc
echo 'println("Hello, Scala!")' > hello.sc

# Run it
scala-cli hello.sc
```

## Learning Path

| Step | Topic | Time | Resource |
|------|--------|------|----------|
| 1 | Scala Installation | 20 min | [Scala_Installation.md](Scala_Installation.md) |
| 2 | IDE Setup | 20 min | [IDE_Setup.md](IDE_Setup.md) |
| 3 | First Program | 10 min | [First_Scala_Program.ipynb](First_Scala_Program.ipynb) |
| 4 | Scala Basics | 30 min | [00_Scala_CheatSheet.ipynb](00_Scala_CheatSheet.ipynb) |

## Alternative: sbt Build Tool

For larger projects, use sbt (Scala Build Tool):

```bash
# Install sbt
# macOS: brew install sbt
# Ubuntu: sudo apt install sbt

# Create a new project
sbt new scala/scala-seed.g8
cd your-project-name

# Run it
sbt run
```

## IDE Setup

### VS Code (Recommended)
1. Install VS Code
2. Install extensions: "Scala Syntax (Official)" and "Metals"
3. Open Scala files - Metals will provide language support

### IntelliJ IDEA
1. Download IntelliJ IDEA Community Edition
2. Install Scala plugin during setup or via Plugins menu
3. Create new Scala project or import existing

## Why Scala?

Scala combines object-oriented and functional programming:

```scala
// Object-oriented
class Person(val name: String, val age: Int)

// Functional programming
val numbers = List(1, 2, 3, 4, 5)
val doubled = numbers.map(_ * 2)  // List(2, 4, 6, 8, 10)
```

## Troubleshooting

### Java Version Issues
```bash
java -version  # Should show Java 8 or 11+
```

### Scala CLI Not Found
```bash
which scala-cli  # Check if in PATH
# If not, add to PATH or use full path
```

## Next Steps

After setup, proceed to [Phase 1: Beginner Fundamentals](../1_Beginner/)

---

**Ready to start coding? Open [First_Scala_Program.ipynb](First_Scala_Program.ipynb)**
