# 🐳 Docker Installation for Apache Spark

## Quick Start with Docker

### 1. Pull Spark Image
```bash
docker pull apache/spark:3.3.0-scala2.13
```

### 2. Run Spark Master
```bash
docker run -d --name spark-master \
  -p 7077:7077 -p 8080:8080 \
  apache/spark:3.3.0-scala2.13 \
  /opt/spark/bin/spark-class org.apache.spark.deploy.master.Master
```

### 3. Run Spark Worker
```bash
docker run -d --name spark-worker \
  --link spark-master \
  apache/spark:3.3.0-scala2.13 \
  /opt/spark/bin/spark-class org.apache.spark.deploy.worker.Worker spark://spark-master:7077
```

### 4. Access Spark UI
- Master UI: http://localhost:8080
- Worker UI: Check master UI for worker details

### 5. Run Spark Shell
```bash
docker run -it --rm \
  --link spark-master \
  apache/spark:3.3.0-scala2.13 \
  /opt/spark/bin/spark-shell --master spark://spark-master:7077
```

## Alternative: Single Node Setup

### Run Everything in One Container
```bash
docker run -it --rm \
  -p 4040:4040 \
  apache/spark:3.3.0-scala2.13 \
  /opt/spark/bin/spark-shell --master local[*]
```

## Testing Your Setup

```scala
// In Spark shell, test basic functionality
val data = Seq(1, 2, 3, 4, 5)
val rdd = sc.parallelize(data)
println(s"Sum: ${rdd.sum()}")
println(s"Count: ${rdd.count()}")
```

## Troubleshooting

### Common Issues:
1. **Port conflicts**: Change port mappings if 7077/8080 are in use
2. **Memory issues**: Add `-e SPARK_WORKER_MEMORY=2g` for more memory
3. **Network issues**: Ensure containers can communicate

### Useful Commands:
```bash
# View container logs
docker logs spark-master
docker logs spark-worker

# Stop containers
docker stop spark-master spark-worker
docker rm spark-master spark-worker
```
