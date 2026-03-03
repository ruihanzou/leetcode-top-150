/**
 * LeetCode 918. Maximum Sum Circular Subarray
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个长度为 n 的环形整数数组 nums，返回 nums 的非空子数组的最大可能和。
 * 环形数组意味着数组的末端与开头相连。即 nums[i] 的下一个元素是 nums[(i+1)%n]，
 * nums[i] 的前一个元素是 nums[(i-1+n)%n]。
 * 子数组最多只能包含固定缓冲区 nums 中的每个元素一次。
 *
 * 示例 1：nums = [1,-2,3,-2] → 输出 3（子数组 [3]）
 * 示例 2：nums = [5,-3,5] → 输出 10（子数组 [5,5]，环形首尾相连）
 * 示例 3：nums = [-3,-2,-3] → 输出 -2（子数组 [-2]）
 *
 * 【拓展练习】
 * 1. LeetCode 53. Maximum Subarray —— 非环形版本，Kadane算法基础
 * 2. LeetCode 152. Maximum Product Subarray —— 最大乘积子数组
 * 3. LeetCode 1191. K-Concatenation Maximum Sum —— k次拼接数组的最大子数组和
 */

import java.util.ArrayDeque;
import java.util.Deque;

class MaxSumCircularSubarray918 {

    /**
     * ==================== 解法一：Kadane 变体 ====================
     *
     * 【核心思路】
     * 环形子数组的最大和有两种情况：
     * (1) 最大子数组不跨越边界 → 普通 Kadane 求 maxSum
     * (2) 最大子数组跨越边界 → 等价于"总和 - 中间最小子数组和"
     * 答案 = max(maxSum, totalSum - minSum)。
     * 特殊情况：如果所有元素都为负，minSum = totalSum，此时答案取 maxSum。
     *
     * 【思考过程】
     * 1. 环形子数组如果跨越首尾边界，那它包含数组尾部一段 + 头部一段。
     *    中间"不被选中的部分"恰好是一个连续子数组。
     *
     * 2. 要最大化跨边界的子数组和，等价于最小化中间不选的部分。
     *    即 totalSum - minSubarraySum。
     *
     * 3. 所以只需一次遍历同时求 maxSum 和 minSum：
     *    - maxSum：标准 Kadane（维护以当前结尾的最大和）
     *    - minSum：对称的 Kadane（维护以当前结尾的最小和）
     *
     * 4. 边界情况：如果所有元素为负，maxSum = max(nums)（负数），
     *    而 totalSum - minSum = 0（选了空子数组），但题目要求非空。
     *    所以当 maxSum < 0 时，直接返回 maxSum。
     *
     * 【举例】nums = [5, -3, 5]
     *   totalSum = 7
     *   Kadane 求 maxSum：
     *     i=0: curMax=5, maxSum=5
     *     i=1: curMax=max(5-3,-3)=2, maxSum=5
     *     i=2: curMax=max(2+5,5)=7, maxSum=7
     *   Kadane 求 minSum：
     *     i=0: curMin=5, minSum=5
     *     i=1: curMin=min(5-3,-3)=-3, minSum=-3
     *     i=2: curMin=min(-3+5,5)=2, minSum=-3
     *   答案 = max(7, 7-(-3)) = max(7, 10) = 10
     *   对应跨边界子数组 [5, 5]（跳过中间的 -3）
     *
     * 【时间复杂度】O(n) 一次遍历
     * 【空间复杂度】O(1)
     */
    public int maxSubarraySumCircularKadane(int[] nums) {
        int totalSum = 0;
        int maxSum = Integer.MIN_VALUE, curMax = 0;
        int minSum = Integer.MAX_VALUE, curMin = 0;

        for (int num : nums) {
            totalSum += num;
            curMax = Math.max(curMax + num, num);
            maxSum = Math.max(maxSum, curMax);
            curMin = Math.min(curMin + num, num);
            minSum = Math.min(minSum, curMin);
        }

        if (maxSum < 0) return maxSum;
        return Math.max(maxSum, totalSum - minSum);
    }

    /**
     * ==================== 解法二：单调队列（前缀和 + 滑动窗口） ====================
     *
     * 【核心思路】
     * 将数组"拉直"成长度 2n 的前缀和序列。环形子数组的和 = prefix[j] - prefix[i]，
     * 其中 j - i <= n。用单调递增队列维护窗口内的最小前缀和，
     * 对每个 j 求 prefix[j] - min(prefix[i])，i ∈ [j-n, j-1]。
     *
     * 【思考过程】
     * 1. 环形数组可以看作把数组复制一份接在后面：nums + nums。
     *    任何长度不超过 n 的子数组都对应复制数组中的一段。
     *
     * 2. 前缀和 prefix[0..2n]，子数组 [i,j) 的和 = prefix[j] - prefix[i]。
     *    约束：1 <= j-i <= n，即窗口大小最多 n。
     *
     * 3. 固定 j，要最大化 prefix[j] - prefix[i] → 最小化 prefix[i]。
     *    i 的范围是 [j-n, j-1]，这是一个滑动窗口求最小值的问题。
     *
     * 4. 用单调递增队列（双端队列）维护窗口内的最小前缀和：
     *    - 队头是当前窗口最小值
     *    - 新元素从队尾入，维护递增性（弹出队尾所有 >= 新元素的值）
     *    - 窗口滑出时从队头弹出
     *
     * 【举例】nums = [5, -3, 5], n=3
     *   拉直前缀和：prefix = [0, 5, 2, 7, 12, 9, 14]
     *   j=1: 窗口[0,0], min prefix=0, ans=5-0=5
     *   j=2: 窗口[0,1], min prefix=0, ans=max(5,2-0)=5
     *   j=3: 窗口[0,2], min prefix=0, ans=max(5,7-0)=7
     *   j=4: 窗口[1,3], min prefix=2, ans=max(7,12-2)=10
     *   j=5: 窗口[2,4], min prefix=2, ans=max(10,9-2)=10
     *   j=6: 窗口[3,5], min prefix=7, ans=max(10,14-7)=10
     *   答案 10
     *
     * 【时间复杂度】O(n) 每个元素入队出队各一次
     * 【空间复杂度】O(n) 队列和前缀和数组
     */
    public int maxSubarraySumCircularDeque(int[] nums) {
        int n = nums.length;
        long[] prefix = new long[2 * n + 1];
        for (int i = 0; i < 2 * n; i++) {
            prefix[i + 1] = prefix[i] + nums[i % n];
        }

        long ans = Long.MIN_VALUE;
        Deque<Integer> deque = new ArrayDeque<>();
        deque.offerLast(0);

        for (int j = 1; j <= 2 * n; j++) {
            while (!deque.isEmpty() && deque.peekFirst() < j - n) {
                deque.pollFirst();
            }
            ans = Math.max(ans, prefix[j] - prefix[deque.peekFirst()]);
            while (!deque.isEmpty() && prefix[deque.peekLast()] >= prefix[j]) {
                deque.pollLast();
            }
            deque.offerLast(j);
        }

        return (int) ans;
    }
}
