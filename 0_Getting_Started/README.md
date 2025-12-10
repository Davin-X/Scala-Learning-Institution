# 🏁 Phase 0: Getting Started - Your Scala Environment Setup

## Welcome to Scala Development! 🚀

**Phase 0**: Setting up your development environment (1-2 days)

**Prerequisites**: None - this is your starting point!

**Goal**: Create a fully functional Scala development environment on your system

---

## 🎯 Learning Objectives

After completing this phase, you'll be able to:
- Install and configure Scala development tools
- Create and run your first Scala programs
- Understand the Scala development ecosystem
- Set up a professional development environment
- Navigate the basic Scala tooling landscape

---

## 📋 Phase Overview & Structure

### **0A: Understanding Scala & Its Ecosystem**
- What is Scala and why should you learn it?
- Scala vs Java comparison
- Scala developers' benefits and career paths
- Functional programming fundamentals overview

### **0B: Installing Scala & Build Tools**
- JDK installation and configuration
- Scala CLI setup for learning and scripting
- sbt (Build Tool) installation for projects
- IDE setup (VS Code with Metals / IntelliJ IDEA)

### **0C: Your First Scala Programs**
- Writing and running simple Scala code
- Understanding Scala syntax basics
- Working with the Scala REPL
- Basic file operations and program structure

### **0D: Development Environment Optimization**
- IDE configuration and extensions
- Code formatting and linting setup
- Version control integration
- Troubleshooting common issues

---

## 💡 Why Scala? Understanding the Language

### **Scala's Unique Position**

Scala combines the **best of both worlds**:

```scala
// Functional Programming Power (like Haskell)
val numbers = List(1, 2, 3, 4, 5)
val doubled = numbers.map(_ * 2)     // [2, 4, 6, 8, 10]
val sum = numbers.reduce(_ + _)      // 15

// Object-Oriented Programming (like Java)
class Employee(val name: String, val salary: Double) {
  def getRaise(percentage: Double): Double = salary * (1 + percentage/100)
}

val emp = new Employee("Alice", 50000)
println(emp.getRaise(10))  // 55000 additional type safety
```

### **Real-World Benefits**

```scala
// Alternative: Ruby's elegance with Java's power
// Built for large-scale applications (200K+ lines of code)
// Type-safe: Compile-time error catching
// Functional: Cleaner, more maintainable code
// Concurrent: Built-in parallelism
// Ecosystem: Rich library support
```

### **Industry Applications**

- **Financial Services**: High-frequency trading, risk modeling
- **Big Data**: Spark (distributed computing)
- **Web Development**: Play Framework, HTTP4s
- **Machine Learning**: Integration with ML libraries
- **Microservices**: Akka actor system

---

## 🔧 Path 1: Basic Setup (Recommended for Beginners)

### **Step 1: Install Java (Required)**
Scala runs on the Java Virtual Machine (JVM).

