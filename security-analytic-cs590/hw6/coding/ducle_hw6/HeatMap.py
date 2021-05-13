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
        print("[-] Usage: HeatMap <file> <attribute_1> <attribute_2>", file=sys.stderr)
        sys.exit(-1)
    spark = SparkSession\
        .builder\
        .appName("HeatMap_Query")\
        .getOrCreate()
    # read input file
    df = spark.read.csv(sys.argv[1],header=True)
    # df.printSchema()

    df.createOrReplaceTempView("connection")
    first_att=sys.argv[2]
    second_att=sys.argv[3]
    # obtain all unique rows of first attribute
    first_query = "SELECT DISTINCT "+first_att+\
                  " FROM connection" 
    firstDF = spark.sql(first_query)
    first_attribute_columns = np.array(firstDF.collect()).flatten()
    first_attribute_columns = [str(first_attribute_columns[i]) for i in range(len(first_attribute_columns))]

    # obtain unique rows of second attribute
    second_query = "SELECT DISTINCT " + second_att +\
                   " FROM connection"
    secondDF = spark.sql(second_query)
    second_attribute_rows = np.array(secondDF.collect()).flatten()
    second_attribute_rows = [str(second_attribute_rows[i]) for i in range(len(second_attribute_rows))]

    # third query count unique
    third_query = "SELECT "+ first_att+ ", "+ second_att + ", COUNT("+first_att+", "+ second_att +") as count FROM connection GROUP BY " +first_att+", "+ second_att
    thirdDF = spark.sql(third_query)
    cell_with_value = np.array(thirdDF.collect())

    # rows
    rows    = len(second_attribute_rows)
    # columns
    columns = len(first_attribute_columns)
    print("[+] Heatmap size : " + str(rows*columns))
    # initialize matrix heatMap['firstAtt']['secondAtt'] = count
    heatMap ={}    
    # initialize with 0
    for column in first_attribute_columns:
        heatMap[column] = {}
        for row in second_attribute_rows:
            heatMap[column][row]=0
            # may need to call query heare
    # update those are not 0
    for element in cell_with_value:
        heatMap[element[0]][element[1]] = int(element[2])

    dfObj = pd.DataFrame(heatMap)
    print(dfObj)


    # old and slow
    # initialize matrix heatMap['firstAtt']['secondAtt'] = count
    # heatMap ={}    
    # for column in first_attribute_columns:
    #     heatMap[column] = {}
    #     for row in second_attribute_rows:
    #         countQuery = "SELECT COUNT(*) FROM connection WHERE " + first_att +"='"+ column +"' AND " + second_att + "='"+row+"'"
    #         fourthDF = spark.sql(countQuery)
    #         tempCount = np.array(fourthDF.collect()).flatten()
    #         # print("[+] Running")
    #         heatMap[column][row]=tempCount[0]
    #         # may need to call query heare
    # dfObj = pd.DataFrame(heatMap)
    # print(dfObj)
    # spark.stop()
