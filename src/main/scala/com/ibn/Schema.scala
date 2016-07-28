package com.ibn

import org.apache.spark.sql.types.LongType
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by madhusudanan on 27/07/16.
  */

case class person(name : String, age : Long)

object Schema {

  val conf = new SparkConf().setMaster("local").setAppName("test")
  val sc = new SparkContext(conf)
  val ss = org.apache.spark.sql.SparkSession.builder.getOrCreate()
  import ss.implicits._

  def rddToDS(): Unit = {

    val people = sc.textFile("src/main/resources/people.txt")
    val rdd = people.map(s => person(s.split(" ")(0),s.split(" ")(1).toLong))

    val peopleDs = rdd.toDS()

  }
  def main(arg: Array[String]) {


    // sc is an existing SparkContext.

    // Create an RDD
    val people = sc.textFile("src/main/resources/people.txt")

    // The schema is encoded in a string
    val schemaString = "name age"

    // Import Row.
    import org.apache.spark.sql.Row;

    // Import Spark SQL data types
    import org.apache.spark.sql.types.{StructType, StructField, StringType};

    // Generate the schema based on the string of schema
    val schema =
      StructType( List(
        StructField("name", StringType, true),
        StructField("age", LongType, true)
      ))

    // Convert records of the RDD (people) to Rows.
    val rowRDD = people.map(_.split(" ")).map(p => Row(p(0), p(1).toLong))

    // Apply the schema to the RDD.
    val peopleDataFrame = ss.createDataFrame(rowRDD, schema)

    val peopleDS = peopleDataFrame.as[person]


/*    peopleDataFrame.rdd.collect().foreach(row => {
      println( "Direct Projection " + row.getString(0) + "," + row.getLong(1))
    })*/

    peopleDS.where($"age" > 5 && $"age" > 6).show

    // peopleDS.select(nameColumn).show()

  }

}
