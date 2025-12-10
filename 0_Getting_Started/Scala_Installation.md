# 🚀 Scala Installation Guide

Choose your preferred installation method. We recommend **Scala CLI** for beginners as it's the most straightforward.

## Option 1: Scala CLI (Recommended for Beginners) ⭐⭐⭐⭐⭐

Scala CLI is the easiest way to get started with Scala. It combines Scala, scalac, and sbt into a single command.

### Installation

#### macOS (Homebrew)
```bash
brew install scala-cli
```

#### Linux (curl)
```bash
curl -fL https://github.com/VirtusLab/scala-cli/releases/latest/download/scala-cli-x86_64-pc-linux.gz | gzip -d > scala-cli
chmod +x scala-cli
sudo mv scala-cli /usr/local/bin/
```

#### Windows (PowerShell)
```powershell
# Download from releases page or use scoop
scoop bucket add extras
scoop install scala-cli
```

### Verification
```bash
scala-cli --version
# Should show: Scala CLI version X.X.X
```

### Your First Scala Program
```bash
# Create a file
echo 'println("Hello, Scala World!")' > hello.scala

# Run it
scala-cli hello.scala
# Output: Hello, Scala World!
```

## Option 2: sbt (Traditional Build Tool)

sbt is the standard build tool for Scala projects. It's more complex but provides complete control.

### Installation

#### macOS
```bash
brew install sbt
```

#### Linux
```bash
# Ubuntu/Debian
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt

# Or manual install
curl -L -o sbt.tgz https://github.com/sbt/sbt/releases/download/v1.8.2/sbt-1.8.2.tgz
tar -xzf sbt.tgz
sudo mv sbt/bin/* /usr/local/bin/
```

#### Windows
Download and install from: https://www.scala-sbt.org/download.html

### Create Your First sbt Project
```bash
# Create new project
sbt new scala/hello-world.g8

# Navigate to project
cd hello-world

# Run it
sbt run
# Output: Hello, World!
```

### Understanding sbt Structure
```
hello-world/
├── build.sbt          # Project configuration
├── project/
│   └── build.properties # sbt version
└── src/
    └── main/
        └── scala/
            └── Main.scala # Your code here
```

## Option 3: IntelliJ IDEA + Scala Plugin

### Installation
1. Download IntelliJ IDEA Community Edition (Free): https://www.jetbrains.com/idea/download/
2. Install Scala Plugin: File → Settings → Plugins → Search "Scala" → Install

### Create New Project
1. File → New → Project → Scala
2. Select "sbt" or "Scala CLI"
3. Create `src/main/scala/Main.scala`:
```scala
object Main extends App {
  println("Hello, Scala!")
}
```

## Option 4: Command Line Scala REPL

### Interactive Scala
```bash
# Start REPL
scala

# Try some commands
scala> val x = 42
scala> println(s"The answer is $x")

# Exit with :quit
```

## Troubleshooting Common Issues

### Java Not Found
Scala requires Java 8 or 11. Check your Java version:
```bash
java -version
# Should show Java 1.8.x or 11.x.x
```

If not installed:
```bash
# macOS
brew cask install adoptopenjdk11

# Ubuntu
sudo apt install openjdk-11-jdk

# Download from https://adoptium.net/
```

### Permission Denied (Linux/Mac)
When running `./scala-cli` directly:
```bash
chmod +x scala-cli
```

### PATH Issues
Add to your shell profile (~/.bashrc, ~/.zshrc):
```bash
export PATH=$PATH:/usr/local/bin
```

### Conflicting Scala Versions
If you have multiple Scala installations, use specific versions:
```bash
scala-cli --scala 2.13.8 hello.scala
```

## Recommended Development Environment

1. **VS Code** + Metals extension (lightweight, free)
2. **IntelliJ IDEA Community** + Scala plugin (powerful, free)
3. **vim/neovim** with Scala syntax highlighting (minimalist)

## Next Steps

Once installed, proceed to:
1. [IDE Setup Guide](IDE_Setup.md)
2. [First Scala Program](First_Scala_Program.ipynb)

## Getting Help

- Scala Discord: https://discord.gg/scala
- Stack Overflow: [scala] tag
- GitHub Issues: https://github.com/Davin-X/Scala_practice/issues

🎉 **You're ready to start your Scala journey!**
