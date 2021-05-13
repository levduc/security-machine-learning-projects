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
    if len(sys.argv) != 6:
        print("[-] Should take 5 arguments")
        print("[-] Usage: BiCondition <file> <attribute_1> <att1_type> <attribute_2> <att2_type>", file=sys.stderr)
        sys.exit(-1)
    spark = SparkSession\
        .builder\
        .appName("BiCondition_Query")\
        .getOrCreate()
    # read input file
    df = spark.read.csv(sys.argv[1],header=True)
    # df.printSchema()
    # df.show()
    # Register the DataFrame as a SQL temporary view
    df.createOrReplaceTempView("connection")
    first_att=sys.argv[2]
    first_att_type= sys.argv[3]
    second_att=sys.argv[4]
    second_att_type=sys.argv[5]
    first_query = "SELECT "+first_att+ ", " + second_att +\
                  " FROM connection WHERE " + first_att + "='"+ first_att_type + "' AND " + second_att + "='"+ second_att_type + "'"  
    firstDF = spark.sql(first_query)
    # firstDF.show();
    firstDF.createOrReplaceTempView("countview")
    # after get count table
    second_query = "SELECT COUNT("+ first_att + ") as countBiCondition FROM countview"
    # count row
    secondDF = spark.sql(second_query)
    secondDF.show();
    spark.stop()
