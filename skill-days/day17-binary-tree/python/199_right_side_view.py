"""
LeetCode 199. Binary Tree Right Side View
难度: Medium

题目描述：
给定一个二叉树的根节点 root，想象自己站在它的右侧，
按照从顶部到底部的顺序，返回从右侧所能看到的节点值。

示例 1：root = [1,2,3,null,5,null,4] → 输出 [1,3,4]
示例 2：root = [1,null,3] → 输出 [1,3]
示例 3：root = [] → 输出 []

【拓展练习】
1. LeetCode 116. Populating Next Right Pointers —— 填充每个节点的下一个右侧节点指针
2. LeetCode 515. Find Largest Value in Each Tree Row —— 每层的最大值
3. LeetCode 513. Find Bottom Left Tree Value —— 树最底层最左边的值
"""

from typing import Optional, List
from collections import deque


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    """
    ==================== 解法一：BFS 层序遍历 ====================

    【核心思路】
    使用 BFS 逐层遍历二叉树，每层的最后一个节点就是从右侧看到的节点。

    【思考过程】
    1. "右视图"就是每层最右边的节点。自然想到层序遍历。

    2. BFS 用队列实现，每次处理一层：
       - 记录当前层的节点数 size
       - 遍历这 size 个节点，最后一个就是该层的右视图节点
       - 同时将子节点加入队列

    3. 这是最标准、最直观的解法。

    【举例】root = [1,2,3,null,5,null,4]
            1
           / \
          2   3
           \   \
            5   4

      第0层: [1]，最后一个=1
      第1层: [2, 3]，最后一个=3
      第2层: [5, 4]，最后一个=4
      右视图 = [1, 3, 4]

    【时间复杂度】O(n)，每个节点访问一次
    【空间复杂度】O(n)，队列最多存储一层的节点数（最坏 n/2）
    """
    def rightSideView(self, root: Optional[TreeNode]) -> List[int]:
        if not root:
            return []

        result = []
        queue = deque([root])

        while queue:
            size = len(queue)
            for i in range(size):
                node = queue.popleft()
                if i == size - 1:
                    result.append(node.val)
                if node.left:
                    queue.append(node.left)
                if node.right:
                    queue.append(node.right)

        return result

    """
    ==================== 解法二：DFS（优先访问右子树） ====================

    【核心思路】
    DFS 遍历二叉树，优先访问右子树。对于每一层，第一个被访问到的节点
    就是右视图中的节点。用 depth 与 result 的长度关系判断是否是该层的第一个。

    【思考过程】
    1. 如果 DFS 时总是先走右子树再走左子树，那么每层第一个被访问到的
       节点一定是最右边的。

    2. 如何判断某个节点是否是该层"第一次被访问"？
       → 用 result 列表的长度：如果 depth == len(result)，
         说明这一层还没有节点加入 result，当前节点就是该层右视图节点。

    3. 遍历顺序：根 → 右 → 左（前序遍历的镜像）。

    【举例】root = [1,2,3,null,5,null,4]
            1
           / \
          2   3
           \   \
            5   4

      dfs(1, depth=0): result=[], 0==0 → 加入1, result=[1]
        dfs(3, depth=1): result=[1], 1==1 → 加入3, result=[1,3]
          dfs(4, depth=2): result=[1,3], 2==2 → 加入4, result=[1,3,4]
        dfs(2, depth=1): result=[1,3,4], 1!=3 → 不加
          dfs(5, depth=2): result=[1,3,4], 2!=3 → 不加
      结果 = [1, 3, 4]

    【时间复杂度】O(n)，每个节点访问一次
    【空间复杂度】O(h)，递归栈深度，h 为树高
    """
    def rightSideView_dfs(self, root: Optional[TreeNode]) -> List[int]:
        result = []

        def dfs(node, depth):
            if not node:
                return

            if depth == len(result):
                result.append(node.val)

            dfs(node.right, depth + 1)
            dfs(node.left, depth + 1)

        dfs(root, 0)
        return result
