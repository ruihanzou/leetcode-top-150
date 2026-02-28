/**
 * LeetCode 153. Find Minimum in Rotated Sorted Array
 * 难度: Medium
 *
 * 题目描述：
 * 已知一个长度为 n 的数组，预先按照升序排列，经由 1 到 n 次旋转后，得到输入数组。
 * 例如，原数组 nums = [0,1,2,4,5,6,7] 在变化后可能得到：
 * - 若旋转 4 次，则可以得到 [4,5,6,7,0,1,2]
 * - 若旋转 7 次，则可以得到 [0,1,2,4,5,6,7]
 *
 * 注意，数组 [a[0], a[1], ..., a[n-1]] 旋转一次的结果为 [a[n-1], a[0], a[1], ..., a[n-2]]。
 * 给你一个元素值互不相同的数组 nums，返回其中的最小元素。
 * 你必须设计一个时间复杂度为 O(log n) 的算法。
 *
 * 示例：nums = [3,4,5,1,2] → 输出 1
 *
 * 【拓展练习】
 * 1. LeetCode 154. Find Minimum in Rotated Sorted Array II —— 含重复元素的旋转数组找最小
 * 2. LeetCode 33. Search in Rotated Sorted Array —— 旋转数组中搜索目标值
 * 3. LeetCode 81. Search in Rotated Sorted Array II —— 含重复元素的旋转数组搜索
 */

class FindMinRotated153 {

    /**
     * ==================== 解法一：二分查找 ====================
     *
     * 【核心思路】
     * 旋转数组可以看成两个升序段拼接：[大段..., 小段...]。
     * 最小值是右段的第一个元素。利用 nums[mid] 与 nums[right] 的比较，
     * 判断 mid 在左段还是右段，从而缩小搜索范围。
     *
     * 【思考过程】
     * 1. 如果数组没旋转（或旋转了n次回到原样），nums[0] 就是最小值，
     *    此时 nums[left] < nums[right]。
     *
     * 2. 如果发生了旋转，数组分为两段：
     *    左段 [left..pivot] 全部 > 右段 [pivot+1..right]。
     *    最小值在右段的起始位置。
     *
     * 3. 比较 nums[mid] 与 nums[right]：
     *    - nums[mid] > nums[right]：mid 在左段，最小值在 mid 右边
     *      → left = mid + 1
     *    - nums[mid] < nums[right]：mid 在右段（或右段就是整个），
     *      最小值在 mid 或 mid 左边 → right = mid
     *    - 因为元素互不相同，不会出现 nums[mid] == nums[right]
     *
     * 4. 为什么和 right 比而不和 left 比？
     *    和 left 比时，nums[mid] > nums[left] 无法判断最小值方向，
     *    因为无论 mid 在左段还是数组未旋转，都满足这个条件。
     *    而和 right 比则能唯一确定 mid 所在的段。
     *
     * 【举例】nums = [3,4,5,1,2]
     *   left=0, right=4
     *   mid=2: nums[2]=5 > nums[4]=2 → left=3
     *   mid=3: nums[3]=1 < nums[4]=2 → right=3
     *   left==right=3，返回 nums[3]=1
     *
     * 【举例】nums = [4,5,6,7,0,1,2]
     *   left=0, right=6
     *   mid=3: nums[3]=7 > nums[6]=2 → left=4
     *   mid=5: nums[5]=1 < nums[6]=2 → right=5
     *   mid=4: nums[4]=0 < nums[5]=1 → right=4
     *   left==right=4，返回 nums[4]=0
     *
     * 【时间复杂度】O(log n)
     * 【空间复杂度】O(1)
     */
    public int findMinBinarySearch(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return nums[left];
    }

    /**
     * ==================== 解法二：线性扫描 ====================
     *
     * 【核心思路】
     * 遍历数组，找到第一个比前一个元素小的位置，该位置就是最小值。
     * 如果遍历完都没找到，说明数组没有旋转，第一个元素就是最小值。
     *
     * 【思考过程】
     * 1. 旋转数组的特征：存在一个"断崖"，即 nums[i] < nums[i-1] 的位置。
     *    这个位置就是原数组的起始位置，也就是最小值。
     *
     * 2. 如果整个数组都是递增的（没有断崖），说明没旋转或旋转了n次，
     *    最小值就是 nums[0]。
     *
     * 3. 这种方法简单直观，但没有利用有序性质，无法满足 O(log n) 要求。
     *
     * 【举例】nums = [3,4,5,1,2]
     *   i=1: 4 > 3 ✓ 继续
     *   i=2: 5 > 4 ✓ 继续
     *   i=3: 1 < 5 ✗ 找到断崖！返回 nums[3]=1
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int findMinLinear(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < nums[i - 1]) {
                return nums[i];
            }
        }
        return nums[0];
    }
}
