# 🏗️ Spark System Design Interview Questions

**Master System Design & Architecture Questions for Spark Roles**

**Prerequisites**: All core modules (01-05) + Coding Questions

**Estimated time**: 60-90 minutes

---

## 🎯 System Design Question Categories

This guide covers the most important system design questions for Spark engineering roles:

- ✅ **Data Pipeline Architecture**: ETL, streaming, batch processing
- ✅ **Scalability & Performance**: Handling large datasets, optimization
- ✅ **Fault Tolerance & Reliability**: Error handling, recovery strategies
- ✅ **Data Lake Design**: Bronze/silver/gold architecture
- ✅ **Real-time Analytics**: Streaming architecture and patterns
- ✅ **ML Pipeline Design**: Production machine learning systems

---

## 🏗️ Question 1: Large-Scale ETL Pipeline Design

### **Scenario:**
Design an ETL pipeline that processes 10TB of daily log data from multiple sources (web servers, mobile apps, IoT devices) and loads it into a data lake for analytics.

### **Requirements:**
- Process data in near real-time (under 15 minutes latency)
- Handle schema evolution and data quality issues
- Support backfilling historical data
- Provide data lineage and monitoring
- Cost-effective storage and processing

### **Key Components to Design:**

#### **1. Data Ingestion Layer**
```
Raw Data Sources → Ingestion Layer → Landing Zone
     ↓                    ↓              ↓
Web Logs         Kafka/Flume     S3/ADLS Raw
Mobile Events    Event Hubs      Parquet Raw
IoT Sensors      MQTT/Kinesis    JSON Raw
```

**Design Decisions:**
- **Ingestion Method**: Kafka for high-throughput, Kinesis for serverless
- **Buffering Strategy**: Time-based windows (5-10 min) vs size-based
- **Data Format**: Keep raw format for flexibility, add metadata

#### **2. Processing Layer (Bronze → Silver)**
```
Landing Zone → Bronze Layer → Silver Layer
     ↓             ↓              ↓
Raw Files     Cleaned Data   Business Ready
JSON/CSV      Parquet        Optimized Schema
No Schema     Inferred       Curated Tables
```

**Spark Implementation:**
```scala
// Bronze Layer: Raw data cleaning
val bronzeDF = spark.read
  .option("multiline", "true")
  .json("s3://landing-zone/*")
  .withColumn("ingestion_timestamp", current_timestamp())
  .withColumn("data_quality_score", 
    when(col("user_id").isNotNull && col("event_type").isNotNull, "high")
    .when(col("user_id").isNotNull, "medium")
    .otherwise("low")
  )

// Silver Layer: Business transformations
val silverDF = bronzeDF
  .filter(col("data_quality_score") =!= "low")
  .withColumn("event_date", to_date(col("event_timestamp")))
  .withColumn("user_segment", 
    when(col("revenue") > 100, "high_value")
    .when(col("revenue") > 10, "medium_value")
    .otherwise("low_value")
  )
  .repartition(col("event_date"))  // Partition by date
```

#### **3. Storage & Optimization**
- **File Format**: Delta Lake for ACID transactions and time travel
- **Partitioning**: Date-based partitioning for query performance
- **Compression**: Snappy for balance of speed and compression
- **Caching**: Cache frequently accessed dimension tables

#### **4. Monitoring & Alerting**
- **Metrics**: Processing latency, data quality scores, error rates
- **Alerts**: SLA breaches, data quality degradation, system failures
- **Dashboards**: Real-time pipeline health monitoring

### **Scalability Considerations:**
- **Horizontal Scaling**: Auto-scale clusters based on data volume
- **Data Skew Handling**: Salting techniques for hot keys
- **Checkpointing**: Regular checkpoints for failure recovery
- **Resource Optimization**: Dynamic allocation, spot instances

### **Cost Optimization:**
- **Storage Tiering**: Hot data in SSD, cold data in HDD/object storage
- **Compute Optimization**: Use serverless when possible, reserved instances for steady workloads
- **Data Lifecycle**: Automatic archival and deletion policies

---

## 📊 Question 2: Real-Time Analytics Platform

### **Scenario:**
Design a real-time analytics platform that processes user clickstream data to provide real-time dashboards and recommendations.

### **Requirements:**
- Process 1M events/second with <5 second latency
- Handle user sessions and real-time personalization
- Provide real-time dashboards and alerts
- Scale to handle traffic spikes (10x normal load)
- Ensure exactly-once processing

### **Architecture Design:**

#### **1. Data Ingestion & Streaming**
```
User Events → Load Balancer → Kafka Cluster → Spark Streaming
     ↓             ↓              ↓              ↓
Web/Mobile     Nginx/HAProxy   Topics         Micro-batches
IoT Sensors   Auto-scaling    Partitions     Windowed aggregations
```

