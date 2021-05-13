import csv
import operator
import matplotlib
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
import sys

def task2(file_name):
    try:
        pairAttributeDict={}
        pairValueDict={}
        with open(file_name) as csv_file:
            csv_reader = csv.reader(csv_file, delimiter=',')
            line_count = 0
            attributeCount=0
            for row in csv_reader:
                # reading headers + initialize 2-d dictionary
                if line_count == 0:
                    attributeCount=len(row)
                    for i in range(len(row)):         # first scan
                        pairAttributeDict[i]={}
                        pairValueDict[i]={}
                        for j in range(i+1,len(row)): # second scan
                            pairAttributeDict[i][j] = row[i]+'_'+row[j]
                            pairValueDict[i][j]={}
                    line_count += 1
                else: 
                    #count here
                    for i in range(attributeCount): # len(row) != attributeCount
                        first = row[i]
                        for j in range(i+1,attributeCount):
                            second   = row[j]
                            if second == '':
                                second ='(empty)'             #make empty string
                            tempPair = first +'$'+second      #to avoid split space later
                            if tempPair in pairValueDict[i][j]:
                                pairValueDict[i][j][tempPair] +=1
                            else:
                                pairValueDict[i][j][tempPair]  =1
                    line_count += 1
        # close csv file
        csv_file.close()
        for i in range(attributeCount):
            for j in range(i+1,attributeCount):
                outputFile = pairAttributeDict[i][j]+'.txt'
                # sort pairValueDict here
                # (1) break ties 
                # sort by name first, this consider first 2 things as one word 
                sortedValue = sorted(pairValueDict[i][j].items(),key = operator.itemgetter(0))
                # sort by value later
                sortedValue.sort(key=operator.itemgetter(1), reverse=True)
                # (2) write to output file
                with open(outputFile, 'w+') as of:
                    for value in sortedValue:
                        firstAtt, secondAtt = value[0].split("$")
                        of.write(firstAtt+' '+ secondAtt+' '+ str(value[1])+'\n')
                    print("[+] text output finished !")
                #close output file
                of.closed
                # (3) generate heat map directly from sortedValue
                #  sortedValue[0]= ['att1$att2' 'count']
                firstAttribute = []
                secondAttribute= []
                for value in sortedValue:
                    firstAtt, secondAtt = value[0].split("$")
                    if firstAtt not in firstAttribute:
                        firstAttribute.append(firstAtt)
                    if secondAtt not in secondAttribute:
                        secondAttribute.append(secondAtt)
                dimension = (len(firstAttribute),len(secondAttribute))
                countMatrix=np.zeros(dimension, dtype=int)
                for x in range(len(firstAttribute)):
                    for y in range(len(secondAttribute)):
                        tempWord = firstAttribute[x]+'$'+secondAttribute[y]
                        if tempWord in pairValueDict[i][j]:
                            countMatrix[x][y] = pairValueDict[i][j][tempWord]
                print("[+] done with matrix. Plotting heatmap...")
                # matplot
                fig = plt.figure()
                ax = sns.heatmap(countMatrix)
                # ind = np.arange(len(firstAttribute))
                # ax.set_xticks(ind)
                # ax.set_xticklabels(firstAttribute,rotation=90)
                # ind1 = np.arange(len(secondAttribute))
                # ax.set_yticks(ind1)
                # ax.set_yticklabels(secondAttribute,rotation=90)
                fileName = pairAttributeDict[i][j]+'.png'
                plt.savefig(fileName,bbox_inches='tight')
    except FileNotFoundError:
        print("[-] Wrong file or file path")
if len(sys.argv) !=2:
    print('[-] Usage: python task2.py filename')
else:
    task2(sys.argv[1])
