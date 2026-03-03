"""
LeetCode 125. Valid Palindrome
难度: Easy

题目描述：
如果在将所有大写字符转换为小写字符、并移除所有非字母数字字符之后，
短语正着读和反着读都一样，则可以认为该短语是一个回文串。
字母和数字都属于字母数字字符。
给你一个字符串 s，如果它是回文串，返回 true；否则，返回 false。

示例 1：s = "A man, a plan, a canal: Panama" → 输出 true
  解释："amanaplanacanalpanama" 是回文串。
示例 2：s = "race a car" → 输出 false
  解释："raceacar" 不是回文串。
示例 3：s = " " → 输出 true
  解释：在移除非字母数字字符之后，s 是一个空字符串 ""。空字符串正着读和反着读都一样。

【拓展练习】
1. LeetCode 680. Valid Palindrome II —— 允许删除一个字符后判断回文
2. LeetCode 234. Palindrome Linked List —— 链表版本的回文判断
3. LeetCode 9. Palindrome Number —— 判断整数是否为回文数
"""


class Solution:
    """
    ==================== 解法一：双指针（原地判断） ====================

    【核心思路】
    使用左右双指针从字符串两端向中间收缩，跳过非字母数字字符，
    将当前字符转为小写后逐一比较。一旦发现不匹配立即返回 False。

    【思考过程】
    1. 回文串的特征：首尾对应字符相同。自然想到双指针从两端向中间逼近。
    2. 题目要求忽略非字母数字字符 → 遇到时直接跳过（指针前进）。
    3. 题目要求忽略大小写 → 比较时统一转为小写。
    4. 这种方法不需要额外空间存储过滤后的字符串，是最优解。

    【举例】s = "A man, a plan, a canal: Panama"
      left=0('A'), right=29('a') → 'a'=='a' ✓
      left=1(' ') 跳过 → left=2('m'), right=28('m') → 'm'=='m' ✓
      ... 逐一匹配直到 left >= right，返回 True

    【时间复杂度】O(n)，每个字符最多访问一次
    【空间复杂度】O(1)，仅用两个指针变量
    """
    def isPalindrome_two_pointer(self, s: str) -> bool:
        left, right = 0, len(s) - 1
        while left < right:
            while left < right and not s[left].isalnum():
                left += 1
            while left < right and not s[right].isalnum():
                right -= 1
            if s[left].lower() != s[right].lower():
                return False
            left += 1
            right -= 1
        return True

    """
    ==================== 解法二：预处理 + 反转比较 ====================

    【核心思路】
    先将字符串中的字母数字字符提取出来并转小写，形成一个"干净"的字符串，
    然后判断该字符串是否等于其反转。

    【思考过程】
    1. 回文串 = 正着读和反着读一样 → 最直接的想法就是反转后比较。
    2. 但原始字符串有特殊字符和大小写问题，所以先做预处理。
    3. Python 中切片 [::-1] 可以轻松反转字符串。
    4. 代码非常简洁，但需要额外 O(n) 空间。

    【举例】s = "A man, a plan, a canal: Panama"
      过滤 → "amanaplanacanalpanama"
      反转 → "amanaplanacanalpanama"
      相等 → 返回 True

    【时间复杂度】O(n)，遍历一次 + 反转一次
    【空间复杂度】O(n)，存储过滤后的字符串
    """
    def isPalindrome_reverse(self, s: str) -> bool:
        filtered = ''.join(c.lower() for c in s if c.isalnum())
        return filtered == filtered[::-1]

    """
    ==================== 解法三：预处理 + 双指针 ====================

    【核心思路】
    先将字符串过滤为仅含小写字母数字的新字符串，再在新字符串上使用标准双指针判断回文。

    【思考过程】
    1. 解法一在原字符串上跳过非法字符，逻辑略复杂。
    2. 如果先把字符串"清洗"干净，双指针逻辑就变得非常简单：
       只需比较 cleaned[left] 和 cleaned[right] 即可。
    3. 代价是多用了 O(n) 空间，但代码更易读。

    【举例】s = "race a car"
      过滤 → "raceacar"
      left=0('r'), right=7('r') → ✓
      left=1('a'), right=6('a') → ✓
      left=2('c'), right=5('c') → ✓
      left=3('e'), right=4('a') → ✗ → 返回 False

    【时间复杂度】O(n)，遍历一次过滤 + 一次双指针
    【空间复杂度】O(n)，存储过滤后的字符串
    """
    def isPalindrome_clean_two_pointer(self, s: str) -> bool:
        cleaned = ''.join(c.lower() for c in s if c.isalnum())
        left, right = 0, len(cleaned) - 1
        while left < right:
            if cleaned[left] != cleaned[right]:
                return False
            left += 1
            right -= 1
        return True
