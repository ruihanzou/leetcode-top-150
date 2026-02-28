"""
LeetCode 45. Jump Game II
难度: Medium

题目描述：
给定一个长度为 n 的非负整数数组 nums，你最初位于 nums[0]。
每个元素 nums[i] 代表从位置 i 最多可以向前跳 nums[i] 步。
题目保证你总能到达 nums[n-1]。返回到达最后一个位置的最少跳跃次数。

示例：nums = [2,3,1,1,4] → 输出 2（0→1→4）
      nums = [2,3,0,1,4] → 输出 2（0→1→4）

约束：1 <= nums.length <= 10^4, 0 <= nums[i] <= 1000, 保证可以到达 nums[n-1]

【拓展练习】
1. LeetCode 55. Jump Game —— 判断能否到达终点
2. LeetCode 1306. Jump Game III —— 可以左右跳，判断可达性
3. LeetCode 1024. Video Stitching —— 类似的区间覆盖贪心
"""

from typing import List


class Solution:
    """
    ==================== 解法一：贪心/BFS分层遍历 ====================

    【核心思路】
    把问题看作 BFS：第 0 层是起点，每层扩展能跳到的所有位置。
    层数就是最少跳跃次数。用 cur_end 和 farthest 模拟分层。

    【思考过程】
    1. 类比 BFS 的层序遍历：
       - 第 0 步能到 [0]
       - 第 1 步能到 [1, nums[0]]
       - 第 2 步能到 [cur_end+1, farthest]

    2. cur_end 表示当前这一"步"的最远边界。
       遍历到 cur_end 时，这一步的所有位置都考虑过了，步数+1，
       cur_end 更新为这一步内所有位置能到的最远处 farthest。

    3. 一旦 cur_end >= n-1，说明当前步数内已经能到终点。

    【举例】nums = [2,3,1,1,4], n=5
      jumps=0, cur_end=0, farthest=0
      i=0: farthest=max(0,0+2)=2, i==cur_end → jumps=1, cur_end=2
      i=1: farthest=max(2,1+3)=4
      i=2: farthest=max(4,2+1)=4, i==cur_end → jumps=2, cur_end=4
      cur_end>=4 → 返回 jumps=2

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def jump_greedy(self, nums: List[int]) -> int:
        n = len(nums)
        jumps = 0
        cur_end = 0
        farthest = 0

        for i in range(n - 1):
            farthest = max(farthest, i + nums[i])
            if i == cur_end:
                jumps += 1
                cur_end = farthest
                if cur_end >= n - 1:
                    break

        return jumps

    """
    ==================== 解法二：动态规划 ====================

    【核心思路】
    dp[i] 表示从起点到达位置 i 的最少跳跃次数。
    对每个位置 i，遍历它能跳到的所有位置 j，更新 dp[j] = min(dp[j], dp[i]+1)。

    【思考过程】
    1. dp[0] = 0，其余初始化为 ∞。

    2. 对于可达位置 i（dp[i] < ∞），从 i 可以跳到 i+1, i+2, ..., i+nums[i]。
       dp[j] = min(dp[j], dp[i] + 1)。

    3. 由于从左到右遍历，先到达的位置 dp 值一定更小或相等，
       所以可以提前结束（dp[n-1] 一旦被更新就是最优）。

    【举例】nums = [2,3,1,1,4]
      dp = [0, ∞, ∞, ∞, ∞]
      i=0: dp[1]=1, dp[2]=1 → [0, 1, 1, ∞, ∞]
      i=1: dp[2]=min(1,2)=1, dp[3]=2, dp[4]=2 → [0, 1, 1, 2, 2]
      dp[4] = 2 ✓

    【时间复杂度】O(n²) 最坏情况
    【空间复杂度】O(n)
    """
    def jump_dp(self, nums: List[int]) -> int:
        n = len(nums)
        dp = [float('inf')] * n
        dp[0] = 0

        for i in range(n):
            if dp[i] == float('inf'):
                continue
            for j in range(1, nums[i] + 1):
                if i + j >= n:
                    break
                dp[i + j] = min(dp[i + j], dp[i] + 1)
            if dp[n - 1] != float('inf'):
                return dp[n - 1]

        return dp[n - 1]

    """
    ==================== 解法三：从后往前贪心 ====================

    【核心思路】
    每次从后往前找到能跳到当前目标的最左位置，跳过去，步数+1。
    重复直到回到起点。

    【思考过程】
    1. 目标从 n-1 开始。找到最左的位置 i 满足 i + nums[i] >= target。

    2. 选最左的原因：跳得越远越好，这样总步数越少。

    3. 将 target 更新为 i，步数+1，直到 target == 0。

    4. 每一轮需要扫描 [0, target-1]，总复杂度 O(n²)。

    【举例】nums = [2,3,1,1,4]
      target=4:
        i=0: 0+2=2<4, i=1: 1+3=4>=4 → 最左位置=1, jumps=1, target=1
      target=1:
        i=0: 0+2=2>=1 → 最左位置=0, jumps=2, target=0
      target==0 → 返回 2

    【时间复杂度】O(n²)
    【空间复杂度】O(1)
    """
    def jump_backward(self, nums: List[int]) -> int:
        jumps = 0
        target = len(nums) - 1

        while target > 0:
            for i in range(target):
                if i + nums[i] >= target:
                    target = i
                    jumps += 1
                    break

        return jumps
