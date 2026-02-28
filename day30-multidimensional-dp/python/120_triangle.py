"""
LeetCode 120. Triangle
难度: Medium

题目描述：
给定一个三角形 triangle，找出自顶向下的最小路径和。
每一步只能移动到下一行中相邻的结点上。更正式地说，如果你正位于当前行的下标 i，
那么下一步可以移动到下一行的下标 i 或 i+1。

示例 1：
输入：triangle = [[2],[3,4],[6,5,7],[4,1,8,3]]
输出：11
解释：自顶向下的最小路径和为 2 + 3 + 5 + 1 = 11
    2
   3 4
  6 5 7
 4 1 8 3

示例 2：
输入：triangle = [[-10]]
输出：-10

【拓展练习】
1. LeetCode 64. Minimum Path Sum —— 网格中只能向右或向下走的最短路径
2. LeetCode 931. Minimum Falling Path Sum —— 方阵中相邻列的最小下降路径
3. LeetCode 1289. Minimum Falling Path Sum II —— 非相邻列的最小下降路径（Hard）
"""

from typing import List
from functools import lru_cache


class Solution:
    """
    ==================== 解法一：自底向上DP ====================

    【核心思路】
    从三角形底部开始，逐层向上合并。dp[j] 表示从当前行第 j 个位置出发
    到底部的最小路径和。每层更新 dp[j] = triangle[i][j] + min(dp[j], dp[j+1])。

    【思考过程】
    1. 自顶向下做的话，每个位置可以从上一行的 j-1 或 j 转移来，
       但边界处理较麻烦（第一列只能从 j 来，最后一列只能从 j-1 来）。

    2. 如果反过来自底向上做，dp[j] = triangle[i][j] + min(dp[j], dp[j+1])，
       不需要特殊处理边界，因为底层 dp 数组长度为 n，
       上一层长度为 n-1，恰好每个位置都能取到 dp[j] 和 dp[j+1]。

    3. 最终 dp[0] 就是从顶到底的最小路径和。

    【举例】triangle = [[2],[3,4],[6,5,7],[4,1,8,3]]
      初始 dp = [4,1,8,3]（底层）
      处理第2层 [6,5,7]:
        dp[0] = 6 + min(4,1) = 7
        dp[1] = 5 + min(1,8) = 6
        dp[2] = 7 + min(8,3) = 10
        dp = [7,6,10,3]
      处理第1层 [3,4]:
        dp[0] = 3 + min(7,6) = 9
        dp[1] = 4 + min(6,10) = 10
        dp = [9,10,10,3]
      处理第0层 [2]:
        dp[0] = 2 + min(9,10) = 11
      答案 dp[0] = 11

    【时间复杂度】O(n²)，n 为三角形行数
    【空间复杂度】O(n)，一维滚动数组
    """
    def minimumTotal(self, triangle: List[List[int]]) -> int:
        n = len(triangle)
        dp = triangle[-1][:]

        for i in range(n - 2, -1, -1):
            for j in range(i + 1):
                dp[j] = triangle[i][j] + min(dp[j], dp[j + 1])

        return dp[0]

    """
    ==================== 解法二：自顶向下DP + 记忆化 ====================

    【核心思路】
    用递归 + 记忆化 memo[i][j] 表示从位置 (i,j) 出发到底部的最小路径和。
    memo[i][j] = triangle[i][j] + min(dfs(i+1,j), dfs(i+1,j+1))。

    【思考过程】
    1. 自然地用递归来思考：从 (0,0) 出发，每步可以选择走到 (i+1,j) 或 (i+1,j+1)。
       走到底层时返回当前值。

    2. 不加记忆化，时间复杂度是 O(2^n)——每层有两个选择，共 n 层。
       但很多子问题重复计算了，比如 (2,1) 可以从 (1,0) 和 (1,1) 两条路径到达。

    3. 加上 memo，每个 (i,j) 只计算一次，总共 O(n²) 个状态。

    【举例】triangle = [[2],[3,4],[6,5,7],[4,1,8,3]]
      dfs(0,0) = 2 + min(dfs(1,0), dfs(1,1))
      dfs(1,0) = 3 + min(dfs(2,0), dfs(2,1))
      dfs(2,0) = 6 + min(4,1) = 7
      dfs(2,1) = 5 + min(1,8) = 6   ← (2,1) 只算一次
      dfs(1,0) = 3 + min(7,6) = 9
      dfs(1,1) = 4 + min(dfs(2,1), dfs(2,2))
               = 4 + min(6, 10) = 10  ← dfs(2,1) 直接查表
      dfs(0,0) = 2 + min(9,10) = 11

    【时间复杂度】O(n²)
    【空间复杂度】O(n²)，memo + 递归栈 O(n)
    """
    def minimumTotal_memo(self, triangle: List[List[int]]) -> int:
        n = len(triangle)

        @lru_cache(maxsize=None)
        def dfs(i: int, j: int) -> int:
            if i == n:
                return 0
            return triangle[i][j] + min(dfs(i + 1, j), dfs(i + 1, j + 1))

        return dfs(0, 0)
