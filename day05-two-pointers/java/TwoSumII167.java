/**
 * LeetCode 167. Two Sum II - Input Array Is Sorted
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个已按照非递减顺序排列的整数数组 numbers，请你从数组中找出两个数
 * 满足相加之和等于目标数 target。
 * 函数应该以长度为 2 的整数数组的形式返回这两个数的下标值（下标从 1 开始计数）。
 * 你可以假设每个输入只对应唯一的答案，而且你不可以重复使用相同的元素。
 * 你所设计的解决方案必须只使用常量级的额外空间。
 *
 * 示例 1：numbers = [2,7,11,15], target = 9 → 输出 [1,2]
 *   解释：2 与 7 之和等于目标数 9。因此 index1 = 1, index2 = 2，返回 [1,2]。
 * 示例 2：numbers = [2,3,4], target = 6 → 输出 [1,3]
 * 示例 3：numbers = [-1,0], target = -1 → 输出 [1,2]
 *
 * 【拓展练习】
 * 1. LeetCode 1. Two Sum —— 无序数组版本，用哈希表
 * 2. LeetCode 15. 3Sum —— 三数之和，固定一个数后转化为双指针
 * 3. LeetCode 653. Two Sum IV - Input is a BST —— 在二叉搜索树中找两数之和
 */

import java.util.HashMap;
import java.util.Map;

class TwoSumII167 {

    /**
     * ==================== 解法一：对撞双指针 ====================
     *
     * 【核心思路】
     * 利用数组有序的特性，设置左指针指向最小值、右指针指向最大值。
     * 如果两数之和等于 target，找到答案；
     * 如果和太小，左指针右移（增大和）；如果和太大，右指针左移（减小和）。
     *
     * 【思考过程】
     * 1. 数组有序 → 最小值在左，最大值在右。
     * 2. 从两端开始，和 = numbers[left] + numbers[right]：
     *    - 和 == target → 找到答案
     *    - 和 < target → 需要更大的数，左指针右移
     *    - 和 > target → 需要更小的数，右指针左移
     * 3. 因为每一步都缩小搜索范围，且答案唯一存在，一定能找到。
     * 4. 这是利用有序性的最优解法。
     *
     * 【举例】numbers = [2,7,11,15], target = 9
     *   left=0, right=3: 2+15=17 > 9 → right=2
     *   left=0, right=2: 2+11=13 > 9 → right=1
     *   left=0, right=1: 2+7=9 == 9 → 返回 [1,2]
     *
     * 【时间复杂度】O(n)，每个元素最多访问一次
     * 【空间复杂度】O(1)，仅用两个指针
     */
    public int[] twoSumTwoPointer(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[]{left + 1, right + 1};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * ==================== 解法二：二分查找 ====================
     *
     * 【核心思路】
     * 固定一个数 numbers[i]，在 i+1 到 n-1 的范围内二分查找 target - numbers[i]。
     *
     * 【思考过程】
     * 1. 数组有序 → 自然想到二分查找。
     * 2. 枚举第一个数 numbers[i]，需要的第二个数就是 target - numbers[i]。
     * 3. 由于数组有序，可以在剩余部分用二分查找，每次 O(log n)。
     * 4. 总共枚举 n 次，每次二分 O(log n)，整体 O(n log n)。
     * 5. 虽然不如双指针快，但展示了二分查找在有序数组上的应用。
     *
     * 【举例】numbers = [2,7,11,15], target = 9
     *   i=0: 找 9-2=7，在 [7,11,15] 中二分 → 找到 index=1
     *   返回 [1,2]
     *
     * 【时间复杂度】O(n log n)，外层 O(n)，内层二分 O(log n)
     * 【空间复杂度】O(1)，不需要额外空间
     */
    public int[] twoSumBinarySearch(int[] numbers, int target) {
        for (int i = 0; i < numbers.length - 1; i++) {
            int complement = target - numbers[i];
            int lo = i + 1, hi = numbers.length - 1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (numbers[mid] == complement) {
                    return new int[]{i + 1, mid + 1};
                } else if (numbers[mid] < complement) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * ==================== 解法三：哈希表 ====================
     *
     * 【核心思路】
     * 遍历数组，对于每个数检查哈希表中是否存在 target - numbers[i]，
     * 如果存在则找到答案；否则将当前数及其下标存入哈希表。
     *
     * 【思考过程】
     * 1. 这是 Two Sum 的通用解法，不依赖数组的有序性。
     * 2. 用哈希表存储已遍历的数 → O(1) 查找互补数。
     * 3. 虽然没有利用有序性，但也能正确求解，而且时间复杂度 O(n)。
     * 4. 缺点是需要额外 O(n) 空间，题目要求常量空间时不是最优选择。
     *
     * 【举例】numbers = [2,7,11,15], target = 9
     *   i=0: 找 9-2=7，哈希表为空 → 存入 {2:0}
     *   i=1: 找 9-7=2，哈希表有 2→0 → 返回 [1,2]
     *
     * 【时间复杂度】O(n)，遍历一次数组
     * 【空间复杂度】O(n)，哈希表存储
     */
    public int[] twoSumHashMap(int[] numbers, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < numbers.length; i++) {
            int complement = target - numbers[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement) + 1, i + 1};
            }
            map.put(numbers[i], i);
        }
        return new int[]{-1, -1};
    }
}
