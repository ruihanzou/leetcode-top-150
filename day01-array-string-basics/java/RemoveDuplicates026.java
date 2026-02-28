/**
 * LeetCode 26. Remove Duplicates from Sorted Array
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个非严格递增排列的整数数组 nums，原地删除重复出现的元素，使每个元素只出现一次，
 * 返回删除后数组的新长度 k。nums 的前 k 个元素应该是去重后的结果，
 * 其余元素和数组大小不重要。不能使用额外数组空间。
 *
 * 示例：nums = [1,1,2] → 输出 2, nums = [1,2,_]
 *
 * 【拓展练习】
 * 1. LeetCode 80. Remove Duplicates from Sorted Array II —— 允许最多出现两次
 * 2. LeetCode 27. Remove Element —— 移除指定值，类似的双指针技巧
 * 3. LeetCode 83. Remove Duplicates from Sorted List —— 链表版本的去重
 */

class RemoveDuplicates026 {

    /**
     * ==================== 解法一：快慢双指针 ====================
     *
     * 【核心思路】
     * 慢指针 slow 指向已去重部分的最后一个元素，快指针 fast 扫描数组。
     * 当 nums[fast] != nums[slow] 时，说明遇到了新元素，放到 slow+1 位置。
     *
     * 【思考过程】
     * 1. 数组已排序，所以相同的元素一定相邻。只需要比较当前元素与上一个保留元素。
     *
     * 2. slow 始终指向"已确认保留的最后一个元素"。
     *    fast 每次前进一步，如果 nums[fast] 和 nums[slow] 不同，
     *    就是一个新的不重复元素，放到 slow+1 位置。
     *
     * 3. 因为数组有序，nums[fast] >= nums[slow]。
     *    如果相等就跳过（重复），不等就保留。
     *
     * 【举例】nums = [0,0,1,1,1,2,2,3,3,4]
     *   初始: slow=0
     *   fast=1: nums[1]=0 == nums[0]=0, 跳过
     *   fast=2: nums[2]=1 != nums[0]=0, slow=1, nums[1]=1
     *   fast=3: nums[3]=1 == nums[1]=1, 跳过
     *   fast=4: nums[4]=1 == nums[1]=1, 跳过
     *   fast=5: nums[5]=2 != nums[1]=1, slow=2, nums[2]=2
     *   fast=6: nums[6]=2 == nums[2]=2, 跳过
     *   fast=7: nums[7]=3 != nums[2]=2, slow=3, nums[3]=3
     *   fast=8: nums[8]=3 == nums[3]=3, 跳过
     *   fast=9: nums[9]=4 != nums[3]=3, slow=4, nums[4]=4
     *   返回 slow+1=5, nums=[0,1,2,3,4,...]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int removeDuplicatesTwoPointer(int[] nums) {
        if (nums.length == 0) return 0;

        int slow = 0;
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
        }
        return slow + 1;
    }

    /**
     * ==================== 解法二：计数法 ====================
     *
     * 【核心思路】
     * 维护一个写入位置 pos，遍历数组时通过比较相邻元素判断是否为新值，
     * 是新值就写入 pos 位置并前进。本质与解法一等价，但从"计数不同值"的角度思考。
     *
     * 【思考过程】
     * 1. 另一个角度：我们不看"跳过重复"，而是"统计有几种不同的值"。
     *
     * 2. 遍历数组，每当 nums[i] != nums[i-1]（即出现了新值），
     *    就把这个值记录到结果中，相当于给它编号 pos。
     *
     * 3. pos 从 1 开始（第一个元素 nums[0] 一定保留），
     *    每遇到一个新值就 nums[pos] = nums[i], pos++。
     *    最终 pos 就是不同元素的个数。
     *
     * 【举例】nums = [1,1,2]
     *   pos=1（nums[0]=1 已保留）
     *   i=1: nums[1]=1 == nums[0]=1, 不是新值
     *   i=2: nums[2]=2 != nums[1]=1, 是新值, nums[1]=2, pos=2
     *   返回 pos=2, nums=[1,2,_]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int removeDuplicatesCount(int[] nums) {
        if (nums.length == 0) return 0;

        int pos = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                nums[pos++] = nums[i];
            }
        }
        return pos;
    }
}
