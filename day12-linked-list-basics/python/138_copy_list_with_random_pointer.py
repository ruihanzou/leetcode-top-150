"""
LeetCode 138. Copy List with Random Pointer
难度: Medium

题目描述：
给你一个长度为 n 的链表，每个节点包含一个额外增加的随机指针 random，
该指针可以指向链表中的任何节点或空节点。
构造这个链表的深拷贝。深拷贝应该正好由 n 个全新节点组成，
其中每个新节点的值都设为其对应的原节点的值。
新节点的 next 指针和 random 指针也都应指向复制链表中的新节点，
并使原链表和复制链表中的这些指针能够表示相同的链表状态。
复制链表中的指针都不应指向原链表中的节点。

示例 1：head = [[7,null],[13,0],[11,4],[10,2],[1,0]]
  → 输出 [[7,null],[13,0],[11,4],[10,2],[1,0]]
示例 2：head = [[1,1],[2,1]] → 输出 [[1,1],[2,1]]
示例 3：head = [[3,null],[3,0],[3,null]] → 输出 [[3,null],[3,0],[3,null]]

【拓展练习】
1. LeetCode 133. Clone Graph —— 克隆无向图，同样需要哈希表记录已复制的节点
2. LeetCode 1485. Clone Binary Tree With Random Pointer —— 克隆带随机指针的二叉树
3. LeetCode 382. Linked List Random Node —— 链表随机节点（蓄水池抽样）
"""

from typing import Optional


class Node:
    def __init__(self, x: int, next: 'Node' = None, random: 'Node' = None):
        self.val = int(x)
        self.next = next
        self.random = random


class Solution:
    """
    ==================== 解法一：哈希表映射 ====================

    【核心思路】
    用哈希表建立"原节点 → 新节点"的映射。
    第一遍遍历：创建所有新节点，建立映射关系。
    第二遍遍历：根据映射设置每个新节点的 next 和 random 指针。

    【思考过程】
    1. 深拷贝的难点在于 random 指针：它可以指向链表中任意位置，
       而我们在顺序遍历时，random 指向的目标节点可能还没被创建。

    2. 解决方案：两遍遍历。
       第一遍只创建节点、不连接指针，同时用字典记录 old→new 的映射。
       第二遍利用映射把 next 和 random 指针连上。

    3. 对于 random == None 的情况，dict.get(None) 返回 None，刚好正确。

    【举例】head = [7→13→11→10→1]
      第一遍：创建 7', 13', 11', 10', 1'，建立映射
        mapping = {7→7', 13→13', 11→11', 10→10', 1→1'}
      第二遍：
        7'.next = mapping[7.next] = mapping[13] = 13'
        7'.random = mapping[7.random] = mapping[None] = None
        13'.next = 11', 13'.random = mapping[head] = 7'
        ... 依次类推

    【时间复杂度】O(n)
    【空间复杂度】O(n)，哈希表存储映射
    """
    def copyRandomList_hashmap(self, head: Optional[Node]) -> Optional[Node]:
        if not head:
            return None

        mapping = {}

        curr = head
        while curr:
            mapping[curr] = Node(curr.val)
            curr = curr.next

        curr = head
        while curr:
            mapping[curr].next = mapping.get(curr.next)
            mapping[curr].random = mapping.get(curr.random)
            curr = curr.next

        return mapping[head]

    """
    ==================== 解法二：交织复制（O(1) 空间） ====================

    【核心思路】
    不使用哈希表，通过在原链表每个节点后面插入其复制节点的方式，
    利用位置关系来设置 random 指针，最后拆分出复制链表。
    分三步：① 交织插入 → ② 设置 random → ③ 拆分链表

    【思考过程】
    1. 哈希表解法用 O(n) 额外空间，能否做到 O(1)？
       → 关键问题：如何在没有映射的情况下，找到原节点对应的复制节点？

    2. 巧妙的想法：把复制节点插到原节点后面！
       原链表：A → B → C
       交织后：A → A' → B → B' → C → C'
       这样 A' = A.next，B' = B.next，即"原节点的 next 就是其复制节点"。

    3. 设置 random：如果 A.random = C，那么 A'.random = C.next = C'。
       因为 C' 就在 C 的后面。

    4. 最后拆分：将交织链表恢复为两个独立的链表。
       注意必须恢复原链表的结构（面试中常被要求）。

    【举例】原链表：7 → 13 → 11，其中 13.random = 7
      Step1 交织：7 → 7' → 13 → 13' → 11 → 11'
      Step2 random：13.random = 7，所以 13'.random = 7.next = 7'
      Step3 拆分：
        原链表恢复：7 → 13 → 11
        复制链表：7' → 13' → 11'

    【时间复杂度】O(n)
    【空间复杂度】O(1)，不计输出链表的空间
    """
    def copyRandomList_interweave(self, head: Optional[Node]) -> Optional[Node]:
        if not head:
            return None

        # Step 1: 在每个原节点后面插入复制节点
        curr = head
        while curr:
            copy = Node(curr.val)
            copy.next = curr.next
            curr.next = copy
            curr = copy.next

        # Step 2: 设置复制节点的 random 指针
        curr = head
        while curr:
            if curr.random:
                curr.next.random = curr.random.next
            curr = curr.next.next

        # Step 3: 拆分链表
        new_head = head.next
        curr = head
        while curr:
            copy = curr.next
            curr.next = copy.next
            copy.next = copy.next.next if copy.next else None
            curr = curr.next

        return new_head
