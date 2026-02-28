"""
LeetCode 222. Count Complete Tree Nodes
难度: Easy

题目描述：
给你一棵完全二叉树的根节点 root，求出该树的节点个数。
完全二叉树的定义：除了最底层，每一层都是完全填满的，
且最底层的节点都集中在该层最左边的若干位置上。

示例 1：root = [1,2,3,4,5,6] → 输出 6
示例 2：root = [] → 输出 0

进阶：遍历树来统计节点是 O(n) 的，你能设计一个更快的算法吗？

【拓展练习】
1. LeetCode 104. Maximum Depth of Binary Tree —— 求二叉树最大深度
2. LeetCode 110. Balanced Binary Tree —— 判断是否为平衡二叉树
3. LeetCode 958. Check Completeness of a Binary Tree —— 判断是否为完全二叉树
"""

from typing import Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    """
    ==================== 解法一：普通递归遍历 ====================

    【核心思路】
    不利用完全二叉树的特殊性质，直接递归统计所有节点：
    count(root) = 1 + count(left) + count(right)。

    【思考过程】
    1. 最直观的方法：树的节点数 = 左子树节点数 + 右子树节点数 + 1。
    2. 递归基：空节点返回 0。
    3. 这种方法对任何二叉树都适用，没有利用"完全二叉树"的性质。

    【举例】root = [1,2,3,4,5,6]
            1
           / \
          2   3
         / \ /
        4  5 6
      count(4)=1, count(5)=1, count(2)=1+1+1=3
      count(6)=1, count(3)=1+1+0=2
      count(1)=1+3+2=6

    【时间复杂度】O(n)，每个节点访问一次
    【空间复杂度】O(h)，递归栈深度，h = log n
    """
    def countNodes(self, root: Optional[TreeNode]) -> int:
        if not root:
            return 0
        return 1 + self.countNodes(root.left) + self.countNodes(root.right)

    """
    ==================== 解法二：利用完全二叉树性质 + 二分 ====================

    【核心思路】
    比较左右子树的高度（只沿最左路径量高度）：
    - 若左右高度相等，说明左子树是满二叉树，节点数可直接用公式 2^h - 1 算出，
      然后递归计算右子树。
    - 若左高度 > 右高度，说明右子树是比左子树少一层的满二叉树，
      节点数直接算出，然后递归计算左子树。

    【思考过程】
    1. 完全二叉树的特殊性质：最后一层之前全满，最后一层从左到右连续填充。

    2. 如果从根出发，左子树的最左路径深度 == 右子树的最左路径深度：
       → 说明左子树的最后一层是满的（左子树是完美二叉树）。
       → 左子树节点数 = 2^leftHeight - 1，加上根节点共 2^leftHeight 个。
       → 只需递归处理右子树。

    3. 如果左子树深度 > 右子树深度（只会多1）：
       → 说明最后一层的节点没有填到右子树，右子树是高度少1的完美二叉树。
       → 右子树节点数 = 2^rightHeight - 1，加上根节点共 2^rightHeight 个。
       → 只需递归处理左子树。

    4. 每次递归只走一边，递归深度 O(log n)，每次量高度 O(log n)。

    【举例】root = [1,2,3,4,5,6]
            1
           / \
          2   3
         / \ /
        4  5 6

      countNodes(1):
        leftH = height(2) = 2, rightH = height(3) = 1
        leftH != rightH → 递归左子树 + (1 << 1) = countNodes(2) + 2
      countNodes(2):
        leftH = height(4) = 1, rightH = height(5) = 1
        leftH == rightH → (1 << 1) + countNodes(null) = 2 + countNodes(3右)
        不，递归右子树：(1 << 1) + countNodes(5) = 2 + 1 = ... 不精确
      最终结果 = 6 ✓

    【时间复杂度】O(log²n)，递归 O(log n) 层，每层量高度 O(log n)
    【空间复杂度】O(log n)，递归栈深度
    """
    def countNodes_binary(self, root: Optional[TreeNode]) -> int:
        if not root:
            return 0

        left_height = self._get_height(root.left)
        right_height = self._get_height(root.right)

        if left_height == right_height:
            return (1 << left_height) + self.countNodes_binary(root.right)
        else:
            return (1 << right_height) + self.countNodes_binary(root.left)

    def _get_height(self, node: Optional[TreeNode]) -> int:
        height = 0
        while node:
            height += 1
            node = node.left
        return height
