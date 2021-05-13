from os import listdir
from os.path import isfile, join
import re
import nltk
import math
from nltk.corpus import stopwords
# from nltk.stem.snowball import SnowballStemmer
# from nltk.corpus import wordnet
# from nltk.stem import WordNetLemmatizer
# nltk.download('punkt')
# nltk.download('stopwords')
# nltk.download('snowball')
### Preprocessing: Label
def preProcessingLabel(labelFile = "SPAM.label"):
    labelDict = {}
    hamCount = 0
    spamCount = 0
    with open("SPAM.label") as f:
        for line in f:
            (val, key) = line.split()
            labelDict[key] = int(val)
            # only consider TRAINING label
            if 'TRAIN_' in key:
                if int(val) == 1: 
                    hamCount +=1
                else:
                    spamCount +=1
    f.close()
    return hamCount, spamCount, labelDict
### Preprocessing: building vocabulary + frequency:
def buildingVocabulary(trainingDir = 'processed_traning'):
    # read spam label to identify which doc is spam
    (hamCount, spamCount, labelDict) = preProcessingLabel()    
    # for each file in the training directory
    onlyfiles = [f for f in listdir(trainingDir) if isfile(join(trainingDir, f))]
    wordList = set()     # vocabulary
    spamWordDict = {}    # frequency in spam doc
    hamWordDict = {}     # frequency in ham doc
    stop_words = set(stopwords.words('english'))        #english stop words
    for i in onlyfiles:
        #  ignore random dir
        if i == '.DS_Store':
            continue
        inputFile = trainingDir+'/'+i
        s = open(inputFile,encoding="latin-1").read()
        tokens = s.split()
        for word in tokens:
            ### Processing word here##################
            # ignore stopwords
            if word in stop_words:
                continue
            if word in ':;.,/\(\)\[\]<>': # remove normal character
                continue
            # lemmatize word 
            lmWord = word
            ##########################################
            ### Update vocabulary#####################
            if lmWord in wordList:
                # if in ham, update ham
                if labelDict[i] == 1: # ham
                    if lmWord in hamWordDict:
                        hamWordDict[lmWord] += 1
                    else:
                        hamWordDict[lmWord] = 1
                # if in spam, update spam
                if labelDict[i] == 0: # spam
                    if lmWord in spamWordDict:
                        spamWordDict[lmWord] += 1
                    else:
                        spamWordDict[lmWord] = 1
            else:
                # add new word to word list.
                wordList.add(lmWord)
                # if in ham, update ham
                if labelDict[i] == 1: # ham
                    if lmWord in hamWordDict:
                        hamWordDict[lmWord] += 1
                    else:
                        hamWordDict[lmWord] = 1
                # if in spam, update spam
                if labelDict[i] == 0: # spam
                    if lmWord in spamWordDict:
                        spamWordDict[lmWord] += 1
                    else:
                        spamWordDict[lmWord] = 1
            ##########################################
    return wordList, spamWordDict, hamWordDict
# Naive Bayes for each file 
def NBeachFile(fileName,hamCount,spamCount,vocabulary,spamWordDict,hamWordDict):
    spamWordCount = sum(spamWordDict.values())   #count word freq in spam
    hamWordCount = sum(hamWordDict.values())     #count word freq in ham
    vocabSize = len(vocabulary)
    stop_words = set(stopwords.words('english')) #english stop words
    spamProb = math.log(spamCount/(hamCount+spamCount))
    hamProb = math.log(hamCount/(hamCount+spamCount))
    s = open(fileName,encoding="latin-1").read()
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
            ### compute spamscore
            freqInSpam = 0
            freqInHam = 0
            if lmWord in spamWordDict:
                freqInSpam = spamWordDict[lmWord]
            else:
                freqInSpam = 0
            if lmWord in hamWordDict:
                freqInHam = hamWordDict[lmWord]
            else:
                freqInHam = 0
            # log to avoid becoming 0
            spamProb += math.log((freqInSpam+1)/(vocabSize+spamWordCount))
            hamProb += math.log((freqInHam+1)/(vocabSize+hamWordCount))
    if hamProb > spamProb:
        return 1
    else:
        return 0
# Naive bayes implementation
def NB(testDir, vocabulary,spamWordDict,hamWordDict):
    # read spam label to identify which doc is spam
    (hamCount, spamCount, labelDict) = preProcessingLabel()
    # for each file in the testing directory
    onlyfiles = [f for f in listdir(testDir) if isfile(join(testDir, f))]
    tp = 0 # true positive
    fp = 0 # false postive
    fn = 0 # false negative
    tn = 0 # true negative
    for i in onlyfiles:
        if i == '.DS_Store':
            continue
        inputFile = testDir+'/'+i
        ### use naive bayes to get prediction
        score = NBeachFile(inputFile,hamCount,spamCount,vocabulary,spamWordDict,hamWordDict)
        ### compute stat base on labelDict
        if score == 1:
            if labelDict[i] == 1:  # label 1 as positive
                tp += 1
            else: 
                fp += 1
        else:
            if labelDict[i] == 0:  # label 0 as negative
                tn += 1
            else:
                fn += 1
    return tp,fp,fn,tn
### simple stat
def computeStat(tp,fp,fn,tn):
    print('False Positive Rate:\t', fp)
    print('False Negative Rate:\t', fn)
    precision = tp/(tp+fp)
    recall = tp/(tp+fn)
    print('Recall:\t\t\t', recall)    
    print('Precision:\t\t',precision)
    fscore = 2*precision*recall/(precision + recall)
    print('F-score beta = 1: \t', fscore)


#### Running code
# preprocessing
(vocabulary,spamWordDict,hamWordDict) = buildingVocabulary('processed_training')
# perform NB on test data and compare with label
(tp,fp,fn,tn)=NB('processed_testing',vocabulary,spamWordDict,hamWordDict)
# output stat 
computeStat(tp,fp,fn,tn)

print('##########################################################')
#### Predict duc spam
print('[+] Task 3.4: spam email that past both naive bayes and SVM')
(hamCount, spamCount, labelDict) = preProcessingLabel()
spamLabel = NBeachFile('duc-spam/TEST_01327.eml',hamCount,spamCount, vocabulary,spamWordDict,hamWordDict)
print('[>>>>] Email is labeled as: ',spamLabel)