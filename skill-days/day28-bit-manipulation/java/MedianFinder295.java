/**
 * LeetCode 295. Find Median from Data Stream
 * 难度: Hard
 *
 * 题目描述：
 * 中位数是有序整数列表中间的数。如果列表大小是偶数，中位数是中间两个数的平均值。
 *
 * 实现 MedianFinder 类：
 * - MedianFinder() 初始化 MedianFinder 对象。
 * - void addNum(int num) 将数据流中的整数 num 添加到数据结构中。
 * - double findMedian() 返回到目前为止所有元素的中位数。
 *
 * 示例：
 *   MedianFinder medianFinder = new MedianFinder();
 *   medianFinder.addNum(1);    // arr = [1]
 *   medianFinder.addNum(2);    // arr = [1, 2]
 *   medianFinder.findMedian(); // 返回 1.5
 *   medianFinder.addNum(3);    // arr = [1, 2, 3]
 *   medianFinder.findMedian(); // 返回 2.0
 *
 * 【拓展练习】
 * 1. LeetCode 480. Sliding Window Median —— 滑动窗口中位数，需要支持删除操作的堆
 * 2. LeetCode 4. Median of Two Sorted Arrays —— 两个有序数组的中位数，二分查找
 * 3. LeetCode 703. Kth Largest Element in a Stream —— 数据流中第K大元素，单个最小堆
 */

import java.util.*;

/**
 * ==================== 解法一：对顶堆（最大堆 + 最小堆） ====================
 *
 * 【核心思路】
 * 维护两个堆：maxHeap 存较小的一半（Java PriorityQueue 默认最小堆，取反或用 reverseOrder），
 * minHeap 存较大的一半。保持 maxHeap 的大小 >= minHeap 的大小，
 * 且两者大小差不超过 1。这样中位数就是 maxHeap 堆顶，或两个堆顶的平均值。
 *
 * 【思考过程】
 * 1. 中位数 = 有序序列的中间值。如果我们把数据分成较小的一半和较大的一半，
 *    中位数就在分界处。
 * 2. 较小的一半需要快速取最大值 → 最大堆；
 *    较大的一半需要快速取最小值 → 最小堆。
 * 3. 插入时先放入 maxHeap，再把 maxHeap 的最大值弹给 minHeap，
 *    如果 minHeap 比 maxHeap 大了，再弹回来。这样保证两堆平衡。
 *
 * 【举例】依次添加 5, 2, 3, 4
 *   add(5): maxHeap=[5], 弹5到minHeap → max=[], min=[5]
 *           min比max大 → 弹5回max → max=[5], min=[]
 *           中位数 = 5
 *   add(2): max先加2=[5,2]，弹最大5到min → max=[2], min=[5]
 *           平衡，中位数 = (2+5)/2 = 3.5
 *   add(3): max先加3=[3,2]，弹最大3到min → max=[2], min=[3,5]
 *           min比max大 → 弹3回max → max=[3,2], min=[5]
 *           中位数 = 3
 *   add(4): max先加4=[4,3,2]，弹最大4到min → max=[3,2], min=[4,5]
 *           平衡，中位数 = (3+4)/2 = 3.5
 *
 * 【时间复杂度】addNum O(log n)，findMedian O(1)
 * 【空间复杂度】O(n)
 */
class MedianFinder295 {

    private PriorityQueue<Integer> maxHeap;
    private PriorityQueue<Integer> minHeap;

    public MedianFinder295() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        maxHeap.offer(num);
        minHeap.offer(maxHeap.poll());

        if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}

/**
 * ==================== 解法二：有序列表 + 二分插入 ====================
 *
 * 【核心思路】
 * 维护一个始终有序的 ArrayList，每次插入时用 Collections.binarySearch
 * 找到插入位置，然后直接根据长度的奇偶性取中间元素作为中位数。
 *
 * 【思考过程】
 * 1. 最直观的想法：保持数据始终有序，中位数直接用下标取。
 * 2. 二分查找找插入位置是 O(log n)，但 ArrayList 的插入操作是 O(n)
 *    （需要移动后面的元素）。
 * 3. 虽然插入的渐进复杂度不如堆，但实际中对于小规模数据，
 *    连续内存的缓存友好性可能让它跑得更快。
 *
 * 【举例】依次添加 5, 2, 3, 4
 *   add(5): sorted = [5]，中位数 = 5
 *   add(2): 二分找到位置0，sorted = [2, 5]，中位数 = (2+5)/2 = 3.5
 *   add(3): 二分找到位置1，sorted = [2, 3, 5]，中位数 = 3
 *   add(4): 二分找到位置2，sorted = [2, 3, 4, 5]，中位数 = (3+4)/2 = 3.5
 *
 * 【时间复杂度】addNum O(n)（二分 O(log n) + 插入 O(n)），findMedian O(1)
 * 【空间复杂度】O(n)
 */
class MedianFinderSortedList {

    private List<Integer> sortedList;

    public MedianFinderSortedList() {
        sortedList = new ArrayList<>();
    }

    public void addNum(int num) {
        int pos = Collections.binarySearch(sortedList, num);
        if (pos < 0) pos = -(pos + 1);
        sortedList.add(pos, num);
    }

    public double findMedian() {
        int n = sortedList.size();
        int mid = n / 2;
        if (n % 2 == 1) {
            return sortedList.get(mid);
        }
        return (sortedList.get(mid - 1) + sortedList.get(mid)) / 2.0;
    }
}
