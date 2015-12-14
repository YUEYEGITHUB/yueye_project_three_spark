import org.apache.spark.examples.streaming.StreamingExamples
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaToSpark {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("please intpu 4 arguments ")
      System.exit(1)
    }

    StreamingExamples.setStreamingLogLevels()

    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkConf = new SparkConf()
      .setMaster("spark://sy-0225:7077")
      .setAppName("KafkaToSpark")
      .setSparkHome("/usr/lib/spark")
      .setJars(SparkContext.jarOfClass(this.getClass).toList)
    val ssc =  new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint(".")

    val updateFunc = (values: Seq[Int], state: Option[Int]) => {
      val currentCount = values.foldLeft(0)(_ + _)
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }

    val topicpMap = topics.split(",").map((_,numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, sy-0225, testGroup, topicpMap).map(_._2)
    val findCount = lines.flatMap(_.split("\t")(2)).map((_,1)).updateStateByKey[Int](updateFunc)
      .map{case (char,count) => (count,char)}.transform(_.sortByKey(false)).map{case (char,count) => (count,char)}

    findCount.print(10)
    ssc.start()
    ssc.awaitTermination()
  }
}

