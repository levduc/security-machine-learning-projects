import pandas as pd
import math
import random

def project_hist(dataset, num_attr, all_attributes, epsilon):
    """
    project the dataset into a differential private histogram
    :param dataset: a panda dataframe of adult data set
    :param num_attr: list of numeric column name in the adult data set
    :param all_attributes: list of all column names in the data set
    :param epsilon: parameter used for adding noise
    :return: hist, hist_attr, hist_buckets:
        hist: a histogram based on hist_attr and hist_buckets. The
            hist should satisfies bounded DP.
        hist_attr: a list of your interested column
        hist_buckets: definition of your histogram
    """
    # 2.1 define the histogram
    hist_attr = [
    'age', 
    'class', 
    'capital-gain',
    'capital-loss',
    'education-num',
    'marital-status'
    # "workclass", 
    # "education", 
    # 'hours-per-week',
    # "fnlwgt",
    # "occupation", 
    # "relationship", 
    # "race",
    # "sex"
    ]
    drop_attr = [x for x in all_attributes if x not in hist_attr]
    hist_buckets = {
        'age': {
            'bin_edge': [0,40,50],
            'bin_label': ['other','40-49']
        },
        'class': {
            'bin_label': ['0', '1'],
            'mapping': {
                ' <=50K': '0',
                ' >50K': '1'
            }
        },
        'capital-gain':{
            'bin_edge': [0,1,10000],
            'bin_label': ['0-1','2-99999']
        },
        'capital-loss':{
            'bin_edge': [0,1,10000],
            'bin_label': ['0-1','2-5000']
        },
        'education-num': {
            'bin_edge': [1, 10, 14, 17],
            'bin_label': ['0-9','10-13','14-16']
        },
        'marital-status': {
            'bin_label': ['married', 'single'],
            'mapping': {
                ' Married-civ-spouse' : 'married',
                ' Married-spouse-absent' : 'single',
                ' Never-married' : 'single',
                ' Divorced' : 'single',
                ' Separated' : 'single',
                ' Married-AF-spouse' : 'married',
                ' Widowed' : 'single',
            }
        }
    }
    # 2.2 make changes to the dataset
    dataset = dataset.drop(columns=drop_attr)
    for attr in hist_attr:
        if attr in num_attr:
            dataset[attr] = pd.cut(dataset[attr], hist_buckets[attr]['bin_edge'],
                                   right=False, labels=hist_buckets[attr]['bin_label'])
        else:
            dataset.replace({attr: hist_buckets[attr]['mapping']}, inplace=True)
    for cat in hist_attr:
        dataset = dataset[dataset[cat] != ' ?']
    # 2.4 project by counting
    dataset['index'] = dataset.index
    hist = dataset.groupby(hist_attr).count().to_dict()['index']
    for key in hist:
        if pd.isnull(hist[key]):
            hist[key] = 0
    # 3 add noise, use epsilon here, you can remove function
    # add_noise_to_hist, if you do not need it.
    hist = add_noise_to_hist(hist, epsilon)
    return hist, hist_attr, hist_buckets

def add_noise_to_hist(hist, epsilon):
    sens = 2                         # not replace
    beta = sens/epsilon              # beta
    for block in hist:
        U = random.uniform(-.5, .5)  # uniform U between -.5 and .5
        #noise equation from wikipedia
        noise =-math.copysign(beta*math.log(1-2*math.fabs(U)),U)
        hist[block] += noise         # adding noise
    return hist
