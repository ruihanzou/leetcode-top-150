"""
LeetCode 135. Candy
难度: Hard

题目描述：
n 个孩子站成一排，每个孩子有一个评分 ratings[i]。
你需要按照以下规则给这些孩子分糖果：
  - 每个孩子至少分到 1 颗糖果
  - 相邻的孩子中，评分更高的孩子必须比他的邻居获得更多的糖果
求最少需要准备多少颗糖果。

示例 1：
  输入：ratings = [1,0,2]
  输出：5
  解释：分配方案 [2,1,2]，共 5 颗

示例 2：
  输入：ratings = [1,2,2]
  输出：4
  解释：分配方案 [1,2,1]，共 4 颗（第三个孩子只需 1 颗，因为只需大于评分更低的邻居）

【拓展练习】
1. 如果要求评分相同的相邻孩子也必须获得相同数量的糖果，如何修改？
2. 如果孩子围成一圈而不是站成一排，如何处理？
"""

from typing import List


class Solution:
    """
    ==================== 解法一：两遍遍历贪心 ====================

    【核心思路】
    分两次遍历：左→右保证右边评分高的比左邻多，右→左保证左边评分高的比右邻多，
    取两次中较大值即可同时满足两个方向的约束。

    【思考过程】
    1. 初始化 candy 数组，每人 1 颗
    2. 从左到右遍历：如果 ratings[i] > ratings[i-1]，则 candy[i] = candy[i-1] + 1
       这保证了"右边评分高于左边"的约束
    3. 从右到左遍历：如果 ratings[i] > ratings[i+1]，则 candy[i] = max(candy[i], candy[i+1] + 1)
       用 max 是因为不能破坏第一遍已经满足的约束
    4. 求和即为答案

    【举例】ratings = [1, 3, 5, 3, 2, 1]
    初始化:        [1, 1, 1, 1, 1, 1]
    左→右:  1<3 → [1, 2, 1, 1, 1, 1]
            3<5 → [1, 2, 3, 1, 1, 1]
    右→左:  2>1 → candy[4]=max(1,2)=2 → [1, 2, 3, 1, 2, 1]
            3>2 → candy[3]=max(1,3)=3 → [1, 2, 3, 3, 2, 1]
            5>3 → candy[2]=max(3,4)=4 → [1, 2, 4, 3, 2, 1]
    结果: [1, 2, 4, 3, 2, 1] → 总和 = 13

    【时间复杂度】O(n) — 两次遍历
    【空间复杂度】O(n) — candy 数组
    """
    def candy_two_pass(self, ratings: List[int]) -> int:
        n = len(ratings)
        candy = [1] * n

        for i in range(1, n): # from left to right
            if ratings[i] > ratings[i - 1]: # find the ascending order and give more candy
                candy[i] = candy[i - 1] + 1

        for i in range(n - 2, -1, -1): # from right to left
            if ratings[i] > ratings[i + 1]: # find the descending order and give more candy
                # the length of the descending order may be longer than the ascending order, so we need to take the max
                # the candy[i] is the candy of the current child, the candy[i + 1] + 1 is the candy of the next child
                candy[i] = max(candy[i], candy[i + 1] + 1) 
        return sum(candy) # time complexity: O(n), space complexity: O(n)


    """
    ==================== 解法二：一遍遍历（上升/下降序列法）====================

    【为什么要有一遍遍历的写法？】
    解法一已经 O(n) 时间、O(n) 空间。一遍遍历的动机是：
    - 空间降到 O(1)：不存整个 candy 数组，只维护「当前这一段」的信息；
    - 把问题看成「折线」：从左到右走一遍，只关心当前是在上坡还是下坡、坡有多长。

    【思路是怎么来的？—— 把 ratings 看成折线】
    1. 把 ratings 画成折线图，会有「上升段」和「下降段」。
    2. 上升段：从左到右必须严格递增发糖，最少方案就是 1, 2, 3, ...（第 k 个位置给 k）。
    3. 下降段：从峰顶往右必须严格递减，最少方案是 ..., 3, 2, 1（从峰往下第 k 个给 k）。
    4. 峰顶的值 = 同时满足「比左边多」和「比右边多」，所以峰顶至少是 max(左坡长度, 右坡长度)。
       但遍历时我们「先经历上坡、后经历下坡」，在到达峰顶时还不知道下坡有多长，
       所以先按「上坡」给峰顶赋值；等发现下坡很长时，再给峰顶「补一颗」。

    【变量含义】
    - up：当前连续上升的「步数」（即这一段上坡除了起点外走了几步）。
    - down：当前连续下降的「步数」（即这一段下坡从峰顶往后走了几步）。
    - peak：最近一次峰顶的糖果数（即上坡结束时给峰顶的值 = up+1）。
    - total：当前累加的糖果总数。

    【三种情况怎么处理？】

    (1) 上升 (ratings[i] > ratings[i-1])
        - 进入或继续上坡，下坡要清零：down = 0。
        - 上坡多了一步：up += 1；当前这个位置应该是 up+1 颗（第 1 步给 2，第 2 步给 3，…）。
        - 当前点就是「当前上坡的峰顶」，记 peak = up + 1，total += peak。

    (2) 下降 (ratings[i] < ratings[i-1])
        - 进入或继续下坡，上坡要清零：up = 0。
        - 下坡多了一步：down += 1。从峰顶往后数，第 1 个下坡点给 1，第 2 个给 2，…所以当前给 down 颗：total += down。
        - 关键——为什么要「补峰顶」？
          峰顶之前按上坡算，只给了 peak = 当时的 up+1 颗。如果下坡很长，比如下坡有 peak 个点，
          那按 1,2,...,peak 发糖，峰顶右侧第一个点会拿到 peak 颗，和峰顶一样多，违反「评分高则糖多」。
          所以当「下坡长度」down 达到 peak 时，说明峰顶必须至少是 peak+1 才行，因此 total += 1（给峰顶补一颗）。

    (3) 平坦 (ratings[i] == ratings[i-1])
        - 既不上升也不下降，up、down 都清零，当前点只给 1 颗，peak 记为 1，total += 1。

    【完整手推】ratings = [1, 3, 5, 3, 2, 1]
    索引:    0   1   2   3   4   5
    rating:  1   3   5   3   2   1
             ·   ↗   ↗   ↘   ↘   ↘   （· 起点，↗ 上坡，↘ 下坡）

    i=0: 起点，total=1, up=0, down=0, peak=1
    i=1: 1<3 上升 → up=1, down=0, peak=2, total += 2 → total=3
    i=2: 3<5 上升 → up=2, down=0, peak=3, total += 3 → total=6   （峰顶 5 得到 3 颗）
    i=3: 5>3 下降 → up=0, down=1, total += 1；down(1) < peak(3) 不补 → total=7
    i=4: 3>2 下降 → down=2, total += 2；down(2) < peak(3) 不补 → total=9
    i=5: 2>1 下降 → down=3, total += 3；down(3) >= peak(3) 补 1 → total=13
    最终糖果 [1, 2, 4, 3, 2, 1]，和两遍遍历结果一致。

    【时间复杂度】O(n) — 一次遍历
    【空间复杂度】O(1) — 只用常数变量
    """
    def candy_one_pass(self, ratings: List[int]) -> int:
        n = len(ratings)
        if n <= 1:
            return n

        total = 1
        up, down, peak = 0, 0, 1

        for i in range(1, n):
            if ratings[i] > ratings[i - 1]:
                up += 1
                down = 0
                peak = up + 1
                total += peak
            elif ratings[i] < ratings[i - 1]:
                up = 0
                down += 1
                total += down
                if down >= peak:
                    total += 1
            else:
                up = 0
                down = 0
                peak = 1
                total += 1

        return total
