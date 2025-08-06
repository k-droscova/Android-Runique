# 🏃‍♀️ Runique App

**Runique** is a multi-module running tracker Android app built as part of the course  
**“The Essentials of Industry-Level Android App Development”** by [Philipp Lackner](https://pl-coding.com/).  
The goal of this project was to implement a modern, scalable, and production-ready app using best practices in architecture, modularization, and UI/UX.

You can find the original Figma design here:  
👉 [Runique Figma Mockups](https://www.figma.com/design/NNSWHCD7WMpzks7xfKp35c/Runique-Course?node-id=0-1&p=f)

---

## ✨ Highlights

This app was built with a strong focus on **Clean Architecture**, **SOLID principles**, and **modern Android development**.  
It also explores **dynamic feature delivery**, allowing features to be installed on demand at runtime.

---

## 🎯 Goals

- ✅ Create a **multi-module** app with **Clean Architecture** and well-structured layers (data, domain, presentation).
- ✅ Apply the **MVI pattern** in the presentation layer using `Action`, `State`, `ViewModel`, and `Composable Screen`.
- ✅ Integrate a **dynamic feature module** (Analytics) that is downloaded only when accessed.

---

## 💡 Key Takeaways

Throughout this project, I explored a wide range of advanced Android concepts:

1. 📐 **Architecture & Gradle Setup**  
   - Custom Gradle plugins  
   - Clean modular project structure  
   - Dependency management and build optimization

2. 🚦 **State Management & Dependency Injection**  
   - Unidirectional data flow with MVI  
   - `Koin` for dependency injection  
   - Coroutines and Flows for reactive state handling

3. 🌐 **Networking & Auth**  
   - `Ktor` for API communication  
   - OAuth-based authentication  
   - Token handling (access & refresh)

4. 📍 **Location & Foreground Services**  
   - Foreground service for active run tracking  
   - Real-time location updates and distance calculations

5. 🗺️ **Google Maps Integration**  
   - Maps Compose for rendering running routes  
   - Custom markers and camera animations

6. 💾 **Offline-First Architecture**  
   - `Room` for local persistence  
   - Custom sync mechanism to handle pending creations and deletions  
   - `WorkManager` for background syncing

7. 📱 **Modern UI with Jetpack Compose**  
   - Custom theming and design system  
   - Material 3, dynamic colors, and animations  
   - Modular screen components and responsive layouts

---

## 🧩 Dynamic Feature: Analytics

The **Analytics** module is implemented as a dynamic feature using Play Feature Delivery.  
It’s installed **on demand** the first time the user accesses it. This feature showcases:

- Room DAO-based aggregation (total distance, pace, max speed, etc.)
- On-demand module installation
- Dynamic dependency injection using `Koin`

---

## 🚀 Tech Stack

| Category              | Technology                         |
|-----------------------|-------------------------------------|
| Language              | Kotlin                              |
| Architecture          | Clean Architecture + MVI            |
| DI                    | Koin                                |
| Networking            | Ktor                                |
| Local Storage         | Room                                |
| Background Work       | WorkManager                         |
| UI                    | Jetpack Compose (Material 3)        |
| Maps                  | Google Maps Compose                 |
| Location Tracking     | Fused Location Provider + Foreground Service |
| Auth                  | OAuth with token refresh            |
| Modularization        | Multi-module + Dynamic Features     |

---

## 📜 Certificate of Completion

This project was completed as part of the [Essentials of Industry-Level Android App Development](https://pl-coding.com/) course.  
Below is the certificate of completion awarded upon successfully finishing the course:

👉 [View Certificate (PDF)](certificate.pdf)


---


## 🧠 Credits

- Course: [Philipp Lackner – Essentials of Industry-Level Android](https://pl-coding.com/)
- UI Design: [Runique Figma Mockups](https://www.figma.com/design/NNSWHCD7WMpzks7xfKp35c/Runique-Course?node-id=0-1&p=f)

---

## 📄 License

This project is for educational purposes and not intended for commercial distribution.

---
