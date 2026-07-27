# GradePulse AI - Comprehensive Features Walkthrough

Welcome to the complete technical walkthrough for the advanced features added to the **GradePulse AI - School Exam Correction & Evaluation Platform**.

---

## 🏛️ System Architecture & Technology Stack

* **Framework:** Spring Boot 3.3.2 (Java 17 / Java 26 compatibility)
* **Database:** SQLite JDBC with Spring Data JPA
* **PDF & Image Processing:** Apache PDFBox 2.0.30, Java AWT / ImageIO
* **AI Evaluation Engines:** Google Gemini 1.5 Flash (Multimodal REST API) + Local Rule Engine Fallback
* **Frontend:** Vanilla HTML5, CSS3 Glassmorphic UI Tokens, JavaScript (ES6+), FontAwesome 6, Chart.js

---

## 🚀 Detailed Features Guide

### ✏️ Feature 1: Teacher Human Override & Gradebook Export System

#### 1. Overview
Allows educators to review AI-assigned scores, manually override rubric criteria marks, add teacher notes, export class gradebooks in CSV format, and generate official printable student report cards.

#### 2. Backend Implementation
* **Model (`EvaluationResult.java`):**
  * Added `isTeacherOverridden` (`Boolean`) — flags if marks were adjusted manually.
  * Added `teacherNotes` (`String`, `length = 2000`) — stores examiner's comments or override justification.
* **DTO (`EvaluationDtos.java`):**
  * `OverrideEvaluationRequest`: encapsulates `submissionId`, `rubricBreakdown`, `totalMarksObtained`, and `teacherNotes`.
* **Service (`LlmEvaluationService.java`):**
  * `overrideEvaluation(OverrideEvaluationRequest request)`: Recalculates total score, percentage, letter grade (`A+`, `A`, `B`, `C`, `D`, `F`), pass status (`PASSED`/`FAILED`), and saves the override to SQLite.
  * `exportCsvGradebook(Long examId)`: Generates downloadable CSV grade sheet (`Submission ID`, `Roll Number`, `Student Name`, `Exam Title`, `Marks Obtained`, `Max Marks`, `Percentage`, `Grade`, `Pass Status`, `Teacher Overridden`, `Notes`).
  * `generateReportCardHtml(Long submissionId)`: Generates styled, printable HTML grade report card with one-click print capabilities.
* **Controller (`EvaluationController.java`):**
  * `POST /api/evaluations/{submissionId}/override`
  * `GET /api/evaluations/export/csv/{examId}`
  * `GET /api/evaluations/export/report-card/{submissionId}`

#### 3. Frontend UI (`index.html` & `app.js`)
* Added **Edit / Override Marks** modal allowing direct score adjustments per rubric criterion.
* Added **Download Exam Gradebook (CSV)** and **Printable Report Card** action buttons on scorecards.

---

### 👁️ Feature 2: Multimodal Vision LLM Grading Engine

#### 1. Overview
Directly transmits original student answer sheet images (Base64 encoded) to the Multimodal Gemini Vision API alongside OCR text, enabling AI evaluation of handwritten text, math equations, diagrams, and visual layout.

#### 2. Backend Implementation
* **Payload Structure (`LlmEvaluationService.callGeminiLiveApi`):**
  * Reads student answer file from `uploads/` directory.
  * Encodes bytes to Base64 string and detects MIME type (`image/jpeg`, `image/png`, `image/webp`, `application/pdf`).
  * Constructs multimodal request payload:
    ```json
    {
      "contents": [
        {
          "parts": [
            { "text": "... detailed grading prompt & rubrics ..." },
            {
              "inlineData": {
                "mimeType": "image/png",
                "data": "<base64_encoded_string>"
              }
            }
          ]
        }
      ]
    }
    ```
* Automatic fallback to built-in Intelligent Rule Engine if API quota or network connection fails.

---

### ✂️ Feature 3: Bulk Multi-Page PDF Splitter & Auto-Roll Detector

#### 1. Overview
Processes multi-page scanned PDF documents containing multiple student answer papers. Automatically splits pages into individual PNG images, extracts student IDs & names via OCR regex, and evaluates papers in batch.

#### 2. Backend Implementation
* **Service (`BulkProcessingService.processBulkPdf`):**
  * Uses `PDDocument.load(bytes)` and `PDFRenderer` to render pages at 150 DPI.
  * Uses `PDFTextStripper` to extract text streams per page.
  * Applies Regex auto-detection:
    * **Roll Number Regex:** `(?i)(?:roll|student\s*id|id)\s*[:#-]?\s*([A-Z0-9-]+)`
    * **Name Regex:** `(?i)(?:name|student)\s*[:#-]?\s*([A-Za-z\s]{3,25})`
  * Saves rendered page PNG images into `uploads/` folder and creates `PaperSubmission` records.
* **Controller (`SubmissionController.java`):**
  * `POST /api/submissions/bulk-pdf`

#### 3. Frontend UI (`index.html` & `app.js`)
* Added **Multi-Page PDF Scan Splitter & Auto-Roll Detector** dropzone card in the Bulk Correction Hub.

---

### 📊 Feature 4: Class Misconception Clustering & Item Analysis

#### 1. Overview
Analyzes class performance per question/rubric criterion, assigns difficulty ratings, groups common student mistakes across papers, and generates AI revision recommendations.

#### 2. Backend Implementation
* **DTO (`EvaluationDtos.java`):**
  * `ItemAnalysisDto`: `examId`, `examTitle`, `totalPapersEvaluated`, `classAveragePercentage`, `rubricItemStats` (`RubricStat`).
  * `RubricStat`: `criteriaName`, `maxScore`, `averageScoreObtained`, `percentageClassScore`, `difficultyRating` (`EASY`, `MODERATE`, `HARD`).
  * `MisconceptionClusterDto`: `examId`, `examTitle`, `commonErrors` (`MisconceptionItem`).
  * `MisconceptionItem`: `issueTitle`, `frequencyCount`, `affectedPercentage`, `recommendation`.
* **Service (`AnalyticsService.java`):**
  * `getItemAnalysisForExam(Long examId)`: Aggregates criterion scores across all evaluations for a selected exam.
  * `getMisconceptionClustersForExam(Long examId)`: Aggregates improvement areas across papers and clusters error patterns with frequency percentages.
* **Controller (`AnalyticsController.java`):**
  * `GET /api/analytics/exam/{examId}/item-analysis`
  * `GET /api/analytics/exam/{examId}/misconceptions`

#### 3. Frontend UI (`index.html` & `app.js`)
* Added **Class Misconception & Rubric Item Analysis** dashboard card with interactive exam selector, difficulty badges, and AI recommendation boxes.

---

## 🧪 Test Execution & Verification

### Unit Test Suite Results
Run via `mvn test`:

| Test Class | Tests Run | Failures | Errors | Status |
| :--- | :---: | :---: | :---: | :---: |
| `AuthServiceTest` | 3 | 0 | 0 | **PASSED** |
| `LlmEvaluationServiceTest` | 4 | 0 | 0 | **PASSED** |
| `OcrServiceTest` | 3 | 0 | 0 | **PASSED** |
| `AnalyticsServiceTest` | 3 | 0 | 0 | **PASSED** |
| **Total** | **13** | **0** | **0** | **100% SUCCESS** |

---

## 📦 Repository & Build Status

* **GitHub Repository:** [azeez1846/School_Exam_Correction](https://github.com/azeez1846/School_Exam_Correction.git)
* **Branch:** `main`
* **Commit Hash:** `e26b8c0`
* **Server Port:** `8081` (`http://localhost:8081`)
