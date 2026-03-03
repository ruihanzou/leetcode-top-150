/**
 * LeetCode 201. Bitwise AND of Numbers Range
 * 难度: Medium
 *
 * 题目描述：
 * 给你两个整数 left 和 right，表示区间 [left, right]。
 * 返回此区间内所有数字按位与的结果（包含 left 和 right 端点）。
 *
 * 示例 1：left = 5, right = 7 → 输出 4
 *   解释：5 = 101, 6 = 110, 7 = 111 → 101 & 110 & 111 = 100 = 4
 * 示例 2：left = 0, right = 0 → 输出 0
 * 示例 3：left = 1, right = 2147483647 → 输出 0
 *
 * 【拓展练习】
 * 1. LeetCode 191. Number of 1 Bits —— 位操作基础练习
 * 2. LeetCode 338. Counting Bits —— 统计每个数的二进制 1 的个数
 * 3. LeetCode 477. Total Hamming Distance —— 数组中所有数对的汉明距离之和
 */

class BitwiseANDRange201 {

    /**
     * ==================== 解法一：位移找公共前缀 ====================
     *
     * 【核心思路】
     * 区间 [left, right] 所有数的按位与，结果就是 left 和 right 的二进制公共前缀，
     * 后面的位全部填 0。因为在公共前缀之后的位，在区间内一定存在某个数使其为 0。
     *
     * 【思考过程】
     * 1. 关键观察：如果 left != right，那么在 left 到 right 之间，
     *    一定存在至少两个连续的数，使得最低位一个为 0 一个为 1。
     *    AND 之后最低位必然为 0。
     * 2. 推广：不断把 left 和 right 同时右移，直到它们相等。
     *    此时相等的值就是公共前缀。移了几位就左移回去几位。
     * 3. 右移的位数代表"两数从哪一位开始不同"，这些位在 AND 后全是 0。
     *
     * 【举例】left = 5 (101), right = 7 (111)
     *   第1次: left=101, right=111, 不等 → 右移: left=10, right=11, shift=1
     *   第2次: left=10,  right=11,  不等 → 右移: left=1,  right=1,  shift=2
     *   第3次: left=1,   right=1,   相等 → 停止
     *   结果 = 1 << 2 = 100 = 4
     *
     * 【时间复杂度】O(log n)，最多移 32 次
     * 【空间复杂度】O(1)
     */
    public int rangeBitwiseAndShift(int left, int right) {
        int shift = 0;
        while (left < right) {
            left >>= 1;
            right >>= 1;
            shift++;
        }
        return left << shift;
    }

    /**
     * ==================== 解法二：Brian Kernighan 算法 ====================
     *
     * 【核心思路】
     * 利用 n & (n-1) 消除 n 最右边的 1 的技巧。
     * 不断对 right 执行 right & (right-1)，直到 right <= left。
     * 此时 right 就是公共前缀（后面不同的位全被消掉了）。
     *
     * 【思考过程】
     * 1. 我们需要找 left 和 right 的公共前缀。
     * 2. right >= left，说明 right 比 left 多了一些低位的 1。
     * 3. right & (right-1) 会消掉 right 最右边的 1。
     *    如果消掉的这个 1 在公共前缀之外，那 right 变小但仍然 >= left。
     *    如果消到公共前缀内了，right 就会 <= left。
     * 4. 当 right <= left 时，right 中剩下的就正好是公共前缀
     *    （不同的低位 1 都被消完了）。
     *
     * 【举例】left = 5 (101), right = 7 (111)
     *   right=111, right-1=110 → right&(right-1)=110 (6), 6>5 继续
     *   right=110, right-1=101 → right&(right-1)=100 (4), 4<5 停止
     *   结果 = right & left = 4 & 5 = 4
     *   （实际上此时 right=4 就是答案，因为 5&6&7 = 100 = 4）
     *
     * 【时间复杂度】O(log n)，最多消除 32 个 1
     * 【空间复杂度】O(1)
     */
    public int rangeBitwiseAndKernighan(int left, int right) {
        while (right > left) {
            right &= (right - 1);
        }
        return right;
    }
}
