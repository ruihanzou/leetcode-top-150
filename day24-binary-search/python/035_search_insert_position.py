"""
LeetCode 35. Search Insert Position
难度: Easy

题目描述：
给定一个排序数组和一个目标值，在数组中找到目标值并返回其索引。
如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
必须使用时间复杂度为 O(log n) 的算法。

示例：nums = [1,3,5,6], target = 5 → 输出 2
      nums = [1,3,5,6], target = 2 → 输出 1
      nums = [1,3,5,6], target = 7 → 输出 4

【拓展练习】
1. LeetCode 34. Find First and Last Position of Element in Sorted Array —— 二分查找左右边界
2. LeetCode 278. First Bad Version —— 二分查找变体
3. LeetCode 69. Sqrt(x) —— 二分查找求平方根
"""

from typing import List


class Solution:
    """
    ==================== 解法一：标准二分查找 ====================

    【核心思路】
    经典的二分查找变体：找第一个 >= target 的位置。
    如果找到 target 就返回其下标；如果找不到，lo 最终停在的位置
    就是 target 应该插入的位置。

    【思考过程】
    1. 数组有序，自然想到二分查找。

    2. 标准二分：lo=0, hi=n-1，每次取 mid=(lo+hi)//2。
       - nums[mid] == target → 直接返回 mid
       - nums[mid] < target → 目标在右边，lo = mid + 1
       - nums[mid] > target → 目标在左边，hi = mid - 1

    3. 如果循环结束没找到 target，此时 lo > hi。
       lo 正好指向"第一个大于 target 的元素"的位置，
       也就是 target 应该插入的位置。
       原因：最后一步要么是 lo = mid+1（因为 nums[mid] < target），
       说明 lo 跳过了所有 < target 的元素；
       要么是 hi = mid-1（因为 nums[mid] > target），
       lo 不变，仍然指向第一个 >= target 的位置。

    【举例】nums = [1,3,5,6], target = 2
      lo=0, hi=3
      mid=1: nums[1]=3 > 2 → hi=0
      mid=0: nums[0]=1 < 2 → lo=1
      lo=1 > hi=0，返回 lo=1（2应插在下标1）

    【时间复杂度】O(log n)
    【空间复杂度】O(1)
    """
    def searchInsert(self, nums: List[int], target: int) -> int:
        lo, hi = 0, len(nums) - 1

        while lo <= hi:
            mid = (lo + hi) // 2
            if nums[mid] == target:
                return mid
            elif nums[mid] < target:
                lo = mid + 1
            else:
                hi = mid - 1

        return lo

    """
    ==================== 解法二：线性扫描 ====================

    【核心思路】
    从左到右扫描，找到第一个 >= target 的位置就是答案。
    如果全部 < target，则插入到末尾。

    【思考过程】
    1. 最朴素的做法：依次比较数组中的每个元素。

    2. 因为数组有序，第一个 >= target 的位置就是插入位置。
       如果 nums[i] == target，返回 i（找到了）。
       如果 nums[i] > target，说明 target 应插入 i 位置。

    3. 如果遍历完都没有 >= target 的元素，说明 target 比所有元素都大，
       应插入到数组末尾，返回 n。

    【举例】nums = [1,3,5,6], target = 2
      i=0: nums[0]=1 < 2，继续
      i=1: nums[1]=3 >= 2，返回 1

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def searchInsert_linear(self, nums: List[int], target: int) -> int:
        for i, num in enumerate(nums):
            if num >= target:
                return i
        return len(nums)
