"""
LeetCode 139. Word Break
难度: Medium

题目描述：
给你一个字符串 s 和一个字符串列表 wordDict 作为字典。
如果可以利用字典中出现的一个或多个单词拼接出 s，则返回 true。
注意：字典中的单词可以重复使用。

示例：s = "leetcode", wordDict = ["leet","code"] → 输出 true
     （"leetcode" = "leet" + "code"）

【拓展练习】
1. LeetCode 140. Word Break II —— 返回所有可行的拆分方案（回溯+记忆化）
2. LeetCode 472. Concatenated Words —— 判断数组中哪些词可由其他词拼接而成
3. LeetCode 91. Decode Ways —— 类似的字符串分段DP问题
"""

from typing import List
from collections import deque


class Solution:
    """
    ==================== 解法一：DP ====================

    【核心思路】
    dp[i] 表示 s[0:i]（前 i 个字符）是否可以被字典中的单词拼接而成。
    对于每个位置 i，枚举所有可能的最后一个单词 s[j:i]，
    如果 dp[j] 为 True 且 s[j:i] 在字典中，则 dp[i] = True。

    【思考过程】
    1. 定义状态 dp[i]："s 的前 i 个字符是否可拆分"。
       目标是 dp[n]（n 为 s 的长度）。

    2. 转移：dp[i] = True 当且仅当存在某个 j (0 <= j < i) 使得：
       - dp[j] = True（前 j 个字符已成功拆分）
       - s[j:i] 在字典中（从 j 到 i 这段是一个完整单词）

    3. 将字典放入 set 中，判断 s[j:i] 是否在字典里就是 O(L) 的操作。
       可以利用字典中单词最大长度来限制内层循环范围，避免无用枚举。

    【举例】s = "leetcode", wordDict = ["leet", "code"]
      dp = [T, F, F, F, F, F, F, F, F]  (长度9，dp[0]=True 表示空串可拆分)
      i=4: j=0, s[0:4]="leet" ∈ dict, dp[0]=T → dp[4]=T
      i=8: j=4, s[4:8]="code" ∈ dict, dp[4]=T → dp[8]=T
      答案 = dp[8] = True

    【时间复杂度】O(n² * L)，n 为字符串长度，L 为字典中最长单词长度
    【空间复杂度】O(n)
    """
    def wordBreak_dp(self, s: str, wordDict: List[str]) -> bool:
        word_set = set(wordDict)
        max_len = max(len(w) for w in wordDict)
        n = len(s)

        dp = [False] * (n + 1)
        dp[0] = True

        for i in range(1, n + 1):
            for j in range(max(0, i - max_len), i):
                if dp[j] and s[j:i] in word_set:
                    dp[i] = True
                    break

        return dp[n]

    """
    ==================== 解法二：BFS ====================

    【核心思路】
    把问题看作图的遍历：每个位置 i 是一个节点，
    如果 s[i:i+len(word)] 等于字典中的某个 word，则从 i 连一条边到 i+len(word)。
    问题变成：从节点 0 出发，能否到达节点 n。

    【思考过程】
    1. BFS 天然适合探索"从起点能否到达终点"的问题。

    2. 从位置 0 开始，尝试匹配字典中的每个单词。
       如果 s[0:len(w)] == w，就把 len(w) 加入队列。
       从队列取出位置 i，再尝试从 i 开始匹配……直到到达 n。

    3. 用 visited 集合防止重复访问同一位置。

    4. 与 DP 解法等价，但思维模型不同：DP 是"填表"，BFS 是"搜索"。

    【举例】s = "leetcode", wordDict = ["leet", "code"]
      队列: [0]
      取出 0: s[0:4]="leet" ∈ dict → 加入 4
      取出 4: s[4:8]="code" ∈ dict → 加入 8
      8 == n → 返回 True

    【时间复杂度】O(n² * L)，最坏情况每个位置都要检查所有单词
    【空间复杂度】O(n)
    """
    def wordBreak_bfs(self, s: str, wordDict: List[str]) -> bool:
        word_set = set(wordDict)
        n = len(s)
        visited = set()
        queue = deque([0])

        while queue:
            start = queue.popleft()
            if start == n:
                return True
            if start in visited:
                continue
            visited.add(start)

            for end in range(start + 1, n + 1):
                if s[start:end] in word_set:
                    queue.append(end)

        return False
