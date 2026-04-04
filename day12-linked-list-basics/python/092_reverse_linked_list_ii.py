"""
LeetCode 92. Reverse Linked List II
难度: Medium

题目描述：
给你单链表的头指针 head 和两个整数 left 和 right，其中 left <= right。
请你反转从位置 left 到位置 right 的链表节点，返回反转后的链表。
位置从 1 开始计数。

示例 1：head = [1,2,3,4,5], left = 2, right = 4 → 输出 [1,4,3,2,5]
示例 2：head = [5], left = 1, right = 1 → 输出 [5]

【拓展练习】
1. LeetCode 206. Reverse Linked List —— 反转整个链表，本题的简化版
2. LeetCode 25. Reverse Nodes in k-Group —— 每 k 个节点一组进行反转
3. LeetCode 24. Swap Nodes in Pairs —— 两两交换链表节点，可看作 k=2 的反转
"""

from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：穿针引线（头插法） ====================

    【核心思路】
    先找到区间 [left, right] 左侧的前驱节点 pre，再在该区间内用「头插法」原地反转。

    操作要点（整段循环只需记这一句）：
    - pre 始终钉在「区间左边界」的外侧，循环中 pre 不变；
    - curr 指向区间第一个节点（原 left 位置），循环中 curr 也不变；
    - 每一轮把 curr 的下一个节点摘下来，插到 pre 的后面；
    - 共做 right - left 次（要搬的节点数 = 区间长度减 1）。

    【什么是头插法】
    头插法：在链表的「头部一侧」反复插入节点，后插入的节点更靠近头。
    - 在 LeetCode 206（反转整条链表）里，常见写法是不断把当前节点挪到表头；
    - 本题是 206 的变体：表头被固定成「pre 之后的第一个位置」——每轮把 curr 后面的节点
      插到 pre 之后，相当于对子区间 [left, right] 反复做 head insert，把 left+1 … right
      依次挪到「反转块」的最前面，最终得到 [left … right] 的逆序。

    【循环内四行代码在做什么】（按「先保存再改指针」的顺序，避免断链后找不到节点）
    1. nxt = curr.next          本轮要搬到 pre 后面的节点；
    2. curr.next = nxt.next     从 curr 后摘掉 nxt，curr 仍指向区间首节点；
    3. nxt.next = pre.next      nxt 接到「当前反转块」的头（原先的 pre.next）；
    4. pre.next = nxt           pre 指向新的反转块头。

    【思考过程】
    1. 只反转 [left, right]，不动前后 → 先定位 left 的前驱 pre（left=1 时用 dummy）。

    2. 用虚拟头 dummy 统一处理 left=1（此时 pre 即为 dummy）。

    3. 头插次数为何是 right - left、不是 +1？见下【结点数与循环次数】。

    【结点数与循环次数】
    - right - left + 1：闭区间 [left, right] 上共有多少个节点（含两端），这是「数数」。
    - right - left：循环里要执行多少次「把 curr.next 摘下来插到 pre 后面」，这是「搬几次」。
    - curr 始终指向原 left 位置的节点，循环每轮只搬 curr.next，即依次搬原 left+1 … right，
      共 (right - (left+1) + 1) = right - left 个节点；原 left 节点从不作为 nxt 被摘走，
      在别人一次次头插到前面后，它自然落在反转段的末尾，正是逆序后它应在的位置。
    - 小结：结点数比「要摘的 nxt 个数」多 1，多出来的就是当锚点的 curr，故循环 right - left 次。

    【易错点】
    - 循环次数不要写成 right - left + 1：那是区间结点数，+1 会多搬一次、破坏链表。
    - 若怕混淆，可先写 moves = right - left，再 for _ in range(moves)。

    【举例与图示】head = [1,2,3,4,5], left = 2, right = 4（right-left=2 次循环）

      初始：定位完 pre 之后（curr 为区间首节点，整段循环中不变）
            dummy ──→ 1 ──→ 2 ──→ 3 ──→ 4 ──→ 5
                      ↑     ↑
                     pre  curr

      第 1 轮：nxt = curr.next，本轮搬的是节点 3
                         nxt
                          ↓
            dummy ──→ 1 ──→ 2 ──→ 3 ──→ 4 ──→ 5
                      ↑     ↑
                     pre  curr
            执行四行指针更新后：
            dummy ──→ 1 ──→ 3 ──→ 2 ──→ 4 ──→ 5
                      ↑           ↑
                     pre        curr（仍为 2）

      第 2 轮：nxt = curr.next，本轮搬的是节点 4；结束后：
            dummy ──→ 1 ──→ 4 ──→ 3 ──→ 2 ──→ 5
                      ↑                 ↑
                     pre              curr（仍为 2）

      结果：[1,4,3,2,5]

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def reverseBetween_insert(self, head: Optional[ListNode], left: int, right: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        pre = dummy

        for _ in range(left - 1): # 找到 left 位置的前驱节点 pre，循环 left - 1 次
            pre = pre.next

        curr = pre.next  # 区间首节点，整段循环中不变
        # 每轮：pre -> curr -> nxt -> ... 变为 pre -> nxt -> curr -> ...（四行指针顺序勿改）
        moves = right - left  # 勿用 +1，+1 是区间结点数，这里是要搬的次数
        for _ in range(moves):
            nxt = curr.next
            curr.next = nxt.next
            nxt.next = pre.next
            pre.next = nxt

        return dummy.next

    """
    ==================== 解法二：截取反转拼接 ====================

    【核心思路】
    将链表分为三段：[1, left-1]、[left, right]、[right+1, end]。
    把中间段截取出来单独反转，然后重新拼接三段。

    【思考过程】
    1. 这是最直观的思路：先把要反转的部分"切"出来，反转，再"粘"回去。

    2. 需要记录四个关键位置：
       - pre: left 的前驱节点
       - left_node: left 位置的节点（反转后变成子链表的尾）
       - right_node: right 位置的节点（反转后变成子链表的头）
       - succ: right 的后继节点

    3. 切断连接：pre.next = None, right_node.next = None
       反转 [left_node, right_node] 这段
       重新连接：pre.next = right_node, left_node.next = succ

    4. 反转单链表用经典的三指针法。

    【举例】head = [1,2,3,4,5], left = 2, right = 4
      切分前：1 → 2 → 3 → 4 → 5
      pre=1, left_node=2, right_node=4, succ=5

      切断：[1]  [2→3→4]  [5]
      反转中间段：[4→3→2]
      拼接：1 → 4 → 3 → 2 → 5

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def reverseBetween_cut_reverse(self, head: Optional[ListNode], left: int, right: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)

        pre = dummy
        for _ in range(left - 1):
            pre = pre.next
        left_node = pre.next

        right_node = pre
        for _ in range(right - left + 1):
            right_node = right_node.next
        succ = right_node.next

        pre.next = None
        right_node.next = None

        self._reverse(left_node)

        pre.next = right_node
        left_node.next = succ

        return dummy.next

    def _reverse(self, head: ListNode) -> None:
        prev, curr = None, head
        while curr:
            nxt = curr.next
            curr.next = prev
            prev = curr
            curr = nxt
