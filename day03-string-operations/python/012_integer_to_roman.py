"""
LeetCode 12. Integer to Roman
难度: Medium

题目描述：
给定一个整数 num（1 ≤ num ≤ 3999），将其转换为罗马数字。

示例：num = 1994 → 输出 "MCMXCIV"

【拓展练习】
1. LeetCode 13. Roman to Integer —— 罗马数字转整数（反向操作）
2. LeetCode 273. Integer to English Words —— 整数转英文表示
"""


class Solution:
    """
    ==================== 解法一：贪心映射表 ====================

    【核心思路】
    建立包含 13 个值-符号对的映射表（从大到小：1000, 900, 500, 400, ...）。
    从最大的值开始，尽可能多地使用当前符号，然后转到下一个更小的值。

    【思考过程】
    1. 罗马数字本质是一种"贪心"表示：优先使用大面额的符号。
       类似于用尽可能少的纸币凑齐金额。

    2. 除了 7 个基本符号外，还有 6 个特殊组合（如 CM=900），
       把它们也放进映射表，就不需要额外处理减法规则。

    3. 对于当前值 val，while num >= val 时不断追加对应符号并减去 val。

    【举例】num = 1994
      1000: 1994>=1000, 追加"M", num=994
      900:  994>=900,   追加"CM", num=94
      500:  94<500,     跳过
      400:  94<400,     跳过
      100:  94<100,     跳过
      90:   94>=90,     追加"XC", num=4
      50:   4<50,       跳过
      ...
      4:    4>=4,       追加"IV", num=0
      结果: "MCMXCIV"

    【时间复杂度】O(1) num 最大 3999，循环次数有上界（约15次）
    【空间复杂度】O(1) 映射表大小固定
    """
    def int_to_roman_greedy(self, num: int) -> str:
      # why not use a dictionary? 
      # because the dictionary is not ordered, 
      # and the order is important for the roman numerals.
        pairs = [
            (1000, 'M'), (900, 'CM'), (500, 'D'), (400, 'CD'),
            (100, 'C'),  (90, 'XC'),  (50, 'L'),  (40, 'XL'),
            (10, 'X'),   (9, 'IX'),   (5, 'V'),   (4, 'IV'), (1, 'I')
        ]
        result = []
        for val, sym in pairs:
            while num >= val:
                result.append(sym)
                num -= val
        return ''.join(result)

    """
    ==================== 解法二：硬编码法 ====================

    【核心思路】
    把千位、百位、十位、个位各自可能的罗马表示全部列出。
    取出 num 的各个数位，直接查表拼接。

    【思考过程】
    1. num 最大 3999，所以千位只有 0~3，百位、十位、个位各有 0~9。

    2. 每个数位的罗马表示是固定的：
       - 千位: "", "M", "MM", "MMM"
       - 百位: "", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"
       - 十位: "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"
       - 个位: "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"

    3. 直接索引 thousands[num//1000] + hundreds[(num%1000)//100] + ... 即可。

    【举例】num = 1994
      千位 1994//1000 = 1 → "M"
      百位 994//100 = 9   → "CM"
      十位 94//10 = 9     → "XC"
      个位 4%10 = 4       → "IV"
      结果: "MCMXCIV"

    【时间复杂度】O(1) 纯查表
    【空间复杂度】O(1) 固定大小的查找表
    """
    def int_to_roman_hardcoded(self, num: int) -> str:
        thousands = ['', 'M', 'MM', 'MMM']
        hundreds  = ['', 'C', 'CC', 'CCC', 'CD', 'D', 'DC', 'DCC', 'DCCC', 'CM']
        tens      = ['', 'X', 'XX', 'XXX', 'XL', 'L', 'LX', 'LXX', 'LXXX', 'XC']
        ones      = ['', 'I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX']

        return (thousands[num // 1000]
                + hundreds[(num % 1000) // 100]
                + tens[(num % 100) // 10]
                + ones[num % 10])
