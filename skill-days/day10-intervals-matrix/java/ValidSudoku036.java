/**
 * LeetCode 36. Valid Sudoku
 * 难度: Medium
 *
 * 题目描述：
 * 请你判断一个 9x9 的数独是否有效。只需要根据以下规则，验证已经填入的数字是否有效即可：
 * 1. 数字 1-9 在每一行只能出现一次。
 * 2. 数字 1-9 在每一列只能出现一次。
 * 3. 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。
 * 注意：一个有效的数独（部分已被填充）不一定是可解的，只需验证已填入的数字是否满足规则。
 * 空白格用 '.' 表示。
 *
 * 示例：
 * 输入：board =
 * [["5","3",".",".","7",".",".",".","."]
 * ,["6",".",".","1","9","5",".",".","."]
 * ,[".","9","8",".",".",".",".","6","."]
 * ,["8",".",".",".","6",".",".",".","3"]
 * ,["4",".",".","8",".","3",".",".","1"]
 * ,["7",".",".",".","2",".",".",".","6"]
 * ,[".","6",".",".",".",".","2","8","."]
 * ,[".",".",".","4","1","9",".",".","5"]
 * ,[".",".",".",".","8",".",".","7","9"]]
 * 输出：true
 *
 * 【拓展练习】
 * 1. LeetCode 37. Sudoku Solver —— 回溯法解数独，在验证有效的基础上填充所有空格
 * 2. LeetCode 2133. Check if Every Row and Every Column Contains All Numbers —— 简化版矩阵验证
 * 3. LeetCode 1001. Grid Illumination —— 利用行/列/对角线哈希计数的技巧
 */

import java.util.HashSet;
import java.util.Set;

class ValidSudoku036 {

    /**
     * ==================== 解法一：三个集合数组 ====================
     *
     * 【核心思路】
     * 用三组集合分别记录每行、每列、每个3x3宫格中已经出现过的数字。
     * 遍历棋盘，对每个数字检查它是否在对应的行/列/宫格集合中已存在。
     *
     * 【思考过程】
     * 1. 数独有效的条件是三个维度都不重复：行、列、宫格。
     *    → 分别用9个集合记录每个维度已出现的数字。
     *
     * 2. 对于位置 (i, j) 的数字，它属于：
     *    - 第 i 行
     *    - 第 j 列
     *    - 第 (i/3)*3 + j/3 个宫格（将9个宫格编号为0-8）
     *
     * 3. 如果在任一集合中发现重复，立即返回false。
     *    遍历完所有格子都没重复，返回true。
     *
     * 【举例】board[0][0]='5'
     *   行集合 rows[0] 加入 '5'
     *   列集合 cols[0] 加入 '5'
     *   宫格集合 boxes[0] 加入 '5'  （宫格编号 = (0/3)*3 + 0/3 = 0）
     *   后续如果 board[1][1]='5'，检查 boxes[0] 已有 '5' → 返回 false
     *
     * 【时间复杂度】O(1) — 固定遍历 81 个格子
     * 【空间复杂度】O(1) — 集合最多存储 81 个数字
     */
    @SuppressWarnings("unchecked")
    public boolean isValidSudoku_sets(char[][] board) {
        Set<Character>[] rows = new HashSet[9];
        Set<Character>[] cols = new HashSet[9];
        Set<Character>[] boxes = new HashSet[9];

        for (int i = 0; i < 9; i++) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            boxes[i] = new HashSet<>();
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char c = board[i][j];
                if (c == '.') continue;

                int boxIdx = (i / 3) * 3 + j / 3;

                if (rows[i].contains(c) || cols[j].contains(c) || boxes[boxIdx].contains(c)) {
                    return false;
                }

                rows[i].add(c);
                cols[j].add(c);
                boxes[boxIdx].add(c);
            }
        }

        return true;
    }

    /**
     * ==================== 解法二：位运算替代集合 ====================
     *
     * 【核心思路】
     * 用一个 int 的低 9 位分别表示数字 1-9 是否出现过，替代 HashSet。
     * 检查某一位是否为1来判断是否重复，用 OR 来添加。
     *
     * 【思考过程】
     * 1. 数字只有 1-9 共9种，一个 int 有 32 位，完全够用。
     *    用第 k 位（bit k）表示数字 k 是否出现过。
     *
     * 2. 检查是否重复：(mask >> num) & 1 == 1 说明已出现。
     *    标记已出现：mask |= (1 << num)。
     *
     * 3. 相比 HashSet，位运算更紧凑，cache 友好，常数更小。
     *
     * 【举例】处理数字 '5'（num=5）
     *   rows[0] = 0b000000000，第5位是0 → 未出现
     *   rows[0] |= (1<<5) → rows[0] = 0b000100000
     *   下次再遇到 '5'：(rows[0]>>5)&1 = 1 → 已出现，返回false
     *
     * 【时间复杂度】O(1)
     * 【空间复杂度】O(1)
     */
    public boolean isValidSudoku_bit(char[][] board) {
        int[] rows = new int[9];
        int[] cols = new int[9];
        int[] boxes = new int[9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char c = board[i][j];
                if (c == '.') continue;

                int num = c - '0';
                int mask = 1 << num;
                int boxIdx = (i / 3) * 3 + j / 3;

                if ((rows[i] & mask) != 0 || (cols[j] & mask) != 0 || (boxes[boxIdx] & mask) != 0) {
                    return false;
                }

                rows[i] |= mask;
                cols[j] |= mask;
                boxes[boxIdx] |= mask;
            }
        }

        return true;
    }

    /**
     * ==================== 解法三：一次遍历+编码key ====================
     *
     * 【核心思路】
     * 对每个数字，生成三个字符串 key 分别表示"第r行出现了d"、"第c列出现了d"、
     * "第b宫格出现了d"，全部放入一个全局 HashSet。如果 add 返回 false 说明重复。
     *
     * 【思考过程】
     * 1. 前两种方法用三组数组记录，能否合并成一个集合？
     *    → 只要 key 的编码能区分行/列/宫格，就不会误判。
     *
     * 2. 编码方式：
     *    - 行：d + " in row " + i
     *    - 列：d + " in col " + j
     *    - 宫格：d + " in box " + boxIdx
     *    三者不会冲突，因为前缀不同。
     *
     * 3. 利用 Set.add() 的返回值（重复时返回 false），代码非常简洁。
     *
     * 【举例】board[0][0]='5'
     *   添加 "5 in row 0" → true（首次）
     *   添加 "5 in col 0" → true
     *   添加 "5 in box 0" → true
     *   若 board[2][1]='5'：
     *   添加 "5 in row 2" → true
     *   添加 "5 in col 1" → true
     *   添加 "5 in box 0" → false（重复！）→ 返回 false
     *
     * 【时间复杂度】O(1)
     * 【空间复杂度】O(1)
     */
    public boolean isValidSudoku_encode(char[][] board) {
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char c = board[i][j];
                if (c == '.') continue;

                int boxIdx = (i / 3) * 3 + j / 3;

                if (!seen.add(c + " in row " + i) ||
                    !seen.add(c + " in col " + j) ||
                    !seen.add(c + " in box " + boxIdx)) {
                    return false;
                }
            }
        }

        return true;
    }
}
