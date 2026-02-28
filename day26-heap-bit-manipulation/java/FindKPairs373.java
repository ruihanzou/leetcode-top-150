/**
 * LeetCode 373. Find K Pairs with Smallest Sums
 * 难度: Medium
 *
 * 题目描述：
 * 给定两个以非递减顺序排列的整数数组 nums1 和 nums2，以及一个整数 k，
 * 返回最小的 k 个数对 (u, v)，其中 u 来自 nums1，v 来自 nums2。
 * 数对 (u, v) 的和为 u + v。
 *
 * 示例 1：nums1 = [1,7,11], nums2 = [2,4,6], k = 3
 *   输出：[[1,2],[1,4],[1,6]]
 * 示例 2：nums1 = [1,1,2], nums2 = [1,2,3], k = 2
 *   输出：[[1,1],[1,1]]
 * 示例 3：nums1 = [1,2], nums2 = [3], k = 3
 *   输出：[[1,3],[2,3]]
 *
 * 【拓展练习】
 * 1. LeetCode 378. Kth Smallest Element in a Sorted Matrix —— 矩阵中第K小元素，类似多路归并
 * 2. LeetCode 719. Find K-th Smallest Pair Distance —— 二分+双指针求第K小距离
 * 3. LeetCode 786. K-th Smallest Prime Fraction —— 有序分数矩阵中第K小，同样可用堆
 */

import java.util.*;

class FindKPairs373 {

    /**
     * ==================== 解法一：最小堆 BFS 扩展 ====================
     *
     * 【核心思路】
     * 把问题想象成一个 m×n 的矩阵，其中 matrix[i][j] = nums1[i] + nums2[j]。
     * 矩阵的每一行和每一列都是递增的。从左上角 (0,0) 开始，用最小堆做类 BFS 扩展，
     * 每次弹出当前最小的 (i,j)，然后将 (i+1,j) 和 (i,j+1) 加入堆（需去重）。
     *
     * 【思考过程】
     * 1. 暴力枚举所有 m×n 个数对再排序是 O(mn log(mn))，太慢。
     * 2. 矩阵的递增性质保证了 (0,0) 是全局最小。弹出一个最小元素后，
     *    下一个最小元素一定在其右邻或下邻中——类似 BFS 层次扩展。
     * 3. 用 visited 集合避免重复入堆。每次从堆中弹出的就是当前全局最小。
     * 4. 最多弹 k 次即可得到答案。
     *
     * 【举例】nums1 = [1,7,11], nums2 = [2,4,6], k = 3
     *   矩阵：
     *         2   4   6
     *     1 [ 3   5   7 ]
     *     7 [ 9  11  13 ]
     *    11 [13  15  17 ]
     *
     *   初始堆: [(3, 0, 0)]
     *   弹出(3, 0,0) → 结果[(1,2)]，入堆(5,0,1)和(9,1,0)
     *   弹出(5, 0,1) → 结果[(1,2),(1,4)]，入堆(7,0,2)和(11,1,1)
     *   弹出(7, 0,2) → 结果[(1,2),(1,4),(1,6)]，已满k=3
     *
     * 【时间复杂度】O(k log k)，堆中最多 k 个元素
     * 【空间复杂度】O(k)，堆和 visited 集合
     */
    public List<List<Integer>> kSmallestPairsBFS(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums1.length == 0 || nums2.length == 0) return result;

        int m = nums1.length, n = nums2.length;
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        Set<Long> visited = new HashSet<>();

        heap.offer(new int[]{nums1[0] + nums2[0], 0, 0});
        visited.add(0L);

        while (!heap.isEmpty() && result.size() < k) {
            int[] cur = heap.poll();
            int i = cur[1], j = cur[2];
            result.add(Arrays.asList(nums1[i], nums2[j]));

            if (i + 1 < m) {
                long key = (long)(i + 1) * n + j;
                if (visited.add(key)) {
                    heap.offer(new int[]{nums1[i + 1] + nums2[j], i + 1, j});
                }
            }
            if (j + 1 < n) {
                long key = (long)i * n + (j + 1);
                if (visited.add(key)) {
                    heap.offer(new int[]{nums1[i] + nums2[j + 1], i, j + 1});
                }
            }
        }

        return result;
    }

    /**
     * ==================== 解法二：最小堆逐列扩展 ====================
     *
     * 【核心思路】
     * 只在 nums2 方向上扩展：先把每行的第一个元素 (nums1[i]+nums2[0], i, 0)
     * 全部入堆，然后每次弹出堆顶 (i, j) 后，只将同行的下一个 (i, j+1) 入堆。
     * 这样天然不会重复，无需 visited 集合。
     *
     * 【思考过程】
     * 1. BFS 扩展需要 visited 去重，能否避免？
     * 2. 观察：固定 i，j 的增长方向是单调的（0→1→2→...）。
     *    如果我们把"每行当前最小未使用的列"用堆管理，
     *    就变成了经典的"k 路归并"问题。
     * 3. 初始时堆里放每行的第 0 列（最多 min(m, k) 个），
     *    每次弹出后把同一行的下一列推入。
     * 4. 不需要 visited，因为每个 (i,j) 只会从 (i,j-1) 弹出后产生。
     *
     * 【举例】nums1 = [1,7,11], nums2 = [2,4,6], k = 3
     *   初始堆: [(3,0,0), (9,1,0), (13,2,0)]
     *   弹出(3,0,0) → 结果[(1,2)]，入堆(5,0,1)
     *   堆: [(5,0,1), (9,1,0), (13,2,0)]
     *   弹出(5,0,1) → 结果[(1,2),(1,4)]，入堆(7,0,2)
     *   弹出(7,0,2) → 结果[(1,2),(1,4),(1,6)]，已满k=3
     *
     * 【时间复杂度】O(k log(min(m, k)))，堆大小最多 min(m, k)
     * 【空间复杂度】O(min(m, k))
     */
    public List<List<Integer>> kSmallestPairsColumn(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums1.length == 0 || nums2.length == 0) return result;

        int m = nums1.length, n = nums2.length;
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        for (int i = 0; i < Math.min(m, k); i++) {
            heap.offer(new int[]{nums1[i] + nums2[0], i, 0});
        }

        while (!heap.isEmpty() && result.size() < k) {
            int[] cur = heap.poll();
            int i = cur[1], j = cur[2];
            result.add(Arrays.asList(nums1[i], nums2[j]));

            if (j + 1 < n) {
                heap.offer(new int[]{nums1[i] + nums2[j + 1], i, j + 1});
            }
        }

        return result;
    }
}
