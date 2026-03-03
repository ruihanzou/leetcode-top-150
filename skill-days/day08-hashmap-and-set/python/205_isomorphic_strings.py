"""
LeetCode 205. Isomorphic Strings
难度: Easy

题目描述：
给定两个字符串 s 和 t，判断它们是否同构。
如果 s 中的字符可以按某种映射关系替换得到 t，则称 s 和 t 是同构的。
每个出现的字符都应当映射到另一个字符，同时不改变字符的顺序。
不同字符不能映射到同一个字符上，但字符可以映射到自己本身。

示例 1：s = "egg", t = "add" → 输出 true（e→a, g→d）
示例 2：s = "foo", t = "bar" → 输出 false（o不能同时映射到a和r）
示例 3：s = "paper", t = "title" → 输出 true（p→t, a→i, e→l, r→e）

【拓展练习】
1. LeetCode 290. Word Pattern —— 同样的双向映射思想，从字符级别扩展到单词级别
2. LeetCode 890. Find and Replace Pattern —— 同构匹配的变体，需要找出所有匹配的单词
3. LeetCode 1153. String Transforms Into Another String —— 更复杂的字符映射转换问题
"""


class Solution:
    """
    ==================== 解法一：双向哈希映射 ====================

    【核心思路】
    维护两个字典：s→t 和 t→s。
    遍历每对字符 (s[i], t[i])，检查映射是否一致：
    - s[i] 已映射到某字符，必须等于 t[i]
    - t[i] 已被某字符映射，必须等于 s[i]
    若不一致则不同构。

    【思考过程】
    1. 同构要求的是双射（bijection）：s 的每个字符映射到 t 的唯一字符，
       且 t 的每个字符也只被 s 的一个字符映射。

    2. 只维护单向映射 s→t 会漏掉"多对一"的情况。
       例如 s="ab", t="aa"，单向映射 a→a, b→a 不冲突，但不是双射。
       所以需要同时维护反向映射 t→s。

    3. 每个位置检查两个映射是否都满足，全部通过则同构。

    【举例】s = "egg", t = "add"
      i=0: s2t={e:a}, t2s={a:e}  ✓
      i=1: s2t={e:a, g:d}, t2s={a:e, d:g}  ✓
      i=2: g 已映射到 d，t[2]=d 一致 ✓；d 已被 g 映射，s[2]=g 一致 ✓
      全部通过 → True

    【时间复杂度】O(n)，n 为字符串长度
    【空间复杂度】O(n)，两个映射字典
    """
    def isIsomorphic_two_map(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False

        s2t, t2s = {}, {}
        for cs, ct in zip(s, t):
            if cs in s2t and s2t[cs] != ct:
                return False
            if ct in t2s and t2s[ct] != cs:
                return False
            s2t[cs] = ct
            t2s[ct] = cs
        return True

    """
    ==================== 解法二：记录首次出现位置 ====================

    【核心思路】
    对于同构字符串，两个字符串中每个位置的字符在各自字符串中的"首次出现位置"
    应该相同。用这个性质来判断同构关系。

    【思考过程】
    1. 同构的本质是"结构相同"。如果我们把每个字符替换成它在字符串中第一次出现的下标，
       两个同构字符串会产生相同的下标序列。
       例如 "egg" → [0,1,1]，"add" → [0,1,1]，序列相同 → 同构。

    2. Python 中 str.index(c) 或 str.find(c) 可以获取字符的首次出现位置。
       直接比较两个字符串每个位置字符的 index 即可。

    3. 这种方法代码极其简洁，但 find() 本身是 O(n)，整体 O(n²)。
       可以用字典优化到 O(n)。

    【举例】s = "foo", t = "bar"
      i=0: s.index('f')=0, t.index('b')=0 → 相同 ✓
      i=1: s.index('o')=1, t.index('a')=1 → 相同 ✓
      i=2: s.index('o')=1, t.index('r')=2 → 不同 ✗ → False

    【时间复杂度】O(n)（使用字典记录首次位置的版本）
    【空间复杂度】O(n)
    """
    def isIsomorphic_first_pos(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False

        first_s, first_t = {}, {}
        for i, (cs, ct) in enumerate(zip(s, t)):
            if first_s.get(cs, -1) != first_t.get(ct, -1):
                return False
            first_s[cs] = i
            first_t[ct] = i
        return True
