"""
LeetCode 77. Combinations
难度: Medium

题目描述：
给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。
你可以按任何顺序返回答案。

示例：
输入: n = 4, k = 2
输出: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]

【拓展练习】
1. LeetCode 39. Combination Sum —— 组合类回溯，元素可重复使用
2. LeetCode 40. Combination Sum II —— 组合类回溯，元素不可重复，需去重
3. LeetCode 216. Combination Sum III —— 固定 k 个数求和，组合回溯
"""

from typing import List


class Solution:
    """
    ==================== 解法一：回溯 + 剪枝 ====================

    【核心思路】
    从数字 1 开始，依次决定每个位置选哪个数。
    为了避免重复（如 [1,2] 和 [2,1]），规定后选的数必须大于先选的数，
    即用 start 参数限制搜索起点。当路径长度等于 k 时收集结果。

    【思考过程】
    1. 组合 vs 排列的关键区别：组合不考虑顺序，[1,2] 和 [2,1] 算同一个。
       → 保持升序选择即可去重：如果上一个选了 i，下一个从 i+1 开始选。

    2. 回溯框架：
       - 状态：当前路径 path，下一个可选的起始值 start。
       - 选择列表：[start, start+1, ..., n]。
       - 终止条件：len(path) == k。

    3. 剪枝优化：如果剩余可选的数字不够凑满 k 个，直接剪掉。
       还需要选 k - len(path) 个数，可选范围是 [i, n]，共 n-i+1 个。
       如果 n - i + 1 < k - len(path)，即 i > n - (k - len(path)) + 1，
       就不必继续了。

    【举例】n = 4, k = 2
      start=1:
        选1 → start=2:
          选2 → [1,2] ✓
          选3 → [1,3] ✓
          选4 → [1,4] ✓
        选2 → start=3:
          选3 → [2,3] ✓
          选4 → [2,4] ✓
        选3 → start=4:
          选4 → [3,4] ✓
        选4 → start=5: 还需1个数但超出范围 → 剪枝

      结果: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]

    【时间复杂度】O(C(n,k) * k)  生成 C(n,k) 个组合，每个复制需 O(k)
    【空间复杂度】O(k) 递归深度和路径长度
    """
    def combine(self, n: int, k: int) -> List[List[int]]:
        result = []
        self._backtrack(n, k, 1, [], result)
        return result

    def _backtrack(self, n: int, k: int, start: int, path: list, result: list):
        if len(path) == k:
            result.append(path[:])
            return
        need = k - len(path)
        for i in range(start, n - need + 2):
            path.append(i)
            self._backtrack(n, k, i + 1, path, result)
            path.pop()

    """
    ==================== 解法二：迭代 / 字典序法 ====================

    【核心思路】
    模拟字典序枚举：维护一个长度为 k 的数组表示当前组合，
    每次找到最右边可以"加 1"的位置，将其加 1 后，后面的位置依次填充连续值。
    这样可以不用递归地按字典序生成所有组合。

    【思考过程】
    1. 将组合看作一个 k 位的"数"，每一位的取值范围与前一位有关。
       字典序中，最小的组合是 [1,2,...,k]，最大的是 [n-k+1,...,n]。

    2. 从最小组合开始，每次生成"下一个"组合：
       - 从最右位开始，找到第一个还能增大的位置 j（即 combo[j] < n - k + j + 1）。
       - 将 combo[j] += 1，然后 combo[j+1] = combo[j]+1, combo[j+2] = combo[j]+2, ...

    3. 如果所有位都不能增大了（即 combo[0] == n-k+1），说明已经生成完毕。

    【举例】n = 5, k = 3
      初始: [1,2,3]
      [1,2,3] → 最右位 combo[2]=3, 上限=5, 可增大 → [1,2,4]
      [1,2,4] → combo[2]=4 < 5 → [1,2,5]
      [1,2,5] → combo[2]=5 已到上限，看 combo[1]=2 < 4 → combo[1]=3, 后续填充 → [1,3,4]
      [1,3,4] → [1,3,5] → [1,4,5]
      [1,4,5] → combo[2]=5 到上限, combo[1]=4 到上限, combo[0]=1 < 3 → [2,3,4]
      [2,3,4] → [2,3,5] → [2,4,5] → [3,4,5]
      [3,4,5] → 全部到上限，结束

    【时间复杂度】O(C(n,k) * k)
    【空间复杂度】O(k) 存储当前组合
    """
    def combineIterative(self, n: int, k: int) -> List[List[int]]:
        combo = list(range(1, k + 1))
        result = []

        while True:
            result.append(combo[:])

            j = k - 1
            while j >= 0 and combo[j] == n - k + j + 1:
                j -= 1
            if j < 0:
                break

            combo[j] += 1
            for i in range(j + 1, k):
                combo[i] = combo[i - 1] + 1

        return result
