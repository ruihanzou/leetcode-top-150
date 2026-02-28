"""
LeetCode 58. Length of Last Word
难度: Easy

题目描述：
给定一个字符串 s，由若干单词和空格组成，返回最后一个单词的长度。
单词是仅由字母组成的最大子串。

示例：s = "Hello World" → 输出 5

【拓展练习】
1. LeetCode 557. Reverse Words in a String III —— 翻转每个单词中的字符
2. LeetCode 151. Reverse Words in a String —— 翻转字符串中的单词顺序
"""


class Solution:
    """
    ==================== 解法一：从后往前遍历 ====================

    【核心思路】
    从字符串末尾开始，先跳过末尾的所有空格，然后计数非空格字符直到遇到空格或到达开头。

    【思考过程】
    1. 我们只关心最后一个单词，所以从后往前找最高效。

    2. 末尾可能有空格（如 "Hello World   "），需要先跳过。

    3. 跳过空格后，从当前位置往前数，直到再次遇到空格或到达字符串开头，
       计数值就是最后一个单词的长度。

    【举例】s = "   fly me   to   the moon  "
      从后往前跳过2个空格，到 'n'
      计数: n,o,o,m = 4个字符，遇到空格停止
      返回 4

    【时间复杂度】O(n) 最坏情况遍历整个字符串
    【空间复杂度】O(1)
    """
    def length_of_last_word_reverse(self, s: str) -> int:
        i = len(s) - 1

        while i >= 0 and s[i] == ' ':
            i -= 1

        length = 0
        while i >= 0 and s[i] != ' ':
            length += 1
            i -= 1
        return length

    """
    ==================== 解法二：split 分割 ====================

    【核心思路】
    使用语言内置的 split 方法按空格分割字符串，取最后一个非空元素的长度。

    【思考过程】
    1. Python 的 str.split()（不传参数）会按任意空白符分割，
       并自动忽略首尾空白和连续空白。

    2. 分割后直接取最后一个元素 [-1] 的长度即可。

    3. 代码极其简洁，面试中可以作为快速解法，但要说明 split 的实现本身是 O(n)。

    【举例】s = "Hello World"
      split() → ["Hello", "World"]
      最后一个元素 "World"，长度 5

    【时间复杂度】O(n) split 需要遍历整个字符串
    【空间复杂度】O(n) 存储分割后的列表
    """
    def length_of_last_word_split(self, s: str) -> int:
        return len(s.split()[-1])

    """
    ==================== 解法三：rstrip + rfind ====================

    【核心思路】
    先 rstrip 去除末尾空格，然后用 rfind 找最后一个空格的位置，
    最后一个单词的长度 = 总长度 - 最后一个空格位置 - 1。

    【思考过程】
    1. rstrip 后字符串不会以空格结尾，所以最后一个空格之后的所有字符
       就是最后一个单词。

    2. rfind(' ') 返回最后一个空格的下标。
       如果没有空格（整个字符串就是一个单词），返回 -1，
       此时长度 = len - (-1) - 1 = len，正确。

    【举例】s = "Hello World"
      rstrip 后仍是 "Hello World"，长度 11
      rfind(' ') = 5
      长度 = 11 - 5 - 1 = 5

    【时间复杂度】O(n)
    【空间复杂度】O(n) rstrip 创建新字符串
    """
    def length_of_last_word_rstrip(self, s: str) -> int:
        s = s.rstrip()
        return len(s) - s.rfind(' ') - 1
