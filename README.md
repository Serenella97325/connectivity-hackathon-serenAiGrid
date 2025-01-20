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
  `POST http://localhost:8081/spring-boot-network/network/register`
  Data: Network name, type (LAN/WAN), number of nodes, description.

- **List Registered Networks:**
  `GET http://localhost:8081/spring-boot-network/network/list`

- **Retrieve Data for a Specific Network:**
  `GET http://localhost:8081/spring-boot-network/network/{networkId}`

- **Monitor Network Data:**
  `GET http://localhost:8081/spring-boot-network/monitoring/network/{networkId}`
  Description: Simulates network data (e.g., bandwidth usage, IP addresses, latency) for the specified network identified by `networkId`.

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

#### APIs
- **Retrieve Server Metadata:**
  `GET http://localhost:8080/spring-boot-hapi-fhir/metadata`
  Description: Returns the metadata of the FHIR server.

- **Create a Bundle:**
  `POST http://localhost:8080/spring-boot-hapi-fhir/Bundle`
  Description: Creates a FHIR Bundle containing simulated medical data (e.g., emergencies, telemedicine sessions, devices).

- **Retrieve a Bundle:**
  `GET http://localhost:8080/spring-boot-hapi-fhir/Bundle/{id}`
  Description: Retrieves the details of a specific FHIR Bundle by its unique identifier (`id`).

#### Flow of Use
1. **Bundle Creation:**
   - A client sends a request to the `/Bundle` endpoint to create a new FHIR Bundle with simulated data (emergencies, telemedicine, devices).
   - Service: `BundleProvider`.

2. **Python Processing:**
   - The Bundle is sent to a Python server (`serenaigrid-ai-data-processing-module`) for AI-based processing before being returned to the client.

---

### 3. Artificial Intelligence Module

The module `serenaigrid-ai-data-processing-module` is responsible for processing and optimizing both medical and network data received from the Spring Boot simulators. By leveraging AI algorithms, the module dynamically allocates network resources and generates documentation in PDF format, tailored to the needs of healthcare teams. The module is divided into three core Python files that implement Flask-based data reception, AI-driven optimization, and PDF report generation.

#### Features
- **Data Processing and Integration:**
  - Receives medical and network data from Spring Boot simulators via a Flask server running on port 5000.
  - Combines the data and stores it in JSON format for analysis and decision-making.

- **Dynamic Bandwidth Allocation Optimization:**
  - Implements a greedy algorithm to optimize network bandwidth allocation based on medical priorities (e.g., emergencies, telemedicine sessions).
  - Adjusts bandwidth allocation dynamically according to the Quality of Service (QoS) requirements of the medical services.

- **Report Generation:**
  - Uses the Llama3 language model (via the Ollama API) to generate PDF reports for various use cases.
  - Supports three report templates (`Public Tenders`, `Emergencies`, `General Reports`)

#### Logic Files
- `server.py`
  - Implements a Flask server that receives medical and network data from the Spring Boot simulators.
  - Endpoints:
    - `/process-bundle`: Receives medical data in FHIR-compliant JSON format.
    - `/process-monitoring-data`: Receives network monitoring data (e.g., bandwidth, latency).

- `greedyAlgorithm.py`
  - Optimizes network resource allocation based on the medical priorities and QoS requirements.
  - Core Functions:
    - Bandwidth allocation prioritization: High > Normal > Low priority.
    - Graphical representation of optimization results using `matplotlib` and `seaborn`.
  - Inputs:
    - Medical emergencies and telemedicine sessions with specified priorities (High, Normal, Low).
    - Network data (e.g., bandwidth, latency).
  - Outputs:
    - Optimized network resource allocation details saved in result.json, including:
      - Managed and unmet emergencies.
      - Initial and final bandwidth allocations for the network and individual nodes.
  - Visualization::
    - Graphical representation of resource allocation before and after optimization.

- `llama3.py`
  - Interacts with the Llama3-based model via the Ollama API to generate context-aware reports.
  - Report Types:
    - Public tenders.
    - Emergency management reports.
    - General operational reports.
  - Report Generation:
    - Generates PDF reports using the FPDF library, tailored to user specifications.
  - Input:
    - The user selects the report type, and related data is passed to the LLM for processing.
  - Output:
    - A PDF report is generated and saved as `generated_report.pdf`.

#### Additional Files
- `data_received.json`
  - Example of the combined medical and network data received from the Spring Boot simulators.
  - The medical data follows the FHIR standard, and the network data includes details like node bandwidth and latency.

- `result.json`
  - Example of the optimization result from the `greedyAlgorithm.py` module.
  - The medical data follows the FHIR standard, and the network data includes details like node bandwidth and latency.

- `generated_report.pdf`
  - Example of a PDF report generated by the AI module using the "Emergency" template.

---

## How to Get Started

### Prerequisites
- **Java** (17)
- **Python** (3.10.11)
- Required libraries (see individual module documentation).

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Serenella97325/connectivity-hackathon-serenAiGrid.git
