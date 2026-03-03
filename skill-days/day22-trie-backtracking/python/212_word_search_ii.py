"""
LeetCode 212. Word Search II
难度: Hard

题目描述：
给定一个 m x n 二维字符网格 board 和一个单词列表 words，
返回所有二维网格中的单词。单词必须按照字母顺序，通过相邻的单元格内的字母构成，
其中"相邻"单元格是那些水平相邻或垂直相邻的单元格。
同一个单元格内的字母在一个单词中不允许被重复使用。

示例：
  board = [["o","a","a","n"],
           ["e","t","a","e"],
           ["i","h","k","r"],
           ["i","f","l","v"]]
  words = ["oath","pea","eat","rain"]
  输出 ["eat","oath"]

【拓展练习】
1. LeetCode 79. Word Search —— 在棋盘中搜索单个单词
2. LeetCode 208. Implement Trie (Prefix Tree) —— 标准 Trie 实现
3. LeetCode 211. Design Add and Search Words Data Structure —— Trie+通配符搜索
"""

from typing import List


class Solution:
    """
    ==================== 解法一：Trie + 回溯 ====================

    【核心思路】
    将所有待搜索单词构建成 Trie，然后在棋盘上每个位置启动 DFS，
    沿着 Trie 的路径同步搜索。当 Trie 路径上的节点标记了一个完整单词时，
    将该单词加入结果集。

    【思考过程】
    1. 暴力做法是对每个单词在棋盘上做一次 DFS（解法二），但如果 words 很多，
       会重复探索大量相同的路径。

    2. 优化思路：把所有单词建成 Trie，用 Trie 来引导 DFS 方向。
       - DFS 扩展到下一个字符时，先检查 Trie 中是否有对应子节点。
       - 如果没有，立即剪枝（不可能匹配任何单词）。
       - 如果有，继续深入。如果到达某个单词的末尾，记录结果。

    3. 关键优化——"找到即删除"：
       找到一个单词后，把 Trie 中对应的 word 标记清除，避免重复收录。
       进一步地，如果某个节点的所有子节点都被删光了（不再前缀匹配任何剩余单词），
       可以把该子节点删除，加速后续剪枝。

    4. 回溯时恢复棋盘状态（将标记过的格子还原）。

    【举例】
      words = ["oath","oat"], board 含 'o','a','t','h'
      Trie: root → o → a → t (word="oat") → h (word="oath")

      从 board 的 'o' 出发 DFS：
        'o' → Trie root.children['o'] 存在，继续
        'a' → Trie .children['a'] 存在，继续
        't' → Trie .children['t'] 存在，且 word="oat"，收录 "oat"
        'h' → Trie .children['h'] 存在，且 word="oath"，收录 "oath"
        回溯

    【时间复杂度】O(m * n * 4^L)，L 为最长单词长度，实际因 Trie 剪枝远小于此
    【空间复杂度】O(sum of word lengths)，Trie 存储空间
    """
    def findWords_trie(self, board: List[List[str]], words: List[str]) -> List[str]:
        root = {}
        for word in words:
            node = root
            for ch in word:
                node = node.setdefault(ch, {})
            node['#'] = word

        m, n = len(board), len(board[0])
        result = []

        def dfs(i, j, parent):
            ch = board[i][j]
            node = parent[ch]

            if '#' in node:
                result.append(node['#'])
                del node['#']

            board[i][j] = '!'
            for di, dj in ((0, 1), (0, -1), (1, 0), (-1, 0)):
                ni, nj = i + di, j + dj
                if 0 <= ni < m and 0 <= nj < n and board[ni][nj] in node:
                    dfs(ni, nj, node)
            board[i][j] = ch

            if not node:
                del parent[ch]

        for i in range(m):
            for j in range(n):
                if board[i][j] in root:
                    dfs(i, j, root)

        return result

    """
    ==================== 解法二：暴力逐词搜索 ====================

    【核心思路】
    对 words 中的每个单词，在棋盘上每个位置尝试 DFS 匹配。
    如果找到匹配则将该单词加入结果集。

    【思考过程】
    1. 最直接的想法：对每个单词单独做一次"Word Search"（LeetCode 79）。
    2. 对于每个单词，从棋盘上的每个位置出发做 DFS，检查是否能拼出该单词。
    3. DFS 过程中标记已访问的格子，避免重复使用。
    4. 缺点：如果 words 有 W 个单词，每个单词都要遍历整个棋盘并做 DFS，
       大量重复工作。相比 Trie+回溯，没有前缀共享和剪枝优化。

    【举例】
      words = ["oath","pea"], board 含 'o','a','t','h'
      搜索 "oath"：从每个 'o' 出发 DFS，找到 o→a→t→h 路径 → 收录
      搜索 "pea"：从每个 'p' 出发 DFS，找不到 → 不收录

    【时间复杂度】O(W * m * n * 4^L)，W 为单词数，L 为最长单词长度
    【空间复杂度】O(L)，递归栈深度
    """
    def findWords_brute(self, board: List[List[str]], words: List[str]) -> List[str]:
        m, n = len(board), len(board[0])
        result = []

        def dfs(i, j, word, idx):
            if idx == len(word):
                return True
            if i < 0 or i >= m or j < 0 or j >= n:
                return False
            if board[i][j] != word[idx]:
                return False

            temp = board[i][j]
            board[i][j] = '!'
            found = (dfs(i + 1, j, word, idx + 1) or
                     dfs(i - 1, j, word, idx + 1) or
                     dfs(i, j + 1, word, idx + 1) or
                     dfs(i, j - 1, word, idx + 1))
            board[i][j] = temp
            return found

        for word in words:
            found = False
            for i in range(m):
                for j in range(n):
                    if board[i][j] == word[0] and dfs(i, j, word, 0):
                        result.append(word)
                        found = True
                        break
                if found:
                    break

        return result
