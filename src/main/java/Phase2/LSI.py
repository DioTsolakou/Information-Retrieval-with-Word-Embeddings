import numpy as np
import pandas as pd
#import textmining as tm


def read_file():
    docxterm = np.loadtxt("../../../../docXterm.txt")
    termxdoc = np.array(docxterm.T, dtype=int)
    print("txd shape :", termxdoc.shape)
    return termxdoc


def svd_analysis(array):
    u, s, v = np.linalg.svd(array)
    print("u shape :", u.shape)
    print("s shape :", s.shape)
    print("v shape :", v.shape)
    return u, s, v


def main():
    txd = read_file()
    u, v, t = svd_analysis(txd)

    #txd.write_csv('matrix.csv', cutoff=1)


if __name__ == "__main__":
    main()
