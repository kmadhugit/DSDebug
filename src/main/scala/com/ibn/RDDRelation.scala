
/*
 * (C) Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibn

import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer;

// One method for defining the schema of an RDD is to make a case class with the desired column
// names and types.



case class Record(key: Int, value: String)

case class Order(oid: Int, bid: Int, cts: String)

//case class OItem(iid: Int, oid: Int, gid: Int, gnum: Double, price: Double, gstore: Double)
case class OItem(iid: Int, gnum: Double, gid: Int, oid: Int, price: Double, gstore: Double)

object RDDRelation {

  def mysplit(str : String, delim : Char) = {
    var idx = 0
    var lastidx = 0
    val buf = new ListBuffer[String]
    str.foreach(x => {
      if(x == delim) {
        buf += str.substring(lastidx,idx)
        lastidx = idx+1;
      }
      idx += 1;
    })
    if(lastidx < idx)
      buf += str.substring(lastidx,idx)
    buf.toArray
  }

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("test")
    val sc = new SparkContext(conf)

    val ss = new SQLContext(sc)
    import ss.implicits._

    // val ss = org.apache.spark.sql.SparkSession.builder.getOrCreate(); import ss.implicits._


    val sl = StorageLevel.MEMORY_AND_DISK_SER

    val orderDF = sc.textFile("src/main/resources/OS_ORDER.txt", 10).map { line =>
      // val data=line.split("\\|") =>@Charlie Don't use string, use char as below
      val data = mysplit(line,'|')
      Order(data(0).toInt, data(1).toInt, data(2))
    }.toDF

    orderDF.registerTempTable("orderTab")
    orderDF.persist(sl)

    val oitemDF = sc.textFile("src/main/resources/OS_ORDER_ITEM.txt", 10).map { line =>
      // val data=line.split("\\|") =>@Charlie Don't use string, use char as below
      // val data = line.split('|')
      val data = mysplit(line,'|')
      OItem(data(0).toInt, data(1).toDouble, data(2).toInt, data(3).toInt, data(4).toDouble, data(5).toDouble)
    }.toDF
    oitemDF.registerTempTable("oitemTab")
    oitemDF.persist(sl)

    //println (ss.sql("SELECT * FROM orderTab r JOIN oitemTab s ON r.oid = s.oid").collect().length)

    mysplit("/abc/ef/ghijk/",'/')

    val s = readLine()

    sc.stop()
  }
}
