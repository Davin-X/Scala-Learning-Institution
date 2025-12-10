# 📄 Scala File Processor CLI

A comprehensive command-line application demonstrating Scala concepts from all learning phases.

## 🎯 Features

- **Multi-operation processing**: Statistics, analysis, prime filtering, palindrome detection
- **Parallel processing**: Uses Scala's parallel collections for performance
- **Configurable operations**: Command-line argument parsing with scopt
- **Logging**: Professional logging with Logback
- **Error handling**: Comprehensive error handling and recovery
- **File I/O**: Reading from and writing to files
- **Algorithm demonstration**: Uses interview algorithms in real application

## 🚀 Installation & Setup

### Prerequisites
- Scala 2.13+
- sbt 1.5+

### Building
```bash
cd 4_Practical_Projects/01_File_Processor_CLI
sbt compile
```

### Running
```bash
# Build fat JAR
sbt assembly

# Run the application
java -jar target/scala-2.13/file-processor-cli-assembly-1.0.0.jar --help
```

## 📋 Usage Examples

### Basic Text Statistics
```bash
# Process a text file and show statistics
java -jar target/scala-2.13/file-processor-cli-assembly-1.0.0.jar \
  -i sample-data/text-sample.txt \
  -op stats
```

### Text Analysis with Output File
```bash
# Analyze text and save results to file
java -jar target/scala-2.13/file-processor-cli-assembly-1.0.0.jar \
  -i sample-data/text-sample.txt \
  -op analyze \
  -o analysis.txt
```

### Prime Number Filtering (Sequential)
```bash
# Find primes in a list of numbers
java -jar target/scala-2.13/file-processor-cli-assembly-1.0.0.jar \
  -i sample-data/numbers.txt \
  -op prime-filter \
  -o primes.txt
```

### Palindrome Detection (Parallel)
```bash
# Check palindromes with parallel processing
java -jar target/scala-2.13/file-processor-cli-assembly-1.0.0.jar \
  -i sample-data/phrases.txt \
  -op palindrome-check \
  --parallel
```

## 📁 Project Structure

```
01_File_Processor_CLI/
├── build.sbt                    # Build configuration with all dependencies
├── src/main/scala/
│   └── FileProcessorApp.scala   # Main application with all features
├── sample-data/                # Example data files for testing
├── README.md                   # This documentation
└── logback.xml                 # Logging configuration
```

## 🔧 Scala Concepts Demonstrated

### Phase 1: Beginner Concepts
- **Case classes**: Configuration management
- **Collections**: Lists, arrays, maps for data processing
- **Pattern matching**: Command-line argument handling
- **File I/O**: Reading and writing files
- **Basic error handling**: Try/Success/Failure

### Phase 2: Intermediate Concepts
- **Implicits**: Type class-like configurations
- **Futures/Async**: Potential for async file processing
- **Traits**: LazyLogging mixin
- **Options**: Handling optional configurations
- **Functional programming**: Map, filter, flatMap operations

### Phase 3: Interview Algorithms
- **Binary search**: Algorithm choice reasoning
- **Prime algorithms**: Sieve-inspired primality testing
- **String algorithms**: Palindrome detection, anagram finding
- **Array operations**: Sorting, filtering, manipulation
- **Mathematical series**: Closed-form calculations

### Phase 4: Production Features
- **Logging**: Professional application logging
- **Configuration**: Externalized configuration management
- **Error recovery**: Graceful error handling and recovery
- **Performance**: Parallel processing for scalability
- **CLI interfaces**: Professional command-line application
- **Build automation**: sbt for dependency management
- **Testing**: Structure for unit and integration tests

## 📈 Performance Considerations

- **Parallel processing**: Automatically uses parallel collections for large datasets
- **Memory efficiency**: Streams data instead of loading entire files into memory
- **Algorithm optimization**: Uses most efficient algorithms for each operation
- **Logging**: Configurable logging levels for performance monitoring

## 🧪 Testing

The project includes a foundation for comprehensive testing:

```bash
# Run tests
sbt test

# Run with coverage (if added)
sbt coverage test coverageReport
```

## 🎯 Learning Outcomes

### Technical Skills Applied
1. **Command-line application development** with proper argument parsing
2. **File I/O operations** with error handling
3. **Algorithm implementation** in production code
4. **Configuration management** with type safety
5. **Logging and monitoring** for production applications
6. **Performance optimization** with parallel processing

### Architecture Decisions
1. **Functional core**: Pure functions for algorithms
2. **Imperative shell**: Side effects isolated (file I/O, logging)
3. **Type safety**: Strong typing throughout the application
4. **Error handling**: Comprehensive error recovery and reporting
5. **Modularity**: Clear separation of concerns

## 🔄 Extension Ideas

### Additional Operations
- **JSON processing**: Parse and analyze JSON data files
- **CSV transformations**: ETL operations on structured data
- **Image processing**: Basic image manipulation algorithms
- **Network operations**: HTTP client for web APIs
- **Database integration**: SQLite or JDBC connections

### Performance Enhancements
- **Akka actors**: Distributed processing across multiple cores
- **Spark integration**: Big data processing for large files
- **Caching**: Memoization for repeated computations
- **Streaming**: Process large files without loading into memory

### Enterprise Features
- **Configuration files**: YAML/JSON configuration loading
- **Metrics**: Application performance monitoring
- **Security**: Input validation and sanitization
- **Deployment**: Docker containerization

---

*"Learning programming is like learning martial arts. Theory is important, but practice makes perfect. This project demonstrates how theory becomes real applications."*

**Build this application, understand every line of code, then extend it with your own features!** 🚀
