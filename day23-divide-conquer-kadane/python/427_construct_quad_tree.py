"""
LeetCode 427. Construct Quad Tree
难度: Medium

题目描述：
给你一个 n * n 的二维矩阵 grid（值为 0 或 1），用四叉树表示该矩阵。
四叉树中每个节点要么是叶子节点，要么有四个子节点（topLeft, topRight,
bottomLeft, bottomRight）。
- 如果区域内所有值相同，该节点是叶子节点，val 为该值。
- 如果区域内值不全相同，该节点不是叶子节点，递归构建四个子区域。

返回四叉树的根节点。

示例：grid = [[0,1],[1,0]] → 输出一棵非叶根节点，四个叶子分别为 0,1,1,0

【拓展练习】
1. LeetCode 558. Logical OR of Two Binary Grids Represented as Quad-Trees —— 四叉树的合并操作
2. LeetCode 304. Range Sum Query 2D - Immutable —— 二维前缀和的基础应用
3. LeetCode 240. Search a 2D Matrix II —— 二维矩阵的分治搜索
"""

from typing import List


class Node:
    def __init__(self, val: bool, isLeaf: bool,
                 topLeft=None, topRight=None,
                 bottomLeft=None, bottomRight=None):
        self.val = val
        self.isLeaf = isLeaf
        self.topLeft = topLeft
        self.topRight = topRight
        self.bottomLeft = bottomLeft
        self.bottomRight = bottomRight


class Solution:
    """
    ==================== 解法一：递归分治 ====================

    【核心思路】
    对当前区域检查是否所有值相同：若相同则返回叶子节点；
    否则将区域四等分，递归构建四个子节点。

    【思考过程】
    1. 四叉树的定义本身就是递归的：要么整个区域是叶子，要么分成四块各自递归。
       所以自顶向下递归是最自然的做法。

    2. 关键操作是"判断区域内所有值是否相同"。最简单的方法是遍历整个区域，
       检查所有元素是否都等于左上角元素。

    3. 如果全相同 → 创建叶子节点（isLeaf=True, val=该值）。
       如果不全相同 → 创建内部节点（isLeaf=False），递归构建四个子树。

    4. 区域用 (row, col, size) 描述：左上角坐标和边长。
       四等分后每块边长 size//2。

    【举例】grid = [[1,1,0,0],[1,1,0,0],[0,0,1,1],[0,0,1,1]]  (4×4)
      整体不全相同 → 分成四块：
        左上 [[1,1],[1,1]] → 全1 → 叶子(val=True)
        右上 [[0,0],[0,0]] → 全0 → 叶子(val=False)
        左下 [[0,0],[0,0]] → 全0 → 叶子(val=False)
        右下 [[1,1],[1,1]] → 全1 → 叶子(val=True)
      根节点 isLeaf=False, 四个叶子子节点

    【时间复杂度】O(n² log n) —— 每层递归遍历所有格子判断是否全同，共 log n 层
    【空间复杂度】O(log n) 递归栈深度
    """
    def construct_recursive(self, grid: List[List[int]]) -> 'Node':
        def build(row, col, size):
            val = grid[row][col]
            all_same = all(
                grid[i][j] == val
                for i in range(row, row + size)
                for j in range(col, col + size)
            )
            if all_same:
                return Node(val == 1, True)
            half = size // 2
            return Node(
                True, False,
                build(row, col, half),
                build(row, col + half, half),
                build(row + half, col, half),
                build(row + half, col + half, half),
            )

        return build(0, 0, len(grid))

    """
    ==================== 解法二：前缀和优化 ====================

    【核心思路】
    预处理二维前缀和，O(1) 判断任意子矩阵是否全 0 或全 1，
    避免每次递归都 O(size²) 遍历。

    【思考过程】
    1. 解法一的瓶颈在于 allSame 检查需要遍历整个子区域。
       如果能 O(1) 判断"区域内 1 的个数"，就能快速判断：
       - sum == 0 → 全0
       - sum == size² → 全1
       - 否则 → 非全同

    2. 二维前缀和 prefix[i][j] = sum(grid[0..i-1][0..j-1])。
       子矩阵 (r1,c1)-(r2,c2) 的和用容斥原理 O(1) 计算。

    3. 递归结构不变，只是 allSame 变成 O(1) 查询。
       每个节点只做 O(1) 工作，总节点数最多 O(n²)，所以总复杂度 O(n²)。

    【举例】grid = [[0,1],[1,0]]  (2×2)
      prefix = [[0,0,0],[0,0,1],[0,1,2]]
      整体 sum = 2, size²=4, 2≠0且≠4 → 非全同 → 分成四个 1×1 叶子

    【时间复杂度】O(n²) —— 前缀和预处理 O(n²)，构建四叉树每节点 O(1)
    【空间复杂度】O(n²) 前缀和数组
    """
    def construct_prefix_sum(self, grid: List[List[int]]) -> 'Node':
        n = len(grid)
        prefix = [[0] * (n + 1) for _ in range(n + 1)]
        for i in range(1, n + 1):
            for j in range(1, n + 1):
                prefix[i][j] = (grid[i - 1][j - 1]
                                + prefix[i - 1][j]
                                + prefix[i][j - 1]
                                - prefix[i - 1][j - 1])

        def region_sum(r1, c1, r2, c2):
            return prefix[r2][c2] - prefix[r1][c2] - prefix[r2][c1] + prefix[r1][c1]

        def build(row, col, size):
            s = region_sum(row, col, row + size, col + size)
            if s == 0:
                return Node(False, True)
            if s == size * size:
                return Node(True, True)
            half = size // 2
            return Node(
                True, False,
                build(row, col, half),
                build(row, col + half, half),
                build(row + half, col, half),
                build(row + half, col + half, half),
            )

        return build(0, 0, n)
