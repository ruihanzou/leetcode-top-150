/**
 * LeetCode 122. Best Time to Buy and Sell Stock II
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个整数数组 prices，其中 prices[i] 表示某支股票第 i 天的价格。
 * 每天你可以决定买入或卖出股票，但任何时候最多只能持有一股。
 * 你可以在同一天买入并卖出（即无限次交易）。返回你能获得的最大利润。
 *
 * 示例：prices = [7,1,5,3,6,4] → 输出 7（第2天买1，第3天卖5赚4；第4天买3，第5天卖6赚3）
 *       prices = [1,2,3,4,5] → 输出 4（第1天买入，第5天卖出）
 *       prices = [7,6,4,3,1] → 输出 0（价格持续下降，不交易）
 *
 * 约束：1 <= prices.length <= 3 * 10^4, 0 <= prices[i] <= 10^4
 *
 * 【拓展练习】
 * 1. LeetCode 121. Best Time to Buy and Sell Stock —— 只允许一次交易
 * 2. LeetCode 123. Best Time to Buy and Sell Stock III —— 最多两次交易
 * 3. LeetCode 309. Best Time to Buy and Sell Stock with Cooldown —— 卖出后有冷冻期
 */

class BestTimeToBuyAndSellStockII122 {

    /**
     * ==================== 解法一：贪心（累加正差值） ====================
     *
     * 【核心思路】
     * 只要明天比今天贵，就今天买明天卖，累加所有正的差值就是最大利润。
     *
     * 【思考过程】
     * 1. 可以无限次交易，所以可以捕获每一段上涨。
     *
     * 2. 一段连续上涨 prices[i] < prices[i+1] < ... < prices[j] 的总利润
     *    = prices[j] - prices[i]
     *    = (prices[i+1]-prices[i]) + (prices[i+2]-prices[i+1]) + ... + (prices[j]-prices[j-1])。
     *    即等于每日正差值的累加。
     *
     * 3. 所以我们只需要遍历一遍，把所有 prices[i]-prices[i-1] > 0 的差值加起来。
     *
     * 【举例】prices = [7,1,5,3,6,4]
     *   差值 = [-6, 4, -2, 3, -2]
     *   正差值 = [4, 3]
     *   总利润 = 4 + 3 = 7
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int maxProfitGreedy(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    /**
     * ==================== 解法二：动态规划 ====================
     *
     * 【核心思路】
     * 每天有两种状态：持有股票（hold）和不持有股票（cash）。
     * 状态转移：
     *   cash[i] = max(cash[i-1], hold[i-1] + prices[i])  // 不动 or 卖出
     *   hold[i] = max(hold[i-1], cash[i-1] - prices[i])  // 不动 or 买入
     *
     * 【思考过程】
     * 1. 定义状态：
     *    - cash：当天结束时不持有股票的最大利润
     *    - hold：当天结束时持有股票的最大利润
     *
     * 2. 每天有两个选择：
     *    - 如果当前不持有：可以从"昨天不持有"（不动），或"昨天持有+今天卖出"转移
     *    - 如果当前持有：可以从"昨天持有"（不动），或"昨天不持有+今天买入"转移
     *
     * 3. 最终答案是最后一天不持有股票的最大利润 cash。
     *
     * 4. 因为只用到前一天的状态，所以空间可以压缩到 O(1)。
     *
     * 【举例】prices = [7,1,5,3,6,4]
     *   初始: cash=0, hold=-∞
     *   day0(7): cash=max(0,-∞+7)=0,    hold=max(-∞,0-7)=-7
     *   day1(1): cash=max(0,-7+1)=0,    hold=max(-7,0-1)=-1
     *   day2(5): cash=max(0,-1+5)=4,    hold=max(-1,0-5)=-1  → 注意此时cash用的是上一轮的cash=0
     *   day3(3): cash=max(4,-1+3)=4,    hold=max(-1,4-3)=1
     *   day4(6): cash=max(4,1+6)=7,     hold=max(1,4-6)=1
     *   day5(4): cash=max(7,1+4)=7,     hold=max(1,7-4)=3
     *   答案 = cash = 7
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int maxProfitDP(int[] prices) {
        int cash = 0;
        int hold = Integer.MIN_VALUE;

        for (int price : prices) {
            int prevCash = cash;
            cash = Math.max(cash, hold + price);
            hold = Math.max(hold, prevCash - price);
        }

        return cash;
    }
}
