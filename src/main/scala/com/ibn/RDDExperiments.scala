package com.ibn

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by madhusudanan on 20/07/16.
  */
object RDDExperiments {

  case class data(name: String, age: Long);

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("test")
    val sc = new SparkContext(conf)
    val words = sc.textFile("src/main/resources/log4j.properties").flatMap(_.split(" |\\.|=")).filter(_ != "")
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._
    words.toDF()
    val group = words.groupBy(_.toLowerCase)
    println(group.collect().toList)
    println(group.map(x=>(x._1, x._2.size)).collect().toList)
  }
}
