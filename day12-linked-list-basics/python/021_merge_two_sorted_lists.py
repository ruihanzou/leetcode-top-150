"""
LeetCode 21. Merge Two Sorted Lists
难度: Easy

题目描述：
将两个升序链表合并为一个新的升序链表并返回。
新链表是通过拼接给定的两个链表的所有节点组成的。

示例 1：l1 = [1,2,4], l2 = [1,3,4] → 输出 [1,1,2,3,4,4]
示例 2：l1 = [], l2 = [] → 输出 []
示例 3：l1 = [], l2 = [0] → 输出 [0]

【拓展练习】
1. LeetCode 23. Merge k Sorted Lists —— 合并 k 个升序链表，可用优先队列或分治
2. LeetCode 88. Merge Sorted Array —— 合并两个有序数组，从后向前填充
3. LeetCode 148. Sort List —— 链表归并排序，用到合并两个有序链表作为子过程
"""

from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：迭代（虚拟头节点） ====================

    【核心思路】
    创建虚拟头节点，用一个指针逐步比较两个链表的当前节点，
    将较小的节点接到结果链表后面。

    【思考过程】
    1. 归并排序中的"合并"步骤：两个有序序列合并为一个有序序列。
       每次从两个链表头部取较小的那个，接到结果链表后面。

    2. 使用虚拟头节点（dummy）避免判断结果链表是否为空的边界问题。

    3. 当其中一个链表遍历完后，直接把另一个链表剩余部分接上即可，
       因为剩余部分本身就是有序的，且所有元素都 >= 已合并的部分。

    【举例】l1 = [1,2,4], l2 = [1,3,4]
      比较 1 vs 1：取 l1 的 1, result = [1]
      比较 2 vs 1：取 l2 的 1, result = [1,1]
      比较 2 vs 3：取 l1 的 2, result = [1,1,2]
      比较 4 vs 3：取 l2 的 3, result = [1,1,2,3]
      比较 4 vs 4：取 l1 的 4, result = [1,1,2,3,4]
      l1 遍历完，接上 l2 剩余 [4], result = [1,1,2,3,4,4]

    【时间复杂度】O(m + n)
    【空间复杂度】O(1)
    """
    def mergeTwoLists_iterative(self, list1: Optional[ListNode], list2: Optional[ListNode]) -> Optional[ListNode]:
        dummy = ListNode(0)
        curr = dummy

        while list1 and list2:
            if list1.val <= list2.val:
                curr.next = list1
                list1 = list1.next
            else:
                curr.next = list2
                list2 = list2.next
            curr = curr.next

        curr.next = list1 if list1 else list2

        return dummy.next

    """
    ==================== 解法二：递归 ====================

    【核心思路】
    比较两个链表头节点，较小的节点作为合并后链表的头，
    其 next 指向"该节点的 next"与"另一个链表"的递归合并结果。

    【思考过程】
    1. 递归定义：mergeTwoLists(l1, l2) 返回 l1 和 l2 合并后的有序链表头。

    2. 基本情况：如果其中一个为 None，返回另一个。

    3. 递归步骤：假设 l1.val <= l2.val，
       则合并结果的头是 l1，l1.next = mergeTwoLists(l1.next, l2)。

    4. 代码非常简洁优雅，但递归深度为 O(m+n)，
       链表很长时可能栈溢出。

    【举例】l1 = [1,2,4], l2 = [1,3,4]
      merge([1,2,4], [1,3,4])：1<=1, 取l1的1
        → 1.next = merge([2,4], [1,3,4])：2>1, 取l2的1
          → 1.next = merge([2,4], [3,4])：2<=3, 取l1的2
            → 2.next = merge([4], [3,4])：4>3, 取l2的3
              → 3.next = merge([4], [4])：4<=4, 取l1的4
                → 4.next = merge(None, [4])：l1为None, 返回[4]
      结果：1→1→2→3→4→4

    【时间复杂度】O(m + n)
    【空间复杂度】O(m + n)，递归调用栈深度
    """
    def mergeTwoLists_recursive(self, list1: Optional[ListNode], list2: Optional[ListNode]) -> Optional[ListNode]:
        if not list1:
            return list2
        if not list2:
            return list1

        if list1.val <= list2.val:
            list1.next = self.mergeTwoLists_recursive(list1.next, list2)
            return list1
        else:
            list2.next = self.mergeTwoLists_recursive(list1, list2.next)
            return list2
