/**
 * LeetCode 274. H-Index
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个整数数组 citations，其中 citations[i] 表示研究者第 i 篇论文的引用次数，
 * 返回该研究者的 H 指数。
 * H 指数定义：h 代表"高引用次数"，一名研究者的 h 指数是指其发表的 n 篇论文中，
 * 总共有 h 篇论文分别被引用了至少 h 次，且其余 n-h 篇论文每篇被引用次数不超过 h 次。
 *
 * 示例：citations = [3,0,6,1,5] → 输出 3
 *
 * 【拓展练习】
 * 1. LeetCode 275. H-Index II —— 引用数组已排序，用二分查找优化到O(log n)
 * 2. LeetCode 1281. Subtract the Product and Sum of Digits of an Integer —— 数组基本操作练习
 * 3. LeetCode 442. Find All Duplicates in an Array —— 利用数组索引做计数的技巧
 */

import java.util.Arrays;

class HIndex274 {

    /**
     * ==================== 解法一：排序 ====================
     *
     * 【核心思路】
     * 将 citations 降序排列后，第 i 篇论文（1-index）的引用数 >= i
     * 说明"至少有 i 篇论文引用数 >= i"，即 h=i 是合法的。
     * 我们要找的就是满足该条件的最大 i。
     *
     * 【思考过程】
     * 1. h 的定义是"至少 h 篇论文引用 >= h"。
     *    如果我们把论文按引用数从高到低排好，那么排在第 1 位的引用数最大，
     *    排在第 2 位的次大……排在第 i 位的是"引用数第 i 大"的论文。
     *
     * 2. 如果第 i 大的论文引用数 >= i，说明前 i 篇论文的引用数全部 >= i，
     *    因此 h=i 成立。
     *
     * 3. 我们需要的是最大的合法 h，所以从大到小扫描 i，第一个满足条件的就是答案。
     *    等价地，从小到大扫描 i，取最后一个满足条件的。
     *
     * 【举例】citations = [3,0,6,1,5]
     *   降序排列 → [6,5,3,1,0]
     *   i=1: citations[0]=6 >= 1 ✓  (至少1篇引用>=1)
     *   i=2: citations[1]=5 >= 2 ✓  (至少2篇引用>=2)
     *   i=3: citations[2]=3 >= 3 ✓  (至少3篇引用>=3)
     *   i=4: citations[3]=1 >= 4 ✗  停下，答案 h=3
     *
     * 【时间复杂度】O(n log n) 排序主导
     * 【空间复杂度】O(1) 原地排序（不计排序栈空间）
     */
    public int hIndexSort(int[] citations) {
        Arrays.sort(citations);
        int n = citations.length;
        for (int i = 0; i < n; i++) {
            // citations 升序，citations[n-1-i] 是第 i+1 大的值
            if (citations[n - 1 - i] < i + 1) {
                return i;
            }
        }
        return n;
    }

