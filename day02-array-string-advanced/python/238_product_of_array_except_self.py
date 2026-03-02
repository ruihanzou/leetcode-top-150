"""
LeetCode 238. Product of Array Except Self
难度: Medium

题目描述：
给定一个整数数组 nums，返回数组 answer，其中 answer[i] 等于 nums 中
除 nums[i] 之外其余所有元素的乘积。
题目保证任意前缀或后缀的乘积都在 32 位整数范围内。
要求：不能使用除法，且时间复杂度为 O(n)。

示例 1：
  输入：nums = [1,2,3,4]
  输出：[24,12,8,6]

示例 2：
  输入：nums = [-1,1,0,-3,3]
  输出：[0,0,9,0,0]

【拓展练习】
1. 如果允许使用除法，能否用 O(1) 额外空间解决？需要注意什么边界情况？
2. 如果要求结果对某个质数取模，如何处理？
"""

from typing import List


class Solution:
    """
    ==================== 解法一：两个数组（左积 + 右积）====================

    【核心思路】
    分别构建前缀积数组和后缀积数组，answer[i] = left[i] * right[i]。

    【思考过程】
    1. left[i] 表示 nums[0..i-1] 的乘积（i 左边所有元素之积）
    2. right[i] 表示 nums[i+1..n-1] 的乘积（i 右边所有元素之积）
    3. answer[i] = left[i] * right[i] 就是除了 nums[i] 之外的乘积
    4. left 从左往右累乘构建，right 从右往左累乘构建

    【举例】nums = [1, 2, 3, 4]
    left:  [1, 1, 2, 6]     （left[0]=1, left[1]=1, left[2]=1*2=2, left[3]=1*2*3=6）
    right: [24, 12, 4, 1]   （right[3]=1, right[2]=4, right[1]=3*4=12, right[0]=2*3*4=24）
    answer: [1*24, 1*12, 2*4, 6*1] = [24, 12, 8, 6]

    【时间复杂度】O(n) — 三次遍历
    【空间复杂度】O(n) — left 和 right 数组
    """
    def productExceptSelf_two_arrays(self, nums: List[int]) -> List[int]:
        n = len(nums)
        left = [1] * n
        right = [1] * n

        for i in range(1, n):
            left[i] = left[i - 1] * nums[i - 1]

        for i in range(n - 2, -1, -1):
            right[i] = right[i + 1] * nums[i + 1]

        return [left[i] * right[i] for i in range(n)]

    """
    ==================== 解法二：单数组优化（先存左积，再乘右积）====================

    【核心思路】
    用 answer 数组先存左积，再从右往左用一个变量累乘右积，原地完成计算。
    （题目说明输出数组不算额外空间）

    【思考过程】
    1. 第一遍：从左到右，answer[i] = 左边所有元素之积
    2. 第二遍：从右到左，维护变量 right_product 表示右边累积
       answer[i] *= right_product，然后 right_product *= nums[i]
    3. 这样省去了单独的 right 数组

    【举例】nums = [1, 2, 3, 4]
    第一遍（左积）: answer = [1, 1, 2, 6]
    第二遍（乘上右积，right_product 初始为 1）:
      i=3: answer[3] = 6*1 = 6,  right_product = 1*4 = 4
      i=2: answer[2] = 2*4 = 8,  right_product = 4*3 = 12
      i=1: answer[1] = 1*12 = 12, right_product = 12*2 = 24
      i=0: answer[0] = 1*24 = 24, right_product = 24*1 = 24
    结果: [24, 12, 8, 6]

    【时间复杂度】O(n) — 两次遍历
    【空间复杂度】O(1) — 不算输出数组，只用了一个额外变量
    """
    def productExceptSelf_optimized(self, nums: List[int]) -> List[int]:
        n = len(nums)
        # 初始化答案数组, 因为 answer[i] 是 i 左边的所有元素之积, 所以是 1
        answer = [1] * n

        # range(1, n) 表示从 1 到 n-1, 因为 i 是当前元素, 所以是 i - 1
        for i in range(1, n):
            # 计算左积 注意这里是 i - 1 因为 i 是当前元素, 因为answer[i] 是 i 左边的所有元素之积, 所以是 i - 1
            answer[i] = answer[i - 1] * nums[i - 1] 

        right_product = 1 # 初始化右积
        # 从右往左遍历, 计算右积
        for i in range(n - 1, -1, -1):
            answer[i] *= right_product # 计算右积
            right_product *= nums[i] # 更新右积方便下次计算

        return answer

    """
    ==================== 解法三：除法法（如果允许除法）====================

    【核心思路】
    先算全部元素之积 total_product，answer[i] = total_product / nums[i]。
    需要特殊处理含 0 的情况。

    【思考过程】
    1. 统计数组中 0 的个数 zero_count
    2. 如果 zero_count > 1：所有位置结果都是 0
    3. 如果 zero_count == 1：只有 0 所在位置的结果 = 其他非零元素之积，其余为 0
    4. 如果 zero_count == 0：answer[i] = total_product / nums[i]

    【举例】nums = [-1, 1, 0, -3, 3]
    zero_count = 1, 非零元素之积 = (-1)*1*(-3)*3 = 9
    0 在 index=2, 所以 answer = [0, 0, 9, 0, 0]

    【时间复杂度】O(n) — 两次遍历
    【空间复杂度】O(1) — 不算输出数组
    """
    def productExceptSelf_division(self, nums: List[int]) -> List[int]:
        n = len(nums)
        total_product = 1
        zero_count = 0

        for num in nums:
            if num == 0:
                zero_count += 1
            else:
                total_product *= num

        if zero_count > 1:
            return [0] * n

        answer = [0] * n
        for i in range(n):
            if zero_count == 1:
                answer[i] = total_product if nums[i] == 0 else 0
            else:
                answer[i] = total_product // nums[i]

        return answer
  