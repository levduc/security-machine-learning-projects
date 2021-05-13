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
from decimal import Decimal

if __name__ == "__main__":
    if len(sys.argv) != 5:
        print("[-] Should take 3 arguments")
        print("[-] Usage: QueryForfun <file> <numerical_att_1> <numerical_att_2> <threshold-k>", file=sys.stderr)
        sys.exit(-1)

    spark = SparkSession\
        .builder\
        .appName("QueryForfun")\
        .getOrCreate()

    # read input file
    df = spark.read.csv(sys.argv[1],header=True)
    # df.printSchema()
    # df.show()

    df.createOrReplaceTempView("connection")
    first_att  =sys.argv[2]
    second_att =sys.argv[3]
    threshold_k=Decimal(sys.argv[4])
    
    # third query count unique
    third_query = "SELECT "+ first_att+ ", "+ second_att + " FROM connection"
    thirdDF = spark.sql(third_query)
    
    # create as numpy array
    twoColumns = np.array(thirdDF.collect())
    count = 0

    # check if the summation is satisfy
    for element in twoColumns:
        try:
            if Decimal(element[0]) + Decimal(element[0]) > threshold_k:
                count = count + 1
        except:
            print("[-] May not be numerical column")
            sys.exit(-1)
    print("[+] ========================================")
    print("[+] The number of packages : " + str(count))


