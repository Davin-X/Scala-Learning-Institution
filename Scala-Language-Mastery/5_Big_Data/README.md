# ⚡ Big Data with Scala & Apache Spark

## Phase 6: Distributed Computing & Big Data Processing

**Prerequisites**: Complete Beginner, Intermediate, and Interview Preparation phases

**Duration**: 3-4 weeks

---

## 🎯 Learning Goals

- Master distributed computing concepts
- Understand Apache Spark architecture and principles
- Implement data processing pipelines with Spark
- Handle large-scale data transformations
- Optimize performance for big data workloads
- Deploy and monitor Spark applications

---

## 📋 Curriculum Overview

### **6A: Distributed Systems Fundamentals**
- Distributed computing concepts
- Parallel processing patterns
- Data partitioning and distribution
- Fault tolerance and resilience
- Scalability principles

### **6B: Apache Spark Core**
- Spark architecture and components
- RDD (Resilient Distributed Dataset) basics
- Transformations and actions
- Spark execution model
- Memory management and optimization

### **6C: DataFrames & Spark SQL**
- Structured data processing with DataFrames
- SQL queries in Spark
- Schema management and data types
- Performance optimization
- Integration with data sources

### **6D: Advanced Spark**
- Streaming data processing
- Machine learning with MLlib
- Graph processing with GraphX
- Custom transformations and UDFs
- Spark application deployment

---

## 🏗️ Spark Architecture Overview

```scala
// Spark Application Structure
import org.apache.spark.sql.SparkSession

val spark = SparkSession.builder()
  .appName("Scala Data Processing")
  .config("spark.master", "local[*]")
  .getOrCreate()

// RDD Operations
val data = spark.sparkContext.parallelize(List(1, 2, 3, 4, 5))
val doubled = data.map(_ * 2)  // Transformation
val result = doubled.collect() // Action

// DataFrame Operations
val df = spark.read.json("data.json")
df.groupBy("category").count().show()
```

---

## 🚀 Key Concepts in Spark

### **1. RDD (Resilient Distributed Dataset)**
```scala
// Creating RDDs
val numbersRDD = spark.sparkContext.parallelize(1 to 1000)
val fileRDD = spark.sparkContext.textFile("input.txt")

// Transformations (lazy)
val evenNumbers = numbersRDD.filter(_ % 2 == 0)
val doubled = evenNumbers.map(_ * 2)

// Actions (trigger execution)
val count = doubled.count()  // Long
val firstFive = doubled.take(5)  // Array[Int]
```

### **2. DataFrames & Spark SQL**
```scala
import spark.implicits._

// Creating DataFrames
val df = Seq(("Alice", 25), ("Bob", 30), ("Charlie", 35)).toDF("name", "age")

// SQL Queries
df.createOrReplaceTempView("people")
val result = spark.sql("SELECT name FROM people WHERE age > 26")

// Functional operations
df.filter($"age" > 26)
  .select($"name", $"age" * 2 as "doubled_age")
  .show()
```

### **3. Working with Different Data Sources**
```scala
// CSV
val csvDF = spark.read
  .option("header", "true")
  .option("inferSchema", "true")
  .csv("data.csv")

// JSON
val jsonDF = spark.read.json("data.json")

// Parquet (columnar format for analytics)
val parquetDF = spark.read.parquet("data.parquet")

// Writing data
jsonDF.write.mode("overwrite").parquet("processed_data.parquet")
csvDF.write.mode("append").json("output.json")
```

---

## 📊 Data Processing Patterns

### **ETL Pipeline Example**
```scala
object ETLPipeline {
  def run(inputPath: String, outputPath: String)(implicit spark: SparkSession): Unit = {

    // Extract: Read from multiple sources
    val ordersDF = spark.read.json(s"$inputPath/orders/*.json")
    val productsDF = spark.read.csv(s"$inputPath/products/products.csv")
    val customersDF = spark.read.parquet(s"$inputPath/customers/")

    // Transform: Join and process data
    val enrichedOrders = ordersDF
      .join(productsDF, "product_id")
      .join(customersDF, "customer_id")
      .withColumn("total_value", $"quantity" * $"price")
      .withColumn("processed_at", current_timestamp())

    // Load: Write to output
    enrichedOrders.write
      .mode("overwrite")
      .partitionBy("order_date", "region")
      .parquet(outputPath)
  }
}
```

