"""
LeetCode 36. Valid Sudoku
难度: Medium

题目描述：
请你判断一个 9x9 的数独是否有效。只需要根据以下规则，验证已经填入的数字是否有效即可：
1. 数字 1-9 在每一行只能出现一次。
2. 数字 1-9 在每一列只能出现一次。
3. 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。
注意：一个有效的数独（部分已被填充）不一定是可解的，只需验证已填入的数字是否满足规则。
空白格用 '.' 表示。

示例：
输入：board =
[["5","3",".",".","7",".",".",".","."]
,["6",".",".","1","9","5",".",".","."]
,[".","9","8",".",".",".",".","6","."]
,["8",".",".",".","6",".",".",".","3"]
,["4",".",".","8",".","3",".",".","1"]
,["7",".",".",".","2",".",".",".","6"]
,[".","6",".",".",".",".","2","8","."]
,[".",".",".","4","1","9",".",".","5"]
,[".",".",".",".","8",".",".","7","9"]]
输出：true

【拓展练习】
1. LeetCode 37. Sudoku Solver —— 回溯法解数独，在验证有效的基础上填充所有空格
2. LeetCode 2133. Check if Every Row and Every Column Contains All Numbers —— 简化版矩阵验证
3. LeetCode 1001. Grid Illumination —— 利用行/列/对角线哈希计数的技巧
"""

from typing import List
from collections import defaultdict


class Solution:
    """
    ==================== 解法一：三个集合数组 ====================

    【核心思路】
    用三组集合分别记录每行、每列、每个3x3宫格中已经出现过的数字。
    遍历棋盘，对每个数字检查它是否在对应的行/列/宫格集合中已存在。

    【思考过程】
    1. 数独有效的条件是三个维度都不重复：行、列、宫格。
       → 分别用9个集合记录每个维度已出现的数字。

    2. 对于位置 (i, j) 的数字，它属于：
       - 第 i 行
       - 第 j 列
       - 第 (i//3)*3 + j//3 个宫格（将9个宫格编号为0-8）

    3. 如果在任一集合中发现重复，立即返回False。
       遍历完所有格子都没重复，返回True。

    【举例】board[0][0]='5'
      行集合 rows[0] 加入 '5'
      列集合 cols[0] 加入 '5'
      宫格集合 boxes[0] 加入 '5'  （宫格编号 = (0//3)*3 + 0//3 = 0）
      后续如果 board[1][1]='5'，检查 boxes[0] 已有 '5' → 返回 False

    【时间复杂度】O(1) — 固定遍历 81 个格子
    【空间复杂度】O(1) — 集合最多存储 81 个数字
    """
    def isValidSudoku_sets(self, board: List[List[str]]) -> bool:
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxes = [set() for _ in range(9)]

        for i in range(9):
            for j in range(9):
                c = board[i][j]
                if c == '.':
                    continue

                box_idx = (i // 3) * 3 + j // 3

                if c in rows[i] or c in cols[j] or c in boxes[box_idx]:
                    return False

                rows[i].add(c)
                cols[j].add(c)
                boxes[box_idx].add(c)

        return True

    """
    ==================== 解法二：位运算替代集合 ====================

    【核心思路】
    用一个整数的低 9 位分别表示数字 1-9 是否出现过，替代 set。
    检查某一位是否为1来判断是否重复，用 OR 来添加。

    【思考过程】
    1. 数字只有 1-9 共9种，一个整数足够用位来表示。
       用第 k 位（bit k）表示数字 k 是否出现过。

    2. 检查是否重复：(mask >> num) & 1 == 1 说明已出现。
       标记已出现：mask |= (1 << num)。

    3. 相比 set，位运算更紧凑，运算更快。

    【举例】处理数字 '5'（num=5）
      rows[0] = 0b000000000，第5位是0 → 未出现
      rows[0] |= (1<<5) → rows[0] = 0b000100000
      下次再遇到 '5'：(rows[0]>>5)&1 = 1 → 已出现，返回False

    【时间复杂度】O(1)
    【空间复杂度】O(1)
    """
    def isValidSudoku_bit(self, board: List[List[str]]) -> bool:
        rows = [0] * 9
        cols = [0] * 9
        boxes = [0] * 9

        for i in range(9):
            for j in range(9):
                c = board[i][j]
                if c == '.':
                    continue

                num = int(c)
                mask = 1 << num
                box_idx = (i // 3) * 3 + j // 3

                if (rows[i] & mask) or (cols[j] & mask) or (boxes[box_idx] & mask):
                    return False

                rows[i] |= mask
                cols[j] |= mask
                boxes[box_idx] |= mask

        return True

    """
    ==================== 解法三：一次遍历+编码key ====================

    【核心思路】
    对每个数字，生成三个字符串 key 分别表示"第r行出现了d"、"第c列出现了d"、
    "第b宫格出现了d"，全部放入一个全局 set。如果已存在说明重复。

    【思考过程】
    1. 前两种方法用三组数组记录，能否合并成一个集合？
       → 只要 key 的编码能区分行/列/宫格，就不会误判。

    2. 编码方式使用元组，比字符串更高效：
       - 行：(d, 'r', i)
       - 列：(d, 'c', j)
       - 宫格：(d, 'b', box_idx)
       三者不会冲突。

    3. 利用 set 的 add 前检查是否已存在，代码非常简洁。

    【举例】board[0][0]='5'
      添加 ('5','r',0) → 首次
      添加 ('5','c',0) → 首次
      添加 ('5','b',0) → 首次
      若 board[2][1]='5'：
      添加 ('5','r',2) → 首次
      添加 ('5','c',1) → 首次
      添加 ('5','b',0) → 已存在（重复！）→ 返回 False

    【时间复杂度】O(1)
    【空间复杂度】O(1)
    """
    def isValidSudoku_encode(self, board: List[List[str]]) -> bool:
        seen = set()

        for i in range(9):
            for j in range(9):
                c = board[i][j]
                if c == '.':
                    continue

                box_idx = (i // 3) * 3 + j // 3

                row_key = (c, 'r', i)
                col_key = (c, 'c', j)
                box_key = (c, 'b', box_idx)

                if row_key in seen or col_key in seen or box_key in seen:
                    return False

                seen.add(row_key)
                seen.add(col_key)
                seen.add(box_key)

        return True
