/**
 * LeetCode 188. Best Time to Buy and Sell Stock IV
 * 难度: Hard
 *
 * 题目描述：
 * 给你一个整数数组 prices 和一个整数 k，其中 prices[i] 是某支给定的股票在第 i 天的价格。
 * 设计一个算法来计算你所能获取的最大利润。你最多可以完成 k 笔交易。
 * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
 *
 * 示例 1：
 * 输入：k = 2, prices = [2,4,1]
 * 输出：2
 * 解释：第1天买入（价格=2），第2天卖出（价格=4），利润=2。
 *
 * 示例 2：
 * 输入：k = 2, prices = [3,2,6,5,0,3]
 * 输出：7
 * 解释：第2天买入（价格=2），第3天卖出（价格=6），利润=4。
 *       然后第5天买入（价格=0），第6天卖出（价格=3），利润=3。总利润=7。
 *
 * 【拓展练习】
 * 1. LeetCode 121. Best Time to Buy and Sell Stock —— 最多一次交易
 * 2. LeetCode 122. Best Time to Buy and Sell Stock II —— 不限次数
 * 3. LeetCode 309. Best Time to Buy and Sell Stock with Cooldown —— 含冷冻期
 */

class BuySellStockIV188 {

    /**
     * ==================== 解法一：状态机DP ====================
     *
     * 【核心思路】
     * dp[j][0] 表示最多完成 j 笔交易、当前不持有股票的最大利润。
     * dp[j][1] 表示最多完成 j 笔交易、当前持有股票的最大利润。
     * 每天更新所有状态：
     * dp[j][0] = max(dp[j][0], dp[j][1] + price)  // 卖出
     * dp[j][1] = max(dp[j][1], dp[j-1][0] - price)  // 买入（消耗一次交易机会）
     *
     * 【思考过程】
     * 1. 交易过程可以建模为状态机：每笔交易包含"买入"和"卖出"两个动作。
     *    在第 j 笔交易中：
     *    - 买入：从"已完成 j-1 笔且空仓"转移到"正在进行第 j 笔且持有"
     *    - 卖出：从"正在进行第 j 笔且持有"转移到"已完成 j 笔且空仓"
     *
     * 2. 对于每一天的价格，从 k 到 1 倒序更新（避免同一天重复使用）：
     *    先更新卖出状态，再更新买入状态。
     *
     * 3. 初始化：dp[j][0] = 0（不持有=利润 0），dp[j][1] = -∞（不可能未买就持有）。
     *
     * 4. 最终答案是 dp[k][0]——最多 k 笔交易且不持有股票。
     *
     * 【举例】k=2, prices = [3,2,6,5,0,3]
     *   初始: dp[1]=[0,-inf], dp[2]=[0,-inf]
     *   day 0 (3): dp[1]=[0,-3], dp[2]=[0,-3]
     *   day 1 (2): dp[1]=[0,-2], dp[2]=[0,-2]
     *   day 2 (6): dp[1]=[4,-2], dp[2]=[4,-2]
     *   day 3 (5): dp[1]=[4,-2], dp[2]=[4,-1]  ← buy2 = sell1-5 = 4-5 = -1
     *   day 4 (0): dp[1]=[4,0], dp[2]=[4,4]    ← buy2 = sell1-0 = 4
     *   day 5 (3): dp[1]=[4,0], dp[2]=[7,4]    ← sell2 = buy2+3 = 4+3 = 7
     *   答案 dp[2][0] = 7
     *
     * 【时间复杂度】O(n * k)
     * 【空间复杂度】O(k)
     */
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k == 0) return 0;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        int[][] dp = new int[k + 1][2];
        for (int j = 0; j <= k; j++) {
            dp[j][1] = Integer.MIN_VALUE;
        }

        for (int price : prices) {
            for (int j = k; j >= 1; j--) {
                dp[j][0] = Math.max(dp[j][0], dp[j][1] + price);
                dp[j][1] = Math.max(dp[j][1], dp[j - 1][0] - price);
            }
        }

        return dp[k][0];
    }

    /**
     * ==================== 解法二：当 k ≥ n/2 时退化为贪心 ====================
     *
     * 【核心思路】
     * 当 k ≥ n/2 时，交易次数不再是瓶颈（n 天内最多 n/2 笔交易），
     * 问题退化为"不限次数"的 LeetCode 122，用贪心即可。
     * 结合解法一，先判断 k 的大小再选择算法。
     *
     * 【思考过程】
     * 1. n 天最多买卖 n/2 次（每次交易至少占 2 天）。
     *    如果 k ≥ n/2，等价于交易次数无限制。
     *
     * 2. 无限制时，贪心策略：只要明天比今天贵就买卖，累加所有上涨的差价。
     *
     * 3. 这个优化很关键：如果不加这个判断，当 k 很大（比如 10^9）时，
     *    O(n*k) 的 dp 会超时。加了之后最坏是 O(n * min(k, n/2))。
     *
     * 【举例】k=100, prices = [1,2,3,4,5]
     *   k=100 ≥ n/2=2，退化为贪心：
     *   (2-1) + (3-2) + (4-3) + (5-4) = 4
     *
     * 【时间复杂度】O(n * min(k, n/2))
     * 【空间复杂度】O(min(k, n/2))
     */
    private int maxProfitUnlimited(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }
}
