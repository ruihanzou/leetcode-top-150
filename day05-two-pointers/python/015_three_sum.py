"""
LeetCode 15. 3Sum
难度: Medium

题目描述：
给你一个整数数组 nums，判断是否存在三元组 [nums[i], nums[j], nums[k]]
满足 i != j、i != k 且 j != k，同时还满足 nums[i] + nums[j] + nums[k] == 0。
请你返回所有和为 0 且不重复的三元组。
注意：答案中不可以包含重复的三元组。

示例 1：nums = [-1,0,1,2,-1,-4] → 输出 [[-1,-1,2],[-1,0,1]]
  解释：
    nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0
    nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0
    nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0
    不重复的三元组是 [-1,0,1] 和 [-1,-1,2]。
示例 2：nums = [0,1,1] → 输出 []
示例 3：nums = [0,0,0] → 输出 [[0,0,0]]

【拓展练习】
1. LeetCode 1. Two Sum —— 两数之和，本题的基础
2. LeetCode 16. 3Sum Closest —— 找最接近目标值的三数之和
3. LeetCode 18. 4Sum —— 四数之和，思路类似但多一层循环
"""

from typing import List


class Solution:
    """
    ==================== 解法一：排序 + 双指针 ====================

    【核心思路】
    先排序数组。固定第一个数 nums[i]，在其右侧用对撞双指针寻找
    满足 nums[left] + nums[right] == -nums[i] 的两个数。
    通过跳过重复元素来避免重复三元组。

    【思考过程】
    1. 三数之和 = 0 → 固定一个数 a，问题变为在剩余部分找两数之和 = -a。
    2. 排序后，两数之和问题可以用对撞双指针 O(n) 解决。
    3. 去重策略：
       - 外层：如果 nums[i] == nums[i-1]，跳过（相同的 a 不重复处理）。
       - 内层：找到一组解后，left 和 right 都跳过重复值。
    4. 剪枝：如果 nums[i] > 0，三个正数不可能和为 0，提前终止。

    【举例】nums = [-1,0,1,2,-1,-4]
      排序 → [-4,-1,-1,0,1,2]
      i=0, nums[i]=-4, target=4: left=1,right=5 → 最大和=-1+2=1<4 → 无解
      i=1, nums[i]=-1, target=1:
        left=2(-1),right=5(2): -1+2=1==1 ✓ → 添加 [-1,-1,2]
        left 跳过重复 → left=3, right=4
        left=3(0),right=4(1): 0+1=1==1 ✓ → 添加 [-1,0,1]
      i=2, nums[i]=-1, 与 i=1 相同，跳过
      i=3, nums[i]=0, target=0: left=4(1),right=5(2): 1+2=3>0 → 无解
      结果：[[-1,-1,2],[-1,0,1]]

    【时间复杂度】O(n²)，排序 O(n log n) + 双层遍历 O(n²)
    【空间复杂度】O(log n)，排序使用的栈空间（不计输出）
    """
    def threeSum_two_pointer(self, nums: List[int]) -> List[List[int]]:
        nums.sort()
        result = []
        n = len(nums)

        for i in range(n - 2):
            if nums[i] > 0:
                break
            if i > 0 and nums[i] == nums[i - 1]:
                continue

            left, right = i + 1, n - 1
            target = -nums[i]

            while left < right:
                s = nums[left] + nums[right]
                if s == target:
                    result.append([nums[i], nums[left], nums[right]])
                    while left < right and nums[left] == nums[left + 1]:
                        left += 1
                    while left < right and nums[right] == nums[right - 1]:
                        right -= 1
                    left += 1
                    right -= 1
                elif s < target:
                    left += 1
                else:
                    right -= 1

        return result

    """
    ==================== 解法二：哈希表法 ====================

    【核心思路】
    固定前两个数 nums[i] 和 nums[j]，用哈希集合查找第三个数
    -(nums[i] + nums[j]) 是否存在于已遍历的元素中。
    通过排序和跳过重复来去重。

    【思考过程】
    1. 两数之和用哈希表是经典做法，三数之和可以类似地扩展。
    2. 固定 nums[i]，问题变为在 i+1 到 n-1 中找两个数之和 = -nums[i]。
    3. 对内层使用哈希集合：遍历 j，查找 -nums[i]-nums[j] 是否已在集合中。
    4. 去重：先排序，然后外层跳过重复的 nums[i]；内层找到解后跳过重复的 nums[j]。

    【举例】nums = [-1,0,1,2,-1,-4]
      排序 → [-4,-1,-1,0,1,2]
      i=0, nums[i]=-4:
        j=1: need=4-(-1)=5, seen={} → 没有。seen={-1}
        ... 无解
      i=1, nums[i]=-1:
        j=2: need=1-(-1)=2, seen={} → 没有。seen={-1}
        j=3: need=1-0=1, seen={-1} → 没有。seen={-1,0}
        j=4: need=1-1=0, seen={-1,0} → 有0！添加 [-1,0,1]。seen={-1,0,1}
        j=5: need=1-2=-1, seen={-1,0,1} → 有-1！添加 [-1,-1,2]。
      i=2, 与 i=1 相同，跳过
      结果：[[-1,-1,2],[-1,0,1]]

    【时间复杂度】O(n²)，双层遍历
    【空间复杂度】O(n)，哈希集合存储
    """
    def threeSum_hash_set(self, nums: List[int]) -> List[List[int]]:
        nums.sort()
        result = []
        n = len(nums)

        for i in range(n - 2):
            if nums[i] > 0:
                break
            if i > 0 and nums[i] == nums[i - 1]:
                continue

            seen = set()
            j = i + 1
            while j < n:
                complement = -nums[i] - nums[j]
                if complement in seen:
                    result.append([nums[i], complement, nums[j]])
                    while j + 1 < n and nums[j] == nums[j + 1]:
                        j += 1
                else:
                    seen.add(nums[j])
                j += 1

        return result
