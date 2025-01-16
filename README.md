# serenAiGrid

Harmonizing healthcare connectivity through AI-driven intelligence and innovation.

## Project Overview

The **serenAiGrid** project aims to optimize healthcare resource management by leveraging Artificial Intelligence (AI) and Large Language Models (LLMs). The system is designed to dynamically allocate network resources in health centers and automate medical documentation processes.

### Key Features

- **Dynamic Bandwidth Allocation:** Ensures network prioritization for telemedicine systems or emergency services during congestion.
- **Automated Procurement Documentation:** Generates documents for public tenders and health procurement, streamlining decision-making.
- **Emergency Management Support:** Provides immediate, AI-generated documentation for health teams during disasters.

---

## System Flow

### Data Input
- **Network:** Real-time data on network status, bandwidth usage, and congestion.
- **Health:** Priority requests for telemedicine or emergencies.

### Processing
- AI evaluates the input data and redistributes network resources dynamically.
- LLM generates clear, understandable documentation regarding these decisions.

### Output
- **Notifications:** Real-time updates via a dashboard.
- **Reports:** Storage of structured and unstructured reports for future use.

---

## Modules Overview

### 1. Data Collection (Network Data Simulation)

The module `serenaigrid-network-data-module` handles the simulation of network data by registering and monitoring networks (LAN or WAN).

#### Features
- **Network Registration:**
  - Users can register networks with specific details like the number of nodes, IP ranges, and parameters.
  - Backend stores this information, assigning a unique identifier to each network.
  - Service: `NetworkService`.

- **Network Simulation:**
  - Simulates data such as IP addresses, bandwidth usage, and latency for registered networks.
  - Data is sent to a Python server (`serenaigrid-ai-data-processing-module`) for further AI-based processing.
  - Service: `NetworkSimulatorService`.

#### APIs
- **Register a Network:**
  `POST /network/register`
  Data: Network name, type (LAN/WAN), number of nodes, description.

- **List Registered Networks:**
  `GET /network/list`

- **Start Simulation for a Registered Network:**
  `POST /monitoring/network/{networkId}`

#### Simulation Details
- **IP Address:**
  - **LAN:** Simulated in the format `192.168.x.x` (x = random 0–255).
  - **WAN:** Simulated as `x.x.x.x` (x = random 0–255).

- **Bandwidth Usage:**
  - **LAN:** Random values between 0–1000 Mbps.
  - **WAN:** Random values between 0–100 Mbps.

- **Latency:**
  - **LAN:** Simulated between 1–50 ms.
  - **WAN:** Simulated between 50–250 ms.

---

### 2. Data Collection (Medical Data Simulation)

The module `serenaigrid-medical-data-module` simulates medical data using the FHIR (Fast Healthcare Interoperability Resources) standard to support medical emergencies, telemedicine sessions, and device management.

#### Features
- **FHIR Data Generation:**
  - Simulates medical emergencies (e.g., cardiac arrest, allergic reactions) with FHIR `Observation` resources.
  - Generates telemedicine sessions, including QoS metrics (latency and bandwidth).
  - Simulates medical devices (e.g., ventilators, ECG monitors), assigning custom properties and statuses.

- **Service:** `FHIRDataGeneratorService`.

#### Flow of Use
1. **Bundle Creation:**
   - A client request triggers the generation of a FHIR Bundle containing simulated emergencies, telemedicine sessions, and device data.
   - Service: `BundleProvider`.

2. **Python Processing:**
   - The Bundle is sent to a Python server (`serenaigrid-ai-data-processing-module`) for AI-based processing before being returned to the client.

---

## How to Get Started

### Prerequisites
- **Java** (>= 11)
- **Python** (>= 3.8)
- Required libraries (see individual module documentation).

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Serenella97325/connectivity-hackathon-serenAiGrid.git
