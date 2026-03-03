/**
 * LeetCode 128. Longest Consecutive Sequence
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个未排序的整数数组 nums，找出数字连续的最长序列（不要求序列元素在原数组中连续）
 * 的长度。请你设计并实现时间复杂度为 O(n) 的算法解决此问题。
 *
 * 示例：
 *   输入: nums = [100,4,200,1,3,2]
 *   输出: 4
 *   解释: 最长数字连续序列是 [1,2,3,4]，长度为 4
 *
 *   输入: nums = [0,3,7,2,5,8,4,6,0,1]
 *   输出: 9
 *   解释: 最长数字连续序列是 [0,1,2,3,4,5,6,7,8]，长度为 9
 *
 * 【拓展练习】
 * 1. LeetCode 298. Binary Tree Longest Consecutive Sequence —— 二叉树中最长连续序列
 * 2. LeetCode 674. Longest Continuous Increasing Subsequence —— 最长连续递增子序列
 * 3. LeetCode 2274. Maximum Consecutive Floors Without Special Floor —— 连续楼层问题
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class LongestConsecutiveSequence128 {

    /**
     * ==================== 解法一：排序后遍历 ====================
     *
     * 【核心思路】
     * 先排序，然后遍历排序后的数组，统计连续递增子序列的最大长度。
     * 相邻相等的元素跳过（不算断裂也不算新增长度）。
     *
     * 【思考过程】
     * 1. 排序后，连续序列变成数组中连续的一段，很容易统计。
     * 2. 需要注意重复元素：[1,2,2,3] 中 [1,2,3] 是长度为 3 的连续序列。
     *    所以相邻相等时不重置计数，直接跳过。
     * 3. 如果相邻元素差恰好为 1，当前长度 +1；否则重置为 1。
     *
     * 【举例】nums = [100,4,200,1,3,2]
     *   排序 → [1,2,3,4,100,200]
     *   i=1: 2-1=1, cur=2
     *   i=2: 3-2=1, cur=3
     *   i=3: 4-3=1, cur=4, best=4
     *   i=4: 100-4=96≠1, cur=1
     *   i=5: 200-100=100≠1, cur=1
     *   答案: 4
     *
     * 【时间复杂度】O(n log n) 排序主导
     * 【空间复杂度】O(1) 不计排序栈空间
     */
    public int longestConsecutiveSort(int[] nums) {
        if (nums.length == 0) return 0;

        Arrays.sort(nums);
        int best = 1;
        int cur = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) {
                continue;
            }
            if (nums[i] == nums[i - 1] + 1) {
                cur++;
                best = Math.max(best, cur);
            } else {
                cur = 1;
            }
        }

        return best;
    }

    /**
     * ==================== 解法二：哈希集合 + 起点判断 ====================
     *
     * 【核心思路】
     * 将所有元素放入哈希集合。对于每个元素 x，如果 x-1 不在集合中，
     * 说明 x 是某个连续序列的起点，从 x 开始往后数有多少个连续数字。
     * 这样每个元素只被访问一次，总复杂度 O(n)。
     *
     * 【思考过程】
     * 1. 朴素想法：对每个数，往后数连续的数有多少个。
     *    但如果每个数都这样做，总计可能 O(n²)（比如从序列中间的每个数都开始数）。
     *
     * 2. 优化关键：只从序列的"起点"开始数。
     *    怎么判断一个数是起点？如果 num-1 不在集合中，说明 num 前面没有数了，
     *    它一定是某条连续序列的第一个数。
     *
     * 3. 这样保证每个连续序列只从起点遍历一次，每个元素最多被访问两次
     *    （一次 contains 判断，一次作为序列中的一部分），总复杂度 O(n)。
     *
     * 【举例】nums = [100,4,200,1,3,2]
     *   set = {100, 4, 200, 1, 3, 2}
     *
     *   num=100: 99不在set中 → 起点! 100在set,101不在 → 长度1
     *   num=4:   3在set中 → 不是起点，跳过
     *   num=200: 199不在set中 → 起点! 200在set,201不在 → 长度1
     *   num=1:   0不在set中 → 起点! 1,2,3,4都在set,5不在 → 长度4
     *   num=3:   2在set中 → 不是起点，跳过
     *   num=2:   1在set中 → 不是起点，跳过
     *   答案: 4
     *
     * 【时间复杂度】O(n) 每个元素最多被访问常数次
     * 【空间复杂度】O(n) 哈希集合
     */
    public int longestConsecutive(int[] nums) {
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        int best = 0;

        for (int num : numSet) {
            if (!numSet.contains(num - 1)) {
                int cur = num;
                int length = 1;
                while (numSet.contains(cur + 1)) {
                    cur++;
                    length++;
                }
                best = Math.max(best, length);
            }
        }

        return best;
    }
}
