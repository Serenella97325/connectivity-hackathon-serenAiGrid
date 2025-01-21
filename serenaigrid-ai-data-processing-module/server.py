import json
from flask import Flask, request, jsonify

app = Flask(__name__)

# Global variables to store received data
medical_data = None
network_data = None

# Path to save data
DATA_PATH = 'data_received.json'


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

    # Processing the bundle as desired (for example, performing calculations or other operations) --> TO DO with AI

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
    print("Dati di monitoraggio rete ricevuti:")
    print(json.dumps(network_data, indent=4, ensure_ascii=False))

    # Processing the network data as desired (for example, performing calculations or other operations) --> TO DO with AI

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


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
