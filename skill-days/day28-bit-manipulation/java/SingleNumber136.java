/**
 * LeetCode 136. Single Number
 * 难度: Easy
 *
 * 题目描述：
 * 给你一个非空整数数组 nums，除了某个元素只出现一次以外，
 * 其余每个元素均出现两次。找出那个只出现了一次的元素。
 * 你必须设计并实现线性时间复杂度的算法，且该算法只使用常量额外空间。
 *
 * 示例 1：nums = [2,2,1] → 输出 1
 * 示例 2：nums = [4,1,2,1,2] → 输出 4
 * 示例 3：nums = [1] → 输出 1
 *
 * 【拓展练习】
 * 1. LeetCode 137. Single Number II —— 其余元素出现三次，找出只出现一次的
 * 2. LeetCode 260. Single Number III —— 有两个只出现一次的元素
 * 3. LeetCode 268. Missing Number —— 找缺失的数字，也可用异或
 */

import java.util.*;

class SingleNumber136 {

    /**
     * ==================== 解法一：异或 ====================
     *
     * 【核心思路】
     * 利用异或的性质：a ^ a = 0, a ^ 0 = a, 且异或满足交换律和结合律。
     * 将数组中所有元素依次异或，出现两次的元素互相抵消为 0，
     * 最后剩下的就是那个只出现一次的元素。
     *
     * 【思考过程】
     * 1. 需要线性时间 + 常量空间，排除排序(O(n log n))和哈希表(O(n)空间)。
     * 2. 异或的关键性质：
     *    - 自反性：a ^ a = 0（相同的数异或为 0）
     *    - 恒等性：a ^ 0 = a（任何数和 0 异或是自身）
     *    - 交换律 + 结合律：顺序无关
     * 3. 所有元素异或起来，出现两次的自动消掉，只剩单独的那个。
     *
     * 【举例】nums = [4, 1, 2, 1, 2]
     *   4 ^ 1 = 5
     *   5 ^ 2 = 7
     *   7 ^ 1 = 6  (1 ^ 1 抵消了)
     *   6 ^ 2 = 4  (2 ^ 2 抵消了)
     *   最终结果 = 4
     *
     * 【时间复杂度】O(n)，遍历一次数组
     * 【空间复杂度】O(1)
     */
    public int singleNumberXor(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    /**
     * ==================== 解法二：哈希表计数 ====================
     *
     * 【核心思路】
     * 用哈希表统计每个元素出现的次数，然后找到出现次数为 1 的元素。
     *
     * 【思考过程】
     * 1. 最直观的方法：数一下每个数出现了几次。
     * 2. 用 HashMap 统计频次，然后遍历找值为 1 的键。
     * 3. 虽然不满足 O(1) 空间的要求，但思路清晰，适合作为对比。
     *
     * 【举例】nums = [4, 1, 2, 1, 2]
     *   计数：{4:1, 1:2, 2:2}
     *   找到 count=1 的键 → 4
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public int singleNumberHash(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int num : nums) {
            count.merge(num, 1, Integer::sum);
        }
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
