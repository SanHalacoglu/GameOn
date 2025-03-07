# Example M5: Testing and Code Review

## 1. Change History

| **Change Date**   | **Modified Sections** | **Rationale** |
| ----------------- | --------------------- | ------------- |
| _Nothing to show_ |

### 2.1. Locations of Back-end Tests and Instructions to Run Them

#### 2.1.1. Tests

| **Interface**      | **URL**          | **Type** | **Test without Mocks**                                                                 | **Test with Mocks**                                                                  | **Mocked Components** |
| ------------------ | ---------------- | -------- | -------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ | --------------------- |
| **Get All Admins** | `/admins`        | GET      | [AdminController.test.ts#L9](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/AdminController.test.ts#L9)      | [AdminController.test.ts#L38](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/AdminController.test.ts#L38)      | DB              |
| **Get Admin by ID**| `/admins/:id`    | GET      | [AdminController.test.ts#L19](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/AdminController.test.ts#L19)     | [AdminController.test.ts#L50](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/AdminController.test.ts#L50)     | DB              |
| **Create Admin**   | `/admins`        | POST     | [AdminController.test.ts#L34](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/AdminController.test.ts#L34)     | [AdminController.test.ts#L62](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/AdminController.test.ts#L62)     | DB              |
| **Update Admin**   | `/admins/:id`    | PUT      | [AdminController.test.ts#L49](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/AdminController.test.ts#L49)     | [AdminController.test.ts#L78](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/AdminController.test.ts#L78)     | DB              |
| **Delete Admin**   | `/admins/:id`    | DELETE   | [AdminController.test.ts#L61](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/AdminController.test.ts#L61)     | [AdminController.test.ts#L90](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/AdminController.test.ts#L90)     | DB              |
| **Get All Games**  | `/games`         | GET      | [GameController.test.ts#L12](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GameController.test.ts#L12)       | [GameController.test.ts#L28](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GameController.test.ts#L28)       | DB               |
| **Get Game by ID** | `/games/:id`     | GET      | [GameController.test.ts#L22](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GameController.test.ts#L22)       | [GameController.test.ts#L40](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GameController.test.ts#L40)       | DB               |
| **Create Game**    | `/games`         | POST     | [GameController.test.ts#L34](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GameController.test.ts#L34)       | [GameController.test.ts#L52](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GameController.test.ts#L52)       | DB               |
| **Update Game**    | `/games/:id`     | PUT      | [GameController.test.ts#L49](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GameController.test.ts#L49)       | [GameController.test.ts#L64](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GameController.test.ts#L64)       | DB               |
| **Delete Game**    | `/games/:id`     | DELETE   | [GameController.test.ts#L61](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GameController.test.ts#L61)       | [GameController.test.ts#L76](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GameController.test.ts#L76)       | DB               |
| **Get All Groups** | `/groups`        | GET      | [GroupController.test.ts#L23](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L23)     | [GroupController.test.ts#L36](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L36)     | group DB         |
| **Get Group by ID**| `/groups/:group_id` | GET   | [GroupController.test.ts#L30](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L30)     | [GroupController.test.ts#L48](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L48)     | group DB         |
| **Create Group**   | `/groups`        | POST     | [GroupController.test.ts#L37](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L37)     | [GroupController.test.ts#L60](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L60)     | group DB         |
| **Update Group**   | `/groups/:group_id` | PUT   | [GroupController.test.ts#L44](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L44)     | [GroupController.test.ts#L72](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L72)     | group DB         |
| **Delete Group**   | `/groups/:group_id` | DELETE | [GroupController.test.ts#L51](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L51)     | [GroupController.test.ts#L84](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L84)     | group DB         |
| **Join Group**     | `/groups/:group_id/join` | POST | [GroupController.test.ts#L74](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L74)     | [GroupController.test.ts#L96](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L96)     | group DB         |
| **Leave Group**    | `/groups/:group_id/leave` | DELETE | [GroupController.test.ts#L87](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L87)     | [GroupController.test.ts#L108](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L108)   | group DB         |
| **Get Group Members** | `/groups/:group_id/members` | GET | Not available                                                                                                 | [GroupController.test.ts#L120](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L120)   | group DB         |
| **Get Group URL**  | `/groups/:group_id/url` | GET | [GroupController.test.ts#L100](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/GroupController.test.ts#L100)   | [GroupController.test.ts#L132](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/GroupController.test.ts#L132)   | group DB         |
| **Initiate Matchmaking** | `/matchmaking/initiate` | POST | Not available | [MatchmakingController.test.ts#L47](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/MatchmakingController.test.ts#L47) | database, redis |
| **Check Matchmaking Status** | `/matchmaking/status/:discord_id` | GET | Not available | [MatchmakingController.test.ts#L139](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/MatchmakingController.test.ts#L139) | database, redis |
| **Get All Preferences** | `/preferences` | GET | [PreferencesController.test.ts#L28](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/PreferencesController.test.ts#L28) | [PreferencesController.test.ts#L39](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/PreferencesController.test.ts#L39) | database |
| **Get Preferences by ID** | `/preferences/:id` | GET | [PreferencesController.test.ts#L36](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/PreferencesController.test.ts#L36) | [PreferencesController.test.ts#L51](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/PreferencesController.test.ts#L51) | database |
| **Create Preferences** | `/preferences` | POST | [PreferencesController.test.ts#L49](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/PreferencesController.test.ts#L49) | [PreferencesController.test.ts#L63](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/PreferencesController.test.ts#L63) | database |
| **Update Preferences** | `/preferences/:id` | PUT | [PreferencesController.test.ts#L61](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/PreferencesController.test.ts#L61) | [PreferencesController.test.ts#L75](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/PreferencesController.test.ts#L75) | database |
| **Delete Preferences** | `/preferences/:id` | DELETE | Not available | [PreferencesController.test.ts#L87](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/PreferencesController.test.ts#L87) | database |
| **Get Preferences by User ID** | `/preferences/user/:userId` | GET | [PreferencesController.test.ts#L43](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/PreferencesController.test.ts#L43) | [PreferencesController.test.ts#L99](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/PreferencesController.test.ts#L99) | database |
| **Get All Reports** | `/reports` | GET | Not available | [ReportController.test.ts#L49](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/ReportController.test.ts#L49) | database |
| **Get Report by ID** | `/reports/:id` | GET | Not available | [ReportController.test.ts#L64](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/ReportController.test.ts#L64) | database |
| **Create Report** | `/reports` | POST | Not available | [ReportController.test.ts#L98](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/ReportController.test.ts#L98) | database |
| **Resolve Report** | `/reports/:id/resolve` | PUT | Not available | [ReportController.test.ts#L144](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/ReportController.test.ts#L144) | database |
| **Delete Report** | `/reports/:id` | DELETE | Not available | [ReportController.test.ts#L191](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/ReportController.test.ts#L191) | database |
| **Get All Users** | `/users` | GET | [UserController.test.ts#L14](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/UserController.test.ts#L14) | [UserController.test.ts#L41](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/UserController.test.ts#L41) | database |
| **Get User by ID** | `/users/:id` | GET | [UserController.test.ts#L23](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/UserController.test.ts#L23) | [UserController.test.ts#L54](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/UserController.test.ts#L54) | database |
| **Create User** | `/users` | POST | [UserController.test.ts#L32](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/UserController.test.ts#L32) | [UserController.test.ts#L67](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/UserController.test.ts#L67) | database |
| **Update User** | `/users/:id` | PUT | [UserController.test.ts#L41](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/UserController.test.ts#L41) | [UserController.test.ts#L80](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/UserController.test.ts#L80) | database |
| **Delete User** | `/users/:id` | DELETE | [UserController.test.ts#L50](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/UserController.test.ts#L50) | [UserController.test.ts#L93](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/UserController.test.ts#L93) | database |
| **Get User Groups** | `/users/:id/groups` | GET | [UserController.test.ts#L74](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/UserController.test.ts#L74) | [UserController.test.ts#L106](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/UserController.test.ts#L106) | database |
| **Ban User** | `/users/:id/ban` | PUT | [UserController.test.ts#L97](https://github.com/cjohst/GameOn/blob/main/backend/tests/without-mocks/controllers/UserController.test.ts#L97) | [UserController.test.ts#L119](https://github.com/cjohst/GameOn/blob/main/backend/tests/with-mocks/controllers/UserController.test.ts#L119) | database |

#### 2.1.2. Commit Hash Where Tests Run

`[Insert Commit SHA here]`

#### 2.1.3. Explanation on How to Run the Tests

1. **Clone the Repository**:

   - Open your terminal and run:
     ```
     git clone https://github.com/example/your-project.git
     ```

2. **...**

### 2.2. GitHub Actions Configuration Location

`~/.github/workflows/backend-tests.yml`

### 2.3. Jest Coverage Report Screenshots With Mocks

_(Placeholder for Jest coverage screenshot with mocks enabled)_

### 2.4. Jest Coverage Report Screenshots Without Mocks

_(Placeholder for Jest coverage screenshot without mocks)_

---

## 3. Back-end Test Specification: Tests of Non-Functional Requirements

### 3.1. Test Locations in Git

| **Non-Functional Requirement**  | **Location in Git**                              |
| ------------------------------- | ------------------------------------------------ |
| **Performance (Response Time)** | [`tests/nonfunctional/response_time.test.js`](#) |
| **Chat Data Security**          | [`tests/nonfunctional/chat_security.test.js`](#) |

### 3.2. Test Verification and Logs

- **Performance (Response Time)**

  - **Verification:** This test suite simulates multiple concurrent API calls using Jest along with a load-testing utility to mimic real-world user behavior. The focus is on key endpoints such as user login and study group search to ensure that each call completes within the target response time of 2 seconds under normal load. The test logs capture metrics such as average response time, maximum response time, and error rates. These logs are then analyzed to identify any performance bottlenecks, ensuring the system can handle expected traffic without degradation in user experience.
  - **Log Output**
    ```
    [Placeholder for response time test logs]
    ```

- **Chat Data Security**
  - **Verification:** ...
  - **Log Output**
    ```
    [Placeholder for chat security test logs]
    ```

---

## 4. Front-end Test Specification

### 4.1. Location in Git of Front-end Test Suite:

`frontend/src/androidTest/java/com/studygroupfinder/`

### 4.2. Tests

- **Use Case: Login**

  - **Expected Behaviors:**
    | **Scenario Steps** | **Test Case Steps** |
    | ------------------ | ------------------- |
    | 1. The user opens â€œAdd Todo Itemsâ€ screen. | Open â€œAdd Todo Itemsâ€ screen. |
    | 2. The app shows an input text field and an â€œAddâ€ button. The add button is disabled. | Check that the text field is present on screen.<br>Check that the button labelled â€œAddâ€ is present on screen.<br>Check that the â€œAddâ€ button is disabled. |
    | 3a. The user inputs an ill-formatted string. | Input â€œ_^_^^OQ#$â€ in the text field. |
    | 3a1. The app displays an error message prompting the user for the expected format. | Check that a dialog is opened with the text: â€œPlease use only alphanumeric charactersâ€. |
    | 3. The user inputs a new item for the list and the add button becomes enabled. | Input â€œbuy milkâ€ in the text field.<br>Check that the button labelled â€œaddâ€ is enabled. |
    | 4. The user presses the â€œAddâ€ button. | Click the button labelled â€œaddâ€. |
    | 5. The screen refreshes and the new item is at the bottom of the todo list. | Check that a text box with the text â€œbuy milkâ€ is present on screen.<br>Input â€œbuy chocolateâ€ in the text field.<br>Click the button labelled â€œaddâ€.<br>Check that two text boxes are present on the screen with â€œbuy milkâ€ on top and â€œbuy chocolateâ€ at the bottom. |
    | 5a. The list exceeds the maximum todo-list size. | Repeat steps 3 to 5 ten times.<br>Check that a dialog is opened with the text: â€œYou have too many items, try completing one firstâ€. |

  - **Test Logs:**
    ```
    [Placeholder for Espresso test execution logs]
    ```

- **Use Case: ...**

  - **Expected Behaviors:**

    | **Scenario Steps** | **Test Case Steps** |
    | ------------------ | ------------------- |
    | ...                | ...                 |

  - **Test Logs:**
    ```
    [Placeholder for Espresso test execution logs]
    ```

- **...**

---

## 5. Automated Code Review Results

### 5.1. Commit Hash Where Codacy Ran

`[Insert Commit SHA here]`

### 5.2. Unfixed Issues per Codacy Category

_(Placeholder for screenshots of Codacyâ€™s Category Breakdown table in Overview)_

### 5.3. Unfixed Issues per Codacy Code Pattern

_(Placeholder for screenshots of Codacyâ€™s Issues page)_

### 5.4. Justifications for Unfixed Issues

- **Code Pattern: [Usage of Deprecated Modules](#)**

  1. **Issue**

     - **Location in Git:** [`src/services/chatService.js#L31`](#)
     - **Justification:** ...

  2. ...

- ...