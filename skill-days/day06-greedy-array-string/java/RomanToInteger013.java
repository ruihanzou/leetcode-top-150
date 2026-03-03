/**
 * LeetCode 13. Roman to Integer
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个罗马数字字符串 s，将其转换为整数。
 * 罗马数字规则：I=1, V=5, X=10, L=50, C=100, D=500, M=1000。
 * 特殊规则：IV=4, IX=9, XL=40, XC=90, CD=400, CM=900。
 *
 * 示例：s = "MCMXCIV" → 输出 1994
 *
 * 【拓展练习】
 * 1. LeetCode 12. Integer to Roman —— 整数转罗马数字（反向操作）
 * 2. LeetCode 273. Integer to English Words —— 整数转英文表示
 */

import java.util.HashMap;
import java.util.Map;

class RomanToInteger013 {

    /**
     * ==================== 解法一：逐字符判断（左到右） ====================
     *
     * 【核心思路】
     * 从左到右遍历，如果当前字符代表的值 < 下一个字符的值，说明是特殊组合（如 IV），
     * 此时减去当前值；否则加上当前值。
     *
     * 【思考过程】
     * 1. 罗马数字通常是从大到小排列的，如 "XVI" = 10+5+1 = 16。
     *    当出现小的在大的左边时，是特殊组合，需要减去小值。
     *
     * 2. 遍历到位置 i 时，比较 value(s[i]) 和 value(s[i+1])：
     *    - 如果 value(s[i]) < value(s[i+1])：result -= value(s[i])
     *    - 否则：result += value(s[i])
     *
     * 3. 最后一个字符一定是加上的（没有下一个字符可比较）。
     *
     * 【举例】s = "MCMXCIV"
     *   M(1000): 1000 >= C(100)? 是 → +1000, result=1000
     *   C(100):  100 >= M(1000)? 否 → -100,  result=900
     *   M(1000): 1000 >= X(10)?  是 → +1000, result=1900
     *   X(10):   10 >= C(100)?   否 → -10,   result=1890
     *   C(100):  100 >= I(1)?    是 → +100,  result=1990
     *   I(1):    1 >= V(5)?      否 → -1,    result=1989
     *   V(5):    最后一个        → +5,    result=1994
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int romanToIntLeftToRight(String s) {
        Map<Character, Integer> map = Map.of(
            'I', 1, 'V', 5, 'X', 10, 'L', 50,
            'C', 100, 'D', 500, 'M', 1000
        );

        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            int cur = map.get(s.charAt(i));
            if (i + 1 < s.length() && cur < map.get(s.charAt(i + 1))) {
                result -= cur;
            } else {
                result += cur;
            }
        }
        return result;
    }

    /**
     * ==================== 解法二：替换法 ====================
     *
     * 【核心思路】
     * 先把六种特殊组合（IV, IX, XL, XC, CD, CM）替换成不冲突的单字符标记，
     * 然后逐字符查表累加即可。
     *
     * 【思考过程】
     * 1. 特殊组合的存在使得简单累加不对。如果能消除特殊组合，
     *    问题就退化为纯粹的逐字符查表求和。
     *
     * 2. 把 "IV" 替换为某个标记（如 "a"），并在映射表中令 'a'=4。
     *    同理 "IX"→"b"=9, "XL"→"c"=40, "XC"→"d"=90, "CD"→"e"=400, "CM"→"f"=900。
     *
     * 3. 替换后字符串中所有字符都可以直接查表累加。
     *
     * 【举例】s = "MCMXCIV"
     *   替换 CM→f: "MfXCIV"
     *   替换 XC→d: "MfdIV"
     *   替换 IV→a: "Mfda"
     *   逐字符: M(1000) + f(900) + d(90) + a(4) = 1994
     *
     * 【时间复杂度】O(n) 替换和遍历各 O(n)
     * 【空间复杂度】O(n) 替换后的新字符串
     */
    public int romanToIntReplace(String s) {
        s = s.replace("IV", "a").replace("IX", "b")
             .replace("XL", "c").replace("XC", "d")
             .replace("CD", "e").replace("CM", "f");

        int result = 0;
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case 'I': result += 1;    break;
                case 'V': result += 5;    break;
                case 'X': result += 10;   break;
                case 'L': result += 50;   break;
                case 'C': result += 100;  break;
                case 'D': result += 500;  break;
                case 'M': result += 1000; break;
                case 'a': result += 4;    break;
                case 'b': result += 9;    break;
                case 'c': result += 40;   break;
                case 'd': result += 90;   break;
                case 'e': result += 400;  break;
                case 'f': result += 900;  break;
            }
        }
        return result;
    }

    /**
     * ==================== 解法三：从右到左遍历 ====================
     *
     * 【核心思路】
     * 从右往左遍历，维护"前一个（右边）字符的值"prev。
     * 如果当前值 < prev，说明是特殊组合的左半部分，减去当前值；否则加上。
     *
     * 【思考过程】
     * 1. 解法一是向右看下一个字符来判断，解法三是向左看已经处理的字符。
     *    逻辑是等价的，只是遍历方向不同。
     *
     * 2. 从右往左时，当前值 < 已处理的前值 prev，说明当前字符是被减去的。
     *    例如 "IV" 从右往左：先处理 V(+5)，再处理 I，发现 1 < 5，所以 -1。
     *
     * 3. 每次更新 prev = 当前值。
     *
     * 【举例】s = "MCMXCIV"（从右往左）
     *   V(5):    prev=0, 5>=0  → +5,    result=5,    prev=5
     *   I(1):    prev=5, 1<5   → -1,    result=4,    prev=1
     *   C(100):  prev=1, 100>=1 → +100, result=104,  prev=100
     *   X(10):   prev=100, 10<100 → -10, result=94,  prev=10
     *   M(1000): prev=10, 1000>=10 → +1000, result=1094, prev=1000
     *   C(100):  prev=1000, 100<1000 → -100, result=994, prev=100
     *   M(1000): prev=100, 1000>=100 → +1000, result=1994, prev=1000
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int romanToIntRightToLeft(String s) {
        Map<Character, Integer> map = Map.of(
            'I', 1, 'V', 5, 'X', 10, 'L', 50,
            'C', 100, 'D', 500, 'M', 1000
        );

        int result = 0;
        int prev = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int cur = map.get(s.charAt(i));
            if (cur < prev) {
                result -= cur;
            } else {
                result += cur;
            }
            prev = cur;
        }
        return result;
    }
}
