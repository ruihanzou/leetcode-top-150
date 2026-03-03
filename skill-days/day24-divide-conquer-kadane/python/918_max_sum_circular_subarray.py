"""
LeetCode 918. Maximum Sum Circular Subarray
难度: Medium

题目描述：
给定一个长度为 n 的环形整数数组 nums，返回 nums 的非空子数组的最大可能和。
环形数组意味着数组的末端与开头相连。即 nums[i] 的下一个元素是 nums[(i+1)%n]，
nums[i] 的前一个元素是 nums[(i-1+n)%n]。
子数组最多只能包含固定缓冲区 nums 中的每个元素一次。

示例 1：nums = [1,-2,3,-2] → 输出 3（子数组 [3]）
示例 2：nums = [5,-3,5] → 输出 10（子数组 [5,5]，环形首尾相连）
示例 3：nums = [-3,-2,-3] → 输出 -2（子数组 [-2]）

【拓展练习】
1. LeetCode 53. Maximum Subarray —— 非环形版本，Kadane算法基础
2. LeetCode 152. Maximum Product Subarray —— 最大乘积子数组
3. LeetCode 1191. K-Concatenation Maximum Sum —— k次拼接数组的最大子数组和
"""

from typing import List
from collections import deque


class Solution:
    """
    ==================== 解法一：Kadane 变体 ====================

    【核心思路】
    环形子数组的最大和有两种情况：
    (1) 最大子数组不跨越边界 → 普通 Kadane 求 max_sum
    (2) 最大子数组跨越边界 → 等价于"总和 - 中间最小子数组和"
    答案 = max(max_sum, total_sum - min_sum)。
    特殊情况：如果所有元素都为负，min_sum = total_sum，此时答案取 max_sum。

    【思考过程】
    1. 环形子数组如果跨越首尾边界，那它包含数组尾部一段 + 头部一段。
       中间"不被选中的部分"恰好是一个连续子数组。

    2. 要最大化跨边界的子数组和，等价于最小化中间不选的部分。
       即 total_sum - min_subarray_sum。

    3. 所以只需一次遍历同时求 max_sum 和 min_sum：
       - max_sum：标准 Kadane（维护以当前结尾的最大和）
       - min_sum：对称的 Kadane（维护以当前结尾的最小和）

    4. 边界情况：如果所有元素为负，max_sum = max(nums)（负数），
       而 total_sum - min_sum = 0（选了空子数组），但题目要求非空。
       所以当 max_sum < 0 时，直接返回 max_sum。

    【举例】nums = [5, -3, 5]
      total_sum = 7
      Kadane 求 max_sum：
        i=0: cur_max=5, max_sum=5
        i=1: cur_max=max(5-3,-3)=2, max_sum=5
        i=2: cur_max=max(2+5,5)=7, max_sum=7
      Kadane 求 min_sum：
        i=0: cur_min=5, min_sum=5
        i=1: cur_min=min(5-3,-3)=-3, min_sum=-3
        i=2: cur_min=min(-3+5,5)=2, min_sum=-3
      答案 = max(7, 7-(-3)) = max(7, 10) = 10
      对应跨边界子数组 [5, 5]（跳过中间的 -3）

    【时间复杂度】O(n) 一次遍历
    【空间复杂度】O(1)
    """
    def maxSubarraySumCircular_kadane(self, nums: List[int]) -> int:
        total_sum = 0
        max_sum, cur_max = float('-inf'), 0
        min_sum, cur_min = float('inf'), 0

        for num in nums:
            total_sum += num
            cur_max = max(cur_max + num, num)
            max_sum = max(max_sum, cur_max)
            cur_min = min(cur_min + num, num)
            min_sum = min(min_sum, cur_min)

        if max_sum < 0:
            return max_sum
        return max(max_sum, total_sum - min_sum)

    """
    ==================== 解法二：单调队列（前缀和 + 滑动窗口） ====================

    【核心思路】
    将数组"拉直"成长度 2n 的前缀和序列。环形子数组的和 = prefix[j] - prefix[i]，
    其中 j - i <= n。用单调递增队列维护窗口内的最小前缀和，
    对每个 j 求 prefix[j] - min(prefix[i])，i ∈ [j-n, j-1]。

    【思考过程】
    1. 环形数组可以看作把数组复制一份接在后面：nums + nums。
       任何长度不超过 n 的子数组都对应复制数组中的一段。

    2. 前缀和 prefix[0..2n]，子数组 [i,j) 的和 = prefix[j] - prefix[i]。
       约束：1 <= j-i <= n，即窗口大小最多 n。

    3. 固定 j，要最大化 prefix[j] - prefix[i] → 最小化 prefix[i]。
       i 的范围是 [j-n, j-1]，这是一个滑动窗口求最小值的问题。

    4. 用单调递增队列（双端队列）维护窗口内的最小前缀和：
       - 队头是当前窗口最小值
       - 新元素从队尾入，维护递增性（弹出队尾所有 >= 新元素的值）
       - 窗口滑出时从队头弹出

    【举例】nums = [5, -3, 5], n=3
      拉直前缀和：prefix = [0, 5, 2, 7, 12, 9, 14]
      j=1: 窗口[0,0], min prefix=0, ans=5-0=5
      j=2: 窗口[0,1], min prefix=0, ans=max(5,2-0)=5
      j=3: 窗口[0,2], min prefix=0, ans=max(5,7-0)=7
      j=4: 窗口[1,3], min prefix=2, ans=max(7,12-2)=10
      j=5: 窗口[2,4], min prefix=2, ans=max(10,9-2)=10
      j=6: 窗口[3,5], min prefix=7, ans=max(10,14-7)=10
      答案 10

    【时间复杂度】O(n) 每个元素入队出队各一次
    【空间复杂度】O(n) 队列和前缀和数组
    """
    def maxSubarraySumCircular_deque(self, nums: List[int]) -> int:
        n = len(nums)
        prefix = [0] * (2 * n + 1)
        for i in range(2 * n):
            prefix[i + 1] = prefix[i] + nums[i % n]

        ans = float('-inf')
        dq = deque([0])

        for j in range(1, 2 * n + 1):
            while dq and dq[0] < j - n:
                dq.popleft()
            ans = max(ans, prefix[j] - prefix[dq[0]])
            while dq and prefix[dq[-1]] >= prefix[j]:
                dq.pop()
            dq.append(j)

        return ans
