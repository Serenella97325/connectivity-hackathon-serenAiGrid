# Usa un'immagine base di Python
FROM python:3.9-slim

# Imposta la directory di lavoro
WORKDIR /app

# Copia il file requirements.txt per installare le dipendenze
COPY requirements.txt .

# Copia i file del progetto (incluso il codice Python)
COPY . .

# Installa le dipendenze
RUN pip install --no-cache-dir -r requirements.txt

# Esponi la porta per Flask
EXPOSE 5000

# Avvia l'app Flask
CMD ["python", "server.py"]
