/**
 * LeetCode 238. Product of Array Except Self
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个整数数组 nums，返回数组 answer，其中 answer[i] 等于 nums 中
 * 除 nums[i] 之外其余所有元素的乘积。
 * 题目保证任意前缀或后缀的乘积都在 32 位整数范围内。
 * 要求：不能使用除法，且时间复杂度为 O(n)。
 *
 * 示例 1：
 *   输入：nums = [1,2,3,4]
 *   输出：[24,12,8,6]
 *
 * 示例 2：
 *   输入：nums = [-1,1,0,-3,3]
 *   输出：[0,0,9,0,0]
 *
 * 【拓展练习】
 * 1. 如果允许使用除法，能否用 O(1) 额外空间解决？需要注意什么边界情况？
 * 2. 如果要求结果对某个质数取模，如何处理？
 */
class ProductOfArrayExceptSelf238 {

    /**
     * ==================== 解法一：两个数组（左积 + 右积）====================
     *
     * 【核心思路】
     * 分别构建前缀积数组和后缀积数组，answer[i] = left[i] * right[i]。
     *
     * 【思考过程】
     * 1. left[i] 表示 nums[0..i-1] 的乘积（i 左边所有元素之积）
     * 2. right[i] 表示 nums[i+1..n-1] 的乘积（i 右边所有元素之积）
     * 3. answer[i] = left[i] * right[i] 就是除了 nums[i] 之外的乘积
     * 4. left 从左往右累乘构建，right 从右往左累乘构建
     *
     * 【举例】nums = [1, 2, 3, 4]
     * left:  [1, 1, 2, 6]     （left[0]=1, left[1]=1, left[2]=1*2=2, left[3]=1*2*3=6）
     * right: [24, 12, 4, 1]   （right[3]=1, right[2]=4, right[1]=3*4=12, right[0]=2*3*4=24）
     * answer: [1*24, 1*12, 2*4, 6*1] = [24, 12, 8, 6]
     *
     * 【时间复杂度】O(n) — 三次遍历
     * 【空间复杂度】O(n) — left 和 right 数组
     */
    public int[] productExceptSelfTwoArrays(int[] nums) {
        int n = nums.length;
        int[] left = new int[n];
        int[] right = new int[n];
        int[] answer = new int[n];

        left[0] = 1;
        for (int i = 1; i < n; i++) {
            left[i] = left[i - 1] * nums[i - 1];
        }

        right[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            right[i] = right[i + 1] * nums[i + 1];
        }

        for (int i = 0; i < n; i++) {
            answer[i] = left[i] * right[i];
        }
        return answer;
    }

    /**
     * ==================== 解法二：单数组优化（先存左积，再乘右积）====================
     *
     * 【核心思路】
     * 用 answer 数组先存左积，再从右往左用一个变量累乘右积，原地完成计算。
     * （题目说明输出数组不算额外空间）
     *
     * 【思考过程】
     * 1. 第一遍：从左到右，answer[i] = 左边所有元素之积
     * 2. 第二遍：从右到左，维护变量 rightProduct 表示右边累积
     *    answer[i] *= rightProduct，然后 rightProduct *= nums[i]
     * 3. 这样省去了单独的 right 数组
     *
     * 【举例】nums = [1, 2, 3, 4]
     * 第一遍（左积）: answer = [1, 1, 2, 6]
     * 第二遍（乘上右积，rightProduct 初始为 1）:
     *   i=3: answer[3] = 6*1 = 6,  rightProduct = 1*4 = 4
     *   i=2: answer[2] = 2*4 = 8,  rightProduct = 4*3 = 12
     *   i=1: answer[1] = 1*12 = 12, rightProduct = 12*2 = 24
     *   i=0: answer[0] = 1*24 = 24, rightProduct = 24*1 = 24
     * 结果: [24, 12, 8, 6]
     *
     * 【时间复杂度】O(n) — 两次遍历
     * 【空间复杂度】O(1) — 不算输出数组，只用了一个额外变量
     */
    public int[] productExceptSelfOptimized(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];

        answer[0] = 1;
        for (int i = 1; i < n; i++) {
            answer[i] = answer[i - 1] * nums[i - 1];
        }

        int rightProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= rightProduct;
            rightProduct *= nums[i];
        }
        return answer;
    }

    /**
     * ==================== 解法三：除法法（如果允许除法）====================
     *
     * 【核心思路】
     * 先算全部元素之积 totalProduct，answer[i] = totalProduct / nums[i]。
     * 需要特殊处理含 0 的情况。
     *
     * 【思考过程】
     * 1. 统计数组中 0 的个数 zeroCount
     * 2. 如果 zeroCount > 1：所有位置结果都是 0
     * 3. 如果 zeroCount == 1：只有 0 所在位置的结果 = 其他非零元素之积，其余为 0
     * 4. 如果 zeroCount == 0：answer[i] = totalProduct / nums[i]
     *
     * 【举例】nums = [-1, 1, 0, -3, 3]
     * zeroCount = 1, 非零元素之积 = (-1)*1*(-3)*3 = 9
     * 0 在 index=2, 所以 answer = [0, 0, 9, 0, 0]
     *
     * 【时间复杂度】O(n) — 两次遍历
     * 【空间复杂度】O(1) — 不算输出数组
     */
    public int[] productExceptSelfDivision(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        int totalProduct = 1;
        int zeroCount = 0;

        for (int num : nums) {
            if (num == 0) {
                zeroCount++;
            } else {
                totalProduct *= num;
            }
        }

        if (zeroCount > 1) {
            return answer;
        }

        for (int i = 0; i < n; i++) {
            if (zeroCount == 1) {
                answer[i] = (nums[i] == 0) ? totalProduct : 0;
            } else {
                answer[i] = totalProduct / nums[i];
            }
        }
        return answer;
    }
}
