# Supplements

This repositary contains supplement information for course CS4984/CS5984: Big Text Summarization, Fall18, Virginia Tech.

If you encounter any question or issue, please check relative documentations first. For more questions, you can create an issue in this GitHub page:

<img src="./doc/img_2.png " width="250">

## ArchiveSpark

"An Apache Spark framework for easy data processing, extraction as well as derivation for archival collections." - helgeho
[ArchiveSpark Official GitHub page](https://github.com/helgeho/ArchiveSpark)

In this class, we will utilize ArchiveSpark to process our web archive collections. We can leverage the power of ArchiveSpark in various ways: content extraction, word count, clustering (LDA) etc.

In following sections, you will find information about local usage and test with our Docker image; Instructions for running ArchiveSpark job on DLRL cluster.

## Local Usage with Docker

We provide a Docker image that contains a full development environment with ArchiveSpark. Check following links for detailed information about Docker.

[What is docker?](https://www.docker.com/resources/what-container)

### Install Docker CE

You will install Docker CE version on your local environment:
[Linux](https://docs.docker.com/install/linux/docker-ce/ubuntu/),
[MacOS](https://docs.docker.com/docker-for-mac/install/),
[MacOS](https://docs.docker.com/docker-for-windows/install/)

### Deploy the Container
You will perform various docker operations in command line.
Check [Docker command line basics](https://docs.docker.com/engine/reference/commandline/cli/#examples) for more details.

1. Pull the container image from Docker image hub


   `pull vt_dlrl/fall18_cs4984-cs5984:latest`


2. Start the container 

   `docker run -d -p 8082:8080 --rm -v ~/docker/cs5984/share_dir:/share_dir -v ~/docker/cs5984/logs:/logs -v ~/docker/cs5984/notebook:/notebook -e ZEPPELIN_LOG_DIR='/logs' -e ZEPPELIN_NOTEBOOK_DIR='/notebook' --name cs5984 vt_dlrl/fall18_cs4984-cs5984`

3. Access Zeppelin Website through following url in your browser (the service might take several minutes to boot up):

   `http://localhost:8082`

You can consider the container as a sub Linux system inside your current OS. You can access the sub system through following commands:
`docker ps`
`docker exec -it your_docker_id bash`

In `docker run` command:

* `-p` option project the service port in the sub system (8080) to your local system (8081).
* `-v` option mounts one shared directory `~/docker/zeppelin/my_files` in your own system and bind two other directories to the shared directory. In the sub system, the directory is `/my_files`. Zeppelin notebook and log will also be automatically saved in this shared directory through `-e` option.

Please refer Docker documentations for all other detailed information for the run command as needed.

### Notice

* **Important** All changes you make inside Docker sub systme will **not** be saved unless you commit the changes. Refer [Docker commit](https://docs.docker.com/engine/reference/commandline/commit/) and make sure to commit your changes if you make some significant changes to the docker environment.
* Your notebook (code) and log will be saved in the bind mounted directories
* You can mess with your docker container in whatever way you want: install applications, changing files etc. Refer Linux related materials.
* If you think you broke the container, stop it and restart it. Every time you restart the container, it will start from the initial status:
    `docker stop your_container_id`

### Zeppelin is Your Playground

Zeppelin is a notebook environment upon Spark with a good interface for visualization, debugging and documentation. You can run, test your code all within Zepplelin. We have integrated AchiveSpark in our Docker environment so that you can play around with it. The primary language you will use is Scala. (Python is also available if needed)

Refer [Zeppelin Official Website](https://zeppelin.apache.org/) for detailed documentation.

#### Sample Code

ArchiveSpark provides some good [Documentations and Recipies](https://github.com/helgeho/ArchiveSpark/blob/master/docs/README.md) that you can refer. Please check this site first if you encounter any issue using ArchiveSpark.

We have also prepared a Zeppelin based sample code, find our sample code in Zeppelin here:

<img src="./doc/img_1.png " width="250">

The notebook source file is also available in this repository:

`/sample_notebooks/ArchiveSpark_HtmlText_extraction.json`

You can import the notebook to Zeppelin if needed.

#### Spark-Shell Testing in Docker

Other than running code in Zeppelin, you can also run your code through `spark-shell` within Docker. (This is recommended before you run any code on DLRL cluster) We have prepared one example `ArchiveSpark_HtmlText_extraction.scala` in `share_dir`.

1. Package your code into one scala script:

`ArchiveSpark_HtmlText_extraction.scala`

2. Copy/Move your script to `~/docker/cs5984/share_dir/`
3. Access Docker shell:

   `docker ps`
   `docker exec -it your_docker_id bash`

4. Run spark-shell to execute your script:

   `/archive_spark/spark-2.2.1-bin-hadoop2.7/bin/spark-shell -i /share_dir/ArchiveSpark_HtmlText_extraction.scala --files /archive_spark/archivespark_dlrl/libs/en-sent.bin --jars /archive_spark/archivespark_dlrl/libs/archivespark-assembly-2.7.6.jar,/archive_spark/archivespark_dlrl/libs/archivespark-assembly-2.7.6-deps.jar,/archive_spark/archivespark_dlrl/libs/stanford-corenlp-3.5.1.jar,/archive_spark/archivespark_dlrl/libs/opennlp-tools-1.9.0.jar `

`--files` and `--jars` options will load all basic dependencies you would need for your script. You can add more dependencies as you need for your code.

### Real Job on Cluster

After testing and validating your code, you can package your code into one Scala script file and run it on DLRL cluster through following commands: 
1. Enable JAVA8 env:

`export JAVA_HOME=/usr/java/jdk1.8.0_171/`

2. Execute Scala Scripts:



### Best Practice

Before you run the code on DLRL cluster, here is the recommended procedures for preparing your code:

1. If your dataset is small or process is not heavy: just get the result from your local Zeppelin environment.
2. If your dataset if big or process is heavy: sample your dataset first for fast testing.
3. Package your script and do Spark-Shell Testing in Docker
4. Load your script to DLRL cluster and run it