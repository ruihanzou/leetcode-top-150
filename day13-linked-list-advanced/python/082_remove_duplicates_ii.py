"""
LeetCode 82. Remove Duplicates from Sorted List II
难度: Medium

题目描述：
给定一个已排序的链表的头 head，删除原始链表中所有重复数字的节点，
只留下不同的数字。返回已排序的链表。

示例 1：head = [1,2,3,3,4,4,5] → 输出 [1,2,5]
示例 2：head = [1,1,1,2,3] → 输出 [2,3]

【拓展练习】
1. LeetCode 83. Remove Duplicates from Sorted List —— 保留每个重复数字的一个节点
2. LeetCode 1836. Remove Duplicates From an Unsorted Linked List —— 无序链表去重
3. LeetCode 203. Remove Linked List Elements —— 删除链表中指定值的所有节点
"""

from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：虚拟头节点 + 迭代 ====================

    【核心思路】
    使用虚拟头节点和一个 prev 指针。遍历链表时，如果发现当前节点值和下一个节点值相同，
    就一直跳过所有相同值的节点；否则 prev 正常前移。

    【思考过程】
    1. 因为头节点本身也可能是重复节点而被删除，所以需要 dummy 节点。
    2. prev 指针始终指向"已确认保留的最后一个节点"。
    3. 检查 prev.next 和 prev.next.next 的值是否相同：
       - 如果相同，说明 prev.next 的值是重复值，记录这个值 dup_val，
         然后一直跳过所有值等于 dup_val 的节点。
       - 如果不同，说明 prev.next 不是重复节点，prev 前移。

    【举例】head = [1,2,3,3,4,4,5]
      dummy → 1 → 2 → 3 → 3 → 4 → 4 → 5
      prev=dummy:
        prev.next=1, prev.next.next=2, 1≠2 → prev=1
      prev=1:
        prev.next=2, prev.next.next=3, 2≠3 → prev=2
      prev=2:
        prev.next=3, prev.next.next=3, 3==3 → dup_val=3
        跳过所有3 → prev.next=4
      prev=2:
        prev.next=4, prev.next.next=4, 4==4 → dup_val=4
        跳过所有4 → prev.next=5
      prev=2:
        prev.next=5, prev.next.next=null → prev=5
      结果：1 → 2 → 5

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def deleteDuplicates_iterative(self, head: Optional[ListNode]) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        prev = dummy

        while prev.next and prev.next.next:
            if prev.next.val == prev.next.next.val:
                dup_val = prev.next.val
                while prev.next and prev.next.val == dup_val:
                    prev.next = prev.next.next
            else:
                prev = prev.next

        return dummy.next

    """
    ==================== 解法二：递归 ====================

    【核心思路】
    递归地处理链表：如果当前节点是重复节点，跳过所有相同值节点后递归处理剩余部分；
    如果不是重复节点，保留当前节点，递归处理下一个节点。

    【思考过程】
    1. 递归定义：deleteDuplicates(head) 返回以 head 为起点的链表去重后的头节点。
    2. base case：head 为空或只有一个节点，直接返回。
    3. 如果 head.val == head.next.val，说明 head 的值是重复值，
       需要跳过所有等于 head.val 的节点，然后递归处理后续。
    4. 如果 head.val != head.next.val，head 是安全的，
       head.next 指向递归处理 head.next 的结果。

    【举例】head = [1,1,1,2,3]
      head=1, head.next=1, 值相同 → 跳过所有1 → 递归处理 [2,3]
        head=2, head.next=3, 值不同 → 保留2，递归处理 [3]
          head=3, head.next=null → 返回 3
        2.next = 3 → 返回 [2,3]
      返回 [2,3]

    【时间复杂度】O(n)
    【空间复杂度】O(n)，递归栈深度
    """
    def deleteDuplicates_recursive(self, head: Optional[ListNode]) -> Optional[ListNode]:
        if not head or not head.next:
            return head

        if head.val == head.next.val:
            while head.next and head.val == head.next.val:
                head = head.next
            return self.deleteDuplicates_recursive(head.next)
        else:
            head.next = self.deleteDuplicates_recursive(head.next)
            return head
