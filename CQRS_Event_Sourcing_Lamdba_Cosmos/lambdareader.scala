// Databricks notebook source
// Import Libraries
import com.microsoft.azure.cosmosdb.spark._
import com.microsoft.azure.cosmosdb.spark.schema._
import com.microsoft.azure.cosmosdb.spark.config.Config
import org.codehaus.jackson.map.ObjectMapper
import com.microsoft.azure.cosmosdb.spark.streaming._
import java.time._


// Configure connection to Azure Cosmos DB Change Feed
val sourceConfigMap = Map(
"Endpoint" -> "",
"Masterkey" -> "",
"Database" -> "lambdadb",
"Collection" -> "lambdacollection",
"ConnectionMode" -> "Gateway",
"ChangeFeedCheckpointLocation" -> "/changefeedcheckpointlocation",
"changefeedqueryname" -> "Streaming Query from Cosmos DB Change Feed Interval Count")

// COMMAND ----------

var tweets = spark.read.format("com.microsoft.azure.cosmosdb.spark").options(sourceConfigMap).load()
tweets.count()
tweets.createOrReplaceTempView("tweets")


// COMMAND ----------

// MAGIC %sql
// MAGIC select concat(concat((dense_rank() OVER (PARTITION BY 1 ORDER BY tweets DESC)-1), '. '), text) as hashtags, tweets
// MAGIC from (
// MAGIC select hashtags.text, count(distinct id) as tweets
// MAGIC from (
// MAGIC select 
// MAGIC   explode(entities.hashtags) as hashtags,
// MAGIC   id
// MAGIC from tweets
// MAGIC ) a
// MAGIC group by hashtags.text
// MAGIC order by tweets desc
// MAGIC limit 10
// MAGIC ) b

// COMMAND ----------


