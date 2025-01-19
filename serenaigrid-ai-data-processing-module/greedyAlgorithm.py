import matplotlib.pyplot as plt
import pandas as pd
import json

# Path to the JSON file saved by Flask
DATA_PATH = 'data_received.json'


def load_data():
    # Load data from JSON file 'data_received'
    with open(DATA_PATH, 'r') as f:
        data = json.load(f)
    return data


def optimize_bandwidth_allocation(medical_data, network_data):
    """
    Optimizes bandwidth allocation across network nodes based on medical emergencies and QoS.

    :param medical_data: Dictionary containing medical emergency data.
    :param network_data: Dictionary containing network node data.
    :return: Dictionary with allocation results and summary.
    """
    # Parse network data and calculate total bandwidth
    nodes = network_data['networkMetrics']
    total_bandwidth = sum(node['bandwidthUsage'] for node in nodes)
    initial_bandwidth = total_bandwidth

    # Parse medical emergencies and telemedicine sessions
    emergencies = []
    for entry in medical_data['entry']:
        resource = entry['resource']
        if resource['resourceType'] == 'Observation':
            qos = next(
                (component['valueQuantity']['value'] for component in resource.get('component', [])
                 if component['code']['coding'][0]['code'] in ['emergency-qos', 'telemedicine-qos']),
                None
            )
            priority = next(
                (ext['valueString'] for ext in resource['extension']
                 if ext['url'] in ['http://emergency.org/fhir/network-priority', 'http://telemedicine.org/fhir/network-priority']),
                None
            )
            bandwidth = next(
                (ext['valueQuantity']['value'] for ext in resource['extension']
                 if ext['url'] in ['http://emergency.org/fhir/network-bandwidth', 'http://telemedicine.org/fhir/network-bandwidth']),
                None
            )
            emergency_type = next(
                (note['text'] for note in resource.get('note', [])
                 if 'Emergency' in note['text']),
                None
            )
            patient_reference = resource.get(
                'subject', {}).get('reference', None)

            if qos is not None and priority:
                emergencies.append({
                    'emergency_id': resource['id'],
                    'emergency_priority': priority,
                    'emergency_bandwidth': bandwidth,
                    'emergency_QoS': qos,
                    'emergency_type': emergency_type or 'Telemedicine session',
                    'patient_reference': patient_reference
                })

    # Sort emergencies by priority ('High' > 'Normal' > 'Low') and QoS in ascending order
    priority_map = {'High': 1, 'Normal': 2, 'Low': 3}
    emergencies_sorted = sorted(emergencies, key=lambda x: (
        priority_map[x['emergency_priority']], x['emergency_QoS']))

    # Allocate bandwidth dynamically across nodes
    allocations = []
    unhandled_emergencies = []
    node_bandwidth = {node['id']: node['bandwidthUsage'] for node in nodes}
    node_bandwidth_initial_allocation = node_bandwidth.copy()

    for emergency in emergencies_sorted:
        if total_bandwidth <= 0:
            unhandled_emergencies.append(emergency)
            continue

        # Determine the required bandwidth for the emergency
        required_bandwidth = emergency['emergency_bandwidth']

        # Allocate bandwidth from nodes with available resources
        allocated = False
        for node_id, available_bandwidth in node_bandwidth.items():
            if available_bandwidth > 0 and available_bandwidth >= required_bandwidth:

                # Create a new allocation dictionary for this emergency
                current_allocation = {
                    'node_id': node_id,
                    'emergency_id': emergency['emergency_id'],
                    'emergency_priority': emergency['emergency_priority'],
                    'emergency_type': emergency['emergency_type'],
                    'emergency_QoS': emergency['emergency_QoS'],
                    'patient_reference': emergency['patient_reference']
                }

                # Add the current allocation to the list
                allocations.append(current_allocation)

                # Reduce bandwidth from the node
                node_bandwidth[node_id] -= required_bandwidth
                total_bandwidth -= required_bandwidth
                allocated = True

                break

        if not allocated:
            unhandled_emergencies.append(emergency)

    # Generate summary
    summary = {
        "handled_emergencies": allocations,
        "unhandled_emergencies": unhandled_emergencies,
        "initial_bandwidth": initial_bandwidth,
        "remaining_bandwidth": total_bandwidth,
        "node_bandwidth_initial_allocation": node_bandwidth_initial_allocation,
        "node_bandwidth_final_allocation": node_bandwidth
    }

    return {
        "allocations": allocations,
        "summary": summary
    }


# Extract useful information from network observations and metrics
medical_data_json = load_data()['medical_data']
network_data_json = load_data()['network_data']

result = optimize_bandwidth_allocation(medical_data_json, network_data_json)

# Output the result
print("Allocation Result:", result['allocations'])
print("\n")
print("Summary:", result['summary'])
