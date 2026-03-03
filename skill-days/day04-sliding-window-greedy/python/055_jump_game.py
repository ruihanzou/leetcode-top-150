"""
LeetCode 55. Jump Game
难度: Medium

题目描述：
给定一个非负整数数组 nums，你最初位于数组的第一个位置。
数组中的每个元素代表你在该位置可以跳跃的最大长度。
判断你是否能够到达最后一个位置。

示例：nums = [2,3,1,1,4] → 输出 true（0→1→4 或 0→2→3→4）
      nums = [3,2,1,0,4] → 输出 false（无论怎么跳都会到达位置3，而位置3的值为0）

约束：1 <= nums.length <= 10^4, 0 <= nums[i] <= 10^5

【拓展练习】
1. LeetCode 45. Jump Game II —— 求到达终点的最少跳跃次数
2. LeetCode 1306. Jump Game III —— 可以左右跳，判断能否到达值为0的位置
3. LeetCode 1345. Jump Game IV —— 可以跳到相同值的位置，BFS求最短步数
"""

from typing import List


class Solution:
    """
    ==================== 解法一：贪心（维护最远可达位置） ====================

    【核心思路】
    从左到右遍历，维护当前能到达的最远位置 max_reach。
    如果 max_reach >= n-1，说明能到终点；如果某个位置 i > max_reach，说明到不了。

    【思考过程】
    1. 如果位置 i 可达，那么从 i 最远能跳到 i + nums[i]。
       所以 max_reach = max(max_reach, i + nums[i])。

    2. 只要 max_reach >= i，位置 i 就是可达的，可以继续往后走。

    3. 一旦遇到 i > max_reach，说明位置 i 不可达，返回 False。

    【举例】nums = [2,3,1,1,4], n=5
      i=0: max_reach=max(0,0+2)=2
      i=1: 1<=2可达, max_reach=max(2,1+3)=4 >= 4(n-1) → True

      nums = [3,2,1,0,4], n=5
      i=0: max_reach=3
      i=1: max_reach=3
      i=2: max_reach=3
      i=3: max_reach=3
      i=4: 4>3 → False

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def canJump_greedy(self, nums: List[int]) -> bool:
        max_reach = 0

        for i, jump in enumerate(nums):
            if i > max_reach:
                return False
            max_reach = max(max_reach, i + jump)
            if max_reach >= len(nums) - 1:
                return True

        return True

    """
    ==================== 解法二：动态规划 ====================

    【核心思路】
    dp[i] 表示位置 i 是否可达。
    对于每个可达的位置 i，将 dp[i+1..i+nums[i]] 标记为可达。

    【思考过程】
    1. 初始 dp[0] = True（起点可达）。

    2. 遍历每个位置 i，如果 dp[i] = True，
       则从 i 可以跳到 i+1, i+2, ..., i+nums[i]，标记这些位置为 True。

    3. 最后检查 dp[n-1]。

    【举例】nums = [2,3,1,1,4]
      dp = [T, F, F, F, F]
      i=0(可达, nums[0]=2): dp[1]=T, dp[2]=T → [T, T, T, F, F]
      i=1(可达, nums[1]=3): dp[2]=T, dp[3]=T, dp[4]=T → [T, T, T, T, T]
      dp[4] = True ✓

    【时间复杂度】O(n²)
    【空间复杂度】O(n)
    """
    def canJump_dp(self, nums: List[int]) -> bool:
        n = len(nums)
        dp = [False] * n
        dp[0] = True

        for i in range(n):
            if not dp[i]:
                continue
            for j in range(1, nums[i] + 1):
                if i + j >= n:
                    break
                dp[i + j] = True
            if dp[n - 1]:
                return True

        return dp[n - 1]

    """
    ==================== 解法三：从后往前贪心 ====================

    【核心思路】
    从后往前扫描，维护"最靠左的能到达终点的位置" last_good。
    如果某位置 i 能跳到 last_good（即 i + nums[i] >= last_good），
    则 i 也能到达终点，更新 last_good = i。

    【思考过程】
    1. 最终目标是到达 n-1，初始化 last_good = n-1。

    2. 从 n-2 往前扫描：如果 i + nums[i] >= last_good，
       说明从 i 可以直接或间接到达终点，更新 last_good = i。

    3. 最后检查 last_good == 0，即起点是否能到达终点。

    【举例】nums = [2,3,1,1,4], last_good = 4
      i=3: 3+1=4 >= 4 ✓ → last_good=3
      i=2: 2+1=3 >= 3 ✓ → last_good=2
      i=1: 1+3=4 >= 2 ✓ → last_good=1
      i=0: 0+2=2 >= 1 ✓ → last_good=0
      last_good == 0 → True

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def canJump_backward(self, nums: List[int]) -> bool:
        last_good = len(nums) - 1

        for i in range(len(nums) - 2, -1, -1):
            if i + nums[i] >= last_good:
                last_good = i

        return last_good == 0
