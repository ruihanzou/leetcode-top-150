/**
 * LeetCode 70. Climbing Stairs
 * 难度: Easy
 *
 * 题目描述：
 * 假设你正在爬楼梯，需要 n 阶你才能到达楼顶。
 * 每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶？
 *
 * 示例：n = 3 → 输出 3（1+1+1, 1+2, 2+1）
 *
 * 【拓展练习】
 * 1. LeetCode 746. Min Cost Climbing Stairs —— 带花费的爬楼梯，同样的 DP 转移
 * 2. LeetCode 509. Fibonacci Number —— 本题本质上就是斐波那契数列
 * 3. LeetCode 1137. N-th Tribonacci Number —— 三项递推的推广
 */

class ClimbingStairs070 {

    /**
     * ==================== 解法一：DP ====================
     *
     * 【核心思路】
     * dp[i] 表示到达第 i 阶的方法数。
     * 因为每次可以从第 i-1 阶爬 1 步，或从第 i-2 阶爬 2 步到达第 i 阶，
     * 所以 dp[i] = dp[i-1] + dp[i-2]，本质就是斐波那契数列。
     *
     * 【思考过程】
     * 1. 到达第 i 阶的最后一步只有两种可能：从 i-1 跨 1 步，或从 i-2 跨 2 步。
     *    这两种情况互斥且完备，所以方法数直接相加。
     *
     * 2. 基础情况：dp[0] = 1（在起点，一种方式），dp[1] = 1（只能跨1步）。
     *
     * 3. 递推填表即可。
     *
     * 【举例】n = 5
     *   dp = [1, 1, 2, 3, 5, 8]
     *   dp[2] = dp[1]+dp[0] = 2
     *   dp[3] = dp[2]+dp[1] = 3
     *   dp[4] = dp[3]+dp[2] = 5
     *   dp[5] = dp[4]+dp[3] = 8
     *   答案 = 8
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public int climbStairsDp(int n) {
        if (n <= 1) return 1;
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    /**
     * ==================== 解法二：DP 空间优化 ====================
     *
     * 【核心思路】
     * dp[i] 只依赖 dp[i-1] 和 dp[i-2]，不需要保留整个数组，
     * 用两个变量滚动即可。
     *
     * 【思考过程】
     * 1. 观察递推公式 dp[i] = dp[i-1] + dp[i-2]，当前值只用到前两个值。
     *
     * 2. 用变量 a 表示 dp[i-2]，b 表示 dp[i-1]。
     *    每步更新：temp = a + b; a = b; b = temp。
     *
     * 3. 这样空间从 O(n) 优化到 O(1)。
     *
     * 【举例】n = 5
     *   初始: a=1, b=1
     *   i=2: a=1, b=2   (1+1=2)
     *   i=3: a=2, b=3   (1+2=3)
     *   i=4: a=3, b=5   (2+3=5)
     *   i=5: a=5, b=8   (3+5=8)
     *   答案 = 8
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int climbStairsOpt(int n) {
        if (n <= 1) return 1;
        int a = 1, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    /**
     * ==================== 解法三：矩阵快速幂 ====================
     *
     * 【核心思路】
     * 斐波那契递推可以表示为矩阵乘法：
     * [F(n+1)]   [1 1]^n   [F(1)]
     * [F(n)  ] = [1 0]   * [F(0)]
     *
     * 利用矩阵快速幂，可以在 O(log n) 时间内求出结果。
     *
     * 【思考过程】
     * 1. 递推关系 dp[i] = dp[i-1] + dp[i-2] 是线性递推，
     *    可以写成矩阵形式：
     *    | dp[i]   |   | 1  1 |   | dp[i-1] |
     *    | dp[i-1] | = | 1  0 | * | dp[i-2] |
     *
     * 2. 连续递推 n-1 次等价于矩阵的 n-1 次幂乘以初始向量。
     *
     * 3. 矩阵幂可以用快速幂（反复平方法）在 O(log n) 完成。
     *    虽然常数比迭代大，但渐进更优，对极大 n 有优势。
     *
     * 【举例】n = 5
     *   M = [[1,1],[1,0]]
     *   M^4 = [[5,3],[3,2]]
     *   结果 = M^4 * [1,1]^T 的第一行 = 5*1 + 3*1 = 8
     *
     * 【时间复杂度】O(log n)
     * 【空间复杂度】O(1)（矩阵大小固定为 2×2）
     */
    public int climbStairsMatrix(int n) {
        if (n <= 1) return 1;
        long[][] M = {{1, 1}, {1, 0}};
        long[][] result = matrixPow(M, n);
        return (int) result[0][0];
    }

    private long[][] multiply(long[][] A, long[][] B) {
        return new long[][] {
            {A[0][0] * B[0][0] + A[0][1] * B[1][0], A[0][0] * B[0][1] + A[0][1] * B[1][1]},
            {A[1][0] * B[0][0] + A[1][1] * B[1][0], A[1][0] * B[0][1] + A[1][1] * B[1][1]}
        };
    }

    private long[][] matrixPow(long[][] M, int p) {
        long[][] result = {{1, 0}, {0, 1}};
        while (p > 0) {
            if ((p & 1) == 1) {
                result = multiply(result, M);
            }
            M = multiply(M, M);
            p >>= 1;
        }
        return result;
    }
}
