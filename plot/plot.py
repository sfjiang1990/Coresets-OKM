import matplotlib
import matplotlib.pyplot as plt
import numpy as np
import gzip
import csv

def plot_data(name):
    plt.figure(name)
    x = []
    y = []
    with gzip.open('../data/data.csv.gz', 'rt') as csvfile:
        csvreader = csv.reader(csvfile)
        for row in csvreader:
            x.append(float(row[0]))
            y.append(float(row[1]))

    x = np.array(x)
    y = np.array(y)
    plt.rcParams.update({'font.size': 26})
    plt.xlabel('longtitude')
    plt.ylabel('latitude')
    plt.axes().set_aspect(aspect=1.0/1.0)
    plt.plot(x, y, 'k,')
    plt.show()

def plot_eva_solver(tag):
    plt.figure(tag)
    plt.rcParams.update({'font.size': 32})
    x = []
    y = []
    with open('../data/evaluate_solver_' + tag + '.csv') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            x.append(float(row[0]))
            y.append(float(row[1]))
    x = np.array(x)
    y = np.array(y)
    plt.xlabel('numer of samples')
    plt.ylabel('objective value')
    plt.plot(x, y, 'k', lw = 6)

def main():
    # type 42 is to truetype; to avoid type 3!
    matplotlib.rcParams['pdf.fonttype'] = 42
    matplotlib.rcParams['ps.fonttype'] = 42
    plot_eva_solver('10p')
    plot_eva_solver('100p')
    plot_data('')
    plt.show()

if __name__ == '__main__':
    main()

