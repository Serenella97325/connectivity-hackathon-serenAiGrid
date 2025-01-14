import json
from flask import Flask, request, jsonify

app = Flask(__name__)

# Variabili globali per memorizzare i dati ricevuti
medical_data = None
network_data = None


@app.route('/process-bundle', methods=['POST'])
def process_bundle():

    global medical_data

    # Ottieni il bundle JSON inviato dal server Spring Boot
    medical_data = request.get_json()

    # Verifica se i dati sono stati ricevuti correttamente
    if not medical_data:
        return jsonify({"status": "error", "message": "Nessun bundle ricevuto"}), 400

    # Stampa il JSON formattato in modo leggibile
    print("Bundle ricevuto:")
    # Formattazione leggibile del JSON
    print(json.dumps(medical_data, indent=4, ensure_ascii=False))

    # Elabora il bundle come desiderato (ad esempio, eseguire calcoli o altre operazioni)
    # Qui puoi fare qualsiasi cosa con il bundle che hai ricevuto
    # Restituisci il bundle direttamente
    return jsonify(medical_data), 200


@app.route('/process-monitoring-data', methods=['POST'])
def process_monitoring_data():
    global network_data

    # Ottieni i dati JSON inviati da Spring Boot
    network_data = request.get_json()

    if not network_data:
        return jsonify({"status": "error", "message": "Nessun dato di monitoraggio ricevuto"}), 400

    # Log dei dati ricevuti
    print("Dati di monitoraggio rete ricevuti:")
    print(json.dumps(network_data, indent=4, ensure_ascii=False))

    # Esegui il processamento necessario
    # Ad esempio: modifica, calcoli, ecc.
    processed_data = network_data  # Per ora, restituisci i dati come sono

    return jsonify(processed_data), 200


if __name__ == '__main__':
    app.run(debug=True, port=5000)
