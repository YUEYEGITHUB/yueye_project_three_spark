import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkContext, SparkConf}

object SparkOnHive {
  def main(args: Array[String]): Unit ={

    val sparkConf = new SparkConf()
      .setMaster("spark://sy-0217:7077")
      .setAppName("SparkOnHive")
      .setSparkHome("/usr/lib/spark")
      .setJars(SparkContext.jarOfClass(this.getClass).toList)
    val sc = new SparkContext(sparkConf)
    val hc = new HiveContext(sc)
    val rs = hc.sql("select query,sum(1) as sum from sogoulog group by query order by sum desc limit 10").foreach(println)

  }
}
