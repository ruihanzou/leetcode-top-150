/**
 * LeetCode 79. Word Search
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个 m x n 二维字符网格 board 和一个字符串单词 word，
 * 如果 word 存在于网格中，返回 true；否则返回 false。
 * 单词必须按照字母顺序，通过相邻的单元格内的字母构成，
 * 其中"相邻"单元格是水平或垂直相邻的。同一单元格内的字母不允许被重复使用。
 *
 * 示例 1：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED" → true
 * 示例 2：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "SEE" → true
 * 示例 3：board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCB" → false
 *
 * 【拓展练习】
 * 1. LeetCode 212. Word Search II —— 在网格中搜索多个单词，Trie + DFS
 * 2. LeetCode 980. Unique Paths III —— 网格中从起点到终点经过所有空格的路径数
 * 3. LeetCode 200. Number of Islands —— 经典网格 DFS/BFS
 */

class WordSearch079 {

    /**
     * ==================== 解法一：DFS回溯（visited数组标记） ====================
     *
     * 【核心思路】
     * 从网格中每个位置出发，尝试 DFS 匹配 word 的每个字符。
     * 使用一个 visited 二维数组标记已访问的格子，防止重复使用。
     *
     * 【思考过程】
     * 1. 单词搜索的本质是在网格上做路径搜索，路径上的字符拼起来等于 word。
     *    → 典型的 DFS + 回溯问题。
     *
     * 2. 对于 word 的第 k 个字符，当前位置 (i,j) 必须满足：
     *    - 在网格范围内
     *    - 未被访问过
     *    - board[i][j] == word[k]
     *    然后向四个方向递归匹配第 k+1 个字符。
     *
     * 3. 回溯时需要把 visited[i][j] 重置为 false，允许其他路径使用这个格子。
     *
     * 【举例】board = [["A","B"],["C","D"]], word = "ABDC"
     *   从(0,0)='A'开始，匹配word[0]='A' ✓
     *     → 右(0,1)='B'，匹配word[1]='B' ✓
     *       → 下(1,1)='D'，匹配word[2]='D' ✓
     *         → 左(1,0)='C'，匹配word[3]='C' ✓ → 找到！
     *
     * 【时间复杂度】O(m * n * 3^L)，L为word长度，每步最多3个方向（排除来路）
     * 【空间复杂度】O(m * n + L)，visited数组 + 递归栈
     */
    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (dfs(board, word, i, j, 0, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int i, int j, int k, boolean[][] visited) {
        if (k == word.length()) return true;
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return false;
        if (visited[i][j] || board[i][j] != word.charAt(k)) return false;

        visited[i][j] = true;
        boolean found = dfs(board, word, i + 1, j, k + 1, visited)
                      || dfs(board, word, i - 1, j, k + 1, visited)
                      || dfs(board, word, i, j + 1, k + 1, visited)
                      || dfs(board, word, i, j - 1, k + 1, visited);
        visited[i][j] = false;
        return found;
    }

    /**
     * ==================== 解法二：DFS回溯（原地修改标记） ====================
     *
     * 【核心思路】
     * 与解法一逻辑相同，但不使用额外的 visited 数组。
     * 访问某个格子时，将 board[i][j] 临时改为一个特殊字符（如 '#'），
     * 回溯时再恢复原值。这样只需 O(L) 的递归栈空间。
     *
     * 【思考过程】
     * 1. 解法一的 visited 数组占 O(m*n) 空间，能否省掉？
     *    → 可以直接在 board 上做标记：把当前字符改成一个不可能出现的字符。
     *
     * 2. 这是一种常见的空间优化技巧：原地修改 + 回溯恢复。
     *    需要注意的是，如果输入不可修改，则不能用此方法。
     *
     * 3. 特殊字符选择 '#'（或任何不在 board 取值范围内的字符），
     *    保证 DFS 过程中不会误匹配。
     *
     * 【举例】board = [["A","B"],["C","D"]], word = "ABDC"
     *   (0,0): 'A'→'#', 匹配word[0] ✓
     *     (0,1): 'B'→'#', 匹配word[1] ✓
     *       (1,1): 'D'→'#', 匹配word[2] ✓
     *         (1,0): 'C'→'#', 匹配word[3] ✓ → 返回true
     *         恢复'C', 恢复'D', 恢复'B', 恢复'A'
     *
     * 【时间复杂度】O(m * n * 3^L)
     * 【空间复杂度】O(L) 仅递归栈
     */
    public boolean existInPlace(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (dfsInPlace(board, word, i, j, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsInPlace(char[][] board, String word, int i, int j, int k) {
        if (k == word.length()) return true;
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return false;
        if (board[i][j] != word.charAt(k)) return false;

        char temp = board[i][j];
        board[i][j] = '#';

        boolean found = dfsInPlace(board, word, i + 1, j, k + 1)
                      || dfsInPlace(board, word, i - 1, j, k + 1)
                      || dfsInPlace(board, word, i, j + 1, k + 1)
                      || dfsInPlace(board, word, i, j - 1, k + 1);

        board[i][j] = temp;
        return found;
    }
}
