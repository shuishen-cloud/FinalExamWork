#include <iostream>
#include <vector>
#include <map>
#include <string>
#include <fstream>
#include <algorithm>
#include <queue>
#include <numeric>

using namespace std;

// 课程结构体
struct Course {
    string id;         // 课程编号
    string name;       // 课程名称
    int credit;        // 学分
    vector<string> pre; // 先修课程列表
    int earliestTerm;  // 最早可安排学期（由先修课决定）
    int assignedTerm;  // 实际安排的学期（-1表示未安排）
};

// 全局数据
vector<Course> courses;               // 所有课程
map<string, int> courseIndexMap;      // 课程编号→数组索引
int totalTerms;                       // 总学期数
int termCreditLimit;                  // 每学期学分上限
vector<int> termCredits;              // 各学期已分配学分
vector<vector<string>> termCourses;   // 各学期的课程列表


// 初始化数据
void init() {
    termCredits.resize(totalTerms, 0);
    termCourses.resize(totalTerms);
    for (auto& c : courses) {
        c.assignedTerm = -1;
        // 计算最早可安排学期：先修课的最大安排学期+1（若无先修则为1）
        int maxPreTerm = 0;
        for (const string& preId : c.pre) {
            auto it = courseIndexMap.find(preId);
            if (it == courseIndexMap.end()) {
                cerr << "错误：先修课程" << preId << "不存在！" << endl;
                exit(1);
            }
            int preIdx = it->second;
            maxPreTerm = max(maxPreTerm, courses[preIdx].earliestTerm);
        }
        c.earliestTerm = maxPreTerm + 1;
        // 若最早学期超过总学期数，直接判定无解
        if (c.earliestTerm > totalTerms) {
            cerr << "错误：课程" << c.id << "最早学期" << c.earliestTerm << "超过总学期数" << totalTerms << endl;
            exit(1);
        }
    }
}


// 策略1：各学期学分尽量均匀
bool arrangeUniform() {
    // 按最早可安排学期排序，先安排先修课多的课程
    vector<int> courseIndices(courses.size());
    iota(courseIndices.begin(), courseIndices.end(), 0);
    sort(courseIndices.begin(), courseIndices.end(), [&](int a, int b) {
        if (courses[a].earliestTerm != courses[b].earliestTerm) {
            return courses[a].earliestTerm < courses[b].earliestTerm;
        }
        return courses[a].pre.size() > courses[b].pre.size();
    });

    for (int idx : courseIndices) {
        Course& c = courses[idx];
        bool assigned = false;
        // 从最早学期开始尝试安排
        for (int t = c.earliestTerm - 1; t < totalTerms; ++t) {
            if (termCredits[t] + c.credit <= termCreditLimit) {
                termCredits[t] += c.credit;
                termCourses[t].push_back(c.id);
                c.assignedTerm = t + 1; // 学期从1开始
                assigned = true;
                break;
            }
        }
        if (!assigned) {
            return false; // 无法安排该课程
        }
    }
    return true;
}


// 策略2：课程尽量集中在前面的学期
bool arrangeConcentrated() {
    // 按最早可安排学期排序，先安排学分大的课程
    vector<int> courseIndices(courses.size());
    iota(courseIndices.begin(), courseIndices.end(), 0);
    sort(courseIndices.begin(), courseIndices.end(), [&](int a, int b) {
        if (courses[a].earliestTerm != courses[b].earliestTerm) {
            return courses[a].earliestTerm < courses[b].earliestTerm;
        }
        return courses[a].credit > courses[b].credit;
    });

    for (int idx : courseIndices) {
        Course& c = courses[idx];
        bool assigned = false;
        // 从最早学期开始，优先填满前面的学期
        for (int t = c.earliestTerm - 1; t < totalTerms; ++t) {
            if (termCredits[t] + c.credit <= termCreditLimit) {
                termCredits[t] += c.credit;
                termCourses[t].push_back(c.id);
                c.assignedTerm = t + 1;
                assigned = true;
                break;
            }
        }
        if (!assigned) {
            return false;
        }
    }
    return true;
}


// 输出教学计划到文件
void outputPlan(const string& filename) {
    ofstream fout(filename);
    if (!fout.is_open()) {
        cerr << "无法打开输出文件！" << endl;
        return;
    }

    fout << "===== 教学计划 =====" << endl;
    fout << "总学期数：" << totalTerms << " | 每学期学分上限：" << termCreditLimit << endl << endl;
    for (int t = 0; t < totalTerms; ++t) {
        fout << "第" << (t + 1) << "学期（学分：" << termCredits[t] << "）：" << endl;
        for (const string& courseId : termCourses[t]) {
            int idx = courseIndexMap[courseId];
            fout << "  " << courseId << " - " << courses[idx].name << "（" << courses[idx].credit << "学分）" << endl;
        }
        fout << endl;
    }
    fout.close();
    cout << "教学计划已输出到" << filename << endl;
}


int main() {
    // 初始化测试数据（可根据需求修改）
    totalTerms = 6;
    termCreditLimit = 10;
    vector<int> credits = {2,3,4,3,2,3,4,4,2,5,3,2};
    vector<string> courseIds = {"C1","C2","C3","C4","C5","C6","C7","C8","C9","C10","C11","C12"};
    vector<string> courseNames = {
        "程序设计基础", "高等数学", "数据结构", "汇编语言", "语言的设计和分析",
        "计算机原理", "编译原理", "操作系统", "高等数学", "线性代数", "普通物理", "数值分析"
    };
    vector<vector<string>> preCourses = {
        {}, {"C1"}, {"C1","C2"}, {"C1"}, {"C3","C4"},
        {"C11"}, {"C11","C3"}, {"C3","C6"}, {}, {"C9"}, {"C9"}, {"C9","C10","C1"}
    };

    // 填充课程数据
    for (int i = 0; i < courseIds.size(); ++i) {
        Course c;
        c.id = courseIds[i];
        c.name = courseNames[i];
        c.credit = credits[i];
        c.pre = preCourses[i];
        courses.push_back(c);
        courseIndexMap[courseIds[i]] = i;
    }

    // 初始化课程最早学期
    init();

    // 选择排课策略
    int strategy;
    cout << "请选择排课策略：1-学分均匀 2-课程集中" << endl;
    cin >> strategy;

    bool success = false;
    if (strategy == 1) {
        success = arrangeUniform();
    } else if (strategy == 2) {
        success = arrangeConcentrated();
    } else {
        cerr << "无效策略！" << endl;
        return 1;
    }

    if (!success) {
        cout << "无法满足条件，排课失败！" << endl;
        return 1;
    }

    // 输出结果
    outputPlan("教学计划.txt");

    return 0;
}
