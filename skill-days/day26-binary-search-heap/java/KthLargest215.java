/**
 * LeetCode 215. Kth Largest Element in an Array
 * 难度: Medium
 *
 * 题目描述：
 * 给定整数数组 nums 和整数 k，请返回数组中第 k 个最大的元素。
 * 请注意，你需要找的是数组排序后的第 k 个最大的元素，而不是第 k 个不同的元素。
 * 你必须设计并实现时间复杂度为 O(n) 的算法解决此题。
 *
 * 示例：nums = [3,2,1,5,6,4], k = 2 → 输出 5
 *
 * 【拓展练习】
 * 1. LeetCode 347. Top K Frequent Elements —— 前K个高频元素，堆/桶排序
 * 2. LeetCode 703. Kth Largest Element in a Stream —— 数据流中的第K大元素
 * 3. LeetCode 973. K Closest Points to Origin —— 最接近原点的K个点
 */

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

class KthLargest215 {

    /**
     * ==================== 解法一：快速选择 ====================
     *
     * 【核心思路】
     * 借鉴快排的 partition，每次选一个 pivot 将数组分为三部分：
     * 大于pivot、等于pivot、小于pivot。根据第 k 大落在哪个部分，
     * 只需递归处理对应的一部分，平均只需 O(n) 时间。
     *
     * 【思考过程】
     * 1. 第 k 大等价于第 n-k+1 小（0-index 下是第 n-k 小）。
     *
     * 2. 快排每次 partition 后，pivot 的位置确定了。
     *    如果 pivot 的位置恰好是我们要找的，直接返回。
     *    否则只递归一侧，所以期望时间 n + n/2 + n/4 + ... = O(n)。
     *
     * 3. 为了避免最坏 O(n²)（每次选到最大/最小元素），
     *    使用随机选 pivot 的策略。
     *
     * 4. 这里用三路划分（荷兰国旗问题）：
     *    把等于 pivot 的元素单独一区，这样重复元素多时更高效。
     *
     * 【举例】nums = [3,2,1,5,6,4], k = 2
     *   要找第2大 = 排序后倒数第2个
     *   随机选 pivot=4，partition 后：
     *     大于4: [5,6]  等于4: [4]  小于4: [3,2,1]
     *   第2大在"大于4"部分（2 <= len([5,6])=2）
     *   递归 [5,6], k=2
     *   选 pivot=5，partition：大于5:[6] 等于5:[5] 小于5:[]
     *   第2大，len([6])=1 < 2，2 <= 1+1=2，返回5
     *
     * 【时间复杂度】平均 O(n)，最坏 O(n²)（随机化后概率极低）
     * 【空间复杂度】O(1)（迭代写法）
     */
    public int findKthLargestQuickSelect(int[] nums, int k) {
        Random rand = new Random();
        int left = 0, right = nums.length - 1;
        k = k - 1;

        while (left <= right) {
            int pivotIdx = left + rand.nextInt(right - left + 1);
            int pivot = nums[pivotIdx];
            swap(nums, pivotIdx, right);

            int store = left;
            for (int i = left; i < right; i++) {
                if (nums[i] > pivot) {
                    swap(nums, store, i);
                    store++;
                }
            }

            int eqStart = store;
            for (int i = store; i < right; i++) {
                if (nums[i] == pivot) {
                    swap(nums, store, i);
                    store++;
                }
            }

            swap(nums, store, right);
            int eqEnd = store + 1;

            if (k < eqStart) {
                right = eqStart - 1;
            } else if (k >= eqEnd) {
                left = eqEnd;
            } else {
                return nums[eqStart];
            }
        }

        return nums[left];
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

    /**
     * ==================== 解法二：最小堆 ====================
     *
     * 【核心思路】
     * 维护一个大小为 k 的最小堆。遍历数组，始终保留最大的 k 个元素。
     * 遍历完成后，堆顶就是第 k 大的元素。
     *
     * 【思考过程】
     * 1. 如果用最大堆，弹出 k-1 次堆顶后的堆顶就是答案，
     *    但建堆 O(n)，弹出 k-1 次每次 O(log n)，总共 O(n + k log n)。
     *
     * 2. 更好的做法：用最小堆，大小限制为 k。
     *    - 前 k 个元素直接入堆
     *    - 之后每个元素，如果比堆顶大，就替换堆顶
     *    - 最终堆顶就是第 k 大
     *
     * 3. 为什么是最小堆而不是最大堆？
     *    最小堆的堆顶是堆中最小的，即第 k 大。
     *    每次淘汰的是堆中最小的，保留的永远是见过的最大的 k 个。
     *
     * 【举例】nums = [3,2,1,5,6,4], k = 2
     *   初始堆（前2个）：[2, 3]  堆顶=2
     *   元素1: 1 < 2，跳过
     *   元素5: 5 > 2，替换 → [3, 5]  堆顶=3
     *   元素6: 6 > 3，替换 → [5, 6]  堆顶=5
     *   元素4: 4 < 5，跳过
     *   堆顶 = 5，即第2大
     *
     * 【时间复杂度】O(n log k)
     * 【空间复杂度】O(k)
     */
    public int findKthLargestHeap(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }

    /**
     * ==================== 解法三：排序 ====================
     *
     * 【核心思路】
     * 直接排序，返回第 k 大的元素。
     *
     * 【思考过程】
     * 1. 最直接的做法：排序后，第 k 大就是 nums[n-k]（升序排列）。
     *
     * 2. 简单但时间复杂度不满足题目要求的 O(n)。
     *    作为基准解法和正确性验证用。
     *
     * 【举例】nums = [3,2,1,5,6,4], k = 2
     *   排序 → [1,2,3,4,5,6]
     *   第2大 = nums[6-2] = nums[4] = 5
     *
     * 【时间复杂度】O(n log n)
     * 【空间复杂度】O(1) 原地排序
     */
    public int findKthLargestSort(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length - k];
    }
}
