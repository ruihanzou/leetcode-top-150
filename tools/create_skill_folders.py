"""Create skill-grouped day folders and copy solutions into them."""

from pathlib import Path
import shutil

ROOT = Path(__file__).resolve().parents[1]

SCHEDULE = [
    ("day01-two-pointers",          [88, 27, 26, 125, 392]),
    ("day02-two-pointers",          [80, 167, 11, 15, 42]),
    ("day03-sliding-window",        [219, 209, 3, 76]),
    ("day04-sliding-window-greedy", [30, 121, 122, 55]),
    ("day05-greedy-and-prefix",     [45, 189, 238, 134, 274]),
    ("day06-greedy-array-string",   [135, 58, 14, 28, 13]),
    ("day07-array-string",          [169, 151, 6, 12, 68]),
    ("day08-hashmap-and-set",       [383, 242, 205, 290, 1]),
    ("day09-hashmap-and-set",       [202, 49, 128, 380, 149]),
    ("day10-intervals-matrix",      [228, 56, 57, 452, 36]),
    ("day11-matrix-stack",          [54, 48, 73, 289, 20]),
    ("day12-stack",                 [71, 155, 150, 224, 141]),
    ("day13-linked-list",           [21, 2, 138, 92, 19]),
    ("day14-linked-list",           [82, 61, 86, 146, 25]),
    ("day15-binary-tree",           [104, 100, 226, 101, 112]),
    ("day16-binary-tree",           [222, 637, 105, 106, 117]),
    ("day17-binary-tree",           [114, 129, 236, 199, 102]),
    ("day18-binary-tree-bst",       [103, 124, 530, 108]),
    ("day19-bst-graph",             [230, 98, 173, 200, 130]),
    ("day20-graph",                 [133, 399, 207, 210, 909]),
    ("day21-graph-trie",            [433, 208, 211, 127]),
    ("day22-trie-backtracking",     [212, 17, 77, 46]),
    ("day23-backtracking",          [39, 22, 79, 52]),
    ("day24-divide-conquer-kadane", [427, 148, 53, 918, 23]),
    ("day25-binary-search",         [35, 69, 74, 162, 33]),
    ("day26-binary-search-heap",    [34, 153, 4, 215]),
    ("day27-heap-bit-manipulation", [373, 502, 67, 190]),
    ("day28-bit-manipulation",      [191, 136, 137, 201, 295]),
    ("day29-math-dp-1d",            [9, 66, 172, 50, 70]),
    ("day30-dp-1d",                 [198, 139, 322, 300, 123]),
    ("day31-dp-2d",                 [120, 64, 63, 5, 97]),
    ("day32-dp-2d",                 [72, 221, 188]),
]

SKILL_ROOT = ROOT / "skill-days"


def find_py(pid: int) -> list[Path]:
    return list(ROOT.glob(f"day[0-9]*/**/python/{pid:03d}_*.py"))


def find_java(pid: int) -> list[Path]:
    return list(ROOT.glob(f"day[0-9]*/**/java/*{pid:03d}.java"))


def main() -> None:
    SKILL_ROOT.mkdir(exist_ok=True)

    for folder_name, pids in SCHEDULE:
        day_dir = SKILL_ROOT / folder_name
        py_dir = day_dir / "python"
        java_dir = day_dir / "java"
        py_dir.mkdir(parents=True, exist_ok=True)
        java_dir.mkdir(parents=True, exist_ok=True)

        for pid in pids:
            for src in find_py(pid):
                dst = py_dir / src.name
                if not dst.exists():
                    shutil.copy2(src, dst)

            for src in find_java(pid):
                dst = java_dir / src.name
                if not dst.exists():
                    shutil.copy2(src, dst)

        py_count = len(list(py_dir.iterdir()))
        java_count = len(list(java_dir.iterdir()))
        print(f"{folder_name}: {len(pids)} problems, {py_count} py, {java_count} java")


if __name__ == "__main__":
    main()
