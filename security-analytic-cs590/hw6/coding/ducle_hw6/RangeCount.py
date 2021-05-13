from __future__ import print_function
import sys
import pyspark
from operator import add
from pyspark.sql import SparkSession
from pyspark.sql import SQLContext
from pyspark.sql import Row
from pyspark.sql.types import *
import csv

if __name__ == "__main__":
    if len(sys.argv) != 5:
        print("[-] Should take 4 arguments")
        print("[-] Usage: RangeQuery <file> <column-name> <f1> <f2>", file=sys.stderr)
        sys.exit(-1)
    spark = SparkSession\
        .builder\
        .appName("RangeQuery")\
        .getOrCreate()
    # read input file
    df = spark.read.csv(sys.argv[1],header=True)
    # df.printSchema()
    # df.show()
    # Register the DataFrame as a SQL temporary view
    df.createOrReplaceTempView("connection")
    InputColumnName=sys.argv[2]
    first_query = "SELECT "+InputColumnName+ ", COUNT("+InputColumnName+")AS count\
             FROM connection\
             GROUP BY "+InputColumnName
    firstDF = spark.sql(first_query)
    firstDF.createOrReplaceTempView("countview")
    # after get count table
    second_query = "SELECT * from countview WHERE count BETWEEN " + sys.argv[3]+ " AND " +sys.argv[4] + " ORDER BY count DESC" 
    # select from count table
    secondDF = spark.sql(second_query)
    secondDF.createOrReplaceTempView("countview_in_range")
    # count rows
    third_query  = "SELECT COUNT(" + InputColumnName + " ) FROM countview_in_range"
    thirdDF = spark.sql(third_query)

    thirdDF.show();
    spark.stop()
