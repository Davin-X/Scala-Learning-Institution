// CLI Application demonstrating Scala concepts from all phases
// Features: Command-line args, file I/O, algorithms, configuration, logging

import scala.io.Source
import java.io.{File, PrintWriter}
import com.typesafe.scalalogging.LazyLogging
import scala.util.{Try, Success, Failure}
import scala.collection.parallel.CollectionConverters._

object FileProcessorApp extends App with LazyLogging {

  // Configuration case class
  case class Config(
    inputFile: String = "",
    outputFile: String = "",
    operation: String = "stats",
    parallel: Boolean = false,
    help: Boolean = false
  )

  // Command-line argument parsing using scopt (from config)
  val parser = new scopt.OptionParser[Config]("file-processor") {
    head("Scala File Processor CLI", "1.0")

    opt[String]('i', "input")
      .required()
      .valueName("<file>")
      .action((x, c) => c.copy(inputFile = x))
      .text("Input file to process")

    opt[String]('o', "output")
      .valueName("<file>")
      .action((x, c) => c.copy(outputFile = x))
      .text("Output file (optional)")

    opt[String]('op', "operation")
      .valueName("<operation>")
      .action((x, c) => c.copy(operation = x))
      .validate {
        case "stats" | "analyze" | "prime-filter" | "palindrome-check" => success
        case _ => failure("Operation must be: stats, analyze, prime-filter, or palindrome-check")
      }
      .text("Operation to perform: stats, analyze, prime-filter, palindrome-check")

    opt[Unit]('p', "parallel")
      .action((_, c) => c.copy(parallel = true))
      .text("Process in parallel")

    help("help")
      .text("Display help information")

    note("\nExamples:")
    note("  file-processor -i data.txt -op stats")
    note("  file-processor -i numbers.csv -op prime-filter -o primes.txt --parallel")
  }

  // Parse command line arguments
  parser.parse(args, Config()) match {
    case Some(config) =>
      if (config.help) {
        // Help is automatically displayed by scopt
        System.exit(0)
      }

      // Validate input file exists
      val inputFile = new File(config.inputFile)
      if (!inputFile.exists()) {
        logger.error(s"Input file does not exist: ${config.inputFile}")
        System.exit(1)
      }

      logger.info(s"Processing file: ${config.inputFile}")
      logger.info(s"Operation: ${config.operation}")
      logger.info(s"Parallel mode: ${config.parallel}")

      // Execute operation based on configuration
      processFile(config)

    case None =>
      // Parsing failed, error message displayed by scopt
      System.exit(1)
  }

  // Main file processing logic using concepts from all phases
  def processFile(config: Config): Unit = {
    val startTime = System.currentTimeMillis()
    logger.info("Starting file processing...")

    Try {
      val lines = Source.fromFile(config.inputFile).getLines().toList
      logger.info(s"Loaded ${lines.length} lines from file")

      val result = config.operation match {
        case "stats" =>
          logger.info("Computing text statistics...")
          computeTextStats(lines)

        case "analyze" =>
          logger.info("Performing text analysis...")
          analyzeText(lines)

        case "prime-filter" =>
          logger.info("Filtering prime numbers...")
          filterPrimes(lines, config.parallel)

        case "palindrome-check" =>
          logger.info("Checking palindromes...")
          checkPalindromes(lines, config.parallel)

        case _ =>
          throw new IllegalArgumentException(s"Unknown operation: ${config.operation}")
      }

      // Output results
      config.outputFile match {
        case "" =>
          // Print to console
          println(result)

        case outputPath =>
          // Write to file
          val writer = new PrintWriter(new File(outputPath))
          try {
            writer.write(result)
            logger.info(s"Results written to: $outputPath")
          } finally {
            writer.close()
          }
      }

      val duration = System.currentTimeMillis() - startTime
      logger.info(f"Processing completed in ${duration}ms")

    } match {
      case Success(_) =>
        logger.info("File processing completed successfully")

      case Failure(e) =>
        logger.error(s"Error processing file: ${e.getMessage}", e)
        System.exit(1)
    }
  }

  // Text statistics using basic Scala collections (Phase 1 concepts)
  def computeTextStats(lines: List[String]): String = {
    val allText = lines.mkString("\\n")
    val words = allText.split("\\s+").filter(_.nonEmpty)
    val chars = allText.length
    val linesCount = lines.length
    val wordCount = words.length
    val avgWordsPerLine = if (linesCount > 0) wordCount.toDouble / linesCount else 0.0

    s"""=== TEXT STATISTICS ===
       |Total characters: $chars
       |Total words: $wordCount
       |Total lines: $linesCount
       |Average words per line: ${avgWordsPerLine("%.2f")}
       |Longest line: ${lines.map(_.length).maxOption.getOrElse(0)} characters
       |Shortest line: ${lines.map(_.length).minOption.getOrElse(0)} characters
       |Unique words: ${words.distinct.length}
       |Most frequent word: ${findMostFrequent(words)}
       |""".stripMargin
  }

