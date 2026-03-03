/**
 * LeetCode 300. Longest Increasing Subsequence
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个整数数组 nums，找到其中最长严格递增子序列的长度。
 * 子序列是由数组派生而来的序列，删除（或不删除）数组中的元素而不改变其余元素的顺序。
 *
 * 示例：nums = [10,9,2,5,3,7,101,18] → 输出 4（[2,3,7,101]）
 *
 * 【拓展练习】
 * 1. LeetCode 354. Russian Doll Envelopes —— 二维的LIS问题（先排序再对一维做LIS）
 * 2. LeetCode 673. Number of Longest Increasing Subsequence —— 统计LIS的个数
 * 3. LeetCode 334. Increasing Triplet Subsequence —— 判断是否存在长度为3的递增子序列
 */

import java.util.Arrays;

class LIS300 {

    /**
     * ==================== 解法一：DP ====================
     *
     * 【核心思路】
     * dp[i] 表示以 nums[i] 结尾的最长递增子序列的长度。
     * 对于每个 i，枚举所有 j < i，如果 nums[j] < nums[i]，
     * 则 dp[i] = max(dp[i], dp[j] + 1)。答案是 max(dp)。
     *
     * 【思考过程】
     * 1. 经典的"以某元素结尾"的 DP 定义。
     *    为什么要定义"以 nums[i] 结尾"？因为递增子序列的长度取决于末尾元素的大小，
     *    相同长度但末尾元素不同的子序列，对后续元素的可扩展性不同。
     *
     * 2. 转移：如果 nums[j] < nums[i]（j < i），那么 nums[i] 可以接在
     *    以 nums[j] 结尾的 LIS 后面，形成长度 dp[j] + 1 的 LIS。
     *    取所有合法 j 的最大值。
     *
     * 3. 初始值：dp[i] = 1（每个元素本身构成长度为1的子序列）。
     *
     * 4. 最终答案是 dp 数组中的最大值。
     *
     * 【举例】nums = [10, 9, 2, 5, 3, 7, 101, 18]
     *   dp[0]=1  (10)
     *   dp[1]=1  (9)
     *   dp[2]=1  (2)
     *   dp[3]=2  (2→5)
     *   dp[4]=2  (2→3)
     *   dp[5]=3  (2→3→7)
     *   dp[6]=4  (2→3→7→101)
     *   dp[7]=4  (2→3→7→18)
     *   答案 = 4
     *
     * 【时间复杂度】O(n²)
     * 【空间复杂度】O(n)
     */
    public int lengthOfLISDp(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        int ans = 1;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            ans = Math.max(ans, dp[i]);
        }

        return ans;
    }

    /**
     * ==================== 解法二：贪心 + 二分查找 ====================
     *
     * 【核心思路】
     * 维护一个 tails 数组，tails[i] 表示长度为 i+1 的所有递增子序列中，
     * 末尾元素的最小值。tails 数组始终是严格递增的。
     * 对于每个 nums[k]，用二分查找在 tails 中找到第一个 >= nums[k] 的位置并替换。
     *
     * 【思考过程】
     * 1. O(n²) 的 DP 能否优化？关键观察：
     *    对于相同长度的递增子序列，末尾元素越小，后续可扩展的可能性越大。
     *
     * 2. 维护 tails 数组，其中 tails[i] 是"长度为 i+1 的 LIS 的最小末尾元素"。
     *    这个数组有一个关键性质：它始终是严格递增的。
     *
     * 3. 对于新元素 x：
     *    - 如果 x > tails[size-1]，直接追加，LIS 长度+1
     *    - 否则，二分找到 tails 中第一个 >= x 的位置，替换它
     *
     * 4. 最终 tails 的有效长度就是 LIS 长度。
     *
     * 【举例】nums = [10, 9, 2, 5, 3, 7, 101, 18]
     *   x=10:  tails=[10]
     *   x=9:   替换 → tails=[9]
     *   x=2:   替换 → tails=[2]
     *   x=5:   追加 → tails=[2,5]
     *   x=3:   替换 → tails=[2,3]
     *   x=7:   追加 → tails=[2,3,7]
     *   x=101: 追加 → tails=[2,3,7,101]
     *   x=18:  替换 → tails=[2,3,7,18]
     *   答案 = 4
     *
     * 【时间复杂度】O(n log n) 每个元素做一次二分查找
     * 【空间复杂度】O(n)
     */
    public int lengthOfLISBisect(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;

        for (int x : nums) {
            int lo = 0, hi = size;
            while (lo < hi) {
                int mid = lo + (hi - lo) / 2;
                if (tails[mid] < x) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            tails[lo] = x;
            if (lo == size) size++;
        }

        return size;
    }
}
