"""
LeetCode 54. Spiral Matrix
难度: Medium

题目描述：
给你一个 m 行 n 列的矩阵 matrix，请按照顺时针螺旋顺序，返回矩阵中的所有元素。

示例 1：
输入：matrix = [[1,2,3],[4,5,6],[7,8,9]]
输出：[1,2,3,6,9,8,7,4,5]

示例 2：
输入：matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
输出：[1,2,3,4,8,12,11,10,9,5,6,7]

【拓展练习】
1. LeetCode 59. Spiral Matrix II —— 反向操作，按螺旋顺序填入1到n²
2. LeetCode 885. Spiral Matrix III —— 从指定起点开始螺旋遍历整个网格
3. LeetCode 2326. Spiral Matrix IV —— 将链表值按螺旋顺序填入矩阵
"""

from typing import List


class Solution:
    """
    ==================== 解法一：边界模拟 ====================

    【核心思路】
    维护 top/bottom/left/right 四个边界，按"右→下→左→上"的顺序逐层收缩遍历。
    每走完一条边，收缩对应边界；当边界交叉时结束。

    【思考过程】
    1. 螺旋顺序本质是"从外向内逐层剥洋葱"。
       最外层是：顶行从左到右 → 右列从上到下 → 底行从右到左 → 左列从下到上。

    2. 走完最外层后，四个边界各向内收缩一格，处理下一层。
       → top++, bottom--, left++, right--

    3. 需要注意：走完"右"和"下"之后，要检查 top<=bottom 和 left<=right，
       避免单行或单列时重复遍历。

    【举例】matrix = [[1,2,3],[4,5,6],[7,8,9]]
      初始：top=0, bottom=2, left=0, right=2
      第一层：
        右：1,2,3 → top=1
        下：6,9   → right=1
        左：8,7   → bottom=1
        上：4     → left=1
      第二层：top=1,bottom=1,left=1,right=1
        右：5     → top=2
      top>bottom，结束。结果：[1,2,3,6,9,8,7,4,5]

    【时间复杂度】O(m*n) — 每个元素恰好访问一次
    【空间复杂度】O(1) — 不计输出数组
    """
    def spiralOrder_boundary(self, matrix: List[List[int]]) -> List[int]:
        result = []
        top, bottom = 0, len(matrix) - 1
        left, right = 0, len(matrix[0]) - 1

        while top <= bottom and left <= right:
            for j in range(left, right + 1):
                result.append(matrix[top][j])
            top += 1

            for i in range(top, bottom + 1):
                result.append(matrix[i][right])
            right -= 1

            if top <= bottom:
                for j in range(right, left - 1, -1):
                    result.append(matrix[bottom][j])
                bottom -= 1

            if left <= right:
                for i in range(bottom, top - 1, -1):
                    result.append(matrix[i][left])
                left += 1

        return result

    """
    ==================== 解法二：方向数组+visited标记 ====================

    【核心思路】
    用方向数组 dirs = [右,下,左,上] 控制移动方向，用 visited 标记已访问格子。
    碰到边界或已访问格子时，顺时针转向（方向索引+1 mod 4）。

    【思考过程】
    1. 螺旋的本质：一直往当前方向走，走不动了就右转。
       → 用一个方向索引 d 和方向数组 dirs 实现。

    2. "走不动"的条件：下一个位置越界 或 已经访问过。
       → 用 visited 集合或二维数组标记。

    3. 总共需要访问 m*n 个格子，用计数器控制循环。

    【举例】matrix = [[1,2,3],[4,5,6],[7,8,9]]
      从(0,0)出发，方向=右：
      (0,0)→(0,1)→(0,2)：碰到右边界，转向=下
      (1,2)→(2,2)：碰到下边界，转向=左
      (2,1)→(2,0)：碰到左边界，转向=上
      (1,0)：碰到已访问(0,0)，转向=右
      (1,1)：9个格子全访问完，结束

    【时间复杂度】O(m*n)
    【空间复杂度】O(m*n) — visited 数组
    """
    def spiralOrder_direction(self, matrix: List[List[int]]) -> List[int]:
        m, n = len(matrix), len(matrix[0])
        result = []
        visited = [[False] * n for _ in range(m)]
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]

        r, c, d = 0, 0, 0
        for _ in range(m * n):
            result.append(matrix[r][c])
            visited[r][c] = True

            nr, nc = r + dirs[d][0], c + dirs[d][1]

            if nr < 0 or nr >= m or nc < 0 or nc >= n or visited[nr][nc]:
                d = (d + 1) % 4
                nr, nc = r + dirs[d][0], c + dirs[d][1]

            r, c = nr, nc

        return result
