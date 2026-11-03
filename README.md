<div align="center">

  <p align="center">
    <img src="assets/screenshots/app-icon.jpg" width="130" style="border-radius: 26px;" alt="Notezy app icon" />
  </p>

  # 📝 Notezy
  ### *Your thoughts, organized in one place.*

  A sleek, modern mobile notes application designed for capturing spontaneous ideas, sketching creative thoughts, and keeping your life organized.

  <p align="center">
    <a href="https://github.com/Andinet2123/Notezy/stargazers"><img src="https://img.shields.io/github/stars/Andinet2123/Notezy?color=FF7A00&logo=star&style=for-the-badge" alt="Stars"></a>
    <a href="https://github.com/Andinet2123/Notezy/network/members"><img src="https://img.shields.io/github/forks/Andinet2123/Notezy?color=3B82F6&logo=git&style=for-the-badge" alt="Forks"></a>
    <a href="https://github.com/Andinet2123/Notezy/issues"><img src="https://img.shields.io/github/issues/Andinet2123/Notezy?color=EF4444&logo=github&style=for-the-badge" alt="Issues"></a>
    <a href="https://github.com/Andinet2123/Notezy/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-MIT-10B981?style=for-the-badge" alt="License"></a>
    <a href="https://github.com/Andinet2123/Notezy"><img src="https://img.shields.io/badge/PRs-Welcome-6366F1?style=for-the-badge" alt="PRs Welcome"></a>
  </p>

  <p align="center">
    <a href="#-screenshots"><b>Screenshots</b></a> •
    <a href="#-features"><b>Features</b></a> •
    <a href="#-design-philosophy"><b>Design</b></a> •
    <a href="#-tech-stack"><b>Tech Stack</b></a> •
    <a href="#-getting-started"><b>Getting Started</b></a> •
    <a href="#-roadmap"><b>Roadmap</b></a>
  </p>

</div>

---

## 📱 Screenshots

<div align="center">
  <p align="center">
    <img src="assets/screenshots/home.jpg" width="240" alt="Notezy home screen with notes list and search" />
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <img src="assets/screenshots/note-editor.jpg" width="240" alt="Notezy note editor" />
  </p>
  <p align="center">
    <b>🏠 Home Screen & Search</b> &nbsp;&bull;&nbsp; <b>✍️ Rich Note Editor</b>
  </p>

  <br />

  <p align="center">
    <img src="assets/screenshots/sketch-canvas.jpg" width="240" alt="Notezy sketch canvas with color and line width controls" />
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <img src="assets/screenshots/editor-themes.jpg" width="240" alt="Notezy editor background theme options" />
  </p>
  <p align="center">
    <b>🎨 Freehand Sketch Canvas</b> &nbsp;&bull;&nbsp; <b>🌈 Custom Editor Themes</b>
  </p>
</div>

---

## 💡 Notezy Workflow

```mermaid
flowchart LR
    A[💡 New Idea] --> B[📝 Notezy Editor]
    B --> C[🎨 Sketch Canvas]
    B --> D[☑️ Task Checklists]
    B --> E[🌈 Custom Theme Palette]
    C --> F[🔒 Passcode Protected Vault]
    D --> F
    E --> F
    F --> G[🔍 Instant Search & Quick Pin]
```

---

## ✨ Features

| Feature | Highlight | Description |
| :--- | :--- | :--- |
| ✍️ **Create & Edit** | Effortless Writing | Quick capture mode to draft thoughts, memos, and ideas in seconds. |
| 🔍 **Instant Search** | Fast Retrieval | Real-time search query to instantly locate any note or checklist item. |
| 🔒 **Locked Notes** | Privacy First | Secure sensitive personal notes and ideas behind a private passcode. |
| 📌 **Pin & Share** | Quick Actions | Pin your most crucial notes to the top and share content with one tap. |
| 🎨 **Sketch Canvas** | Visual Doodling | Built-in freehand drawing pad with selectable color palettes and adjustable stroke widths. |
| 🌈 **Editor Themes** | Personal Aesthetics | Customize your writing experience with calming warm and modern background tones. |
| ☑️ **Interactive Checklists** | Task Management | Turn thoughts into actionable to-do items with toggleable checkboxes. |
| ⚡ **Clean Interface** | Zero Distractions | Carefully designed layout that puts your typography and writing front and center. |

