/**
 * LeetCode 73. Set Matrix Zeroes
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个 m x n 的矩阵，如果一个元素为 0，则将其所在行和列的所有元素都设为 0。
 * 请使用原地算法。
 *
 * 示例 1：
 * 输入：matrix = [[1,1,1],[1,0,1],[1,1,1]]
 * 输出：[[1,0,1],[0,0,0],[1,0,1]]
 *
 * 示例 2：
 * 输入：matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
 * 输出：[[0,0,0,0],[0,4,5,0],[0,3,1,0]]
 *
 * 【拓展练习】
 * 1. LeetCode 289. Game of Life —— 类似的"需要同时更新但要避免覆盖"的矩阵问题
 * 2. LeetCode 2123. Minimum Operations to Remove Adjacent Ones in Matrix —— 矩阵标记与影响传播
 * 3. LeetCode 1329. Sort the Matrix Diagonally —— 对矩阵对角线操作的练习
 */

class SetMatrixZeroes073 {

    /**
     * ==================== 解法一：额外标记数组 ====================
     *
     * 【核心思路】
     * 用两个布尔数组 zeroRows[] 和 zeroCols[] 分别记录哪些行、哪些列需要置零。
     * 第一遍扫描标记，第二遍根据标记置零。
     *
     * 【思考过程】
     * 1. 不能边遍历边置零，因为新写入的0会影响后续判断。
     *    → 需要先记录所有需要置零的行列，最后统一操作。
     *
     * 2. 最直观的做法：用两个数组分别记录行和列。
     *    - 第一遍：扫描矩阵，遇到0就标记 zeroRows[i]=true, zeroCols[j]=true。
     *    - 第二遍：如果 zeroRows[i] 或 zeroCols[j] 为 true，则 matrix[i][j]=0。
     *
     * 【举例】matrix = [[1,1,1],[1,0,1],[1,1,1]]
     *   第一遍：matrix[1][1]=0 → zeroRows[1]=true, zeroCols[1]=true
     *   第二遍：
     *     第1行全置零 → [0,0,0]
     *     第1列全置零 → matrix[0][1]=0, matrix[2][1]=0
     *   结果：[[1,0,1],[0,0,0],[1,0,1]]
     *
     * 【时间复杂度】O(m*n)
     * 【空间复杂度】O(m+n)
     */
    public void setZeroes_extra(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean[] zeroRows = new boolean[m];
        boolean[] zeroCols = new boolean[n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    zeroRows[i] = true;
                    zeroCols[j] = true;
                }
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (zeroRows[i] || zeroCols[j]) {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    /**
     * ==================== 解法二：用第一行第一列作标记 ====================
     *
     * 【核心思路】
     * 将矩阵的第一行和第一列当作标记数组使用，从而省去额外的 O(m+n) 空间。
     * 但第一行/第一列本身是否需要置零，需要用一个额外变量记录。
     *
     * 【思考过程】
     * 1. 解法一用了 O(m+n) 额外空间。能否做到 O(1)？
     *    → 想办法把标记信息存储在矩阵自身里。
     *
     * 2. 第一行 matrix[0][j] 可以用来标记"第 j 列是否需要置零"。
     *    第一列 matrix[i][0] 可以用来标记"第 i 行是否需要置零"。
     *
     * 3. 但这样第一行和第一列的原始信息会被覆盖。
     *    解决方案：用一个额外变量 firstColZero 记录第一列是否需要置零。
     *    第一行是否需要置零由 matrix[0][0] 来标记。
     *
     * 4. 置零时从内到外处理（先处理非第一行/列的部分，最后处理第一行和第一列），
     *    避免标记被提前清除。
     *
     * 【举例】matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
     *   firstColZero = true（matrix[0][0]=0）
     *   标记阶段（从(1,1)开始扫描）：
     *     无新的0需要标记
     *   但 matrix[0][0]=0 → 第一行置零；matrix[0][3]=0 → 第3列置零
     *   置零阶段：
     *     根据第一行标记：第3列全置0 → matrix[1][3]=0, matrix[2][3]=0
     *     第一行本身：matrix[0][0]=0 → 第一行全置0
     *     firstColZero=true → 第一列全置0
     *   结果：[[0,0,0,0],[0,4,5,0],[0,3,1,0]]
     *
     * 【时间复杂度】O(m*n)
     * 【空间复杂度】O(1)
     */
    public void setZeroes_inplace(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstColZero = false;

        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) {
                firstColZero = true;
            }
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 1; j--) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
            if (firstColZero) {
                matrix[i][0] = 0;
            }
        }
    }
}
