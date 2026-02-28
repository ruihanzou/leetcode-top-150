"""
LeetCode 123. Best Time to Buy and Sell Stock III
难度: Hard

题目描述：
给定一个数组 prices，它的第 i 个元素是一支给定的股票在第 i 天的价格。
设计一个算法来计算你所能获取的最大利润。你最多可以完成两笔交易。
注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。

示例 1：
输入：prices = [3,3,5,0,0,3,1,4]
输出：6
解释：第4天买入（价格=0），第6天卖出（价格=3），利润=3。
      然后第7天买入（价格=1），第8天卖出（价格=4），利润=3。总利润=6。

示例 2：
输入：prices = [1,2,3,4,5]
输出：4

示例 3：
输入：prices = [7,6,4,3,1]
输出：0

【拓展练习】
1. LeetCode 121. Best Time to Buy and Sell Stock —— 只能交易一次
2. LeetCode 122. Best Time to Buy and Sell Stock II —— 不限交易次数
3. LeetCode 188. Best Time to Buy and Sell Stock IV —— 最多 k 次交易
"""

from typing import List


class Solution:
    """
    ==================== 解法一：状态机DP（4个状态变量） ====================

    【核心思路】
    用 4 个变量跟踪状态：
    - buy1: 完成第一次买入后的最大利润（负值表示成本）
    - sell1: 完成第一次卖出后的最大利润
    - buy2: 完成第二次买入后的最大利润
    - sell2: 完成第二次卖出后的最大利润
    每天遍历时更新这 4 个状态。

    【思考过程】
    1. 最多两次交易，可以分为 5 个阶段：
       未操作 → 第一次持有 → 第一次卖出 → 第二次持有 → 第二次卖出

    2. 每个阶段用一个变量维护到当前天为止该阶段能获得的最大利润：
       - buy1 = max(buy1, -price)：要么之前就买了，要么今天买
       - sell1 = max(sell1, buy1 + price)：要么之前就卖了，要么今天卖
       - buy2 = max(buy2, sell1 - price)：第二次买入基于第一次卖出的利润
       - sell2 = max(sell2, buy2 + price)：第二次卖出

    3. 注意更新顺序：同一天内可以"卖出再买入"（sell1 → buy2），
       所以 4 个变量按顺序更新不会出错。

    【举例】prices = [3,3,5,0,0,3,1,4]
      初始: buy1=-inf, sell1=0, buy2=-inf, sell2=0
      day 0 (3): buy1=-3, sell1=0, buy2=-3, sell2=0
      day 1 (3): buy1=-3, sell1=0, buy2=-3, sell2=0
      day 2 (5): buy1=-3, sell1=2, buy2=-3, sell2=2
      day 3 (0): buy1=0, sell1=2, buy2=2, sell2=2
      day 4 (0): buy1=0, sell1=2, buy2=2, sell2=2
      day 5 (3): buy1=0, sell1=3, buy2=2, sell2=5
      day 6 (1): buy1=0, sell1=3, buy2=2, sell2=5
      day 7 (4): buy1=0, sell1=4, buy2=2, sell2=6
      答案 sell2 = 6

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def maxProfit(self, prices: List[int]) -> int:
        buy1 = buy2 = float('-inf')
        sell1 = sell2 = 0

        for price in prices:
            buy1 = max(buy1, -price)
            sell1 = max(sell1, buy1 + price)
            buy2 = max(buy2, sell1 - price)
            sell2 = max(sell2, buy2 + price)

        return sell2

    """
    ==================== 解法二：通用k次交易DP（k=2） ====================

    【核心思路】
    这是 LeetCode 188 的通用解法特化到 k=2。
    用 dp[k][0/1] 表示最多完成 k 笔交易、当前是否持有股票时的最大利润。
    dp[j][0] = max(dp[j][0], dp[j][1] + price)  # 卖出
    dp[j][1] = max(dp[j][1], dp[j-1][0] - price)  # 买入

    【思考过程】
    1. 将"最多 k 次交易"一般化：用二维状态 (交易次数, 是否持有)。

    2. 买入时消耗一次交易机会：dp[j][1] 的转移来自 dp[j-1][0]。
       卖出不消耗：dp[j][0] 的转移来自 dp[j][1]。

    3. k=2 时只需要 dp[0..2][0..1] 共 6 个状态，和解法一本质相同。

    4. 这种写法的优势是代码可直接推广到任意 k。

    【举例】prices = [3,3,5,0,0,3,1,4], k=2
      同解法一，dp[1][0]=sell1, dp[1][1]=buy1, dp[2][0]=sell2, dp[2][1]=buy2
      最终 dp[2][0] = 6

    【时间复杂度】O(n * k) = O(n)，k=2
    【空间复杂度】O(k) = O(1)
    """
    def maxProfit_general(self, prices: List[int]) -> int:
        k = 2
        dp = [[0, float('-inf')] for _ in range(k + 1)]

        for price in prices:
            for j in range(k, 0, -1):
                dp[j][0] = max(dp[j][0], dp[j][1] + price)
                dp[j][1] = max(dp[j][1], dp[j - 1][0] - price)

        return dp[k][0]
