"""
LeetCode 141. Linked List Cycle
难度: Easy

题目描述：
给你一个链表的头节点 head，判断链表中是否有环。
如果链表中有某个节点，可以通过连续跟踪 next 指针再次到达该节点，则链表中存在环。
为了表示给定链表中的环，评测系统内部使用整数 pos 来表示链表尾连接到链表中的位置
（索引从 0 开始）。注意：pos 不作为参数进行传递。仅仅是为了标识链表的实际情况。
如果链表中存在环，则返回 true；否则返回 false。

示例 1：head = [3,2,0,-4], pos = 1 → 输出 true
  解释：链表中有一个环，其尾部连接到第二个节点。
示例 2：head = [1,2], pos = 0 → 输出 true
示例 3：head = [1], pos = -1 → 输出 false

【拓展练习】
1. LeetCode 142. Linked List Cycle II —— 找到环的入口节点
2. LeetCode 287. Find the Duplicate Number —— 将数组问题转化为链表环检测
3. LeetCode 202. Happy Number —— 用快慢指针判断是否进入循环
"""

from typing import Optional


class ListNode:
    def __init__(self, x):
        self.val = x
        self.next = None


class Solution:
    """
    ==================== 解法一：哈希集合 ====================

    【核心思路】
    遍历链表，将每个访问过的节点存入集合。
    如果当前节点已在集合中出现过，说明存在环。

    【思考过程】
    1. 最直观的想法：如果有环，遍历过程中一定会再次访问某个节点。
       → 用 set 记录所有访问过的节点，每次检查是否重复即可。

    2. 这里存的是节点引用（id），不是节点值，
       因为不同节点可能有相同的 val。

    3. 如果遍历到 None，说明链表有尽头，不存在环。

    【举例】head = [3,2,0,-4], pos = 1
      第1步：visited = {3}，当前节点 3
      第2步：visited = {3,2}，当前节点 2
      第3步：visited = {3,2,0}，当前节点 0
      第4步：visited = {3,2,0,-4}，当前节点 -4
      第5步：next 指向节点 2，2 已在 visited 中 → 返回 True

    【时间复杂度】O(n)
    【空间复杂度】O(n)
    """
    def hasCycle_hashset(self, head: Optional[ListNode]) -> bool:
        visited = set()
        curr = head
        while curr:
            if curr in visited:
                return True
            visited.add(curr)
            curr = curr.next
        return False

    """
    ==================== 解法二：快慢指针（Floyd 判圈） ====================

    【核心思路】
    设置快指针（每次走两步）和慢指针（每次走一步）。
    如果链表有环，快指针终将追上慢指针；如果无环，快指针会先到达 None。

    【思考过程】
    1. 哈希表需要 O(n) 额外空间，能否只用 O(1)？
       → Floyd 判圈算法：两个速度不同的指针同时出发。

    2. 为什么快指针一定能追上慢指针？
       当两者都进入环后，每一步快指针比慢指针多走 1 步，
       相对距离每次缩小 1，最终一定会重合。

    3. 注意边界：链表为空或只有一个节点且无环时，直接返回 False。

    【举例】head = [3,2,0,-4], pos = 1（尾部连接到节点 2）
      初始：slow=3, fast=3
      第1步：slow=2, fast=0
      第2步：slow=0, fast=2（fast 走了 0→-4→2）
      第3步：slow=-4, fast=-4（fast 走了 2→0→-4）
      slow == fast → 返回 True

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def hasCycle_floyd(self, head: Optional[ListNode]) -> bool:
        slow = fast = head
        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next
            if slow is fast:
                return True
        return False
