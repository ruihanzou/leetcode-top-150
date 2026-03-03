"""
LeetCode 52. N-Queens II
难度: Hard

题目描述：
n 皇后问题：在 n×n 的棋盘上放置 n 个皇后，使得任何两个皇后不能互相攻击
（即不能在同一行、同一列、同一对角线上）。
给定整数 n，返回 n 皇后问题不同解的数量。

示例：
输入: n = 4
输出: 2

【拓展练习】
1. LeetCode 51. N-Queens —— 返回所有具体的棋盘布局，而不只是数量
2. LeetCode 36. Valid Sudoku —— 类似的行/列/块冲突检测
3. LeetCode 37. Sudoku Solver —— 回溯填充数独，更复杂的约束
"""


class Solution:
    """
    ==================== 解法一：回溯（逐行放置） ====================

    【核心思路】
    逐行放置皇后。对于第 row 行，尝试在每一列放置皇后，
    检查是否与前面已放置的皇后冲突（同列、同对角线）。
    不冲突则递归处理下一行，冲突则跳过。

    【思考过程】
    1. 每行恰好放一个皇后（否则同行冲突），所以按行递归是自然的。

    2. 冲突检测需要检查三种情况：
       (a) 同列：用 cols 集合记录哪些列已被占用。
       (b) 主对角线（左上→右下）：同一条主对角线上的格子满足 row-col 相同。
           用 diag1 集合记录已占用的 row-col 值。
       (c) 副对角线（右上→左下）：同一条副对角线上的格子满足 row+col 相同。
           用 diag2 集合记录已占用的 row+col 值。

    3. 终止条件：row == n，说明所有行都放好了，解的数量 +1。

    【举例】n = 4
      row=0: 尝试 col=0,1,2,3
        col=1: cols={1}, diag1={-1}, diag2={1}
          row=1: col=3: 无冲突 → cols={1,3}, diag1={-1,-2}, diag2={1,4}
            row=2: col=0: 检查 cols→0不在, diag1→2-0=2不在, diag2→2+0=2不在 → 放置
              cols={0,1,3}, diag1={-1,-2,2}, diag2={1,2,4}
              row=3: col=2: cols→2不在, diag1→3-2=1不在, diag2→3+2=5不在 → 放置
                row=4 == n → count++ (解1: .Q../...Q/Q.../..Q.)

        col=2: cols={2}, diag1={-2}, diag2={2}
          row=1: col=0: 无冲突 →
            row=2: col=3: 无冲突 →
              row=3: col=1: 无冲突 →
                row=4 == n → count++ (解2: ..Q./Q.../...Q/.Q..)

      最终 count = 2

    【时间复杂度】O(n!)  每行的可选列数逐步减少
    【空间复杂度】O(n)  递归深度 + 三个集合各 O(n)
    """
    def totalNQueens(self, n: int) -> int:
        self.count = 0
        self._solve(n, 0, set(), set(), set())
        return self.count

    def _solve(self, n: int, row: int, cols: set, diag1: set, diag2: set):
        if row == n:
            self.count += 1
            return
        for col in range(n):
            d1 = row - col
            d2 = row + col
            if col in cols or d1 in diag1 or d2 in diag2:
                continue
            cols.add(col)
            diag1.add(d1)
            diag2.add(d2)
            self._solve(n, row + 1, cols, diag1, diag2)
            cols.remove(col)
            diag1.remove(d1)
            diag2.remove(d2)

    """
    ==================== 解法二：位运算优化回溯 ====================

    【核心思路】
    用三个整数的二进制位来替代集合，分别表示列、主对角线、副对角线的占用情况。
    通过位运算快速找到所有可放置的位置，并逐一尝试。

    【思考过程】
    1. 解法一中每次检查 col in cols 等集合操作，虽然平均 O(1)，
       但有哈希开销。如果 n 不大（n ≤ 32），可以用 int 位运算替代。

    2. 三个变量：
       - columns: 第 i 位为 1 表示第 i 列已被占。
       - diag1:   主对角线占用。上一行的 diag1 在当前行向左移一位。
       - diag2:   副对角线占用。上一行的 diag2 在当前行向右移一位。

    3. 可放置位置 = ~(columns | diag1 | diag2) & ((1 << n) - 1)
       取出最低位的 1：pos = available & (-available)
       放置后更新：columns | pos, (diag1 | pos) << 1, (diag2 | pos) >> 1

    4. 位运算使得冲突检测和位置枚举都变成 O(1) 操作，常数因子大幅减小。

    【举例】n = 4
      全集掩码 = 0b1111 = 15

      row=0: columns=0, diag1=0, diag2=0
        available = ~0 & 15 = 15 = 0b1111
        pos=0b0001 (col=0): → columns=0b0001, diag1=0b0010, diag2=0b0000
          row=1: available = ~(0b0001|0b0010|0b0000) & 15 = ~0b0011 & 15 = 0b1100
            pos=0b0100 (col=2): → columns=0b0101, diag1=0b1100, diag2=0b0010
              row=2: available = ~(0b0101|0b1100|0b0010) & 15 = ~0b1111 & 15 = 0 → 无解
            pos=0b1000 (col=3): → ...继续搜索...

      最终找到 2 个解

    【时间复杂度】O(n!)  搜索空间不变，但常数因子更小
    【空间复杂度】O(n)   递归栈深度
    """
    def totalNQueensBit(self, n: int) -> int:
        full = (1 << n) - 1
        return self._solveBit(full, 0, 0, 0)

    def _solveBit(self, full: int, columns: int, diag1: int, diag2: int) -> int:
        if columns == full:
            return 1
        result = 0
        available = ~(columns | diag1 | diag2) & full
        while available:
            pos = available & (-available)
            available ^= pos
            result += self._solveBit(full,
                                     columns | pos,
                                     (diag1 | pos) << 1,
                                     (diag2 | pos) >> 1)
        return result
