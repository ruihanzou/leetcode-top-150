"""
LeetCode 34. Find First and Last Position of Element in Sorted Array
难度: Medium

题目描述：
给你一个按照非递减顺序排列的整数数组 nums 和一个目标值 target。
请你找出给定目标值在数组中的开始位置和结束位置。
如果数组中不存在目标值 target，返回 [-1, -1]。
必须设计并实现时间复杂度为 O(log n) 的算法。

示例：nums = [5,7,7,8,8,10], target = 8 → 输出 [3,4]
      nums = [5,7,7,8,8,10], target = 6 → 输出 [-1,-1]

【拓展练习】
1. LeetCode 35. Search Insert Position —— 二分查找插入位置
2. LeetCode 278. First Bad Version —— 二分查找边界
3. LeetCode 2089. Find Target Indices After Sorting —— 排序后找目标索引
"""

from typing import List


class Solution:
    """
    ==================== 解法一：两次二分（分别找左右边界） ====================

    【核心思路】
    进行两次二分查找：
    第一次找 target 的左边界（第一个等于 target 的位置）。
    第二次找 target 的右边界（最后一个等于 target 的位置）。

    【思考过程】
    1. 经典的"左边界二分"和"右边界二分"模板。

    2. 左边界二分：找第一个 >= target 的位置。
       当 nums[mid] >= target 时 hi = mid - 1（即使等于也继续往左找）。
       当 nums[mid] < target 时 lo = mid + 1。
       结束后 lo 就是第一个 >= target 的位置。
       需要验证 nums[lo] == target。

    3. 右边界二分：找最后一个 <= target 的位置。
       当 nums[mid] <= target 时 lo = mid + 1（即使等于也继续往右找）。
       当 nums[mid] > target 时 hi = mid - 1。
       结束后 hi 就是最后一个 <= target 的位置。
       需要验证 nums[hi] == target。

    4. 优化：先找左边界，如果左边界不存在（nums[lo] != target），
       说明 target 不在数组中，直接返回 [-1,-1]，省去第二次二分。

    【举例】nums = [5,7,7,8,8,10], target = 8
      找左边界：
        lo=0, hi=5
        mid=2: nums[2]=7 < 8 → lo=3
        mid=4: nums[4]=8 >= 8 → hi=3
        mid=3: nums[3]=8 >= 8 → hi=2
        lo=3, nums[3]=8 == target ✓ → left=3

      找右边界：
        lo=3, hi=5
        mid=4: nums[4]=8 <= 8 → lo=5
        mid=5: nums[5]=10 > 8 → hi=4
        hi=4, nums[4]=8 == target ✓ → right=4

      返回 [3, 4]

    【时间复杂度】O(log n) 两次二分
    【空间复杂度】O(1)
    """
    def searchRange(self, nums: List[int], target: int) -> List[int]:
        if not nums:
            return [-1, -1]

        left = self._find_left(nums, target)
        if left == -1:
            return [-1, -1]
        right = self._find_right(nums, target)

        return [left, right]

    def _find_left(self, nums: List[int], target: int) -> int:
        lo, hi = 0, len(nums) - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if nums[mid] >= target:
                hi = mid - 1
            else:
                lo = mid + 1
        if lo < len(nums) and nums[lo] == target:
            return lo
        return -1

    def _find_right(self, nums: List[int], target: int) -> int:
        lo, hi = 0, len(nums) - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if nums[mid] <= target:
                lo = mid + 1
            else:
                hi = mid - 1
        if hi >= 0 and nums[hi] == target:
            return hi
        return -1

    """
    ==================== 解法二：一次二分找左边界 + 线性右扩 ====================

    【核心思路】
    先用二分找到 target 的左边界（第一个出现的位置），
    然后从左边界开始向右扫描，找到最后一个等于 target 的位置。

    【思考过程】
    1. 左边界用二分查找，O(log n)。

    2. 找到左边界后，从 left 开始往右走，直到 nums[i] != target。
       右扩的步数等于 target 出现的次数 k。

    3. 最坏情况下 k = n（整个数组都是 target），退化为 O(n)。
       但如果 target 出现次数很少，这种方法实际很快。

    4. 这种方法的好处是代码简单，只需要一个二分模板。

    【举例】nums = [5,7,7,8,8,10], target = 8
      二分找左边界 → left=3
      从 i=3 向右扫描：nums[3]=8 ✓, nums[4]=8 ✓, nums[5]=10 ✗
      right = 4
      返回 [3, 4]

    【时间复杂度】O(log n + k)，k 是 target 出现次数，最坏 O(n)
    【空间复杂度】O(1)
    """
    def searchRange_linear(self, nums: List[int], target: int) -> List[int]:
        if not nums:
            return [-1, -1]

        lo, hi = 0, len(nums) - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if nums[mid] >= target:
                hi = mid - 1
            else:
                lo = mid + 1

        if lo >= len(nums) or nums[lo] != target:
            return [-1, -1]

        left = lo
        right = lo
        while right + 1 < len(nums) and nums[right + 1] == target:
            right += 1

        return [left, right]
