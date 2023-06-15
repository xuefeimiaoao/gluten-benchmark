package benchmark.gluten

import com.databricks.spark.sql.perf.tpch._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


object TpchDataGenParquet {
  def main(args: Array[String]): Unit = {
    var scaleFactor = "100" // scaleFactor defines the size of the dataset to generate (in GB).
    var numPartitions = 200 // how many dsdgen partitions to run - number of input tasks.

    val format = "parquet" // valid spark format like parquet "parquet".
    var rootDir = "/cloudgpfs/dataforge/ml-studio/yckj4506/data/dataset/benchmark-gluten/tpch_parquet_path" // root directory of location to create data in.
    var dbgenDir = "/cloudgpfs/dataforge/ml-studio/yckj4506/data/dataset/benchmark-gluten/tpch_dbgen" // location of dbgen

    if (args.length > 0) scaleFactor = args(0)
    if (args.length > 1) numPartitions = args(1).toInt
    if (args.length > 2) rootDir = args(2)
    if (args.length > 3) dbgenDir = args(3)

    val conf = new SparkConf()
    val builder = SparkSession.builder().config(conf)
    val spark = builder.getOrCreate()

    val tables = new TPCHTables(spark.sqlContext,
      dbgenDir = dbgenDir,
      scaleFactor = scaleFactor,
      useDoubleForDecimal = false, // true to replace DecimalType with DoubleType
      useStringForDate = false) // true to replace DateType with StringType


    tables.genData(
      location = rootDir,
      format = format,
      overwrite = true, // overwrite the data that is already there
      partitionTables = false, // do not create the partitioned fact tables
      clusterByPartitionColumns = false, // shuffle to get partitions coalesced into single files.
      filterOutNullPartitionValues = false, // true to filter out the partition with NULL key value
      tableFilter = "", // "" means generate all tables
      numPartitions = numPartitions) // how many dsdgen partitions to run - number of input tasks.
  }
}
