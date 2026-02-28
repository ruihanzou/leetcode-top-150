"""
LeetCode 27. Remove Element
难度: Easy

题目描述：
给定一个整数数组 nums 和一个值 val，原地移除所有等于 val 的元素，
返回移除后数组的新长度 k。nums 的前 k 个元素应该包含不等于 val 的元素，
其余元素和数组大小不重要。不能使用额外数组空间，必须原地修改。

示例：nums = [3,2,2,3], val = 3 → 输出 2, nums = [2,2,_,_]

【拓展练习】
1. LeetCode 26. Remove Duplicates from Sorted Array —— 类似的快慢指针技巧
2. LeetCode 283. Move Zeroes —— 移除0并将其移到末尾
3. LeetCode 844. Backspace String Compare —— 逆向双指针处理删除
"""

from typing import List


class Solution:
    """
    ==================== 解法一：快慢双指针 ====================

    【核心思路】
    用慢指针 slow 记录下一个要填入的位置，快指针 fast 遍历数组。
    遇到不等于 val 的元素就复制到 slow 位置，然后 slow 前进。

    【思考过程】
    1. 题目要求原地删除，不能用额外数组。那就需要把"要保留的元素"
       集中到数组前部。

    2. 遍历数组时，对每个元素判断：是否要保留？
       - 如果 nums[fast] != val，保留，放到 slow 位置，slow++
       - 如果 nums[fast] == val，跳过

    3. 最终 slow 就是新数组的长度。保留的元素按原始顺序排列。

    【举例】nums = [0,1,2,2,3,0,4,2], val = 2
      fast=0: 0!=2, nums[0]=0, slow=1
      fast=1: 1!=2, nums[1]=1, slow=2
      fast=2: 2==2, 跳过
      fast=3: 2==2, 跳过
      fast=4: 3!=2, nums[2]=3, slow=3
      fast=5: 0!=2, nums[3]=0, slow=4
      fast=6: 4!=2, nums[4]=4, slow=5
      fast=7: 2==2, 跳过
      返回 slow=5, nums=[0,1,3,0,4,...]

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def removeElement_two_pointer(self, nums: List[int], val: int) -> int:
        slow = 0
        for fast in range(len(nums)):
            if nums[fast] != val:
                nums[slow] = nums[fast]
                slow += 1
        return slow

    """
    ==================== 解法二：交换移除（与末尾交换） ====================

    【核心思路】
    遇到等于 val 的元素时，用数组最后一个元素覆盖它，然后缩小数组有效范围。
    这样做赋值操作次数等于被删除元素的个数，适合要删除的元素很少的情况。

    【思考过程】
    1. 解法一中，如果数组几乎没有等于 val 的元素，大部分操作是无意义的复制
       （nums[slow] = nums[fast]，其中 slow == fast）。

    2. 换个思路：遇到等于 val 的元素才操作。把它和末尾元素"交换"
       （实际只需覆盖，因为被删元素不需要保留），然后缩小范围 n--。

    3. 注意交换过来的元素可能也等于 val，所以当前位置 i 不要前进，
       下一轮再判断一次。

    4. 代价：不保留元素的相对顺序。但题目允许这样做。

    【举例】nums = [3,2,2,3], val = 3
      i=0, n=4: nums[0]=3==val, 用 nums[3]=3 覆盖, n=3
      i=0, n=3: nums[0]=3==val, 用 nums[2]=2 覆盖, n=2
      i=0, n=2: nums[0]=2!=val, i=1
      i=1, n=2: i>=n 退出
      返回 n=2, nums=[2,2,...]

    【时间复杂度】O(n) 赋值操作最多 n 次（每次要么 i++ 要么 n--）
    【空间复杂度】O(1)
    """
    def removeElement_swap(self, nums: List[int], val: int) -> int:
        i, n = 0, len(nums)
        while i < n:
            if nums[i] == val:
                nums[i] = nums[n - 1]
                n -= 1
            else:
                i += 1
        return n
