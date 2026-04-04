"""
LeetCode 117. Populating Next Right Pointers in Each Node II
难度: Medium

题目描述：
给定一个二叉树（不一定是完美二叉树），填充每个节点的 next 指针，让这个指针指向其同层的右侧节点。
如果找不到右侧节点，则将 next 指针设置为 NULL。初始状态下，所有 next 指针都被设置为 NULL。

示例：root = [1,2,3,4,5,null,7]
      填充后：1→NULL, 2→3, 3→NULL, 4→5, 5→7, 7→NULL

【拓展练习】
1. LeetCode 116. Populating Next Right Pointers in Each Node —— 完美二叉树版本，更简单
2. LeetCode 199. Binary Tree Right Side View —— 利用层序遍历的变体
3. LeetCode 102. Binary Tree Level Order Traversal —— BFS层序遍历基础
"""

from collections import deque
from typing import Optional


class Node:
    def __init__(self, val: int = 0, left=None, right=None, next=None):
        self.val = val
        self.left = left
        self.right = right
        self.next = next


class Solution:
    """
    ==================== 解法一：BFS 层序遍历 ====================

    【核心思路】
    用 BFS 逐层遍历二叉树，在每一层内，将同层节点从左到右用 next 指针串联起来。
    每一层的最后一个节点的 next 保持为 None。

    【思考过程】
    1. "同层右侧节点"→ 自然联想到层序遍历（BFS）。

    2. BFS 用队列实现，每次处理一层前先记录当前层的节点数 size。
       然后依次出队 size 个节点，前 size-1 个节点的 next 指向下一个出队的节点，
       第 size 个节点的 next 为 None。

    3. 同时把左右子节点入队，保证下一层的遍历。

    【举例】root = [1,2,3,4,5,null,7]
          1
         / \
        2    3
       / \    \
      4   5    7

      第1层：[1] → 1.next=None
      第2层：[2,3] → 2.next=3, 3.next=None
      第3层：[4,5,7] → 4.next=5, 5.next=7, 7.next=None

    【时间复杂度】O(n) 每个节点访问一次
    【空间复杂度】O(n) 队列最多存储一层的节点数
    """
    def connect_bfs(self, root: Optional[Node]) -> Optional[Node]:
        if not root:
            return None

        queue = deque([root])
        while queue:
            size = len(queue)
            for i in range(size):
                node = queue.popleft()
                node.next = queue[0] if i < size - 1 else None
                if node.left:
                    queue.append(node.left)
                if node.right:
                    queue.append(node.right)

        return root

    """
    ==================== 解法二：利用已建立的 next 指针（常量空间） ====================

    【核心思路】
    处理第 N 层时，利用第 N-1 层已经建好的 next 指针来横向遍历。
    用一个虚拟头节点 dummy 串联第 N 层的所有节点，避免处理第一个子节点的特殊情况。

    【思考过程】
    1. BFS 用了队列存储整层节点，能否省掉？
       → 如果上一层的 next 指针已经建好，我们可以通过 next 横向遍历上一层，
         不需要队列。

    2. 遍历第 N-1 层的每个节点 cur，把 cur 的子节点挂到第 N 层的链表上：
       - 如果 cur 有左子 → 接到 tail.next
       - 如果 cur 有右子 → 接到 tail.next
       - 然后 cur = cur.next（沿着已建好的 next 指针横移）

    3. 用 dummy 虚拟头节点来统一处理"第 N 层第一个节点"的情况。
       每层结束后，dummy.next 就是下一层的最左节点，作为新的起点。

    4. 重复直到某层没有子节点（dummy.next == None）。

    【举例】root = [1,2,3,4,5,null,7]
      处理第0层(根)：cur=1
        1有左子2 → dummy→2，tail=2
        1有右子3 → 2.next=3，tail=3
        cur=1.next=None → 第1层链表建好：2→3→None
      处理第1层：cur=2
        2有左子4 → dummy→4，tail=4
        2有右子5 → 4.next=5，tail=5
        cur=2.next=3
        3无左子
        3有右子7 → 5.next=7，tail=7
        cur=3.next=None → 第2层链表建好：4→5→7→None
      处理第2层：cur=4，无子节点；cur=5，无子节点；cur=7，无子节点
        dummy.next=None → 结束

    【时间复杂度】O(n) 每个节点访问一次
    【空间复杂度】O(1) 只用了几个指针变量
    """
    def connect_constant_space(self, root: Optional[Node]) -> Optional[Node]:
        if not root:
            return None

        cur = root # 当前层的起点
        while cur:
            dummy = Node(0) # 下一层的虚拟头
            tail = dummy  # 下一层链表的尾指针
            

            while cur:
                if cur.left:
                    tail.next = cur.left
                    tail = tail.next
                if cur.right:
                    tail.next = cur.right
                    tail = tail.next
                cur = cur.next

            cur = dummy.next

        return root
