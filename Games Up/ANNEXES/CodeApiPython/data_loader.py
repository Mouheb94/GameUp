import pandas as pd

def load_training_data(file_path="./training_data.csv"):
    # Charger un fichier CSV contenant les données d'utilisateur, jeux, etc.
    return pd.read_csv(file_path)
