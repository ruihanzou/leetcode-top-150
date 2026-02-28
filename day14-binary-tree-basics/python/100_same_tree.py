"""
LeetCode 100. Same Tree
难度: Easy

题目描述：
给你两棵二叉树的根节点 p 和 q，编写一个函数来检验这两棵树是否相同。
如果两棵树在结构上相同，并且节点具有相同的值，则认为它们是相同的。

示例 1：p = [1,2,3], q = [1,2,3] → 输出 true
  解释：
    1       1
   / \     / \
  2   3   2   3
  两棵树结构和值完全相同。
示例 2：p = [1,2], q = [1,null,2] → 输出 false
  解释：
    1       1
   /         \
  2           2
  结构不同。
示例 3：p = [1,2,1], q = [1,1,2] → 输出 false

【拓展练习】
1. LeetCode 101. Symmetric Tree —— 判断一棵树是否镜像对称
2. LeetCode 572. Subtree of Another Tree —— 判断一棵树是否是另一棵树的子树
3. LeetCode 951. Flip Equivalent Binary Trees —— 翻转等价二叉树
"""

from collections import deque
from typing import Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    """
    ==================== 解法一：DFS 递归比较 ====================

    【核心思路】
    同时递归遍历两棵树，逐节点比较：
    - 如果两个节点都为 None，相同
    - 如果只有一个为 None，不同
    - 如果值不等，不同
    - 否则递归比较左子树和右子树

    【思考过程】
    1. "两棵树相同"可以递归定义：根节点值相同，且左子树相同，且右子树相同。
    2. base case：两个 None → 相同；一个 None 一个非 None → 不同。
    3. Python 中可以利用短路求值写得很简洁。

    【举例】p = [1,2,3], q = [1,2,3]
      isSame(1,1) → 值相等
        isSame(2,2) → 值相等
          isSame(None,None) → True
          isSame(None,None) → True
        → True
        isSame(3,3) → 值相等
          isSame(None,None) → True
          isSame(None,None) → True
        → True
      → True

    【时间复杂度】O(n)，n 为较小树的节点数
    【空间复杂度】O(h)，递归栈深度，最坏 O(n)
    """
    def isSameTree_dfs(self, p: Optional[TreeNode], q: Optional[TreeNode]) -> bool:
        if not p and not q:
            return True
        if not p or not q:
            return False
        return (p.val == q.val
                and self.isSameTree_dfs(p.left, q.left)
                and self.isSameTree_dfs(p.right, q.right))

    """
    ==================== 解法二：BFS 迭代（队列） ====================

    【核心思路】
    用队列同时对两棵树做层序遍历，每次从队列取出一对节点进行比较。
    如果任何一对节点不匹配，立即返回 False。

    【思考过程】
    1. 递归本质上使用了系统栈，我们可以用显式队列（或栈）来模拟。
    2. 每次入队的是"一对"节点 (p_node, q_node)，出队后检查：
       - 都为 None → 跳过（这对没问题）
       - 一个 None → 返回 False
       - 值不等 → 返回 False
       - 值相等 → 把 (p.left, q.left) 和 (p.right, q.right) 入队
    3. 队列清空后说明所有节点对都匹配，返回 True。

    【举例】p = [1,2], q = [1,null,2]
      queue = [(1,1)]
      取出 (1,1)：值相等，入队 (2,None) 和 (None,2)
      取出 (2,None)：一个为 None → 返回 False

    【时间复杂度】O(n)
    【空间复杂度】O(n)，队列空间
    """
    def isSameTree_bfs(self, p: Optional[TreeNode], q: Optional[TreeNode]) -> bool:
        queue = deque([(p, q)])

        while queue:
            node1, node2 = queue.popleft()

            if not node1 and not node2:
                continue
            if not node1 or not node2:
                return False
            if node1.val != node2.val:
                return False

            queue.append((node1.left, node2.left))
            queue.append((node1.right, node2.right))

        return True
