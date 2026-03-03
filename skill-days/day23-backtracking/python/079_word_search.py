"""
LeetCode 79. Word Search
难度: Medium

题目描述：
给定一个 m x n 二维字符网格 board 和一个字符串单词 word，
如果 word 存在于网格中，返回 True；否则返回 False。
单词必须按照字母顺序，通过相邻的单元格内的字母构成，
其中"相邻"单元格是水平或垂直相邻的。同一单元格内的字母不允许被重复使用。

示例 1：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED" → True
示例 2：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "SEE" → True
示例 3：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCB" → False

【拓展练习】
1. LeetCode 212. Word Search II —— 在网格中搜索多个单词，Trie + DFS
2. LeetCode 980. Unique Paths III —— 网格中从起点到终点经过所有空格的路径数
3. LeetCode 200. Number of Islands —— 经典网格 DFS/BFS
"""

from typing import List


class Solution:
    """
    ==================== 解法一：DFS回溯（visited数组标记） ====================

    【核心思路】
    从网格中每个位置出发，尝试 DFS 匹配 word 的每个字符。
    使用一个 visited 二维数组标记已访问的格子，防止重复使用。

    【思考过程】
    1. 单词搜索的本质是在网格上做路径搜索，路径上的字符拼起来等于 word。
       → 典型的 DFS + 回溯问题。

    2. 对于 word 的第 k 个字符，当前位置 (i,j) 必须满足：
       - 在网格范围内
       - 未被访问过
       - board[i][j] == word[k]
       然后向四个方向递归匹配第 k+1 个字符。

    3. 回溯时需要把 visited[i][j] 重置为 False，允许其他路径使用这个格子。

    【举例】board = [["A","B"],["C","D"]], word = "ABDC"
      从(0,0)='A'开始，匹配word[0]='A' ✓
        → 右(0,1)='B'，匹配word[1]='B' ✓
          → 下(1,1)='D'，匹配word[2]='D' ✓
            → 左(1,0)='C'，匹配word[3]='C' ✓ → 找到！

    【时间复杂度】O(m * n * 3^L)，L为word长度，每步最多3个方向（排除来路）
    【空间复杂度】O(m * n + L)，visited数组 + 递归栈
    """
    def exist(self, board: List[List[str]], word: str) -> bool:
        m, n = len(board), len(board[0])
        visited = [[False] * n for _ in range(m)]

        def dfs(i: int, j: int, k: int) -> bool:
            if k == len(word):
                return True
            if i < 0 or i >= m or j < 0 or j >= n:
                return False
            if visited[i][j] or board[i][j] != word[k]:
                return False

            visited[i][j] = True
            for di, dj in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                if dfs(i + di, j + dj, k + 1):
                    return True
            visited[i][j] = False
            return False

        for i in range(m):
            for j in range(n):
                if dfs(i, j, 0):
                    return True
        return False

    """
    ==================== 解法二：DFS回溯（原地修改标记） ====================

    【核心思路】
    与解法一逻辑相同，但不使用额外的 visited 数组。
    访问某个格子时，将 board[i][j] 临时改为一个特殊字符（如 '#'），
    回溯时再恢复原值。这样只需 O(L) 的递归栈空间。

    【思考过程】
    1. 解法一的 visited 数组占 O(m*n) 空间，能否省掉？
       → 可以直接在 board 上做标记：把当前字符改成一个不可能出现的字符。

    2. 这是一种常见的空间优化技巧：原地修改 + 回溯恢复。
       需要注意的是，如果输入不可修改，则不能用此方法。

    3. 特殊字符选择 '#'（或任何不在 board 取值范围内的字符），
       保证 DFS 过程中不会误匹配。

    【举例】board = [["A","B"],["C","D"]], word = "ABDC"
      (0,0): 'A'→'#', 匹配word[0] ✓
        (0,1): 'B'→'#', 匹配word[1] ✓
          (1,1): 'D'→'#', 匹配word[2] ✓
            (1,0): 'C'→'#', 匹配word[3] ✓ → 返回True
            恢复'C', 恢复'D', 恢复'B', 恢复'A'

    【时间复杂度】O(m * n * 3^L)
    【空间复杂度】O(L) 仅递归栈
    """
    def existInPlace(self, board: List[List[str]], word: str) -> bool:
        m, n = len(board), len(board[0])

        def dfs(i: int, j: int, k: int) -> bool:
            if k == len(word):
                return True
            if i < 0 or i >= m or j < 0 or j >= n:
                return False
            if board[i][j] != word[k]:
                return False

            temp = board[i][j]
            board[i][j] = '#'
            for di, dj in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                if dfs(i + di, j + dj, k + 1):
                    return True
            board[i][j] = temp
            return False

        for i in range(m):
            for j in range(n):
                if dfs(i, j, 0):
                    return True
        return False
