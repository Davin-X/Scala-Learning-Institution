# Scala Learning Repository

A comprehensive Scala learning resource with structured progression from beginner to advanced topics.

[![Scala](https://img.shields.io/badge/Scala-2.13.8-red)](https://scala-lang.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## Learning Path

| Phase | Focus Area | Content | Duration |
|-------|------------|---------|----------|
| [0: Getting Started](0_Getting_Started/) | Environment Setup | JDK, Scala CLI, sbt, IDE | 1-2 days |
| [1: Beginner](1_Beginner/) | Core Scala | Variables, OOP, collections, functions | 2-3 weeks |
| [2: Intermediate](2_Intermediate/) | Advanced Features | Implicits, futures, Spark, macros | 3-4 weeks |
| [3: Interview Prep](3_Interview_Preparation/) | Algorithms | Arrays, strings, data structures | 2-3 weeks |
| [4: Projects](4_Practical_Projects/) | Real Applications | CLI tools, web services | 2-4 weeks |
| [5: Big Data](5_Big_Data/) | Spark Ecosystem | RDDs, DataFrames, distributed computing | 2-3 weeks |
| [6: Libraries](6_Libraries/) | Advanced Ecosystem | Cats, HTTP4s, functional programming | 2-3 weeks |

## Repository Structure

```
Scala-Learning-Institution/
├── 0_Getting_Started/          # Environment setup and first programs
├── 1_Beginner/                 # Core Scala fundamentals  
├── 2_Intermediate/             # Advanced concepts and patterns
├── 3_Interview_Preparation/    # Algorithm training and practice
├── 4_Practical_Projects/       # Real-world applications
├── 5_Big_Data/                 # Spark and distributed computing
├── 6_Libraries/               # Advanced functional libraries
├── best-practices/            # Coding standards and guidelines
└── README.md                  # This file
```

## Quick Start

### Prerequisites
- Java 8 or 11 (JDK)
- Text editor or IDE

### Installation Options

#### Option 1: Scala CLI (Recommended)
```bash
# Install Scala CLI
curl -fL https://github.com/VirtusLab/scala-cli/releases/latest/download/scala-cli-x86_64-pc-linux.gz | gzip -d > scala-cli
chmod +x scala-cli
sudo mv scala-cli /usr/local/bin/

# Test installation
scala-cli --version
```

#### Option 2: sbt Build Tool
```bash
# Install sbt
# macOS: brew install sbt
# Ubuntu: sudo apt install sbt
# Windows: Download from sbt website

# Test installation  
sbt --version
```

### Your First Scala Program
```bash
# Create hello.sc
echo 'println("Hello, Scala!")' > hello.sc

# Run it
scala-cli hello.sc
```

## Learning Resources

### Official Documentation
- [Scala Language Tour](https://docs.scala-lang.org/tour/tour-of-scala.html)
- [Scala Standard Library](https://www.scala-lang.org/api/current/)

### Practice Platforms
- [LeetCode Scala Problems](https://leetcode.com/problemset/all/)
- [Exercism Scala Track](https://exercism.org/tracks/scala)

## Learning Outcomes

By completing this repository, you will be able to:

### Beginner Level
- Write, compile, and run Scala programs
- Use functional programming concepts
- Create classes, objects, and case classes
- Work with collections and pattern matching

### Intermediate Level  
- Implement advanced OOP with traits and mixins
- Use implicits and type classes
- Write concurrent programs with Futures
- Test applications with ScalaTest

### Advanced Level
- Build distributed systems with Spark
- Create web services with HTTP4s
- Apply functional programming with Cats
- Design scalable, maintainable Scala applications

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

MIT License - see LICENSE file for details.

---

**Start your Scala journey with [Getting Started](0_Getting_Started/)**
