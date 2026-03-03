"""
LeetCode 30. Substring with Concatenation of All Words
难度: Hard

题目描述：
给定一个字符串 s 和一个字符串数组 words。words 中所有字符串长度相同。
s 中的串联子串是指一个包含 words 中所有字符串以任意顺序排列连接起来的子串。
返回所有串联子串在 s 中的起始索引。你可以以任意顺序返回答案。

示例 1：
  输入：s = "barfoothefoobarman", words = ["foo","bar"]
  输出：[0,9]
  解释：
    从索引 0 开始的子串是 "barfoo"，它是 ["bar","foo"] 的串联。
    从索引 9 开始的子串是 "foobar"，它是 ["foo","bar"] 的串联。

示例 2：
  输入：s = "wordgoodgoodgoodbestword", words = ["word","good","best","word"]
  输出：[8]
  解释：从索引 8 开始的子串是 "able goodbestword"，
    它是 ["word","good","best","word"] 的串联。

示例 3：
  输入：s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
  输出：[6,9,12]

【拓展练习】
1. LeetCode 76. Minimum Window Substring —— 滑动窗口 + 字符计数
2. LeetCode 438. Find All Anagrams in a String —— 固定窗口找变位词
3. LeetCode 567. Permutation in String —— 判断是否包含排列
"""

from typing import List
from collections import Counter


class Solution:
    """
    ==================== 解法一：暴力逐位检查 ====================

    【核心思路】
    枚举 s 中每一个可能的起始位置 i，从 i 开始每次截取 word_len 长度的子串，
    连续截取 word_count 个，检查这些子串是否恰好构成 words 的一个排列。

    【思考过程】
    1. 串联子串的总长度 = word_count * word_len，
       所以起始位置 i 的范围是 [0, len(s) - total_len]。

    2. 对每个 i，用一个临时计数器 seen，逐个截取长度 word_len 的子串，
       如果该子串不在 words 中或者出现次数超过 words 中的次数，就提前终止。

    3. 如果成功截取了 word_count 个子串，说明 i 是一个合法起始位置。

    【举例】s = "barfoothefoobarman", words = ["foo","bar"]
      word_len=3, word_count=2, total_len=6
      word_freq = {"foo":1, "bar":1}

      i=0: 截取 "bar"(✓), "foo"(✓), 共2个 → 合法，加入结果
      i=1: 截取 "arf" → 不在words中，跳过
      ...
      i=9: 截取 "foo"(✓), "bar"(✓), 共2个 → 合法，加入结果
      结果 = [0, 9]

    【时间复杂度】O(n * m * k) —— n=len(s), m=word_count, k=word_len
    【空间复杂度】O(m * k) —— 计数器空间
    """
    def findSubstring_brute(self, s: str, words: List[str]) -> List[int]:
        if not s or not words:
            return []

        word_len = len(words[0])
        word_count = len(words)
        total_len = word_len * word_count
        word_freq = Counter(words)
        result = []

        for i in range(len(s) - total_len + 1):
            seen = Counter()
            j = 0
            while j < word_count:
                start = i + j * word_len
                word = s[start:start + word_len]
                if word not in word_freq:
                    break
                seen[word] += 1
                if seen[word] > word_freq[word]:
                    break
                j += 1
            if j == word_count:
                result.append(i)

        return result

    """
    ==================== 解法二：滑动窗口（按单词长度分组） ====================

    【核心思路】
    将所有起始位置按 i % word_len 分成 word_len 组。
    每组内，以 word_len 为步长滑动窗口，窗口内维护单词计数。
    窗口右端加入一个新单词时更新计数，窗口内单词数超过 word_count 时左端弹出。

    【思考过程】
    1. 暴力解法中，相邻起始位置 i 和 i+1 的检查窗口有大量重叠，重复工作太多。

    2. 关键观察：如果我们把 s 按 word_len 为单位分段，那么一个合法的串联子串
       一定是从某一段的起点开始，连续取 word_count 段。

    3. 起始偏移量只有 word_len 种（0, 1, ..., word_len-1）。
       对于每种偏移 offset，我们把 s 切成一段一段的"单词"：
       s[offset:offset+wl], s[offset+wl:offset+2*wl], ...

    4. 然后在这些"单词"上做滑动窗口：维护一个大小为 word_count 的窗口，
       右端加入新单词，左端必要时弹出多余单词。

    5. 这样每个单词只被加入和弹出各一次，每组的时间是 O(n/word_len)，
       共 word_len 组，总时间 O(n)。

    【举例】s = "barfoothefoobarman", words = ["foo","bar"], word_len=3
      offset=0: 分段 ["bar","foo","the","foo","bar","man"]
        right=0 "bar": window={"bar":1}, count=1
        right=1 "foo": window={"bar":1,"foo":1}, count=2==word_count
                → 起始=0*3=0 ✓, 弹left "bar": count=1
        right=2 "the": 不在words中 → 清空, left=3
        right=3 "foo": window={"foo":1}, count=1
        right=4 "bar": window={"foo":1,"bar":1}, count=2
                → 起始=3*3=9 ✓, 弹left "foo": count=1
        right=5 "man": 不在words中 → 清空
      结果 = [0, 9]

    【时间复杂度】O(n * k) —— 外层 word_len 组，每组 O(n/word_len) 个单词，
                              每个单词截取 O(k)，总计 O(n*k)
    【空间复杂度】O(m) —— 窗口计数器最多 m=word_count 个条目
    """
    def findSubstring_sliding(self, s: str, words: List[str]) -> List[int]:
        if not s or not words:
            return []

        word_len = len(words[0])
        word_count = len(words)
        total_len = word_len * word_count
        word_freq = Counter(words)
        result = []

        for offset in range(word_len):
            left = offset
            window = Counter()
            matched = 0

            for right_start in range(offset, len(s) - word_len + 1, word_len):
                word = s[right_start:right_start + word_len]

                if word not in word_freq:
                    window.clear()
                    matched = 0
                    left = right_start + word_len
                    continue

                window[word] += 1
                matched += 1

                while window[word] > word_freq[word]:
                    left_word = s[left:left + word_len]
                    window[left_word] -= 1
                    matched -= 1
                    left += word_len

                if matched == word_count:
                    result.append(left)

        return result
