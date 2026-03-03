"""
LeetCode 433. Minimum Genetic Mutation
难度: Medium

题目描述：
基因序列可以表示为一条由 8 个字符组成的字符串，其中每个字符都是 'A'、'C'、'G' 和 'T'
之一。
假设我们需要调查从基因序列 start 变为 end 所发生的基因变化。一次基因变化就意味着
这个基因序列中的一个字符发生了变化。
另有一个基因库 bank 记录了所有有效的基因变化，只有基因库中的基因才是有效的基因序列。

给你两个基因序列 start 和 end，以及一个基因库 bank，请你找出并返回能够使 start
变化为 end 所需的最少变化次数。如果无法完成此基因变化，返回 -1。
注意：起始基因序列 start 默认是有效的，但它不一定会出现在基因库中。

示例 1：start = "AACCGGTT", end = "AACCGGTA", bank = ["AACCGGTA"]
  → 输出 1
示例 2：start = "AACCGGTT", end = "AAACGGTA", bank = ["AACCGGTA","AACCGCTA","AAACGGTA"]
  → 输出 2
  解释："AACCGGTT" → "AACCGGTA" → "AAACGGTA"

【拓展练习】
1. LeetCode 127. Word Ladder —— 类似的 BFS 最短转换问题（26个字母）
2. LeetCode 126. Word Ladder II —— 找所有最短转换序列
3. LeetCode 752. Open the Lock —— BFS 求最短操作次数
"""

from typing import List
from collections import deque


class Solution:
    """
    ==================== 解法一：BFS ====================

    【核心思路】
    将每个合法基因序列看作图中的节点，两个序列之间如果恰好相差一个字符就连一条边。
    从 start 出发 BFS 找到 end 的最短路径。

    【思考过程】
    1. "最少变化次数" = 最短路径 → BFS。

    2. 什么算"相邻"？两个基因序列只差一个字符。
       可以枚举当前序列的每个位置，尝试替换为 A/C/G/T 四种字符，
       如果替换后的序列在基因库中，就可以转移过去。

    3. 基因序列长度固定为 8，字符集只有 4 个 → 每个节点最多 8×3=24 个邻居。

    4. 用 set 存储 bank 以实现 O(1) 查找。用 visited 防止重复访问。

    【举例】start="AACCGGTT", end="AAACGGTA", bank=["AACCGGTA","AACCGCTA","AAACGGTA"]
      BFS 第0层：{"AACCGGTT"}
      第1层：尝试所有单字符变化 →
        "AACCGGTA" 在bank中 ✓ → 加入
        其他变化不在bank中
      第2层：从"AACCGGTA"出发 →
        "AAACGGTA" 在bank中 ✓ → 是end！返回 2

    【时间复杂度】O(n * L * 4)，n 为 bank 大小，L 为序列长度（8），4 为字符集大小
    【空间复杂度】O(n * L)
    """
    def minMutation_bfs(self, startGene: str, endGene: str, bank: List[str]) -> int:
        bank_set = set(bank)
        if endGene not in bank_set:
            return -1

        visited = {startGene}
        queue = deque([(startGene, 0)])
        genes = ['A', 'C', 'G', 'T']

        while queue:
            current, steps = queue.popleft()
            if current == endGene:
                return steps

            for i in range(len(current)):
                for g in genes:
                    if g == current[i]:
                        continue
                    mutation = current[:i] + g + current[i + 1:]
                    if mutation in bank_set and mutation not in visited:
                        visited.add(mutation)
                        queue.append((mutation, steps + 1))

        return -1

    """
    ==================== 解法二：双向 BFS ====================

    【核心思路】
    同时从 start 和 end 两端开始 BFS，每次扩展较小的那一端。
    当两端的搜索"相遇"时，总步数就是答案。

    【思考过程】
    1. 单向 BFS 从一端搜索，搜索空间随深度指数增长。
       双向 BFS 从两端同时搜索，搜索空间大约是单向的平方根。

    2. 每轮选择当前集合较小的一端扩展，可以让搜索树更平衡。

    3. 实现：
       - 维护 front_set 和 back_set，分别从 start 和 end 出发
       - 每轮从较小集合出发，生成下一层 new_front
       - 如果 new_front 中的元素出现在 back_set 中 → 相遇 → 返回步数
       - 交换 front 和 back（始终扩展较小的）

    4. 对于本题（序列长度8，bank通常不大），双向 BFS 优势不明显，
       但在更大规模的问题（如 Word Ladder）中效果显著。

    【举例】start="AACCGGTT", end="AAACGGTA", bank=["AACCGGTA","AACCGCTA","AAACGGTA"]
      front={AACCGGTT}, back={AAACGGTA}, steps=0
      扩展front: {AACCGGTA} (在bank中)，不在back中 → steps=1
      front={AACCGGTA}, back={AAACGGTA}
      扩展front(或back都一样大小): {AAACGGTA, AACCGCTA}
      AAACGGTA 在 back 中 → 相遇！返回 steps=2

    【时间复杂度】O(n * L * 4)，但实际搜索空间远小于单向 BFS
    【空间复杂度】O(n * L)
    """
    def minMutation_bidirectional(self, startGene: str, endGene: str, bank: List[str]) -> int:
        bank_set = set(bank)
        if endGene not in bank_set:
            return -1

        front = {startGene}
        back = {endGene}
        visited = {startGene, endGene}
        genes = ['A', 'C', 'G', 'T']
        steps = 0

        while front and back:
            if len(front) > len(back):
                front, back = back, front

            new_front = set()
            for current in front:
                for i in range(len(current)):
                    for g in genes:
                        if g == current[i]:
                            continue
                        mutation = current[:i] + g + current[i + 1:]
                        if mutation in back:
                            return steps + 1
                        if mutation in bank_set and mutation not in visited:
                            visited.add(mutation)
                            new_front.add(mutation)

            front = new_front
            steps += 1

        return -1
