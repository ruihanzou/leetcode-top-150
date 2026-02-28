"""
LeetCode 63. Unique Paths II
难度: Medium

题目描述：
一个机器人位于一个 m x n 网格的左上角。机器人每次只能向下或者向右移动一步。
机器人试图达到网格的右下角。现在考虑网格中有障碍物（用 1 表示），
问总共有多少条不同的路径？网格中的空位用 0 表示。

示例 1：
输入：obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
输出：2
解释：有两条路径：右→右→下→下 和 下→下→右→右

示例 2：
输入：obstacleGrid = [[0,1],[0,0]]
输出：1

【拓展练习】
1. LeetCode 62. Unique Paths —— 无障碍版本，可用组合数学直接求解
2. LeetCode 64. Minimum Path Sum —— 网格最短路径和
3. LeetCode 980. Unique Paths III —— 必须经过所有空格的路径数（Hard，回溯）
"""

from typing import List


class Solution:
    """
    ==================== 解法一：二维DP ====================

    【核心思路】
    dp[i][j] 表示从 (0,0) 到 (i,j) 的不同路径数。
    若 (i,j) 是障碍，dp[i][j] = 0；
    否则 dp[i][j] = dp[i-1][j] + dp[i][j-1]。

    【思考过程】
    1. 无障碍版本中 dp[i][j] = dp[i-1][j] + dp[i][j-1]，
       因为只能从上方或左方到达。

    2. 有障碍时，障碍格子不可达，所以 dp[i][j] = 0。
       其他格子的转移不变。

    3. 初始化需要注意：第一行/第一列如果某个格子是障碍，
       那该格子以及之后的格子都不可达（路被挡住了）。

    4. 特殊情况：起点或终点是障碍，直接返回 0。

    【举例】obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
      dp 初始化:
        第一行: [1, 1, 1]（无障碍，全部可达）
        第一列: [1, 1, 1]（无障碍，全部可达）
      dp[1][1] = 0（障碍）
      dp[1][2] = 0 + dp[0][2] = 0 + 1 = 1
      dp[2][1] = dp[1][1] + dp[2][0] = 0 + 1 = 1
      dp[2][2] = dp[1][2] + dp[2][1] = 1 + 1 = 2
      答案 = 2

    【时间复杂度】O(m * n)
    【空间复杂度】O(m * n)
    """
    def uniquePathsWithObstacles(self, obstacleGrid: List[List[int]]) -> int:
        m, n = len(obstacleGrid), len(obstacleGrid[0])
        if obstacleGrid[0][0] == 1 or obstacleGrid[m - 1][n - 1] == 1:
            return 0

        dp = [[0] * n for _ in range(m)]
        for i in range(m):
            if obstacleGrid[i][0] == 1:
                break
            dp[i][0] = 1
        for j in range(n):
            if obstacleGrid[0][j] == 1:
                break
            dp[0][j] = 1

        for i in range(1, m):
            for j in range(1, n):
                if obstacleGrid[i][j] == 0:
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1]

        return dp[m - 1][n - 1]

    """
    ==================== 解法二：一维DP ====================

    【核心思路】
    用一维数组 dp[j] 滚动更新。处理第 i 行时，
    dp[j] 的旧值就是上方的路径数，dp[j-1] 的新值就是左方的路径数。
    遇到障碍时 dp[j] = 0。

    【思考过程】
    1. 和无障碍版本的空间优化思路一样：dp[i][j] 只依赖 dp[i-1][j] 和 dp[i][j-1]。

    2. 从左到右遍历时，dp[j]（未更新）= 上方值，dp[j-1]（已更新）= 左方值。

    3. 障碍处理：如果当前格是障碍，dp[j] = 0，
       这会影响后续格子（该格不贡献路径）。

    【举例】obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
      初始 dp = [1, 1, 1]（第0行）
      处理第1行:
        j=0: grid[1][0]=0, dp[0]=1（不变）
        j=1: grid[1][1]=1, dp[1]=0（障碍）
        j=2: grid[1][2]=0, dp[2]=dp[2]+dp[1]=1+0=1
        dp = [1, 0, 1]
      处理第2行:
        j=0: dp[0]=1
        j=1: grid[2][1]=0, dp[1]=dp[1]+dp[0]=0+1=1
        j=2: grid[2][2]=0, dp[2]=dp[2]+dp[1]=1+1=2
      答案 dp[2] = 2

    【时间复杂度】O(m * n)
    【空间复杂度】O(n)
    """
    def uniquePathsWithObstacles_optimized(self, obstacleGrid: List[List[int]]) -> int:
        m, n = len(obstacleGrid), len(obstacleGrid[0])
        if obstacleGrid[0][0] == 1 or obstacleGrid[m - 1][n - 1] == 1:
            return 0

        dp = [0] * n
        dp[0] = 1

        for i in range(m):
            for j in range(n):
                if obstacleGrid[i][j] == 1:
                    dp[j] = 0
                elif j > 0:
                    dp[j] += dp[j - 1]

        return dp[n - 1]
