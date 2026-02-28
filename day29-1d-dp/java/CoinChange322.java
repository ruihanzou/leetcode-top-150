/**
 * LeetCode 322. Coin Change
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个整数数组 coins 表示不同面额的硬币，以及一个整数 amount 表示总金额。
 * 计算并返回可以凑成总金额所需的最少的硬币个数。如果没有任何一种硬币组合能组成总金额，返回 -1。
 * 每种硬币的数量是无限的。
 *
 * 示例：coins = [1,2,5], amount = 11 → 输出 3（11 = 5 + 5 + 1）
 *
 * 【拓展练习】
 * 1. LeetCode 518. Coin Change II —— 统计凑成金额的组合数（完全背包计数）
 * 2. LeetCode 279. Perfect Squares —— 最少完全平方数之和等于n
 * 3. LeetCode 983. Minimum Cost For Tickets —— 旅行费用的最小花费DP
 */

import java.util.*;

class CoinChange322 {

    /**
     * ==================== 解法一：DP 自底向上（完全背包） ====================
     *
     * 【核心思路】
     * dp[i] 表示凑成金额 i 所需的最少硬币数。
     * 对于每个金额 i，枚举所有硬币面值 c，
     * 如果 i >= c 且 dp[i-c] 不是无穷大，则 dp[i] = min(dp[i], dp[i-c] + 1)。
     *
     * 【思考过程】
     * 1. 这是经典的完全背包问题。"完全"意味着每种硬币可以用无限次。
     *
     * 2. 状态定义：dp[i] = 凑成金额 i 的最少硬币数。
     *    目标：dp[amount]。
     *    初始值：dp[0] = 0（金额为0不需要硬币），其余 dp[i] = ∞。
     *
     * 3. 转移方程：dp[i] = min(dp[i - c] + 1) 对所有 c ∈ coins 且 c <= i。
     *    直觉：凑金额 i 的最后一枚硬币是 c，那么剩余金额 i-c 需要 dp[i-c] 枚。
     *
     * 4. 如果 dp[amount] 仍然是 ∞，说明无法凑出，返回 -1。
     *
     * 【举例】coins = [1, 2, 5], amount = 11
     *   dp[0]=0
     *   dp[1]=dp[0]+1=1                   (用1枚1元)
     *   dp[2]=min(dp[1]+1, dp[0]+1)=1     (用1枚2元)
     *   dp[3]=min(dp[2]+1, dp[1]+1)=2     (1+2)
     *   dp[5]=min(..., dp[0]+1)=1         (1枚5元)
     *   dp[10]=2  (5+5)
     *   dp[11]=3  (5+5+1)
     *
     * 【时间复杂度】O(amount * n)，n 为硬币种类数
     * 【空间复杂度】O(amount)
     */
    public int coinChangeDp(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int c : coins) {
                if (c <= i && dp[i - c] < amount + 1) {
                    dp[i] = Math.min(dp[i], dp[i - c] + 1);
                }
            }
        }

        return dp[amount] <= amount ? dp[amount] : -1;
    }

    /**
     * ==================== 解法二：BFS ====================
     *
     * 【核心思路】
     * 将问题建模为最短路径：从 amount 出发，每次减去一个硬币面值，
     * 目标是到达 0。BFS 天然保证第一次到达 0 时步数最少。
     *
     * 【思考过程】
     * 1. 把金额看作图中的节点。从节点 v 到节点 v-c 有一条边（减去硬币 c）。
     *    问题变成：从 amount 到 0 的最短路径（步数 = 硬币数）。
     *
     * 2. BFS 按层扩展，每一层代表多用了一枚硬币。
     *    第一次到达 0 时的层数就是最少硬币数。
     *
     * 3. 用 visited 数组避免重复访问同一金额。
     *
     * 4. 如果 BFS 结束也没到达 0，返回 -1。
     *
     * 【举例】coins = [1, 2, 5], amount = 11
     *   第0层: {11}
     *   第1层: {10, 9, 6}
     *   第2层: {8, 7, 5, 4, 1}
     *   第3层: {3, 2, 0, ...} → 到达 0！
     *   答案 = 3
     *
     * 【时间复杂度】O(amount * n)
     * 【空间复杂度】O(amount)
     */
    public int coinChangeBfs(int[] coins, int amount) {
        if (amount == 0) return 0;

        boolean[] visited = new boolean[amount + 1];
        visited[amount] = true;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(amount);
        int steps = 0;

        while (!queue.isEmpty()) {
            steps++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int curr = queue.poll();
                for (int c : coins) {
                    int nxt = curr - c;
                    if (nxt == 0) return steps;
                    if (nxt > 0 && !visited[nxt]) {
                        visited[nxt] = true;
                        queue.offer(nxt);
                    }
                }
            }
        }

        return -1;
    }
}
