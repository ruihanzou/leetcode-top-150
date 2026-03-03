/**
 * LeetCode 221. Maximal Square
 * 难度: Medium
 *
 * 题目描述：
 * 在一个由 '0' 和 '1' 组成的二维矩阵内，找到只包含 '1' 的最大正方形，
 * 并返回其面积。
 *
 * 示例 1：
 * 输入：matrix = [["1","0","1","0","0"],
 *                ["1","0","1","1","1"],
 *                ["1","1","1","1","1"],
 *                ["1","0","0","1","0"]]
 * 输出：4
 * 解释：最大正方形的边长为 2，面积 = 4。
 *
 * 示例 2：
 * 输入：matrix = [["0","1"],["1","0"]]
 * 输出：1
 *
 * 示例 3：
 * 输入：matrix = [["0"]]
 * 输出：0
 *
 * 【拓展练习】
 * 1. LeetCode 85. Maximal Rectangle —— 最大矩形面积（Hard，柱状图思路）
 * 2. LeetCode 1277. Count Square Submatrices with All Ones —— 统计全1正方形数量
 * 3. LeetCode 764. Largest Plus Sign —— 最大加号标志（Medium）
 */

class MaximalSquare221 {

    /**
     * ==================== 解法一：二维DP ====================
     *
     * 【核心思路】
     * dp[i][j] 表示以 (i,j) 为右下角的最大全1正方形的边长。
     * 转移方程：如果 matrix[i][j] == '1'，
     * dp[i][j] = min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]) + 1。
     * 答案是 max(dp[i][j])² 。
     *
     * 【思考过程】
     * 1. 以 (i,j) 为右下角能构成正方形的关键约束：
     *    - 左边 dp[i][j-1]：表示向左最多能延伸多远
     *    - 上边 dp[i-1][j]：表示向上最多能延伸多远
     *    - 左上 dp[i-1][j-1]：表示左上方最多能撑起多大的正方形
     *    三者取最小值 + 1，就是以 (i,j) 为右下角的最大正方形边长。
     *
     * 2. 直观理解：要形成边长为 k 的正方形，需要：
     *    - (i,j-1) 处能形成至少 k-1 的正方形（保证左边 k-1 列都是1）
     *    - (i-1,j) 处能形成至少 k-1 的正方形（保证上面 k-1 行都是1）
     *    - (i-1,j-1) 处能形成至少 k-1 的正方形（保证左上区域填满1）
     *    缺一不可，所以取 min。
     *
     * 3. 初始化：第一行和第一列的 dp 值等于 matrix 本身的值。
     *
     * 【举例】matrix = [["1","0","1","0","0"],
     *                  ["1","0","1","1","1"],
     *                  ["1","1","1","1","1"],
     *                  ["1","0","0","1","0"]]
     *   dp 矩阵:
     *     1 0 1 0 0
     *     1 0 1 1 1
     *     1 1 1 2 2
     *     1 0 0 1 0
     *   最大值 2，面积 = 4
     *
     * 【时间复杂度】O(m * n)
     * 【空间复杂度】O(m * n)
     */
    public int maximalSquare(char[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] dp = new int[m][n];
        int maxSide = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        dp[i][j] = Math.min(dp[i - 1][j - 1],
                                   Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                    }
                    maxSide = Math.max(maxSide, dp[i][j]);
                }
            }
        }

        return maxSide * maxSide;
    }

    /**
     * ==================== 解法二：一维DP ====================
     *
     * 【核心思路】
     * 将二维 dp 压缩为一维。dp[j] 存储当前行的结果。
     * 需要额外变量 prev 记录 dp[i-1][j-1]（左上角的值）。
     *
     * 【思考过程】
     * 1. dp[i][j] 依赖三个值：dp[i-1][j-1]、dp[i-1][j]、dp[i][j-1]。
     *    压缩到一维后：
     *    - dp[j]（未更新）= dp[i-1][j]（正上方）
     *    - dp[j-1]（已更新）= dp[i][j-1]（正左方）
     *    - dp[i-1][j-1] 已经被覆盖 → 需要 prev 变量保存
     *
     * 2. 遍历每行时：
     *    - 开始前 prev = 0（左上角没有值）
     *    - 对每个 j：先把 dp[j] 存入 temp，计算新的 dp[j]，然后 prev = temp
     *
     * 3. matrix[i][j] == '0' 时，dp[j] = 0（重要！必须显式置0）。
     *
     * 【举例】matrix = [["1","0","1","0","0"],
     *                  ["1","0","1","1","1"],
     *                  ["1","1","1","1","1"],
     *                  ["1","0","0","1","0"]]
     *   初始 dp = [0,0,0,0,0]
     *   第0行: dp = [1,0,1,0,0], maxSide=1
     *   第1行: dp = [1,0,1,1,1], maxSide=1
     *   第2行:
     *     j=0: prev=0, dp[0]=1
     *     j=1: prev=0, temp=0, dp[1]=min(0,1,0)+1=1, prev=0
     *     j=2: prev=0, temp=1, dp[2]=min(0,0,1)+1=1, prev=1
     *     j=3: prev=1, temp=1, dp[3]=min(1,1,1)+1=2, prev=1
     *     j=4: prev=1, temp=1, dp[4]=min(1,2,1)+1=2, prev=1
     *     dp = [1,1,1,2,2], maxSide=2
     *   第3行: dp = [1,0,0,1,0], maxSide=2
     *   答案 = 2*2 = 4
     *
     * 【时间复杂度】O(m * n)
     * 【空间复杂度】O(n)
     */
    public int maximalSquareOptimized(char[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[] dp = new int[n];
        int maxSide = 0;

        for (int i = 0; i < m; i++) {
            int prev = 0;
            for (int j = 0; j < n; j++) {
                int temp = dp[j];
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) {
                        dp[j] = 1;
                    } else {
                        dp[j] = Math.min(prev, Math.min(dp[j], dp[j - 1])) + 1;
                    }
                    maxSide = Math.max(maxSide, dp[j]);
                } else {
                    dp[j] = 0;
                }
                prev = temp;
            }
        }

        return maxSide * maxSide;
    }
}
