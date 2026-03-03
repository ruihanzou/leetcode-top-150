"""
LeetCode 148. Sort List
难度: Medium

题目描述：
给你链表的头结点 head，请将其按升序排列并返回排序后的链表。
要求在 O(n log n) 时间复杂度和常数级空间复杂度下完成。

示例 1：head = [4,2,1,3] → [1,2,3,4]
示例 2：head = [-1,5,3,4,0] → [-1,0,3,4,5]
示例 3：head = [] → []

【拓展练习】
1. LeetCode 21. Merge Two Sorted Lists —— 合并两个有序链表，本题的子操作
2. LeetCode 147. Insertion Sort List —— 链表插入排序，O(n^2)
3. LeetCode 23. Merge k Sorted Lists —— 合并k个有序链表，分治/堆
"""

from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：自顶向下归并排序 ====================

    【核心思路】
    用快慢指针找到链表中点，将链表拆分为两半，分别递归排序后再合并。
    这是经典的归并排序在链表上的应用。

    【思考过程】
    1. 链表排序不能像数组那样随机访问，快排的 partition 不方便。
       但归并排序只需要顺序扫描和合并，非常适合链表。

    2. 找中点：用快慢指针，slow 一次走一步，fast 一次走两步。
       fast 到末尾时，slow 恰好在中点。

    3. 拆分：在 slow 处断开，得到两个子链表。
       分别递归排序后，用双指针合并两个有序链表。

    4. 递归基：链表为空或只有一个节点时，直接返回。

    【举例】head = [4, 2, 1, 3]
      找中点：slow=2, fast到末尾 → 拆分为 [4,2] 和 [1,3]
      递归 [4,2]：拆分为 [4] 和 [2]，合并得 [2,4]
      递归 [1,3]：拆分为 [1] 和 [3]，合并得 [1,3]
      合并 [2,4] 和 [1,3]：→ [1,2,3,4]

    【时间复杂度】O(n log n)
    【空间复杂度】O(log n) 递归栈
    """
    def sortList(self, head: Optional[ListNode]) -> Optional[ListNode]:
        if not head or not head.next:
            return head

        slow, fast = head, head.next
        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next

        mid = slow.next
        slow.next = None

        left = self.sortList(head)
        right = self.sortList(mid)
        return self._merge(left, right)

    def _merge(self, l1: Optional[ListNode], l2: Optional[ListNode]) -> Optional[ListNode]:
        dummy = ListNode(0)
        cur = dummy
        while l1 and l2:
            if l1.val <= l2.val:
                cur.next = l1
                l1 = l1.next
            else:
                cur.next = l2
                l2 = l2.next
            cur = cur.next
        cur.next = l1 if l1 else l2
        return dummy.next

    """
    ==================== 解法二：自底向上归并排序 ====================

    【核心思路】
    迭代地按步长 1, 2, 4, 8, ... 进行归并。
    每轮将相邻的两段长度为 step 的子链表合并为一段长度为 2*step 的有序链表。
    不使用递归，空间复杂度 O(1)。

    【思考过程】
    1. 自顶向下的递归会占用 O(log n) 栈空间，题目要求常数空间。
       → 改用自底向上的迭代方式。

    2. 第一轮：每两个相邻节点（step=1）合并为一个长度2的有序段。
       第二轮：每两个相邻的长度2段（step=2）合并为长度4。
       ...直到 step >= n，整条链表有序。

    3. 每轮需要：
       a) 从当前位置切下长度为 step 的段
       b) 再切下一段长度为 step 的段
       c) 合并这两段，接到结果链表尾部
       d) 继续处理后续的段

    【举例】head = [4, 2, 1, 3]，n=4
      step=1: 合并(4)(2)→[2,4], 合并(1)(3)→[1,3] → [2,4,1,3]
      step=2: 合并(2,4)(1,3)→[1,2,3,4]
      step=4 >= n，结束

    【时间复杂度】O(n log n)
    【空间复杂度】O(1)
    """
    def sortListBottomUp(self, head: Optional[ListNode]) -> Optional[ListNode]:
        if not head or not head.next:
            return head

        length = 0
        node = head
        while node:
            length += 1
            node = node.next

        dummy = ListNode(0)
        dummy.next = head
        step = 1

        while step < length:
            cur = dummy.next
            tail = dummy

            while cur:
                left = cur
                right = self._split(left, step)
                cur = self._split(right, step)

                merged_head, merged_tail = self._merge_with_tail(left, right)
                tail.next = merged_head
                tail = merged_tail

            step <<= 1

        return dummy.next

    def _split(self, head: Optional[ListNode], step: int) -> Optional[ListNode]:
        if not head:
            return None
        for _ in range(step - 1):
            if not head.next:
                break
            head = head.next
        nxt = head.next
        head.next = None
        return nxt

    def _merge_with_tail(self, l1: Optional[ListNode], l2: Optional[ListNode]):
        dummy = ListNode(0)
        cur = dummy
        while l1 and l2:
            if l1.val <= l2.val:
                cur.next = l1
                l1 = l1.next
            else:
                cur.next = l2
                l2 = l2.next
            cur = cur.next
        cur.next = l1 if l1 else l2
        while cur.next:
            cur = cur.next
        return dummy.next, cur
