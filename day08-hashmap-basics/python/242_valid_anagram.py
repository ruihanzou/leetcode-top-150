"""
LeetCode 242. Valid Anagram
难度: Easy

题目描述：
给定两个字符串 s 和 t，判断 t 是否是 s 的字母异位词。
字母异位词是指由相同的字母重新排列组成的字符串，每个字母的使用次数必须相同。

示例 1：s = "anagram", t = "nagaram" → 输出 true
示例 2：s = "rat", t = "car" → 输出 false

进阶：如果输入字符串包含 unicode 字符怎么办？

【拓展练习】
1. LeetCode 49. Group Anagrams —— 将字母异位词分组，需要设计合适的分组键
2. LeetCode 438. Find All Anagrams in a String —— 滑动窗口查找所有异位词子串
3. LeetCode 567. Permutation in String —— 判断一个字符串的排列是否是另一个的子串
"""

from collections import Counter


class Solution:
    """
    ==================== 解法一：排序比较 ====================

    【核心思路】
    将两个字符串排序后直接比较，若相等则互为字母异位词。

    【思考过程】
    1. 字母异位词由完全相同的字母组成，只是排列顺序不同。

    2. 排序会消除顺序差异，如果排序后的字符串相同，
       说明它们包含完全相同的字符且每个字符出现次数相同。

    3. 这是最直观的解法，代码简洁，但时间复杂度受排序限制。

    【举例】s = "anagram", t = "nagaram"
      排序后：s → "aaagmnr", t → "aaagmnr"
      相等 → True

    【时间复杂度】O(n log n)，排序主导
    【空间复杂度】O(n)，排序用到的额外空间
    """
    def isAnagram_sort(self, s: str, t: str) -> bool:
        return sorted(s) == sorted(t)

    """
    ==================== 解法二：哈希计数 ====================

    【核心思路】
    用 Counter 统计两个字符串的字符频次，直接比较是否相等。

    【思考过程】
    1. 字母异位词等价于"两个字符串中每种字符的出现次数完全相同"。

    2. Python 的 Counter 可以直接比较相等性，代码极其简洁。

    3. 适用于 unicode 字符的情况（进阶问题），因为 Counter 可以存储任意字符。

    【举例】s = "rat", t = "car"
      Counter(s) = {'r':1, 'a':1, 't':1}
      Counter(t) = {'c':1, 'a':1, 'r':1}
      不相等 → False

    【时间复杂度】O(n)
    【空间复杂度】O(1)，最多 26 个字母（或 O(n) 对于 unicode）
    """
    def isAnagram_hash(self, s: str, t: str) -> bool:
        return Counter(s) == Counter(t)

    """
    ==================== 解法三：数组计数 ====================

    【核心思路】
    用长度 26 的列表统计字符频次差异，
    s 中出现的字符 +1，t 中出现的字符 -1，最终检查列表全为 0。

    【思考过程】
    1. 题目限定小写英文字母，字符范围固定为 26 个，
       用数组比字典在常数因子上更优。

    2. 用 +1 和 -1 的抵消策略：遍历 s 时 count[ord(c)-ord('a')]++，
       遍历 t 时 count[ord(c)-ord('a')]--，最终全为 0 则是异位词。

    3. 可以优化为单次遍历：先检查长度，然后同时遍历 s 和 t。

    【举例】s = "anagram", t = "nagaram"
      遍历 s 后：a:3, g:1, m:1, n:1, r:1
      遍历 t 抵消：a:3-3=0, g:1-1=0, m:1-1=0, n:1-1=0, r:1-1=0
      全为 0 → True

    【时间复杂度】O(n)
    【空间复杂度】O(1)，固定 26 长度列表
    """
    def isAnagram_array(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False

        count = [0] * 26
        for cs, ct in zip(s, t):
            count[ord(cs) - ord('a')] += 1
            count[ord(ct) - ord('a')] -= 1
        return all(c == 0 for c in count)