**Spark Streaming Implementation:**
```scala
val spark = SparkSession.builder()
  .appName("RealTimeAnalytics")
  .config("spark.sql.adaptive.enabled", "true")
  .getOrCreate()

// Read from Kafka
val kafkaDF = spark.readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "kafka-cluster:9092")
  .option("subscribe", "user_events")
  .option("startingOffsets", "latest")
  .load()

// Parse JSON events
val eventsDF = kafkaDF.select(
  from_json(col("value").cast("string"), eventSchema).as("event")
).select("event.*")

// Session window aggregation
val sessionStats = eventsDF
  .withWatermark("timestamp", "10 minutes")
  .groupBy(
    session_window(col("timestamp"), "30 minutes"),
    col("user_id")
  )
  .agg(
    count("*").as("page_views"),
    countDistinct("page_url").as("unique_pages"),
    sum("revenue").as("total_revenue")
  )
```

#### **2. State Management**
- **Session State**: Maintain user sessions with timeout
- **Deduplication**: Exactly-once processing with idempotent keys
- **State Store**: RocksDB for large state management
- **Checkpointing**: HDFS/S3 for fault tolerance

#### **3. Real-Time Features**
- **Sliding Windows**: Moving averages for trend detection
- **Complex Event Processing**: Pattern detection and alerting
- **Real-Time Joins**: Enrich events with user profiles
- **Dynamic Dashboards**: Auto-refreshing metrics

#### **4. Scalability & Reliability**
- **Cluster Sizing**: Calculate based on throughput requirements
- **Auto-scaling**: Scale based on lag and throughput metrics
- **Multi-region**: Cross-region replication for disaster recovery
- **Circuit Breakers**: Graceful degradation under load

---

## 🏭 Question 3: Data Lake Architecture Design

### **Scenario:**
Design a modern data lake that serves multiple teams (Data Science, Analytics, ML Engineering) with different data access patterns and requirements.

### **Requirements:**
- Support structured, semi-structured, and unstructured data
- Provide different data quality levels (Bronze/Silver/Gold)
- Enable self-service analytics and data discovery
- Support real-time and batch processing
- Implement proper governance and security

### **Architecture Layers:**

#### **1. Bronze Layer (Raw Data)**
```
Data Sources → Ingestion → Bronze Layer
     ↓            ↓           ↓
APIs         Kafka/EventHubs  Raw Parquet
Databases    Batch Jobs       Schema-on-Read
Files        Streaming        With Metadata
```

**Bronze Layer Design:**
- **Storage**: Delta Lake for ACID transactions
- **Schema**: Flexible schema evolution
- **Partitioning**: By ingestion date and source
- **Retention**: Keep raw data for compliance

#### **2. Silver Layer (Cleaned & Enriched)**
```
Bronze Layer → Processing → Silver Layer
     ↓            ↓            ↓
Raw Data      ETL Jobs      Business Entities
Inconsistent   Validation   Consistent Schema
Duplicates    Deduplication Normalized Tables
```

**Silver Layer Implementation:**
```scala
// Data quality validation
val validatedDF = bronzeDF
  .filter(col("user_id").isNotNull)
  .filter(col("event_timestamp").isNotNull)
  .withColumn("data_quality_check",
    when(col("required_field").isNull, "invalid")
    .otherwise("valid")
  )

// Deduplication by business key
val deduplicatedDF = validatedDF
  .withColumn("row_num", row_number().over(
    Window.partitionBy("business_key")
      .orderBy(col("ingestion_timestamp").desc)
  ))
  .filter(col("row_num") === 1)
  .drop("row_num")
```

#### **3. Gold Layer (Aggregated & Optimized)**
```
Silver Layer → Aggregation → Gold Layer
     ↓            ↓            ↓
Clean Data     Analytics      Business Metrics
Granular       Roll-ups       Optimized for Query
Transactional  Summary        Pre-computed Joins
```

**Gold Layer Features:**
- **Star Schema**: Fact and dimension tables
- **Pre-aggregated**: Common metrics pre-computed
- **Optimized**: Columnar storage, indexing
- **Governed**: Access controls and audit trails

#### **4. Access & Governance**
- **Data Catalog**: Centralized metadata management
- **Access Control**: Role-based permissions
- **Data Quality**: Automated quality monitoring
- **Lineage Tracking**: End-to-end data lineage

---

## 🔄 Question 4: Streaming Pipeline with Exactly-Once Semantics

### **Scenario:**
Design a streaming pipeline that processes financial transactions with exactly-once semantics and real-time fraud detection.

### **Requirements:**
- Process 100K transactions/second
- Exactly-once processing guarantee
- Real-time fraud scoring (<1 second)
- Handle out-of-order events
- Maintain transaction state for 30 days

### **Architecture Components:**

#### **1. Ingestion & Buffering**
```
Transaction Sources → Kafka → Spark Streaming → Processing
        ↓               ↓           ↓             ↓
Payment APIs       Topics     Micro-batches   Fraud Detection
Mobile Apps        Partitions Exactly-once    Risk Scoring
Web Transactions   Replication State Store   Alerts
```

