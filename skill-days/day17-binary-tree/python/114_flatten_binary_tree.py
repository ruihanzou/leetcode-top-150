"""
LeetCode 114. Flatten Binary Tree to Linked List
难度: Medium

题目描述：
给定二叉树的根节点 root，请将它展开为一个单链表：
- 展开后的单链表应该同样使用 TreeNode，其中 right 子指针指向链表中下一个节点，
  左子指针始终为 null。
- 展开后的单链表应该与二叉树的前序遍历顺序相同。

示例：root = [1,2,5,3,4,null,6] → 输出 [1,null,2,null,3,null,4,null,5,null,6]

【拓展练习】
1. LeetCode 430. Flatten a Multilevel Doubly Linked List —— 多级链表展开
2. LeetCode 897. Increasing Order Search Tree —— BST展开为中序链表
3. LeetCode 144. Binary Tree Preorder Traversal —— 前序遍历基础
"""

from typing import Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    """
    ==================== 解法一：前序遍历 + 存储后连接 ====================

    【核心思路】
    先用前序遍历收集所有节点到列表中，然后按列表顺序重新连接：
    每个节点的 left 置 None，right 指向下一个节点。

    【思考过程】
    1. 题目要求按前序遍历顺序展开，最直接的想法就是先做一遍前序遍历。

    2. 把前序遍历的结果存到列表 nodes 中，然后遍历列表：
       nodes[i].left = None
       nodes[i].right = nodes[i+1]

    3. 这种方法简单直观，但需要 O(n) 额外空间存储所有节点。

    【举例】root = [1,2,5,3,4,null,6]
          1
         / \
        2    5
       / \    \
      3   4    6

      前序遍历：[1, 2, 3, 4, 5, 6]
      连接：1→2→3→4→5→6（每个节点 left=None, right=下一个）

    【时间复杂度】O(n) 前序遍历 + 重连
    【空间复杂度】O(n) 存储节点列表
    """
    def flatten_preorder(self, root: Optional[TreeNode]) -> None:
        if not root:
            return

        nodes = []

        def preorder(node):
            if not node:
                return
            nodes.append(node)
            preorder(node.left)
            preorder(node.right)

        preorder(root)

        for i in range(len(nodes) - 1):
            nodes[i].left = None
            nodes[i].right = nodes[i + 1]
        nodes[-1].left = None
        nodes[-1].right = None

    """
    ==================== 解法二：Morris 遍历思想（找前驱节点） ====================

    【核心思路】
    对于当前节点，如果有左子树，就把右子树接到左子树最右节点的后面，
    然后把左子树移到右边，左指针清空。如此循环直到所有节点都只有右子树。

    【思考过程】
    1. 前序遍历顺序是 根→左子树→右子树。如果把整棵左子树"插入"到根和右子树之间，
       就相当于按前序展开了。

    2. 具体操作：当前节点 cur 有左子树时：
       a. 找到左子树的最右节点 rightmost（即左子树前序遍历的最后一个节点）
       b. rightmost.right = cur.right（把原右子树接到最右节点后面）
       c. cur.right = cur.left（把左子树移到右边）
       d. cur.left = None（清空左指针）

    3. 然后 cur = cur.right 继续处理下一个节点。

    4. 这个过程类似 Morris 遍历中"建立线索"的思路，每次把右子树挂到前驱节点上。

    【举例】root = [1,2,5,3,4,null,6]
      初始：    1
              / \
             2    5
            / \    \
           3   4    6

      cur=1，左子树存在：
        找左子树最右节点 = 4
        4.right = 5（原右子树）
        1.right = 2（左子树移过来）
        1.left = None
        变成：1 → 2 → 3
                   \
                    4 → 5 → 6

      cur=2，左子树存在：
        找左子树最右节点 = 3
        3.right = 4
        2.right = 3
        2.left = None
        变成：1 → 2 → 3 → 4 → 5 → 6

      cur=3，无左子树，cur=4，无左子树……直到结束。

    【时间复杂度】O(n) 每个节点最多被访问两次（一次作为 cur，一次被寻找最右节点时路过）
    【空间复杂度】O(1) 原地操作
    """
    def flatten_morris(self, root: Optional[TreeNode]) -> None:
        cur = root
        while cur:
            if cur.left:
                rightmost = cur.left
                while rightmost.right:
                    rightmost = rightmost.right
                rightmost.right = cur.right
                cur.right = cur.left
                cur.left = None
            cur = cur.right

    """
    ==================== 解法三：后序递归（从后往前连接） ====================

    【核心思路】
    按"右→左→根"的逆前序顺序递归处理。维护一个 prev 指针指向"上一个处理的节点"。
    这样处理到当前节点时，prev 就是前序遍历中它后面的那个节点，
    直接 cur.right = prev, cur.left = None 即可。

    【思考过程】
    1. 前序是 根→左→右，如果我们倒过来处理：右→左→根，
       那么每个节点处理时，它前序中的"后继"都已经处理完了。

    2. 维护全局 prev，初始为 None。
       递归处理右子树 → 递归处理左子树 → 处理当前节点：
       cur.right = prev（指向前序中的下一个节点）
       cur.left = None
       prev = cur

    3. 这相当于从链表的尾部向头部构建。

    【举例】前序 = [1,2,3,4,5,6]，逆前序处理顺序 = [6,5,4,3,2,1]
      处理6：prev=None → 6.right=None, prev=6
      处理5：prev=6 → 5.right=6, prev=5
      处理4：prev=5 → 4.right=5, prev=4
      处理3：prev=4 → 3.right=4, prev=3
      处理2：prev=3 → 2.right=3, prev=2
      处理1：prev=2 → 1.right=2, prev=1
      结果：1→2→3→4→5→6

    【时间复杂度】O(n) 每个节点访问一次
    【空间复杂度】O(h) 递归栈深度，h 为树高
    """
    def flatten_reverse(self, root: Optional[TreeNode]) -> None:
        self.prev = None

        def helper(node):
            if not node:
                return
            helper(node.right)
            helper(node.left)
            node.right = self.prev
            node.left = None
            self.prev = node

        helper(root)
