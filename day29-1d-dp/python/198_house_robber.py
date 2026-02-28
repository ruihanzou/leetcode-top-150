"""
LeetCode 198. House Robber
难度: Medium

题目描述：
你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，
影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，
如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
给定一个代表每个房屋存放金额的非负整数数组，计算在不触动警报的情况下，
一夜之内能够偷窃到的最高金额。

示例：nums = [2,7,9,3,1] → 输出 12（偷第1、3、5间：2+9+1=12）

【拓展练习】
1. LeetCode 213. House Robber II —— 房屋围成环，首尾不能同时偷
2. LeetCode 337. House Robber III —— 房屋排列成二叉树，树形DP
3. LeetCode 740. Delete and Earn —— 本质等价于打家劫舍
"""

from typing import List


class Solution:
    """
    ==================== 解法一：DP 数组 ====================

    【核心思路】
    dp[i] 表示前 i 间房能偷到的最大金额。
    对于第 i 间房，要么不偷（dp[i] = dp[i-1]），要么偷（dp[i] = dp[i-2] + nums[i]），
    取较大者。

    【思考过程】
    1. 这是一个典型的"选或不选"模型。如果偷了第 i 间，就不能偷第 i-1 间，
       所以收益是 dp[i-2] + nums[i]；如果不偷第 i 间，收益就是 dp[i-1]。

    2. 状态转移方程：dp[i] = max(dp[i-1], dp[i-2] + nums[i])

    3. 基础情况：
       dp[0] = nums[0]（只有一间房，必偷）
       dp[1] = max(nums[0], nums[1])（两间房，偷金额大的那间）

    【举例】nums = [2, 7, 9, 3, 1]
      dp[0] = 2
      dp[1] = max(2, 7) = 7
      dp[2] = max(dp[1], dp[0]+9) = max(7, 11) = 11
      dp[3] = max(dp[2], dp[1]+3) = max(11, 10) = 11
      dp[4] = max(dp[3], dp[2]+1) = max(11, 12) = 12
      答案 = 12

    【时间复杂度】O(n)
    【空间复杂度】O(n)
    """
    def rob_dp(self, nums: List[int]) -> int:
        n = len(nums)
        if n == 1:
            return nums[0]

        dp = [0] * n
        dp[0] = nums[0]
        dp[1] = max(nums[0], nums[1])

        for i in range(2, n):
            dp[i] = max(dp[i - 1], dp[i - 2] + nums[i])

        return dp[n - 1]

    """
    ==================== 解法二：DP 空间优化 ====================

    【核心思路】
    dp[i] 只依赖 dp[i-1] 和 dp[i-2]，用两个变量滚动即可。

    【思考过程】
    1. 和爬楼梯一样，当前状态只依赖前两个状态。
       用 prev2 表示 dp[i-2]，prev1 表示 dp[i-1]。

    2. 每步更新：
       curr = max(prev1, prev2 + nums[i])
       prev2 = prev1
       prev1 = curr

    3. 空间从 O(n) 降到 O(1)。

    【举例】nums = [2, 7, 9, 3, 1]
      初始: prev2=2, prev1=7
      i=2: curr=max(7, 2+9)=11,  prev2=7,  prev1=11
      i=3: curr=max(11, 7+3)=11, prev2=11, prev1=11
      i=4: curr=max(11, 11+1)=12, prev2=11, prev1=12
      答案 = 12

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def rob_opt(self, nums: List[int]) -> int:
        n = len(nums)
        if n == 1:
            return nums[0]

        prev2, prev1 = nums[0], max(nums[0], nums[1])

        for i in range(2, n):
            curr = max(prev1, prev2 + nums[i])
            prev2 = prev1
            prev1 = curr

        return prev1
