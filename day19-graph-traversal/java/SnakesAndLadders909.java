/**
 * LeetCode 909. Snakes and Ladders
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个大小为 n x n 的整数矩阵 board，方格按从 1 到 n² 编号（蛇形排列）。
 * 玩家从编号 1 出发，每轮掷骰子（1~6步），遇到蛇/梯子必须传送。
 * 返回到达编号 n² 所需的最少移动次数；如果不可能，返回 -1。
 *
 * 示例：→ 输出 4
 *
 * 【拓展练习】
 * 1. LeetCode 1197. Minimum Knight Moves —— BFS 求棋盘最短路径
 * 2. LeetCode 847. Shortest Path Visiting All Nodes —— 状态压缩BFS
 * 3. LeetCode 127. Word Ladder —— BFS 求最短转换序列
 */

import java.util.*;

class SnakesAndLadders909 {

    /**
     * ==================== 解法一：BFS ====================
     *
     * 【核心思路】
     * 将棋盘上的每个编号视为图中的节点，从编号 curr 可以到达 curr+1 ~ curr+6
     * （有蛇/梯子则传送）。从 1 出发 BFS 求到 n² 的最短路径。
     *
     * 【思考过程】
     * 1. 问题本质：无权图最短路径 → BFS。
     *
     * 2. 关键在于编号到行列的坐标转换（蛇形排列）：
     *    - row_from_bottom = (s-1) / n
     *    - r = n - 1 - row_from_bottom
     *    - col_in_row = (s-1) % n
     *    - 偶数行从左到右：c = col_in_row
     *    - 奇数行从右到左：c = n - 1 - col_in_row
     *
     * 3. BFS 每次尝试掷 1~6，如果目标有蛇/梯子则传送。
     *    第一次到达 n² 时的 BFS 层数即为最少步数。
     *
     * 【举例】
     *   1→2(梯子到15)→16(梯子到35)→36，共4步
     *
     * 【时间复杂度】O(n²)
     * 【空间复杂度】O(n²)
     */
    public int snakesAndLadders(int[][] board) {
        int n = board.length;
        int target = n * n;

        boolean[] visited = new boolean[target + 1];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{1, 0});
        visited[1] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int pos = curr[0], moves = curr[1];

            for (int dice = 1; dice <= 6; dice++) {
                int next = pos + dice;
                if (next > target) break;

                int[] rc = numberToCoord(next, n);
                int val = board[rc[0]][rc[1]];
                if (val != -1) {
                    next = val;
                }

                if (next == target) return moves + 1;

                if (!visited[next]) {
                    visited[next] = true;
                    queue.offer(new int[]{next, moves + 1});
                }
            }
        }

        return -1;
    }

    private int[] numberToCoord(int s, int n) {
        int rowFromBottom = (s - 1) / n;
        int colInRow = (s - 1) % n;
        int r = n - 1 - rowFromBottom;
        int c = (rowFromBottom % 2 == 0) ? colInRow : n - 1 - colInRow;
        return new int[]{r, c};
    }
}
