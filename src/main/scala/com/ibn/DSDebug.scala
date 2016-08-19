package com.ibn

// import org.apache.spark.sql.{Column, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by kmadhu on 15/3/16.
 */


object DSDebug {

  case class data(name : String, cnt1 : Long, cnt2 : Long, sum : Long);
  //case class data1(name : String, cnt1 : Long, cnt2 : Long);
  case class data1(name : String, sum : Long);

  def main(args : Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("test")
    val sc = new SparkContext(conf)
/*    val ss = org.apache.spark.sql.SparkSession.builder.getOrCreate()
    import ss.implicits._

    val DF = ss.read.json("src/main/resources/data.json")

    sc.parallelize(1 to 10).toDS()
    val ds1 = DF.as[data]

    ds1.show()*/

  }

}

