from sklearn import svm
from collections import OrderedDict
from os import listdir
from os.path import isfile, join
import re
import nltk
import math
from nltk.corpus import stopwords
import numpy as np
# from nltk.stem.snowball import SnowballStemmer
# from nltk.corpus import wordnet
# from nltk.stem import WordNetLemmatizer
# nltk.download('punkt')
# nltk.download('stopwords')
# nltk.download('snowball')

# preprocessing label
# preprocessing label
from sklearn import svm
from collections import OrderedDict
# preprocessing label
def preProcessingLabelSVM(labelFile = "SPAM.label"):
    labelDict = {}
    hamCount = 0
    spamCount = 0
    with open("SPAM.label") as f:
        for line in f:
            (val, key) = line.split()
            labelDict[key] = int(val)
    f.close()
    return labelDict
### Preprocessing: building vocabulary + frequency:
def SVMBuildingMatrix(trainingDir='processed_training'):
    # read spam label to identify which doc is spam
    labelDict = preProcessingLabelSVM()
    # for each file in the training directory
    onlyfiles = [f for f in listdir(trainingDir) if isfile(join(trainingDir, f))]
    wordList = {}                                       # vocabulary
    trainMatrix = {}                                    # trainingMatrix
    stop_words = set(stopwords.words('english'))        # english stop words
    for i in onlyfiles:
        # see some random-file
        if i == '.DS_Store': #ignore this file
            continue
        trainMatrix[i] = {}
        inputFile = trainingDir+'/'+i
        s = open(inputFile,encoding="latin-1").read()
        tokens = s.split()
        for word in tokens:
            ### Processing word here##################
            # ignore stopwords
            if word in stop_words:
                continue
            if word in ':;.,/\(\)\[\]<>': # some normal character
                continue
            lmWord = word # was thinking about lemmatize word
            ##########################################
            ### Update vocabulary#####################
            if lmWord in wordList:
                # update vector for each document
                wordList[lmWord]+=1
                if lmWord in trainMatrix:
                    trainMatrix[i][lmWord] += 1
                else:
                    trainMatrix[i][lmWord] = 1
            else:
                # add new word to word list
                wordList[lmWord]=1
                if lmWord in trainMatrix:
                    trainMatrix[i][lmWord] += 1
                else:
                    trainMatrix[i][lmWord] = 1
                # update vector for each document
            ##########################################
    return wordList,trainMatrix

### get top attribute from wordList, by default using 1000 attribute
def getTopAttribute(wordList, n=1000, order=False):
    ### get top words 
    top = sorted(wordList.items(), key=lambda x: x[1], reverse=True)[:n]
    if order:
        return OrderedDict(top)
    return dict(top)
### Output training matrix with number of attribute
def getMatrixWithN(Matrix,wordList,n=1000):
    # get top attribute
    topDict = getTopAttribute(wordList,n,True)
    # read spam label to identify which doc is spam
    labelDict = preProcessingLabelSVM()
    # creating training Matrix
    dimension = (len(Matrix),len(topDict))   
    processedMatrix=np.zeros(dimension)
    labelTrainVector=np.zeros(len(Matrix))
    x = 0 # row
    y = 0 # column
    for doc in Matrix:
        for word in topDict:
            if word not in Matrix[doc]:
                y+=1 # move to next column
                continue
            else:
                processedMatrix[x][y] = Matrix[doc][word]
                y+=1 # move to next column
        if doc in labelDict:
            labelTrainVector[x] = labelDict[doc]
        x+=1
        y=0
    return processedMatrix,labelTrainVector
### computing stat
def SVMComputeStat(prediction, labelTestVector):
    if len(prediction) != len(labelTestVector):
        print('[-] 2 vectors are not in the same length')
        return
    tp = 0 # true positive
    fp = 0 # false postive
    fn = 0 # false negative
    tn = 0 # true negative
    vectorLength = len(prediction)
    for i in range(vectorLength):
        if prediction[i] == 1 and prediction[i] == labelTestVector[i]:
            tp += 1
        elif prediction[i] == 1 and prediction[i] != labelTestVector[i]:
            fp += 1
        elif prediction[i] == 0 and prediction[i] == labelTestVector[i]:
            tn += 1
        elif prediction[i] == 0 and prediction[i] != labelTestVector[i]:
            fn += 1
        else:
            print(i,'[-] something is wrong')
#     print('tp =',tp,', tn =',tn, ', fp =', fp, ', fn =', fn)
    print('False Positive Rate:\t', fp)
    print('False Negative Rate:\t', fn)
    precision = tp/(tp+fp)
    recall = tp/(tp+fn)
    print('Recall:\t\t\t', recall)    
    print('Precision:\t\t',precision)
    fscore = 2*precision*recall/(precision + recall)
    print('F-score beta = 1: \t', fscore)



#### SVM running here:
print('[+] SVM on testing data')
(wordList,trainMatrix) = SVMBuildingMatrix('processed_training')
(testWordList,testMatrix) = SVMBuildingMatrix('processed_testing') #we don't care about test wordlist
(processedTrainMatrix, labelTrainVector) = getMatrixWithN(trainMatrix,wordList,300)
(processedTestMatrix, labelTestVector) = getMatrixWithN(testMatrix,wordList,300)
clf = svm.SVC()
clf.fit(processedTrainMatrix, labelTrainVector)  
prediction = clf.predict(processedTestMatrix)
SVMComputeStat(prediction, labelTestVector)

