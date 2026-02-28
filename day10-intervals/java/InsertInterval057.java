/**
 * LeetCode 57. Insert Interval
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个无重叠的、按照区间起始端点排序的区间列表 intervals，
 * 其中 intervals[i] = [starti, endi] 表示第 i 个区间的开始和结束。
 * 同样给定一个区间 newInterval = [start, end] 表示另一个区间的开始和结束。
 * 在 intervals 中插入区间 newInterval，使得 intervals 仍然按照起始端点排序，
 * 且区间之间互不重叠（如果有必要的话，可以合并区间）。
 * 返回插入之后的 intervals。
 *
 * 示例 1：intervals = [[1,3],[6,9]], newInterval = [2,5]
 *   → 输出 [[1,5],[6,9]]
 *
 * 示例 2：intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
 *   → 输出 [[1,2],[3,10],[12,16]]
 *   解释：新区间 [4,8] 与 [3,5],[6,7],[8,10] 重叠，合并为 [3,10]
 *
 * 【拓展练习】
 * 1. LeetCode 56. Merge Intervals —— 合并所有重叠区间
 * 2. LeetCode 715. Range Module —— 用线段维护区间的添加/删除/查询
 * 3. LeetCode 986. Interval List Intersections —— 两个有序区间列表求交集
 */

import java.util.ArrayList;
import java.util.List;

class InsertInterval057 {

    /**
     * ==================== 解法一：线性扫描三阶段 ====================
     *
     * 【核心思路】
     * 将原区间列表分成三个部分处理：
     * ① 完全在 newInterval 左侧的区间（不重叠）→ 直接加入结果
     * ② 与 newInterval 重叠的区间 → 合并到 newInterval 中
     * ③ 完全在 newInterval 右侧的区间（不重叠）→ 直接加入结果
     * 合并后的 newInterval 在阶段②结束后加入结果。
     *
     * 【思考过程】
     * 1. 原列表已按左端点排序且互不重叠。
     *    → 所有在 newInterval 左侧的区间一定连续排列在前面。
     *    → 所有在右侧的区间也一定连续排列在后面。
     *    → 中间那些与 newInterval 重叠的也是连续的。
     *
     * 2. "完全在左侧"的判定：interval[1] < newInterval[0]
     *    （原区间的右端点 < 新区间的左端点 → 不可能重叠）
     *
     * 3. "重叠"的判定：interval[0] <= newInterval[1]
     *    （原区间的左端点 <= 新区间的右端点 → 还有交集）
     *    合并方式：newInterval = [min(左), max(右)]
     *
     * 4. 剩余的就是"完全在右侧"的区间。
     *
     * 【举例】intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
     *   阶段①：[1,2] 的右端点 2 < 4 → 加入结果。result = [[1,2]]
     *   阶段②：[3,5] 的左端点 3 <= 8 → 合并: new = [min(4,3),max(8,5)] = [3,8]
     *          [6,7] 的左端点 6 <= 8 → 合并: new = [min(3,6),max(8,7)] = [3,8]
     *          [8,10] 的左端点 8 <= 8 → 合并: new = [min(3,8),max(8,10)] = [3,10]
     *          [12,16] 的左端点 12 > 10 → 停。加入 new=[3,10]
     *          result = [[1,2],[3,10]]
     *   阶段③：[12,16] → 加入结果。result = [[1,2],[3,10],[12,16]]
     *
     * 【时间复杂度】O(n)，一次遍历
     * 【空间复杂度】O(n)，存储结果
     */
    public int[][] insertLinear(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0, n = intervals.length;

        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        while (i < n && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);

        while (i < n) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }

    /**
     * ==================== 解法二：二分查找 + 合并 ====================
     *
     * 【核心思路】
     * 用二分查找快速定位 newInterval 影响的区间范围，
     * 找到第一个可能重叠的区间和最后一个可能重叠的区间，然后合并。
     *
     * 【思考过程】
     * 1. 线性扫描的阶段①和③本质是在找两个边界：
     *    - left：第一个右端点 >= newInterval[0] 的区间索引
     *    - right：最后一个左端点 <= newInterval[1] 的区间索引
     *    这两个搜索可以用二分加速。
     *
     * 2. 用二分找 left：在所有区间的右端点中找第一个 >= newInterval[0] 的。
     *    用二分找 right：在所有区间的左端点中找最后一个 <= newInterval[1] 的。
     *
     * 3. [left, right] 范围内的区间全部与 newInterval 重叠，合并它们。
     *    合并结果的左端点 = min(intervals[left][0], newInterval[0])
     *    合并结果的右端点 = max(intervals[right][1], newInterval[1])
     *
     * 4. 最终结果 = intervals[0..left-1] + 合并区间 + intervals[right+1..n-1]
     *
     * 【举例】intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
     *   找 left：右端点序列 [2,5,7,10,16]，第一个 >= 4 的是 5（索引1）→ left=1
     *   找 right：左端点序列 [1,3,6,8,12]，最后一个 <= 8 的是 8（索引3）→ right=3
     *   合并 intervals[1..3] = [3,5],[6,7],[8,10] 与 [4,8]：
     *     左 = min(3,4) = 3，右 = max(10,8) = 10 → [3,10]
     *   结果：[[1,2]] + [[3,10]] + [[12,16]] = [[1,2],[3,10],[12,16]]
     *
     * 【时间复杂度】O(n)，虽然二分查找是 O(log n)，但构建结果数组需要 O(n)
     * 【空间复杂度】O(n)，存储结果
     */
    public int[][] insertBinarySearch(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        if (n == 0) return new int[][]{newInterval};

        int left = findLeft(intervals, newInterval[0]);
        int right = findRight(intervals, newInterval[1]);

        List<int[]> result = new ArrayList<>();

        for (int i = 0; i < left; i++) {
            result.add(intervals[i]);
        }

        if (left > right) {
            result.add(newInterval);
        } else {
            int mergedStart = Math.min(intervals[left][0], newInterval[0]);
            int mergedEnd = Math.max(intervals[right][1], newInterval[1]);
            result.add(new int[]{mergedStart, mergedEnd});
        }

        for (int i = right + 1; i < n; i++) {
            result.add(intervals[i]);
        }

        return result.toArray(new int[result.size()][]);
    }

    private int findLeft(int[][] intervals, int target) {
        int lo = 0, hi = intervals.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (intervals[mid][1] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }

    private int findRight(int[][] intervals, int target) {
        int lo = 0, hi = intervals.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (intervals[mid][0] <= target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return hi;
    }
}
