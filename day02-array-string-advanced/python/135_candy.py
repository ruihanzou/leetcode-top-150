"""
LeetCode 135. Candy
难度: Hard

题目描述：
n 个孩子站成一排，每个孩子有一个评分 ratings[i]。
你需要按照以下规则给这些孩子分糖果：
  - 每个孩子至少分到 1 颗糖果
  - 相邻的孩子中，评分更高的孩子必须比他的邻居获得更多的糖果
求最少需要准备多少颗糖果。

示例 1：
  输入：ratings = [1,0,2]
  输出：5
  解释：分配方案 [2,1,2]，共 5 颗

示例 2：
  输入：ratings = [1,2,2]
  输出：4
  解释：分配方案 [1,2,1]，共 4 颗（第三个孩子只需 1 颗，因为只需大于评分更低的邻居）

【拓展练习】
1. 如果要求评分相同的相邻孩子也必须获得相同数量的糖果，如何修改？
2. 如果孩子围成一圈而不是站成一排，如何处理？
"""

from typing import List


class Solution:
    """
    ==================== 解法一：两遍遍历贪心 ====================

    【核心思路】
    分两次遍历：左→右保证右边评分高的比左邻多，右→左保证左边评分高的比右邻多，
    取两次中较大值即可同时满足两个方向的约束。

    【思考过程】
    1. 初始化 candy 数组，每人 1 颗
    2. 从左到右遍历：如果 ratings[i] > ratings[i-1]，则 candy[i] = candy[i-1] + 1
       这保证了"右边评分高于左边"的约束
    3. 从右到左遍历：如果 ratings[i] > ratings[i+1]，则 candy[i] = max(candy[i], candy[i+1] + 1)
       用 max 是因为不能破坏第一遍已经满足的约束
    4. 求和即为答案

    【举例】ratings = [1, 3, 5, 3, 2, 1]
    初始化:        [1, 1, 1, 1, 1, 1]
    左→右:  1<3 → [1, 2, 1, 1, 1, 1]
            3<5 → [1, 2, 3, 1, 1, 1]
    右→左:  2>1 → candy[4]=max(1,2)=2 → [1, 2, 3, 1, 2, 1]
            3>2 → candy[3]=max(1,3)=3 → [1, 2, 3, 3, 2, 1]
            5>3 → candy[2]=max(3,4)=4 → [1, 2, 4, 3, 2, 1]
    结果: [1, 2, 4, 3, 2, 1] → 总和 = 13

    【时间复杂度】O(n) — 两次遍历
    【空间复杂度】O(n) — candy 数组
    """
    def candy_two_pass(self, ratings: List[int]) -> int:
        n = len(ratings)
        candy = [1] * n

        for i in range(1, n):
            if ratings[i] > ratings[i - 1]:
                candy[i] = candy[i - 1] + 1

        for i in range(n - 2, -1, -1):
            if ratings[i] > ratings[i + 1]:
                candy[i] = max(candy[i], candy[i + 1] + 1)

        return sum(candy)

    """
    ==================== 解法二：一遍遍历（上升下降序列法）====================

    【核心思路】
    一次遍历中维护上升序列长度 up、下降序列长度 down 和峰值 peak，
    根据当前趋势实时累加糖果数。

    【思考过程】
    1. 遍历相邻元素对，判断当前是上升、下降还是平坦
    2. 上升时：down 重置，up++，当前孩子分 up+1 颗（峰值 = up+1）
    3. 下降时：up 重置，down++，当前孩子分 down 颗；
       如果下降长度追上了峰值（down >= peak），峰值也需要 +1（给峰顶补一颗）
    4. 平坦时：up、down 都重置，当前孩子分 1 颗

    【举例】ratings = [1, 3, 5, 3, 2, 1]
    i=0: 起始 total=1
    i=1: 1<3 上升, up=1, peak=2, total += 2 → total=3
    i=2: 3<5 上升, up=2, peak=3, total += 3 → total=6
    i=3: 5>3 下降, down=1, 1<peak(3)不补, total += 1 → total=7
    i=4: 3>2 下降, down=2, 2<peak(3)不补, total += 2 → total=9
    i=5: 2>1 下降, down=3, 3>=peak(3)需补1, total += 3+1 → total=13

    【时间复杂度】O(n) — 一次遍历
    【空间复杂度】O(1) — 只用常数变量
    """
    def candy_one_pass(self, ratings: List[int]) -> int:
        n = len(ratings)
        if n <= 1:
            return n

        total = 1
        up, down, peak = 0, 0, 1

        for i in range(1, n):
            if ratings[i] > ratings[i - 1]:
                up += 1
                down = 0
                peak = up + 1
                total += peak
            elif ratings[i] < ratings[i - 1]:
                up = 0
                down += 1
                total += down
                if down >= peak:
                    total += 1
            else:
                up = 0
                down = 0
                peak = 1
                total += 1

        return total