### 5-fold cross validation
print('############################################')
print('[+] 5-fold Cross Validation')
(wordList,trainMatrix) = SVMBuildingMatrix('processed_training')
(processedTrainMatrix, labelTrainVector) = getMatrixWithN(trainMatrix,wordList,300)
# partion training matrix and label vector
partitionMatrices = np.split(processedTrainMatrix, 5)
partitionVectors = np.split(labelTrainVector, 5)
# First fold:
print('[First fold]')
# Consider first-fold as test matrix
firstFoldMatrix = partitionMatrices[0]
firstFoldVector = partitionVectors[0]
# Consider last 4-fold as training matrix
last4Matricies    = np.concatenate((partitionMatrices[1],partitionMatrices[2],partitionMatrices[3],partitionMatrices[4]), axis=0)
last4FoldsVectors = np.concatenate((partitionVectors[1],partitionVectors[2],partitionVectors[3],partitionVectors[4]), axis=0)
# test
clf = svm.SVC()
clf.fit(last4Matricies, last4FoldsVectors)  
fivefoldprediction = clf.predict(firstFoldMatrix)
SVMComputeStat(fivefoldprediction, firstFoldVector)

print('[Second fold]')
# Consider first-fold as test matrix
firstFoldMatrix = partitionMatrices[1]
firstFoldVector = partitionVectors[1]
# Consider last 4-fold as training matrix
last4Matricies    = np.concatenate((partitionMatrices[0],partitionMatrices[2],partitionMatrices[3],partitionMatrices[4]), axis=0)
last4FoldsVectors = np.concatenate((partitionVectors[0],partitionVectors[2],partitionVectors[3],partitionVectors[4]), axis=0)
# test
clf = svm.SVC()
clf.fit(last4Matricies, last4FoldsVectors)  
fivefoldprediction = clf.predict(firstFoldMatrix)
SVMComputeStat(fivefoldprediction, firstFoldVector)

print('[Third fold]')
# Consider first-fold as test matrix
firstFoldMatrix = partitionMatrices[2]
firstFoldVector = partitionVectors[2]
# Consider last 4-fold as training matrix
last4Matricies    = np.concatenate((partitionMatrices[0],partitionMatrices[1],partitionMatrices[3],partitionMatrices[4]), axis=0)
last4FoldsVectors = np.concatenate((partitionVectors[0],partitionVectors[1],partitionVectors[3],partitionVectors[4]), axis=0)
# test
clf = svm.SVC()
clf.fit(last4Matricies, last4FoldsVectors)  
fivefoldprediction = clf.predict(firstFoldMatrix)
SVMComputeStat(fivefoldprediction, firstFoldVector)

print('[Fourth fold]')
# Consider first-fold as test matrix
firstFoldMatrix = partitionMatrices[3]
firstFoldVector = partitionVectors[3]
# Consider last 4-fold as training matrix
last4Matricies    = np.concatenate((partitionMatrices[0],partitionMatrices[1],partitionMatrices[2],partitionMatrices[4]), axis=0)
last4FoldsVectors = np.concatenate((partitionVectors[0],partitionVectors[1],partitionVectors[2],partitionVectors[4]), axis=0)
# test
clf = svm.SVC()
clf.fit(last4Matricies, last4FoldsVectors)  
fivefoldprediction = clf.predict(firstFoldMatrix)
SVMComputeStat(fivefoldprediction, firstFoldVector)

print('[Fifth fold]')
# Consider first-fold as test matrix
firstFoldMatrix = partitionMatrices[4]
firstFoldVector = partitionVectors[4]
# Consider last 4-fold as training matrix
last4Matricies    = np.concatenate((partitionMatrices[0],partitionMatrices[1],partitionMatrices[2],partitionMatrices[3]), axis=0)
last4FoldsVectors = np.concatenate((partitionVectors[0],partitionVectors[1],partitionVectors[2],partitionVectors[3]), axis=0)
# test
clf = svm.SVC()
clf.fit(last4Matricies, last4FoldsVectors)  
fivefoldprediction = clf.predict(firstFoldMatrix)
SVMComputeStat(fivefoldprediction, firstFoldVector)

print('##########################################################')
#### Predict duc spam
print('[+] Task 3.4: spam email that past both naive bayes and SVM')
(wordList,trainMatrix) = SVMBuildingMatrix('processed_training')
(testWordList,testMatrix) = SVMBuildingMatrix('duc-spam') #we don't care about test wordlist
(processedTrainMatrix, labelTrainVector) = getMatrixWithN(trainMatrix,wordList,300)
(processedTestMatrix, labelTestVector) = getMatrixWithN(testMatrix,wordList,300)
clf = svm.SVC()
clf.fit(processedTrainMatrix, labelTrainVector)  
prediction = clf.predict(processedTestMatrix)
print('[>>>>] Email is labeled as: ', prediction[0])