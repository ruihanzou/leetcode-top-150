/**
 * LeetCode 28. Find the Index of the First Occurrence in a String
 * 难度: Easy
 *
 * 题目描述：
 * 给定两个字符串 haystack 和 needle，在 haystack 中找出 needle 字符串的
 * 第一个匹配项的下标。如果 needle 不在 haystack 中，返回 -1。
 *
 * 示例：haystack = "sadbutsad", needle = "sad" → 输出 0
 *
 * 【拓展练习】
 * 1. LeetCode 459. Repeated Substring Pattern —— 利用 KMP 前缀函数判断重复子串
 * 2. LeetCode 214. Shortest Palindrome —— KMP 前缀函数的巧妙应用
 * 3. LeetCode 686. Repeated String Match —— 字符串匹配的变体
 */

class FindTheIndex028 {

    /**
     * ==================== 解法一：暴力匹配 ====================
     *
     * 【核心思路】
     * 逐个位置尝试匹配：对 haystack 中的每个可能起始位置 i，
     * 检查从 i 开始的子串是否与 needle 完全相同。
     *
     * 【思考过程】
     * 1. 最朴素的想法：枚举起始位置 i = 0, 1, ..., n-m，
     *    对每个 i 逐字符比较 haystack[i..i+m-1] 和 needle[0..m-1]。
     *
     * 2. 如果某个位置字符不匹配，立刻放弃这个起始位置，尝试下一个。
     *
     * 3. 第一个完全匹配的位置就是答案。遍历完没找到则返回 -1。
     *
     * 4. 虽然最坏情况是 O(n*m)（如 haystack="aaaa...ab", needle="aaa...ab"），
     *    但在实际字符串（字符种类较多）中平均表现接近 O(n)。
     *
     * 【举例】haystack = "sadbutsad", needle = "sad"
     *   i=0: s==s, a==a, d==d → 完全匹配，返回 0
     *
     * 【时间复杂度】O(n * m) 最坏情况，n = len(haystack), m = len(needle)
     * 【空间复杂度】O(1)
     */
    public int strStrBrute(String haystack, String needle) {
        int n = haystack.length();
        int m = needle.length();

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && haystack.charAt(i + j) == needle.charAt(j)) {
                j++;
            }
            if (j == m) {
                return i;
            }
        }

        return -1;
    }

    /**
     * ==================== 解法二：KMP 算法 ====================
     *
     * 【核心思路】
     * KMP（Knuth-Morris-Pratt）算法通过预处理 needle 构建前缀函数（失败函数），
     * 在匹配失败时利用已匹配的信息跳过不必要的比较，实现线性时间匹配。
     *
     * 【思考过程】
     * 1. 暴力匹配的浪费在于：匹配失败后，起始位置只前进 1 步，
     *    而已经比较过的信息被完全丢弃。
     *
     * 2. KMP 的关键观察：如果 needle[0..j-1] 已经匹配了 haystack 的某段，
     *    而 needle[j] 匹配失败，那么我们可以利用 needle 自身的结构
     *    （前缀和后缀的重叠）来决定 needle 应该回退到哪个位置继续匹配，
     *    而不是从头开始。
     *
     * 3. 前缀函数 lps[i] 定义：needle[0..i] 的最长"真前缀等于真后缀"的长度。
     *    例如 "abab" 的 lps = [0, 0, 1, 2]，因为 "abab" 的最长
     *    真前缀 = 真后缀 是 "ab"，长度 2。
     *
     * 4. 构建前缀函数的过程本身也是一种自我匹配，时间复杂度 O(m)。
     *
     * 5. 匹配时，i 指向 haystack（只前进不后退），j 指向 needle：
     *    - 匹配成功：i++, j++
     *    - 匹配失败：j 回退到 lps[j-1]（j==0 时 i++）
     *    - j == m 时找到匹配
     *
     * 【举例】haystack = "aabaabaaf", needle = "aabaaf"
     *   构建 lps:
     *     needle = a  a  b  a  a  f
     *     lps    = 0  1  0  1  2  0
     *
     *   匹配过程:
     *     haystack: a a b a a b a a f
     *     i=0,j=0: a==a ✓  i=1,j=1
     *     i=1,j=1: a==a ✓  i=2,j=2
     *     i=2,j=2: b==b ✓  i=3,j=3
     *     i=3,j=3: a==a ✓  i=4,j=4
     *     i=4,j=4: a==a ✓  i=5,j=5
     *     i=5,j=5: b!=f ✗  j=lps[4]=2
     *     i=5,j=2: b==b ✓  i=6,j=3
     *     i=6,j=3: a==a ✓  i=7,j=4
     *     i=7,j=4: a==a ✓  i=8,j=5
     *     i=8,j=5: f==f ✓  j=6==m → 返回 i-m+1 = 3
     *
     * 【时间复杂度】O(n + m) 构建前缀函数 O(m)，匹配 O(n)
     * 【空间复杂度】O(m) 前缀函数数组
     */
    public int strStrKMP(String haystack, String needle) {
        int n = haystack.length();
        int m = needle.length();

        int[] lps = buildLPS(needle);

        int j = 0;
        for (int i = 0; i < n; i++) {
            while (j > 0 && haystack.charAt(i) != needle.charAt(j)) {
                j = lps[j - 1];
            }
            if (haystack.charAt(i) == needle.charAt(j)) {
                j++;
            }
            if (j == m) {
                return i - m + 1;
            }
        }

        return -1;
    }

    private int[] buildLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len > 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}
