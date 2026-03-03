"""
LeetCode 210. Course Schedule II
难度: Medium

题目描述：
现在你总共有 numCourses 门课需要选，记为 0 到 numCourses - 1。
给你一个数组 prerequisites，其中 prerequisites[i] = [ai, bi]，表示在选修课程 ai 前
必须先选修 bi。
请你返回你为了学完所有课程所安排的学习顺序。如果有多种有效的顺序，返回其中任意一种。
如果不可能完成所有课程，返回一个空数组。

示例 1：numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
  → 输出 [0,2,1,3] 或 [0,1,2,3]
示例 2：numCourses = 1, prerequisites = [] → 输出 [0]

【拓展练习】
1. LeetCode 207. Course Schedule —— 仅判断是否存在拓扑排序
2. LeetCode 269. Alien Dictionary —— 从字典序推断字母顺序（拓扑排序）
3. LeetCode 1462. Course Schedule IV —— 判断课程间的前置关系（传递闭包）
"""

from typing import List
from collections import deque


class Solution:
    """
    ==================== 解法一：BFS 拓扑排序（Kahn 算法） ====================

    【核心思路】
    与 207 题相同的 Kahn 算法，但这次需要记录出队顺序作为拓扑排序结果。
    如果最终结果长度等于 numCourses，返回该序列；否则返回空数组。

    【思考过程】
    1. 207 题中我们用 Kahn 算法判断是否有环，这里只需额外记录出队顺序。
       BFS 中每个节点出队的顺序就是一个合法的拓扑排序。

    2. 入度为 0 → 没有未完成的前置课程 → 可以学习 → 入队。
       学完后减少后续课程的入度，可能产生新的入度为 0 的课程。

    3. 如果最终 order 长度 < numCourses，说明有环，无法完成所有课程。

    【举例】numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]]
      图：0→1, 0→2, 1→3, 2→3
      入度：0:0, 1:1, 2:1, 3:2

      队列 [0], order=[]
      取出0 → order=[0], 1入度→0, 2入度→0
      队列 [1,2]
      取出1 → order=[0,1], 3入度→1
      取出2 → order=[0,1,2], 3入度→0
      队列 [3]
      取出3 → order=[0,1,2,3]
      长度==4 → 返回 [0,1,2,3]

    【时间复杂度】O(V + E)
    【空间复杂度】O(V + E)
    """
    def findOrder_bfs(self, numCourses: int, prerequisites: List[List[int]]) -> List[int]:
        in_degree = [0] * numCourses
        graph = [[] for _ in range(numCourses)]

        for course, prereq in prerequisites:
            graph[prereq].append(course)
            in_degree[course] += 1

        queue = deque()
        for i in range(numCourses):
            if in_degree[i] == 0:
                queue.append(i)

        order = []
        while queue:
            node = queue.popleft()
            order.append(node)
            for neighbor in graph[node]:
                in_degree[neighbor] -= 1
                if in_degree[neighbor] == 0:
                    queue.append(neighbor)

        return order if len(order) == numCourses else []

    """
    ==================== 解法二：DFS 后序 + 反转 ====================

    【核心思路】
    对有向图做 DFS，在节点的所有邻居都处理完后将该节点加入结果列表（后序）。
    最终将结果反转即为拓扑排序。同时用三色标记法检测环。

    【思考过程】
    1. DFS 后序遍历的性质：一个节点在其所有后继节点之后加入列表。
       反转后，每个节点出现在其所有后继之前 → 正好是拓扑排序！

    2. 为什么后序的反转是拓扑序？
       - 在 DFS 中，节点 u 的邻居 v 会先于 u 完成后序记录
       - 反转后，u 出现在 v 之前，符合 u→v 的依赖关系

    3. 需要三色标记检测环（同 207 题）：
       - 遇到灰色节点 → 有环 → 返回空数组

    【举例】numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]]
      DFS(0) → DFS(1) → DFS(3) → 后序加入3 → 后序加入1
             → DFS(2) → 3已黑色跳过 → 后序加入2
             → 后序加入0
      后序：[3,1,2,0]
      反转：[0,2,1,3] ← 合法拓扑序

    【时间复杂度】O(V + E)
    【空间复杂度】O(V + E)
    """
    def findOrder_dfs(self, numCourses: int, prerequisites: List[List[int]]) -> List[int]:
        graph = [[] for _ in range(numCourses)]
        for course, prereq in prerequisites:
            graph[prereq].append(course)

        WHITE, GRAY, BLACK = 0, 1, 2
        color = [WHITE] * numCourses
        post_order = []
        has_cycle = False

        def dfs(node: int):
            nonlocal has_cycle
            if has_cycle:
                return
            color[node] = GRAY
            for neighbor in graph[node]:
                if color[neighbor] == GRAY:
                    has_cycle = True
                    return
                if color[neighbor] == WHITE:
                    dfs(neighbor)
                    if has_cycle:
                        return
            color[node] = BLACK
            post_order.append(node)

        for i in range(numCourses):
            if color[i] == WHITE:
                dfs(i)
                if has_cycle:
                    return []

        post_order.reverse()
        return post_order
