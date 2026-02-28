/**
 * LeetCode 1. Two Sum
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target
 * 的那两个整数，并返回它们的数组下标。
 * 你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。
 * 你可以按任意顺序返回答案。
 *
 * 示例：
 *   输入: nums = [2,7,11,15], target = 9
 *   输出: [0,1]
 *   解释: nums[0] + nums[1] = 2 + 7 = 9
 *
 *   输入: nums = [3,2,4], target = 6
 *   输出: [1,2]
 *
 *   输入: nums = [3,3], target = 6
 *   输出: [0,1]
 *
 * 【拓展练习】
 * 1. LeetCode 15. 3Sum —— 三数之和，排序+双指针
 * 2. LeetCode 167. Two Sum II - Input Array Is Sorted —— 有序数组，双指针 O(1) 空间
 * 3. LeetCode 560. Subarray Sum Equals K —— 前缀和+哈希表
 */

import java.util.HashMap;
import java.util.Map;

class TwoSum001 {

    /**
     * ==================== 解法一：暴力双循环 ====================
     *
     * 【核心思路】
     * 枚举所有不同下标的二元组 (i, j)，检查 nums[i] + nums[j] == target。
     *
     * 【思考过程】
     * 1. 最直接的想法：既然要找两个数的和等于 target，那就把所有可能的两两组合试一遍。
     * 2. 外层循环 i 从 0 到 n-2，内层循环 j 从 i+1 到 n-1，保证不重复使用同一元素。
     * 3. 一旦找到就直接返回，题目保证只有一个答案。
     *
     * 【举例】nums = [2,7,11,15], target = 9
     *   i=0, j=1: nums[0]+nums[1] = 2+7 = 9 == target ✓ → 返回 [0,1]
     *
     * 【时间复杂度】O(n²)
     * 【空间复杂度】O(1)
     */
    public int[] twoSumBrute(int[] nums, int target) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};
    }

    /**
     * ==================== 解法二：哈希表两遍 ====================
     *
     * 【核心思路】
     * 第一遍：把所有元素的 值→下标 存入哈希表。
     * 第二遍：对每个元素查找 target - nums[i] 是否在哈希表中（且不是自己）。
     *
     * 【思考过程】
     * 1. 暴力法的内层循环本质是"查找 target - nums[i] 是否存在"。
     *    如果预先建好哈希表，查找就从 O(n) 降到 O(1)。
     * 2. 注意同一元素不能使用两次，所以查到的下标不能等于 i。
     * 3. 如果有重复值（如 [3,3], target=6），哈希表会存后一个下标，
     *    但第一遍遍历时 i 指向前一个，查到的是后一个，不会冲突。
     *
     * 【举例】nums = [2,7,11,15], target = 9
     *   建表: {2:0, 7:1, 11:2, 15:3}
     *   i=0: complement = 9-2 = 7, 7 在表中且下标1≠0 → 返回 [0,1]
     *
     * 【时间复杂度】O(n) 两次遍历
     * 【空间复杂度】O(n) 哈希表
     */
    public int[] twoSumTwoPass(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement) && map.get(complement) != i) {
                return new int[]{i, map.get(complement)};
            }
        }
        return new int[]{};
    }

    /**
     * ==================== 解法三：哈希表一遍 ====================
     *
     * 【核心思路】
     * 边遍历边查找：遍历到 nums[i] 时，检查 target - nums[i] 是否已经在哈希表中，
     * 如果在就直接返回；如果不在就把 nums[i] 加入哈希表。
     *
     * 【思考过程】
     * 1. 两遍哈希表可以合并为一遍：当我们遍历到下标 i 时，哈希表中已经存了
     *    下标 0..i-1 的元素。如果 complement 在表中，说明之前某个元素和当前元素
     *    配对成功。
     * 2. 不需要担心"使用两次同一元素"的问题：查找时当前元素还没入表。
     * 3. 也不需要担心遗漏：如果 (i, j) 是答案且 i < j，那么遍历到 j 时，
     *    nums[i] 已经在表中，一定能查到。
     *
     * 【举例】nums = [2,7,11,15], target = 9
     *   i=0: complement=7, map={} 中无7 → 加入 {2:0}
     *   i=1: complement=2, map={2:0} 中有2 → 返回 [0,1]
     *
     * 【时间复杂度】O(n) 一次遍历
     * 【空间复杂度】O(n) 哈希表
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }
}
