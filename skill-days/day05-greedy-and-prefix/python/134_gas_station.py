"""
LeetCode 134. Gas Station
难度: Medium

题目描述：
在一条环路上有 n 个加油站，第 i 个加油站有汽油 gas[i] 升。
从第 i 个加油站开往第 i+1 个加油站需要消耗汽油 cost[i] 升。
你从某个加油站出发时油箱为空。
给定两个整数数组 gas 和 cost，如果你可以按顺序绕环路行驶一周，
则返回出发时加油站的编号，否则返回 -1。
如果存在解，则保证它是唯一的。

示例 1：
  输入：gas = [1,2,3,4,5], cost = [3,4,5,1,2]
  输出：3
  解释：从 3 号加油站出发，油箱 = 0 + 4 - 1 = 3 → 到 4 号，油箱 = 3 + 5 - 2 = 6
        → 到 0 号，油箱 = 6 + 1 - 3 = 4 → 到 1 号，油箱 = 4 + 2 - 4 = 2
        → 到 2 号，油箱 = 2 + 3 - 5 = 0 → 回到 3 号，成功！

示例 2：
  输入：gas = [2,3,4], cost = [3,4,3]
  输出：-1
  解释：总油量 9 < 总消耗 10，无论从哪出发都无法走完一圈。

【拓展练习】
1. 如果允许存在多个解，如何返回所有可行起点？
2. 如果环路是双向的（可以顺时针或逆时针），如何求解？
"""

from typing import List


class Solution:
    """
    ==================== 解法一：暴力模拟 ====================

    【核心思路】
    枚举每一个加油站作为起点，模拟跑一圈，看是否全程油量 >= 0。

    【思考过程】
    1. 对于每个起点 i，初始化油箱 tank = 0
    2. 从 i 出发，依次走 n 步，每步 tank += gas[j] - cost[j]
    3. 如果任意时刻 tank < 0，说明从 i 出发不可行，换下一个起点
    4. 如果成功走完 n 步，返回 i

    【举例】gas = [1,2,3,4,5], cost = [3,4,5,1,2]
    起点0: tank: 1-3=-2 < 0 → 失败
    起点1: tank: 2-4=-2 < 0 → 失败
    起点2: tank: 3-5=-2 < 0 → 失败
    起点3: tank: 4-1=3 → 3+5-2=6 → 6+1-3=4 → 4+2-4=2 → 2+3-5=0 → 成功，返回 3

    【时间复杂度】O(n²) — 外层枚举 n 个起点，内层模拟 n 步
    【空间复杂度】O(1)
    """
    def canCompleteCircuit_brute(self, gas: List[int], cost: List[int]) -> int:
        n = len(gas)
        for i in range(n):
            tank = 0
            valid = True
            for step in range(n):
                j = (i + step) % n
                tank += gas[j] - cost[j]
                if tank < 0:
                    valid = False
                    break
            if valid:
                return i
        return -1

    """
    ==================== 解法二：贪心一次遍历 ====================

    【核心思路】
    用 total 判断整体是否可行，用 current 找到正确的起点：
    当 current < 0 时，说明当前起点不行，将起点移到下一个位置。

    【思考过程】
    1. 关键观察：如果 total(gas) >= total(cost)，则一定存在解
    2. 如果从 i 出发到 j 时油箱变负，说明 i~j 之间的任何站点都不能作为起点
       （因为从 i 出发到中间站点时油箱 >= 0，即中间站点的初始状态比从 i 更差）
    3. 所以直接跳到 j+1 作为新起点，current 重置为 0
    4. 遍历完成后，如果 total >= 0，则 start 就是答案

    【举例】gas = [1,2,3,4,5], cost = [3,4,5,1,2]
    i=0: diff=-2, current=-2<0 → start=1, current=0
    i=1: diff=-2, current=-2<0 → start=2, current=0
    i=2: diff=-2, current=-2<0 → start=3, current=0
    i=3: diff=3,  current=3
    i=4: diff=3,  current=6
    total = (-2)+(-2)+(-2)+3+3 = 0 >= 0 → 返回 start=3

    【时间复杂度】O(n) — 只遍历一次
    【空间复杂度】O(1)
    """
    def canCompleteCircuit_greedy(self, gas: List[int], cost: List[int]) -> int:
        n = len(gas)
        total = 0 # 总油量
        current = 0 # 当前油量
        start = 0 # 起点    
        # 遍历每个加油站
        for i in range(n):
            # 计算当前加油站的油量差
            diff = gas[i] - cost[i]
            total += diff # 更新总油量
            current += diff
            if current < 0:
                start = i + 1
                current = 0
        return start if total >= 0 else -1
