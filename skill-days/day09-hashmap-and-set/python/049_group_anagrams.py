"""
LeetCode 49. Group Anagrams
难度: Medium

题目描述：
给定一个字符串数组 strs，将字母异位词组合在一起。字母异位词是由重新排列源单词的
所有字母得到的一个新单词。可以按任意顺序返回结果。

示例 1：strs = ["eat","tea","tan","ate","nat","bat"]
       → 输出 [["bat"],["nat","tan"],["ate","eat","tea"]]
示例 2：strs = [""] → 输出 [[""]]
示例 3：strs = ["a"] → 输出 [["a"]]

【拓展练习】
1. LeetCode 242. Valid Anagram —— 判断两个字符串是否是异位词，本题的基础
2. LeetCode 249. Group Shifted Strings —— 类似的分组思想，按"移位模式"分组
3. LeetCode 438. Find All Anagrams in a String —— 滑动窗口查找异位词子串
"""

from collections import defaultdict
from typing import List


class Solution:
    """
    ==================== 解法一：排序键分组 ====================

    【核心思路】
    对每个字符串排序后的结果作为字典的 key，
    排序结果相同的字符串就是字母异位词，归为同一组。

    【思考过程】
    1. 字母异位词排序后会变成相同的字符串。
       例如 "eat", "tea", "ate" 排序后都是 "aet"。

    2. 用排序后的字符串作为 key，原始字符串作为 value 加入列表。
       所有共享同一 key 的字符串就是一组异位词。

    3. 这是最直观的做法，利用了"异位词排序后相同"这一核心性质。

    【举例】strs = ["eat","tea","tan","ate","nat","bat"]
      "eat" → 排序 "aet" → groups["aet"] = ["eat"]
      "tea" → 排序 "aet" → groups["aet"] = ["eat","tea"]
      "tan" → 排序 "ant" → groups["ant"] = ["tan"]
      "ate" → 排序 "aet" → groups["aet"] = ["eat","tea","ate"]
      "nat" → 排序 "ant" → groups["ant"] = ["tan","nat"]
      "bat" → 排序 "abt" → groups["abt"] = ["bat"]
      结果：[["eat","tea","ate"], ["tan","nat"], ["bat"]]

    【时间复杂度】O(n * k * log k)，n 为字符串数量，k 为最长字符串长度
    【空间复杂度】O(n * k)，存储所有字符串
    """
    def groupAnagrams_sort(self, strs: List[str]) -> List[List[str]]:
        groups = defaultdict(list)
        for s in strs:
            key = ''.join(sorted(s))
            groups[key].append(s)
        return list(groups.values())

    """
    ==================== 解法二：计数键分组 ====================

    【核心思路】
    用每个字符串的字符频次元组作为字典的 key，
    频次相同的字符串归为同一组。避免了排序操作。

    【思考过程】
    1. 排序的目的是把异位词映射到同一个标准形式。
       但排序的时间复杂度是 O(k log k)，当字符串很长时不够高效。

    2. 另一种标准形式：字符频次。"eat" 和 "tea" 都是 (1,0,0,0,1,...,1,...)。
       Python 中 tuple 是可哈希的，可以直接作为字典的 key。

    3. 这样每个字符串只需要 O(k) 遍历一次就能生成 key，整体复杂度降为 O(n*k)。

    【举例】strs = ["eat","tea","bat"]
      "eat" → count 元组 (1,0,0,0,1,...,1,...) → 作为 key
      "tea" → 同样的 count 元组 → 归为一组
      "bat" → 不同的 count 元组 → 独立一组

    【时间复杂度】O(n * k)，n 为字符串数量，k 为最长字符串长度
    【空间复杂度】O(n * k)
    """
    def groupAnagrams_count(self, strs: List[str]) -> List[List[str]]:
        # defaultdict is a subclass of dict that provides a default value for a key that is not present in the dictionary.
        # list is a mutable sequence of objects.
        groups = defaultdict(list)
        for s in strs:
            count = [0] * 26
            for c in s:
                count[ord(c) - ord('a')] += 1
                # tuple is hashable, so it can be used as a key in a dictionary
                # .append is used to add an element to the end of the list.
            groups[tuple(count)].append(s)
        return list(groups.values())
