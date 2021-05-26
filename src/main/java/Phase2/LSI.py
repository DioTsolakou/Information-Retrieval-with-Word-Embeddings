import os
import numpy as np


class QueryData:
    def __init__(self, id):
        self.id = id


class DocData:
    def __init__(self, id):
        self.id = id


class ScoreDocs:
    def __init__(self, qid, docid, score):
        self.qid = qid
        self.docid = docid
        self.score = score

    def get_qid(self):
        return self.qid

    def get_docid(self):
        return self.docid

    def get_score(self):
        return self.score


def read_files():
    docxterm = np.loadtxt("../../../../docXterm.txt")
    termxdoc = np.array(docxterm.T, dtype=int)
    print("txd shape :", termxdoc.shape)

    queriesxterm = np.loadtxt("../../../../queryXterm.txt")
    termxqueries = np.array(queriesxterm.T, dtype=int)
    print("txq shape :", termxqueries.shape)

    return termxdoc, termxqueries


def read_svd_matrix(txtfile):
    matrix = np.loadtxt("{0}".format(txtfile))
    return np.array(matrix)


def svd_analysis(array):
    u, s_tmp, v = np.linalg.svd(array)
    s = np.zeros(array.shape)
    np.fill_diagonal(s, s_tmp)
    print("u shape :", u.shape)
    print("s shape :", s.shape)
    print("v shape :", v.shape)
    return u, s, v


def create_ranked_matrix(u, s, v, rank):
    uk = u[:, :rank]
    sk = s[:rank, :rank]
    vk = v[:rank, :]
    ak = np.dot(np.dot(uk, sk), vk)
    ak = np.around(ak)
    print("Finished creation of ranked matrices")
    return ak, uk, sk, vk


def calculate_cos_similarity(uk, sk, vk, txq):
    qTk = np.dot(np.dot(txq.T, uk), sk)  # qTk = (64, k)
    enumerator = np.dot(qTk, vk)  # enum = (64, 3204) = qxd
    denominator = np.linalg.norm(qTk) * np.linalg.norm(vk)
    similarity = enumerator / denominator
    similarity = np.array(similarity)
    print("Finished calculation of cosine similarity")
    return similarity


def cmpsd(sd1, sd2):
    if sd1.get_qid() > sd2.get_qid():
        return 1
    elif sd1.get_qid() < sd2.get_qid():
        return -1
    else:
        if sd1.get_score() > sd2.get_score():
            return -1
        elif sd1.get_score() < sd2.get_score():
            return 1
        else:
            return 0


def create_file_for_trec_eval(array, topK, rank):
    array = np.around(array, 5)

    if os.path.isfile("our_results_top{0}_r{1}.txt".format(topK, rank)):
        os.remove("our_results_top{0}_r{1}.txt".format(topK, rank))

    file = open("our_results_top{0}_r{1}.txt".format(topK, rank), "x")
    scoreDocsList = []
    for i in range(array.shape[0]):
        qList = []
        for j in range(array.shape[1]):
            qList.append(ScoreDocs(i + 1, j + 1, array[i][j]))
        sortedQ = sorted(qList, key=lambda x: (x.get_score()), reverse=True)
        topKQ = sortedQ[:topK]
        scoreDocsList.extend(topKQ)

    for t in scoreDocsList:
        if t.get_qid() < 10:
            t.qid = "0" + str(t.get_qid())
        while len(str(t.get_docid())) < 4:
            t.docid = "0" + str(t.get_docid())
        file.write("{0}\t0\t{1}\t0\t{2}\tstandard_run_id\n".format(t.get_qid(), t.get_docid(), t.get_score()))

    file.close()
    print("Finished creating readable file for trec eval")


def main():
    txd, txq = read_files()
    u, s, v = svd_analysis(txd)

    choice = int(input("Do you want to save the u, s and v matrices from svd?"
                       "\n Press 1 for yes, 0 for no\nCaution : Large file size"))

    if choice == 1:
        np.savetxt('u_matrix.txt', u, delimiter=' ')
        np.savetxt('s_matrix.txt', s, delimiter=' ')
        np.savetxt('v_matrix.txt', v, delimiter=' ')

    rank = input("Give rank approximation : ")  # 50, 150, 300
    rank = int(rank)
    topK = input("Give topK docs : ")  # 20, 30, 50
    topK = int(topK)

    ak, uk, sk, vk = create_ranked_matrix(u, s, v, rank)
    np.savetxt('ak_matrix.txt', ak, delimiter=' ')
    similarity_matrix = calculate_cos_similarity(uk, sk, vk, txq)
    np.savetxt('similarity_matrix.txt', similarity_matrix, delimiter=' ')
    create_file_for_trec_eval(similarity_matrix, topK, rank)


if __name__ == "__main__":
    main()
