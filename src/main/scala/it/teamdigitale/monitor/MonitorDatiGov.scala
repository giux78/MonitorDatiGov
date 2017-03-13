package it.teamdigitale.monitor


import com.mongodb.spark._
import com.mongodb.spark.config.WriteConfig
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by ale on 09/03/17.
  */
object MonitorDatiGov {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("RDDRelation")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    val data = sqlContext.read.json("/user/admin/open_data/old_dati_gov/dati_gov.json")
    data.printSchema()

    import org.apache.spark.sql.functions.explode
    val flattenedData = data.withColumn("resources", explode($"resources"))

    val renamed = flattenedData.withColumnRenamed("_catalog_parent_name", "catalog_parent_name").withColumnRenamed("_catalog_source_url", "catalog_source_url")
    renamed.registerTempTable("dati_gov")

    val formatDist = sqlContext.sql("select count(*), resources.format from dati_gov group by resources.format")
    MongoSpark.save(formatDist)

    val formatByGroup = sqlContext.sql("select count(*), resources.format,catalog_parent_name as title from dati_gov group by resources.format, catalog_parent_name")
    val wcDitFormat = WriteConfig(Map("collection" -> "dist_format_by_group", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))
    MongoSpark.save(formatByGroup,wcDitFormat)

    val wcKo = WriteConfig(Map("collection" -> "ko", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))

    val ko = sqlContext.sql("select resources.m_status, resources.name, resources.url as rurl, url, catalog_parent_name   from dati_gov where resources.m_status = 'ko'")

    MongoSpark.save(ko,wcKo)

    val wcmStatus = WriteConfig(Map("collection" -> "m_status", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))

    val m_status = sqlContext.sql("select count(*), resources.m_status  from dati_gov group by resources.m_status")

    MongoSpark.save(m_status,wcmStatus)

    val wcmlicense = WriteConfig(Map("collection" -> "license", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))

    val license = sqlContext.sql("select count(*), license_id from dati_gov group by license_id")

    MongoSpark.save(license,wcmlicense)

    val wcmlicenseByCat = WriteConfig(Map("collection" -> "license_cat", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))

    val licenseCat = sqlContext.sql("select count(*), license_id ,catalog_parent_name as title from dati_gov group by license_id, catalog_parent_name")

    MongoSpark.save(licenseCat,wcmlicenseByCat)

    val newdata = data.withColumnRenamed("_catalog_parent_name", "catalog_parent_name")
    newdata.registerTempTable("new_data")

    val res  = sqlContext.sql("select count(*) , catalog_parent_name  from new_data group by catalog_parent_name")
    val wcmdatasetByCat = WriteConfig(Map("collection" -> "datasets", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))
    MongoSpark.save(res,wcmdatasetByCat)


    val flatGroups = data.withColumn("groups", explode($"groups"))
    val renamedGroups = flatGroups.withColumnRenamed("_catalog_parent_name", "catalog_parent_name").withColumnRenamed("_catalog_source_url", "catalog_source_url")
    renamedGroups.registerTempTable("renamed_groups")

    val resByGroup = sqlContext.sql("select count(*), catalog_parent_name, groups.title from renamed_groups group by catalog_parent_name, groups.title")
    val wcmbyGroup = WriteConfig(Map("collection" -> "dataset_by_group", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))
    MongoSpark.save(resByGroup,wcmbyGroup)

  }
}
