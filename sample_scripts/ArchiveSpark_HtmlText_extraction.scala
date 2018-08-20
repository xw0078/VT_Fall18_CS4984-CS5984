import de.l3s.archivespark._
import de.l3s.archivespark.implicits._
import de.l3s.archivespark.enrich.functions._
import de.l3s.archivespark.specific.warc.implicits._
import de.l3s.archivespark.specific.warc.specs.WarcCdxHdfsSpec
import de.l3s.archivespark.specific.warc.specs.WarcHdfsSpec
import de.l3s.archivespark.specific.warc.enrichfunctions._


// load data from local file system
val warcPath = "/archive_spark/archivespark_dlrl/data/sample_data/warc" // be aware on DLRL cluster, this will be HDFS path
val cdxPath = "/archive_spark/archivespark_dlrl/data/sample_data/cdx" // be aware on DLRL cluster, this will be HDFS path
val records = ArchiveSpark.load(WarcCdxHdfsSpec(cdxPath,warcPath))

val pages = records.filter(r => r.mime == "text/html" && r.status == 200) // extract valid webpages
val earliest = pages.distinctValue(_.surtUrl) {(a, b) => if (a.time < b.time) a else b} // filter out same urls, pick the latest snap
val enriched = earliest.enrich(HtmlText) // Enrich with Htmltext

val Terms = LowerCase.of(HtmlText).mapMulti("terms") { text: String => text.split("\\W+").distinct } // Define Terms enrichment within HTML body
val Title = HtmlText.of(Html.first("title")) // Define Title enrichment within HTML body
val enriched = earliest.enrich(Terms).enrich(Title) // Enrich with Terms and Title

// get desired information from the dataset
// map function applies all operations inside in element wise. In this case, all elements will be distributed for parallel computation.
val result = enriched.map( r => {
    val title = r.valueOrElse(Title, "").replaceAll("[\\t\\n]", " ") // get title value 
    val text = r.valueOrElse(HtmlText, "").replaceAll("[\\t\\n]", " ") // get text value
    // concatenate URL, timestamp, title and text with in the format of tuple, tuple can be converted to Dataframe format later
    (r.originalUrl, r.timestamp,title, text)
})

val result_df = result.toDF("originalUrl","timestamp","title","text") // convert to DataFrame format inorder to export Json format and perform SQL queries as needed

result_df.repartition(1).write.mode("overwrite").format("json").save("/shared_dir/sample_output/html_text_raw") // export data to your local path;be aware on DLRL cluster, this will be HDFS path

