"""
LeetCode 25. Reverse Nodes in k-Group
难度: Hard

题目描述：
给你链表的头节点 head，每 k 个节点一组进行翻转，请你返回修改后的链表。
k 是一个正整数，它的值小于或等于链表的长度。
如果节点总数不是 k 的整数倍，那么请将最后剩余的节点保持原有顺序。
你不能只是单纯地改变节点内部的值，而是需要实际进行节点交换。

示例 1：head = [1,2,3,4,5], k = 2 → 输出 [2,1,4,3,5]
示例 2：head = [1,2,3,4,5], k = 3 → 输出 [3,2,1,4,5]

【拓展练习】
1. LeetCode 206. Reverse Linked List —— 反转整个链表的基础题
2. LeetCode 24. Swap Nodes in Pairs —— k=2 的特殊情况
3. LeetCode 92. Reverse Linked List II —— 反转指定区间内的链表
"""

from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：迭代 ====================

    【核心思路】
    逐组处理：先数够 k 个节点确认有完整的一组，然后对这 k 个节点做链表反转，
    反转后把前一组的尾部连接到当前组反转后的头部，再继续处理下一组。

    【思考过程】
    1. 我们需要一个 dummy 节点来简化头部处理（反转后头节点会变）。
    2. 每次处理一组时，需要知道：
       - group_prev：前一组的最后一个节点（用于连接）
       - group_start：当前组的第一个节点（反转后变成组尾）
       - group_end：当前组的最后一个节点（反转后变成组头）
    3. 先从 group_start 往后走 k 步找到 group_end，如果不够 k 个就停止。
    4. 断开当前组与后续部分的连接，对当前组做标准链表反转，
       然后把 group_prev 连到新头，把新尾连到后续部分。

    【举例】head = [1,2,3,4,5], k = 3
      dummy → 1 → 2 → 3 → 4 → 5
      第一组 [1,2,3]：数够3个，反转 → [3,2,1]
      dummy → 3 → 2 → 1 → 4 → 5
      第二组 [4,5]：只有2个，不足k，保持原序
      结果：3 → 2 → 1 → 4 → 5

    【时间复杂度】O(n)，每个节点被遍历常数次
    【空间复杂度】O(1)，仅用指针变量
    """
    def reverseKGroup_iterative(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        group_prev = dummy

        while True:
            kth = group_prev
            for _ in range(k):
                kth = kth.next
                if not kth:
                    return dummy.next

            group_next = kth.next
            prev, curr = group_next, group_prev.next
            for _ in range(k):
                tmp = curr.next
                curr.next = prev
                prev = curr
                curr = tmp

            tmp = group_prev.next
            group_prev.next = prev
            group_prev = tmp

    """
    ==================== 解法二：递归 ====================

    【核心思路】
    递归地处理链表：先检查当前是否有 k 个节点，如果有就反转这 k 个节点，
    然后递归处理剩余部分，将当前组反转后的尾部连接到递归返回的结果。

    【思考过程】
    1. 递归的子问题定义：reverseKGroup(head, k) 返回以 head 开头的链表
       按 k 个一组反转后的新头节点。
    2. 先检查从 head 开始是否有 k 个节点，不够就直接返回 head（不反转）。
    3. 有 k 个节点时，反转前 k 个节点，记录第 k+1 个节点的位置。
    4. 递归调用处理第 k+1 个节点开始的子链表。
    5. 原来的 head 反转后变成了组尾，把它的 next 指向递归返回的结果即可。

    【举例】head = [1,2,3,4,5], k = 2
      第一层：检查 [1,2] 够2个，反转得 [2,1]，递归处理 [3,4,5]
      第二层：检查 [3,4] 够2个，反转得 [4,3]，递归处理 [5]
      第三层：检查 [5] 不够2个，返回 [5]
      回溯：[4,3] → 3.next = [5] → [4,3,5]
      回溯：[2,1] → 1.next = [4,3,5] → [2,1,4,3,5]

    【时间复杂度】O(n)，每个节点被遍历常数次
    【空间复杂度】O(n/k)，递归栈深度
    """
    def reverseKGroup_recursive(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        node = head
        for _ in range(k):
            if not node:
                return head
            node = node.next

        prev, curr = None, head
        for _ in range(k):
            tmp = curr.next
            curr.next = prev
            prev = curr
            curr = tmp

        head.next = self.reverseKGroup_recursive(curr, k)
        return prev
