/**
 * LeetCode 207. Course Schedule
 * 难度: Medium
 *
 * 题目描述：
 * 你这个学期必须选修 numCourses 门课程，记为 0 到 numCourses - 1。
 * 在选修某些课程之前需要一些先修课程。先修课程按数组 prerequisites 给出，
 * 其中 prerequisites[i] = [ai, bi]，表示如果要学习课程 ai，则必须先学习课程 bi。
 * 请你判断是否可能完成所有课程的学习。
 *
 * 示例 1：numCourses = 2, prerequisites = [[1,0]] → 输出 true
 * 示例 2：numCourses = 2, prerequisites = [[1,0],[0,1]] → 输出 false
 *
 * 【拓展练习】
 * 1. LeetCode 210. Course Schedule II —— 返回拓扑排序的具体顺序
 * 2. LeetCode 630. Course Schedule III —— 贪心+优先队列，选最多课程
 * 3. LeetCode 802. Find Eventual Safe States —— 找到所有最终安全的节点
 */

import java.util.*;

class CourseSchedule207 {

    /**
     * ==================== 解法一：BFS 拓扑排序（Kahn 算法） ====================
     *
     * 【核心思路】
     * 能完成所有课程 ⟺ 课程依赖图中没有环 ⟺ 存在拓扑排序。
     * Kahn 算法：不断将入度为 0 的节点入队并删除其出边，如果最终所有节点都被处理，
     * 说明无环。
     *
     * 【思考过程】
     * 1. "先修课程"形成有向图，能否完成 = 无环 = 存在拓扑排序。
     *
     * 2. Kahn 算法直觉：
     *    - 入度为 0 → 没有前置依赖 → 可以直接学习
     *    - 学完后删掉相关边 → 可能产生新的入度为 0 的节点
     *    - 重复直到队列为空，检查是否所有课程都处理了
     *
     * 【举例】numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]]
     *   入度：0:0, 1:1, 2:1, 3:2
     *   BFS：0→{1,2入度减1}→1→{3入度减1}→2→{3入度减1}→3
     *   处理了4个 == numCourses → true
     *
     * 【时间复杂度】O(V + E)
     * 【空间复杂度】O(V + E)
     */
    public boolean canFinishBfs(int numCourses, int[][] prerequisites) {
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

        int count = 0;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            count++;
            for (int neighbor : graph.get(node)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return count == numCourses;
    }

    /**
     * ==================== 解法二：DFS 检测环（三色标记法） ====================
     *
     * 【核心思路】
     * 用 DFS 遍历有向图，给每个节点标记三种状态：
     * - 白色（0）：未访问
     * - 灰色（1）：当前 DFS 路径上
     * - 黑色（2）：已完成处理
     * 遇到灰色节点说明存在环。
     *
     * 【思考过程】
     * 1. 为什么需要三种颜色而不是两种？
     *    仅用 visited 无法区分"在当前 DFS 栈上"和"已完全处理"。
     *    A→C, B→C 的情况下，DFS(B) 遇到已处理的 C 不是环。
     *    只有遇到灰色（当前栈上）才是真正的回边/环。
     *
     * 2. 流程：进入标灰 → 遍历邻居 → 全部完成标黑
     *    遇灰色邻居 → 有环
     *
     * 【时间复杂度】O(V + E)
     * 【空间复杂度】O(V + E)
     */
    public boolean canFinishDfs(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] pre : prerequisites) {
            graph.get(pre[1]).add(pre[0]);
        }

        int[] color = new int[numCourses]; // 0=white, 1=gray, 2=black

        for (int i = 0; i < numCourses; i++) {
            if (color[i] == 0) {
                if (!dfs207(graph, color, i)) return false;
            }
        }
        return true;
    }

    private boolean dfs207(List<List<Integer>> graph, int[] color, int node) {
        color[node] = 1;
        for (int neighbor : graph.get(node)) {
            if (color[neighbor] == 1) return false;
            if (color[neighbor] == 0 && !dfs207(graph, color, neighbor)) return false;
        }
        color[node] = 2;
        return true;
    }
}
