"""
LeetCode 4. Median of Two Sorted Arrays
难度: Hard

题目描述：
给定两个大小分别为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。
请你找出并返回这两个正序数组的中位数。
算法的时间复杂度应该为 O(log(m+n))。

示例1：nums1 = [1,3], nums2 = [2] → 输出 2.0
示例2：nums1 = [1,2], nums2 = [3,4] → 输出 2.5

【拓展练习】
1. LeetCode 23. Merge k Sorted Lists —— 多路归并，堆的经典应用
2. LeetCode 295. Find Median from Data Stream —— 动态中位数，双堆方法
3. LeetCode 480. Sliding Window Median —— 滑动窗口中位数
"""

from typing import List


class Solution:
    """
    ==================== 解法一：二分查找（划分法） ====================

    【核心思路】
    在较短的数组上二分，找到一个分割位置 i，使得 nums1 的前 i 个元素
    和 nums2 的前 j = (m+n+1)//2 - i 个元素组成"左半部分"，
    保证左半部分所有元素 <= 右半部分所有元素。

    【思考过程】
    1. 中位数把合并后的数组分成等长的两半（奇数时左半多一个）。
       设总长 m+n，左半需要 half = (m+n+1)//2 个元素。

    2. 如果我们从 nums1 中取 i 个，从 nums2 中取 j = half - i 个，
       那么左半就确定了。关键是找到正确的 i。

    3. 分割合法的条件：
       - nums1[i-1] <= nums2[j]  （nums1 左半最大 <= nums2 右半最小）
       - nums2[j-1] <= nums1[i]  （nums2 左半最大 <= nums1 右半最小）

    4. 对 i 做二分搜索：
       - 如果 nums1[i-1] > nums2[j]：i 太大，需要减小 → right = i - 1
       - 如果 nums2[j-1] > nums1[i]：i 太小，需要增大 → left = i + 1
       - 否则找到正确的分割

    5. 确保在较短数组上二分，保证 j >= 0。

    【举例】nums1 = [1,3], nums2 = [2]
      m=2, n=1, half=(3+1)//2=2
      确保 nums1 是较短数组：nums1=[2], nums2=[1,3], m=1, n=2
      left=0, right=1
      mid=0: i=0, j=2
        左半：nums2[0..1]=[1,3], 右半：nums1[0..]=[2]
        nums2[j-1]=nums2[1]=3 > nums1[i]=nums1[0]=2 → left=1
      mid=1: i=1, j=1
        左半：nums1[0]=2, nums2[0]=1, 右半：nums2[1]=3
        nums1[i-1]=2 <= nums2[j]=3 ✓
        nums2[j-1]=1 <= 无穷大 ✓
        左半最大 = max(2, 1) = 2
        总长奇数 → 中位数 = 2.0

    【时间复杂度】O(log min(m, n))
    【空间复杂度】O(1)
    """
    def findMedianSortedArrays_binary(self, nums1: List[int], nums2: List[int]) -> float:
        if len(nums1) > len(nums2):
            nums1, nums2 = nums2, nums1

        m, n = len(nums1), len(nums2)
        half = (m + n + 1) // 2

        left, right = 0, m

        while left <= right:
            i = (left + right) // 2
            j = half - i

            nums1_left = nums1[i - 1] if i > 0 else float('-inf')
            nums1_right = nums1[i] if i < m else float('inf')
            nums2_left = nums2[j - 1] if j > 0 else float('-inf')
            nums2_right = nums2[j] if j < n else float('inf')

            if nums1_left > nums2_right:
                right = i - 1
            elif nums2_left > nums1_right:
                left = i + 1
            else:
                max_left = max(nums1_left, nums2_left)
                if (m + n) % 2 == 1:
                    return float(max_left)
                min_right = min(nums1_right, nums2_right)
                return (max_left + min_right) / 2.0

        return 0.0

    """
    ==================== 解法二：归并找第 k 小 ====================

    【核心思路】
    中位数就是第 k 小的元素（k = (m+n+1)//2 或同时需要第 k 和第 k+1）。
    每次比较两个数组中第 k//2 个元素，排除较小的一半，递归缩小 k。

    【思考过程】
    1. 合并两个数组找中位数最朴素做法是 O(m+n)。
       能否利用两个数组各自有序的特点加速？

    2. 考虑找第 k 小：比较 nums1[k//2-1] 和 nums2[k//2-1]。
       - 如果 nums1[k//2-1] < nums2[k//2-1]：
         nums1 中前 k//2 个元素都不可能是第 k 小（它们最多是前 k-1 小中的），
         可以安全排除。
       - 反之亦然。

    3. 每次排除 k//2 个元素，k 减半，总共 O(log k) = O(log(m+n)) 步。

    4. 边界处理：
       - 某个数组为空 → 直接在另一个数组中取
       - k == 1 → 取两个数组当前首元素的较小者
       - 某个数组剩余不足 k//2 → 取该数组最后一个元素比较

    【举例】nums1 = [1,2], nums2 = [3,4]
      总长4，需要第2小和第3小的平均
      找第2小：k=2
        比较 nums1[0]=1 和 nums2[0]=3 → 排除 nums1[0]
        k=1，取 min(nums1[1]=2, nums2[0]=3) = 2
      找第3小：k=3
        比较 nums1[0]=1 和 nums2[0]=3 → 排除 nums1[0]
        k=2，比较 nums1[1]=2 和 nums2[0]=3 → 排除 nums1[1]
        k=1，nums1 为空，取 nums2[0]=3
      中位数 = (2+3)/2 = 2.5

    【时间复杂度】O(log(m+n))
    【空间复杂度】O(log(m+n)) 递归栈
    """
    def findMedianSortedArrays_kth(self, nums1: List[int], nums2: List[int]) -> float:
        def find_kth(a, i, b, j, k):
            if i >= len(a):
                return b[j + k - 1]
            if j >= len(b):
                return a[i + k - 1]
            if k == 1:
                return min(a[i], b[j])

            half = k // 2
            mid_a = a[i + half - 1] if i + half - 1 < len(a) else float('inf')
            mid_b = b[j + half - 1] if j + half - 1 < len(b) else float('inf')

            if mid_a < mid_b:
                return find_kth(a, i + half, b, j, k - half)
            else:
                return find_kth(a, i, b, j + half, k - half)

        m, n = len(nums1), len(nums2)
        total = m + n

        if total % 2 == 1:
            return float(find_kth(nums1, 0, nums2, 0, total // 2 + 1))
        else:
            left = find_kth(nums1, 0, nums2, 0, total // 2)
            right = find_kth(nums1, 0, nums2, 0, total // 2 + 1)
            return (left + right) / 2.0
