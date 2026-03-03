"""
LeetCode 39. Combination Sum
难度: Medium

题目描述：
给你一个无重复元素的整数数组 candidates 和一个目标整数 target，
找出 candidates 中可以使数字和为目标数 target 的所有不同组合。
candidates 中的同一个数字可以无限制重复被选取。

示例：
输入: candidates = [2,3,6,7], target = 7
输出: [[2,2,3],[7]]

【拓展练习】
1. LeetCode 40. Combination Sum II —— 每个数字只能用一次，需排序去重
2. LeetCode 216. Combination Sum III —— 限定 k 个数，每个数 1-9 只用一次
3. LeetCode 377. Combination Sum IV —— 求组合数（排列数），用 DP 解
"""

from typing import List


class Solution:
    """
    ==================== 解法一：回溯 + 排序剪枝 ====================

    【核心思路】
    先将 candidates 排序。从第一个候选数开始，每次可以选择：
    (1) 继续使用当前数字（因为允许重复使用），
    (2) 跳到下一个数字。
    当剩余目标值为 0 时收集结果；当剩余值 < 0 或当前候选数已超过剩余值时剪枝。

    【思考过程】
    1. 与标准组合问题的区别：每个数字可以重复使用。
       → 递归时 start 不变（继续从当前数字开始），而不是 start+1。

    2. 为了避免重复组合（如 [2,3] 和 [3,2]），规定只能选"当前或之后的"候选数，
       即用 start 限制搜索方向。

    3. 排序后可以剪枝：如果 candidates[i] > remain，
       则 candidates[i+1..] 更大，全部不可能，直接 break。

    4. 终止条件：remain == 0 → 找到一个合法组合；remain < 0 → 超了，回溯。

    【举例】candidates = [2,3,6,7], target = 7
      排序后: [2,3,6,7]

      start=0, remain=7:
        选2, remain=5 → 选2, remain=3 → 选2, remain=1 → 选2, remain=-1 ✗ 回溯
                                                           选3, 3>1 ✗ break
                                          选3, remain=0 ✓ → [2,2,3]
                         选3, remain=2 → 选3, 3>2 ✗ break
                         选6, 6>5 ✗ break
        选3, remain=4 → 选3, remain=1 → 选3, 3>1 ✗ break
                         选6, 6>4 ✗ break
        选6, remain=1 → 选6, 6>1 ✗ break
        选7, remain=0 ✓ → [7]

      结果: [[2,2,3],[7]]

    【时间复杂度】O(n^(T/M))  T=target, M=min(candidates)
    【空间复杂度】O(T/M)  递归栈深度
    """
    def combinationSum(self, candidates: List[int], target: int) -> List[List[int]]:
        candidates.sort()
        result = []
        self._backtrack(candidates, target, 0, [], result)
        return result

    def _backtrack(self, candidates: List[int], remain: int, start: int,
                   path: list, result: list):
        if remain == 0:
            result.append(path[:])
            return
        for i in range(start, len(candidates)):
            if candidates[i] > remain:
                break
            path.append(candidates[i])
            self._backtrack(candidates, remain - candidates[i], i, path, result)
            path.pop()

    """
    ==================== 解法二：动态规划思路 ====================

    【核心思路】
    类似完全背包问题。用 dp[t] 表示"和为 t 的所有组合的列表"。
    对于每个候选数 c，更新 dp[c..target]：
      dp[t] += [combo + [c] for combo in dp[t-c]]
    为了避免重复组合，外层遍历候选数，内层遍历目标值。

    【思考过程】
    1. 回溯是"选择"的思路，DP 是"状态转移"的思路。
       这道题可以看作完全背包：每个物品（候选数）可以选任意次，
       要装满容量为 target 的背包。

    2. 关键是避免重复：外层遍历物品（候选数），内层遍历容量（目标值）。
       这样保证了每个组合中的数字是按候选数的顺序排列的。

    3. 与标准背包不同的是，这里要记录所有具体的组合，不只是计数。
       所以 dp[t] 存的是 List[List[int]]。

    4. 空间上比回溯大得多，因为要存储所有中间状态的组合列表。
       这种方法不太常用，但展示了回溯和 DP 之间的对偶关系。

    【举例】candidates = [2,3,6,7], target = 7
      初始: dp[0] = [[]]

      处理候选数 2:
        dp[2] = dp[0] + [2] = [[2]]
        dp[4] = dp[2] + [2] = [[2,2]]
        dp[6] = dp[4] + [2] = [[2,2,2]]

      处理候选数 3:
        dp[3] = dp[0] + [3] = [[3]]
        dp[5] = dp[2] + [3] = [[2,3]]
        dp[6] += dp[3] + [3] = [[2,2,2],[3,3]]
        dp[7] = dp[4] + [3] = [[2,2,3]]

      处理候选数 7:
        dp[7] += dp[0] + [7] = [[2,2,3],[7]]

      结果: dp[7] = [[2,2,3],[7]]

    【时间复杂度】O(n * target * 组合数量)  实际取决于组合总数
    【空间复杂度】O(target * 组合数量)  存储所有中间和最终组合
    """
    def combinationSumDP(self, candidates: List[int], target: int) -> List[List[int]]:
        dp = [[] for _ in range(target + 1)]
        dp[0] = [[]]

        for c in candidates:
            for t in range(c, target + 1):
                for combo in dp[t - c]:
                    dp[t].append(combo + [c])

        return dp[target]
