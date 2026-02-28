"""
LeetCode 151. Reverse Words in a String
难度: Medium

题目描述：
给定一个字符串 s，将字符串中的单词顺序反转。单词是由非空格字符组成的字符串。
s 中至少存在一个单词。输入字符串可能在前面和后面包含多余的空格。
两个单词之间可能有多余的空格。返回的字符串中单词之间应该只有一个空格。

示例：s = "  hello world  " → 输出 "world hello"

【拓展练习】
1. LeetCode 186. Reverse Words in a String II —— 原地翻转（输入为 char[]）
2. LeetCode 557. Reverse Words in a String III —— 翻转每个单词中的字符，但保持单词顺序
3. LeetCode 58. Length of Last Word —— 从后往前扫描提取最后一个单词
"""


class Solution:
    """
    ==================== 解法一：split + reverse ====================

    【核心思路】
    利用 Python 内置的 split() 按空格分割字符串得到单词列表，
    反转列表后用单个空格拼接。

    【思考过程】
    1. Python 的 str.split()（不传参数）会自动按任意空白符分割，
       并忽略前后及中间的多余空格，非常方便。
    2. 得到单词列表后，用切片 [::-1] 或 reversed() 反转，
       再用 ' '.join() 拼接即可。

    【举例】s = "  hello world  "
      split() → ["hello", "world"]
      反转 → ["world", "hello"]
      join → "world hello"

    【时间复杂度】O(n)
    【空间复杂度】O(n)
    """
    def reverseWords_split(self, s: str) -> str:
        return ' '.join(s.split()[::-1])

    """
    ==================== 解法二：双指针从后往前提取单词 ====================

    【核心思路】
    不使用 split，而是用双指针从字符串末尾开始扫描，
    逐个定位单词的右边界和左边界，提取后追加到结果列表中。

    【思考过程】
    1. 从右往左扫描，跳过空格，找到当前单词的右边界 right。
    2. 继续往左走直到遇到空格或到达开头，此时左边界确定。
    3. 截取子串就是一个完整单词，追加到结果列表。
    4. 重复直到扫描完整个字符串，最后用空格拼接。
    5. 这种做法不依赖语言的 split 函数，展示了手动解析字符串的思路。

    【举例】s = "  hello world  "
      i 从 14 开始往左:
      跳过空格 → i=12, right=12
      扫描字母 → i=7, 提取 s[8:13] = "world"
      跳过空格 → i=5, right=5
      扫描字母 → i=-1, 提取 s[0:6] → "hello" (实际 strip 后)
      结果: "world hello"

    【时间复杂度】O(n)
    【空间复杂度】O(n)
    """
    def reverseWords_two_pointer(self, s: str) -> str:
        result = []
        n = len(s)
        i = n - 1

        while i >= 0:
            while i >= 0 and s[i] == ' ':
                i -= 1
            if i < 0:
                break

            right = i
            while i >= 0 and s[i] != ' ':
                i -= 1

            result.append(s[i + 1: right + 1])

        return ' '.join(result)

    """
    ==================== 解法三：整体翻转 + 逐词翻转 + 清理空格 ====================

    【核心思路】
    经典的"三步翻转法"：
    1. 翻转整个字符串 → 单词顺序反转了，但每个单词内部字符也反了。
    2. 翻转每个单词 → 单词内部恢复正常。
    3. 清理多余空格 → 去除前后空格，单词间仅保留一个空格。

    Python 字符串不可变，需要转成 list 模拟原地操作。

    【思考过程】
    1. "反转单词顺序"可以拆解为两步操作的叠加：
       整体翻转把最后一个单词移到最前面，但单词本身也被翻了；
       再逐个单词翻转，就把单词内部恢复正确。
    2. 这是一种经典技巧，常用于矩阵旋转、数组循环移位等场景。
    3. 多余空格的处理：用一个写指针做原地去重。

    【举例】s = "  hello world  "
      转 list → [' ',' ','h','e','l','l','o',' ','w','o','r','l','d',' ',' ']
      步骤1 整体翻转 → "  dlrow olleh  "
      步骤2 逐词翻转 → "  world hello  "
      步骤3 清理空格 → "world hello"

    【时间复杂度】O(n)
    【空间复杂度】O(n) Python 字符串不可变，需要 list
    """
    def reverseWords_in_place(self, s: str) -> str:
        arr = list(s)
        n = len(arr)

        def reverse(left: int, right: int) -> None:
            while left < right:
                arr[left], arr[right] = arr[right], arr[left]
                left += 1
                right -= 1

        reverse(0, n - 1)

        start = 0
        for j in range(n + 1):
            if j == n or arr[j] == ' ':
                reverse(start, j - 1)
                start = j + 1

        write = 0
        j = 0
        while j < n:
            if arr[j] != ' ':
                if write > 0:
                    arr[write] = ' '
                    write += 1
                while j < n and arr[j] != ' ':
                    arr[write] = arr[j]
                    write += 1
                    j += 1
            else:
                j += 1

        return ''.join(arr[:write])
