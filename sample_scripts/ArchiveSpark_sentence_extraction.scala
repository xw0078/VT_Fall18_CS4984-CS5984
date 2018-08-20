import de.l3s.archivespark._
import de.l3s.archivespark.implicits._
import de.l3s.archivespark.enrich.functions._
import de.l3s.archivespark.specific.warc.implicits._
import de.l3s.archivespark.specific.warc.specs.WarcCdxHdfsSpec
import de.l3s.archivespark.specific.warc.specs.WarcHdfsSpec
import de.l3s.archivespark.specific.warc.enrichfunctions._

import java.io.{FileOutputStream, PrintStream,FileInputStream,PrintWriter}
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
import org.apache.spark.SparkFiles
import org.apache.spark.sql.{Row, SparkSession}


// load data from local file system
val warcPath = "/archive_spark/archivespark_dlrl/data/sample_data/warc" //be aware on DLRL cluster, this will be HDFS path
val cdxPath = "/archive_spark/archivespark_dlrl/data/sample_data/cdx" //be aware on DLRL cluster, this will be HDFS path
val records = ArchiveSpark.load(WarcCdxHdfsSpec(cdxPath,warcPath))

val pages = records.filter(r => r.mime == "text/html" && r.status == 200) // extract valid webpages
val earliest = pages.distinctValue(_.surtUrl) {(a, b) => if (a.time < b.time) a else b} // filter out same urls, pick the latest snap
val enriched = earliest.enrich(HtmlText) // Enrich with Htmltext

// get Array of elements: URL, timestamp, HtmlText
val extracted = enriched.map(r => {
      val text = r.valueOrElse(HtmlText, "").replaceAll("[\\t\\n]", " ")
      Array[String](r.originalUrl, r.timestamp, text)
    })

// clean html text with OpenNLP tool
// notice we use mapPartitions instead of map here: mapPartitions function apply operations in partition wise. In this case, elements are pre-distribted to different partitions, each partition will perform same operations and apply them to given records. In this particular example, the file loading and model initialization operations are heavy and can not be serialized in element wise, thus we have to use mapPartitions instead of map. 
val cleaned = extracted.mapPartitions(partition => {
      val path = SparkFiles.get("en-sent.bin") // get file path from --files option
      val en_sent = new FileInputStream(path) // read external file
      val model = new SentenceModel(en_sent) // create sentence model
      val detector = new SentenceDetectorME(model) // create sentence detector
      val regex = "[A-Za-z0-9\\,\\.\\-\\;\\:\\(\\)\\'\\& ]+\\." // regex for filtering

      partition.map(r => {
        val sentences = detector.sentDetect(r(2)).filter(_.matches(regex)).mkString(" ") // apply sentence detector
        (r(0), r(1), r(2), sentences) // return URL, timestamp,HtmlText, sentences
      })

    })

val cleaned_df = cleaned.toDF("URL", "Timestamp","HtmlText", "Sentences") // convert to DataFrame format

cleaned_df.repartition(1).write.mode("overwrite").format("json").save("/share_dir/sample_output/sentences") // export data to your local path;be aware on DLRL cluster, this will be HDFS path


