/**
 * LeetCode 50. Pow(x, n)
 * 难度: Medium
 *
 * 题目描述：
 * 实现 pow(x, n)，即计算 x 的整数 n 次幂函数。
 * -100.0 < x < 100.0，n 是 32 位有符号整数（-2^31 <= n <= 2^31 - 1）
 *
 * 示例：x=2.0, n=10 → 1024.0，x=2.1, n=3 → 9.261，x=2.0, n=-2 → 0.25
 *
 * 【拓展练习】
 * 1. LeetCode 372. Super Pow —— 超大指数的幂运算
 * 2. LeetCode 69. Sqrt(x) —— 开方运算，快速幂的逆运算
 * 3. LeetCode 29. Divide Two Integers —— 类似的倍增思路
 */

class Pow050 {

    /**
     * ==================== 解法一：快速幂（递归） ====================
     *
     * 【核心思路】
     * 利用 x^n = (x^(n/2))^2 的性质，将指数每次减半，递归求解。
     * - n 为偶数：x^n = (x^(n/2))^2
     * - n 为奇数：x^n = x · (x^(n/2))^2
     *
     * 【思考过程】
     * 1. 暴力做法是乘 n 次，O(n)，n 最大到 2^31，会超时。
     *    → 需要更快的方法。
     *
     * 2. 关键观察：x^10 = x^5 · x^5，x^5 = x · x^2 · x^2。
     *    每次将指数减半，只需要 O(log n) 次乘法。
     *
     * 3. n 为负数时，x^n = 1 / x^(-n)。
     *    注意 n = Integer.MIN_VALUE 时，-n 会溢出 int，
     *    所以用 long 来处理。
     *
     * 4. 递归终点：n=0 时返回 1.0。
     *
     * 【举例】x=2.0, n=10
     *   pow(2,10) = pow(2,5)^2
     *   pow(2,5) = 2 * pow(2,2)^2
     *   pow(2,2) = pow(2,1)^2
     *   pow(2,1) = 2 * pow(2,0)^2 = 2 * 1 = 2
     *   回溯: pow(2,2)=4, pow(2,5)=2*16=32, pow(2,10)=1024
     *
     * 【时间复杂度】O(log n)
     * 【空间复杂度】O(log n) —— 递归栈深度
     */
    public double myPowRecursive(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }
        return fastPow(x, N);
    }

    private double fastPow(double x, long n) {
        if (n == 0) return 1.0;

        double half = fastPow(x, n / 2);
        if (n % 2 == 0) {
            return half * half;
        } else {
            return half * half * x;
        }
    }

    /**
     * ==================== 解法二：快速幂（迭代） ====================
     *
     * 【核心思路】
     * 将指数 n 看作二进制表示，从低位到高位扫描。
     * 每一位对应 x 的某个 2^k 次幂，如果该位为 1 就乘入结果。
     *
     * 【思考过程】
     * 1. 递归版有 O(log n) 的栈空间，能否优化到 O(1)？
     *    → 用迭代替代递归。
     *
     * 2. 例如 x^13，13 的二进制是 1101。
     *    x^13 = x^8 · x^4 · x^1
     *    即二进制中每个 1 对应的 x 的幂次相乘。
     *
     * 3. 用一个变量 base 表示当前位对应的幂次：
     *    - base 初始为 x（对应 2^0 = 1）
     *    - 每轮 base = base * base（对应 2^1, 2^2, 2^3, ...）
     *    - 如果当前位为 1（n % 2 == 1），将 base 乘入 result
     *    - n 右移一位（n /= 2）
     *
     * 4. 同样注意 n 为负数和 Integer.MIN_VALUE 的处理。
     *
     * 【举例】x=2.0, n=13 (二进制 1101)
     *   result=1, base=2
     *   bit 0 (1): result *= 2 → result=2,   base=4
     *   bit 1 (0):                            base=16
     *   bit 2 (1): result *= 16 → result=32,  base=256
     *   bit 3 (1): result *= 256 → result=8192
     *   n=0，返回 8192 = 2^13 ✓
     *
     * 【时间复杂度】O(log n)
     * 【空间复杂度】O(1)
     */
    public double myPowIterative(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;
        double base = x;

        while (N > 0) {
            if (N % 2 == 1) {
                result *= base;
            }
            base *= base;
            N /= 2;
        }

        return result;
    }
}
