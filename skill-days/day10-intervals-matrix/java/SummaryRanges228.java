/**
 * LeetCode 228. Summary Ranges
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个无重复元素的有序整数数组 nums，返回恰好覆盖数组中所有数字的最小有序区间范围列表。
 * 也就是说，nums 的每个元素都恰好被某个区间范围覆盖，并且不存在属于某个区间范围但不属于
 * nums 的数字 x。
 *
 * 每个区间范围 [a,b] 应输出为：
 *   - "a->b"（当 a != b 时）
 *   - "a"（当 a == b 时）
 *
 * 示例 1：nums = [0,1,2,4,5,7] → 输出 ["0->2","4->5","7"]
 *   解释：区间范围是 [0,2], [4,5], [7]
 *
 * 示例 2：nums = [0,2,3,4,6,8,9] → 输出 ["0","2->4","6","8->9"]
 *   解释：区间范围是 [0,0], [2,4], [6,6], [8,9]
 *
 * 【拓展练习】
 * 1. LeetCode 163. Missing Ranges —— 找缺失的区间范围，与本题互补
 * 2. LeetCode 56. Merge Intervals —— 合并重叠区间，区间操作进阶
 * 3. LeetCode 352. Data Stream as Disjoint Intervals —— 在数据流中维护不相交区间
 */

import java.util.ArrayList;
import java.util.List;

class SummaryRanges228 {

    /**
     * ==================== 解法一：双指针 ====================
     *
     * 【核心思路】
     * 用两个指针 i 和 j 找连续序列的起点和终点。
     * i 指向当前区间的起始位置，j 向右扩展直到不再连续，
     * 然后生成区间字符串，i 跳到 j+1 开始下一段。
     *
     * 【思考过程】
     * 1. 数组已排序且无重复 → 连续的数字差值恒为 1。
     *    所以只要 nums[j+1] == nums[j] + 1，j 就继续往右走。
     *
     * 2. 当 j 停下（不连续或到达末尾），[i, j] 就是一段完整的连续序列：
     *    - 若 i == j，只有一个数，输出 "nums[i]"
     *    - 若 i < j，输出 "nums[i]->nums[j]"
     *
     * 3. 然后令 i = j + 1，继续寻找下一段。
     *
     * 【举例】nums = [0,1,2,4,5,7]
     *   i=0, j=0: nums[1]=1==0+1 → j=1; nums[2]=2==1+1 → j=2; nums[3]=4!=2+1 → 停
     *     区间 [0,2] → "0->2"，i=3
     *   i=3, j=3: nums[4]=5==4+1 → j=4; nums[5]=7!=5+1 → 停
     *     区间 [3,4]对应值[4,5] → "4->5"，i=5
     *   i=5, j=5: 到末尾 → 停
     *     区间 [5,5]对应值[7] → "7"，i=6
     *   结果：["0->2","4->5","7"]
     *
     * 【时间复杂度】O(n)，每个元素恰好被访问一次
     * 【空间复杂度】O(1)，不计输出空间
     */
    public List<String> summaryRangesTwoPointers(int[] nums) {
        List<String> result = new ArrayList<>();
        int n = nums.length;
        int i = 0;

        while (i < n) {
            int j = i;
            while (j + 1 < n && nums[j + 1] == nums[j] + 1) {
                j++;
            }
            if (i == j) {
                result.add(String.valueOf(nums[i]));
            } else {
                result.add(nums[i] + "->" + nums[j]);
            }
            i = j + 1;
        }

        return result;
    }

    /**
     * ==================== 解法二：逐个遍历 ====================
     *
     * 【核心思路】
     * 记录当前区间的起始值 start，逐个扫描数组。
     * 当发现当前元素与下一个元素不连续时（或到达末尾），
     * 就用 start 和当前元素生成一个区间字符串。
     *
     * 【思考过程】
     * 1. 换一种视角：不用双指针显式地框定区间，
     *    而是用一个变量 start 记住"当前区间从哪开始"。
     *
     * 2. 遍历每个元素，判断"是否该结束当前区间"：
     *    - 条件：i == n-1（最后一个元素）或 nums[i+1] != nums[i]+1（不连续）
     *    - 满足时生成区间，并将 start 更新为 nums[i+1]
     *
     * 3. 本质和解法一完全一样，只是代码组织方式不同。
     *    解法一是外层控制区间起点、内层扩展终点；
     *    本解法是单层循环、在断裂点处理。
     *
     * 【举例】nums = [0,2,3,4,6,8,9]
     *   start=0
     *   i=0: nums[0]=0, nums[1]=2 != 0+1 → 区间结束, "0", start=2
     *   i=1: nums[1]=2, nums[2]=3 == 2+1 → 继续
     *   i=2: nums[2]=3, nums[3]=4 == 3+1 → 继续
     *   i=3: nums[3]=4, nums[4]=6 != 4+1 → 区间结束, "2->4", start=6
     *   i=4: nums[4]=6, nums[5]=8 != 6+1 → 区间结束, "6", start=8
     *   i=5: nums[5]=8, nums[6]=9 == 8+1 → 继续
     *   i=6: 末尾 → 区间结束, "8->9"
     *   结果：["0","2->4","6","8->9"]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public List<String> summaryRangesLinear(int[] nums) {
        List<String> result = new ArrayList<>();
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            int start = nums[i];
            while (i + 1 < n && nums[i + 1] == nums[i] + 1) {
                i++;
            }
            if (start == nums[i]) {
                result.add(String.valueOf(start));
            } else {
                result.add(start + "->" + nums[i]);
            }
        }

        return result;
    }
}