    /**
     * ==================== 解法二：计数排序（桶排序） ====================
     *
     * 【核心思路】
     * h 最大不可能超过 n（论文总数），所以引用数超过 n 的和恰好等于 n 没有区别。
     * 利用这个性质建桶 count[0..n]，然后从 h=n 递减，累加论文数，
     * 第一个满足 total >= h 的就是最大 h。
     *
     * 【思考过程】
     * 1. 排序解法瓶颈是 O(n log n)，能否做到 O(n)？
     *    → 如果值域有限（0..n），可以用计数排序。
     *
     * 2. 引用数可能很大（比如 1000），但 h 最多是 n。
     *    所以 c > n 的论文，对判断 h <= n 时的贡献和 c = n 一样。
     *    → 把所有 c > n 截断到 n，值域就压缩到 [0, n]。
     *
     * 3. 建好桶后，想检验 h=k 是否合法，需要知道"引用数 >= k 的论文有多少篇"。
     *    这就是 count[k] + count[k+1] + ... + count[n]，即后缀和。
     *
     * 4. 从 h=n 开始往下走，每次把 count[h] 加入 total（累计后缀和），
     *    一旦 total >= h，就找到了最大可行 h。
     *
     * 【举例】citations = [3,0,6,1,5]，n=5
     *   桶：count = [1, 1, 0, 1, 0, 2]
     *        下标:  0  1  2  3  4  5
     *   (6被截断到5，所以count[5]=2表示引用数5和6各一篇)
     *
     *   h=5: total=0+2=2,  2<5 ✗
     *   h=4: total=2+0=2,  2<4 ✗
     *   h=3: total=2+1=3,  3>=3 ✓ → 返回3
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public int hIndexCount(int[] citations) {
        int n = citations.length;
        int[] count = new int[n + 1];

        for (int c : citations) {
            count[Math.min(c, n)]++;
        }

        int total = 0;
        for (int h = n; h >= 0; h--) {
            total += count[h];
            if (total >= h) {
                return h;
            }
        }

        return 0;
    }

    /**
     * ==================== 解法三：二分查找 ====================
     *
     * 【核心思路】
     * 对 h 的候选值 [0, n] 做二分搜索。
     * 对于某个 mid，数一下"引用数 >= mid 的论文有多少篇"，
     * 如果 >= mid，说明 mid 可行，试更大的；否则试更小的。
     *
     * 【思考过程】
     * 1. 合法的 h 值有"单调性"：如果 h=k 合法，那 h=k-1 也一定合法
     *    （引用数 >= k 的论文一定也 >= k-1）。
     *    反之，如果 h=k 不合法，h=k+1 也不合法。
     *    → 这就是经典的"二分答案"模型。
     *
     * 2. 对于候选 mid，我们需要 count(citation >= mid) >= mid。
     *    数这个 count 需要遍历数组一次，O(n)。
     *
     * 3. 二分 O(log n) 轮，每轮检查 O(n)，总复杂度 O(n log n)。
     *    虽然渐进和排序一样，但这种方法展示了"二分答案"的经典套路，
     *    适合扩展到更复杂的场景。
     *
     * 【举例】citations = [3,0,6,1,5]，n=5
     *   lo=0, hi=5
     *   mid=2: 引用>=2的论文=[3,6,5]共3篇, 3>=2 ✓ → lo=3
     *   mid=4: 引用>=4的论文=[6,5]共2篇, 2<4 ✗ → hi=3
     *   mid=3: 引用>=3的论文=[3,6,5]共3篇, 3>=3 ✓ → lo=4
     *   lo>hi，答案=3
     *
     * 【时间复杂度】O(n log n)
     * 【空间复杂度】O(1)
     */
    public int hIndexBinarySearch(int[] citations) {
        int n = citations.length;
        int lo = 0, hi = n, ans = 0;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cnt = 0;
            for (int c : citations) {
                if (c >= mid) cnt++;
            }
            if (cnt >= mid) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return ans;
    }

    /**
     * ==================== 解法四：排序 + 二分查找 ====================
     *
     * 【核心思路】
     * 先对 citations 升序排列。排序后，对于任何位置 i，
     * citations[i..n-1] 就是"引用数 >= citations[i]"的所有论文，共 n-i 篇。
     * 我们要找最大的 h 使得 n-i >= h 且 citations[i] >= h。
     * 等价于找最小的 i 使得 citations[i] >= n-i，此时 h = n-i。
     * 这个最小 i 可以用二分查找。
     *
     * 【思考过程】
     * 1. 排序后 citations[i] 是单调递增的，n-i 是单调递减的。
     *    我们找第一个 citations[i] >= n-i 的位置。
     *
     * 2. 这就是 LeetCode 275（H-Index II）的解法。
     *    如果输入已排序，可以直接 O(log n)；未排序则排序后 O(n log n)。
     *
     * 【举例】citations = [3,0,6,1,5]
     *   升序 → [0,1,3,5,6], n=5
     *   i=0: citations[0]=0, n-i=5, 0>=5? ✗
     *   i=1: citations[1]=1, n-i=4, 1>=4? ✗
     *   i=2: citations[2]=3, n-i=3, 3>=3? ✓ → h=n-i=3
     *
     *   用二分找这个 i=2：
     *   lo=0, hi=4
     *   mid=2: citations[2]=3 >= n-2=3? ✓ → hi=1
     *   mid=0: citations[0]=0 >= n-0=5? ✗ → lo=1
     *   mid=1: citations[1]=1 >= n-1=4? ✗ → lo=2
     *   lo=2 > hi=1, 答案 h=n-lo=5-2=3
     *
     * 【时间复杂度】O(n log n) 排序主导，二分本身 O(log n)
     * 【空间复杂度】O(1)
     */
    public int hIndexSortBinary(int[] citations) {
        Arrays.sort(citations);
        int n = citations.length;
        int lo = 0, hi = n - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (citations[mid] >= n - mid) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }

        return n - lo;
    }
}
