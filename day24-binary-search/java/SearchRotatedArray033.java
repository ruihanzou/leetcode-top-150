/**
 * LeetCode 33. Search in Rotated Sorted Array
 * 难度: Medium
 *
 * 题目描述：
 * 整数数组 nums 按升序排列，数组中的值互不相同。
 * 在传递给函数之前，nums 在预先未知的某个下标 k 上进行了旋转，
 * 使数组变为 [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]。
 * 给你旋转后的数组 nums 和一个整数 target，
 * 如果 nums 中存在这个目标值，返回它的下标，否则返回 -1。
 * 必须设计一个时间复杂度为 O(log n) 的算法。
 *
 * 示例：nums = [4,5,6,7,0,1,2], target = 0 → 输出 4
 *       nums = [4,5,6,7,0,1,2], target = 3 → 输出 -1
 *
 * 【拓展练习】
 * 1. LeetCode 81. Search in Rotated Sorted Array II —— 含重复元素的旋转数组搜索
 * 2. LeetCode 153. Find Minimum in Rotated Sorted Array —— 找旋转数组的最小值
 * 3. LeetCode 154. Find Minimum in Rotated Sorted Array II —— 含重复元素找最小值
 */

class SearchRotatedArray033 {

    /**
     * ==================== 解法一：改进二分（判断有序半边） ====================
     *
     * 【核心思路】
     * 旋转数组对半分后，至少有一半是有序的。
     * 每次二分时先判断哪半边有序，再判断 target 是否落在有序半边的范围内，
     * 从而决定往哪边收缩。
     *
     * 【思考过程】
     * 1. 旋转数组 = 两段有序数组拼接。对半分时：
     *    - 如果 nums[lo] <= nums[mid]，左半边 [lo, mid] 有序
     *    - 否则右半边 [mid, hi] 有序
     *
     * 2. 确定有序半边后，判断 target 是否在有序半边的值域内：
     *    - 如果在，就在有序半边里继续二分
     *    - 如果不在，就去另一半找
     *
     * 3. 这样每步丢掉一半，保证 O(log n)。
     *
     * 【举例】nums = [4,5,6,7,0,1,2], target = 0
     *   lo=0, hi=6
     *   mid=3: nums[0]=4 <= nums[3]=7 → 左半边 [4,5,6,7] 有序
     *          target=0 不在 [4,7] 范围内 → lo=4
     *   mid=5: nums[4]=0 <= nums[5]=1 → 左半边 [0,1] 有序
     *          target=0 在 [0,1] 范围内 → hi=5
     *   mid=4: nums[4]=0 == target → 返回 4
     *
     * 【时间复杂度】O(log n)
     * 【空间复杂度】O(1)
     */
    public int search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                return mid;
            }

            if (nums[lo] <= nums[mid]) {
                if (nums[lo] <= target && target < nums[mid]) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                if (nums[mid] < target && target <= nums[hi]) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }

        return -1;
    }

    /**
     * ==================== 解法二：先找旋转点再二分 ====================
     *
     * 【核心思路】
     * 分两步：
     * 第一步：用二分查找找到旋转点（最小值的位置 pivot）。
     * 第二步：根据 target 和 nums[0] 的大小关系，确定在哪段有序数组中二分。
     *
     * 【思考过程】
     * 1. 如果能找到旋转点 pivot，数组就被分成两段有序数组：
     *    - [0, pivot-1]：值域 [nums[0], nums[pivot-1]]
     *    - [pivot, n-1]：值域 [nums[pivot], nums[n-1]]
     *
     * 2. 找旋转点：用二分查找，比较 nums[mid] 和 nums[hi]：
     *    - 如果 nums[mid] > nums[hi]，最小值在 [mid+1, hi]
     *    - 否则最小值在 [lo, mid]
     *    最终 lo == hi 时就是旋转点。
     *
     * 3. 确定 target 在哪段：
     *    - 如果 target >= nums[0]，target 在左段 [0, pivot-1]
     *    - 否则 target 在右段 [pivot, n-1]
     *    特殊情况：如果 pivot == 0，说明数组没有旋转，整体有序。
     *
     * 4. 在对应段内做标准二分查找。
     *
     * 【举例】nums = [4,5,6,7,0,1,2], target = 0
     *   第一步：找旋转点
     *     lo=0, hi=6
     *     mid=3: nums[3]=7 > nums[6]=2 → lo=4
     *     mid=5: nums[5]=1 <= nums[6]=2 → hi=5
     *     mid=4: nums[4]=0 <= nums[5]=1 → hi=4
     *     lo=hi=4 → pivot=4
     *
     *   第二步：target=0 < nums[0]=4 → 在右段 [4,6] 二分
     *     lo=4, hi=6
     *     mid=5: nums[5]=1 > 0 → hi=4
     *     mid=4: nums[4]=0 == 0 → 返回 4
     *
     * 【时间复杂度】O(log n) 两次二分
     * 【空间复杂度】O(1)
     */
    public int searchTwoPass(int[] nums, int target) {
        int n = nums.length;

        int lo = 0, hi = n - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        int pivot = lo;

        if (pivot == 0) {
            lo = 0;
            hi = n - 1;
        } else if (target >= nums[0]) {
            lo = 0;
            hi = pivot - 1;
        } else {
            lo = pivot;
            hi = n - 1;
        }

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return -1;
    }
}
