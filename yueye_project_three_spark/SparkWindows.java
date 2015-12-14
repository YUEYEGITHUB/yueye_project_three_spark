import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.examples.streaming.StreamingExamples

object SparkWindows {
  def main(args: Array[String]) {
    if (args.length < 4) {
          System.err.println("Please input 4 arguments ")
      System.exit(1)
    }

    StreamingExamples.setStreamingLogLevels()

    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkConf = new SparkConf()
      .setMaster("spark://sy-0225:7077")
      .setAppName("SparkWindows")
      .setSparkHome("/usr/lib/spark")
      .setJars(SparkContext.jarOfClass(this.getClass).toList)
    val ssc =  new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint(".")

    val topicpMap = topics.split(",").map((_,numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, sy-0225, testGroup, yueye_topic).map(_._2)
    val findCount = lines.flatMap(_.split("\t")(2)).map((_,1)).reduceByKeyAndWindow(_ + _,_ - _,Seconds(10),Seconds(5))
      .map{case (char,count) => (count,char)}.transform(_.sortByKey(false)).map{case (char,count) => (count,char)}

    findCount.print(10)
    ssc.start()
    ssc.awaitTermination()
  }
}

