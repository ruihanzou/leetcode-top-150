"""
LeetCode 209. Minimum Size Subarray Sum
难度: Medium

题目描述：
给定一个含有 n 个正整数的数组 nums 和一个正整数 target。
找出该数组中满足其总和大于等于 target 的长度最小的连续子数组
[nums_l, nums_{l+1}, ..., nums_{r-1}, nums_r]，并返回其长度。
如果不存在符合条件的子数组，返回 0。

示例 1：
  输入：target = 7, nums = [2,3,1,2,4,3]
  输出：2
  解释：子数组 [4,3] 是该条件下的长度最小的子数组。

示例 2：
  输入：target = 4, nums = [1,4,4]
  输出：1

示例 3：
  输入：target = 11, nums = [1,1,1,1,1,1,1,1]
  输出：0

【拓展练习】
1. LeetCode 862. Shortest Subarray with Sum at Least K —— 含负数时用单调队列
2. LeetCode 713. Subarray Product Less Than K —— 乘积约束的滑动窗口
3. LeetCode 1658. Minimum Operations to Reduce X to Zero —— 反向思考转化为子数组和
"""

from typing import List
import bisect


class Solution:
    """
    ==================== 解法一：滑动窗口 ====================

    【核心思路】
    维护一个可变大小的窗口 [left, right]，right 不断右移扩大窗口累加和，
    当窗口内的和 >= target 时，尝试收缩左边界以找到满足条件的最短子数组。

    【思考过程】
    1. 暴力做法是枚举所有子数组 O(n²)，但数组元素全为正数，
       这意味着右移 right 只会让和变大，右移 left 只会让和变小，
       具备单调性，可以用滑动窗口。

    2. right 从 0 到 n-1 扫描，每到一个位置就把 nums[right] 加到 window_sum。

    3. 只要 window_sum >= target，就更新最小长度，然后 left 右移缩小窗口。
       left 右移会减小 window_sum，直到 window_sum < target 为止。

    4. 每个元素最多被 left 和 right 各访问一次，所以总操作是 O(n)。

    【举例】target = 7, nums = [2,3,1,2,4,3]
      right=0: sum=2, <7
      right=1: sum=5, <7
      right=2: sum=6, <7
      right=3: sum=8, >=7 → len=4, 缩left: sum=8-2=6, left=1, <7
      right=4: sum=6+4=10, >=7 → len=4, 缩: sum=10-3=7, left=2, >=7 → len=3
               缩: sum=7-1=6, left=3, <7
      right=5: sum=6+3=9, >=7 → len=3, 缩: sum=9-2=7, left=4, >=7 → len=2
               缩: sum=7-4=3, left=5, <7
      最终答案 = 2

    【时间复杂度】O(n) —— left 和 right 各最多走 n 步
    【空间复杂度】O(1)
    """
    def minSubArrayLen_sliding_window(self, target: int, nums: List[int]) -> int:
        n = len(nums)
        left = 0
        window_sum = 0
        min_len = n + 1

        for right in range(n):
            window_sum += nums[right]
            while window_sum >= target:
                min_len = min(min_len, right - left + 1)
                window_sum -= nums[left]
                left += 1

        return min_len if min_len <= n else 0

    """
    ==================== 解法二：前缀和 + 二分查找 ====================

    【核心思路】
    构建前缀和数组 prefix，其中 prefix[i] = nums[0] + ... + nums[i-1]。
    子数组 nums[l..r] 的和 = prefix[r+1] - prefix[l]。
    对于每个右端点 r，我们需要找最大的 l 使得 prefix[r+1] - prefix[l] >= target，
    即 prefix[l] <= prefix[r+1] - target。
    由于所有元素为正，prefix 严格递增，可以用二分查找。

    【思考过程】
    1. 前缀和将"子数组求和"转化为"两个前缀和的差"。

    2. 要求和 >= target，即 prefix[r+1] - prefix[l] >= target，
       等价于 prefix[l] <= prefix[r+1] - target。

    3. 由于 nums 全为正数，prefix 严格递增。
       所以对于固定的 r，满足条件的 l 是连续的一段 [0, ..., l_max]。
       我们要找的是 l_max（最大的满足条件的 l），这样 r - l_max + 1 最小。

    4. 等价于：用二分查找找 prefix 中 <= prefix[r+1] - target 的最大下标。
       或者反过来：找 prefix 中 >= prefix[r+1] - target + 1 的最小下标，
       然后前一个就是 l_max。
       更简洁的做法：用 bisect_right 查找 prefix[r+1] - target，
       返回的位置 pos 意味着 prefix[pos-1] <= prefix[r+1] - target，
       即 l_max = pos - 1。

    【举例】target = 7, nums = [2,3,1,2,4,3]
      prefix = [0, 2, 5, 6, 8, 12, 15]
      r=3 (prefix[4]=8): 找 prefix[l] <= 8-7=1 → bisect_right(prefix, 1)=1
          l_max=0, 长度=3-0+1=4
      r=4 (prefix[5]=12): 找 prefix[l] <= 12-7=5 → bisect_right(prefix, 5)=3
          l_max=2, 长度=4-2+1=3
      r=5 (prefix[6]=15): 找 prefix[l] <= 15-7=8 → bisect_right(prefix, 8)=5
          l_max=4, 长度=5-4+1=2
      答案 = 2

    【时间复杂度】O(n log n) —— n 次二分查找
    【空间复杂度】O(n) —— 前缀和数组
    """
    def minSubArrayLen_prefix_binary(self, target: int, nums: List[int]) -> int:
        n = len(nums)
        prefix = [0] * (n + 1)
        for i in range(n):
            prefix[i + 1] = prefix[i] + nums[i]

        min_len = n + 1
        for r in range(n):
            needed = prefix[r + 1] - target
            pos = bisect.bisect_right(prefix, needed, 0, r + 1)
            if pos > 0:
                min_len = min(min_len, r - (pos - 1) + 1)

        return min_len if min_len <= n else 0
