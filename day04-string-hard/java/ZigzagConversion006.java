/**
 * LeetCode 6. Zigzag Conversion
 * 难度: Medium
 *
 * 题目描述：
 * 将给定字符串 s 根据给定的行数 numRows，以从上往下、从左到右进行 Z 字形排列后，
 * 按行依次读取得到新字符串。
 *
 * 例如字符串 "PAYPALISHIRING" 在 numRows=3 时排列如下：
 * P   A   H   N
 * A P L S I I G
 * Y   I   R
 * 按行读取即 "PAHNAPLSIIGYIR"
 *
 * 示例：s = "PAYPALISHIRING", numRows = 3 → 输出 "PAHNAPLSIIGYIR"
 *
 * 【拓展练习】
 * 1. LeetCode 1041. Robot Bounded In Circle —— 模拟方向变化的类似思路
 * 2. LeetCode 54. Spiral Matrix —— 另一种方向模拟的矩阵遍历
 * 3. LeetCode 498. Diagonal Traverse —— Z 字形遍历矩阵
 */

class ZigzagConversion006 {

    /**
     * ==================== 解法一：模拟行变化 ====================
     *
     * 【核心思路】
     * 创建 numRows 个行缓冲区（StringBuilder），用一个方向标志 goingDown
     * 控制当前字符放到哪一行。遍历字符串，到达第 0 行或第 numRows-1 行时
     * 切换方向。最后将所有行拼接起来。
     *
     * 【思考过程】
     * 1. Z 字形排列的本质是：行号先从 0 递增到 numRows-1（向下），
     *    再从 numRows-1 递减到 0（向上），如此往复。
     *
     * 2. 我们不需要真的建一个二维矩阵，只需要知道每个字符属于哪一行。
     *    用 numRows 个 StringBuilder 分别收集每行的字符即可。
     *
     * 3. 用变量 curRow 追踪当前行号，goingDown 追踪方向：
     *    - 当 curRow == 0 时，必须向下走，goingDown = true
     *    - 当 curRow == numRows-1 时，必须向上走，goingDown = false
     *
     * 4. 特殊情况：numRows == 1 时无需转换，直接返回原字符串。
     *
     * 【举例】s = "PAYPALISHIRING", numRows = 3
     *   row0: P     A     H     N       → "PAHN"
     *   row1: A  P  L  S  I  I  G      → "APLSIIG"
     *   row2: Y     I     R             → "YIR"
     *
     *   遍历过程中 curRow 变化: 0,1,2,1,0,1,2,1,0,1,2,1,0,1
     *   拼接结果: "PAHNAPLSIIGYIR"
     *
     * 【时间复杂度】O(n) 每个字符恰好处理一次
     * 【空间复杂度】O(n) 行缓冲区总大小等于字符串长度
     */
    public String convertSimulate(String s, int numRows) {
        if (numRows == 1 || numRows >= s.length()) {
            return s;
        }

        StringBuilder[] rows = new StringBuilder[numRows];
        for (int i = 0; i < numRows; i++) {
            rows[i] = new StringBuilder();
        }

        int curRow = 0;
        boolean goingDown = false;

        for (char c : s.toCharArray()) {
            rows[curRow].append(c);
            if (curRow == 0 || curRow == numRows - 1) {
                goingDown = !goingDown;
            }
            curRow += goingDown ? 1 : -1;
        }

        StringBuilder result = new StringBuilder();
        for (StringBuilder row : rows) {
            result.append(row);
        }
        return result.toString();
    }

    /**
     * ==================== 解法二：数学规律 / 按行访问 ====================
     *
     * 【核心思路】
     * Z 字形排列有固定的索引规律。一个完整的 V 形周期长度为 cycleLen = 2*(numRows-1)。
     * - 第 0 行：索引 0, cycleLen, 2*cycleLen, ...
     * - 第 i 行（0 < i < numRows-1）：每个周期有两个字符，
     *   索引 j+i 和 j+cycleLen-i（其中 j 是周期起始位置）
     * - 第 numRows-1 行：索引 numRows-1, numRows-1+cycleLen, ...
     *
     * 直接按行计算索引并拼接结果，不需要额外的行缓冲区。
     *
     * 【思考过程】
     * 1. 观察 Z 字形的结构，每个"V 形"包含 2*(numRows-1) 个字符。
     *    例如 numRows=4 时，一个周期是 6 个字符:
     *    下行 0→1→2→3，上行 3→2→1（不重复端点），共 3+3=6。
     *
     * 2. 对于第 0 行和最后一行，每个周期只贡献一个字符。
     *    对于中间行 i，每个周期贡献两个字符：
     *    - 下行部分：距周期起始偏移 i
     *    - 上行部分：距周期起始偏移 cycleLen - i
     *
     * 3. 这样就能按行直接计算出所有字符的索引，拼接得到结果。
     *
     * 【举例】s = "PAYPALISHIRING", numRows = 4, cycleLen = 6
     *   P     I     N        row0: 索引 0, 6, 12
     *   A   L S   I G        row1: 索引 1,5, 7,11, 13
     *   Y A   H R            row2: 索引 2,4, 8,10
     *   P     I              row3: 索引 3, 9
     *   结果: "PINALSIGYAHRPI"
     *
     * 【时间复杂度】O(n) 每个字符被访问一次
     * 【空间复杂度】O(1) 除结果字符串外无额外空间
     */
    public String convertMath(String s, int numRows) {
        if (numRows == 1 || numRows >= s.length()) {
            return s;
        }

        StringBuilder result = new StringBuilder();
        int n = s.length();
        int cycleLen = 2 * (numRows - 1);

        for (int row = 0; row < numRows; row++) {
            for (int j = 0; j + row < n; j += cycleLen) {
                result.append(s.charAt(j + row));
                if (row > 0 && row < numRows - 1 && j + cycleLen - row < n) {
                    result.append(s.charAt(j + cycleLen - row));
                }
            }
        }

        return result.toString();
    }
}
