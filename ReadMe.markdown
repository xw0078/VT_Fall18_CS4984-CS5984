# Process Web Archive with ArchiveSpark

Supplemental information on ArchiveSpark for course CS4984/CS5984: Big Data Text Summarization, Fall2018, Virginia Tech.

Things you will learn about:
Github, Docker, Zeppelin, ArchiveSpark, Spark.

## Description

ArchiveSpark serves as the first (not limited to) component in your project pipeline on web archive data extraction. In this tutorial, you will learn to deploy a test environment for ArchiveSpark, test code locally and execute code on the DLRL cluster. You will also find some further information about Spark programming and NLP processing with Spark.

## Questions and Issues

If you encounter any question or issue, please check relevant documentation first. For more questions, you can create an issue in this GitHub page:

<img src="./doc/img_2.png " width="250">

## Table of Conetents
* [ArchiveSpark](#ArchiveSpark)
* [Docker: Your Local Test Environment](#Docker)
* [Zeppelin is Your Playground](#Zeppelin)
* [Real Job on Cluster](#cluster)
* [Best Practice](#best)
* [Work with PySpark](#pyspark)
* [Spark and NLP](#nlp)

## ArchiveSpark <a id="ArchiveSpark"></a>

"An Apache Spark framework for easy data processing, extraction as well as derivation for archival collections." - helgeho
[ArchiveSpark Official GitHub page](https://github.com/helgeho/ArchiveSpark)

In this class, we will utilize ArchiveSpark to process our web archive collections. We can leverage the power of ArchiveSpark in various ways: content extraction, word count, clustering (LDA), etc.

In the following sections, you will find information about local usage and test with our Docker image; Instructions for running the ArchiveSpark job on DLRL cluster.

### Some Scala and Spark Tutorials

[Top Tutorials To Learn Scala](https://medium.com/quick-code/top-tutorials-to-learn-scala-3a221bf4ef85)

[Spark SQL, DataFrames and Datasets Guide](https://spark.apache.org/docs/2.2.0/sql-programming-guide.html)

## Docker: Your Local Test Environment <a id="Docker"></a>

We provide a Docker image that contains a full development environment with ArchiveSpark. Check following links for detailed information about Docker.

Be aware Docker works as a Virtual Machine in MacOS and Windows. You can configure the computer resources allocations(CPU/Memory) to speed up/down your task. In Linux systems, Docker works as a native application.

[What is docker?](https://www.docker.com/resources/what-container)

### Install Docker CE

Install Docker CE version on your local environment:
[Linux](https://docs.docker.com/install/linux/docker-ce/ubuntu/)
[MacOS](https://docs.docker.com/docker-for-mac/install/)

### Install Docker for Windows 10

1. Install Docker toolbox from [here](https://docs.docker.com/toolbox/toolbox_install_windows/)
2. Disable Hyper-V feature in your windows system, [tutorial here](https://ugetfix.com/ask/how-to-disable-hyper-v-in-windows-10/)
3. Open Docker Quickstart Terminal (it will start an automatic set up)

### Deploy Docker Container (Windows10 pro)

1. In Docker Quickstart Terminal, start the container
   
    `docker run -d -p 8082:8080 --rm -v ~/docker/cs5984/share_dir:/share_dir -v ~/docker/cs5984/logs:/logs -v ~/docker/cs5984/notebook:/notebook -e ZEPPELIN_LOG_DIR='/logs' -e ZEPPELIN_NOTEBOOK_DIR='/notebook' --name cs5984 nytfox/fall18_cs4984-cs5984`

2. check container ip address:
   
   `docker-machine ip default`

3. Open your browser with:
   
   `yourDockerIP:8082`

### Deploy Docker Container (MacOS/Linux)

Check [Docker command line basics](https://docs.docker.com/engine/reference/commandline/cli/#examples) for various docker operations in the command line.

1. Get an account for Docker
2. Login Docker: either through application or commandline
3. Pull the container image from Docker image hub
   
    `pull nytfox/fall18_cs4984-cs5984:latest`

4. Start the container
   
    `docker run -d -p 8082:8080 --rm -v ~/docker/cs5984/share_dir:/share_dir -v ~/docker/cs5984/logs:/logs -v ~/docker/cs5984/notebook:/notebook -e ZEPPELIN_LOG_DIR='/logs' -e ZEPPELIN_NOTEBOOK_DIR='/notebook' --name cs5984 nytfox/fall18_cs4984-cs5984`

5. Access Zeppelin Website through the following URL in your browser (the service might take several minutes to boot up):
   
    `http://localhost:8082`

Consider the Docker container as a sub-Linux system inside your current OS where you can access the subsystem through following commands:

    `docker ps`
    `docker exec -it your_docker_id bash`

In `docker run` command:

* `-p` option project the service port in the subsystem (8080) to your local system (8081).
* `-v` option mounts one shared directory `~/docker/zeppelin/my_files` in your system and bind two other directories to the shared directory. In the subsystem, the directory is `/my_files`. Zeppelin notebook and the log will also be automatically saved in this shared directory through `-e` option.

Please refer Docker documentation for all other detailed information for the run command as needed.

### Notice

* **Important** All changes you make inside Docker subsystem will **not** be saved unless you commit the changes. Refer [Docker commit](https://docs.docker.com/engine/reference/commandline/commit/) and make sure to commit your changes if you make some significant changes to the docker environment.
* Your notebook (code) and the log will be saved in the bind-mounted directories
* You can mess with your Docker container in whatever way you want: install applications, changing files, etc. Refer Linux commands.
* If you think you broke the container, stop it and restart it. Every time you restart the container, it will start from the initial status:
    `docker stop your_container_id`

## Zeppelin is Your Playground <a id="Zeppelin"></a>

Zeppelin is a notebook environment (similar to Jupyter Notebook) upon Spark where you can run, test your code all within Zeppelin. We have integrated AchiveSpark in our Docker environment so that you can play around with it. The primary language is Scala. (Python is also available if needed)

Refer [Zeppelin Official Website](https://zeppelin.apache.org/) for detailed documentation.

### Sample Code

We have prepared a Zeppelin based sample notebook, the notebook source file is available in this repositary:

`/sample_notebooks/ArchiveSpark_HtmlText_extraction.json`

You can download import the notebook to Zeppelin through `import note`.

<img src="./doc/img_1.png " width="250">

ArchiveSpark Github page also provides some good [Documentations and Recipies](https://github.com/helgeho/ArchiveSpark/blob/master/docs/README.md)

### Spark-Shell Testing in Docker

Other than running code in Zeppelin, you can also run your code through `spark-shell` within Docker. (This is recommended before you run any code on DLRL cluster) We have prepared one example `ArchiveSpark_HtmlText_extraction.scala` in `share_dir`.

1. Package (copy) your code into one scala script:

    `ArchiveSpark_HtmlText_extraction.scala`

2. Copy/Move your script to
   
   `~/docker/cs5984/share_dir/`

3. Access Docker shell:
   
   `docker ps`
   `docker exec -it your_docker_id bash`

4. Run spark-shell to execute your script:
   
    ```/archive_spark/spark-2.2.1-bin-hadoop2.7/bin/spark-shell -i /share_dir/ArchiveSpark_HtmlText_extraction.scala --files /archive_spark/archivespark_dlrl/libs/en-sent.bin --jars /archive_spark/archivespark_dlrl/libs/archivespark-assembly-2.7.6.jar,/archive_spark/archivespark_dlrl/libs/archivespark-assembly-2.7.6-deps.jar,/archive_spark/archivespark_dlrl/libs/stanford-corenlp-3.5.1.jar,/archive_spark/archivespark_dlrl/libs/opennlp-tools-1.9.0.jar ```

 `-i` option points to the path of your script

 `--files` and `--jars` options will load all necessary dependencies you would need for your script. You can add more dependencies as you need for your code.

## Real Job on Cluster <a id="cluster"></a>

After testing and validating your code, you can package your code into one Scala script file and run it on DLRL cluster through following commands:

1. Enable JAVA8 env:

    `export JAVA_HOME=/usr/java/jdk1.8.0_171/`

2. Execute Scala Scripts:

    `spark2-shell -i /your/script.scala --files /home/public/cs4984_cs5984_f18/unlabeled/lib/en-sent.bin --jars /home/public/cs4984_cs5984_f18/unlabeled/lib/archivespark-assembly-2.7.6.jar,/home/public/cs4984_cs5984_f18/unlabeled/lib/archivespark-assembly-2.7.6-deps.jar,/home/public/cs4984_cs5984_f18/unlabeled/lib/stanford-corenlp-3.5.1.jar,/home/public/cs4984_cs5984_f18/unlabeled/lib/opennlp-tools-1.9.0.jar `

## Best Practice <a id="best"></a>

Before you run the code on DLRL cluster, here is the recommended procedures for preparing your code:

1. If your dataset is small or process is not heavy: get the result from your local Zeppelin environment.
2. If your dataset is big or process is heavy: sample your dataset first for fast testing.
3. Package your script and do Spark-Shell Testing in Docker
4. Load your script to DLRL cluster and run it

## Work with PySpark <a id="pyspark"></a>

If you want to work with Python with Spark (PySpark), find the sample code we provide in Zeppelin: `SampleCode_PySpark`

A cool thing: you can [exchange variable between Spark and PySpark](https://www.zepl.com/viewer/notebooks/bm90ZTovL21vb24vM2E0ZTk5Y2U1ZmNiNGQ3NGE1YTZkZTMzMTQxNjE1NWYvbm90ZS5qc29u) in Zeppelin.

## Spark and NLP <a id="nlp"></a>

Spark provides packages for NLP related tasks, check following resources:
* [MLib](https://spark.apache.org/docs/2.2.0/ml-guide.html) package for Spark with Scala
* [PySpark MLib](http://spark.apache.org/docs/2.2.0/api/python/pyspark.mllib.html) package for Spark with Python
* [SparkNLP](https://nlp.johnsnowlabs.com/components.html) package for Scala and Python

