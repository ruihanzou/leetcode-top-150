/**
 * LeetCode 72. Edit Distance
 * 难度: Medium
 *
 * 题目描述：
 * 给你两个单词 word1 和 word2，请返回将 word1 转换成 word2 所使用的最少操作数。
 * 你可以对一个单词进行如下三种操作：插入一个字符、删除一个字符、替换一个字符。
 *
 * 示例 1：
 * 输入：word1 = "horse", word2 = "ros"
 * 输出：3
 * 解释：horse → rorse（替换 'h' → 'r'）→ rose（删除 'r'）→ ros（删除 'e'）
 *
 * 示例 2：
 * 输入：word1 = "intention", word2 = "execution"
 * 输出：5
 *
 * 【拓展练习】
 * 1. LeetCode 583. Delete Operation for Two Strings —— 只允许删除的编辑距离
 * 2. LeetCode 712. Minimum ASCII Delete Sum for Two Strings —— 带权删除
 * 3. LeetCode 1143. Longest Common Subsequence —— 最长公共子序列
 */

class EditDistance072 {

    /**
     * ==================== 解法一：二维DP ====================
     *
     * 【核心思路】
     * dp[i][j] 表示 word1 的前 i 个字符转换成 word2 的前 j 个字符所需的最少操作数。
     * 如果 word1[i-1] == word2[j-1]，则 dp[i][j] = dp[i-1][j-1]（无需操作）。
     * 否则 dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])，
     * 分别对应删除、插入、替换。
     *
     * 【思考过程】
     * 1. 考虑 word1[0..i-1] → word2[0..j-1] 的最后一步操作：
     *    - 如果末尾字符相同，不需要额外操作，问题规模缩小为 dp[i-1][j-1]。
     *    - 如果不同，有三种选择：
     *      a) 删除 word1[i-1]：问题变为 word1[0..i-2] → word2[0..j-1]，即 dp[i-1][j]
     *      b) 插入 word2[j-1]：问题变为 word1[0..i-1] → word2[0..j-2]，即 dp[i][j-1]
     *      c) 替换 word1[i-1] 为 word2[j-1]：问题变为 dp[i-1][j-1]
     *      取三者最小值 + 1。
     *
     * 2. 初始化：dp[i][0] = i（删除 i 个字符），dp[0][j] = j（插入 j 个字符）。
     *
     * 【举例】word1 = "horse", word2 = "ros"
     *       ""  r  o  s
     *   ""   0  1  2  3
     *    h   1  1  2  3
     *    o   2  2  1  2
     *    r   3  2  2  2
     *    s   4  3  3  2
     *    e   5  4  4  3
     *   答案 dp[5][3] = 3
     *
     * 【时间复杂度】O(m * n)
     * 【空间复杂度】O(m * n)
     */
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                                   Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[m][n];
    }

    /**
     * ==================== 解法二：一维DP空间优化 ====================
     *
     * 【核心思路】
     * dp[j] 表示当前行的编辑距离。由于 dp[i][j] 依赖三个值：
     * dp[i-1][j-1]（左上），dp[i-1][j]（正上），dp[i][j-1]（正左），
     * 压缩时需要额外变量 prev 保存 dp[i-1][j-1]。
     *
     * 【思考过程】
     * 1. 标准的二维到一维压缩。难点在于"左上"值 dp[i-1][j-1]：
     *    当我们更新 dp[j] 时，dp[j] 的旧值是 dp[i-1][j]（正上），
     *    dp[j-1] 已经被更新成 dp[i][j-1]（正左），
     *    但 dp[i-1][j-1] 已经被覆盖了！
     *
     * 2. 解决方案：在更新 dp[j] 之前，把 dp[j] 的旧值存入 prev。
     *    更新完后，prev 就准备好作为下一轮的 dp[i-1][j-1]。
     *
     * 3. 每行开始时，prev = dp[0] 的旧值（即 dp[i-1][0] = i-1），
     *    dp[0] = i（表示删掉 i 个字符）。
     *
     * 【举例】word1 = "horse", word2 = "ros"
     *   初始 dp = [0, 1, 2, 3]
     *   i=1 ('h'): prev=0, dp[0]=1
     *     j=1: temp=dp[1]=1, dp[1]=1+min(0,1,1)=1, prev=1
     *     j=2: temp=dp[2]=2, dp[2]=1+min(1,1,2)=2, prev=2
     *     j=3: temp=dp[3]=3, dp[3]=1+min(2,2,3)=3, prev=3
     *     dp = [1,1,2,3]
     *   ...（继续）
     *   最终 dp[3] = 3
     *
     * 【时间复杂度】O(m * n)
     * 【空间复杂度】O(n)
     */
    public int minDistanceOptimized(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[] dp = new int[n + 1];

        for (int j = 0; j <= n; j++) dp[j] = j;

        for (int i = 1; i <= m; i++) {
            int prev = dp[0];
            dp[0] = i;
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[j] = prev;
                } else {
                    dp[j] = 1 + Math.min(prev, Math.min(dp[j], dp[j - 1]));
                }
                prev = temp;
            }
        }

        return dp[n];
    }
}
