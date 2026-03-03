/**
 * LeetCode 399. Evaluate Division
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个变量对数组 equations 和一个实数值数组 values，其中 equations[i] = [Ai, Bi]
 * 和 values[i] 表示等式 Ai / Bi = values[i]。每个 Ai 或 Bi 是一个表示单个变量的字符串。
 * 另有一个查询数组 queries，其中 queries[j] = [Cj, Dj] 表示第 j 个查询，
 * 你需要找出 Cj / Dj = ? 的答案。
 * 如果存在某个无法确定的答案，则用 -1.0 替代。
 *
 * 示例：
 *   equations = [["a","b"],["b","c"]], values = [2.0,3.0]
 *   queries = [["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]
 *   → 输出 [6.0,0.5,-1.0,1.0,-1.0]
 *
 * 【拓展练习】
 * 1. LeetCode 990. Satisfiability of Equality Equations —— 等式可满足性，并查集经典应用
 * 2. LeetCode 1976. Number of Ways to Arrive at Destination —— 带权图最短路径计数
 * 3. LeetCode 743. Network Delay Time —— 加权有向图最短路径
 */

import java.util.*;

class EvaluateDivision399 {

    /**
     * ==================== 解法一：建图 + BFS ====================
     *
     * 【核心思路】
     * 将等式关系建模为带权有向图：a/b=k 意味着 a→b 权重为 k，b→a 权重为 1/k。
     * 对于查询 c/d，从 c 出发 BFS 找到 d，沿途权重相乘即为结果。
     *
     * 【思考过程】
     * 1. a/b=2 可以理解为"a 是 b 的 2 倍"。如果我们画一条 a→b 的边权重 2，
     *    那么从 a 走到 b 就是除以 2（即 a/b=2）；反向 b→a 权重 1/2（即 b/a=0.5）。
     *
     * 2. 对于 a/c，如果有路径 a→b→c，那么 a/c = (a/b) * (b/c) = 边权之积。
     *    → 问题转化为在带权有向图中找路径，并计算路径上权重的乘积。
     *
     * 3. 用 BFS 从起点出发搜索终点：
     *    - 维护到当前节点的累积乘积
     *    - 如果找到终点，累积乘积就是答案
     *    - 如果 BFS 结束也没找到终点，说明不连通，返回 -1.0
     *
     * 【举例】equations=[["a","b"],["b","c"]], values=[2.0,3.0]
     *   建图：a→b:2, b→a:0.5, b→c:3, c→b:1/3
     *   查询 a/c：BFS a→b(×2)→c(×3)=6.0
     *
     * 【时间复杂度】O(Q * (V + E))，Q 为查询数，每次 BFS 遍历图
     * 【空间复杂度】O(V + E)
     */
    public double[] calcEquationBfs(List<List<String>> equations, double[] values,
                                    List<List<String>> queries) {
        Map<String, List<double[]>> graphWeight = new HashMap<>();
        Map<String, List<String>> graphNode = new HashMap<>();

        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double val = values[i];

            graphNode.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            graphNode.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
            graphWeight.computeIfAbsent(a, k -> new ArrayList<>()).add(new double[]{val});
            graphWeight.computeIfAbsent(b, k -> new ArrayList<>()).add(new double[]{1.0 / val});
        }

        double[] results = new double[queries.size()];
        for (int q = 0; q < queries.size(); q++) {
            String src = queries.get(q).get(0);
            String dst = queries.get(q).get(1);
            results[q] = bfs(src, dst, graphNode, graphWeight);
        }
        return results;
    }

    private double bfs(String src, String dst,
                       Map<String, List<String>> graphNode,
                       Map<String, List<double[]>> graphWeight) {
        if (!graphNode.containsKey(src) || !graphNode.containsKey(dst)) return -1.0;
        if (src.equals(dst)) return 1.0;

        Set<String> visited = new HashSet<>();
        Queue<Object[]> queue = new LinkedList<>();
        queue.offer(new Object[]{src, 1.0});
        visited.add(src);

        while (!queue.isEmpty()) {
            Object[] curr = queue.poll();
            String node = (String) curr[0];
            double product = (double) curr[1];

            List<String> neighbors = graphNode.get(node);
            List<double[]> weights = graphWeight.get(node);

            for (int i = 0; i < neighbors.size(); i++) {
                String neighbor = neighbors.get(i);
                double weight = weights.get(i)[0];
                if (neighbor.equals(dst)) return product * weight;
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(new Object[]{neighbor, product * weight});
                }
            }
        }
        return -1.0;
    }

    /**
     * ==================== 解法二：Floyd-Warshall ====================
     *
     * 【核心思路】
     * 使用 Floyd-Warshall 算法预处理所有变量对之间的除法结果。
     * dist[i][j] 表示 i/j 的值。松弛操作：如果知道 i/k 和 k/j，
     * 则 i/j = (i/k) * (k/j)。
     *
     * 【思考过程】
     * 1. Floyd-Warshall 通常用于求所有点对最短路径，这里可以类比：
     *    - "距离"变成"除法比值"
     *    - "取 min"变成"能否通过中间节点算出比值"
     *    - 路径上的"加法"变成"乘法"
     *
     * 2. 初始化：
     *    - dist[a][a] = 1.0（自己除自己为1）
     *    - 如果给定 a/b = k，则 dist[a][b] = k, dist[b][a] = 1/k
     *
     * 3. 三重循环松弛，查询时直接查表 O(1)。
     *
     * 【举例】变量 {a, b, c}
     *   k=b: dist[a][c] = dist[a][b]*dist[b][c] = 2*3 = 6
     *
     * 【时间复杂度】O(V³ + Q)
     * 【空间复杂度】O(V²)
     */
    public double[] calcEquationFloyd(List<List<String>> equations, double[] values,
                                      List<List<String>> queries) {
        Map<String, Integer> varIndex = new HashMap<>();
        int idx = 0;
        for (List<String> eq : equations) {
            for (String v : eq) {
                if (!varIndex.containsKey(v)) {
                    varIndex.put(v, idx++);
                }
            }
        }

        int n = varIndex.size();
        double[][] dist = new double[n][n];
        for (double[] row : dist) Arrays.fill(row, -1.0);
        for (int i = 0; i < n; i++) dist[i][i] = 1.0;

        for (int i = 0; i < equations.size(); i++) {
            int a = varIndex.get(equations.get(i).get(0));
            int b = varIndex.get(equations.get(i).get(1));
            dist[a][b] = values[i];
            dist[b][a] = 1.0 / values[i];
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] > 0 && dist[k][j] > 0 && dist[i][j] < 0) {
                        dist[i][j] = dist[i][k] * dist[k][j];
                    }
                }
            }
        }

        double[] results = new double[queries.size()];
        for (int q = 0; q < queries.size(); q++) {
            String c = queries.get(q).get(0);
            String d = queries.get(q).get(1);
            if (!varIndex.containsKey(c) || !varIndex.containsKey(d)) {
                results[q] = -1.0;
            } else {
                results[q] = dist[varIndex.get(c)][varIndex.get(d)];
            }
        }
        return results;
    }
}
