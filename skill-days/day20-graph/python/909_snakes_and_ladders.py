"""
LeetCode 909. Snakes and Ladders
难度: Medium

题目描述：
给你一个大小为 n x n 的整数矩阵 board，方格按从 1 到 n² 编号。
编号采用 Boustrophedon 风格（蛇形排列），从棋盘左下角开始，每一行交替方向编号。

玩家从棋盘编号 1（board[n-1][0]）出发。每一轮，玩家需要从当前方格 curr 开始出发，
执行以下操作：
- 选定目标方格 next，编号范围 [curr+1, min(curr+6, n²)]（模拟掷骰子）
- 如果 next 对应的格子有蛇或梯子（board值 != -1），则传送到 board 值对应的编号
- 如果 next 对应的格子没有蛇或梯子，则停留在 next
返回到达编号 n² 所需的最少移动次数；如果不可能，返回 -1。

示例：
  board = [[-1,-1,-1,-1,-1,-1],
           [-1,-1,-1,-1,-1,-1],
           [-1,-1,-1,-1,-1,-1],
           [-1,35,-1,-1,13,-1],
           [-1,-1,-1,-1,-1,-1],
           [-1,15,-1,-1,-1,-1]]
  → 输出 4
  解释：1→2(梯子到15)→15→16(梯子到35)→35→36

【拓展练习】
1. LeetCode 1197. Minimum Knight Moves —— BFS 求棋盘最短路径
2. LeetCode 847. Shortest Path Visiting All Nodes —— 状态压缩BFS
3. LeetCode 127. Word Ladder —— BFS 求最短转换序列
"""

from typing import List
from collections import deque


class Solution:
    """
    ==================== 解法一：BFS ====================

    【核心思路】
    将棋盘上的每个编号视为图中的一个节点。从编号 curr 可以到达
    curr+1, curr+2, ..., curr+6（如果有蛇/梯子则传送到目标位置）。
    从编号 1 出发 BFS 找到编号 n² 的最短路径。

    【思考过程】
    1. 问题本质是求从节点 1 到节点 n² 的最短路径 → BFS。

    2. 关键难点是坐标转换：编号 → 行列坐标。
       - 棋盘从左下角开始编号，且奇数行从左到右，偶数行从右到左（蛇形）。
       - 编号 s（1-indexed）：
         从底部数第几行：row_from_bottom = (s-1) // n
         实际行号：r = n - 1 - row_from_bottom
         该行内位置：col_in_row = (s-1) % n
         如果 row_from_bottom 是偶数，列号 c = col_in_row（从左到右）
         如果 row_from_bottom 是奇数，列号 c = n - 1 - col_in_row（从右到左）

    3. BFS 流程：
       - 起点编号 1，目标编号 n²
       - 每次从当前编号尝试走 1~6 步
       - 如果目标位置有蛇/梯子，传送到对应编号
       - 用 visited 集合防止重复访问
       - 第一次到达 n² 时的步数就是答案

    4. 注意：到达蛇/梯子时必须传送，不能选择不传送。

    【举例】6x6 棋盘
      编号1(位置[5,0]) → 掷骰子可到2~7
      编号2 → board对应位置有梯子值35 → 传送到35
      编号35 → 掷骰子可到36(即n²=36) → 到达终点
      但中间可能有更短路径，BFS保证找到最短

      实际最短路径：1→2(→15)→16(→35)→36，共4步

    【时间复杂度】O(n²)，每个编号最多入队一次
    【空间复杂度】O(n²)，visited 集合和队列
    """
    def snakesAndLadders(self, board: List[List[int]]) -> int:
        n = len(board)
        target = n * n

        def get_board_value(s: int) -> int:
            row_from_bottom = (s - 1) // n
            col_in_row = (s - 1) % n
            r = n - 1 - row_from_bottom
            c = col_in_row if row_from_bottom % 2 == 0 else n - 1 - col_in_row
            return board[r][c]

        visited = set()
        visited.add(1)
        queue = deque([(1, 0)])

        while queue:
            curr, moves = queue.popleft()
            for dice in range(1, 7):
                nxt = curr + dice
                if nxt > target:
                    break

                val = get_board_value(nxt)
                if val != -1:
                    nxt = val

                if nxt == target:
                    return moves + 1

                if nxt not in visited:
                    visited.add(nxt)
                    queue.append((nxt, moves + 1))

        return -1
