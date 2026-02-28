"""
LeetCode 219. Contains Duplicate II
难度: Easy

题目描述：
给你一个整数数组 nums 和一个整数 k，判断数组中是否存在两个不同的索引 i 和 j，
满足 nums[i] == nums[j] 且 abs(i - j) <= k。如果存在，返回 true；否则，返回 false。

示例：
  输入: nums = [1,2,3,1], k = 3
  输出: true
  解释: nums[0] == nums[3]，且 abs(0-3) = 3 <= 3

  输入: nums = [1,0,1,1], k = 1
  输出: true
  解释: nums[2] == nums[3]，且 abs(2-3) = 1 <= 1

  输入: nums = [1,2,3,1,2,3], k = 2
  输出: false
  解释: 每对相等元素的下标差都大于 2

【拓展练习】
1. LeetCode 217. Contains Duplicate —— 判断数组是否有重复元素（无距离限制）
2. LeetCode 220. Contains Duplicate III —— 值差 <= t 且下标差 <= k，桶排序/有序集合
3. LeetCode 480. Sliding Window Median —— 滑动窗口中位数，有序集合维护窗口
"""

from typing import List


class Solution:
    """
    ==================== 解法一：哈希表记录最近索引 ====================

    【核心思路】
    用哈希表存储每个值最近一次出现的下标。遍历数组时，如果当前值已在表中，
    检查当前下标与记录的下标之差是否 <= k；然后更新为当前下标。

    【思考过程】
    1. 对于每个 nums[i]，我们需要知道"与它相等的最近的前一个元素在哪里"。
       如果距离 <= k，直接返回 true。
    2. 为什么只需记录"最近"的下标？因为如果更早的下标距离 > k，
       那只有更近的下标才有可能满足 <= k。
    3. 每次遇到相同值时更新下标，保证表中始终是最近的。

    【举例】nums = [1,2,3,1], k = 3
      i=0: num=1, 表={} 中无1 → 记录 {1:0}
      i=1: num=2, 表中无2 → 记录 {1:0, 2:1}
      i=2: num=3, 表中无3 → 记录 {1:0, 2:1, 3:2}
      i=3: num=1, 表中有1，下标0，|3-0|=3 <= 3 → 返回 True

    【时间复杂度】O(n) 一次遍历
    【空间复杂度】O(n) 哈希表最多存 n 个元素
    """
    def containsNearbyDuplicate_map(self, nums: List[int], k: int) -> bool:
        last_index = {}
        for i, num in enumerate(nums):
            if num in last_index and i - last_index[num] <= k:
                return True
            last_index[num] = i
        return False

    """
    ==================== 解法二：滑动窗口哈希集合 ====================

    【核心思路】
    维护一个大小最多为 k 的哈希集合，代表当前窗口 [i-k, i-1] 中的元素。
    遍历时检查 nums[i] 是否已在集合中（说明窗口内有重复），
    然后将 nums[i] 加入集合，并移除窗口左边界之外的元素。

    【思考过程】
    1. 解法一用了 O(n) 空间。如果 k 远小于 n，能否只用 O(k) 空间？
    2. 关键观察：我们只关心距离 <= k 的元素对。
       所以只需要维护一个大小为 k 的"滑动窗口"。
    3. 窗口用 set 表示：
       - 新元素进入窗口前，先检查是否已在 set 中（重复）。
       - 然后加入 set。
       - 如果 set 大小超过 k，移除最左边的元素 nums[i-k]。

    【举例】nums = [1,2,3,1], k = 3
      i=0: window={}, 1不在 → window={1}
      i=1: window={1}, 2不在 → window={1,2}
      i=2: window={1,2}, 3不在 → window={1,2,3}
      i=3: window={1,2,3}, 1在! → 返回 True

    【时间复杂度】O(n) 一次遍历，每次操作 O(1)
    【空间复杂度】O(k) 集合最多 k 个元素（优于解法一的 O(n)）
    """
    def containsNearbyDuplicate(self, nums: List[int], k: int) -> bool:
        window = set()
        for i, num in enumerate(nums):
            if num in window:
                return True
            window.add(num)
            if len(window) > k:
                window.remove(nums[i - k])
        return False
