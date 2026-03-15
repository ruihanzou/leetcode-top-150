"""
LeetCode 57. Insert Interval
难度: Medium

题目描述：
给你一个无重叠的、按照区间起始端点排序的区间列表 intervals，
其中 intervals[i] = [starti, endi] 表示第 i 个区间的开始和结束。
同样给定一个区间 newInterval = [start, end] 表示另一个区间的开始和结束。
在 intervals 中插入区间 newInterval，使得 intervals 仍然按照起始端点排序，
且区间之间互不重叠（如果有必要的话，可以合并区间）。
返回插入之后的 intervals。

示例 1：intervals = [[1,3],[6,9]], newInterval = [2,5]
  → 输出 [[1,5],[6,9]]

示例 2：intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
  → 输出 [[1,2],[3,10],[12,16]]
  解释：新区间 [4,8] 与 [3,5],[6,7],[8,10] 重叠，合并为 [3,10]

【拓展练习】
1. LeetCode 56. Merge Intervals —— 合并所有重叠区间
2. LeetCode 715. Range Module —— 用线段维护区间的添加/删除/查询
3. LeetCode 986. Interval List Intersections —— 两个有序区间列表求交集
"""

from typing import List
import bisect


class Solution:
    """
    ==================== 解法一：线性扫描三阶段 ====================

    【核心思路】
    将原区间列表分成三个部分处理：
    ① 完全在 newInterval 左侧的区间（不重叠）→ 直接加入结果
    ② 与 newInterval 重叠的区间 → 合并到 newInterval 中
    ③ 完全在 newInterval 右侧的区间（不重叠）→ 直接加入结果
    合并后的 newInterval 在阶段②结束后加入结果。

    【思考过程】
    1. 原列表已按左端点排序且互不重叠。
       → 所有在 newInterval 左侧的区间一定连续排列在前面。
       → 所有在右侧的区间也一定连续排列在后面。
       → 中间那些与 newInterval 重叠的也是连续的。

    2. "完全在左侧"的判定：interval[1] < newInterval[0]
       （原区间的右端点 < 新区间的左端点 → 不可能重叠）

    3. "重叠"的判定：interval[0] <= newInterval[1]
       （原区间的左端点 <= 新区间的右端点 → 还有交集）
       合并方式：newInterval = [min(左), max(右)]

    4. 剩余的就是"完全在右侧"的区间。

    【举例】intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
      阶段①：[1,2] 的右端点 2 < 4 → 加入结果。result = [[1,2]]
      阶段②：[3,5] 的左端点 3 <= 8 → 合并: new = [min(4,3),max(8,5)] = [3,8]
             [6,7] 的左端点 6 <= 8 → 合并: new = [min(3,6),max(8,7)] = [3,8]
             [8,10] 的左端点 8 <= 8 → 合并: new = [min(3,8),max(8,10)] = [3,10]
             [12,16] 的左端点 12 > 10 → 停。加入 new=[3,10]
             result = [[1,2],[3,10]]
      阶段③：[12,16] → 加入结果。result = [[1,2],[3,10],[12,16]]

    【时间复杂度】O(n)，一次遍历
    【空间复杂度】O(n)，存储结果
    """
    def insert_linear(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        result = []
        i, n = 0, len(intervals)
        
        while i < n and intervals[i][1] < newInterval[0]:
            result.append(intervals[i])
            i += 1

        while i < n and intervals[i][0] <= newInterval[1]:
            newInterval[0] = min(newInterval[0], intervals[i][0])
            newInterval[1] = max(newInterval[1], intervals[i][1])
            i += 1
        result.append(newInterval)

        while i < n:
            # 右侧的区间直接加入结果
            result.append(intervals[i])
            i += 1

        return result

    """
    ==================== 解法二：二分查找 + 合并 ====================

    【核心思路】
    用二分查找快速定位 newInterval 影响的区间范围，
    找到第一个可能重叠的区间和最后一个可能重叠的区间，然后合并。

    【思考过程】
    1. 线性扫描的阶段①和③本质是在找两个边界：
       - left：第一个右端点 >= newInterval[0] 的区间索引
       - right：最后一个左端点 <= newInterval[1] 的区间索引
       这两个搜索可以用二分加速。

    2. 用 bisect_left 在右端点序列中找 newInterval[0] → left
       用 bisect_right 在左端点序列中找 newInterval[1] → right（取 right-1）

    3. [left, right] 范围内的区间全部与 newInterval 重叠，合并它们。
       合并结果的左端点 = min(intervals[left][0], newInterval[0])
       合并结果的右端点 = max(intervals[right][1], newInterval[1])

    4. 最终结果 = intervals[:left] + [合并区间] + intervals[right+1:]

    【举例】intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
      右端点序列 [2,5,7,10,16]，bisect_left(4) = 1 → left=1
      左端点序列 [1,3,6,8,12]，bisect_right(8) = 4，right=4-1=3
      合并 intervals[1..3] = [3,5],[6,7],[8,10] 与 [4,8]：
        左 = min(3,4) = 3，右 = max(10,8) = 10 → [3,10]
      结果：[[1,2]] + [[3,10]] + [[12,16]] = [[1,2],[3,10],[12,16]]

    【时间复杂度】O(n)，虽然二分查找是 O(log n)，但拼接结果需要 O(n)
    【空间复杂度】O(n)，存储结果
    """
    def insert_binary_search(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        n = len(intervals)
        if n == 0:
            return [newInterval]

        ends = [iv[1] for iv in intervals]
        starts = [iv[0] for iv in intervals]

        left = bisect.bisect_left(ends, newInterval[0])
        # 找到第一个大于 newInterval[1] 的索引，如果找不到，则返回 len(starts)
        right = bisect.bisect_right(starts, newInterval[1]) - 1

        #
        if left > right:
            return intervals[:left] + [newInterval] + intervals[left:]

        merged_start = min(intervals[left][0], newInterval[0])
        merged_end = max(intervals[right][1], newInterval[1])

        return intervals[:left] + [[merged_start, merged_end]] + intervals[right + 1:]
