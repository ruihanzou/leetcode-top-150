/**
 * LeetCode 502. IPO
 * 难度: Hard
 *
 * 题目描述：
 * 假设力扣（LeetCode）即将开始 IPO。为了以更高的价格将股票卖给风险投资公司，
 * 力扣希望在 IPO 之前开展一些项目以增加其资本。
 * 由于资源有限，它只能在 IPO 之前最多完成 k 个不同的项目。
 * 帮助力扣设计完成最多 k 个不同项目后得到最大总资本的方式。
 *
 * 给你 n 个项目。对于每个项目 i，它都有一个纯利润 profits[i]，
 * 以及启动该项目需要的最低资本 capital[i]。
 *
 * 最初，你的资本为 w。当你完成一个项目时，你将获得纯利润，
 * 且利润将被添加到你的总资本中。
 *
 * 总而言之，从给定项目中选择最多 k 个不同项目的列表，
 * 以最大化最终资本，并输出最终可获得的最多资本。
 *
 * 示例：k = 2, w = 0, profits = [1,2,3], capital = [0,1,1] → 输出 4
 * 解释：初始资本0 → 做项目0（利润1）→ 资本1 → 做项目2（利润3）→ 资本4
 *
 * 【拓展练习】
 * 1. LeetCode 253. Meeting Rooms II —— 会议室数量，堆的经典应用
 * 2. LeetCode 630. Course Schedule III —— 课程表III，贪心+最大堆
 * 3. LeetCode 871. Minimum Number of Refueling Stops —— 最少加油次数，贪心+堆
 */

import java.util.Arrays;
import java.util.PriorityQueue;

class IPO502 {

    /**
     * ==================== 解法一：贪心 + 两个堆 ====================
     *
     * 【核心思路】
     * 贪心策略：每次选择当前资本能启动的所有项目中利润最大的。
     * 用最小堆按资本排序管理未解锁项目，用最大堆按利润排序管理已解锁项目。
     *
     * 【思考过程】
     * 1. 核心贪心直觉：资本只会增加不会减少。做了一个项目后资本变多，
     *    能做的项目只会更多。所以每次贪心选利润最高的一定是最优的。
     *
     * 2. 证明贪心正确性：假设某次选了利润非最高的项目 A 而非最高的 B，
     *    交换选择顺序后，选 B 时资本更多（>=），之后仍能选 A，
     *    总利润不变但中间资本更高，能解锁更多项目。所以贪心不劣。
     *
     * 3. 实现：
     *    - 把所有项目按 (capital, profit) 放入最小堆
     *    - 每一轮：把所有 capital <= w 的项目取出，利润放入最大堆
     *    - 从最大堆取最大利润，w += profit
     *    - 重复 k 次
     *
     * 4. 为什么需要两个堆？
     *    - 最小堆：高效找到所有"当前可启动"的项目（资本 <= w 的）
     *    - 最大堆：在可启动项目中高效找利润最大的
     *
     * 【举例】k=2, w=0, profits=[1,2,3], capital=[0,1,1]
     *   最小堆（按资本）：[(0,1), (1,2), (1,3)]
     *
     *   第1轮：w=0
     *     弹出 (0,1)（capital=0 <= w=0）→ 利润1入最大堆
     *     (1,2) capital=1 > 0，停止
     *     最大堆取最大：利润=1，w=0+1=1
     *
     *   第2轮：w=1
     *     弹出 (1,2)（capital=1 <= w=1）→ 利润2入最大堆
     *     弹出 (1,3)（capital=1 <= w=1）→ 利润3入最大堆
     *     最大堆取最大：利润=3，w=1+3=4
     *
     *   返回 w=4
     *
     * 【时间复杂度】O(n log n) —— 每个项目最多入堆出堆各一次
     * 【空间复杂度】O(n) —— 两个堆的总空间
     */
    public int findMaximizedCapitalTwoHeaps(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        for (int i = 0; i < n; i++) {
            minHeap.offer(new int[]{capital[i], profits[i]});
        }

        for (int t = 0; t < k; t++) {
            while (!minHeap.isEmpty() && minHeap.peek()[0] <= w) {
                maxHeap.offer(minHeap.poll()[1]);
            }

            if (maxHeap.isEmpty()) break;

            w += maxHeap.poll();
        }

        return w;
    }

    /**
     * ==================== 解法二：排序 + 贪心 + 最大堆 ====================
     *
     * 【核心思路】
     * 先按资本排序，用指针代替最小堆。每次将所有可做项目的利润加入最大堆，
     * 取最大利润。
     *
     * 【思考过程】
     * 1. 解法一中最小堆的作用是按资本从小到大取项目。
     *    如果我们先排序，用一个指针扫描就能做到同样的事，
     *    避免了最小堆的额外开销。
     *
     * 2. 排序后，指针 ptr 指向下一个待考察的项目。
     *    每次将 capital[ptr] <= w 的项目利润入最大堆，ptr++。
     *    然后从最大堆取最大利润。
     *
     * 3. 本质和解法一完全一样，只是用排序+指针替代了最小堆。
     *    实际运行可能略快（排序比堆操作缓存友好）。
     *
     * 【举例】k=2, w=0, profits=[1,2,3], capital=[0,1,1]
     *   按资本排序：[(0,1), (1,2), (1,3)]，ptr=0
     *
     *   第1轮：w=0
     *     ptr=0: capital=0 <= 0 ✓ → 利润1入最大堆，ptr=1
     *     ptr=1: capital=1 > 0 ✗ 停止
     *     最大堆取最大：1，w=0+1=1
     *
     *   第2轮：w=1
     *     ptr=1: capital=1 <= 1 ✓ → 利润2入最大堆，ptr=2
     *     ptr=2: capital=1 <= 1 ✓ → 利润3入最大堆，ptr=3
     *     最大堆取最大：3，w=1+3=4
     *
     *   返回 w=4
     *
     * 【时间复杂度】O(n log n) —— 排序 O(n log n)，堆操作总共 O(n log n)
     * 【空间复杂度】O(n) —— 最大堆空间
     */
    public int findMaximizedCapitalSortHeap(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        int[][] projects = new int[n][2];
        for (int i = 0; i < n; i++) {
            projects[i][0] = capital[i];
            projects[i][1] = profits[i];
        }
        Arrays.sort(projects, (a, b) -> a[0] - b[0]);

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        int ptr = 0;

        for (int t = 0; t < k; t++) {
            while (ptr < n && projects[ptr][0] <= w) {
                maxHeap.offer(projects[ptr][1]);
                ptr++;
            }

            if (maxHeap.isEmpty()) break;

            w += maxHeap.poll();
        }

        return w;
    }
}
