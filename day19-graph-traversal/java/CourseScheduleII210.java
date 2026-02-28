/**
 * LeetCode 210. Course Schedule II
 * 难度: Medium
 *
 * 题目描述：
 * 现在你总共有 numCourses 门课需要选，记为 0 到 numCourses - 1。
 * 给你一个数组 prerequisites，其中 prerequisites[i] = [ai, bi]，表示在选修课程 ai 前
 * 必须先选修 bi。
 * 请你返回你为了学完所有课程所安排的学习顺序。如果不可能完成所有课程，返回一个空数组。
 *
 * 示例：numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
 *   → 输出 [0,2,1,3] 或 [0,1,2,3]
 *
 * 【拓展练习】
 * 1. LeetCode 207. Course Schedule —— 仅判断是否存在拓扑排序
 * 2. LeetCode 269. Alien Dictionary —— 从字典序推断字母顺序（拓扑排序）
 * 3. LeetCode 1462. Course Schedule IV —— 判断课程间的前置关系（传递闭包）
 */

import java.util.*;

class CourseScheduleII210 {

    /**
     * ==================== 解法一：BFS 拓扑排序（Kahn 算法） ====================
     *
     * 【核心思路】
     * 与 207 题相同的 Kahn 算法，额外记录出队顺序作为拓扑排序结果。
     *
     * 【思考过程】
     * 1. 入度为 0 → 没有前置依赖 → 可以学习 → 入队并记录。
     * 2. 学完后减少后续课程的入度，新的入度为 0 的继续入队。
     * 3. 最终 order 长度 < numCourses → 有环 → 返回空数组。
     *
     * 【举例】numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]]
     *   入度：0:0, 1:1, 2:1, 3:2
     *   BFS出队顺序：0→1→2→3（或 0→2→1→3）
     *
     * 【时间复杂度】O(V + E)
     * 【空间复杂度】O(V + E)
     */
    public int[] findOrderBfs(int numCourses, int[][] prerequisites) {
        int[] inDegree = new int[numCourses];
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] pre : prerequisites) {
            graph.get(pre[1]).add(pre[0]);
            inDegree[pre[0]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int[] order = new int[numCourses];
        int idx = 0;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            order[idx++] = node;
            for (int neighbor : graph.get(node)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return idx == numCourses ? order : new int[0];
    }

    /**
     * ==================== 解法二：DFS 后序 + 反转 ====================
     *
     * 【核心思路】
     * 对有向图做 DFS，在节点的所有邻居都处理完后将该节点加入结果列表（后序）。
     * 最终将结果反转即为拓扑排序。用三色标记法检测环。
     *
     * 【思考过程】
     * 1. DFS 后序：一个节点在其所有后继之后被记录。
     *    反转后 → 每个节点在其后继之前 → 拓扑排序。
     *
     * 2. 为什么后序反转是拓扑序？
     *    节点 u 的邻居 v 先完成后序记录 → 反转后 u 在 v 前面 → 满足 u→v 的依赖。
     *
     * 3. 三色标记检测环：遇到灰色 → 有环 → 返回空数组。
     *
     * 【时间复杂度】O(V + E)
     * 【空间复杂度】O(V + E)
     */
    public int[] findOrderDfs(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] pre : prerequisites) {
            graph.get(pre[1]).add(pre[0]);
        }

        int[] color = new int[numCourses];
        List<Integer> postOrder = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            if (color[i] == 0) {
                if (!dfs210(graph, color, i, postOrder)) {
                    return new int[0];
                }
            }
        }

        int[] result = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            result[i] = postOrder.get(numCourses - 1 - i);
        }
        return result;
    }

    private boolean dfs210(List<List<Integer>> graph, int[] color, int node,
                           List<Integer> postOrder) {
        color[node] = 1;
        for (int neighbor : graph.get(node)) {
            if (color[neighbor] == 1) return false;
            if (color[neighbor] == 0 && !dfs210(graph, color, neighbor, postOrder)) return false;
        }
        color[node] = 2;
        postOrder.add(node);
        return true;
    }
}