---

## 🎨 Design Philosophy

Notezy is crafted around visual warmth and focus:
- **Dark Mode Home:** Deep, contrast-rich list screen that reduces eye strain and makes notes pop.
- **Warm Note Cards:** Tactile card surfaces accented with vibrant orange elements.
- **Calming Editor:** Minimalist, glare-free editor canvas with customizable pastel themes for distraction-free writing.

---

## 🛠️ Tech Stack

<div align="center">

| Layer | Technologies |
| :--- | :--- |
| **Frontend / Mobile** | ![React Native](https://img.shields.io/badge/React_Native-20232A?style=flat-square&logo=react&logoColor=61DAFB) ![Expo](https://img.shields.io/badge/Expo-000020?style=flat-square&logo=expo&logoColor=white) ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=typescript&logoColor=white) |
| **Styling & Icons** | ![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=flat-square&logo=tailwind-css&logoColor=white) ![Lucide Icons](https://img.shields.io/badge/Lucide_Icons-F05032?style=flat-square&logo=lucide&logoColor=white) |
| **Local Storage & State** | ![AsyncStorage](https://img.shields.io/badge/AsyncStorage-4E5D6C?style=flat-square) ![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat-square&logo=sqlite&logoColor=white) |
| **Security** | ![Expo LocalAuthentication](https://img.shields.io/badge/Biometric_/_Passcode-10B981?style=flat-square&logo=keycdn&logoColor=white) |

</div>

> *Tip: You can customize the badges above to match the exact libraries used in your build.*

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed on your development machine:
- [Node.js](https://nodejs.org/) (v18.0.0 or higher)
- [npm](https://www.npmjs.com/) or [yarn](https://yarnpkg.com/)
- [Git](https://git-scm.com/)
- [Expo Go](https://expo.dev/go) app on your physical device (or Android Studio / Xcode emulator)

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Andinet2123/Notezy.git
   cd Notezy
   ```

2. **Install project dependencies:**
   ```bash
   npm install
   ```

3. **Start the development server:**
   ```bash
   npx expo start
   ```

4. **Launch the app:**
   - Scan the QR code with **Expo Go** (Android) or the **Camera App** (iOS).
   - Or press `a` for Android Emulator / `i` for iOS Simulator.

---

## 📁 Project Structure

```text
Notezy/
├── assets/
│   └── screenshots/          # App icon & preview screenshots
│       ├── app-icon.jpg
│       ├── home.jpg
│       ├── note-editor.jpg
│       ├── sketch-canvas.jpg
│       └── editor-themes.jpg
├── src/
│   ├── components/           # NoteCard, ColorPicker, CanvasBar, Header
│   ├── screens/              # HomeScreen, EditorScreen, CanvasScreen, LockScreen
│   ├── hooks/                # useNotes, useCanvas, usePasscode
│   ├── storage/              # SQLite / AsyncStorage persistence
│   └── styles/               # Color themes and global design tokens
├── package.json
└── README.md
```

---

## 🗺️ Roadmap

- [x] Rich text writing & note editing
- [x] Freehand sketch canvas with line width and color palette
- [x] Custom writing background theme selector
- [x] Passcode lock for confidential notes
- [ ] Biometric unlock (Fingerprint / FaceID)
- [ ] Audio voice-memo attachments
- [ ] Cloud sync & backup (Google Drive / iCloud)
- [ ] Export notes to PDF and Markdown

---

## 🤝 Contributing

Contributions make the open-source community thrive! Any bug fixes, feature proposals, or design enhancements are welcome.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more details.

---

<div align="center">

  Crafted with ❤️ by **[Andinet Yohannes](https://github.com/Andinet2123)**

  <p align="center">
    <a href="https://github.com/Andinet2123"><img src="https://img.shields.io/badge/GitHub-Profile-181717?style=flat&logo=github" alt="GitHub"></a>
  </p>

  ⭐ *If you find Notezy useful, don't forget to star the repository!*

</div>
