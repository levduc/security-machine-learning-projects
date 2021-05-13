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
        print("[-] Usage: TopK <file> <column-name> <k>", file=sys.stderr)
        sys.exit(-1)
    spark = SparkSession\
        .builder\
        .appName("TopK")\
        .getOrCreate()
    # read input file
    df = spark.read.csv(sys.argv[1],header=True)
    # df.printSchema()
    # Register the DataFrame as a SQL temporary view
    df.createOrReplaceTempView("connection")
    InputColumnName=sys.argv[2]
    topK=sys.argv[3]
    query = "SELECT "+InputColumnName+ ", COUNT("+InputColumnName+")AS count\
             FROM connection\
             GROUP BY "+InputColumnName+\
             " ORDER BY count DESC \
             LIMIT " + topK
    sqlDF = spark.sql(query)

    npDF = np.array(sqlDF.collect())
    topK = {}
    for element in npDF:
        print(element[0] +"\t\t" + element[1])
    # print top K without truncating the data.
    # sqlDF.show(int(topK), False);
    spark.stop()
