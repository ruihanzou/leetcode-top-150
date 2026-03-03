"""
LeetCode 149. Max Points on a Line
难度: Hard

题目描述：
给定一个数组 points，其中 points[i] = [xi, yi] 表示 X-Y 平面上的一个点，
返回同一条直线上最多有多少个点。

示例：points = [[1,1],[2,2],[3,3]] → 输出 3

【拓展练习】
1. LeetCode 356. Line Reflection —— 判断点集是否关于某条竖直线对称
2. LeetCode 447. Number of Boomerangs —— 距离哈希的应用
3. LeetCode 939. Minimum Area Rectangle —— 哈希+枚举对角线
"""

from typing import List
from math import gcd
from collections import defaultdict


class Solution:
    """
    ==================== 解法一：哈希表统计斜率 ====================

    【核心思路】
    固定一个点 i，枚举所有其他点 j，计算斜率并用哈希表统计。
    过点 i 的所有直线中，斜率出现次数最多的那条就是经过 i 的最优直线。
    对所有 i 取最大值即为答案。

    【思考过程】
    1. 两点确定一条直线。如果我们固定点 i，枚举所有 j，
       斜率相同的点 j 们必然和 i 共线。

    2. 斜率用浮点数表示有精度问题。改用 (dy/g, dx/g) 的元组作为 key，
       其中 g = gcd(dy, dx)，并统一符号（保证 dx > 0，或 dx == 0 时 dy > 0）。

    3. 特殊情况：点 i 和点 j 重合时 dx=dy=0，单独计数为 same。
       最终经过 i 的最优直线上的点数 = max(斜率出现次数) + same + 1（i自己）。

    【举例】points = [[1,1],[3,2],[5,3],[4,1],[2,3],[1,4]]
      固定 (1,1):
        → (3,2): 斜率 (1,2)
        → (5,3): 斜率 (1,2)   ← 和 (3,2) 同斜率，共线！
        → (4,1): 斜率 (0,1)
        → (2,3): 斜率 (2,1)
        → (1,4): 斜率 (1,0)   即垂直线
      斜率 (1,2) 出现 2 次 → 加上 (1,1) 自己 = 3 个点共线

    【时间复杂度】O(n²) 两层循环
    【空间复杂度】O(n) 哈希表
    """
    def maxPoints_slope(self, points: List[List[int]]) -> int:
        n = len(points)
        if n <= 2:
            return n

        ans = 1
        for i in range(n):
            slopes = defaultdict(int)
            same = 0
            local_max = 0
            for j in range(i + 1, n):
                dx = points[j][0] - points[i][0]
                dy = points[j][1] - points[i][1]

                if dx == 0 and dy == 0:
                    same += 1
                    continue

                g = gcd(abs(dx), abs(dy))
                dx //= g
                dy //= g

                if dx < 0:
                    dx, dy = -dx, -dy
                elif dx == 0:
                    dy = abs(dy)

                slopes[(dy, dx)] += 1
                local_max = max(local_max, slopes[(dy, dx)])

            ans = max(ans, local_max + same + 1)

        return ans

    """
    ==================== 解法二：暴力枚举两点确定直线 ====================

    【核心思路】
    枚举所有两点对 (i, j) 确定一条直线，然后检查其他所有点 k 是否在这条直线上。
    用叉积判断三点共线：(xj-xi)*(yk-yi) == (xk-xi)*(yj-yi)。

    【思考过程】
    1. 最直接的思路：遍历所有可能的直线（由两点确定），数每条直线上有多少点。

    2. 三点共线的充要条件是叉积为零：
       向量 (j-i) × (k-i) = 0，即 (xj-xi)(yk-yi) - (xk-xi)(yj-yi) = 0。
       这个方法不需要计算斜率，完全避免了浮点和 gcd 的问题。

    3. 复杂度较高，O(n³)，但代码简洁，适合 n 较小时。

    【举例】points = [[1,1],[2,2],[3,3]]
      枚举 i=0,j=1（直线过(1,1)和(2,2)）:
        k=2: (2-1)*(3-1) == (3-1)*(2-1) → 2==2 ✓ → 3点共线
      答案 = 3

    【时间复杂度】O(n³)
    【空间复杂度】O(1)
    """
    def maxPoints_brute(self, points: List[List[int]]) -> int:
        n = len(points)
        if n <= 2:
            return n

        ans = 2
        for i in range(n):
            for j in range(i + 1, n):
                count = 2
                for k in range(n):
                    if k == i or k == j:
                        continue
                    cross = ((points[j][0] - points[i][0]) * (points[k][1] - points[i][1])
                             - (points[k][0] - points[i][0]) * (points[j][1] - points[i][1]))
                    if cross == 0:
                        count += 1
                ans = max(ans, count)

        return ans
