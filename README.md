GurionMapSim !!

A multithreaded simulation of a robot perception and mapping system, implemented in Java using a MicroService-based architecture.

Project overview:
This project simulates the perception and mapping system of a robotic vacuum cleaner ("GurionRock Pro Max Ultra Over 9000") as it navigates an unknown environment. It processes data from simulated sensors: Camera, LiDAR, IMU, and GPS, and fuses it using a simplified SLAM algorithm.

The system is fully event-driven, uses a message-passing framework, and supports concurrent processing across multiple threads.


### Microservices:
- **TimeService** – Emits ticks to drive system progression.
- **CameraService** – Sends `DetectObjectsEvent` based on internal frequency and raw object descriptions.
- **LiDarWorkerService** – Listens for detection events and enriches them with 3D coordinates.
- **FusionSlamService** – Integrates positional data and builds a global map.
- **PoseService** – Provides real-time pose updates of the robot.

### Message Types:
- `TickBroadcast`, `CrashedBroadcast`, `TerminatedBroadcast`
- `DetectObjectsEvent`, `TrackedObjectsEvent`, `PoseEvent`

### Core Concepts:
- **Event Loop + MessageBus**: Each MicroService runs in its own thread and processes messages asynchronously.
- **Synchronization**: All communication is coordinated via a thread-safe singleton `MessageBus`.
- **SLAM**: Tracks objects and refines positions using averaging over time.

## 🔧 Requirements

- Java 8+
- Maven
- GSON (for JSON parsing)
- JUnit (for unit testing)

## 📂 Input

Provide 4 JSON files via a configuration file:
1. Configuration file
2. Pose data
3. LiDAR data
4. Camera data

Example run:

```bash
java -jar GurionMapSim.jar path/to/config.json
פרם

פ
