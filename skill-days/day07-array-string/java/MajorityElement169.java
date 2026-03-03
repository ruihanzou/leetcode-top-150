/**
 * LeetCode 169. Majority Element
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个大小为 n 的数组 nums，返回其中的多数元素。
 * 多数元素是指在数组中出现次数大于 ⌊n/2⌋ 的元素。
 * 题目保证数组中一定存在多数元素。
 *
 * 示例：nums = [2,2,1,1,1,2,2] → 输出 2
 *
 * 【拓展练习】
 * 1. LeetCode 229. Majority Element II —— 找出现次数超过 n/3 的所有元素（最多2个）
 * 2. LeetCode 1150. Check If a Number Is Majority Element in a Sorted Array —— 有序数组中判断多数元素
 * 3. LeetCode 347. Top K Frequent Elements —— 找出现频率前 K 高的元素
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class MajorityElement169 {

    /**
     * ==================== 解法一：排序法 ====================
     *
     * 【核心思路】
     * 排序后，多数元素（出现次数 > n/2）一定覆盖数组中间位置 n/2。
     * 所以 nums[n/2] 就是答案。
     *
     * 【思考过程】
     * 1. 多数元素出现次数超过一半，无论它的值是最小、最大还是中间，
     *    排序后它一定"跨过"数组的中点。
     *
     * 2. 极端情况验证：
     *    - 多数元素是最小值：它占据 [0, >n/2)，中点 n/2 被覆盖 ✓
     *    - 多数元素是最大值：它占据 (<n/2, n)，中点 n/2 被覆盖 ✓
     *    - 多数元素在中间：更不用说 ✓
     *
     * 3. 因此排序后直接返回 nums[n/2]。
     *
     * 【举例】nums = [2,2,1,1,1,2,2]
     *   排序 → [1,1,1,2,2,2,2]
     *   n=7, n/2=3, nums[3]=2 → 答案是 2
     *
     * 【时间复杂度】O(n log n)
     * 【空间复杂度】O(1) 原地排序（不计排序栈空间）
     */
    public int majorityElementSort(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length / 2];
    }

    /**
     * ==================== 解法二：哈希计数 ====================
     *
     * 【核心思路】
     * 用哈希表统计每个元素的出现次数，找到次数超过 n/2 的那个。
     *
     * 【思考过程】
     * 1. 最直观的想法：数每个元素出现了几次，谁超过一半就是谁。
     *
     * 2. 遍历数组，用 HashMap 记录 <元素, 次数>。
     *    每次更新计数后检查是否已超过 n/2，如果是可以提前返回。
     *
     * 3. 优点是直观且可以提前终止；缺点是需要 O(n) 额外空间。
     *
     * 【举例】nums = [2,2,1,1,1,2,2]
     *   遍历：
     *   2→{2:1}, 2→{2:2}, 1→{1:1}, 1→{1:2},
     *   1→{1:3}, 2→{2:3}, 2→{2:4}
     *   n/2=3, 当 {2:4} 时 4>3 → 返回 2
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public int majorityElementHash(int[] nums) {
        Map<Integer, Integer> counts = new HashMap<>();
        int threshold = nums.length / 2;

        for (int num : nums) {
            int count = counts.merge(num, 1, Integer::sum);
            if (count > threshold) {
                return num;
            }
        }

        return -1;
    }

    /**
     * ==================== 解法三：Boyer-Moore 投票算法 ====================
     *
     * 【核心思路】
     * 维护一个候选者 candidate 和计数器 count。遍历数组时，
     * count==0 则更换候选者，遇到相同的 count++，不同的 count--。
     * 最终 candidate 就是多数元素。
     *
     * 【思考过程】
     * 1. 能否做到 O(n) 时间 O(1) 空间？不用排序也不用哈希表？
     *
     * 2. Boyer-Moore 投票的直觉："多数派 vs 少数派"的对抗。
     *    多数元素出现次数超过一半，如果让多数和少数"一一抵消"，
     *    最后剩下的一定是多数元素。
     *
     * 3. 算法过程：
     *    - 维护 candidate 和 count
     *    - count == 0 时，选当前元素为新候选者，count = 1
     *    - 当前元素 == candidate，count++（支持票）
     *    - 当前元素 != candidate，count--（反对票）
     *
     * 4. 正确性证明：多数元素的"净票数"为 出现次数 - 其他所有元素出现次数 > 0，
     *    所以它不可能被完全抵消掉，最终一定是 candidate。
     *
     * 【举例】nums = [2,2,1,1,1,2,2]
     *   初始: candidate=?, count=0
     *   2: count==0, candidate=2, count=1
     *   2: ==candidate, count=2
     *   1: !=candidate, count=1
     *   1: !=candidate, count=0
     *   1: count==0, candidate=1, count=1
     *   2: !=candidate, count=0
     *   2: count==0, candidate=2, count=1
     *   最终 candidate=2 → 答案
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int majorityElementVote(int[] nums) {
        int candidate = 0;
        int count = 0;

        for (int num : nums) {
            if (count == 0) {
                candidate = num;
            }
            count += (num == candidate) ? 1 : -1;
        }

        return candidate;
    }
}
