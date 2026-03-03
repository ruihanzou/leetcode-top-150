/**
 * LeetCode 130. Surrounded Regions
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个 m×n 的矩阵 board，由若干字符 'X' 和 'O' 组成。
 * 找到所有被 'X' 围绕的区域，并将这些区域里所有的 'O' 用 'X' 填充。
 * 被围绕的区域不会存在于边界上，换句话说，任何边界上的 'O' 都不会被填充为 'X'。
 * 任何不在边界上且不与边界上的 'O' 相连的 'O' 最终都会被填充为 'X'。
 *
 * 示例：
 *   board = [["X","X","X","X"],
 *            ["X","O","O","X"],
 *            ["X","X","O","X"],
 *            ["X","O","X","X"]]
 *   输出：[["X","X","X","X"],
 *          ["X","X","X","X"],
 *          ["X","X","X","X"],
 *          ["X","O","X","X"]]
 *   解释：左下角的 'O' 在边界上所以不被围绕。其余 'O' 都被 'X' 围绕。
 *
 * 【拓展练习】
 * 1. LeetCode 200. Number of Islands —— 同类型网格搜索
 * 2. LeetCode 417. Pacific Atlantic Water Flow —— 从边界出发的双向搜索
 * 3. LeetCode 1020. Number of Enclaves —— 类似问题，求被围绕的陆地数量
 */

import java.util.LinkedList;
import java.util.Queue;

class SurroundedRegions130 {

    /**
     * ==================== 解法一：边界 DFS/BFS ====================
     *
     * 【核心思路】
     * "被围绕的 'O'" 等价于 "不与边界相连的 'O'"。
     * 反向思考：从边界上的所有 'O' 出发做 DFS，标记所有与边界相连的 'O'。
     * 标记完成后，未被标记的 'O' 就是被围绕的，替换为 'X'；
     * 被标记的 'O' 恢复原样。
     *
     * 【思考过程】
     * 1. 直接判断一个 'O' 是否被围绕很困难（需要知道整个连通分量是否碰到边界）。
     *
     * 2. 逆向思维：先找出所有"安全"的 'O'（与边界相连的），剩下的就是被围绕的。
     *
     * 3. 具体步骤：
     *    a) 遍历四条边界，对每个边界上的 'O' 做 DFS，将其及其连通的 'O' 标记为 'T'。
     *    b) 遍历整个矩阵：
     *       - 'O' → 'X'（被围绕的，没被标记过）
     *       - 'T' → 'O'（与边界相连的，恢复原样）
     *
     * 【举例】
     *   原始：X X X X     标记后：X X X X     最终：X X X X
     *         X O O X             X O O X           X X X X
     *         X X O X             X X O X           X X X X
     *         X O X X             X T X X           X O X X
     *   边界 'O' 在 (3,1)，DFS 标记为 'T'。
     *   内部 'O' 未被标记 → 全变 'X'，'T' 恢复为 'O'。
     *
     * 【时间复杂度】O(m×n)，每个格子最多访问常数次
     * 【空间复杂度】O(m×n)，DFS 递归栈最坏情况
     */
    public void solveDFS(char[][] board) {
        if (board == null || board.length == 0) return;

        int m = board.length, n = board[0].length;

        for (int i = 0; i < m; i++) {
            dfsMark(board, i, 0, m, n);
            dfsMark(board, i, n - 1, m, n);
        }
        for (int j = 0; j < n; j++) {
            dfsMark(board, 0, j, m, n);
            dfsMark(board, m - 1, j, m, n);
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X';
                } else if (board[i][j] == 'T') {
                    board[i][j] = 'O';
                }
            }
        }
    }

    private void dfsMark(char[][] board, int i, int j, int m, int n) {
        if (i < 0 || i >= m || j < 0 || j >= n || board[i][j] != 'O') return;
        board[i][j] = 'T';
        dfsMark(board, i + 1, j, m, n);
        dfsMark(board, i - 1, j, m, n);
        dfsMark(board, i, j + 1, m, n);
        dfsMark(board, i, j - 1, m, n);
    }

    /**
     * ==================== 解法二：并查集 ====================
     *
     * 【核心思路】
     * 创建一个虚拟节点 dummy，将所有边界上的 'O' 与 dummy 连通。
     * 然后遍历网格，将相邻的 'O' 合并。
     * 最终，与 dummy 不在同一集合中的 'O' 就是被围绕的。
     *
     * 【思考过程】
     * 1. 问题本质是判断连通性：一个 'O' 是否与边界上的 'O' 连通。
     *
     * 2. 并查集天然适合处理连通性问题。
     *
     * 3. 引入一个虚拟节点 dummy（编号 m*n），所有边界 'O' 都与 dummy 合并。
     *    这样只需检查 find(cell) == find(dummy) 即可判断是否与边界相连。
     *
     * 4. 遍历网格，对每个 'O'：
     *    - 如果在边界上，与 dummy 合并
     *    - 与右边和下边的 'O' 合并
     *
     * 5. 最后遍历网格，find(cell) != find(dummy) 的 'O' 替换为 'X'。
     *
     * 【举例】
     *   board = [["X","O","X"],
     *            ["O","O","O"],
     *            ["X","O","X"]]
     *   边界 'O'：(0,1),(1,0),(1,2),(2,1) 都与 dummy 连通
     *   中间 (1,1) 与 (0,1) 相邻 → 也和 dummy 连通
     *   所有 'O' 都与边界相连，没有被围绕的 'O'
     *
     * 【时间复杂度】O(m×n×α(m×n))
     * 【空间复杂度】O(m×n)
     */
    public void solveUnionFind(char[][] board) {
        if (board == null || board.length == 0) return;

        int m = board.length, n = board[0].length;
        int dummy = m * n;
        int[] parent = new int[m * n + 1];
        int[] rank = new int[m * n + 1];
        for (int i = 0; i <= m * n; i++) parent[i] = i;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    int idx = i * n + j;
                    if (i == 0 || i == m - 1 || j == 0 || j == n - 1) {
                        ufUnion(parent, rank, idx, dummy);
                    }
                    if (i + 1 < m && board[i + 1][j] == 'O') {
                        ufUnion(parent, rank, idx, (i + 1) * n + j);
                    }
                    if (j + 1 < n && board[i][j + 1] == 'O') {
                        ufUnion(parent, rank, idx, i * n + j + 1);
                    }
                }
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O' && ufFind(parent, i * n + j) != ufFind(parent, dummy)) {
                    board[i][j] = 'X';
                }
            }
        }
    }

    private int ufFind(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    private void ufUnion(int[] parent, int[] rank, int x, int y) {
        int rx = ufFind(parent, x), ry = ufFind(parent, y);
        if (rx == ry) return;
        if (rank[rx] < rank[ry]) { int tmp = rx; rx = ry; ry = tmp; }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
    }
}
