"""
LeetCode 53. Maximum Subarray
难度: Medium

题目描述：
给你一个整数数组 nums，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），
返回其最大和。

示例：nums = [-2,1,-3,4,-1,2,1,-5,4] → 输出 6
  解释：连续子数组 [4,-1,2,1] 的和最大，为 6。

【拓展练习】
1. LeetCode 918. Maximum Sum Circular Subarray —— 环形数组版本的最大子数组和
2. LeetCode 152. Maximum Product Subarray —— 最大乘积子数组，需同时维护最大最小值
3. LeetCode 121. Best Time to Buy and Sell Stock —— Kadane算法思想的经典变体
"""

from typing import List


class Solution:
    """
    ==================== 解法一：Kadane 算法（动态规划） ====================

    【核心思路】
    维护"以当前元素结尾的最大子数组和" cur_max。
    对于每个新元素，要么接上前面的子数组（cur_max + nums[i]），
    要么从自己开始新的子数组（nums[i]），取较大者。
    全局最大值 global_max 记录过程中出现的最大 cur_max。

    【思考过程】
    1. 定义子问题：dp[i] = 以 nums[i] 结尾的最大子数组和。
       转移：dp[i] = max(dp[i-1] + nums[i], nums[i])
           = max(dp[i-1], 0) + nums[i]
       含义：如果前面累积的和是正的，接上有利；否则不如从头开始。

    2. dp[i] 只依赖 dp[i-1]，所以只需一个变量 cur_max 即可，空间 O(1)。

    3. 答案 = max(dp[0], dp[1], ..., dp[n-1])，即全局最大值。

    【举例】nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
      i=0: cur_max=max(-2,-2)=-2,  global_max=-2
      i=1: cur_max=max(-2+1,1)=1,  global_max=1
      i=2: cur_max=max(1-3,-3)=-2, global_max=1
      i=3: cur_max=max(-2+4,4)=4,  global_max=4
      i=4: cur_max=max(4-1,-1)=3,  global_max=4
      i=5: cur_max=max(3+2,2)=5,   global_max=5
      i=6: cur_max=max(5+1,1)=6,   global_max=6
      i=7: cur_max=max(6-5,-5)=1,  global_max=6
      i=8: cur_max=max(1+4,4)=5,   global_max=6
      答案 6

    【时间复杂度】O(n) 一次遍历
    【空间复杂度】O(1)
    """
    def maxSubArray_kadane(self, nums: List[int]) -> int:
        cur_max = global_max = nums[0]
        for num in nums[1:]:
            cur_max = max(cur_max + num, num)
            global_max = max(global_max, cur_max)
        return global_max

    """
    ==================== 解法二：分治法 ====================

    【核心思路】
    将数组从中间一分为二：
    - 最大子数组要么完全在左半边
    - 要么完全在右半边
    - 要么跨越中间（从中间向两边延伸）
    递归求左半和右半的最大子数组，再求跨中间的最大子数组，三者取最大。

    【思考过程】
    1. 分治的关键是"跨中间"的情况怎么处理。
       从 mid 向左扫描，累加取最大 → left_cross。
       从 mid+1 向右扫描，累加取最大 → right_cross。
       跨中间最大 = left_cross + right_cross。

    2. 左半和右半递归求解，base case 是单个元素。

    3. T(n) = 2T(n/2) + O(n)（合并步 O(n) 扫描中间）→ O(n log n)。
       比 Kadane 慢，但这是分治法的经典范例。

    【举例】nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
      mid=4, 分成 [-2,1,-3,4] 和 [-1,2,1,-5,4]
      跨中间：从 index 4 往左累加取最大 → 4+(-3)+1+(-2)=0, max=4
              从 index 5 往右累加取最大 → (-1), (-1+2)=1, (1+1)=2, max=2
              跨中间 = 4 + 2 = 6
      三者取最大 → 6

    【时间复杂度】O(n log n)
    【空间复杂度】O(log n) 递归栈
    """
    def maxSubArray_divide(self, nums: List[int]) -> int:
        def solve(lo, hi):
            if lo == hi:
                return nums[lo]
            mid = (lo + hi) // 2

            left_max = solve(lo, mid)
            right_max = solve(mid + 1, hi)

            left_cross = float('-inf')
            s = 0
            for i in range(mid, lo - 1, -1):
                s += nums[i]
                left_cross = max(left_cross, s)

            right_cross = float('-inf')
            s = 0
            for i in range(mid + 1, hi + 1):
                s += nums[i]
                right_cross = max(right_cross, s)

            return max(left_max, right_max, left_cross + right_cross)

        return solve(0, len(nums) - 1)

    """
    ==================== 解法三：前缀和 ====================

    【核心思路】
    子数组 nums[i..j] 的和 = prefix[j+1] - prefix[i]。
    要最大化 prefix[j+1] - prefix[i]（其中 i <= j），
    等价于对每个 j，找 j 之前（含 0）的最小 prefix[i]。
    从左到右扫描，维护前缀和最小值即可。

    【思考过程】
    1. prefix[0] = 0, prefix[k] = nums[0] + ... + nums[k-1]。
       子数组 [i,j] 的和 = prefix[j+1] - prefix[i]。

    2. 固定右端 j，要最大化和 → 最小化 prefix[i]，其中 0 <= i <= j。
       → 扫描过程中维护 min_prefix = min(prefix[0..j])。

    3. 对每个位置 j+1，更新 ans = max(ans, prefix[j+1] - min_prefix)，
       然后更新 min_prefix = min(min_prefix, prefix[j+1])。

    4. 本质上和 Kadane 算法等价，但从前缀和的视角理解，
       与"买卖股票"问题（LeetCode 121）异曲同工。

    【举例】nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
      prefix = [0, -2, -1, -4, 0, -1, 1, 2, -3, 1]
      扫描过程中 min_prefix 从 0 开始：
      j=0: prefix[1]=-2, ans=-2-0=-2, min_prefix=min(0,-2)=-2
      j=1: prefix[2]=-1, ans=max(-2,-1-(-2))=1, min_prefix=-2
      ...
      j=6: prefix[7]=2, ans=max(5,2-(-4))=6, min_prefix=-4
      最终 ans=6

    【时间复杂度】O(n)
    【空间复杂度】O(1) 不需要存储完整前缀和数组
    """
    def maxSubArray_prefix(self, nums: List[int]) -> int:
        ans = float('-inf')
        prefix_sum = 0
        min_prefix = 0

        for num in nums:
            prefix_sum += num
            ans = max(ans, prefix_sum - min_prefix)
            min_prefix = min(min_prefix, prefix_sum)

        return ans
