/**
 * LeetCode 5. Longest Palindromic Substring
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个字符串 s，找到 s 中最长的回文子串。
 *
 * 示例 1：
 * 输入：s = "babad"
 * 输出："bab"（"aba" 也是有效答案）
 *
 * 示例 2：
 * 输入：s = "cbbd"
 * 输出："bb"
 *
 * 【拓展练习】
 * 1. LeetCode 647. Palindromic Substrings —— 统计回文子串的数量
 * 2. LeetCode 516. Longest Palindromic Subsequence —— 最长回文子序列（dp）
 * 3. LeetCode 214. Shortest Palindrome —— 在字符串前面添加字符构成最短回文串（Hard）
 */

class LongestPalindrome005 {

    /**
     * ==================== 解法一：中心扩展 ====================
     *
     * 【核心思路】
     * 回文串一定有一个"中心"。遍历所有可能的中心（单字符中心和双字符中心），
     * 从中心向两边扩展，找到以该中心为对称轴的最长回文。
     *
     * 【思考过程】
     * 1. 回文有两种形态：奇数长度（如 "aba"，中心是 'b'）和
     *    偶数长度（如 "abba"，中心是 "bb" 之间的间隙）。
     *
     * 2. 对于长度为 n 的字符串，有 n 个单字符中心和 n-1 个双字符中心，共 2n-1 个。
     *
     * 3. 对每个中心，用双指针 left/right 向两边扩展，
     *    只要 s[left] == s[right] 就继续。扩展完毕记录最长的。
     *
     * 4. 这种方法直观且常数因子小，是面试中最推荐的解法。
     *
     * 【举例】s = "babad"
     *   中心 'b'(0): 扩展 → "b"，长度1
     *   中心 'a'(1): 扩展 → "bab"，长度3 ← 记录
     *   中心 'b'(2): 扩展 → "aba"，长度3
     *   中心 'a'(3): 扩展 → "a"，长度1
     *   中心 'd'(4): 扩展 → "d"，长度1
     *   双字符中心均不成回文
     *   答案 "bab"
     *
     * 【时间复杂度】O(n²)，每个中心最多扩展 O(n)
     * 【空间复杂度】O(1)
     */
    public String longestPalindrome(String s) {
        int start = 0, maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            int len1 = expand(s, i, i);
            int len2 = expand(s, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }

        return s.substring(start, start + maxLen);
    }

    private int expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    /**
     * ==================== 解法二：动态规划 ====================
     *
     * 【核心思路】
     * dp[i][j] 表示 s[i..j] 是否为回文。
     * 转移方程：dp[i][j] = (s[i] == s[j]) && dp[i+1][j-1]。
     * 遍历所有子串，记录最长的回文子串。
     *
     * 【思考过程】
     * 1. 判断 s[i..j] 是否回文：首尾字符相同，且去掉首尾后 s[i+1..j-1] 也是回文。
     *
     * 2. 基本情况：单字符 dp[i][i] = true；
     *    双字符 dp[i][i+1] = (s[i] == s[i+1])。
     *
     * 3. 枚举长度从 3 到 n，对于每个长度枚举起点 i，终点 j = i + len - 1。
     *    这样保证 dp[i+1][j-1] 在计算 dp[i][j] 之前已经填好。
     *
     * 【举例】s = "babad"
     *   长度1: dp[0][0..4] = [T,T,T,T,T]
     *   长度2: dp[0][1]='b'≠'a'→F, dp[1][2]='a'≠'b'→F, dp[2][3]='b'≠'a'→F, dp[3][4]='a'≠'d'→F
     *   长度3: dp[0][2]='b'='b' && dp[1][1]=T → T → "bab"
     *          dp[1][3]='a'='a' && dp[2][2]=T → T → "aba"
     *          dp[2][4]='b'≠'d' → F
     *   长度4,5: 均为 F
     *   最长回文 "bab"（长度3）
     *
     * 【时间复杂度】O(n²)
     * 【空间复杂度】O(n²)
     */
    public String longestPalindromeDP(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int start = 0, maxLen = 1;

        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }

        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
                start = i;
                maxLen = 2;
            }
        }

        for (int len = 3; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                    start = i;
                    maxLen = len;
                }
            }
        }

        return s.substring(start, start + maxLen);
    }

    /**
     * ==================== 解法三：Manacher 算法 ====================
     *
     * 【核心思路】
     * Manacher 算法利用已知回文的对称性来跳过重复计算，
     * 在 O(n) 时间内求出以每个位置为中心的最长回文半径。
     *
     * 【思考过程】
     * 1. 先在字符间插入分隔符（如 '#'），将奇偶长度统一为奇数处理。
     *    "babad" → "#b#a#b#a#d#"
     *
     * 2. 维护一个"最右回文边界" right 和对应的中心 center。
     *    对于当前位置 i：
     *    - 如果 i < right，利用 i 关于 center 的镜像位置 mirror = 2*center - i，
     *      可以跳过一部分已知的回文区域。
     *    - 然后尝试继续扩展。
     *
     * 3. 每次扩展后，如果新的回文右边界超过 right，就更新 center 和 right。
     *
     * 4. 最终 p[i] 数组中的最大值对应最长回文子串。
     *
     * 【举例】s = "babad" → t = "#b#a#b#a#d#"
     *   p 数组（回文半径）: [0,1,0,3,0,3,0,1,0,1,0]
     *   最大值 p[3]=3，中心位置3，对应原串中心1，长度3 → "bab"
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public String longestPalindromeManacher(String s) {
        StringBuilder sb = new StringBuilder("#");
        for (char c : s.toCharArray()) {
            sb.append(c).append('#');
        }
        String t = sb.toString();
        int n = t.length();
        int[] p = new int[n];

        int center = 0, right = 0;
        int maxLen = 0, maxCenter = 0;

        for (int i = 0; i < n; i++) {
            if (i < right) {
                p[i] = Math.min(right - i, p[2 * center - i]);
            }
            while (i - p[i] - 1 >= 0 && i + p[i] + 1 < n
                    && t.charAt(i - p[i] - 1) == t.charAt(i + p[i] + 1)) {
                p[i]++;
            }
            if (i + p[i] > right) {
                center = i;
                right = i + p[i];
            }
            if (p[i] > maxLen) {
                maxLen = p[i];
                maxCenter = i;
            }
        }

        int start = (maxCenter - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }
}
