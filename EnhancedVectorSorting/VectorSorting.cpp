//============================================================================
// Name        : VectorSorting.cpp
// Author      : Nash Ellis
// Version     : 2.0
// Copyright   : Copyright © 2023 SNHU COCE
// Description : Vector Sorting Algorithms (Selection, Quick, Merge, std::sort)
//============================================================================

#include <algorithm>
#include <iostream>
#include <chrono>
#include <string>
#include <vector>
#include <limits>
#include "CSVparser.hpp"

using namespace std;

//============================================================================
// Structure Definition
//============================================================================

struct Bid {
    string bidId;
    string title;
    string fund;
    double amount;

    Bid() : amount(0.0) {}
};

//============================================================================
// Helper Functions
//============================================================================

void displayBid(const Bid& bid) {
    cout << bid.bidId << ": " << bid.title << " | " << bid.amount << " | " << bid.fund << endl;
}

double strToDouble(string str, char ch) {
    str.erase(remove(str.begin(), str.end(), ch), str.end());
    return atof(str.c_str());
}

Bid getBid() {
    Bid bid;
    cin.ignore(numeric_limits<streamsize>::max(), '\n');

    cout << "Enter Id: ";
    getline(cin, bid.bidId);
    if (bid.bidId.empty()) bid.bidId = "UNKNOWN";

    cout << "Enter title: ";
    getline(cin, bid.title);
    if (bid.title.empty()) bid.title = "Untitled";

    cout << "Enter fund: ";
    getline(cin, bid.fund);
    if (bid.fund.empty()) bid.fund = "Unspecified";

    cout << "Enter amount: $";
    string strAmount;
    getline(cin, strAmount);
    bid.amount = strToDouble(strAmount, '$');
    if (bid.amount < 0) bid.amount = 0.0;

    return bid;
}

vector<Bid> loadBids(const string& csvPath) {
    cout << "Loading CSV file: " << csvPath << endl;
    vector<Bid> bids;

    try {
        csv::Parser file(csvPath);
        for (int i = 0; i < file.rowCount(); ++i) {
            Bid bid;
            bid.bidId = file[i][1];
            bid.title = file[i][0];
            bid.fund = file[i][8];
            bid.amount = strToDouble(file[i][4], '$');
            bids.push_back(bid);
        }
    }
    catch (const csv::Error& e) {
        cerr << "Error reading CSV: " << e.what() << endl;
    }

    cout << bids.size() << " bids loaded successfully." << endl;
    return bids;
}

//============================================================================
// Sorting Algorithms
//============================================================================

/**
 * Selection Sort algorithm.
 * Time Complexity: O(n^2)
 */
void selectionSort(vector<Bid>& bids) {
    for (unsigned int pos = 0; pos < bids.size(); ++pos) {
        unsigned int minIndex = pos;

        for (unsigned int j = pos + 1; j < bids.size(); ++j) {
            if (bids[j].title < bids[minIndex].title) {
                minIndex = j;
            }
        }

        if (minIndex != pos) {
            swap(bids[pos], bids[minIndex]);
        }
    }
}

/**
 * Partition function for Quick Sort.
 */
int partition(vector<Bid>& bids, int begin, int end) {
    int low = begin;
    int high = end;
    string pivot = bids[(begin + end) / 2].title;
    bool done = false;

    while (!done) {
        while (bids[low].title < pivot) {
            ++low;
        }
        while (bids[high].title > pivot) {
            --high;
        }

        if (low >= high) {
            done = true;
        }
        else {
            swap(bids[low], bids[high]);
            ++low;
            --high;
        }
    }

    return high;
}

/**
 * Recursive Quick Sort algorithm.
 * Time Complexity: O(n log n) average, O(n^2) worst case
 */
void quickSort(vector<Bid>& bids, int begin, int end) {
    if (begin >= end) return;

    int part = partition(bids, begin, end);
    quickSort(bids, begin, part);
    quickSort(bids, part + 1, end);
}

/**
 * Merge function used in Merge Sort.
 */
