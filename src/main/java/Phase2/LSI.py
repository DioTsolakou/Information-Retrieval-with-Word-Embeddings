import numpy as np
#import pandas as pd
#import textmining as tm


def read_file():
    docxterm = np.loadtxt("../../../../docXterm.txt")
    termxdoc = np.array(docxterm.T, dtype=int)
    print("txd shape :", termxdoc.shape)
    return termxdoc


def svd_analysis(array):
    u, s_tmp, v = np.linalg.svd(array)
    #s = np.diag(s)
    s = np.zeros(array.shape)
    np.fill_diagonal(s, s_tmp)
    print("u shape :", u.shape)
    print("s shape :", s.shape)
    print("v shape :", v.shape)
    return u, s, v.T


def main():
    txd = read_file()
    txd = np.array(txd)
    u, s, v = svd_analysis(txd)
    txd_test = np.array(np.dot(np.dot(u, s), v))
    print((txd == txd_test).all())

    #txd.write_csv('matrix.csv', cutoff=1)


if __name__ == "__main__":
    main()
