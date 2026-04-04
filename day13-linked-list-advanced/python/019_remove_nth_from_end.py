"""
LeetCode 19. Remove Nth Node From End of List
难度: Medium

题目描述：
给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。

示例 1：head = [1,2,3,4,5], n = 2 → 输出 [1,2,3,5]
示例 2：head = [1], n = 1 → 输出 []
示例 3：head = [1,2], n = 1 → 输出 [1]

【拓展练习】
1. LeetCode 876. Middle of the Linked List —— 用快慢指针找链表中点
2. LeetCode 2095. Delete the Middle Node of a Linked List —— 删除链表中间节点
3. LeetCode 203. Remove Linked List Elements —— 删除链表中指定值的所有节点
"""

from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：两遍遍历 ====================

    【核心思路】
    第一遍遍历计算链表总长度 L，倒数第 n 个就是正数第 L-n+1 个。
    第二遍遍历走到第 L-n 个节点（目标节点的前驱），执行删除。

    【思考过程】
    1. "倒数第 n 个"这个描述需要知道链表长度才能转化为正数位置。
    2. 先遍历一遍得到长度 L，目标节点是正数第 L-n+1 个（1-indexed）。
    3. 我们需要目标节点的前驱节点来执行删除，即第 L-n 个节点。
    4. 使用 dummy 节点处理删除头节点的边界情况。

    【举例】head = [1,2,3,4,5], n = 2
      第一遍：L = 5
      倒数第2个 = 正数第4个 = 值为4的节点
      第二遍：走到第3个节点（值为3），3.next = 3.next.next → 跳过4
      结果：[1,2,3,5]

    【时间复杂度】O(n)，两遍遍历
    【空间复杂度】O(1)
    """
    def removeNthFromEnd_two_pass(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        length = 0
        curr = head
        while curr:
            length += 1
            curr = curr.next

        curr = dummy
        for _ in range(length - n):
            curr = curr.next

        curr.next = curr.next.next
        return dummy.next

    """
    ==================== 解法二：快慢指针（一遍遍历） ====================

    【核心思路】
    使用两个指针，让快指针先走 n 步，然后快慢指针一起走。
    当快指针到达末尾时，慢指针恰好在倒数第 n 个节点的前驱位置。

    【思考过程】
    1. 能否一遍遍历就解决？→ 需要两个指针保持固定间距。
    2. 快指针先走 n 步后，快慢之间间距为 n。
    3. 两个指针同时前进，快指针到达末尾（null）时，
       慢指针距末尾恰好 n 个节点，即慢指针在目标节点的前驱。
    4. 使用 dummy 节点让慢指针从 dummy 开始，这样最终 slow.next 就是要删除的节点。

    【举例】head = [1,2,3,4,5], n = 2
      dummy → 1 → 2 → 3 → 4 → 5 → null
      slow=dummy, fast=dummy
      fast 先走2步：fast 指向 2
      然后同步走：
        slow=1, fast=3
        slow=2, fast=4
        slow=3, fast=5
        fast.next=null 停止
      slow=3，slow.next=4（就是倒数第2个），删除之
      结果：[1,2,3,5]

    【时间复杂度】O(n)，一遍遍历
    【空间复杂度】O(1)
    """
    def removeNthFromEnd_fast_slow(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        fast = slow = dummy

        # 快指针先走 n 步
        for _ in range(n):
            fast = fast.next

        while fast.next: # 快指针到达末尾时，慢指针恰好在倒数第 n 个节点的前驱位置。
            fast = fast.next
            slow = slow.next

        slow.next = slow.next.next
        return dummy.next
