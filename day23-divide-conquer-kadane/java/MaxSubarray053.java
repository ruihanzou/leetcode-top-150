/**
 * LeetCode 53. Maximum Subarray
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个整数数组 nums，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），
 * 返回其最大和。
 *
 * 示例：nums = [-2,1,-3,4,-1,2,1,-5,4] → 输出 6
 *   解释：连续子数组 [4,-1,2,1] 的和最大，为 6。
 *
 * 【拓展练习】
 * 1. LeetCode 918. Maximum Sum Circular Subarray —— 环形数组版本的最大子数组和
 * 2. LeetCode 152. Maximum Product Subarray —— 最大乘积子数组，需同时维护最大最小值
 * 3. LeetCode 121. Best Time to Buy and Sell Stock —— Kadane算法思想的经典变体
 */

class MaxSubarray053 {

    /**
     * ==================== 解法一：Kadane 算法（动态规划） ====================
     *
     * 【核心思路】
     * 维护"以当前元素结尾的最大子数组和" curMax。
     * 对于每个新元素，要么接上前面的子数组（curMax + nums[i]），
     * 要么从自己开始新的子数组（nums[i]），取较大者。
     * 全局最大值 globalMax 记录过程中出现的最大 curMax。
     *
     * 【思考过程】
     * 1. 定义子问题：dp[i] = 以 nums[i] 结尾的最大子数组和。
     *    转移：dp[i] = max(dp[i-1] + nums[i], nums[i])
     *        = max(dp[i-1], 0) + nums[i]
     *    含义：如果前面累积的和是正的，接上有利；否则不如从头开始。
     *
     * 2. dp[i] 只依赖 dp[i-1]，所以只需一个变量 curMax 即可，空间 O(1)。
     *
     * 3. 答案 = max(dp[0], dp[1], ..., dp[n-1])，即全局最大值。
     *
     * 【举例】nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
     *   i=0: curMax=max(-2,-2)=-2,  globalMax=-2
     *   i=1: curMax=max(-2+1,1)=1,  globalMax=1
     *   i=2: curMax=max(1-3,-3)=-2, globalMax=1
     *   i=3: curMax=max(-2+4,4)=4,  globalMax=4
     *   i=4: curMax=max(4-1,-1)=3,  globalMax=4
     *   i=5: curMax=max(3+2,2)=5,   globalMax=5
     *   i=6: curMax=max(5+1,1)=6,   globalMax=6
     *   i=7: curMax=max(6-5,-5)=1,  globalMax=6
     *   i=8: curMax=max(1+4,4)=5,   globalMax=6
     *   答案 6
     *
     * 【时间复杂度】O(n) 一次遍历
     * 【空间复杂度】O(1)
     */
    public int maxSubArrayKadane(int[] nums) {
        int curMax = nums[0];
        int globalMax = nums[0];
        for (int i = 1; i < nums.length; i++) {
            curMax = Math.max(curMax + nums[i], nums[i]);
            globalMax = Math.max(globalMax, curMax);
        }
        return globalMax;
    }

    /**
     * ==================== 解法二：分治法 ====================
     *
     * 【核心思路】
     * 将数组从中间一分为二：
     * - 最大子数组要么完全在左半边
     * - 要么完全在右半边
     * - 要么跨越中间（从中间向两边延伸）
     * 递归求左半和右半的最大子数组，再求跨中间的最大子数组，三者取最大。
     *
     * 【思考过程】
     * 1. 分治的关键是"跨中间"的情况怎么处理。
     *    从 mid 向左扫描，累加取最大 → leftMax。
     *    从 mid+1 向右扫描，累加取最大 → rightMax。
     *    跨中间最大 = leftMax + rightMax。
     *
     * 2. 左半和右半递归求解，base case 是单个元素。
     *
     * 3. T(n) = 2T(n/2) + O(n)（合并步 O(n) 扫描中间）→ O(n log n)。
     *    比 Kadane 慢，但这是分治法的经典范例。
     *
     * 【举例】nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
     *   mid=4, 分成 [-2,1,-3,4] 和 [-1,2,1,-5,4]
     *   左半最大子数组 = [4] → 4
     *   右半最大子数组 = [2,1] → 3  (实际是[-1,2,1]=2)
     *   跨中间：从 mid=4 往左 → 4+(-3)+1+(-2)=0, 最大=4
     *           从 mid+1=5 往右 → (-1)=-1, 最大=-1? 不对
     *           实际：从 index 3 往左累加，从 index 4 往右累加
     *   三者取最大 → 6 (跨中间 [4,-1,2,1])
     *
     * 【时间复杂度】O(n log n)
     * 【空间复杂度】O(log n) 递归栈
     */
    public int maxSubArrayDivide(int[] nums) {
        return divideConquer(nums, 0, nums.length - 1);
    }

    private int divideConquer(int[] nums, int lo, int hi) {
        if (lo == hi) return nums[lo];
        int mid = lo + (hi - lo) / 2;

        int leftMax = divideConquer(nums, lo, mid);
        int rightMax = divideConquer(nums, mid + 1, hi);

        int leftCross = Integer.MIN_VALUE, sum = 0;
        for (int i = mid; i >= lo; i--) {
            sum += nums[i];
            leftCross = Math.max(leftCross, sum);
        }

        int rightCross = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= hi; i++) {
            sum += nums[i];
            rightCross = Math.max(rightCross, sum);
        }

        return Math.max(Math.max(leftMax, rightMax), leftCross + rightCross);
    }

    /**
     * ==================== 解法三：前缀和 ====================
     *
     * 【核心思路】
     * 子数组 nums[i..j] 的和 = prefix[j+1] - prefix[i]。
     * 要最大化 prefix[j+1] - prefix[i]（其中 i <= j），
     * 等价于对每个 j，找 j 之前（含 0）的最小 prefix[i]。
     * 从左到右扫描，维护前缀和最小值即可。
     *
     * 【思考过程】
     * 1. prefix[0] = 0, prefix[k] = nums[0] + ... + nums[k-1]。
     *    子数组 [i,j] 的和 = prefix[j+1] - prefix[i]。
     *
     * 2. 固定右端 j，要最大化和 → 最小化 prefix[i]，其中 0 <= i <= j。
     *    → 扫描过程中维护 minPrefix = min(prefix[0..j])。
     *
     * 3. 对每个位置 j+1，更新 ans = max(ans, prefix[j+1] - minPrefix)，
     *    然后更新 minPrefix = min(minPrefix, prefix[j+1])。
     *
     * 4. 本质上和 Kadane 算法等价，但从前缀和的视角理解，
     *    与"买卖股票"问题（LeetCode 121）异曲同工。
     *
     * 【举例】nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
     *   prefix = [0, -2, -1, -4, 0, -1, 1, 2, -3, 1]
     *   扫描过程中 minPrefix 从 0 开始：
     *   j=0: prefix[1]=-2, ans=max(-∞, -2-0)=-2, minPrefix=min(0,-2)=-2
     *   j=1: prefix[2]=-1, ans=max(-2, -1-(-2))=1, minPrefix=-2
     *   ...
     *   j=6: prefix[7]=2, ans=max(5, 2-(-4))=6, minPrefix=-4
     *   最终 ans=6
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1) 不需要存储完整前缀和数组
     */
    public int maxSubArrayPrefix(int[] nums) {
        int ans = Integer.MIN_VALUE;
        int prefixSum = 0;
        int minPrefix = 0;

        for (int num : nums) {
            prefixSum += num;
            ans = Math.max(ans, prefixSum - minPrefix);
            minPrefix = Math.min(minPrefix, prefixSum);
        }

        return ans;
    }
}
