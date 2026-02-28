"""
LeetCode 383. Ransom Note
难度: Easy

题目描述：
给定两个字符串 ransomNote 和 magazine，判断 ransomNote 能否由 magazine 中的字符构成。
magazine 中的每个字符只能在 ransomNote 中使用一次。

示例 1：ransomNote = "a", magazine = "b" → 输出 false
示例 2：ransomNote = "aa", magazine = "ab" → 输出 false
示例 3：ransomNote = "aa", magazine = "aab" → 输出 true

【拓展练习】
1. LeetCode 242. Valid Anagram —— 同样使用字符计数，判断两个字符串是否是字母异位词
2. LeetCode 387. First Unique Character in a String —— 字符频次统计的应用
3. LeetCode 438. Find All Anagrams in a String —— 滑动窗口 + 字符计数
"""

from collections import Counter


class Solution:
    """
    ==================== 解法一：哈希计数 ====================

    【核心思路】
    用 Counter 统计 magazine 中每个字符的出现次数，
    然后遍历 ransomNote，每用一个字符就将对应计数减 1，
    如果某个字符计数不足（< 0），说明无法构成。

    【思考过程】
    1. ransomNote 的每个字符都必须来自 magazine，且每个字符只能用一次。
       这本质上是"magazine 中每种字符的数量 >= ransomNote 中对应字符的数量"。

    2. 先统计 magazine 的字符频次作为"可用库存"，
       再逐字符消耗 ransomNote 所需的字符，库存不足则返回 False。

    【举例】ransomNote = "aa", magazine = "aab"
      magazine 统计：{'a':2, 'b':1}
      ransomNote 消耗：第一个 a → {'a':1, 'b':1}，第二个 a → {'a':0, 'b':1}
      全部消耗完毕，返回 True

    【时间复杂度】O(m + n)，m = len(magazine), n = len(ransomNote)
    【空间复杂度】O(1)，最多 26 个小写字母
    """
    def canConstruct_hash(self, ransomNote: str, magazine: str) -> bool:
        count = Counter(magazine)
        for c in ransomNote:
            if count[c] <= 0:
                return False
            count[c] -= 1
        return True

    """
    ==================== 解法二：数组计数 ====================

    【核心思路】
    用长度为 26 的列表代替 Counter，下标 ord(c) - ord('a') 对应字符 c 的频次。
    先加 magazine 的频次，再减 ransomNote 的频次，出现负数即不可构成。

    【思考过程】
    1. 题目说明只包含小写英文字母，字符范围固定为 26 个，
       用数组比字典在常数因子上更快。

    2. 数组下标天然就是字符到整数的映射，arr[ord(c) - ord('a')] 即字符 c 的计数。

    【举例】ransomNote = "aa", magazine = "aab"
      遍历 magazine：arr[0]=2, arr[1]=1
      遍历 ransomNote：arr[0]-- → 1, arr[0]-- → 0，均 >= 0，返回 True

    【时间复杂度】O(m + n)
    【空间复杂度】O(1)，固定 26 长度列表
    """
    def canConstruct_array(self, ransomNote: str, magazine: str) -> bool:
        count = [0] * 26
        for c in magazine:
            count[ord(c) - ord('a')] += 1
        for c in ransomNote:
            count[ord(c) - ord('a')] -= 1
            if count[ord(c) - ord('a')] < 0:
                return False
        return True
