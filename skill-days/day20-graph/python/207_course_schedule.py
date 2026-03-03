"""
LeetCode 207. Course Schedule
难度: Medium

题目描述：
你这个学期必须选修 numCourses 门课程，记为 0 到 numCourses - 1。
在选修某些课程之前需要一些先修课程。先修课程按数组 prerequisites 给出，
其中 prerequisites[i] = [ai, bi]，表示如果要学习课程 ai，则必须先学习课程 bi。
请你判断是否可能完成所有课程的学习。

示例 1：numCourses = 2, prerequisites = [[1,0]] → 输出 true
  解释：总共 2 门课程。学习课程 1 之前，你需要完成课程 0。这是可能的。
示例 2：numCourses = 2, prerequisites = [[1,0],[0,1]] → 输出 false
  解释：总共 2 门课程。学习课程 1 之前需要先完成课程 0；学习课程 0 之前需要先完成课程 1。
       这是不可能的（存在环）。

【拓展练习】
1. LeetCode 210. Course Schedule II —— 返回拓扑排序的具体顺序
2. LeetCode 630. Course Schedule III —— 贪心+优先队列，选最多课程
3. LeetCode 802. Find Eventual Safe States —— 找到所有最终安全的节点
"""

from typing import List
from collections import deque


class Solution:
    """
    ==================== 解法一：BFS 拓扑排序（Kahn 算法） ====================

    【核心思路】
    能完成所有课程 ⟺ 课程依赖关系构成的有向图中没有环 ⟺ 存在拓扑排序。
    Kahn 算法：不断将入度为 0 的节点入队并删除其出边，如果最终所有节点都被处理，
    说明无环；否则剩余节点形成环。

    【思考过程】
    1. "先修课程"天然形成有向图：b→a 表示先学 b 再学 a。
       能否完成所有课程 = 这个有向图是否有环。
       → 经典问题：有向图环检测 / 拓扑排序。

    2. Kahn 算法的直觉：
       - 入度为 0 的节点没有前置依赖，可以直接学习。
       - 学完一门课后，把它从图中"删掉"（将其指向的节点入度减 1）。
       - 如果有新的入度为 0 的节点出现，继续学习。
       - 如果某一刻没有入度为 0 的节点，但还有课没学完 → 环！

    3. 实现：
       - 统计每个节点的入度
       - 入度为 0 的节点全部入队
       - BFS 逐个取出，减少邻居入度，新的入度为 0 的入队
       - 最终检查已处理节点数是否等于总课程数

    【举例】numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]]
      图：0→1, 0→2, 1→3, 2→3
      入度：0:0, 1:1, 2:1, 3:2

      队列：[0]（入度为0）
      取出0 → 1入度→0, 2入度→0 → 队列 [1,2]
      取出1 → 3入度→1 → 队列 [2]
      取出2 → 3入度→0 → 队列 [3]
      取出3 → 队列空
      处理了4个节点 == numCourses → 返回 True

    【时间复杂度】O(V + E)，V 为课程数，E 为依赖关系数
    【空间复杂度】O(V + E)，存储图和入度数组
    """
    def canFinish_bfs(self, numCourses: int, prerequisites: List[List[int]]) -> bool:
        in_degree = [0] * numCourses
        graph = [[] for _ in range(numCourses)]

        for course, prereq in prerequisites:
            graph[prereq].append(course)
            in_degree[course] += 1

        queue = deque()
        for i in range(numCourses):
            if in_degree[i] == 0:
                queue.append(i)

        count = 0
        while queue:
            node = queue.popleft()
            count += 1
            for neighbor in graph[node]:
                in_degree[neighbor] -= 1
                if in_degree[neighbor] == 0:
                    queue.append(neighbor)

        return count == numCourses

    """
    ==================== 解法二：DFS 检测环（三色标记法） ====================

    【核心思路】
    用 DFS 遍历有向图，给每个节点标记三种状态：
    - 白色（0）：未访问
    - 灰色（1）：当前 DFS 路径上（正在处理中）
    - 黑色（2）：已完成处理

    如果 DFS 过程中遇到灰色节点，说明找到了回边，即存在环。

    【思考过程】
    1. DFS 是检测有向图环的另一种经典方法。
       关键在于区分"正在当前路径上的节点"和"已完全处理的节点"。

    2. 为什么需要三种颜色？
       - 仅用 visited（黑白）不够：
         假设 A→C, B→C，DFS(A) 访问了 C 并标记 visited。
         然后 DFS(B) 遇到 C，如果只看 visited，会误认为有环。
         但实际上 C 已经完全处理完了，不在当前 DFS 栈上。
       - 灰色表示"在当前 DFS 栈上"→ 遇到灰色才是真正的环。

    3. 流程：
       - 对每个白色节点启动 DFS
       - 进入时标记灰色
       - 遍历完所有邻居后标记黑色
       - 若遇到灰色邻居 → 有环，返回 False

    【举例】numCourses=3, prerequisites=[[1,0],[2,1],[0,2]]
      图：0→1, 1→2, 2→0（形成环）
      DFS(0): 标灰0 → DFS(1): 标灰1 → DFS(2): 标灰2
              → 2的邻居是0，0是灰色 → 检测到环！返回 False

    【时间复杂度】O(V + E)
    【空间复杂度】O(V + E)，递归栈 + 图存储
    """
    def canFinish_dfs(self, numCourses: int, prerequisites: List[List[int]]) -> bool:
        graph = [[] for _ in range(numCourses)]
        for course, prereq in prerequisites:
            graph[prereq].append(course)

        WHITE, GRAY, BLACK = 0, 1, 2
        color = [WHITE] * numCourses

        def dfs(node: int) -> bool:
            color[node] = GRAY
            for neighbor in graph[node]:
                if color[neighbor] == GRAY:
                    return False
                if color[neighbor] == WHITE and not dfs(neighbor):
                    return False
            color[node] = BLACK
            return True

        for i in range(numCourses):
            if color[i] == WHITE:
                if not dfs(i):
                    return False
        return True
