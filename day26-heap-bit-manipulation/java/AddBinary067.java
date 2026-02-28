/**
 * LeetCode 67. Add Binary
 * 难度: Easy
 *
 * 题目描述：
 * 给你两个二进制字符串 a 和 b，以二进制字符串的形式返回它们的和。
 *
 * 示例 1：a = "11", b = "1" → 输出 "100"
 * 示例 2：a = "1010", b = "1011" → 输出 "10101"
 *
 * 【拓展练习】
 * 1. LeetCode 2. Add Two Numbers —— 链表形式的两数相加，同样是模拟进位
 * 2. LeetCode 66. Plus One —— 数组表示的数加一，简化版进位模拟
 * 3. LeetCode 415. Add Strings —— 十进制字符串相加，同样的逐位进位思路
 */

class AddBinary067 {

    /**
     * ==================== 解法一：模拟逐位相加 ====================
     *
     * 【核心思路】
     * 从两个字符串的末尾开始，逐位相加并处理进位，类似手工竖式加法。
     * 每次计算 当前位之和 = a的当前位 + b的当前位 + carry，
     * 结果的当前位 = 和 % 2，进位 = 和 / 2。
     *
     * 【思考过程】
     * 1. 二进制加法和十进制加法本质一样，只是逢 2 进 1。
     * 2. 从最低位（字符串末尾）开始加，维护一个 carry 变量。
     * 3. 两个字符串长度可能不同，短的那个用完后相当于高位补 0。
     * 4. 循环结束后如果 carry 还是 1，需要在最高位补一个 '1'。
     * 5. 由于是从低位到高位构建结果，最后需要反转。
     *
     * 【举例】a = "1010", b = "1011"
     *   i=3, j=3: 0+1+0=1, 位='1', carry=0 → "1"
     *   i=2, j=2: 1+1+0=2, 位='0', carry=1 → "01"
     *   i=1, j=1: 0+0+1=1, 位='1', carry=0 → "101"
     *   i=0, j=0: 1+1+0=2, 位='0', carry=1 → "0101"
     *   carry=1 → "10101"
     *   反转 → "10101"
     *
     * 【时间复杂度】O(max(m, n))，m 和 n 分别是 a 和 b 的长度
     * 【空间复杂度】O(max(m, n))，存储结果
     */
    public String addBinarySimulate(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1;
        int carry = 0;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;
            if (i >= 0) {
                sum += a.charAt(i) - '0';
                i--;
            }
            if (j >= 0) {
                sum += b.charAt(j) - '0';
                j--;
            }
            sb.append(sum % 2);
            carry = sum / 2;
        }

        return sb.reverse().toString();
    }

    /**
     * ==================== 解法二：位运算（不用加法） ====================
     *
     * 【核心思路】
     * 利用位运算模拟加法：两个数的无进位和 = a XOR b，进位 = (a AND b) << 1。
     * 反复执行直到进位为 0。使用 BigInteger 避免溢出。
     *
     * 【思考过程】
     * 1. 二进制加法可以分解为"无进位加法"和"进位"两部分。
     * 2. XOR 正好实现了无进位加法（0+0=0, 0+1=1, 1+0=1, 1+1=0）。
     * 3. AND 再左移一位得到进位（只有 1+1 才产生进位）。
     * 4. 把无进位和与进位再次相加，直到进位为 0。
     * 5. Java 中二进制字符串可能很长，需要用 BigInteger。
     *
     * 【举例】a = "1010"(10), b = "1011"(11)
     *   轮1: x=10, y=11 → xor=0001(1), carry=10100(20) → x=1, y=20
     *   轮2: x=1, y=20 → xor=10101(21), carry=00000(0) → x=21, y=0
     *   结束，toBinaryString(21) = "10101"
     *
     * 【时间复杂度】O(max(m, n))
     * 【空间复杂度】O(max(m, n))
     */
    public String addBinaryBit(String a, String b) {
        java.math.BigInteger x = new java.math.BigInteger(a, 2);
        java.math.BigInteger y = new java.math.BigInteger(b, 2);

        while (!y.equals(java.math.BigInteger.ZERO)) {
            java.math.BigInteger xor = x.xor(y);
            java.math.BigInteger carry = x.and(y).shiftLeft(1);
            x = xor;
            y = carry;
        }

        return x.toString(2);
    }
}
