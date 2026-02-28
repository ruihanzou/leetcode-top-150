/**
 * LeetCode 121. Best Time to Buy and Sell Stock
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个数组 prices，其中 prices[i] 表示某支股票第 i 天的价格。
 * 你只能选择某一天买入股票，并选择在未来的另一天卖出。
 * 返回你可以从这笔交易中获取的最大利润。如果无法获利，返回 0。
 *
 * 示例：prices = [7,1,5,3,6,4] → 输出 5（第2天买入价格1，第5天卖出价格6）
 *       prices = [7,6,4,3,1] → 输出 0（价格持续下降，无法获利）
 *
 * 约束：1 <= prices.length <= 10^5, 0 <= prices[i] <= 10^4
 *
 * 【拓展练习】
 * 1. LeetCode 122. Best Time to Buy and Sell Stock II —— 可多次交易
 * 2. LeetCode 123. Best Time to Buy and Sell Stock III —— 最多两次交易
 * 3. LeetCode 53. Maximum Subarray —— Kadane 算法的经典应用
 */

class BestTimeToBuyAndSellStock121 {

    /**
     * ==================== 解法一：暴力双循环 ====================
     *
     * 【核心思路】
     * 枚举所有买入日和卖出日的组合，取利润最大值。
     *
     * 【思考过程】
     * 1. 最直接的想法：尝试每一对 (买入日, 卖出日)，计算差价。
     *
     * 2. 买入日 i 从 0 到 n-2，卖出日 j 从 i+1 到 n-1，
     *    利润 = prices[j] - prices[i]，取最大值。
     *
     * 【举例】prices = [7,1,5,3,6,4]
     *   i=0: 7买入，最大卖出价6 → 利润-1（亏损）
     *   i=1: 1买入，最大卖出价6 → 利润5 ✓
     *   i=2: 5买入，最大卖出价6 → 利润1
     *   ... 最大利润 = 5
     *
     * 【时间复杂度】O(n²)
     * 【空间复杂度】O(1)
     */
    public int maxProfitBrute(int[] prices) {
        int maxProfit = 0;
        for (int i = 0; i < prices.length - 1; i++) {
            for (int j = i + 1; j < prices.length; j++) {
                maxProfit = Math.max(maxProfit, prices[j] - prices[i]);
            }
        }
        return maxProfit;
    }

    /**
     * ==================== 解法二：一次遍历（维护最小值） ====================
     *
     * 【核心思路】
     * 遍历价格数组，维护"到目前为止的历史最低价"，
     * 每天用当天价格减去历史最低价来更新最大利润。
     *
     * 【思考过程】
     * 1. 在第 i 天卖出能获得的最大利润 = prices[i] - min(prices[0..i-1])。
     *    所以我们只需要记录前面出现过的最小价格。
     *
     * 2. 遍历时同步维护 minPrice（历史最低价）和 maxProfit（最大利润），
     *    每到一天：先尝试用当天价格更新 maxProfit，再用当天价格更新 minPrice。
     *
     * 3. 一次遍历即可完成，O(n) 时间。
     *
     * 【举例】prices = [7,1,5,3,6,4]
     *   初始 minPrice=7, maxProfit=0
     *   i=0: price=7, profit=0, minPrice=7
     *   i=1: price=1, profit=0, minPrice=1
     *   i=2: price=5, profit=5-1=4, minPrice=1
     *   i=3: price=3, profit=max(4,2)=4, minPrice=1
     *   i=4: price=6, profit=max(4,5)=5, minPrice=1
     *   i=5: price=4, profit=max(5,3)=5, minPrice=1
     *   答案 = 5
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int maxProfitOnePass(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            } else {
                maxProfit = Math.max(maxProfit, price - minPrice);
            }
        }

        return maxProfit;
    }

    /**
     * ==================== 解法三：动态规划（Kadane 变体） ====================
     *
     * 【核心思路】
     * 将问题转化为"最大子数组和"：计算相邻天的价格差 diff[i] = prices[i] - prices[i-1]，
     * 则最大利润等于 diff 数组的最大子数组和（连续子序列和最大值）。
     *
     * 【思考过程】
     * 1. 买入第 i 天卖出第 j 天的利润 = prices[j] - prices[i]
     *    = (prices[i+1]-prices[i]) + (prices[i+2]-prices[i+1]) + ... + (prices[j]-prices[j-1])
     *    = diff[i+1] + diff[i+2] + ... + diff[j]。
     *
     * 2. 这就是 diff 数组上一段连续子数组的和。
     *    求最大连续子数组和 → Kadane 算法。
     *
     * 3. Kadane：维护 currentMax（以当前位置结尾的最大子数组和），
     *    每步决定是"延续前面的子数组"还是"从当前位置重新开始"。
     *
     * 【举例】prices = [7,1,5,3,6,4]
     *   diff = [-6, 4, -2, 3, -2]
     *   Kadane:
     *     d=-6: currentMax=max(-6,0+(-6))=-6, maxProfit=0
     *     d=4:  currentMax=max(4,-6+4)=4, maxProfit=4
     *     d=-2: currentMax=max(-2,4+(-2))=2, maxProfit=4
     *     d=3:  currentMax=max(3,2+3)=5, maxProfit=5
     *     d=-2: currentMax=max(-2,5+(-2))=3, maxProfit=5
     *   答案 = 5
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int maxProfitKadane(int[] prices) {
        int maxProfit = 0;
        int currentMax = 0;

        for (int i = 1; i < prices.length; i++) {
            currentMax = Math.max(0, currentMax + prices[i] - prices[i - 1]);
            maxProfit = Math.max(maxProfit, currentMax);
        }

        return maxProfit;
    }
}
