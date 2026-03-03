"""
LeetCode 399. Evaluate Division
难度: Medium

题目描述：
给你一个变量对数组 equations 和一个实数值数组 values，其中 equations[i] = [Ai, Bi]
和 values[i] 表示等式 Ai / Bi = values[i]。每个 Ai 或 Bi 是一个表示单个变量的字符串。
另有一个查询数组 queries，其中 queries[j] = [Cj, Dj] 表示第 j 个查询，
你需要找出 Cj / Dj = ? 的答案。
如果存在某个无法确定的答案，则用 -1.0 替代。

示例：
  equations = [["a","b"],["b","c"]], values = [2.0,3.0]
  queries = [["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]
  → 输出 [6.0,0.5,-1.0,1.0,-1.0]
  解释：a/b=2.0, b/c=3.0 → a/c=6.0, b/a=0.5

【拓展练习】
1. LeetCode 990. Satisfiability of Equality Equations —— 等式可满足性，并查集经典应用
2. LeetCode 1976. Number of Ways to Arrive at Destination —— 带权图最短路径计数
3. LeetCode 743. Network Delay Time —— 加权有向图最短路径
"""

from typing import List
from collections import defaultdict, deque


class Solution:
    """
    ==================== 解法一：建图 + BFS ====================

    【核心思路】
    将等式关系建模为带权有向图：a/b=k 意味着 a→b 权重为 k，b→a 权重为 1/k。
    对于查询 c/d，从 c 出发 BFS 找到 d，沿途权重相乘即为结果。

    【思考过程】
    1. a/b=2 可以理解为"a 是 b 的 2 倍"。如果我们画一条 a→b 的边权重 2，
       那么从 a 走到 b 就是除以 2（即 a/b=2）；反向 b→a 权重 1/2（即 b/a=0.5）。

    2. 对于 a/c，如果有路径 a→b→c，那么 a/c = (a/b) * (b/c) = 边权之积。
       → 问题转化为在带权有向图中找路径，并计算路径上权重的乘积。

    3. 用 BFS 从起点出发搜索终点：
       - 维护到当前节点的累积乘积
       - 如果找到终点，累积乘积就是答案
       - 如果 BFS 结束也没找到终点，说明不连通，返回 -1.0

    4. 特殊情况：
       - 查询的变量不在图中 → 返回 -1.0
       - 起点和终点相同 → 返回 1.0（自己除以自己）

    【举例】equations=[["a","b"],["b","c"]], values=[2.0,3.0]
      建图：a→b:2, b→a:0.5, b→c:3, c→b:1/3

      查询 a/c：
      BFS从a出发，到a累积=1.0
      → 访问b，累积=1.0*2=2.0
      → 访问c，累积=2.0*3=6.0 → 找到终点，返回6.0

      查询 b/a：
      BFS从b出发 → 访问a，累积=0.5 → 返回0.5

    【时间复杂度】O(Q * (V + E))，Q 为查询数，每次 BFS 遍历图
    【空间复杂度】O(V + E)，图的存储
    """
    def calcEquation_bfs(self, equations: List[List[str]], values: List[float],
                         queries: List[List[str]]) -> List[float]:
        graph = defaultdict(list)
        for (a, b), val in zip(equations, values):
            graph[a].append((b, val))
            graph[b].append((a, 1.0 / val))

        def bfs(src: str, dst: str) -> float:
            if src not in graph or dst not in graph:
                return -1.0
            if src == dst:
                return 1.0

            visited = {src}
            queue = deque([(src, 1.0)])

            while queue:
                node, product = queue.popleft()
                for neighbor, weight in graph[node]:
                    if neighbor == dst:
                        return product * weight
                    if neighbor not in visited:
                        visited.add(neighbor)
                        queue.append((neighbor, product * weight))

            return -1.0

        return [bfs(c, d) for c, d in queries]

    """
    ==================== 解法二：Floyd-Warshall ====================

    【核心思路】
    使用 Floyd-Warshall 算法预处理所有变量对之间的除法结果。
    dist[i][j] 表示 i/j 的值。松弛操作为：如果知道 i/k 和 k/j，
    则 i/j = (i/k) * (k/j)。

    【思考过程】
    1. Floyd-Warshall 通常用于求所有点对最短路径，这里可以类比：
       - "距离"变成"除法比值"
       - "取 min"变成"能否通过中间节点算出比值"
       - 路径上的"加法"变成"乘法"

    2. 初始化：
       - dist[a][a] = 1.0（自己除自己为1）
       - 如果给定 a/b = k，则 dist[a][b] = k, dist[b][a] = 1/k

    3. 三重循环松弛：
       for k: for i: for j:
         如果 dist[i][k] 和 dist[k][j] 都已知，
         则 dist[i][j] = dist[i][k] * dist[k][j]

    4. 查询时直接查表 O(1)。

    5. 优点：预处理后查询极快；缺点：变量多时空间和时间不划算。

    【举例】变量 {a, b, c}
      初始：dist[a][b]=2, dist[b][a]=0.5, dist[b][c]=3, dist[c][b]=1/3
            对角线 dist[x][x]=1
      k=b: dist[a][c] = dist[a][b]*dist[b][c] = 2*3 = 6
           dist[c][a] = dist[c][b]*dist[b][a] = 1/3*0.5 = 1/6

    【时间复杂度】O(V³ + Q)，V 为变量数，Q 为查询数
    【空间复杂度】O(V²)
    """
    def calcEquation_floyd(self, equations: List[List[str]], values: List[float],
                           queries: List[List[str]]) -> List[float]:
        variables = set()
        for a, b in equations:
            variables.add(a)
            variables.add(b)

        dist = defaultdict(lambda: defaultdict(lambda: -1.0))
        for v in variables:
            dist[v][v] = 1.0

        for (a, b), val in zip(equations, values):
            dist[a][b] = val
            dist[b][a] = 1.0 / val

        for k in variables:
            for i in variables:
                for j in variables:
                    if dist[i][k] > 0 and dist[k][j] > 0:
                        dist[i][j] = dist[i][k] * dist[k][j]

        results = []
        for c, d in queries:
            if c not in variables or d not in variables:
                results.append(-1.0)
            else:
                results.append(dist[c][d])
        return results
