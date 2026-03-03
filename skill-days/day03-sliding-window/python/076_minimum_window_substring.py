"""
LeetCode 76. Minimum Window Substring
难度: Hard

题目描述：
给你一个字符串 s 和一个字符串 t，返回 s 中涵盖 t 所有字符的最小子串。
如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 ""。

注意：
- 对于 t 中重复的字符，我们寻找的子串中该字符数量必须不少于 t 中该字符数量。
- 如果 s 中存在这样的子串，我们保证它是唯一的答案。

示例 1：
  输入：s = "ADOBECODEBANC", t = "ABC"
  输出："BANC"
  解释：最小覆盖子串 "BANC" 包含了 t 中的 'A', 'B', 'C'。

示例 2：
  输入：s = "a", t = "a"
  输出："a"

示例 3：
  输入：s = "a", t = "aa"
  输出：""
  解释：t 中有两个 'a'，但 s 中只有一个 'a'，无法涵盖。

【拓展练习】
1. LeetCode 567. Permutation in String —— 判断 s2 是否包含 s1 的排列
2. LeetCode 438. Find All Anagrams in a String —— 找所有变位词的起始索引
3. LeetCode 727. Minimum Window Subsequence —— 最小窗口子序列（更难）
"""

from collections import Counter


class Solution:
    """
    ==================== 解法一：滑动窗口 + matched 计数 ====================

    【核心思路】
    维护窗口 [left, right]，用 window 计数器记录窗口内各字符的出现次数。
    额外维护 matched 变量表示"已满足需求的字符种类数"。
    当 matched == required（需要满足的字符种类数）时，尝试收缩左边界。

    【思考过程】
    1. 先统计 t 中每个字符的需求量 need[c]。
       required = len(need)，即需要满足的不同字符种类数。

    2. 右移 right 扩大窗口，每加入一个字符 c，如果 window[c] == need[c]，
       说明字符 c 的需求刚好被满足，matched += 1。

    3. 当 matched == required 时，窗口已经覆盖了 t 的所有字符。
       此时尝试收缩 left：如果移除 s[left] 后 window[s[left]] < need[s[left]]，
       说明某个字符不再满足需求，matched -= 1。

    4. 在每次 matched == required 时更新最小窗口。

    【举例】s = "ADOBECODEBANC", t = "ABC"
      need = {A:1, B:1, C:1}, required = 3
      right=0 'A': window={A:1}, A满足→matched=1
      right=1 'D': window={A:1,D:1}, matched=1
      right=2 'O': matched=1
      right=3 'B': window={...,B:1}, B满足→matched=2
      right=4 'E': matched=2
      right=5 'C': window={...,C:1}, C满足→matched=3
        → 窗口"ADOBEC"(0,5), 长度6
        缩left=0 'A': window{A:0}<need{A:1}, matched=2, left=1
      ...
      right=10 'A': matched=3 → 窗口"CODEBA"? 继续缩...
      right=12 'C': matched=3 → 窗口"BANC"(9,12), 长度4
        这是最短的
      答案 = "BANC"

    【时间复杂度】O(|s| + |t|) —— 构建 need O(|t|)，滑动窗口 O(|s|)
    【空间复杂度】O(|s| + |t|) —— 最坏情况下 window 和 need 的大小
    """
    def minWindow_matched(self, s: str, t: str) -> str:
        if not s or not t or len(s) < len(t):
            return ""

        need = Counter(t)
        required = len(need)
        window = {}

        left = 0
        matched = 0
        min_len = float('inf')
        min_start = 0

        for right in range(len(s)):
            c = s[right]
            window[c] = window.get(c, 0) + 1

            if c in need and window[c] == need[c]:
                matched += 1

            while matched == required:
                if right - left + 1 < min_len:
                    min_len = right - left + 1
                    min_start = left

                lc = s[left]
                window[lc] -= 1
                if lc in need and window[lc] < need[lc]:
                    matched -= 1
                left += 1

        return "" if min_len == float('inf') else s[min_start:min_start + min_len]

    """
    ==================== 解法二：滑动窗口 + 计数数组 ====================

    【核心思路】
    用长度为 128 的数组代替哈希表来统计字符频率。
    维护 diff 变量表示"窗口中还欠缺的字符总数"，
    当 diff == 0 时窗口满足条件，尝试收缩。

    【思考过程】
    1. 使用数组比哈希表更快（直接索引 vs 哈希计算）。

    2. 用 need[c] 初始化各字符的需求量。
       每次加入字符 c 时，如果加入前 need[c] > 0（说明还欠这个字符），diff -= 1。
       每次移除字符 c 时，如果移除后 need[c] > 0（说明又欠了），diff += 1。

    3. diff 等于 0 时所有需求都被满足，更新答案并收缩左边界。

    4. 这种方法用一个整数 diff 代替了解法一中的 matched 和 required，
       逻辑更简洁。

    【举例】s = "ADOBECODEBANC", t = "ABC"
      need[A]=1, need[B]=1, need[C]=1, diff=3
      right=0 'A': need[A]>0→diff=2, need[A]=0
      right=3 'B': need[B]>0→diff=1, need[B]=0
      right=5 'C': need[C]>0→diff=0, need[C]=0
        → diff==0, 窗口(0,5)="ADOBEC", len=6
        缩left=0 'A': need[A]=1>0→diff=1
      ...最终找到 "BANC"(9,12), len=4

    【时间复杂度】O(|s| + |t|)
    【空间复杂度】O(1) —— 数组大小固定为 128
    """
    def minWindow_array(self, s: str, t: str) -> str:
        if not s or not t or len(s) < len(t):
            return ""

        need = [0] * 128
        for c in t:
            need[ord(c)] += 1

        diff = len(t)
        left = 0
        min_len = float('inf')
        min_start = 0

        for right in range(len(s)):
            idx = ord(s[right])
            if need[idx] > 0:
                diff -= 1
            need[idx] -= 1

            while diff == 0:
                if right - left + 1 < min_len:
                    min_len = right - left + 1
                    min_start = left

                left_idx = ord(s[left])
                need[left_idx] += 1
                if need[left_idx] > 0:
                    diff += 1
                left += 1

        return "" if min_len == float('inf') else s[min_start:min_start + min_len]
