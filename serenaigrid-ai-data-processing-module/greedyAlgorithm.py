import matplotlib.pyplot as plt
import matplotlib.colors as mcolors
from matplotlib.patches import Rectangle
import pandas as pd
import json
import numpy as np
import seaborn as sns

''' Part 1: Managing the allocation of the node band according to the QoS of the emergencies/telemedicine sessions '''

# Path to the JSON file saved by Flask
DATA_PATH = 'data_received.json'

# Saving result in json file
RESULT_PATH = 'result.json'


# Load medical and network data
def load_data():
    # Load data from JSON file 'data_received'
    with open(DATA_PATH, 'r') as f:
        data = json.load(f)
    return data


# Method to optimize bandwidth allocation
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
                    'emergency_bandwidth': emergency['emergency_bandwidth'],
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


# Function to save the result in a JSON file
def save_result_to_json(result, path=RESULT_PATH):
    """
    Save results in JSON format.

    :param result: Dictionary containing the results to be saved.
    :param path: Path to the JSON file.
    """
    with open(path, 'w') as file:
        json.dump(result, file, indent=4)
    print(f"Results saved in: {path}")


# Extract useful information from network observations and metrics
medical_data_json = load_data()['medical_data']
network_data_json = load_data()['network_data']

result = optimize_bandwidth_allocation(medical_data_json, network_data_json)

# Output the result
print("Allocation Result:", result['allocations'])
print("\n")
print("Summary:", result['summary'])


''' Part 2: Salvataggio dei dati da passare a Llama3 per la generazione di report '''

save_result_to_json(result, path=RESULT_PATH)


''' Part 3: Graphical representation of the summary '''

handled_emergencies = result['summary']["handled_emergencies"]
unhandled_emergencies = result['summary']["unhandled_emergencies"]

# Calculate the number of managed and unmanaged emergencies
handled = len(handled_emergencies)
unhandled = len(unhandled_emergencies)

# Identify types of emergency
handled_types = [e['emergency_type'] for e in handled_emergencies]
unhandled_types = [e['emergency_type'] for e in unhandled_emergencies]

# Merge types of emergencies into a string
handled_label = f"$\\bf{{Handled\ Emergencies\ ({handled})}}$\n" + \
    "\n".join(handled_types)
unhandled_label = f"$\\bf{{Unhandled\ Emergencies\ ({unhandled})}}$\n" + "\n".join(
    unhandled_types)

# Labels for the pie chart
pie_labels = [handled_label, unhandled_label]

# Bar Chart: Allocated vs Unallocated Bandwidth
initial_bandwidth = result['summary']["initial_bandwidth"]
remaining_bandwidth = result['summary']["remaining_bandwidth"]
allocated_bandwidth = initial_bandwidth - remaining_bandwidth

plt.figure(figsize=(10, 6))
plt.bar(["Allocated Bandwidth", "Remaining Bandwidth"], [
        allocated_bandwidth, remaining_bandwidth], color=["green", "red"])
plt.title("Bandwidth Allocation")
plt.ylabel("Bandwidth (Mbps)")
plt.show()

# Pie chart: Emergencies Handled vs Unhandled
plt.figure(figsize=(10, 10))
plt.pie(
    [handled, unhandled],
    labels=pie_labels,
    autopct="%1.1f%%",
    colors=["green", "red"],
    textprops={'fontsize': 10}
)
plt.title("Emergency Management with Details")
plt.tight_layout()
plt.show()

# Timeline: Time allocation of emergencies
# Colour map by priority
priority_colors = {"High": "red", "Normal": "orange", "Low": "green"}

# Sort emergencies by priority (High before Normal before Low)
priority_order = {"High": 2, "Normal": 1, "Low": 0}
sorted_emergencies = sorted(
    result['summary']["handled_emergencies"],
    key=lambda x: priority_order[x["emergency_priority"]]
)

# Extract the sorted data
emergency_labels = [e["emergency_type"] for e in sorted_emergencies]
QoSs = [e["emergency_QoS"] for e in sorted_emergencies]
priorities = [e["emergency_priority"] for e in sorted_emergencies]
nodes = [e["node_id"] for e in sorted_emergencies]

# Dynamic color map for nodes
unique_nodes = list(set(nodes))  # Find the unique nodes

# Creating a palette of contrasting colours
colormap = plt.cm.get_cmap('Accent', len(unique_nodes))
node_colors = {node: colormap(i) for i, node in enumerate(unique_nodes)}

# Convert the colormap to HEX format for use in charts
node_colors = {node: mcolors.rgb2hex(color)
               for node, color in node_colors.items()}

# Colours of bars
bar_colors = [priority_colors[p] for p in priorities]
edge_colors = [node_colors[n] for n in nodes]

# Create the chart
fig, ax = plt.subplots(figsize=(12, 8))
bars = ax.barh(emergency_labels, QoSs, color=bar_colors,
               edgecolor=edge_colors, linewidth=4)

# Add legend for priority
priority_handles = [
    Rectangle((0, 0), 1, 1, color=priority_colors[p]) for p in priority_colors]
priority_labels = [f"Priority: {p}" for p in priority_colors]
legend1 = ax.legend(priority_handles, priority_labels,
                    title="Emergency Priority", loc="best")

# Add legend for nodes
node_handles = [plt.Line2D([0], [0], color=node_colors[n], lw=2)
                for n in node_colors]
node_labels = [f"Node: {n}" for n in node_colors]
legend2 = ax.legend(node_handles, node_labels, title="Nodes",
                    loc="best", bbox_to_anchor=(1, 0.5))

# Add both legends to the chart
ax.add_artist(legend1)

# Add titles and labels
ax.set_xlabel("Quality of Service (Mbps/ms)")
ax.set_ylabel("Emergencies")
ax.set_title(
    "Timeline of Bandwidth Allocation in terms of QoS per Emergency with Node Information")
ax.grid(axis="x", linestyle="--", alpha=0.7)

# Show the graph
plt.tight_layout()
plt.show()

# Heatmap: Distribution of the bandwidth between nodes
# Dati dal summary
node_bandwidth_initial = result['summary']['node_bandwidth_initial_allocation']
node_bandwidth_final = result['summary']['node_bandwidth_final_allocation']

# Calculate the bandwidth used by each node
node_bandwidth_used = {}
for e in result['summary']['handled_emergencies']:
    node_id = e['node_id']
    if node_id not in node_bandwidth_used:
        node_bandwidth_used[node_id] = 0
    node_bandwidth_used[node_id] += e['emergency_bandwidth']

# Create a list of nodes (ordered for consistency)
nodes = sorted(set(list(node_bandwidth_initial.keys()) +
               list(node_bandwidth_used.keys())))

# Construct the heatmap matrix
data = []
for node in nodes:
    initial = node_bandwidth_initial.get(node, 0)
    used = node_bandwidth_used.get(node, 0)
    final = node_bandwidth_final.get(node, 0)
    data.append([initial, used, final])

# Convert to a numpy array
data = np.array(data)

# Labels for the heatmap
x_labels = ["Initial Bandwidth", "Used Bandwidth", "Final Bandwidth"]
y_labels = [f"Node {i+1}: {node}" for i, node in enumerate(nodes)]

# Create the heatmap
plt.figure(figsize=(10, 6))
sns.heatmap(data, annot=True, fmt=".2f", cmap="YlGnBu",
            xticklabels=x_labels, yticklabels=y_labels)
plt.title("Bandwidth Distribution Across Nodes")
plt.xlabel("Bandwidth States")
plt.ylabel("Nodes")
plt.tight_layout()
plt.show()
