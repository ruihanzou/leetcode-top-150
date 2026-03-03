"""
LeetCode 124. Binary Tree Maximum Path Sum
难度: Hard

题目描述：
二叉树中的「路径」被定义为一条节点序列，序列中每对相邻节点之间都存在一条边。
同一个节点在一条路径中最多出现一次。该路径至少包含一个节点，且不一定经过根节点。
路径和是路径中各节点值的总和。
给你一个二叉树的根节点 root，返回其最大路径和。

示例 1：root = [1,2,3] → 输出 6（路径 2→1→3）
示例 2：root = [-10,9,20,null,null,15,7] → 输出 42（路径 15→20→7）

【拓展练习】
1. LeetCode 112. Path Sum —— 判断是否存在根到叶子的路径和等于目标值
2. LeetCode 543. Diameter of Binary Tree —— 求二叉树直径（最长路径的边数）
3. LeetCode 687. Longest Univalue Path —— 最长同值路径
"""

from typing import Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    """
    ==================== 解法一：DFS后序遍历 + 全局变量 ====================

    【核心思路】
    对每个节点 node，计算"经过 node 的最大路径和"（左链 + node + 右链），
    用实例变量记录所有节点中的最大值。同时，向父节点返回"以 node 为端点的
    最大单链和"（node + max(左链, 右链)），因为路径不能分叉。

    【思考过程】
    1. 路径可以从任意节点出发到任意节点，可能经过也可能不经过根。
       关键观察：任何路径都有一个"最高点"（拐弯处），路径在这个节点处
       从左子树上来、经过该节点、再到右子树下去（或只走一边）。

    2. 对于每个节点 node，经过它的最大路径和 = leftGain + node.val + rightGain，
       其中 leftGain = max(0, 左子树能贡献的最大单链和)，
       rightGain = max(0, 右子树能贡献的最大单链和)。
       取 max(0, ...) 是因为如果子树贡献为负，不如不走。

    3. 但向父节点汇报时，只能选一条链（左或右），不能两边都选，
       否则父节点那里路径就分叉了。所以返回 node.val + max(leftGain, rightGain)。

    4. 遍历所有节点，取"经过该节点的路径和"的最大值，就是答案。

    【举例】root = [-10, 9, 20, null, null, 15, 7]
              -10
              / \
             9   20
                / \
               15   7

      节点15: leftGain=0, rightGain=0, 经过路径和=15, 返回15
      节点7:  leftGain=0, rightGain=0, 经过路径和=7, 返回7
      节点20: leftGain=15, rightGain=7, 经过路径和=15+20+7=42, 返回20+15=35
      节点9:  leftGain=0, rightGain=0, 经过路径和=9, 返回9
      节点-10: leftGain=9, rightGain=35, 经过路径和=9+(-10)+35=34, 返回-10+35=25
      全局最大值 = max(15, 7, 42, 9, 34) = 42

    【时间复杂度】O(n)，每个节点访问一次
    【空间复杂度】O(h)，递归栈深度，h 为树高
    """
    def maxPathSum(self, root: Optional[TreeNode]) -> int:
        self.max_sum = float('-inf')

        def dfs(node):
            if not node:
                return 0

            left_gain = max(0, dfs(node.left))
            right_gain = max(0, dfs(node.right))

            self.max_sum = max(self.max_sum, left_gain + node.val + right_gain)

            return node.val + max(left_gain, right_gain)

        dfs(root)
        return self.max_sum

    """
    ==================== 解法二：DFS + nonlocal 闭包变量 ====================

    【核心思路】
    与解法一逻辑完全相同，但不使用 self 实例变量，而是利用 Python 的
    nonlocal 关键字在闭包中维护全局最大值。

    【思考过程】
    1. 解法一用 self.max_sum 保存全局最大值，是面向对象的写法。
    2. 在 Python 中，也可以用嵌套函数 + nonlocal 来实现同样的效果：
       在外层函数中定义变量，内层函数通过 nonlocal 声明来修改它。
    3. 这种写法更函数式，避免了修改对象状态。

    【时间复杂度】O(n)，每个节点访问一次
    【空间复杂度】O(h)，递归栈深度
    """
    def maxPathSum_nonlocal(self, root: Optional[TreeNode]) -> int:
        max_sum = float('-inf')

        def dfs(node):
            nonlocal max_sum
            if not node:
                return 0

            left_gain = max(0, dfs(node.left))
            right_gain = max(0, dfs(node.right))

            max_sum = max(max_sum, left_gain + node.val + right_gain)

            return node.val + max(left_gain, right_gain)

        dfs(root)
        return max_sum
