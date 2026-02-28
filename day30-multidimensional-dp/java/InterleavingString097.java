/**
 * LeetCode 97. Interleaving String
 * 难度: Medium
 *
 * 题目描述：
 * 给定三个字符串 s1、s2、s3，请判断 s3 是否由 s1 和 s2 交错组成。
 * 两个字符串交错的定义是：将它们分割成若干非空子串，然后交替拼接。
 * 更正式地，如果存在将 s1 分割为 s1 = a1 + a2 + ... + ak，
 * 将 s2 分割为 s2 = b1 + b2 + ... + bk（或 bk+1），
 * 使得 s3 = a1 + b1 + a2 + b2 + ... 或 s3 = b1 + a1 + b2 + a2 + ...，
 * 则称 s3 是 s1 和 s2 的交错。
 *
 * 示例 1：
 * 输入：s1 = "aabcc", s2 = "dbbca", s3 = "aadbbcbcac"
 * 输出：true
 * 解释：s1 = "aa" + "bc" + "c"，s2 = "d" + "bbc" + "a"
 *       交错 = "aa" + "d" + "bbc" + "bc" + "a" + "c" = "aadbbcbcac"
 *
 * 示例 2：
 * 输入：s1 = "aabcc", s2 = "dbbca", s3 = "aadbbbaccc"
 * 输出：false
 *
 * 【拓展练习】
 * 1. LeetCode 115. Distinct Subsequences —— 字符串匹配的子序列计数
 * 2. LeetCode 72. Edit Distance —— 两个字符串的编辑距离
 * 3. LeetCode 1092. Shortest Common Supersequence —— 最短公共超序列（Hard）
 */

class InterleavingString097 {

    /**
     * ==================== 解法一：二维DP ====================
     *
     * 【核心思路】
     * dp[i][j] 表示 s1 的前 i 个字符和 s2 的前 j 个字符能否交错组成 s3 的前 i+j 个字符。
     * 转移：如果 s1[i-1] == s3[i+j-1]，则 dp[i][j] |= dp[i-1][j]；
     *       如果 s2[j-1] == s3[i+j-1]，则 dp[i][j] |= dp[i][j-1]。
     *
     * 【思考过程】
     * 1. s3 的长度必须等于 s1 + s2 的长度，否则直接返回 false。
     *
     * 2. s3 的第 k = i+j 个字符，要么来自 s1（此时用 s1 的第 i 个字符，
     *    前面已经用了 s1 的前 i-1 个和 s2 的前 j 个），
     *    要么来自 s2（类似地）。
     *
     * 3. dp[i][j] = true 当且仅当：
     *    - s1[i-1] == s3[i+j-1] 且 dp[i-1][j] 为 true，或者
     *    - s2[j-1] == s3[i+j-1] 且 dp[i][j-1] 为 true。
     *
     * 4. 初始化：dp[0][0] = true，第一行和第一列分别表示只用 s2 或只用 s1。
     *
     * 【举例】s1 = "aab", s2 = "axy", s3 = "aaxaby"
     *   dp 表格（T=true, F=false）:
     *          ""   a    x    y
     *     ""    T    T    F    F
     *      a    T    T    F    F
     *      a    T    T    T    F
     *      b    F    T    T    T
     *   dp[3][3] = T → true
     *
     * 【时间复杂度】O(m * n)，m = len(s1), n = len(s2)
     * 【空间复杂度】O(m * n)
     */
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length()) return false;

        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;

        for (int i = 1; i <= m; i++) {
            dp[i][0] = dp[i - 1][0] && s1.charAt(i - 1) == s3.charAt(i - 1);
        }
        for (int j = 1; j <= n; j++) {
            dp[0][j] = dp[0][j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = (dp[i - 1][j] && s1.charAt(i - 1) == s3.charAt(i + j - 1))
                         || (dp[i][j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1));
            }
        }

        return dp[m][n];
    }

    /**
     * ==================== 解法二：一维DP ====================
     *
     * 【核心思路】
     * 将二维 dp 压缩为一维。dp[j] 表示 s1 的前 i 个字符和 s2 的前 j 个字符
     * 能否交错组成 s3 的前 i+j 个字符。逐行更新。
     *
     * 【思考过程】
     * 1. dp[i][j] 只依赖 dp[i-1][j]（上方）和 dp[i][j-1]（左方）。
     *
     * 2. 用一维数组 dp[j]：
     *    - dp[j]（未更新）= dp[i-1][j]，即"上方"的值。
     *    - dp[j-1]（已更新）= dp[i][j-1]，即"左方"的值。
     *
     * 3. 对 j=0（只用 s1），每轮单独更新：dp[0] = dp[0] && s1[i-1] == s3[i-1]。
     *
     * 【举例】s1 = "aab", s2 = "axy", s3 = "aaxaby"
     *   初始 dp = [T, T, F, F]（只用 s2 匹配 s3）
     *   i=1 (s1='a'):
     *     dp[0] = T && 'a'=='a' → T
     *     dp[1] = (T && 'a'=='a') || (T && 'a'=='a') → T
     *     dp[2] = (F && 'x'=='x') || (T && 'x'=='x') → T ... 但实际看 s3[2]
     *     ...
     *   最终 dp[3] = T
     *
     * 【时间复杂度】O(m * n)
     * 【空间复杂度】O(n)
     */
    public boolean isInterleaveOptimized(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length()) return false;

        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int j = 1; j <= n; j++) {
            dp[j] = dp[j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        }

        for (int i = 1; i <= m; i++) {
            dp[0] = dp[0] && s1.charAt(i - 1) == s3.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                dp[j] = (dp[j] && s1.charAt(i - 1) == s3.charAt(i + j - 1))
                      || (dp[j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1));
            }
        }

        return dp[n];
    }
}
