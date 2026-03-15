"""
LeetCode 56. Merge Intervals
难度: Medium

题目描述：
以数组 intervals 表示若干个区间的集合，其中单个区间为 intervals[i] = [starti, endi]。
请合并所有重叠的区间，并返回一个不重叠的区间数组，该数组需恰好覆盖输入中的所有区间。

示例 1：intervals = [[1,3],[2,6],[8,10],[15,18]] → 输出 [[1,6],[8,10],[15,18]]
  解释：区间 [1,3] 和 [2,6] 重叠，合并为 [1,6]

示例 2：intervals = [[1,4],[4,5]] → 输出 [[1,5]]
  解释：区间 [1,4] 和 [4,5] 可被视为重叠区间

【拓展练习】
1. LeetCode 57. Insert Interval —— 在有序不重叠区间中插入新区间并合并
2. LeetCode 986. Interval List Intersections —— 求两个区间列表的交集
3. LeetCode 252. Meeting Rooms —— 判断会议是否有时间冲突
"""

from typing import List
from collections import defaultdict, deque


class Solution:
    """
    ==================== 解法一：排序后合并 ====================

    【核心思路】
    按区间左端点排序后，依次遍历。如果当前区间与结果列表的最后一个区间重叠
    （即当前区间的左端点 <= 上一个区间的右端点），则合并（取右端点的较大值）；
    否则直接加入结果列表。

    【思考过程】
    1. 如果区间无序，任何两个区间都可能重叠，判断复杂度是 O(n²)。
       → 先按左端点排序，这样只需要和"前一个区间"比较。

    2. 排序后，对于相邻两个区间 A=[a1,a2] 和 B=[b1,b2]（a1 <= b1）：
       - 若 b1 <= a2，说明 B 的起点落在 A 内部或恰好接上 → 重叠，合并为 [a1, max(a2,b2)]
       - 若 b1 > a2，说明完全不重叠 → B 是新的独立区间

    3. 注意合并后的区间可能继续与下一个区间重叠，
       但由于我们总是用"结果列表最后一个区间"来比较，天然处理了链式合并。

    【举例】intervals = [[1,3],[2,6],[8,10],[15,18]]
      排序后（本例已有序）→ [[1,3],[2,6],[8,10],[15,18]]
      merged = [[1,3]]
      [2,6]: 2 <= 3 → 重叠，合并为 [1,max(3,6)]=[1,6]，merged = [[1,6]]
      [8,10]: 8 > 6 → 不重叠，merged = [[1,6],[8,10]]
      [15,18]: 15 > 10 → 不重叠，merged = [[1,6],[8,10],[15,18]]

    【时间复杂度】O(n log n)，排序主导
    【空间复杂度】O(n)，存储结果（排序本身 O(n)）
    """
    def merge_sorted(self, intervals: List[List[int]]) -> List[List[int]]:
        intervals.sort(key=lambda x: x[0])
        merged = []

        for interval in intervals:
            # if not merged 表示 merged 为空
            if not merged or merged[-1][1] < interval[0]:
                merged.append(interval)
            else:
                merged[-1][1] = max(merged[-1][1], interval[1])

        return merged

    """
    ==================== 解法二：连通分量（图+BFS） ====================

    【核心思路】
    将每个区间看作图中的一个节点。如果两个区间重叠，就在它们之间建一条边。
    然后用 BFS 找出所有连通分量，每个连通分量中所有区间合并为一个。

    【思考过程】
    1. 两个区间重叠的判定：A=[a1,a2], B=[b1,b2]
       重叠 ⟺ a1 <= b2 且 b1 <= a2

    2. 枚举所有区间对，重叠就连边 → 建邻接表。
       然后 BFS 遍历每个连通分量，对分量内所有区间取 min(左端点)、max(右端点)。

    3. 这种方法时间 O(n²)，不如排序优。但它展示了区间合并的本质：
       "合并"等价于"连通分量"。适合理解但不推荐用于提交。

    【举例】intervals = [[1,3],[2,6],[8,10],[15,18]]
      节点 0=[1,3], 1=[2,6], 2=[8,10], 3=[15,18]
      边：0-1（[1,3]与[2,6]重叠）
      连通分量：{0,1} → [min(1,2),max(3,6)]=[1,6]
               {2} → [8,10]
               {3} → [15,18]
      结果：[[1,6],[8,10],[15,18]]

    【时间复杂度】O(n²)，枚举所有区间对
    【空间复杂度】O(n²)，邻接表最坏情况
    """
    def merge_graph(self, intervals: List[List[int]]) -> List[List[int]]:
        n = len(intervals)
        if n == 0:
            return []

        graph = defaultdict(list)
        for i in range(n):
            for j in range(i + 1, n):
                if intervals[i][0] <= intervals[j][1] and intervals[j][0] <= intervals[i][1]:
                    graph[i].append(j)
                    graph[j].append(i)

        visited = [False] * n
        result = []

        for i in range(n):
            if visited[i]:
                continue

            queue = deque([i])
            visited[i] = True
            lo, hi = intervals[i]

            while queue:
                cur = queue.popleft()
                for neighbor in graph[cur]:
                    if not visited[neighbor]:
                        visited[neighbor] = True
                        lo = min(lo, intervals[neighbor][0])
                        hi = max(hi, intervals[neighbor][1])
                        queue.append(neighbor)

            result.append([lo, hi])

        return result