### **Streaming Data Processing**
```scala
import org.apache.spark.sql.streaming._

val spark = SparkSession.builder()
  .appName("StreamingProcessor")
  .getOrCreate()

// Read streaming data (Kafka, files, etc.)
val streamingDF = spark.readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "localhost:9092")
  .option("subscribe", "events")
  .load()

// Process streaming data
val processedStream = streamingDF
  .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
  .withColumn("event_time", current_timestamp())
  .groupBy(window($"event_time", "10 minutes"), $"key")
  .count()

// Write to output sink
val query = processedStream.writeStream
  .outputMode("complete")
  .format("console")
  .start()

query.awaitTermination()
```

---

## ⚙️ Performance Optimization

### **Memory Management**
```scala
// Configure memory settings
val spark = SparkSession.builder()
  .config("spark.driver.memory", "4g")
  .config("spark.executor.memory", "8g")
  .config("spark.memory.offHeap.enabled", "true")
  .config("spark.memory.offHeap.size", "2g")
  .getOrCreate()
```

### **Caching Strategies**
```scala
// Cache frequently used data
val expensiveComputation = dataFrame
  .groupBy("category")
  .agg(sum("revenue"), avg("quantity"))

expensiveComputation.cache()  // Keep in memory
expensiveComputation.count()  // Triggers caching

// Persist with different storage levels
import org.apache.spark.storage.StorageLevel
expensiveComputation.persist(StorageLevel.MEMORY_AND_DISK)
// Or: MEMORY_ONLY, DISK_ONLY, MEMORY_ONLY_SER, etc.
```

### **Partitioning & Shuffling**
```scala
// Avoid unnecessary shuffling
df.repartition($"category")  // Repartition for specific column

// Use broadcast joins for small tables
val countriesDF = spark.read.csv("countries.csv")
val largeDF = spark.read.parquet("large_dataset/")

import org.apache.spark.sql.functions.broadcast
val result = largeDF.join(broadcast(countriesDF), "country_code")

// Coalesce to reduce partitions
largeDF.coalesce(10).write.parquet("output/")
```

---

## 🔍 Debugging & Monitoring

### **Spark UI & Metrics**
```scala
// Enable detailed metrics
val spark = SparkSession.builder()
  .config("spark.sql.adaptive.enabled", "true")
  .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
  .getOrCreate()

// Query execution plan
df.explain(true)  // Shows physical and logical plans

// Collect diagnostics
df.queryExecution.toString()
```

### **Error Handling in Distributed Computing**
```scala
import scala.util.{Try, Success, Failure}

// Handle exceptions in transformations
def safeTransform(data: String): String = {
  Try {
    // Your transformation logic
    processData(data)
  } match {
    case Success(result) => result
    case Failure(e) =>
      logger.warn(s"Failed to process $data: ${e.getMessage}")
      "DEFAULT_VALUE"  // Return default on failure
  }
}

// Use in Spark transformation
val cleanedData = rawRDD.map(safeTransform)

// Or filter out failures
val (validData, errors) = rawRDD.map(safeTransform).partition(_ != "ERROR")
```

---

## 📁 Project Structure

```
6_Big_Data/
├── README.md                          # This overview
├── 01_Spark_Fundamentals/             # RDDs and basic operations
├── 02_DataFrames_SQL/                 # Structured data processing
├── 03_Performance_Optimization/       # Tuning and monitoring
├── 04_Streaming_Data/                 # Real-time processing
├── 05_MLlib_Basics/                   # Machine learning foundations
├── projects/                          # Real-world examples
│   ├── etl_pipeline/                  # Complete ETL example
│   ├── streaming_analytics/           # Real-time dashboard
│   └── batch_processing/              # Large-scale data jobs
└── resources/                         # Configuration templates
```

---

## 🧪 Hands-On Projects

### **Project 1: ETL Pipeline**
- Build complete data pipeline from CSV → processing → analytics
- Handle data quality issues and missing values
- Implement incremental processing

### **Project 2: Real-Time Analytics**
- Process streaming data from Kafka
- Calculate rolling statistics and alerts
- Visualize results with simple web interface

### **Project 3: Recommendation Engine**
- Use MLlib for collaborative filtering
- Process user-item interaction data
- Generate personalized recommendations

---

## 🎯 Interview Preparation Focus

### **Common Big Data Questions:**

**System Design:**
- Design data pipeline for 1TB daily data
- Architecture for real-time recommendation system
- Distributed cache strategy for global platform

**Performance:**
- Optimize Spark job running slow on large dataset
- Choose between RDD vs DataFrame vs Dataset
- Handle data skew causing job failures

**Monitoring & Troubleshooting:**
- Diagnose memory issues in production Spark job
- Handle executor failures without data loss
- Monitor and alert on pipeline health

---

*"Big Data is not just about volume. It's about the right architecture, processing patterns, and business insights that matter to your organization."*

[← Back to Main Curriculum](../README.md)
