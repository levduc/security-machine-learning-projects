from __future__ import print_function
import sys
import pyspark
from operator import add
from pyspark.sql import SparkSession
from pyspark.sql import SQLContext
from pyspark.sql import Row
from pyspark.sql.types import *
import csv
import numpy as np
import pandas as pd

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("[-] Should take 3 arguments")
        print("[-] Usage: SpecificType <file> <column-name> <type>", file=sys.stderr)
        sys.exit(-1)
    spark = SparkSession\
        .builder\
        .appName("SpecificType")\
        .getOrCreate()
    # read input file
    df = spark.read.csv(sys.argv[1],header=True)
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
    second_query = "SELECT * from countview WHERE " + InputColumnName + " =  '" + sys.argv[3] + "'" 
    # select from count table
    secondDF = spark.sql(second_query)
    secondDF.show();
    spark.stop()