#### **2. Exactly-Once Implementation**
```scala
// Idempotent processing with transaction IDs
val processedTransactions = kafkaDF
  .selectExpr("CAST(key AS STRING) as transaction_id", 
              "CAST(value AS STRING) as payload")
  .withColumn("event", from_json(col("payload"), transactionSchema))
  .select("transaction_id", "event.*")

// Deduplication using transaction ID
val deduplicatedDF = processedTransactions
  .withColumn("processing_timestamp", current_timestamp())
  .dropDuplicates("transaction_id")

// State management for fraud detection
val fraudScoredDF = deduplicatedDF
  .withWatermark("event_timestamp", "10 minutes")
  .groupBy(
    col("user_id"),
    window(col("event_timestamp"), "1 hour", "10 minutes")
  )
  .agg(
    sum("amount").as("window_total"),
    count("*").as("transaction_count"),
    avg("amount").as("avg_amount")
  )
  .withColumn("fraud_score",
    when(col("window_total") > 10000, "high")
    .when(col("transaction_count") > 20, "medium")
    .otherwise("low")
  )
```

#### **3. State Management**
- **RocksDB**: For large state storage
- **Checkpointing**: Regular state snapshots to S3/HDFS
- **TTL Management**: Automatic state cleanup
- **State Migration**: Handle schema changes

#### **4. Monitoring & Alerting**
- **Latency Metrics**: End-to-end processing time
- **Throughput Monitoring**: Events/second processing rate
- **Error Tracking**: Failed transaction handling
- **Fraud Alerts**: Real-time risk notifications

---

## 🤖 Question 5: Production ML Pipeline Design

### **Scenario:**
Design a production ML pipeline that trains and deploys recommendation models using Spark MLlib, serving 10M daily predictions.

### **Requirements:**
- Daily model retraining with fresh data
- A/B testing for model versions
- Real-time feature engineering
- Model performance monitoring
- Automated model deployment

### **Pipeline Architecture:**

#### **1. Data Preparation & Feature Engineering**
```
Raw Data → Feature Store → Training Data
    ↓           ↓              ↓
User Logs    Pre-computed    Labeled Examples
Product Data  Aggregations    Feature Vectors
Interactions  Statistics      Normalized Data
```

#### **2. Model Training Pipeline**
```scala
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.ml.classification._
import org.apache.spark.ml.evaluation._

// Feature engineering
val featurePipeline = new Pipeline().setStages(Array(
  new StringIndexer().setInputCol("category").setOutputCol("category_index"),
  new OneHotEncoder().setInputCols(Array("category_index")).setOutputCols(Array("category_vec")),
  new VectorAssembler().setInputCols(Array("user_features", "item_features", "category_vec"))
    .setOutputCol("features")
))

// Model training
val lr = new LogisticRegression()
  .setFeaturesCol("features")
  .setLabelCol("label")
  .setMaxIter(100)

// Complete pipeline
val pipeline = new Pipeline().setStages(Array(featurePipeline, lr))

// Train model
val model = pipeline.fit(trainingData)

// Evaluate
val predictions = model.transform(testData)
val evaluator = new BinaryClassificationEvaluator()
val auc = evaluator.evaluate(predictions)
```

#### **3. Model Deployment & Serving**
- **Model Registry**: MLflow for model versioning and metadata
- **A/B Testing**: Route traffic between model versions
- **Real-time Serving**: Integration with REST APIs
- **Batch Scoring**: Offline prediction pipelines

#### **4. Monitoring & Retraining**
- **Model Drift Detection**: Monitor prediction distribution changes
- **Performance Metrics**: Accuracy, precision, recall tracking
- **Automated Retraining**: Trigger based on performance degradation
- **Feedback Loop**: Incorporate user feedback for model improvement

---

## 🎯 Interview Preparation Strategy

### **Technical Depth Requirements:**
- **Architecture Decisions**: Explain WHY you chose certain approaches
- **Trade-off Analysis**: Performance vs cost vs complexity
- **Scalability Planning**: How system grows with data/user load
- **Failure Scenarios**: How system handles various failure modes

### **Key Evaluation Criteria:**
- **Requirements Analysis**: Understanding business needs
- **Technology Selection**: Choosing appropriate tools
- **Architecture Design**: System decomposition and interactions
- **Performance Optimization**: Efficiency and scalability
- **Operational Excellence**: Monitoring, deployment, maintenance

### **Common Pitfalls to Avoid:**
- **Over-engineering**: Simple solutions for simple problems
- **Under-engineering**: Not planning for growth
- **Ignoring Constraints**: Cost, performance, operational requirements
- **Single Points of Failure**: No redundancy or failover planning

### **Preparation Tips:**
1. **Practice Whiteboarding**: Draw architecture diagrams
2. **Know Your Tools**: Deep understanding of Spark ecosystem
3. **Study Real Systems**: Learn from Netflix, Uber, Airbnb architectures
4. **Focus on Trade-offs**: Every decision has pros and cons
5. **Communicate Clearly**: Explain complex concepts simply

**Remember**: System design interviews test your ability to design scalable, reliable, and maintainable systems under constraints. Focus on justifying your design decisions!

**🚀 Master these patterns and you'll excel in senior Spark engineering interviews!**
