import numpy as np
#import sys
#import pandas as pd
#import textmining as tm


def read_files():
    docxterm = np.loadtxt("../../../../docXterm.txt")
    termxdoc = np.array(docxterm.T, dtype=int)
    print("txd shape :", termxdoc.shape)

    queriesxterm = np.loadtxt("../../../../queriesXterm.txt")
    termxqueries = np.array(queriesxterm.T, dtype=int)
    print("txq shape :", termxqueries.shape)

    termxdoc = np.array(termxdoc)
    termxqueries = np.array(termxqueries)
    return termxdoc, termxqueries


def svd_analysis(array):
    u, s_tmp, v = np.linalg.svd(array)
    s = np.zeros(array.shape)
    np.fill_diagonal(s, s_tmp)
    #print("u shape :", u.shape)
    #print("s shape :", s.shape)
    #print("v shape :", v.shape)
    return u, s, v


def create_ranked_matrix(u, s, v, rank):
    uk = u[:, :rank]
    sk = s[:rank, :rank]
    vk = v[:rank, :]
    ak = np.dot(np.dot(uk, sk), vk)
    ak = np.around(ak)
    return ak, uk, sk, vk


def calculate_cos_similarity(uk, sk, vk, txq):
    qTk = np.dot(np.dot(txq.T, uk), sk) # qTk = (64, k)
    enumerator = np.dot(qTk, vk) # enum = (64, 3204) = qxd
    denominator = np.linalg.norm(qTk) * np.linalg.norm(vk)
    similarity = enumerator/denominator
    similarity = np.array(similarity)
    return similarity


def main():
    txd, txq = read_files()
    u, s, v = svd_analysis(txd)
    txd_test = np.array(np.dot(np.dot(u, s), v))
    txd_test = np.around(txd_test)
    #print("txd_test shape : ", txd_test.shape)
    #print("txd contents : ", txd)
    #print("txd_test contents : ", txd_test)
    print((txd == txd_test).all())

    rank = argv[1]
    ak, uk, sk, vk = create_ranked_matrix(u, s, v, rank)
    np.savetext('ak_matrix.txt', ak, delimiter=' ')
    similarity_matrix = calculate_cos_similarity(uk, sk, vk, txq)
    np.savetext('similarity_matrix.txt', similarity_matrix, delimiter=' ')


if __name__ == "__main__":
    main()