  def findMostFrequent(words: Array[String]): String = {
    words.groupBy(identity)
         .map { case (word, occurrences) => (word, occurrences.length) }
         .maxByOption(_._2)
         .map(_._1)
         .getOrElse("N/A")
  }

  // Text analysis using advanced Scala features (Phase 2 concepts)
  def analyzeText(lines: List[String]): String = {
    val text = lines.mkString(" ").toLowerCase

    // Find anagrams using functional programming
    val words = text.split("\\s+")
                   .filter(_.length > 2)
                   .filter(_.forall(_.isLetter))
                   .distinct

    val sortedWords = words.groupBy(_.sorted)
    val anagramGroups = sortedWords.filter(_._2.length > 1)

    // Find palindromes using string algorithms
    val palindromes = words.filter(isPalindromicWord)

    // Character frequency analysis
    val charFreq = text.filter(_.isLetter)
                       .groupBy(identity)
                       .map { case (char, list) => (char, list.length) }
                       .toList.sortBy(-_._2)

    s"""=== TEXT ANALYSIS ===
       |Total unique words: ${words.length}
       |Anagram groups found: ${anagramGroups.size}
       |Sample anagrams: ${anagramGroups.take(3).map(_._2.mkString(", ")).mkString("; ")}
       |Palindromic words: ${palindromes.take(5).mkString(", ")}
       |Character frequency: ${charFreq.take(5).map{case (c,f)=>s"$c:$f"}.mkString(", ")}
       |Vowel to consonant ratio: ${calculateVowelRatio(text)}
       |""".stripMargin
  }

  def isPalindromicWord(word: String): Boolean = {
    word == word.reverse
  }

  def calculateVowelRatio(text: String): String = {
    val vowels = "aeiou"
    val totalLetters = text.count(_.isLetter)
    if (totalLetters == 0) return "N/A"

    val vowelCount = text.count(c => vowels.contains(c.toLower))
    val consonants = totalLetters - vowelCount
    if (con consonants == 0) "All vowels" else s"V:C = $vowelCount:$consonants"
  }

  // Prime filtering using number theory algorithms (Phase 3 concepts)
  def filterPrimes(lines: List[String], parallel: Boolean): String = {
    val numbers = extractNumbers(lines).distinct.sortWith(parallel)(_ < _)

    val primes = if (parallel && numbers.length > 1000) {
      logger.info("Using parallel processing for prime filtering")
      numbers.par.filter(isPrime).toList
    } else {
      numbers.filter(isPrime)
    }

    s"""=== PRIME NUMBER ANALYSIS ===
       |Total numbers found: ${numbers.length}
       |Prime numbers: ${primes.length}
       |Largest prime: ${primes.lastOption.getOrElse("None")}
       |Prime numbers: ${primes.mkString(", ")}
       |""".stripMargin
  }

  def extractNumbers(lines: List[String]): List[Int] = {
    lines.flatMap(_.split("\\s+"))
         .filter(_.forall(_.isDigit))
         .map(_.toIntOption)
         .flatten
  }

  // Optimized primality test using Sieve concepts
  def isPrime(n: Int): Boolean = {
    if (n <= 1) return false
    if (n <= 3) return true
    if (n % 2 == 0 || n % 3 == 0) return false

    var i = 5
    while (i * i <= n) {
      if (n % i == 0 || n % (i + 2) == 0) return false
      i += 6
    }
    true
  }

  // Palindrome checking using string algorithms (Phase 3 concepts)
  def checkPalindromes(lines: List[String], parallel: Boolean): String = {
    val cleanLines = lines.map(_.filter(_.isLetterOrDigit).toLowerCase)

    val (palindromes, nonPalindromes) = if (parallel) {
      val grouped = cleanLines.par.partition(isPalindrome)
      (grouped._1.toList, grouped._2.toList)
    } else {
      cleanLines.partition(isPalindrome)
    }

    s"""=== PALINDROME ANALYSIS ===
       |Total lines analyzed: ${lines.length}
       |Palindromic phrases: ${palindromes.length}
       |Non-palindromic phrases: ${nonPalindromes.length}
       |
       |Palindromes found:
       |${palindromes.take(10).mkString("\\n")}
       |""".stripMargin
  }

  // Clean palindrome check with case/character normalization
  def isPalindrome(text: String): Boolean = {
    val cleaned = text.filter(_.isLetterOrDigit).toLowerCase
    cleaned == cleaned.reverse
  }
}
