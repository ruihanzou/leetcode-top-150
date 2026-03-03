"""
LeetCode 162. Find Peak Element
难度: Medium

题目描述：
峰值元素是指其值严格大于左右相邻值的元素。
给你一个整数数组 nums，找到峰值元素并返回其索引。
数组可能包含多个峰值，返回任何一个峰值所在位置即可。
你可以假设 nums[-1] = nums[n] = -∞。
必须实现时间复杂度为 O(log n) 的算法。

示例：nums = [1,2,3,1] → 输出 2（nums[2]=3 是峰值）
      nums = [1,2,1,3,5,6,4] → 输出 5（返回1或5都可以）

【拓展练习】
1. LeetCode 852. Peak Index in a Mountain Array —— 保证单峰的山脉数组
2. LeetCode 1901. Find a Peak Element II —— 二维矩阵找峰值
3. LeetCode 153. Find Minimum in Rotated Sorted Array —— 类似的二分判断方向
"""

from typing import List


class Solution:
    """
    ==================== 解法一：二分查找 ====================

    【核心思路】
    比较 nums[mid] 和 nums[mid+1]：
    - 如果 nums[mid] < nums[mid+1]，说明右边更高，右侧一定存在峰值，lo = mid+1
    - 如果 nums[mid] > nums[mid+1]，说明左边更高（或mid就是峰值），hi = mid
    最终 lo == hi 时，该位置就是一个峰值。

    【思考过程】
    1. 题目说 nums[-1] = nums[n] = -∞，意味着数组两端都是"下降"的。
       只要数组不空，就一定存在峰值（至少有一个局部最大值）。

    2. 关键洞察：如果 nums[mid] < nums[mid+1]，说明从 mid 到 mid+1 是上升的。
       因为 nums[n] = -∞，从 mid+1 出发往右走，总归要下降。
       上升后必下降 → 中间一定有峰值。所以右半边 [mid+1, hi] 一定包含峰值。

    3. 同理如果 nums[mid] > nums[mid+1]，从 mid 往左看，nums[-1] = -∞，
       左边也一定有峰值（或 mid 自己就是峰值），所以 [lo, mid] 包含峰值。

    4. 注意 hi 的初始值是 n-1，不是 n，因为我们不需要越界访问。
       收缩方式：lo = mid+1 或 hi = mid（不是 mid-1），保证峰值不被跳过。

    【举例】nums = [1,2,1,3,5,6,4]
      lo=0, hi=6
      mid=3: nums[3]=3 < nums[4]=5 → lo=4
      mid=5: nums[5]=6 > nums[6]=4 → hi=5
      mid=4: nums[4]=5 < nums[5]=6 → lo=5
      lo=hi=5，返回 5（nums[5]=6 是峰值）

    【时间复杂度】O(log n)
    【空间复杂度】O(1)
    """
    def findPeakElement(self, nums: List[int]) -> int:
        lo, hi = 0, len(nums) - 1

        while lo < hi:
            mid = (lo + hi) // 2
            if nums[mid] < nums[mid + 1]:
                lo = mid + 1
            else:
                hi = mid

        return lo

    """
    ==================== 解法二：线性扫描 ====================

    【核心思路】
    从左到右扫描，一旦发现 nums[i] > nums[i+1]，说明 i 是一个峰值。
    如果一直上升到最后一个元素，那最后一个元素就是峰值。

    【思考过程】
    1. 因为 nums[-1] = -∞，所以从 i=0 开始，nums[0] > nums[-1] 一定成立。
       只要我们找到第一个"下降点" nums[i] > nums[i+1]，
       那么 nums[i] 就是峰值（左边上升或相等到这里，右边开始下降）。

    2. 实际上这就是在找"第一个比右邻居大的元素"。
       因为 nums[n] = -∞，最后一个元素也一定比 nums[n] 大，所以一定有解。

    3. 如果数组严格递增，那么扫描到最后才返回 n-1。

    【举例】nums = [1,2,3,1]
      i=0: nums[0]=1 < nums[1]=2，继续
      i=1: nums[1]=2 < nums[2]=3，继续
      i=2: nums[2]=3 > nums[3]=1，返回 2

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def findPeakElement_linear(self, nums: List[int]) -> int:
        for i in range(len(nums) - 1):
            if nums[i] > nums[i + 1]:
                return i
        return len(nums) - 1
