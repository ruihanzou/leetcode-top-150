"""
LeetCode 74. Search a 2D Matrix
难度: Medium

题目描述：
给你一个满足下述两条属性的 m x n 整数矩阵：
- 每行中的整数从左到右按非递减顺序排列。
- 每行的第一个整数大于前一行的最后一个整数。
给你一个整数 target，如果 target 在矩阵中，返回 true；否则返回 false。

示例：matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3 → 输出 true

【拓展练习】
1. LeetCode 240. Search a 2D Matrix II —— 每行每列有序但行列间无严格大小关系
2. LeetCode 378. Kth Smallest Element in a Sorted Matrix —— 矩阵二分/堆
3. LeetCode 74 vs 240 的对比 —— 理解不同矩阵结构对搜索策略的影响
"""

from typing import List


class Solution:
    """
    ==================== 解法一：展平为一维二分 ====================

    【核心思路】
    由于矩阵的特殊性质（行尾 < 下行首），整个矩阵从左到右、从上到下
    就是一个严格递增的一维数组。可以将二维坐标 (r, c) 映射为一维索引
    idx = r * n + c，然后在 [0, m*n-1] 上做标准二分。

    【思考过程】
    1. 矩阵满足"每行有序 + 行间递增"，所以按行拼接后就是一个有序数组。

    2. 不需要真的拼接，只需要用公式转换：
       一维索引 idx → 行 r = idx // n, 列 c = idx % n
       这样 matrix[r][c] 就是"一维数组的第 idx 个元素"。

    3. 接下来就是标准的一维二分查找。

    【举例】matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
      m=3, n=4, 展平为 [1,3,5,7,10,11,16,20,23,30,34,60]
      lo=0, hi=11
      mid=5: idx=5 → (1,1) → matrix[1][1]=11 > 3 → hi=4
      mid=2: idx=2 → (0,2) → matrix[0][2]=5 > 3 → hi=1
      mid=0: idx=0 → (0,0) → matrix[0][0]=1 < 3 → lo=1
      mid=1: idx=1 → (0,1) → matrix[0][1]=3 == 3 → 返回 True

    【时间复杂度】O(log(m*n))
    【空间复杂度】O(1)
    """
    def searchMatrix_flatten(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        lo, hi = 0, m * n - 1

        while lo <= hi:
            mid = (lo + hi) // 2
            val = matrix[mid // n][mid % n]
            if val == target:
                return True
            elif val < target:
                lo = mid + 1
            else:
                hi = mid - 1

        return False

    """
    ==================== 解法二：先二分找行，再二分找列 ====================

    【核心思路】
    分两步：
    第一步：对行做二分，找到 target 可能所在的行
           （最后一个首元素 <= target 的行）。
    第二步：在该行内做二分，查找 target。

    【思考过程】
    1. 由于行间也有序，target 只可能在一行中。
       具体来说，target 所在行 r 满足 matrix[r][0] <= target <= matrix[r][n-1]。

    2. 第一步：在每行的首元素上二分，找最后一个 matrix[r][0] <= target 的行。
       这等价于找"第一个 matrix[r][0] > target 的行 - 1"。

    3. 第二步：在找到的行内做标准二分。

    4. 如果第一步找不到合法行（target < matrix[0][0]），直接返回 False。

    【举例】matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 11
      第一步：找行
        行首元素 = [1, 10, 23]
        lo=0, hi=2
        mid=1: matrix[1][0]=10 <= 11 → lo=2
        mid=2: matrix[2][0]=23 > 11 → hi=1
        row = hi = 1（第二行 [10,11,16,20]）

      第二步：在 [10,11,16,20] 中找 11
        lo=0, hi=3
        mid=1: matrix[1][1]=11 == 11 → 返回 True

    【时间复杂度】O(log m + log n)
    【空间复杂度】O(1)
    """
    def searchMatrix_two_binary(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])

        lo, hi = 0, m - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if matrix[mid][0] <= target:
                lo = mid + 1
            else:
                hi = mid - 1

        row = hi
        if row < 0:
            return False

        lo, hi = 0, n - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if matrix[row][mid] == target:
                return True
            elif matrix[row][mid] < target:
                lo = mid + 1
            else:
                hi = mid - 1

        return False
