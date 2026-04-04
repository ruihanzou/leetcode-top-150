"""
LeetCode 61. Rotate List
难度: Medium

题目描述：
给你一个链表的头节点 head，旋转链表，将链表每个节点向右移动 k 个位置。

示例 1：head = [1,2,3,4,5], k = 2 → 输出 [4,5,1,2,3]
示例 2：head = [0,1,2], k = 4 → 输出 [2,0,1]

【拓展练习】
1. LeetCode 189. Rotate Array —— 数组版本的旋转问题
2. LeetCode 725. Split Linked List in Parts —— 将链表拆分为 k 个部分
3. LeetCode 143. Reorder List —— 链表重排（涉及找中点、反转、合并）
"""

from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：成环断开 ====================

    【关键】「右移 k 位」不是一位一位挪尾巴，而是直接确定：旋转后新的头节点在原链表的哪里。

    设长度为 n，则真正有效的旋转量是 k % n（每转满一圈恢复原状）。

    以 1→2→3→4→5、k=2 为例，结果是 4→5→1→2→3：
    新头是原链表「第 n-k 个结点」的下一个（新尾是原链表正数第 n-k 个结点）。

    【步骤】
    1. 边界：空链表、单结点、k==0 → 直接返回。
    2. 一遍扫描：求长度 n，并找到尾结点 tail。
    3. k %= n；若 k==0 → 不变，返回。
    4. tail.next = head 连成环；「右移」变成「在环上找断点」。
    5. 新尾：从头结点走 n-k-1 步即到（与成环后从旧尾走 n-k 步等价）。
    6. new_head = new_tail.next，new_tail.next = None，返回 new_head。

    【为何最优】只常数次线性扫描，O(n) 时间、O(1) 额外空间。
    若每次把尾结点搬到表头重复 k 次，最坏可至 O(n·k)。

    【面试可一句话】先求长度，k % n 去掉无效旋转；首尾成环，找到新尾后断开，返回新头。
    """
    def rotateRight_cycle(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        if not head or not head.next or k == 0:
            return head

        tail = head
        n = 1
        while tail.next:
            tail = tail.next
            n += 1

        k %= n
        if k == 0:
            return head

        tail.next = head

        new_tail = head
        # 从新头开始走 n - k - 1 步，找到新尾
        for _ in range(n - k - 1):
            new_tail = new_tail.next

        new_head = new_tail.next
        new_tail.next = None
        return new_head

    """
    ==================== 解法二：快慢指针 ====================

    【核心思路】
    利用快慢指针找到倒数第 k 个节点的前驱，然后在该处断开链表，
    将后半部分拼接到前半部分的前面。

    【思考过程】
    1. 先遍历一遍得到链表长度 n，计算有效旋转量 k % n。
    2. 用快指针先走 k 步，然后快慢同时走，快指针到尾部时慢指针在断点。
    3. 这种方式和成环断开本质相同，但概念上是"找到断点后拼接"而非"成环后断开"。

    【举例】head = [1,2,3,4,5], k = 2
      n = 5, k % n = 2
      fast 先走2步：fast 指向 3
      同步走：slow=1,fast=3 → slow=2,fast=4 → slow=3,fast=5
      fast.next=null 停止
      new_head = slow.next = 4
      fast.next = head → 5.next = 1
      slow.next = null → 3.next = null
      结果：4 → 5 → 1 → 2 → 3

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def rotateRight_two_pointer(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        if not head or not head.next or k == 0:
            return head

        n = 0
        curr = head
        while curr:
            n += 1
            curr = curr.next

        k %= n
        if k == 0:
            return head

        fast = slow = head
        for _ in range(k):
            fast = fast.next

        while fast.next:
            fast = fast.next
            slow = slow.next

        new_head = slow.next
        slow.next = None
        fast.next = head
        return new_head
