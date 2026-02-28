"""
LeetCode 64. Minimum Path Sum
难度: Medium

题目描述：
给定一个包含非负整数的 m x n 网格 grid，请找出一条从左上角到右下角的路径，
使得路径上的数字总和最小。每次只能向下或者向右移动一步。

示例 1：
输入：grid = [[1,3,1],[1,5,1],[4,2,1]]
输出：7
解释：路径 1→3→1→1→1 的总和最小。

示例 2：
输入：grid = [[1,2,3],[4,5,6]]
输出：12

【拓展练习】
1. LeetCode 62. Unique Paths —— 统计从左上到右下的路径数
2. LeetCode 120. Triangle —— 三角形中的最小路径和
3. LeetCode 741. Cherry Pickup —— 两次遍历网格的最大收益（Hard）
"""

from typing import List


class Solution:
    """
    ==================== 解法一：二维DP ====================

    【核心思路】
    dp[i][j] 表示从 (0,0) 到 (i,j) 的最小路径和。
    转移方程：dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])。
    第一行只能从左边来，第一列只能从上面来。

    【思考过程】
    1. 每个格子只能从上方或左方到达，所以到达 (i,j) 的最小代价
       = 格子本身的值 + min(从上方来的代价, 从左方来的代价)。

    2. 第一行的格子只能一路向右走过来，dp[0][j] = 前缀和。
       第一列的格子只能一路向下走过来，dp[i][0] = 前缀和。

    3. 其余格子按行/列遍历，保证 dp[i-1][j] 和 dp[i][j-1] 已经计算好。

    【举例】grid = [[1,3,1],[1,5,1],[4,2,1]]
      dp 初始化第一行: [1, 4, 5]
      dp 初始化第一列: [1, 2, 6]
      dp[1][1] = 5 + min(4, 2) = 7
      dp[1][2] = 1 + min(5, 7) = 6
      dp[2][1] = 2 + min(7, 6) = 8
      dp[2][2] = 1 + min(6, 8) = 7
      答案 dp[2][2] = 7

    【时间复杂度】O(m * n)
    【空间复杂度】O(m * n)
    """
    def minPathSum(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dp = [[0] * n for _ in range(m)]

        dp[0][0] = grid[0][0]
        for j in range(1, n):
            dp[0][j] = dp[0][j - 1] + grid[0][j]
        for i in range(1, m):
            dp[i][0] = dp[i - 1][0] + grid[i][0]

        for i in range(1, m):
            for j in range(1, n):
                dp[i][j] = grid[i][j] + min(dp[i - 1][j], dp[i][j - 1])

        return dp[m - 1][n - 1]

    """
    ==================== 解法二：一维DP空间优化 ====================

    【核心思路】
    逐行处理网格。dp[j] 在处理第 i 行之前存储的是第 i-1 行的结果。
    处理第 i 行时，dp[j] 从左到右更新：
    dp[j] = grid[i][j] + min(dp[j], dp[j-1])。
    其中 dp[j]（右边的）是旧值=上方，dp[j-1] 是刚更新的=左方。

    【思考过程】
    1. 二维 dp 中，dp[i][j] 只依赖 dp[i-1][j]（正上方）和 dp[i][j-1]（正左方）。
       这种"只依赖上一行和当前行左边"的模式可以压缩成一维。

    2. 从左到右更新 dp[j] 时：
       - dp[j] 还没被覆盖，存的是上一行的值 → 相当于 dp[i-1][j]
       - dp[j-1] 刚刚被更新成这一行的值 → 相当于 dp[i][j-1]
       所以 dp[j] = grid[i][j] + min(dp[j], dp[j-1]) 是正确的。

    3. 第一列特殊处理：dp[0] += grid[i][0]（只能从上面来）。

    【举例】grid = [[1,3,1],[1,5,1],[4,2,1]]
      初始 dp = [1, 4, 5]（第0行前缀和）
      处理第1行:
        dp[0] = 1+1 = 2
        dp[1] = 5 + min(4, 2) = 7
        dp[2] = 1 + min(5, 7) = 6
        dp = [2, 7, 6]
      处理第2行:
        dp[0] = 2+4 = 6
        dp[1] = 2 + min(7, 6) = 8
        dp[2] = 1 + min(6, 8) = 7
      答案 dp[2] = 7

    【时间复杂度】O(m * n)
    【空间复杂度】O(n)
    """
    def minPathSum_optimized(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dp = [0] * n

        dp[0] = grid[0][0]
        for j in range(1, n):
            dp[j] = dp[j - 1] + grid[0][j]

        for i in range(1, m):
            dp[0] += grid[i][0]
            for j in range(1, n):
                dp[j] = grid[i][j] + min(dp[j], dp[j - 1])

        return dp[n - 1]