#### **Windows:**
1. Visit [Adoptium.net](https://adoptium.net/)
2. Download Windows installer
3. Run installer with default settings

#### **macOS:**
```bash
# Using Homebrew (recommended)
brew tap homebrew/cask-versions
brew install --cask temurin11

# Alternative: Direct download from adoptium.net
```

#### **Linux:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-11-jdk

# Arch Linux
sudo pacman -S jdk11-openjdk

# Verify installation
java -version
# Should show: OpenJDK version "11.x.x"
```

### **Step 2: Install Scala CLI**
Modern, simple Scala installation for learning and scripting.

#### **Installation:**
```bash
# Universal installer (all platforms)
curl -fL https://github.com/VirtusLab/scala-cli/releases/latest/download/scala-cli-x86_64-pc-linux.gz | gzip -d > scala-cli
chmod +x scala-cli
sudo mv scala-cli /usr/local/bin/

# Verify
scala-cli --version
```

### **Step 3: Your First Program**
Create your first Scala program:

```bash
# Create a new file
nano hello.sc  # or use any text editor

# Add this content:
#!/usr/bin/env scala-cli

println("Hello, Scala World! 🌟")

// Simple calculations
val x = 42
val y = x * 2
println(s"x = $x, y = $y")

// Functions
def square(n: Int): Int = n * n
println(s"Square of 5 is ${square(5)}")
```

Run it:
```bash
scala-cli hello.sc
```

**Expected output:**
```
Hello, Scala World! 🌟
x = 42, y = 84
Square of 5 is 25
```

---

## 🎯 Path 6: Project-Based Setup (Recommended for Professionals)

### **Step 1: Install sbt (Build Tool)**
Industry-standard build tool for Scala projects.

#### **Installation:**
```bash
# Universal installer (all platforms)
curl -fSL https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz | gzip -d > cs
chmod +x cs
./cs setup

# Install sbt via coursier
cs install sbt

# Verify
sbt --version
```

### **Step 2: Create Your First sbt Project**
```bash
# Create project directory
mkdir my-scala-project
cd my-scala-project

# Create build.sbt
echo 'name := "my-scala-project"
version := "0.1.0"
scalaVersion := "2.13.8"' > build.sbt

# Create source directory structure
mkdir -p src/main/scala

# Create main application
cat > src/main/scala/Main.scala << 'EOF'
object Main extends App {
  println("Welcome to Scala development! 🚀")

  // Functional programming example
  val numbers = (1 to 10).toList
  val doubled = numbers.map(_ * 2)
  println(s"Numbers: $numbers")
  println(s"Doubled: $doubled")

  // Case class example
  case class Person(name: String, age: Int)
  val people = List(
    Person("Alice", 25),
    Person("Bob", 30),
    Person("Charlie", 35)
  )

  people.foreach(p => println(s"${p.name} is ${p.age} years old"))
}
EOF

# Run the project
sbt run
```

---

## 🔧 Path 3: IDE Setup (Professional Development Environment)

### **Option A: VS Code + Metals (Recommended for Learning)**
VS Code is lightweight and great for learning.

```bash
# Install VS Code
# Visit https://code.visualstudio.com/ and download

# Essential extensions:
# 1. Scala Syntax (Official)
# 2. Metals (Scala language server)
# 3. Scala (scalameta) - basic syntax highlighting

# Configure Metals in settings.json:
{
  "metals.serverVersion": "latest.release",
  "metals.sbtScript": "sbt",
  "metals.scalafmtConfig": {
    "version": "3.0.0"
  }
}
```

### **Option B: IntelliJ IDEA (Recommended for Professional Development)**
Industry-standard IDE with excellent Scala support.

```bash
# Download IntelliJ IDEA Community Edition (free)
# Visit: https://www.jetbrains.com/idea/download/

# Install Scala plugin:
# File → Settings → Plugins → Search "Scala" → Install

# Configure JDK:
# File → Project Structure → Project SDK → Add JDK 11

# Create new Scala project:
# File → New → Project → Scala (sbt)
```

---

## 📚 Understanding Scala Tooling

### **Scala CLI**
- **Best for**: Learning, scripting, quick prototyping
- **Use when**: Building small programs, testing ideas
- **Commands**:
  ```bash
  scala-cli run myfile.sc     # Run script
  scala-cli compile myfile.sc # Compile to bytecode
  scala-cli repl              # Interactive shell
  ```

### **sbt (Simple Build Tool)**
- **Best for**: Full applications, complex projects
- **Use when**: Building production applications
- **Commands**:
  ```bash
  sbt compile                 # Compile sources
  sbt test                    # Run tests
  sbt run                     # Run application
  sbt package                 # Create JAR
  sbt clean                   # Clean build artifacts
  ```

### **IDE Comparison**
- **VS Code + Metals**: Fast startup, simple, great for learning
- **IntelliJ IDEA**: Full-featured, enterprise-grade, preferred by professionals
- **Both support**: Auto-completion, debugging, refactoring, formatting

---

## 🚀 Path 4: Advanced Environment Setup

### **Configure Code Formatting**
```bash
# Install Scalafmt for consistent code style
cs install scalafmt

# Create .scalafmt.conf in project root
echo 'version = "3.5.9"
align = true
lineEndings = unix
maxColumn = 100' > .scalafmt.conf

# Format code
scalafmt .
```

### **Setup Git Integration**
```bash
# Initialize git repository
git init
git add .
git commit -m "Initial Scala project setup"

# Create .gitignore
echo 'target/
.idea/
.metals/
*.jar
*' > .gitignore
```

### **Version Control Best Practices**
```bash
# Commit frequently with descriptive messages
git add -A
git commit -m "feat: implement user authentication module"

# Use branches for new features
git checkout -b feature/user-registration
# ... work ...
git checkout main
git merge feature/user-registration
```

---

## 🔍 Troubleshooting Guide

### **Common Installation Issues**

#### **Java Version Issues**
```bash
# Check Java version
java -version

# If wrong version, check alternatives
update-alternatives --list java
update-alternatives --set java /usr/lib/jvm/java-11-openjdk-amd64/bin/java
```

#### **Scala CLI Issues**
```bash
# Check if scala-cli is in PATH
which scala-cli

# If not found, add to PATH or use full path
export PATH="$PATH:/usr/local/bin"
/usr/local/bin/scala-cli --version
```

#### **sbt Issues**
```bash
# Clear sbt cache if problems
rm -rf ~/.sbt/preloaded ~/.ivy2/cache

# Check sbt repositories
sbt
# Type: Global / repositories
# Should show configured repositories
```

#### **IDE Issues**
- **Metals not working**: Ensure JDK 11+ and scala-cli installed
- **IntelliJ slow**: Increase RAM allocation (File → Settings → Build, Execution, Deployment → Compiler → Build process heap size)
- **No syntax highlighting**: Ensure Metals extension is enabled and Metals server is running

---

## 📋 Phase Validation: Success Checklist

- [ ] Java JDK installed and configured correctly
- [ ] Scala CLI working (can run hello.sc)
- [ ] sbt installed (can create and run projects)
- [ ] IDE configured (can see Scala syntax highlighting)
- [ ] Created and ran first Scala program
- [ ] Understanding of Scala vs Java differences
- [ ] Know when to use Scala CLI vs sbt
- [ ] Can set up new projects from scratch
- [ ] Familiar with basic scala-cli and sbt commands
- [ ] Environment ready for Phase 1 learning

---

## 🎯 Common Scala's Strengths Demo

Let's run a program that demonstrates why Scala is special:

```scala
// advanced.sc - showcases Scala's power
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration.Duration

implicit val ec: ExecutionContext = ExecutionContext.global

// Functional programming
def fibonacci(n: Int): Int = {
  @annotation.tailrec
  def loop(a: Int, b: Int, n: Int): Int = {
    if (n <= 0) a else loop(b, a + b, n - 1)
  }
  loop(0, 1, n)
}

// Concurrency made easy
def calculateFibonacci(numbers: List[Int]): Future[List[Int]] = {
  Future.traverse(numbers.map(Future(_)))(identity)
    .map(_.map(fibonacci))
}

// Pattern matching
def describeValue(input: Any): String = input match {
  case "scala" => "The language you're learning! 🎯"
  case n: Int if n > 100 => s"Large number: $n"
  case n: Int => s"Number: $n"
  case list: List[_] => s"List with ${list.length} elements"
  case _ => s"Other value: $input"
}

// Case classes (algebraic data types)
case class Person(name: String, age: Int, email: Option[String] = None)

// Run the demonstration
object ScalaDemo extends App {
  println("=== Scala Power Demonstration ===\\n")

  // Functional programming
  val fibNumbers = (1 to 10).toList.map(fibonacci)
  println(s"Fibonacci numbers: $fibNumbers\\n")

  // Concurrency
  val concurrentResult = calculateFibonacci(List(5, 10, 15, 20))
  concurrentResult.foreach(result =>
    println(s"Concurrent results: $result\\n")
  )(ec)

  // Pattern matching
  val examples = List("scala", 42, 150, List(1,2,3), "hello")
  println("Pattern matching examples:")
  examples.foreach(ex => println(s"  $ex -> ${describeValue(ex)}"))
  println()

  // Algebraic data types
  val people = List(
    Person("Alice", 25, Some("alice@email.com")),
    Person("Bob", 30), // email is None
    Person("Charlie", 35, Some("charlie@company.com"))
  )

  println("People data:")
  people.foreach(person => {
    val email = person.email.getOrElse("no email")
    println(s"  ${person.name} (${person.age}) - $email")
  })

  println("\\n=== Welcome to Scala Development! 🚀 ===")
}
```

Run it with:
```bash
scala-cli advanced.sc
```

---

*"A smooth development environment is the foundation of effective learning. By the end of this phase, you'll have a professional Scala setup that will support your entire learning journey!"*

**Next**: Ready for [Phase 1: Beginner Fundamentals](../1_Beginner/)!) 🚀
