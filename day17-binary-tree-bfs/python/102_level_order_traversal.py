"""
LeetCode 102. Binary Tree Level Order Traversal
难度: Medium

题目描述：
给定二叉树的根节点 root，返回其节点值的层序遍历（即按从左到右、逐层遍历）。

示例：
    3
   / \
  9  20
    /  \
   15   7
输出: [[3], [9, 20], [15, 7]]

【拓展练习】
1. LeetCode 107. Binary Tree Level Order Traversal II —— 自底向上的层序遍历
2. LeetCode 103. Binary Tree Zigzag Level Order Traversal —— 锯齿形层序遍历
3. LeetCode 199. Binary Tree Right Side View —— 每层最右节点
"""

from typing import List, Optional
from collections import deque


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    """
    ==================== 解法一：BFS 队列（逐层遍历） ====================

    【核心思路】
    使用队列做 BFS，每次处理一整层。
    每层开始时队列中的所有节点恰好是当前层的全部节点，
    逐个出队并记录值，同时将子节点入队供下一层使用。

    【思考过程】
    1. 层序遍历最直觉的方式就是 BFS。
       核心技巧是在每层开始时，用 level_size = len(queue) 锁定当前层的节点数，
       然后只处理这 level_size 个节点，处理完后队列中剩下的就是下一层的节点。

    2. 每层收集到一个 level 列表中，最终所有层的列表组成结果。

    【举例】root = [3, 9, 20, null, null, 15, 7]
      初始: queue=[3]
      第0层: size=1, 出队3, 入队9,20 → level=[3]
      第1层: size=2, 出队9(无子), 出队20(入队15,7) → level=[9,20]
      第2层: size=2, 出队15, 出队7 → level=[15,7]
      结果: [[3], [9,20], [15,7]]

    【时间复杂度】O(n) 每个节点入队出队各一次
    【空间复杂度】O(n) 队列最多存放一层的节点
    """
    def levelOrder_bfs(self, root: Optional[TreeNode]) -> List[List[int]]:
        if not root:
            return []

        result = []
        queue = deque([root])

        while queue:
            level_size = len(queue)
            level = []

            for _ in range(level_size):
                node = queue.popleft()
                level.append(node.val)
                if node.left:
                    queue.append(node.left)
                if node.right:
                    queue.append(node.right)

            result.append(level)

        return result

    """
    ==================== 解法二：DFS 递归（按深度分组） ====================

    【核心思路】
    DFS 遍历二叉树，传递当前深度 depth。
    维护一个二维列表 result，result[depth] 存放第 depth 层的节点值。
    当 depth == len(result) 时，说明首次到达该层，需要新建空列表。

    【思考过程】
    1. BFS 天然按层处理数据，但 DFS 也可以——关键是用 depth 参数标记层级。

    2. 前序遍历（根→左→右）保证同一层中节点按从左到右的顺序被访问，
       因此直接 append 到 result[depth] 即可保持正确顺序。

    3. 如果 depth 超出 result 当前长度，说明到了新的一层，需要新增空列表。
       由于 DFS 先递归左子树，首次到达每层一定是最左边的路径，
       所以 depth 恰好等于 len(result)，不会跳过某一层。

    【举例】root = [3, 9, 20, null, null, 15, 7]
      dfs(3, 0): result=[[3]]
      dfs(9, 1): result=[[3],[9]]
      dfs(20, 1): result=[[3],[9,20]]
      dfs(15, 2): result=[[3],[9,20],[15]]
      dfs(7, 2): result=[[3],[9,20],[15,7]]

    【时间复杂度】O(n) 每个节点访问一次
    【空间复杂度】O(h) 递归栈深度，h 为树的高度（最坏 O(n)）
    """
    def levelOrder_dfs(self, root: Optional[TreeNode]) -> List[List[int]]:
        result = []

        def dfs(node: Optional[TreeNode], depth: int) -> None:
            if not node:
                return

            if depth == len(result):
                result.append([])

            result[depth].append(node.val)
            dfs(node.left, depth + 1)
            dfs(node.right, depth + 1)

        dfs(root, 0)
        return result
