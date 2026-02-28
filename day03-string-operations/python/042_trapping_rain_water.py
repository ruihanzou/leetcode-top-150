"""
LeetCode 42. Trapping Rain Water
难度: Hard

题目描述：
给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，
下雨之后能接多少雨水。

示例：height = [0,1,0,2,1,0,1,3,2,1,2,1] → 输出 6

【拓展练习】
1. LeetCode 11. Container With Most Water —— 双指针求最大面积
2. LeetCode 84. Largest Rectangle in Histogram —— 单调栈求最大矩形
3. LeetCode 407. Trapping Rain Water II —— 二维接雨水（BFS + 优先队列）
"""

from typing import List


class Solution:
    """
    ==================== 解法一：前后缀数组 ====================

    【核心思路】
    每个位置 i 能存的水 = min(左边最高, 右边最高) - height[i]。
    预处理两个数组：leftMax[i] 和 rightMax[i]，分别记录 i 左边（含自身）
    和右边（含自身）的最大高度，然后逐位置计算积水量。

    【思考过程】
    1. 对于位置 i，水面高度取决于左右两侧最高柱子中较矮的那个，
       因为水会从较矮的一侧溢出。

    2. leftMax[i] = max(leftMax[i-1], height[i])，从左往右扫一遍即可。
       rightMax[i] = max(rightMax[i+1], height[i])，从右往左扫一遍。

    3. 每个位置的积水 = min(leftMax[i], rightMax[i]) - height[i]，
       只有非负时才有积水。

    【举例】height = [0,1,0,2,1,0,1,3,2,1,2,1]
      leftMax  = [0,1,1,2,2,2,2,3,3,3,3,3]
      rightMax = [3,3,3,3,3,3,3,3,2,2,2,1]
      water[2] = min(1,3)-0 = 1
      water[4] = min(2,3)-1 = 1
      water[5] = min(2,3)-0 = 2
      ...总和 = 6

    【时间复杂度】O(n) 三次遍历
    【空间复杂度】O(n) 两个辅助数组
    """
    def trap_prefix_suffix(self, height: List[int]) -> int:
        n = len(height)
        if n <= 2:
            return 0

        left_max = [0] * n
        right_max = [0] * n

        left_max[0] = height[0]
        for i in range(1, n):
            left_max[i] = max(left_max[i - 1], height[i])

        right_max[n - 1] = height[n - 1]
        for i in range(n - 2, -1, -1):
            right_max[i] = max(right_max[i + 1], height[i])

        water = 0
        for i in range(n):
            water += min(left_max[i], right_max[i]) - height[i]
        return water

    """
    ==================== 解法二：双指针 ====================

    【核心思路】
    用左右两个指针从两端向中间移动，同时维护 leftMax 和 rightMax。
    哪边的 max 更小，就处理哪边——因为较小的那边是瓶颈，能确定该位置的积水。

    【思考过程】
    1. 解法一需要 O(n) 空间存前后缀数组，能否优化到 O(1)？
       → 关键观察：计算位置 i 的积水只需要 min(leftMax, rightMax)。

    2. 如果 leftMax < rightMax，那 min 就是 leftMax，
       无论右边的真实最大值是多少（它至少 >= rightMax > leftMax），
       所以 left 位置的积水确定为 leftMax - height[left]。

    3. 反之 rightMax <= leftMax 时，right 位置的积水确定。
       每次移动较小 max 对应的指针，直到 left 和 right 相遇。

    【举例】height = [0,1,0,2,1,0,1,3,2,1,2,1]
      left=0, right=11, lMax=0, rMax=1
      lMax(0) <= rMax(1): water += 0-0=0, left=1
      lMax=1, rMax=1: lMax<=rMax, water += 1-1=0, left=2
      lMax=1, rMax=1: lMax<=rMax, water += 1-0=1, left=3
      lMax=2, rMax=1: lMax>rMax, water += 1-1=0, right=10
      ...最终 water=6

    【时间复杂度】O(n) 一次遍历
    【空间复杂度】O(1) 只用常量变量
    """
    def trap_two_pointers(self, height: List[int]) -> int:
        left, right = 0, len(height) - 1
        left_max = right_max = 0
        water = 0

        while left < right:
            left_max = max(left_max, height[left])
            right_max = max(right_max, height[right])

            if left_max <= right_max:
                water += left_max - height[left]
                left += 1
            else:
                water += right_max - height[right]
                right -= 1

        return water

    """
    ==================== 解法三：单调栈 ====================

    【核心思路】
    维护一个单调递减栈（栈中存下标），当遇到比栈顶更高的柱子时，
    说明栈顶柱子被左右两侧夹住，可以计算"横向"积水。

    【思考过程】
    1. 前两种解法是"竖向"思考：每个位置能存多少水。
       单调栈是"横向"思考：找到一个凹槽后，一层一层地算这个凹槽的积水面积。

    2. 遍历到 height[i] 时，如果 height[i] > height[stack[-1]]，
       说明栈顶是凹槽的底部。弹出栈顶作为 bottom，
       新的栈顶就是凹槽的左边界，i 是右边界。

    3. 积水宽度 = i - left - 1
       积水高度 = min(height[left], height[i]) - height[bottom]
       面积 = 宽 × 高，累加即可。

    【举例】height = [0,1,0,2,1,0,1,3,2,1,2,1]
      i=3(h=2): 弹出 i=2(h=0), left=1(h=1)
        → 宽=3-1-1=1, 高=min(1,2)-0=1, 面积=1
      i=3(h=2): 弹出 i=1(h=1), left=0(h=0)
        → 宽=3-0-1=2, 高=min(0,2)-1=0, 不贡献（被削平了）
      ...总和 = 6

    【时间复杂度】O(n) 每个元素最多入栈出栈一次
    【空间复杂度】O(n) 栈空间
    """
    def trap_monotonic_stack(self, height: List[int]) -> int:
        stack = []
        water = 0

        for i, h in enumerate(height):
            while stack and h > height[stack[-1]]:
                bottom = stack.pop()
                if not stack:
                    break
                left = stack[-1]
                width = i - left - 1
                bounded_height = min(height[left], h) - height[bottom]
                water += width * bounded_height
            stack.append(i)

        return water
