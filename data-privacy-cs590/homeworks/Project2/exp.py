import argparse
import pandas as pd
import hist
from sklearn import linear_model
from sklearn.model_selection import train_test_split

# parse commandline arguments
parser = argparse.ArgumentParser(description='Process adult data set.')
parser.add_argument('-e', '--epsilon', type=float, default=0.1, help="noise parameter used in Differential Privacy")
parser.add_argument('-f', '--file', type=str, default="adult.data.txt",
                    help="input file location, default: adult.data.txt")
argument = parser.parse_args()
epsilon = argument.epsilon

# detect invalid epsilon

if epsilon < 0:
    print("epsilon cannot be less than 0, receiving epsilon=%f" % epsilon)
    exit(1)

# === 1. read data
all_attributes = ["age", "workclass", "fnlwgt", "education", "education-num",
                  "marital-status", "occupation", "relationship", "race", "sex",
                  "capital-gain", "capital-loss", "hours-per-week", "native-country", "class"]
num_attr = ['age', 'fnlwgt', 'education-num', 'capital-gain', 'capital-loss', 'hours-per-week']

dataset = pd.read_csv(argument.file, names=all_attributes)



total=0.0
for i in range(0,10):
	# ==== 2 and 3 project and add noise
	noisy_hist, hist_attr, hist_buckets = hist.project_hist(dataset, num_attr, all_attributes, epsilon)

	# ==== 4. re-generate
	synthe_data_list = []
	for key in noisy_hist:
	    synthe_data_list += [list(key)] * int(round(noisy_hist[key]))

	synthe_data_df = pd.DataFrame(synthe_data_list, columns=hist_attr)
	# ==== 5. train
	# define the target variable (dependent variable) as y
	y = synthe_data_df['class']
	# remove label column
	synthe_data_df = synthe_data_df.drop(columns=['class'], axis=1)
	synthe_data_df = pd.get_dummies(synthe_data_df)

	# create training and testing vars
	X_train, X_test, y_train, y_test = train_test_split(synthe_data_df, y, test_size=0.2)

	# fit a model
	lr = linear_model.LogisticRegression()
	lr.fit(X_train, y_train)

	# ==== 6. evaluate
	preds = lr.predict(X_test)
	print((i+1),'\t', lr.score(X_test, y_test))
	total+=lr.score(X_test, y_test)
print('Average:', total/10)
