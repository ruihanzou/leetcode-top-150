"""
LeetCode 9. Palindrome Number
难度: Easy

题目描述：
给你一个整数 x，如果 x 是一个回文整数，返回 true；否则，返回 false。
回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
例如，121 是回文，而 123 不是。

示例：x = 121 → true，x = -121 → false，x = 10 → false

【拓展练习】
1. LeetCode 7. Reverse Integer —— 整数反转，注意溢出处理
2. LeetCode 234. Palindrome Linked List —— 链表的回文判断
3. LeetCode 680. Valid Palindrome II —— 允许删除一个字符的回文判断
"""


class Solution:
    """
    ==================== 解法一：反转一半数字 ====================

    【核心思路】
    将数字的后半段反转，与前半段比较。
    如果相等（偶数位）或反转数//10等于前半段（奇数位），就是回文。

    【思考过程】
    1. 负数一定不是回文（有负号）。以 0 结尾的非零数也不是回文。

    2. 如果完全反转整个数字，可能溢出（在某些语言中）。
       → 只反转后半段，这样最多只到原数的一半大小，不会溢出。

    3. 怎么知道已经反转了一半？
       → 当反转后的数 >= 剩余的前半段时，说明已经到达或超过中点。

    4. 偶数位数：反转后的数 == 前半段，如 1221 → 前12，后12。
       奇数位数：反转后的数多一位（中间那位），需要 //10 去掉，
       如 12321 → 前12，后123，123//10=12。

    【举例】x = 12321
      初始: x=12321, reversed=0
      第1轮: reversed=1,   x=1232   (1232>1, 继续)
      第2轮: reversed=12,  x=123    (123>12, 继续)
      第3轮: reversed=123, x=12     (12<123, 停止)
      检查: x == reversed//10 → 12 == 123//10=12 ✓ → True

    【时间复杂度】O(log n) —— 只处理一半的位数
    【空间复杂度】O(1)
    """
    def isPalindrome_half(self, x: int) -> bool:
        if x < 0 or (x % 10 == 0 and x != 0):
            return False

        reversed_half = 0
        while x > reversed_half:
            reversed_half = reversed_half * 10 + x % 10
            x //= 10

        return x == reversed_half or x == reversed_half // 10

    """
    ==================== 解法二：转字符串 + 双指针 ====================

    【核心思路】
    将整数转为字符串，用左右双指针向中间收拢，逐一比较字符。

    【思考过程】
    1. 转字符串后，回文判断变成经典的双指针问题。

    2. 左指针从头、右指针从尾，每次比较对应字符，
       不相等就返回 False，全部匹配则返回 True。

    3. Python 中更简洁的写法：s == s[::-1]，直接反转比较。
       但双指针写法更通用，面试中展示算法思维更好。

    【举例】x = 12321 → s = "12321"
      left=0, right=4: '1'=='1' ✓
      left=1, right=3: '2'=='2' ✓
      left=2, right=2: 相遇，结束 → True

    【时间复杂度】O(log n) —— 字符串长度为 O(log n)
    【空间复杂度】O(log n) —— 存储字符串
    """
    def isPalindrome_string(self, x: int) -> bool:
        s = str(x)
        left, right = 0, len(s) - 1

        while left < right:
            if s[left] != s[right]:
                return False
            left += 1
            right -= 1

        return True
