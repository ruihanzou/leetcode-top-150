"""
LeetCode 11. Container With Most Water
难度: Medium

题目描述：
给定一个长度为 n 的整数数组 height。有 n 条垂线，第 i 条线的两个端点是
(i, 0) 和 (i, height[i])。
找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。
返回容器可以储存的最大水量。
注意：你不能倾斜容器。

示例 1：height = [1,8,6,2,5,4,8,3,7] → 输出 49
  解释：选择第 2 条线（高度 8）和第 9 条线（高度 7），
  容器宽度为 9-2=7，高度取较短的 7，面积 = 7 × 7 = 49。
示例 2：height = [1,1] → 输出 1

【拓展练习】
1. LeetCode 42. Trapping Rain Water —— 接雨水问题，思路类似但更复杂
2. LeetCode 84. Largest Rectangle in Histogram —— 柱状图中最大矩形
3. LeetCode 407. Trapping Rain Water II —— 3D 接雨水
"""

from typing import List


class Solution:
    """
    ==================== 解法一：暴力枚举 ====================

    【核心思路】
    双重循环枚举所有可能的两条线段组合，计算每对线段构成的容器面积，
    取最大值。

    【思考过程】
    1. 容器面积 = min(height[i], height[j]) × (j - i)。
    2. 最直接的想法：枚举所有 (i, j) 对，计算面积并取最大。
    3. 时间复杂度 O(n²)，对于大数据量会超时，但逻辑最简单直观。
    4. 可以作为验证其他解法正确性的基准方法。

    【举例】height = [1,8,6,2,5,4,8,3,7]
      (0,1): min(1,8)×1 = 1
      (0,8): min(1,7)×8 = 8
      (1,8): min(8,7)×7 = 49   ← 最大
      (1,6): min(8,8)×5 = 40
      ... 枚举所有组合，最终答案 49

    【时间复杂度】O(n²)，双重循环枚举所有配对
    【空间复杂度】O(1)，仅用常量变量
    """
    def maxArea_brute_force(self, height: List[int]) -> int:
        max_area = 0
        n = len(height)
        for i in range(n - 1):
            for j in range(i + 1, n):
                area = min(height[i], height[j]) * (j - i)
                max_area = max(max_area, area)
        return max_area

    """
    ==================== 解法二：对撞双指针 ====================

    【核心思路】
    左右双指针从两端向中间收缩。每次计算当前面积后，
    移动高度较小的那一侧的指针（因为移动较高的一侧不可能得到更大面积）。

    【思考过程】
    1. 面积 = min(height[left], height[right]) × (right - left)。
    2. 为什么移动较短边？
       - 宽度每次减少 1（因为指针向内移动）。
       - 如果移动较长边，高度只可能不变或减小（受限于较短边），面积必然减小。
       - 如果移动较短边，高度有可能变大，从而有机会获得更大面积。
    3. 所以每次移动较短边是贪心最优策略，不会错过最优解。

    【举例】height = [1,8,6,2,5,4,8,3,7]
      left=0(1), right=8(7): area=1×8=8, 移动 left（1<7）
      left=1(8), right=8(7): area=7×7=49, 移动 right（7<8）
      left=1(8), right=7(3): area=3×6=18, 移动 right（3<8）
      left=1(8), right=6(8): area=8×5=40, 移动 left（相等时移动哪边都可以）
      ... 最终 max_area = 49

    【时间复杂度】O(n)，每个元素最多被访问一次
    【空间复杂度】O(1)，仅用两个指针和一个变量
    """
    def maxArea_two_pointer(self, height: List[int]) -> int:
        left, right = 0, len(height) - 1
        max_area = 0
        while left < right:
            area = min(height[left], height[right]) * (right - left)
            max_area = max(max_area, area)
            if height[left] < height[right]:
                left += 1
            else:
                right -= 1
        return max_area
