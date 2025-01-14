import json
from flask import Flask, request, jsonify

app = Flask(__name__)

# Global variables to store received data
medical_data = None
network_data = None


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

    # Return the bundle directly to Spring Boot
    return jsonify(medical_data), 200


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

    # Return the network data directly to Spring Boot
    processed_data = network_data

    return jsonify(processed_data), 200


if __name__ == '__main__':
    app.run(debug=True, port=5000)
