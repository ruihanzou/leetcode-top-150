/**
 * LeetCode 212. Word Search II
 * 难度: Hard
 *
 * 题目描述：
 * 给定一个 m x n 二维字符网格 board 和一个单词列表 words，
 * 返回所有二维网格中的单词。单词必须按照字母顺序，通过相邻的单元格内的字母构成，
 * 其中"相邻"单元格是那些水平相邻或垂直相邻的单元格。
 * 同一个单元格内的字母在一个单词中不允许被重复使用。
 *
 * 示例：
 *   board = [["o","a","a","n"],
 *            ["e","t","a","e"],
 *            ["i","h","k","r"],
 *            ["i","f","l","v"]]
 *   words = ["oath","pea","eat","rain"]
 *   输出 ["eat","oath"]
 *
 * 【拓展练习】
 * 1. LeetCode 79. Word Search —— 在棋盘中搜索单个单词
 * 2. LeetCode 208. Implement Trie (Prefix Tree) —— 标准 Trie 实现
 * 3. LeetCode 211. Design Add and Search Words Data Structure —— Trie+通配符搜索
 */

import java.util.*;

class WordSearchII212 {

    /**
     * ==================== 解法一：Trie + 回溯 ====================
     *
     * 【核心思路】
     * 将所有待搜索单词构建成 Trie，然后在棋盘上每个位置启动 DFS，
     * 沿着 Trie 的路径同步搜索。当 Trie 路径上的节点标记了一个完整单词时，
     * 将该单词加入结果集。
     *
     * 【思考过程】
     * 1. 暴力做法是对每个单词在棋盘上做一次 DFS，但如果 words 很多，
     *    会重复探索大量相同的路径。
     *
     * 2. 优化思路：把所有单词建成 Trie，用 Trie 来引导 DFS 方向。
     *    - DFS 扩展到下一个字符时，先检查 Trie 中是否有对应子节点。
     *    - 如果没有，立即剪枝（不可能匹配任何单词）。
     *    - 如果有，继续深入。如果到达某个单词的末尾，记录结果。
     *
     * 3. 关键优化——"找到即删除"：
     *    找到一个单词后，把 Trie 中对应的 word 标记清除，避免重复收录。
     *    进一步地，如果某个节点的所有子节点都被删光了，
     *    可以把该子节点删除，加速后续剪枝。
     *
     * 4. 回溯时恢复棋盘状态（将标记过的格子还原）。
     *
     * 【举例】
     *   words = ["oath","oat"], board 含 'o','a','t','h'
     *   Trie: root → o → a → t (word="oat") → h (word="oath")
     *
     *   从 board 的 'o' 出发 DFS：
     *     'o' → Trie root.children['o'] 存在，继续
     *     'a' → Trie .children['a'] 存在，继续
     *     't' → Trie .children['t'] 存在，且 word="oat"，收录 "oat"
     *     'h' → Trie .children['h'] 存在，且 word="oath"，收录 "oath"
     *     回溯
     *
     * 【时间复杂度】O(m * n * 4^L)，L 为最长单词长度，实际因 Trie 剪枝远小于此
     * 【空间复杂度】O(sum of word lengths)，Trie 存储空间
     */
    private int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public List<String> findWordsTrie(char[][] board, String[] words) {
        TrieNode root = new TrieNode();
        for (String word : words) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int idx = ch - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.word = word;
        }

        List<String> result = new ArrayList<>();
        int m = board.length, n = board[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int idx = board[i][j] - 'a';
                if (root.children[idx] != null) {
                    dfs(board, i, j, root, result);
                }
            }
        }

        return result;
    }

    private void dfs(char[][] board, int i, int j, TrieNode parent, List<String> result) {
        char ch = board[i][j];
        int idx = ch - 'a';
        TrieNode node = parent.children[idx];

        if (node.word != null) {
            result.add(node.word);
            node.word = null;
        }

        board[i][j] = '!';
        for (int[] d : dirs) {
            int ni = i + d[0], nj = j + d[1];
            if (ni >= 0 && ni < board.length && nj >= 0 && nj < board[0].length
                    && board[ni][nj] != '!' && node.children[board[ni][nj] - 'a'] != null) {
                dfs(board, ni, nj, node, result);
            }
        }
        board[i][j] = ch;

        boolean empty = true;
        for (TrieNode child : node.children) {
            if (child != null) { empty = false; break; }
        }
        if (empty && node.word == null) {
            parent.children[idx] = null;
        }
    }

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
    }

    /**
     * ==================== 解法二：暴力逐词搜索 ====================
     *
     * 【核心思路】
     * 对 words 中的每个单词，在棋盘上每个位置尝试 DFS 匹配。
     * 如果找到匹配则将该单词加入结果集。
     *
     * 【思考过程】
     * 1. 最直接的想法：对每个单词单独做一次"Word Search"（LeetCode 79）。
     * 2. 对于每个单词，从棋盘上的每个位置出发做 DFS，检查是否能拼出该单词。
     * 3. DFS 过程中标记已访问的格子，避免重复使用。
     * 4. 缺点：如果 words 有 W 个单词，每个单词都要遍历整个棋盘并做 DFS，
     *    大量重复工作。相比 Trie+回溯，没有前缀共享和剪枝优化。
     *
     * 【举例】
     *   words = ["oath","pea"], board 含 'o','a','t','h'
     *   搜索 "oath"：从每个 'o' 出发 DFS，找到 o→a→t→h 路径 → 收录
     *   搜索 "pea"：从每个 'p' 出发 DFS，找不到 → 不收录
     *
     * 【时间复杂度】O(W * m * n * 4^L)，W 为单词数，L 为最长单词长度
     * 【空间复杂度】O(L)，递归栈深度
     */
    public List<String> findWordsBrute(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        int m = board.length, n = board[0].length;

        for (String word : words) {
            boolean found = false;
            for (int i = 0; i < m && !found; i++) {
                for (int j = 0; j < n && !found; j++) {
                    if (board[i][j] == word.charAt(0) && searchWord(board, i, j, word, 0)) {
                        result.add(word);
                        found = true;
                    }
                }
            }
        }

        return result;
    }

    private boolean searchWord(char[][] board, int i, int j, String word, int idx) {
        if (idx == word.length()) return true;
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return false;
        if (board[i][j] != word.charAt(idx)) return false;

        char temp = board[i][j];
        board[i][j] = '!';
        boolean found = searchWord(board, i + 1, j, word, idx + 1)
                || searchWord(board, i - 1, j, word, idx + 1)
                || searchWord(board, i, j + 1, word, idx + 1)
                || searchWord(board, i, j - 1, word, idx + 1);
        board[i][j] = temp;
        return found;
    }
}
