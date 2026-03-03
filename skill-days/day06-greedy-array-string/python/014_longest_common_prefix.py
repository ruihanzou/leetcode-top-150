"""
LeetCode 14. Longest Common Prefix
难度: Easy

题目描述：
编写一个函数来查找字符串数组中的最长公共前缀。
如果不存在公共前缀，返回空字符串 ""。

示例：strs = ["flower","flow","flight"] → 输出 "fl"

【拓展练习】
1. LeetCode 1048. Longest String Chain —— 最长字符串链（动态规划）
2. LeetCode 720. Longest Word in Dictionary —— 字典中最长的单词（Trie）
"""

from typing import List


class Solution:
    """
    ==================== 解法一：纵向扫描 ====================

    【核心思路】
    逐列比较：对于第 col 列，检查所有字符串在 col 位置的字符是否相同。
    一旦不同或某个字符串已经结束，就返回当前已匹配的前缀。

    【思考过程】
    1. 以第一个字符串为基准，逐个字符地和其他所有字符串比较。

    2. 在第 col 列时，如果某个字符串 strs[i] 的长度 <= col，
       或者 strs[i][col] != strs[0][col]，说明公共前缀到此为止。

    3. 如果所有字符串都匹配了 strs[0] 的全部字符，返回 strs[0]。

    【举例】strs = ["flower", "flow", "flight"]
      col=0: 'f','f','f' → 全部匹配
      col=1: 'l','l','l' → 全部匹配
      col=2: 'o','o','i' → 不匹配！返回 "fl"

    【时间复杂度】O(S)，S 是所有字符串的字符总数（最坏情况全部相同）
    【空间复杂度】O(1)
    """
    def longest_common_prefix_vertical(self, strs: List[str]) -> str:
        if not strs:
            return ""

        for col in range(len(strs[0])):
            c = strs[0][col]
            for i in range(1, len(strs)):
                if col >= len(strs[i]) or strs[i][col] != c:
                    return strs[0][:col]
        return strs[0]

    """
    ==================== 解法二：横向扫描 ====================

    【核心思路】
    先把第一个字符串作为初始前缀 prefix，然后逐个与后续字符串比较，
    不断缩短 prefix 直到它成为当前字符串的前缀，再继续下一个字符串。

    【思考过程】
    1. LCP(s1, s2, s3) = LCP(LCP(s1, s2), s3)，公共前缀满足结合律。

    2. 用 prefix = strs[0]，然后依次与 strs[1], strs[2], ... 比较。
       每次比较时，如果 strs[i] 不以 prefix 开头，就把 prefix 缩短一个字符，
       重复直到匹配或 prefix 变为空。

    3. 如果中途 prefix 变为空，直接返回 ""。

    【举例】strs = ["flower", "flow", "flight"]
      prefix = "flower"
      与 "flow" 比较: "flower" 不是前缀 → "flowe" → "flow" → 匹配！prefix="flow"
      与 "flight" 比较: "flow" 不是前缀 → "flo" → "fl" → 匹配！prefix="fl"
      返回 "fl"

    【时间复杂度】O(S)
    【空间复杂度】O(1)
    """
    def longest_common_prefix_horizontal(self, strs: List[str]) -> str:
        if not strs:
            return ""

        prefix = strs[0]
        for i in range(1, len(strs)):
            while not strs[i].startswith(prefix):
                prefix = prefix[:-1]
                if not prefix:
                    return ""
        return prefix

    """
    ==================== 解法三：排序法 ====================

    【核心思路】
    对字符串数组按字典序排序后，只需要比较排序后的第一个和最后一个字符串。
    因为字典序最小和最大的字符串之间的公共前缀，就是所有字符串的公共前缀。

    【思考过程】
    1. 排序后，字符串按字典序排列。如果所有字符串有公共前缀 P，
       那么排序后第一个和最后一个也一定以 P 开头。

    2. 反过来，如果第一个和最后一个的公共前缀是 P，那么中间的所有字符串
       （字典序介于两者之间）也一定以 P 开头。

    3. 所以排序后只需比较 first 和 last 两个字符串，找它们的公共前缀即可。

    【举例】strs = ["flower", "flow", "flight"]
      排序后: ["flight", "flow", "flower"]
      比较 "flight" 和 "flower":
        col=0: 'f'='f' ✓
        col=1: 'l'='l' ✓
        col=2: 'i'≠'o' ✗ → 返回 "fl"

    【时间复杂度】O(S·log n)，排序 O(n·m·log n)，比较 O(m)
    【空间复杂度】O(1) 不计排序使用的空间
    """
    def longest_common_prefix_sort(self, strs: List[str]) -> str:
        if not strs:
            return ""

        strs.sort()
        first, last = strs[0], strs[-1]
        col = 0
        while col < len(first) and col < len(last) and first[col] == last[col]:
            col += 1
        return first[:col]

    """
    ==================== 解法四：分治法 ====================

    【核心思路】
    将字符串数组拆成左右两半，递归求各半的最长公共前缀，
    再合并两个前缀（取两者的公共前缀）。

    【思考过程】
    1. LCP(s1..sn) = LCP( LCP(s1..s_{n/2}), LCP(s_{n/2+1}..sn) )
       这是分治的典型模式：拆分、递归、合并。

    2. 基准情况：只有一个字符串时，它自身就是"公共前缀"。

    3. 合并时，两个前缀的公共前缀就是逐字符比较。

    4. 递归深度 O(log n)，每层处理的字符总数最多 O(S)，
       但由于前缀可能很短，实际效率取决于具体输入。

    【举例】strs = ["flower", "flow", "flight", "fly"]
      左半 ["flower","flow"]  → LCP = "flow"
      右半 ["flight","fly"]   → LCP = "fl"
      合并 LCP("flow","fl")   → "fl"

    【时间复杂度】O(S·log n)，S 是所有字符总数
    【空间复杂度】O(m·log n)，m 是最短字符串长度，递归栈深度 log n
    """
    def longest_common_prefix_divide(self, strs: List[str]) -> str:
        if not strs:
            return ""
        return self._divide_and_conquer(strs, 0, len(strs) - 1)

    def _divide_and_conquer(self, strs: List[str], left: int, right: int) -> str:
        if left == right:
            return strs[left]

        mid = (left + right) // 2
        lcp_left = self._divide_and_conquer(strs, left, mid)
        lcp_right = self._divide_and_conquer(strs, mid + 1, right)

        min_len = min(len(lcp_left), len(lcp_right))
        for i in range(min_len):
            if lcp_left[i] != lcp_right[i]:
                return lcp_left[:i]
        return lcp_left[:min_len]
