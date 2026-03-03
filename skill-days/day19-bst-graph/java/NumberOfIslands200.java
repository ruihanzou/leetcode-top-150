/**
 * LeetCode 200. Number of Islands
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个由 '1'（陆地）和 '0'（水）组成的二维网格 grid，请你计算网格中岛屿的数量。
 * 岛屿总是被水包围，并且每座岛屿只能由水平方向和/或垂直方向上相邻的陆地连接形成。
 * 此外，你可以假设该网格的四条边均被水包围。
 *
 * 示例 1：
 *   grid = [["1","1","1","1","0"],
 *           ["1","1","0","1","0"],
 *           ["1","1","0","0","0"],
 *           ["0","0","0","0","0"]]
 *   输出：1
 *
 * 示例 2：
 *   grid = [["1","1","0","0","0"],
 *           ["1","1","0","0","0"],
 *           ["0","0","1","0","0"],
 *           ["0","0","0","1","1"]]
 *   输出：3
 *
 * 【拓展练习】
 * 1. LeetCode 695. Max Area of Island —— 求最大岛屿面积
 * 2. LeetCode 463. Island Perimeter —— 求岛屿周长
 * 3. LeetCode 305. Number of Islands II —— 动态增加陆地时维护岛屿数（并查集经典）
 */

import java.util.LinkedList;
import java.util.Queue;

class NumberOfIslands200 {

    /**
     * ==================== 解法一：DFS（深度优先搜索） ====================
     *
     * 【核心思路】
     * 遍历网格，每遇到一个 '1'，岛屿计数 +1，然后用 DFS 将整个相连的陆地全部
     * 标记为 '0'（已访问），防止重复计数。
     *
     * 【思考过程】
     * 1. 一个岛屿 = 一组连通的 '1'。问题转化为：网格中有多少个连通分量？
     *
     * 2. 经典的连通分量计数思路：遍历每个格子，遇到未访问的 '1' 时启动一次搜索，
     *    把整个连通分量标记完，计数 +1。
     *
     * 3. 标记方式：直接将 '1' 改为 '0'，省去额外的 visited 数组。
     *
     * 4. DFS 从当前格子向上下左右四个方向递归，遇到 '0' 或越界时终止。
     *
     * 【举例】
     *   grid = [["1","1","0"],
     *           ["0","1","0"],
     *           ["0","0","1"]]
     *   (0,0)='1' → 启动 DFS，标记 (0,0),(0,1),(1,1) 为 '0'，count=1
     *   继续扫描，(2,2)='1' → 启动 DFS，标记 (2,2) 为 '0'，count=2
     *   答案：2
     *
     * 【时间复杂度】O(m×n)，每个格子最多访问一次
     * 【空间复杂度】O(m×n)，最坏情况下全是陆地，递归深度为 m×n
     */
    public int numIslandsDFS(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int m = grid.length, n = grid[0].length;
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j, m, n);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int i, int j, int m, int n) {
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == '0') return;
        grid[i][j] = '0';
        dfs(grid, i + 1, j, m, n);
        dfs(grid, i - 1, j, m, n);
        dfs(grid, i, j + 1, m, n);
        dfs(grid, i, j - 1, m, n);
    }

    /**
     * ==================== 解法二：BFS（广度优先搜索） ====================
     *
     * 【核心思路】
     * 与 DFS 思路相同，但使用队列进行广度优先搜索来标记连通的陆地。
     *
     * 【思考过程】
     * 1. DFS 用递归/栈，BFS 用队列。核心逻辑一样：遇到 '1' → 搜索整个岛并标记。
     *
     * 2. BFS 的优势在于不会出现递归栈溢出的问题（对于超大网格更安全）。
     *
     * 3. 入队时就标记为 '0'（而不是出队时），避免同一个格子被多次入队。
     *
     * 【举例】
     *   grid = [["1","1","0"],
     *           ["1","0","0"]]
     *   (0,0)='1' → 入队 (0,0)，标记为 '0'
     *     出队 (0,0)，邻居 (0,1) 和 (1,0) 入队并标记
     *     出队 (0,1)，无新邻居
     *     出队 (1,0)，无新邻居
     *   count=1，答案：1
     *
     * 【时间复杂度】O(m×n)
     * 【空间复杂度】O(min(m,n))，队列中最多存储 min(m,n) 个节点
     */
    public int numIslandsBFS(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int m = grid.length, n = grid[0].length;
        int count = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    grid[i][j] = '0';
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});

                    while (!queue.isEmpty()) {
                        int[] cell = queue.poll();
                        for (int[] d : dirs) {
                            int nx = cell[0] + d[0], ny = cell[1] + d[1];
                            if (nx >= 0 && nx < m && ny >= 0 && ny < n && grid[nx][ny] == '1') {
                                grid[nx][ny] = '0';
                                queue.offer(new int[]{nx, ny});
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * ==================== 解法三：并查集（Union-Find） ====================
     *
     * 【核心思路】
     * 将每个 '1' 格子看作一个节点，相邻的 '1' 格子进行 union 操作。
     * 最终并查集中独立集合的数量就是岛屿数量。
     *
     * 【思考过程】
     * 1. 连通分量问题的经典解法之一就是并查集。
     *
     * 2. 初始化时，每个 '1' 格子是一个独立集合，计数 = 所有 '1' 的个数。
     *
     * 3. 遍历网格，对每个 '1' 格子，尝试与右边和下边的 '1' 格子合并。
     *    每次成功合并，计数 -1。
     *
     * 4. 只需要向右和向下合并（避免重复），因为上和左的合并在之前的格子已处理。
     *
     * 【举例】
     *   grid = [["1","1"],
     *           ["0","1"]]
     *   初始：3 个 '1' → count=3
     *   (0,0)和(0,1) union → count=2
     *   (0,1)和(1,1) union → count=1
     *   答案：1
     *
     * 【时间复杂度】O(m×n×α(m×n))，α 为反阿克曼函数，近似常数
     * 【空间复杂度】O(m×n)，并查集数组
     */
    public int numIslandsUnionFind(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int m = grid.length, n = grid[0].length;
        int[] parent = new int[m * n];
        int[] rank = new int[m * n];
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    parent[i * n + j] = i * n + j;
                    count++;
                }
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    int idx = i * n + j;
                    if (j + 1 < n && grid[i][j + 1] == '1') {
                        count = union(parent, rank, idx, idx + 1, count);
                    }
                    if (i + 1 < m && grid[i + 1][j] == '1') {
                        count = union(parent, rank, idx, idx + n, count);
                    }
                }
            }
        }

        return count;
    }

    private int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    private int union(int[] parent, int[] rank, int x, int y, int count) {
        int rx = find(parent, x), ry = find(parent, y);
        if (rx == ry) return count;
        if (rank[rx] < rank[ry]) { int tmp = rx; rx = ry; ry = tmp; }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
        return count - 1;
    }
}
