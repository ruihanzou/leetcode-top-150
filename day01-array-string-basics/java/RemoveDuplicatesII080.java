/**
 * LeetCode 80. Remove Duplicates from Sorted Array II
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个非严格递增排列的整数数组 nums，原地删除重复出现的元素，
 * 使得每个元素最多出现两次，返回删除后数组的新长度 k。
 * nums 的前 k 个元素应该是处理后的结果，其余元素和数组大小不重要。
 * 不能使用额外数组空间，必须原地修改。
 *
 * 示例：nums = [1,1,1,2,2,3] → 输出 5, nums = [1,1,2,2,3,_]
 *
 * 【拓展练习】
 * 1. LeetCode 26. Remove Duplicates from Sorted Array —— 每个元素最多出现一次（k=1 的特例）
 * 2. LeetCode 27. Remove Element —— 移除指定值
 * 3. LeetCode 82. Remove Duplicates from Sorted List II —— 链表版本，删除所有重复节点
 */

class RemoveDuplicatesII080 {

    /**
     * ==================== 解法一：通用解法 - 与前第k个比较 ====================
     *
     * 【核心思路】
     * 维护慢指针 slow 表示下一个写入位置。对于每个新元素 nums[fast]，
     * 只要它 != nums[slow - 2]，就可以写入（保证同一值最多出现 2 次）。
     * 这个方法通用性极强，改 2 为 k 即可处理"最多 k 次"的问题。
     *
     * 【思考过程】
     * 1. 数组有序，重复元素相邻。如果一个值已经在结果中出现了 2 次，
     *    那么 nums[slow-1] 和 nums[slow-2] 都等于这个值。
     *
     * 2. 判断 nums[fast] 能否放到 slow 位置：
     *    - 如果 nums[fast] != nums[slow-2]，说明即使放进去也不会超过 2 次。
     *    - 如果 nums[fast] == nums[slow-2]，说明已经有 2 个相同值了，跳过。
     *
     * 3. 前 2 个元素无条件保留（不可能超过 2 次），所以 slow 从 2 开始。
     *
     * 4. 推广：如果最多允许 k 个重复，把 2 换成 k 即可。
     *
     * 【举例】nums = [1,1,1,2,2,3]
     *   slow=2（前两个元素 [1,1] 直接保留）
     *   fast=2: nums[2]=1, nums[slow-2]=nums[0]=1, 1==1, 跳过
     *   fast=3: nums[3]=2, nums[slow-2]=nums[0]=1, 2!=1, nums[2]=2, slow=3
     *   fast=4: nums[4]=2, nums[slow-2]=nums[1]=1, 2!=1, nums[3]=2, slow=4
     *   fast=5: nums[5]=3, nums[slow-2]=nums[2]=2, 3!=2, nums[4]=3, slow=5
     *   返回 5, nums=[1,1,2,2,3,_]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int removeDuplicatesGeneral(int[] nums) {
        if (nums.length <= 2) return nums.length;

        int slow = 2;
        for (int fast = 2; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow - 2]) {
                nums[slow++] = nums[fast];
            }
        }
        return slow;
    }

    /**
     * ==================== 解法二：计数法 ====================
     *
     * 【核心思路】
     * 显式维护当前元素的出现次数 count。遍历数组时，遇到相同元素 count++，
     * 遇到不同元素 count 重置为 1。只有 count <= 2 时才写入。
     *
     * 【思考过程】
     * 1. 更直观的思路：既然限制"最多 2 次"，那就直接数每个元素出现了几次。
     *
     * 2. 数组有序，相同元素连续。用一个变量 count 记录当前值已经出现的次数。
     *    - 如果 nums[fast] == nums[fast-1]，count++
     *    - 否则 count = 1（新值出现）
     *
     * 3. 只有 count <= 2 时才把 nums[fast] 写入 slow 位置。
     *
     * 4. 相比解法一，这种写法更直观易懂，但通用性稍差
     *    （解法一改一个常量就能处理 k 次的问题）。
     *
     * 【举例】nums = [0,0,1,1,1,1,2,3,3]
     *   slow=1, count=1
     *   fast=1: 0==0, count=2, 2<=2, nums[1]=0, slow=2
     *   fast=2: 1!=0, count=1, 1<=2, nums[2]=1, slow=3
     *   fast=3: 1==1, count=2, 2<=2, nums[3]=1, slow=4
     *   fast=4: 1==1, count=3, 3>2, 跳过
     *   fast=5: 1==1, count=4, 4>2, 跳过
     *   fast=6: 2!=1, count=1, 1<=2, nums[4]=2, slow=5
     *   fast=7: 3!=2, count=1, 1<=2, nums[5]=3, slow=6
     *   fast=8: 3==3, count=2, 2<=2, nums[6]=3, slow=7
     *   返回 7, nums=[0,0,1,1,2,3,3,...]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public int removeDuplicatesCount(int[] nums) {
        if (nums.length <= 2) return nums.length;

        int slow = 1;
        int count = 1;
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] == nums[fast - 1]) {
                count++;
            } else {
                count = 1;
            }
            if (count <= 2) {
                nums[slow++] = nums[fast];
            }
        }
        return slow;
    }
}
