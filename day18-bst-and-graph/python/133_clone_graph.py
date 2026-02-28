"""
LeetCode 133. Clone Graph
难度: Medium

题目描述：
给你无向连通图中一个节点的引用，请你返回该图的深拷贝（克隆）。
图中的每个节点都包含它的值 val（int）和其邻居的列表 neighbors（List[Node]）。

节点定义：
class Node:
    def __init__(self, val=0, neighbors=None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []

示例 1：adjList = [[2,4],[1,3],[2,4],[1,3]] → 返回克隆后的图
  节点 1 的邻居是 [2,4]，节点 2 的邻居是 [1,3]，以此类推。
示例 2：adjList = [[]] → 返回只有一个节点的图
示例 3：adjList = [] → 返回 null（空图）

【拓展练习】
1. LeetCode 138. Copy List with Random Pointer —— 深拷贝带随机指针的链表
2. LeetCode 1485. Clone Binary Tree With Random Pointer —— 克隆带随机指针的二叉树
3. LeetCode 261. Graph Valid Tree —— 判断图是否为有效的树
"""

from typing import Optional
from collections import deque


class Node:
    def __init__(self, val=0, neighbors=None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []


class Solution:
    """
    ==================== 解法一：DFS + 哈希表 ====================

    【核心思路】
    用哈希表记录 {原节点 → 克隆节点} 的映射。
    DFS 遍历原图，对每个节点创建克隆，递归地克隆其邻居并建立连接。

    【思考过程】
    1. 深拷贝 = 创建全新的节点，且节点之间的连接关系与原图完全一致。

    2. 图可能有环，所以需要记录已经克隆过的节点（避免无限循环）。
       → 用哈希表 old_to_new 存储映射。

    3. DFS 递归逻辑：
       a) 如果当前节点已经克隆过（在哈希表中），直接返回克隆节点。
       b) 否则创建克隆节点，存入哈希表。
       c) 递归克隆所有邻居，加入克隆节点的 neighbors 列表。

    4. 递归的终止条件：节点为 None 或已访问过。

    【举例】adjList = [[2,4],[1,3],[2,4],[1,3]]
      clone(1) → 创建 1'，递归 clone(2), clone(4)
        clone(2) → 创建 2'，递归 clone(1)→已存在返回1', clone(3)
          clone(3) → 创建 3'，递归 clone(2)→已存在, clone(4)
            clone(4) → 创建 4'，递归 clone(1)→已存在, clone(3)→已存在
      所有节点克隆完毕，连接关系正确

    【时间复杂度】O(V+E)，V 为节点数，E 为边数
    【空间复杂度】O(V)，哈希表 + 递归栈
    """
    def cloneGraph_dfs(self, node: Optional['Node']) -> Optional['Node']:
        if not node:
            return None

        old_to_new = {}

        def dfs(n: 'Node') -> 'Node':
            if n in old_to_new:
                return old_to_new[n]

            clone = Node(n.val)
            old_to_new[n] = clone
            for neighbor in n.neighbors:
                clone.neighbors.append(dfs(neighbor))
            return clone

        return dfs(node)

    """
    ==================== 解法二：BFS + 哈希表 ====================

    【核心思路】
    使用 BFS 层序遍历原图，用哈希表记录映射关系。
    每次从队列取出一个节点，遍历其邻居：
    若邻居未克隆则创建并入队，然后将克隆后的邻居加入当前克隆节点的 neighbors。

    【思考过程】
    1. BFS 和 DFS 都可以遍历图，核心思路一样，只是遍历顺序不同。

    2. BFS 步骤：
       a) 创建起始节点的克隆，放入队列和哈希表。
       b) 每次出队一个节点，遍历其邻居：
          - 邻居未克隆 → 创建克隆并入队
          - 将邻居的克隆加入当前克隆节点的 neighbors

    3. BFS 保证每个节点只入队一次（通过哈希表判断是否已创建克隆）。

    【举例】adjList = [[2,4],[1,3],[2,4],[1,3]]
      queue: [1], old_to_new: {1→1'}
      出队 1：邻居 2,4 → 创建 2',4' 入队，1'.neighbors = [2',4']
      出队 2：邻居 1(已克隆),3 → 创建 3' 入队，2'.neighbors = [1',3']
      出队 4：邻居 1(已克隆),3(已克隆) → 4'.neighbors = [1',3']
      出队 3：邻居 2(已克隆),4(已克隆) → 3'.neighbors = [2',4']

    【时间复杂度】O(V+E)
    【空间复杂度】O(V)，哈希表 + 队列
    """
    def cloneGraph_bfs(self, node: Optional['Node']) -> Optional['Node']:
        if not node:
            return None

        old_to_new = {node: Node(node.val)}
        queue = deque([node])

        while queue:
            curr = queue.popleft()
            for neighbor in curr.neighbors:
                if neighbor not in old_to_new:
                    old_to_new[neighbor] = Node(neighbor.val)
                    queue.append(neighbor)
                old_to_new[curr].neighbors.append(old_to_new[neighbor])

        return old_to_new[node]
