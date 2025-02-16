import json
from ollama import generate
from fpdf import FPDF

# Json file saved on the results
RESULT_PATH = 'result.json'


# Function to load `result` from a JSON file
def load_result_from_json(path=RESULT_PATH):
    """
    Load saved results from a JSON file.

    :param path: Path to the JSON file.
    :return: Dictionary containing the results uploaded.
    """
    with open(path, 'r') as file:
        return json.load(file)


# Function to generate a report using LLaMA 3
def generate_report_with_llama(template_type, result_data):
    """
    Generate a report using the LLaMA 3 template from Ollama.

    :param template_type: Type of template required ("public tenders", "emergencies", "reports").
    :param result_data: Dictionary with optimized data.
    """
    # Predefined templates
    templates = {
        "public tenders": (
            "Generate documentation for a public tender. "
            "Make sure to include a summary of the results of the bandwidth optimization, "
            "specifying the initial and final allocation for each node, "
            "and the list of managed and unmanaged emergencies."
        ),
        "emergencies": (
            "Generate a detailed report on bandwidth allocation during emergency situations. "
            "Highlights the priorities being managed, the nodes involved, and the emergencies left unmanaged."
        ),
        "reports": (
            "Create a technical report on optimizing bandwidth distribution. "
            "Include a technical description of the allocation methods and an assessment of remaining resources."
        ),
    }

    # Add the formatted result as context
    instructions = templates.get(template_type.lower())
    if not instructions:
        raise ValueError(
            f"Invalid template type: '{template_type}'. Choose between: {', '.join(templates.keys())}."
        )

    # Aggiungi il risultato formattato come contesto
    instructions += (
        "\n\nThese are the data resulting from the optimization:\n"
        f"{json.dumps(result_data, indent=4)}"
    )

    # Call to LLaMA 3 (via Ollama)
    try:
        response = generate(
            "llama3",
            instructions,
            options={
                "temperature": 0.5
            }
        )

        optimization_result = response['response']
        print("Optimization generated by AI:")
        print(optimization_result)
        return optimization_result  # Return the generated report

    except Exception as e:
        print(f"Error while using LLaMA 3: {e}")
        return None


# Fuction to save the report on PDF format
def save_report_as_pdf(report_content, filename="report.pdf"):
    """
    Save the generated report to a PDF file.

    :param report_content: The content of the report as a string.
    :param filename: Name of the PDF file to save the report.
    """
    try:
        pdf = FPDF()
        pdf.add_page()
        pdf.set_font("Arial", size=12)

        # Add the content line by line
        for line in report_content.splitlines():
            pdf.multi_cell(0, 10, txt=line)

        pdf.output(filename)
        print(f"Report saved successfully as {filename}.")
    except Exception as e:
        print(f"Error while saving the report: {e}")


if __name__ == "__main__":

    # Load the result from the JSON file
    loaded_result = load_result_from_json()

    # Display options to the user
    print("Choose the type of report you want to generate:")
    print("1. Public Tenders")
    print("2. Emergencies")
    print("3. Reports")

    # Get user input
    user_choice = input(
        "Enter the number corresponding to your choice: ").strip()

    # Map the user's choice to a template type
    choices_map = {
        "1": "public tenders",
        "2": "emergencies",
        "3": "reports"
    }

    selected_template = choices_map.get(user_choice)

    if selected_template:

        # Generate the report based on the user's choice and save it PDF format
        generated_report = generate_report_with_llama(
            selected_template, loaded_result)
        save_report_as_pdf(generated_report, "generated_report.pdf")

    else:
        print("Invalid choice. Please run the script again and select a valid option.")
