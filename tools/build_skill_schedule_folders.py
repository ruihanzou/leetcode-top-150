from __future__ import annotations

"""
根据 PLAN_BY_SKILL.md 中的「30 天刷题表」，按天构建新的题目目录：

- skill-schedule/day01/python/*.py
- skill-schedule/day01/java/*.java
- ...

不会移动或删除原有文件，只是拷贝一份，方便按新 30 天计划查看和练习。
"""

from pathlib import Path
import shutil

from check_schedule import parse_skill_schedule

ROOT = Path(__file__).resolve().parents[1]
PLAN = ROOT / "PLAN_BY_SKILL.md"


def find_python_files(pid: int) -> list[Path]:
    """在 dayXX-* 目录下查找某题号的 Python 解法文件。"""
    pattern = f"day*/**/{pid:03d}_*.py"
    return list(ROOT.glob(pattern))


def find_java_files(pid: int) -> list[Path]:
    """在 dayXX-* 目录下查找某题号的 Java 解法文件。"""
    pattern = f"day*/**/*{pid:03d}.java"
    return list(ROOT.glob(pattern))


def main() -> None:
    schedule = parse_skill_schedule()

    skill_root = ROOT / "skill-schedule"
    skill_root.mkdir(exist_ok=True)

    for day in range(1, 31):
        problems = schedule.get(day, [])
        day_dir = skill_root / f"day{day:02d}"
        py_dir = day_dir / "python"
        java_dir = day_dir / "java"
        py_dir.mkdir(parents=True, exist_ok=True)
        java_dir.mkdir(parents=True, exist_ok=True)

        for pid in problems:
            # Python files
            py_files = find_python_files(pid)
            for src in py_files:
                dst = py_dir / src.name
                if not dst.exists():
                    shutil.copy2(src, dst)

            # Java files
            java_files = find_java_files(pid)
            for src in java_files:
                dst = java_dir / src.name
                if not dst.exists():
                    shutil.copy2(src, dst)


if __name__ == "__main__":
    main()

