import numpy as np
import tensorflow as tf
import sys
from tensorflow.examples.tutorials.mnist import input_data
from tensorflow.contrib.layers import flatten

# Due to my limitation knowledge with tensorflow
# Some functionalities of tensorflow were borrowed from:
# https://www.youtube.com/watch?v=ixkpBmKRZDw
# https://github.com/sujaybabruwad/LeNet-in-Tensorflow

BATCH_SIZE = 128
def initalizeData():
    mnist = input_data.read_data_sets("MNIST_data/", reshape=False)
    trainingX, trainingY           = mnist.train.images, mnist.train.labels
    validationX, validationY = mnist.validation.images, mnist.validation.labels
    testX, testY             = mnist.test.images, mnist.test.labels
    return trainingX, trainingY, validationX, validationY,testX, testY
# padding
def ModifyDataSet(trainingX,validationX,testX):
    print("[+] Before Shape: {}".format(trainingX[0].shape))
    trainingX      = np.pad(trainingX, ((0,0),(2,2),(2,2),(0,0)), 'constant')
    validationX = np.pad(validationX, ((0,0),(2,2),(2,2),(0,0)), 'constant')
    testX       = np.pad(testX,  ((0,0),(2,2),(2,2),(0,0)), 'constant')
    print("[+] After Shape: {}".format(trainingX[0].shape))
    return trainingX,validationX,testX
def LeNet(x):
    mu = 0
    sigma = 0.1
    #C1: Convolution 1: Input = 32x32x1. Output = 28x28x6.
    conv1_W = tf.Variable(tf.truncated_normal(shape=(5, 5, 1, 6), mean = mu, stddev = sigma))
    conv1_b = tf.Variable(tf.zeros(6))
    conv1   = tf.nn.conv2d(x, conv1_W, strides=[1, 1, 1, 1], padding='VALID') + conv1_b
    conv1 = tf.nn.tanh(conv1) #tanh
    
    #S2: Subsampling Output = 14x14x6.
    conv1 = tf.nn.max_pool(conv1, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='VALID')

    #C3: Convolution 3: Output = 10x10x16.
    conv2_W = tf.Variable(tf.truncated_normal(shape=(5, 5, 6, 16), mean = mu, stddev = sigma))
    conv2_b = tf.Variable(tf.zeros(16))
    conv2   = tf.nn.conv2d(conv1, conv2_W, strides=[1, 1, 1, 1], padding='VALID') + conv2_b
    conv2   = tf.nn.tanh(conv2)  #tanh

    # S4: Output = 5x5x16.
    conv2  = tf.nn.max_pool(conv2, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='VALID')
    fc0    = flatten(conv2)
    
    # C5. Output = 120.
    fc1_W  = tf.Variable(tf.truncated_normal(shape=(400, 120), mean = mu, stddev = sigma))
    fc1_b  = tf.Variable(tf.zeros(120))
    fc1    = tf.matmul(fc0, fc1_W) + fc1_b
    fc1    = tf.nn.tanh(fc1)   #tanh

    # F6. Output = 84.
    fc2_W  = tf.Variable(tf.truncated_normal(shape=(120, 84), mean = mu, stddev = sigma))
    fc2_b  = tf.Variable(tf.zeros(84))
    fc2    = tf.matmul(fc1, fc2_W) + fc2_b
    fc2    = tf.nn.tanh(fc2)   #tanh

    # output
    fc3_W  = tf.Variable(tf.truncated_normal(shape=(84, 10), mean = mu, stddev = sigma))
    fc3_b  = tf.Variable(tf.zeros(10))
    logits = tf.matmul(fc2, fc3_W) + fc3_b
    return logits
# evaluate function 
def evaluate(X_data, y_data,accuracy_operation,x,y):
    num_examples = len(X_data)
    total_accuracy = 0
    sess = tf.get_default_session()
    for offset in range(0, num_examples, BATCH_SIZE):
        batch_x, batch_y = X_data[offset:offset+BATCH_SIZE], y_data[offset:offset+BATCH_SIZE]
        accuracy = sess.run(accuracy_operation, feed_dict={x: batch_x, y: batch_y})
        total_accuracy += (accuracy * len(batch_x))
    return total_accuracy / num_examples
def LeNetFull(rate):
    # Parsing Data
    trainingX, trainingY, validationX, validationY,testX, testY = initalizeData()
    # Adding row and column
    trainingX,validationX,testX  = ModifyDataSet(trainingX,validationX,testX)
    # place hold for feeding
    x = tf.placeholder(tf.float32, (None, 32, 32, 1))
    y = tf.placeholder(tf.int32, (None))
    hotY = tf.one_hot(y, 10)
    logits = LeNet(x)
    cross_entropy = tf.nn.softmax_cross_entropy_with_logits_v2(labels=hotY,logits=logits)
    loss_operation = tf.reduce_mean(cross_entropy)
    optimizer = tf.train.AdamOptimizer(learning_rate = rate)
    training_operation = optimizer.minimize(loss_operation)
    correct_prediction = tf.equal(tf.argmax(logits, 1), tf.argmax(hotY, 1))
    accuracy_operation = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
    saver = tf.train.Saver()
    validation_accuracy = 0.0
    with tf.Session() as sess:
        sess.run(tf.global_variables_initializer())
        num_examples = len(trainingX)
        while validation_accuracy < 0.95:
            for offset in range(0, num_examples, BATCH_SIZE):
                end = offset + BATCH_SIZE
                batch_x, batch_y = trainingX[offset:end+BATCH_SIZE], trainingY[offset:end+BATCH_SIZE]
                sess.run(training_operation, feed_dict={x: batch_x, y: batch_y})

            validation_accuracy = evaluate(validationX, validationY,accuracy_operation,x,y)
            print("[+] Accuracy = {:.3f}".format(validation_accuracy))
######
## run the code here
if len(sys.argv) !=2:
    print('[-] Usage: python3 lenet.py learning_rate')
else:
    LeNetFull(float(sys.argv[1]))

        
