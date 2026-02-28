/**
 * LeetCode 46. Permutations
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个不含重复数字的数组 nums，返回其所有可能的全排列。你可以按任意顺序返回答案。
 *
 * 示例：
 * 输入: nums = [1,2,3]
 * 输出: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 *
 * 【拓展练习】
 * 1. LeetCode 47. Permutations II —— 含重复数字的全排列，需排序+去重
 * 2. LeetCode 31. Next Permutation —— 求下一个排列，字典序思路
 * 3. LeetCode 60. Permutation Sequence —— 求第 k 个排列，数学+阶乘
 */

import java.util.*;

class Permutations046 {

    /**
     * ==================== 解法一：回溯 + visited 标记 ====================
     *
     * 【核心思路】
     * 每个位置可以放任何一个还没用过的数字。
     * 用 visited 数组标记哪些数字已经被选过，
     * 依次为每个位置选择一个未用过的数字，递归到下一个位置。
     *
     * 【思考过程】
     * 1. 排列 vs 组合的区别：排列考虑顺序，[1,2] 和 [2,1] 是不同的排列。
     *    → 不能像组合那样用 start 限制起点，而是每次都从头开始选，
     *      但跳过已经选过的数字。
     *
     * 2. 回溯框架：
     *    - 状态：当前路径 path，已使用标记 visited。
     *    - 选择列表：所有 visited[i] == false 的 nums[i]。
     *    - 终止条件：path.size() == nums.length。
     *
     * 3. 回溯时需要"撤销选择"：path 移除末尾元素，visited[i] 标回 false。
     *
     * 【举例】nums = [1,2,3]
     *   选1(visited[0]=T) → 选2(visited[1]=T) → 选3 → [1,2,3] ✓
     *                                             撤销3
     *                        撤销2
     *                        选3(visited[2]=T) → 选2 → [1,3,2] ✓
     *   撤销1
     *   选2(visited[1]=T) → 选1(visited[0]=T) → 选3 → [2,1,3] ✓
     *                        撤销1
     *                        选3(visited[2]=T) → 选1 → [2,3,1] ✓
     *   撤销2
     *   选3(visited[2]=T) → 选1 → 选2 → [3,1,2] ✓
     *                        选2 → 选1 → [3,2,1] ✓
     *
     * 【时间复杂度】O(n * n!)  共 n! 个排列，每个排列复制需 O(n)
     * 【空间复杂度】O(n)  递归深度 + visited 数组
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] visited = new boolean[nums.length];
        backtrack(nums, visited, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] visited, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) continue;
            visited[i] = true;
            path.add(nums[i]);
            backtrack(nums, visited, path, result);
            path.remove(path.size() - 1);
            visited[i] = false;
        }
    }

    /**
     * ==================== 解法二：回溯 + 交换法（原地交换产生排列） ====================
     *
     * 【核心思路】
     * 不使用额外的 visited 数组，而是通过交换数组元素来"原地"产生排列。
     * 对于位置 index，将 nums[index] 分别与 nums[index..n-1] 中的每个元素交换，
     * 使得位置 index 上依次出现不同的数字，递归处理 index+1。
     *
     * 【思考过程】
     * 1. 传统的 visited 方法需要额外空间。能否不用 visited？
     *    → 核心观察：数组中 [0, index-1] 是已确定的前缀，
     *      [index, n-1] 是还没确定的"候选池"。
     *
     * 2. 要为位置 index 选一个数，只需从候选池中选一个放到 index 位置。
     *    选择方式就是"交换 nums[index] 和 nums[j]"，其中 j ∈ [index, n-1]。
     *
     * 3. 递归处理 index+1 后，再交换回来（回溯），恢复数组状态。
     *
     * 4. 注意：这种方法产生的排列顺序和 visited 方法不同，但结果集相同。
     *
     * 【举例】nums = [1,2,3], index=0
     *   swap(0,0) → [1,2,3], index=1:
     *     swap(1,1) → [1,2,3], index=2: 收集 [1,2,3]
     *     swap(1,2) → [1,3,2], index=2: 收集 [1,3,2]
     *     swap(1,2) 换回 → [1,2,3]
     *   swap(0,1) → [2,1,3], index=1:
     *     swap(1,1) → [2,1,3] → 收集 [2,1,3]
     *     swap(1,2) → [2,3,1] → 收集 [2,3,1]
     *   swap(0,1) 换回 → [1,2,3]
     *   swap(0,2) → [3,2,1], index=1:
     *     swap(1,1) → [3,2,1] → 收集 [3,2,1]
     *     swap(1,2) → [3,1,2] → 收集 [3,1,2]
     *   swap(0,2) 换回 → [1,2,3]
     *
     * 【时间复杂度】O(n * n!)
     * 【空间复杂度】O(n) 递归栈深度，不需要 visited 数组
     */
    public List<List<Integer>> permuteSwap(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSwap(nums, 0, result);
        return result;
    }

    private void backtrackSwap(int[] nums, int index, List<List<Integer>> result) {
        if (index == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int num : nums) perm.add(num);
            result.add(perm);
            return;
        }
        for (int i = index; i < nums.length; i++) {
            swap(nums, index, i);
            backtrackSwap(nums, index + 1, result);
            swap(nums, index, i);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
