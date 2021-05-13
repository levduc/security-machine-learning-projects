import csv
import operator
import matplotlib.pyplot as plt
import numpy as np
import sys


def task1(file_name, attribute_name):
    attributeIndex = -1
    valueDict = {}
    try:
        with open(file_name) as csv_file:
            csv_reader = csv.reader(csv_file, delimiter=',')
            line_count = 0
            for row in csv_reader:
                # reading headers
                if line_count == 0:
                    for i in range(len(row)):
                        if row[i] == attribute_name:
                            attributeIndex = i
                            break
                    # Cannot find attribute, return
                    if attributeIndex == -1:
                        print('[-] Cannot find attribute name')
                        return
                    line_count += 1
                else:
                    #counting
                    value = row[attributeIndex]
                    if value in valueDict:
                        valueDict[value] += 1
                    else:
                        valueDict[value] = 1
                    line_count += 1
        # close csv file
        csv_file.close()
        # (1) break ties 
        # sort by name first 
        sortedValue = sorted(valueDict.items(),key = operator.itemgetter(0))
        # sort by value late
        sortedValue.sort(key=operator.itemgetter(1), reverse=True)
        # (2) Text output
        outputFile = attribute_name+'.txt'
        with open(outputFile, 'w+') as of:
            for value in sortedValue:
                of.write(str(value[0])+ ' ' + str(value[1])+'\n')
        print('[+] text output finished !')
        # (3) Figure output
        numAttributeValues = len(sortedValue) 
        attributeValues = []
        frequency = []
        if numAttributeValues > 10:
            for i in range(5):
                attributeValues.append(sortedValue[i][0])
                frequency.append(sortedValue[i][1])
            for i in range(5):
                attributeValues.append(sortedValue[numAttributeValues+i-5][0])
                frequency.append(sortedValue[numAttributeValues+i-5][1])
        else:
            attributeValues = [item[0] for item in sortedValue]
            frequency = [item[1] for item in sortedValue]
        
        fig, ax =plt.subplots()
        ind = np.arange(len(attributeValues)) #?
        width = .3
        bar = ax.bar(ind, frequency, width, color='magenta')
        ax.set_ylabel('Frequency')
        ax.set_xlabel('Attribute Names')
        ax.set_xticks(ind)
        ax.set_xticklabels(attributeValues,rotation=90) # to display everything
        fileName = attribute_name+' '+str(numAttributeValues)+'.png'
        fig.savefig(fileName,bbox_inches='tight')
        print('[+] figure output finished !')
    except FileNotFoundError:
        print("[-] Wrong file or file path")

# 
if len(sys.argv) !=3:
    print('[-] Usage: python task1 filename attribute_name')
else:
    task1(sys.argv[1],sys.argv[2])
