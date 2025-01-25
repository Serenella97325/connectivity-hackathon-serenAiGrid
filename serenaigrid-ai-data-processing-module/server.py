import json
import subprocess
import threading
from flask import Flask, request, jsonify

app = Flask(__name__)

# Global variables to store received data
medical_data = None
network_data = None

# Path to save data
DATA_PATH = 'data_received.json'
RESULT_PATH = 'result.json'


# Function to get the JSON bundle containing medical data sent from the Spring Boot server
@app.route('/process-bundle', methods=['POST'])
def process_bundle():

    global medical_data

    # Get the JSON bundle containing medical data sent from the Spring Boot server
    medical_data = request.get_json()

    # Check if data was received correctly
    if not medical_data:
        return jsonify({"status": "error", "message": "Nessun bundle ricevuto"}), 400

    # Print the JSON formatted legibly
    print("Bundle received:")
    # Readable JSON format
    print(json.dumps(medical_data, indent=4, ensure_ascii=False))

    # After receiving the medical data, check if both sets of data are available and save combined data
    save_combined_data()

    # Return the bundle directly to Spring Boot
    return jsonify(medical_data), 200


# Function to get network JSON data containing the network details
@app.route('/process-monitoring-data', methods=['POST'])
def process_monitoring_data():

    global network_data

    # Get network JSON data sent from Spring Boot
    network_data = request.get_json()

    if not network_data:
        return jsonify({"status": "error", "message": "Nessun dato di monitoraggio ricevuto"}), 400

    # Log of data received
    print("Network monitoring data received:")
    print(json.dumps(network_data, indent=4, ensure_ascii=False))

    # After receiving the network data, check if both sets of data are available and save combined data
    save_combined_data()

    # Return the network data directly to Spring Boot
    processed_data = network_data

    return jsonify(processed_data), 200


# Function to save the combined data (medical and network)
def save_combined_data():
    if medical_data and network_data:
        # Combining medical and network data
        combined_data = {
            "medical_data": medical_data,
            "network_data": network_data
        }

        # Save data in JSON file
        with open(DATA_PATH, 'w') as f:
            json.dump(combined_data, f, indent=4, ensure_ascii=False)

        print("Combined data saved in file:")
        print(json.dumps(combined_data, indent=4, ensure_ascii=False))

        # Call greedyAlgorithm.py to process the data in background
        threading.Thread(target=run_greedy_algorithm).start()

        # Call llama3.py to generate the report in background
        threading.Thread(target=run_llama_report).start()


# Function to run greedyAlgorithm.py
def run_greedy_algorithm():
    try:
        result = subprocess.run(
            ['python', 'greedyAlgorithm.py'],
            capture_output=True,
            text=True
        )

        if result.returncode == 0:
            print("Greedy Algorithm executed successfully")
            print(result.stdout)
        else:
            print("Error executing greedy algorithm:", result.stderr)
    except Exception as e:
        print(f"Errore nell'eseguire greedy_algorithm.py: {e}")


# Function to run llama3.py to generate the report
def run_llama_report():
    try:
        result = subprocess.run(
            ['python', 'llama3.py'],
            capture_output=True,
            text=True
        )

        if result.returncode == 0:
            print("Llama report generated successfully")
            print(result.stdout)
        else:
            print("Error executing llama_report.py:", result.stderr)
    except Exception as e:
        print(f"Errore nell'eseguire llama_report.py: {e}")


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
