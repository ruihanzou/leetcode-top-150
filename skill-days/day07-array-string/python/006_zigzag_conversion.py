"""
LeetCode 6. Zigzag Conversion
难度: Medium

题目描述：
将给定字符串 s 根据给定的行数 numRows，以从上往下、从左到右进行 Z 字形排列后，
按行依次读取得到新字符串。

例如字符串 "PAYPALISHIRING" 在 numRows=3 时排列如下：
P   A   H   N
A P L S I I G
Y   I   R
按行读取即 "PAHNAPLSIIGYIR"

示例：s = "PAYPALISHIRING", numRows = 3 → 输出 "PAHNAPLSIIGYIR"

【拓展练习】
1. LeetCode 1041. Robot Bounded In Circle —— 模拟方向变化的类似思路
2. LeetCode 54. Spiral Matrix —— 另一种方向模拟的矩阵遍历
3. LeetCode 498. Diagonal Traverse —— Z 字形遍历矩阵
"""


class Solution:
    """
    ==================== 解法一：模拟行变化 ====================

    【核心思路】
    创建 numRows 个行缓冲区（列表），用一个方向标志控制当前字符放到哪一行。
    遍历字符串，到达第 0 行或第 numRows-1 行时切换方向。
    最后将所有行拼接起来。

    【思考过程】
    1. Z 字形排列的本质是：行号先从 0 递增到 numRows-1（向下），
       再从 numRows-1 递减到 0（向上），如此往复。

    2. 不需要建二维矩阵，只需知道每个字符属于哪一行。
       用 numRows 个列表分别收集每行的字符即可。

    3. 用变量 cur_row 追踪当前行号，step 追踪方向（+1 或 -1）：
       - 当 cur_row == 0 时，step = 1（向下）
       - 当 cur_row == numRows-1 时，step = -1（向上）

    4. 特殊情况：numRows == 1 时无需转换，直接返回原字符串。

    【举例】s = "PAYPALISHIRING", numRows = 3
      row0: P     A     H     N       → "PAHN"
      row1: A  P  L  S  I  I  G      → "APLSIIG"
      row2: Y     I     R             → "YIR"
      拼接结果: "PAHNAPLSIIGYIR"

    【时间复杂度】O(n)
    【空间复杂度】O(n)
    """
    def convert_simulate(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s

        # range(numRows) is [0, 1, 2, ..., numRows-1]
        # [] for _ in range(numRows) is a list of empty lists, each list is a row.
        # for example, if numRows is 3, rows will be [[], [], []].
        # in java, it is a List<List<Character>> and using add method to add the character to the row.
        # For example:
        # List<List<Character>> rows = new ArrayList<>();
        # for (int i = 0; i < numRows; i++) {
        #     rows.add(new ArrayList<>());
        # }
        # rows = [""] * numRows

        rows = [[] for _ in range(numRows)]
        cur_row = 0
        going_down = False

        for c in s:
            rows[cur_row].append(c)
            if cur_row == 0 or cur_row == numRows - 1:
                going_down = not going_down
            cur_row += 1 if going_down else -1

        return 
        
        """
        class Solution:
    def convert(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s
        
        rows = [""] * numRows
        currentRow = 0
        goingDown = False
        
        for ch in s:
            rows[currentRow] += ch
            
            if currentRow == 0 or currentRow == numRows - 1:
                goingDown = not goingDown
            
            if goingDown:
                currentRow += 1
            else:
                currentRow -= 1
        
        return "".join(rows)

        """

    """
    ==================== 解法二：数学规律 / 按行访问 ====================

    【核心思路】
    Z 字形排列有固定的索引规律。一个完整的 V 形周期长度为 cycle = 2*(numRows-1)。
    - 第 0 行和最后一行：每个周期只贡献一个字符
    - 中间行 i：每个周期贡献两个字符，偏移分别为 i 和 cycle - i

    直接按行计算索引并拼接结果。

    【思考过程】
    1. 观察 Z 字形的结构，每个 V 形包含 2*(numRows-1) 个字符。
       例如 numRows=4 时，一个周期是 6 个字符:
       下行 0→1→2→3，上行 3→2→1（不重复端点），共 3+3=6。

    2. 对于第 0 行和最后一行，每个周期只贡献一个字符。
       对于中间行 i，每个周期贡献两个字符：
       - 下行部分：距周期起始偏移 i
       - 上行部分：距周期起始偏移 cycle - i

    3. 这样能按行直接算出所有字符的索引，拼接得到结果。

    【举例】s = "PAYPALISHIRING", numRows = 4, cycle = 6
      P     I     N        row0: 索引 0, 6, 12
      A   L S   I G        row1: 索引 1,5, 7,11, 13
      Y A   H R            row2: 索引 2,4, 8,10
      P     I              row3: 索引 3, 9
      结果: "PINALSIGYAHRPI"

    【时间复杂度】O(n)
    【空间复杂度】O(1) 除结果字符串外无额外空间
    """
    def convert_math(self, s: str, numRows: int) -> str:
        if numRows == 1 or numRows >= len(s):
            return s

        n = len(s)
        cycle = 2 * (numRows - 1)
        result = []

        for row in range(numRows):
            j = 0
            while j + row < n:
                result.append(s[j + row])
                if 0 < row < numRows - 1 and j + cycle - row < n:
                    result.append(s[j + cycle - row])
                j += cycle

        return ''.join(result)