void merge(vector<Bid>& bids, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;
    vector<Bid> leftHalf(n1);
    vector<Bid> rightHalf(n2);

    for (int i = 0; i < n1; ++i) leftHalf[i] = bids[left + i];
    for (int j = 0; j < n2; ++j) rightHalf[j] = bids[mid + 1 + j];

    int i = 0, j = 0, k = left;
    while (i < n1 && j < n2) {
        if (leftHalf[i].title <= rightHalf[j].title) {
            bids[k] = leftHalf[i++];
        }
        else {
            bids[k] = rightHalf[j++];
        }
        ++k;
    }

    while (i < n1) bids[k++] = leftHalf[i++];
    while (j < n2) bids[k++] = rightHalf[j++];
}

/**
 * Recursive Merge Sort algorithm.
 * Time Complexity: O(n log n)
 */
void mergeSort(vector<Bid>& bids, int left, int right) {
    if (left >= right) return;
    int mid = left + (right - left) / 2;
    mergeSort(bids, left, mid);
    mergeSort(bids, mid + 1, right);
    merge(bids, left, mid, right);
}

/**
 * Sort using C++ Standard (std::sort)
 * Time Complexity: O(n log n)
 */
void standardSort(vector<Bid>& bids) {
    sort(bids.begin(), bids.end(), [](const Bid& a, const Bid& b) {
        return a.title < b.title;
        });
}

//============================================================================
// Main Application
//============================================================================

int main(int argc, char* argv[]) {
    string csvPath = (argc == 2) ? argv[1] : "eBid_Monthly_Sales.csv";
    vector<Bid> bids;
    int choice = 0;

    while (choice != 9) {
        cout << "\nMenu:\n";
        cout << "  1. Load Bids\n";
        cout << "  2. Display All Bids\n";
        cout << "  3. Selection Sort\n";
        cout << "  4. Quick Sort\n";
        cout << "  5. Merge Sort\n";
        cout << "  6. Standard Sort (std::sort)\n";
        cout << "  9. Exit\n";
        cout << "Enter choice: ";

        if (!(cin >> choice)) {
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "Invalid input. Please enter a number.\n";
            continue;
        }

        switch (choice) {
        case 1: {
            auto start = chrono::high_resolution_clock::now();
            bids = loadBids(csvPath);
            auto end = chrono::high_resolution_clock::now();
            chrono::duration<double> elapsed = end - start;
            cout << "Loaded in " << elapsed.count() << " seconds.\n";
            break;
        }

        case 2:
            if (bids.empty()) {
                cout << "No bids loaded.\n";
                break;
            }
            for (const auto& bid : bids) displayBid(bid);
            break;

        case 3:
            if (bids.empty()) {
                cout << "No bids loaded.\n";
                break;
            }
            {
                auto start = chrono::high_resolution_clock::now();
                selectionSort(bids);
                auto end = chrono::high_resolution_clock::now();
                chrono::duration<double> elapsed = end - start;
                cout << "Selection Sort completed in " << elapsed.count() << " seconds.\n";
            }
            break;

        case 4:
            if (bids.empty()) {
                cout << "No bids loaded.\n";
                break;
            }
            {
                auto start = chrono::high_resolution_clock::now();
                quickSort(bids, 0, bids.size() - 1);
                auto end = chrono::high_resolution_clock::now();
                chrono::duration<double> elapsed = end - start;
                cout << "Quick Sort completed in " << elapsed.count() << " seconds.\n";
            }
            break;

        case 5:
            if (bids.empty()) {
                cout << "No bids loaded.\n";
                break;
            }
            {
                auto start = chrono::high_resolution_clock::now();
                mergeSort(bids, 0, bids.size() - 1);
                auto end = chrono::high_resolution_clock::now();
                chrono::duration<double> elapsed = end - start;
                cout << "Merge Sort completed in " << elapsed.count() << " seconds.\n";
            }
            break;

        case 6:
            if (bids.empty()) {
                cout << "No bids loaded.\n";
                break;
            }
            {
                auto start = chrono::high_resolution_clock::now();
                standardSort(bids);
                auto end = chrono::high_resolution_clock::now();
                chrono::duration<double> elapsed = end - start;
                cout << "std::sort completed in " << elapsed.count() << " seconds.\n";
            }
            break;

        case 9:
            cout << "Good bye.\n";
            break;

        default:
            cout << "Invalid choice. Try again.\n";
            break;
        }
    }

    return 0;
}
