"""
LeetCode 88. Merge Sorted Array
难度: Easy

题目描述：
给定两个按非递减顺序排列的整数数组 nums1 和 nums2，以及两个整数 m 和 n，
分别表示 nums1 和 nums2 中的元素数目。
将 nums2 合并到 nums1 中，使合并后的数组同样按非递减顺序排列。
nums1 的长度为 m + n，其中后 n 个元素为 0，应忽略；nums2 的长度为 n。
最终结果存储在 nums1 中，不返回新数组。

示例：nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3 → nums1 = [1,2,2,3,5,6]

【拓展练习】
1. LeetCode 21. Merge Two Sorted Lists —— 合并两个有序链表，思路类似
2. LeetCode 977. Squares of a Sorted Array —— 双指针从两端向中间合并
3. LeetCode 986. Interval List Intersections —— 双指针处理有序区间
"""

from typing import List


class Solution:
    """
    ==================== 解法一：额外数组合并 ====================

    【核心思路】
    新开一个长度 m+n 的数组，用两个指针分别从 nums1、nums2 头部开始归并，
    每次取较小的放入新数组，最后拷回 nums1。

    【思考过程】
    1. 最直观的做法：归并排序的 merge 步骤。两个有序数组，各用一个指针，
       每次比较两个指针指向的元素，取较小的放入结果数组。

    2. 归并完成后结果在新数组中，需要拷贝回 nums1。

    3. 缺点是需要额外 O(m+n) 空间。能否在 nums1 上原地操作？
       → 如果从前往后填，会覆盖 nums1 还未处理的元素。
       → 这引出了解法二：从后往前填。

    【举例】nums1 = [1,2,3,0,0,0], m=3, nums2 = [2,5,6], n=3
      temp = []
      p1=0, p2=0:
        1 vs 2 → temp=[1], p1=1
        2 vs 2 → temp=[1,2], p1=2
        3 vs 2 → temp=[1,2,2], p2=1
        3 vs 5 → temp=[1,2,2,3], p1=3
        剩余 nums2: temp=[1,2,2,3,5,6]
      拷回 nums1 → [1,2,2,3,5,6]

    【时间复杂度】O(m+n)
    【空间复杂度】O(m+n)
    """
    def merge_extra_array(self, nums1: List[int], m: int, nums2: List[int], n: int) -> None:
        temp = []
        p1, p2 = 0, 0

        while p1 < m and p2 < n:
            if nums1[p1] <= nums2[p2]:
                temp.append(nums1[p1])
                p1 += 1
            else:
                temp.append(nums2[p2])
                p2 += 1

        while p1 < m:
            temp.append(nums1[p1])
            p1 += 1
        while p2 < n:
            temp.append(nums2[p2])
            p2 += 1

        nums1[:] = temp

    """
    ==================== 解法二：逆向双指针 ====================

    【核心思路】
    从后往前填充 nums1。nums1 末尾有 n 个空位，恰好可以容纳合并结果。
    每次取 nums1[p1] 和 nums2[p2] 中较大的放到 nums1 的末尾，
    这样不会覆盖还未处理的有效元素。

    【思考过程】
    1. nums1 长度为 m+n，后 n 个位置是空的（填充了 0）。
       如果从后往前放，最大的元素先就位，不会影响前面还没处理的元素。

    2. 为什么不会覆盖？考虑写指针 k 和读指针 p1 的关系：
       k = m + n - 1 开始，每放一个元素 k--。
       p1 最大是 m-1，只要 k > p1，写入位置就不会覆盖读取位置。
       而 k = p1 + p2 + 1 >= p1（因为 p2 >= 0），所以始终安全。

    3. 循环结束后，如果 nums2 还有剩余（p2 >= 0），需要拷贝到 nums1 前面。
       如果 nums1 有剩余，它们本身就在正确位置，无需操作。

    【举例】nums1 = [1,2,3,0,0,0], m=3, nums2 = [2,5,6], n=3
      p1=2, p2=2, k=5:
        nums1[2]=3 vs nums2[2]=6 → nums1[5]=6, p2=1, k=4
        nums1[2]=3 vs nums2[1]=5 → nums1[4]=5, p2=0, k=3
        nums1[2]=3 vs nums2[0]=2 → nums1[3]=3, p1=1, k=2
        nums1[1]=2 vs nums2[0]=2 → nums1[2]=2, p2=-1, k=1
      p2<0 退出循环，nums1 剩余 [1,2] 已在正确位置
      结果: [1,2,2,3,5,6]

    【时间复杂度】O(m+n)
    【空间复杂度】O(1)
    """
    def merge_reverse(self, nums1: List[int], m: int, nums2: List[int], n: int) -> None:
        p1, p2, k = m - 1, n - 1, m + n - 1

        while p1 >= 0 and p2 >= 0:
            if nums1[p1] >= nums2[p2]:
                nums1[k] = nums1[p1]
                p1 -= 1
            else:
                nums1[k] = nums2[p2]
                p2 -= 1
            k -= 1

        while p2 >= 0:
            nums1[k] = nums2[p2]
            p2 -= 1
            k -= 1
